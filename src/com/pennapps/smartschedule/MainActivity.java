package com.pennapps.smartschedule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pennapps.smartschedule.scheduler.Day;
import com.pennapps.smartschedule.scheduler.Event;
import com.pennapps.smartschedule.scheduler.EventFetcher;
import com.pennapps.smartschedule.scheduler.EventPusher;
import com.pennapps.smartschedule.scheduler.RollingScheduler;
import com.pennapps.smartschedule.scheduler.ScheduledEvent;
import com.pennapps.smartschedule.scheduler.SchedulingCalendar;
import com.pennapps.smartschedule.scheduler.SchedulingSettings;
import com.pennapps.smartschedule.storage.StorageUtil;

public class MainActivity extends Activity {

    protected static final int RESULT_TAKEEVENT = 0, 
            RESULT_EVENTPUSHED = 1;

    public TextParser thing;
    protected TextToSpeech tts;
    
    private OnClickListener listener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.rlAddTask:
            	testData();
            	/*
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); 
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
                startActivityForResult(intent, RESULT_TAKEEVENT);
                */break;
//                startActivityForResult(intent, RESULT_TAKEEVENT);
//                ArrayList<String> stuff = new ArrayList<String>();
//                stuff.add("Data structures project due Friday at 5 p.m. takes 5 hours 37 minutes");
//                handleSpeechEvent(stuff);
//                break;
            }
        }
    };

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        findViewById(R.id.rlAddTask).setOnClickListener(listener);
        loadRecentTasks();
    }

    @Override
    public void onResume(){
        super.onResume();
        // loadRecentTasks();
    }
    
    private void loadRecentTasks(){
        LinearLayout layout = (LinearLayout) findViewById(R.id.llRecentTasks);
        for (final String task : StorageUtil.getRecentTasks(this)){
            TextView taskView = new TextView(this);
            LinearLayout.LayoutParams taskParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            taskParams.setMargins(20, 20, 20, 20);
            taskView.setText(task);
            taskView.setTextSize(30);
            taskView.setLayoutParams(taskParams);
            taskView.setBackgroundResource(R.drawable.recent_task);
            taskView.setClickable(true);
            taskView.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Period duration = StorageUtil.getDuration(MainActivity.this, task);
                    Log.wtf("Storage duration", duration+"");
                    handleScheduledEvent(new ScheduledEvent(task, null, // should be null (or ask the user)
                            duration.toStandardDuration()));
                }
            });
            
            View split = new View(this);
            LinearLayout.LayoutParams splitParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 2);
            split.setPadding(10, 10, 10, 10);
            split.setLayoutParams(splitParams);
            split.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            
            layout.addView(taskView);
            layout.addView(split);
        }
    }
    
    private void testData() {
        ScheduledEvent scheduledEvent = TextParser.getScheduledEvent("Data structures project due October 15th at 7 p.m. takes 7 hours and 53 minutes");
        
        EventFetcher fetch = new EventFetcher(getContentResolver(), getEmail());
        fetch.getCalendarID();
        SchedulingCalendar cal = fetch.getCalendar();
        
        // check presets
        if(scheduledEvent.getDuration() == null) {
            scheduledEvent.setDuration(getDuration(
                    cal.getEvents(DateTime.now().minusWeeks(4),
                            DateTime.now()), scheduledEvent.getName()));
        }

        boolean split = true;
        
        if(split) {
	        List<Event> events = RollingScheduler.scheduleSplit(cal, Day.today(), scheduledEvent,
	                new SchedulingSettings());
	        Log.wtf("EVENTS", "" + events);
	        for(Event event : events) {
	        	cal.addEvent(event);
	
	            putEvent(event);
	        }
        } else {
        	Event event = RollingScheduler.scheduleFirst(cal, scheduledEvent, new SchedulingSettings());
        	Log.wtf("EVENT", "" + event);
        	
        	cal.addEvent(event);
        	
        	putEvent(event);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
        case RESULT_TAKEEVENT:
            if(resultCode != RESULT_OK || data == null)
                break;
            handleSpeechEvent(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS));
            break;
        }
    }
    
    private String getEmail(){
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(this).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                if(account.name.contains("@gmail.com"))
                    return account.name;
            }
        }
        return "";
    }
    
    private void handleSpeechEvent(ArrayList<String> events){
        ScheduledEvent scheduledEvent = TextParser.getScheduledEvent(events);
        
        EventFetcher fetch = new EventFetcher(getContentResolver(), getEmail());
        fetch.getCalendarID();
        SchedulingCalendar cal = fetch.getCalendar();
        
        // check presets
        if(scheduledEvent.getDuration() == null){
            scheduledEvent.setDuration(getDuration(
                    cal.getEvents(DateTime.now().minusWeeks(4),
                            DateTime.now()), scheduledEvent.getName()));
        }
        else 
            Log.wtf("Duration", scheduledEvent.getDuration()+"");
        
        Event event = RollingScheduler.scheduleFirst(cal, scheduledEvent,
                new SchedulingSettings());
        cal.addEvent(event);

        putEvent(event);
    }
    
    private void handleScheduledEvent(ScheduledEvent scheduledEvent){
        EventFetcher fetch = new EventFetcher(getContentResolver(), getEmail());
        fetch.getCalendarID();
        SchedulingCalendar cal = fetch.getCalendar();
        
        Event event = RollingScheduler.scheduleFirst(cal, scheduledEvent,
                new SchedulingSettings());
        cal.addEvent(event);

        putEvent(event);
    }
    
    private void putEvent(Event event){
        StorageUtil.putDuration(this, event.getName(), Period
                .millis((int) (event.getEnd().getMillis() - event.getStart().getMillis())));
        startActivityForResult(EventPusher.insertEvent(event), RESULT_EVENTPUSHED);
    }

    private static final String[] options = { "Set Google account", // vals = ______@gmail.com
            "Set max task time per day", // max time per day per task: vals = 0.5, 1.0, 1.5, 2.0, ... , 24.0
            "Set laziness" }; // vals = Proactive, Balanced, Procrastinate
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        for(String s : options)
            menu.add(s);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(getIndex(item.getTitle()+"")){
        case 0:
            new Dialog(this).show();
            return true;
        case 1:
            return true;
        case 2:
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private static int getIndex(String option){
        for(int a=0; a<options.length; a++)
            if(options[a].equals(option))
                return a;
        return -1;
    }
    
    private static Duration getDuration(List<Event> events, String event){
        Collections.reverse(events);
        for(Event e : events){
            Log.wtf("Event names", e.getName()+" "+event);
            if(e.getName().equals(event))
                return Duration.millis((int) (e.getEnd().getMillis() - e.getStart().getMillis()));
        }
        return Period.minutes(10).toStandardDuration(); // default
    }
    
}
