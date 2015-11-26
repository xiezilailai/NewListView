package com.example.xiezi.aboutlistview;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by 蝎子莱莱123 on 2015/11/23.
 */
public  class NewListView extends ListView {
    private View header;
    private int headerHeight;
    private int current;//当前触点y值
    private int mark;//点击时触点y值
    private int space;
    private boolean flag=false;//根据是否从释放刷新离开而决定是否要刷新
    private TextView textView;
    private ImageView imageView,imageView2,imageView3;
    private Animation ani1,ani2,ani3;
    private OnRefreshListener onRefreshListener;
    private  static boolean isAnimated=false;//判断箭头是否翻转
    private Handler myHandler;//用来作更新的等待时间



    public NewListView(Context context) {
        super(context);
        init();
    }
    public void setOnRefreshListener(OnRefreshListener listener) {
        onRefreshListener = listener;
    }
    public NewListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NewListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init(){
        ani1=new RotateAnimation(0,180, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        ani1.setDuration(300);
        ani1.setFillAfter(true);
        ani2=new RotateAnimation(180,0, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        ani2.setDuration(300);
        ani2.setFillAfter(true);
        ani3=new RotateAnimation(0,1080,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        ani3.setInterpolator(new LinearInterpolator());;
        ani3.setRepeatCount(1);
        ani3.setDuration(1000);
        ani2.setFillAfter(true);

        LayoutInflater layoutInflater=LayoutInflater.from(getContext());
        header=layoutInflater.inflate(R.layout.header2,null);
        textView=(TextView)header.findViewById(R.id.textView);
        imageView=(ImageView)header.findViewById(R.id.imageView);
        imageView2=(ImageView)header.findViewById(R.id.imageView2);
        imageView3=(ImageView)header.findViewById(R.id.imageView3);
        this.addHeaderView(header);
        measureView(header);
        headerHeight=header.getMeasuredHeight();
        header.setPadding(0, -headerHeight, 0, header.getPaddingBottom());
        header.postInvalidate();


    }
    private void measureView(View view) {//???????????????????????????
        ViewGroup.LayoutParams p = view.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int width = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int height;
        int tempHeight = p.height;
        if (tempHeight > 0) {
            height = MeasureSpec.makeMeasureSpec(tempHeight,
                    MeasureSpec.EXACTLY);
        } else {
            height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        view.measure(width, height);

    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                    mark=(int)ev.getY();
                break;


            case MotionEvent.ACTION_MOVE:


                    current = (int) ev.getY();
                    space = current - mark;
                    if (space>0&&space<headerHeight+200) {

                        header.setPadding(0, -headerHeight + space, 0, 0);
                        if(space>headerHeight+150){

                            if(!isAnimated){
                                imageView.clearAnimation();
                                textView.setText("释放刷新");
                                imageView.startAnimation(ani1);
                                isAnimated=true;
                                flag=true;
                            }

                        }
                        else{

                            if(isAnimated) {
                                flag=false;
                                imageView.clearAnimation();
                                textView.setText("下拉刷新");
                                imageView.startAnimation(ani2);
                                isAnimated = false;
                            }
                        }
                        header.postInvalidate();
                    }


                break;
            case MotionEvent.ACTION_UP:
                isAnimated = false;
                imageView.clearAnimation();

                if(flag){
                    imageView2.clearAnimation();
                    textView.setText("正在刷新");
                    imageView.setVisibility(View.GONE);
                    imageView2.setVisibility(View.VISIBLE);
                    imageView2.startAnimation(ani3);
                    header.setPadding(getPaddingLeft(), -headerHeight + 150, getPaddingRight(), getPaddingBottom());
                    header.postInvalidate();

                    //handler与thread更新主ui
                    /*
                    * 这里是通过让正在刷新进行两秒后执行刷新后所要进行的操作
                    * 而在实际的应用中并不需要，可以直接将在操作完成后，将动画clear即可，动画在这之前将会一直
                    * 进行
                    *
                    *
                    *
                    *
                    * */
                    MyThread();
                    myHandler=new android.os.Handler(){
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            switch (msg.what){
                                case 0:
                                    imageView2.clearAnimation();
                                    imageView3.setVisibility(View.VISIBLE);
                                    imageView2.setVisibility(View.GONE);
                                    imageView.setVisibility(View.GONE);
                                    onRefreshListener.onRefresh();
                                    textView.setText("刷新完成");

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            header.setPadding(getPaddingLeft(), -headerHeight, getPaddingRight(), getPaddingBottom());
                                            imageView.setVisibility(VISIBLE);
                                            imageView3.setVisibility(GONE);
                                            textView.setText("下拉刷新");
                                        }
                                    },1000);


                                    break;
                            }
                        }
                    };







                }//如果不是从释放刷新离开的，直接变为普通状态
                else{
                    textView.setText("下拉刷新");

                    header.setPadding(getPaddingLeft(), -headerHeight, getPaddingRight(), getPaddingBottom());
                }
                flag=false;







                break;
        }
        return super.onTouchEvent(ev);
    }

    private void MyThread(){//刷新等待两秒
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);//睡眠2秒
                    myHandler.sendEmptyMessage(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }




    public interface OnRefreshListener{
        void onRefresh();
    }
}
