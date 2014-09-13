package com.pennapps.smartschedule;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.ListView;

import com.pennapps.smartschedule.event.Event;

public class CalendarViewActivity extends Activity{

    private class Listener implements OnDateChangeListener{

        @Override
        public void onSelectedDayChange(CalendarView view, int year, int month,
                int dayOfMonth) {
            Log.wtf("Date changed", year+" "+month+" "+dayOfMonth);
            ArrayList<Event> events = new ArrayList<Event>();
            events.add(new Event("Birthday"));
            events.add(new Event("Cake"));
            events.add(new Event("Win PennApps"));
            events.add(new Event("Birthday"));
            events.add(new Event("Cake"));
            events.add(new Event("Win PennApps"));
            events.add(new Event("Birthday"));
            events.add(new Event("Cake"));
            events.add(new Event("Win PennApps"));
            setupEventList(events);
        }
        
    }
    
    private Listener listener;
    
    @Override
    public void onCreate(Bundle b){
        super.onCreate(b);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.calendar);
        
        listener = new Listener();
        
        setupGUI();
    }
    
    private void setupGUI(){
        CalendarView cvSchedule = (CalendarView) findViewById(R.id.cvSchedule);
        cvSchedule.setOnDateChangeListener(listener);
    }
    
    private void setupEventList(List<Event> events){
        ListView listView = (ListView) findViewById(R.id.lvEventDetails);
        ArrayAdapter<Event> adapter = new ArrayAdapter<Event>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);
        for(Event event : events)
            adapter.add(event);
        adapter.notifyDataSetChanged();
    }
    
}
