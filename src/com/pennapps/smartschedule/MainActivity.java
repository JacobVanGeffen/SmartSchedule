package com.pennapps.smartschedule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
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

    public static final boolean DEBUG = true;
    
    public TextParser thing;
    protected TextToSpeech tts;
    private boolean isEditMode = false;
    
    private OnClickListener listener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.rlAddTask:
            	if(DEBUG) {
            		testData();
            		break;
            	}
            	
                if(isEditMode){
                    endEditMode(true);
                    return;
                }
                
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); 
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
                startActivityForResult(intent, RESULT_TAKEEVENT);
                break;
            }
        }
    };
    
    private void endEditMode(boolean delete){
        ((TextView)findViewById(R.id.tvAddTask)).setText("Add Task");
        findViewById(R.id.rlAddTask).setBackgroundResource(R.drawable.add_task);
        isEditMode = false;
        
        for(int a=taskViews.size()-1; a>=0; a--){
            Log.wtf("Task", a+"");
            if(delete && toDelete.contains(taskViews.get(a))){
                Log.wtf("Task delete", a+" delete");
                
                StorageUtil.removeDuration(this, taskViews.get(a).getText()+"");
                
                ((LinearLayout)findViewById(R.id.llRecentTasks)).removeView(taskViews.get(a));
                ((LinearLayout)findViewById(R.id.llRecentTasks)).removeView(splitViews.get(a));
                
                taskViews.remove(a);
                splitViews.remove(a);
            }else{
                taskViews.get(a).setBackgroundResource(R.drawable.recent_task);
            }
        }
    }
    
    private HashSet<View> toDelete;
    private void startEditMode(){
        isEditMode = true;
        toDelete = new HashSet<View>();
        ((TextView)findViewById(R.id.tvAddTask)).setText("Delete");
        findViewById(R.id.rlAddTask).setBackgroundResource(R.drawable.delete_task);
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        Log.wtf("Infinity time", DateTime.now().plusYears(1000)+"");
        
        findViewById(R.id.rlAddTask).setOnClickListener(listener);
        ((TextView) findViewById(R.id.tvRecentTasks)).setTypeface(Typeface
                .createFromAsset(getAssets(), "Roboto-Light.ttf"));
        ((TextView) findViewById(R.id.tvAddTask)).setTypeface(Typeface
                .createFromAsset(getAssets(), "Roboto-Light.ttf"));
    }

    @Override
    public void onResume(){
        super.onResume();
        clearRecentTasks();
        loadRecentTasks();
    }
    
    private void clearRecentTasks(){
        if(isEditMode)
            endEditMode(false);
        try{
            taskViews.clear();
            splitViews.clear();
            toDelete.clear();
        }catch(NullPointerException ex){}
        ((LinearLayout)findViewById(R.id.llRecentTasks)).removeAllViews();
    }
    
    ArrayList<TextView> taskViews;
    ArrayList<View> splitViews;
    private void loadRecentTasks(){
        taskViews = new ArrayList<TextView>();
        splitViews = new ArrayList<View>();
        
        LinearLayout layout = (LinearLayout) findViewById(R.id.llRecentTasks);
        for (final String task : StorageUtil.getRecentTasks(this)){
            if(task.contains("(Part"))
                continue;
            final TextView taskView = new TextView(this);
            LinearLayout.LayoutParams taskParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            taskParams.setMargins(20, 20, 20, 20);
            taskView.setText(task);
            taskView.setTextSize(30);
            taskView.setLayoutParams(taskParams);
            taskView.setBackgroundResource(R.drawable.recent_task);
            taskView.setClickable(true);
            taskView.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if(isEditMode){
                        if(toDelete.contains(taskView)){
                            toDelete.remove(taskView);
                            taskView.setBackgroundResource(MainActivity.this
                                    .getResources().getColor(android.R.color.transparent));
                        }else{
                            toDelete.add(taskView);
                            taskView.setBackgroundColor(MainActivity.this
                                    .getResources().getColor(R.color.transparent_light_red));
                        }
                        return;
                    }
                    
                    Period duration = StorageUtil.getDuration(MainActivity.this, task);
                    Log.wtf("Storage duration", duration + "");
                    handleScheduledEvent(new ScheduledEvent(task, null,
                            duration.toStandardDuration()));
                }
            });
            taskView.setTypeface(Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf"));
            
            View split = new View(this);
            LinearLayout.LayoutParams splitParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 2);
            split.setPadding(10, 10, 10, 10);
            split.setLayoutParams(splitParams);
            split.setBackgroundColor(getResources().getColor(R.color.light_green));
            
            layout.addView(taskView);
            layout.addView(split);
            
            taskViews.add(taskView);
            splitViews.add(split);
        }
    }
    
    private void testData() {
        ScheduledEvent scheduledEvent = TextParser.getScheduledEvent("Data structures project due in 4 days takes 7 hours and 53 minutes");
        
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
	                new SchedulingSettings(this));
	        Log.wtf("EVENTS", "" + events);
	        for(Event event : events) {
	        	cal.addEvent(event);
	
	            putEvent(event, false);
	        }
        } else {
        	Event event = RollingScheduler.scheduleFirst(cal, scheduledEvent, new SchedulingSettings(this));
        	Log.wtf("EVENT", "" + event);
        	
        	cal.addEvent(event);
        	
        	putEvent(event, false);
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
    
    public String getEmail(){
        String storedEmail = StorageUtil.getGoogleAccount(this);
        if(!storedEmail.equals(""))
            return storedEmail;
        
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(this).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                if(account.name.contains("@gmail.com")){
                    StorageUtil.setGoogleAccount(this, account.name);
                    return account.name;
                }
            }
        }
        return "";
    }
    
    private void handleSpeechEvent(ArrayList<String> events){
        ScheduledEvent scheduledEvent = TextParser.getScheduledEvent(events);
        Log.wtf("Scheduled event", scheduledEvent+"") ;
        boolean setDur = true;
        
        EventFetcher fetch = new EventFetcher(getContentResolver(), getEmail());
        fetch.getCalendarID();
        SchedulingCalendar cal = fetch.getCalendar();
        
        // check presets
        if(scheduledEvent.getDuration() == null){
            scheduledEvent.setDuration(/*getDuration(
                    cal.getEvents(DateTime.now().minusWeeks(4),
                            DateTime.now()), scheduledEvent.getName())*/
                    StorageUtil.getDuration(this, scheduledEvent.getName()).toStandardDuration());
            setDur = false;
        }
        else 
            Log.wtf("Duration", scheduledEvent.getDuration()+"");
        
        SchedulingSettings settings = new SchedulingSettings(this);
        settings.setSplittable(scheduledEvent.getDuration().getStandardHours() >= 2);
        
        if (settings.isSplittable()) {
            List<Event> listEvents = RollingScheduler.scheduleSplit(cal,
                    Day.today(), scheduledEvent, settings);
            
            Duration totalScheduled = SchedulingCalendar.sumLists(listEvents);
            if(totalScheduled.getMillis() < scheduledEvent.getDuration().getMillis()) {
            	AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("Cannot schedule task");
                dialog.setMessage("Insufficient time available on calendar");
                dialog.setNegativeButton("Ok", null);
                dialog.show();
                return;	
            }
            
            Collections.reverse(listEvents);
            for (Event event : listEvents) {
                cal.addEvent(event);
                putEvent(event, false);
            }
            return;
        }
        
        Event event = RollingScheduler.scheduleFirst(cal, scheduledEvent,
                settings);
        
        if(event == null){
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Cannot schedule task");
            dialog.setMessage("Insufficient time available on calendar");
            dialog.setNegativeButton("Ok", null);
            dialog.show();
            return;
        }
        
        cal.addEvent(event);

        putEvent(event, setDur);
    }
    
    private void handleScheduledEvent(ScheduledEvent scheduledEvent){
        Log.wtf("Scheduled event", scheduledEvent+"") ;
        
        EventFetcher fetch = new EventFetcher(getContentResolver(), getEmail());
        fetch.getCalendarID();
        SchedulingCalendar cal = fetch.getCalendar();
        
        Event event = RollingScheduler.scheduleFirst(cal, scheduledEvent,
                new SchedulingSettings(this));
        
        if(event == null){
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Cannot schedule task");
            dialog.setMessage("No space available on calendar");
            dialog.setNegativeButton("Ok", null);
            dialog.show();
            return;
        }
        
        cal.addEvent(event);

        putEvent(event, false);
    }
    
    private void putEvent(Event event, boolean setDur){
        if(setDur)
            StorageUtil.putDuration(this, event.getName(), Period
                .millis((int) (event.getEnd().getMillis() - event.getStart().getMillis())));
        startActivityForResult(EventPusher.insertEvent(event, this), RESULT_EVENTPUSHED);
    }

    private static final String[] options = { "Set Google account", // vals = ______@gmail.com
            "Set max task time per day", // max time per day per task: vals = 1, 2, 3, ..., 24
            "Set laziness" }; // vals = Proactive, Balanced
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        for(String s : options)
            menu.add(s);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        AlertDialog.Builder builder = null;
        
        switch(item.getItemId()){
        case R.id.action_edit:
            if(isEditMode){
                endEditMode(false);
                break;
            }
            
            Log.wtf("Action", "edit");
            startEditMode();
            break;
        case R.id.action_calendar:
            startActivity(EventPusher.goToCalendarEvent());
            break;
        }
        
        switch(getIndex(item.getTitle()+"")){
        case 0:
            builder = new AlertDialog.Builder(this);
            final EditText editText = new EditText(this);
            editText.setText(getEmail());
            
            builder.setTitle("Google Account");
            builder.setView(editText);
            
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    StorageUtil.setGoogleAccount(MainActivity.this, editText.getText().toString());
                }
            });
            
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // canceled
                }
            });
            
            builder.show();
            return true;
            
        case 1:
            builder = new AlertDialog.Builder(this);
            final NumberPicker picker = new NumberPicker(this);
            picker.setMinValue(1);
            picker.setMaxValue(24);
            picker.setValue(StorageUtil.getMaxTime(this));
            
            builder.setTitle("Max time spent on a task per day (hours)");
            builder.setView(picker);
            
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    StorageUtil.setMaxTime(MainActivity.this, picker.getValue());
                }
            });
            
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // canceled
                }
            });
            
            builder.show();
            return true;
        case 2:
            builder = new AlertDialog.Builder(this);
            
            builder.setTitle("How large tasks are planned");
            builder.setMessage("Current: "+StorageUtil.getLaziness(StorageUtil.getLaziness(this)));
            
            builder.setNegativeButton(StorageUtil.getLaziness(true), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    StorageUtil.setLaziness(MainActivity.this, true);
                }
            });
            
            builder.setNeutralButton(StorageUtil.getLaziness(false), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    StorageUtil.setLaziness(MainActivity.this, false);
                }
            });
            
            builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // canceled
                }
            });
            
            builder.show();
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
        return Duration.standardMinutes(10); // default
    }
    
}
