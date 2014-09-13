package com.pennapps.smartschedule;

import java.util.regex.Pattern;

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

import com.pennapps.smartschedule.scheduler.Event;
import com.pennapps.smartschedule.scheduler.EventFetcher;
import com.pennapps.smartschedule.scheduler.EventPusher;
import com.pennapps.smartschedule.scheduler.RollingScheduler;
import com.pennapps.smartschedule.scheduler.ScheduledEvent;
import com.pennapps.smartschedule.scheduler.SchedulingCalendar;
import com.pennapps.smartschedule.scheduler.SchedulingSettings;

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
                Log.wtf("onclick", "onclick");
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); 
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
                startActivityForResult(intent, RESULT_TAKEEVENT);
                break;
            }
        }
    };

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        findViewById(R.id.rlAddTask).setOnClickListener(listener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
        case RESULT_TAKEEVENT:
            if(resultCode != RESULT_OK || data == null)
                break;
            
            ScheduledEvent scheduledEvent = TextParser.getScheduledEvent(data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS));

            EventFetcher fetch = new EventFetcher(getContentResolver());
            fetch.getCalendarID();
            SchedulingCalendar cal = fetch.getCalendar();

            Event event = RollingScheduler.scheduleFirst(cal, scheduledEvent,
                    new SchedulingSettings());
            cal.addEvent(event);

            putEvent(event);
            break;
        }
    }
    
    @SuppressWarnings("unused")
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
    
    private void putEvent(Event event){
        startActivityForResult(EventPusher.insertEvent(event), RESULT_EVENTPUSHED);
    }

    private static final String[] options = { "Set Google account", // vals = ______@gmail.com
            "Set max task time per day", // max time per day per task: vals = 1.0, 1.5, 2.0, ... , 24.0
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
    
}
