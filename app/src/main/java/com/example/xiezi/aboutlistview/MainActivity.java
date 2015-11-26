package com.example.xiezi.aboutlistview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NewListView.OnRefreshListener {

    private NewListView listview;
    private ArrayAdapter arrayAdapter;
        private List<String>list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        listview=(NewListView)findViewById(R.id.listView);
        list=new ArrayList<>();
        for(int i=0;i<20;i++){
            list.add("this is a test!!");
        }
        arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
        listview.setAdapter(arrayAdapter);
        listview.setOnRefreshListener(this);


    }


    @Override
    public void onRefresh() {
        list.add(0,"this is a new item");
        arrayAdapter.notifyDataSetChanged();
    }
}
