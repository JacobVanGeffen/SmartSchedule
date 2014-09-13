package com.pennapps.smartschedule;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class CalendarViewActivity extends Activity{

    @Override
    public void onCreate(Bundle b){
        super.onCreate(b);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.calendar);
    }
    
    
    
    private void setupEventList(List<String> events){
        ListView listView = (ListView) findViewById(R.id.lvEventDetails);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);
        for(String s : events)
            adapter.add(s);
        adapter.notifyDataSetChanged();
    }
    
}
