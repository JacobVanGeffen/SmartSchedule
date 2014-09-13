package com.pennapps.smartschedule;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.Period;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.pennapps.smartschedule.scheduler.Event;
import com.pennapps.smartschedule.scheduler.EventFetcher;
import com.pennapps.smartschedule.scheduler.EventPusher;
import com.pennapps.smartschedule.scheduler.RollingScheduler;
import com.pennapps.smartschedule.scheduler.ScheduledEvent;
import com.pennapps.smartschedule.scheduler.SchedulingCalendar;
import com.pennapps.smartschedule.scheduler.SchedulingSettings;

public class ExampleActivity extends Activity {
	public TextParser thing;
    protected static final int RESULT_SPEECH = 1,
            RESULT_TEXTTOSPEECH = 0,
            RESULT_EVENTPUSHED = 2;
    protected TextToSpeech tts;

    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
 
        switch (requestCode) {
        case RESULT_SPEECH:
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> text = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS); // represents all possible texts
                Log.wtf("Text", text.toString());
                // tts.speak(text.get(0), TextToSpeech.QUEUE_ADD, null);
                ScheduledEvent scheduledEvent = TextParser.getScheduledEvent(text);
                Log.wtf("Event", scheduledEvent+"");
                
                EventFetcher fetch = new EventFetcher(getContentResolver());
                fetch.getCalendarID();
                SchedulingCalendar cal = fetch.getCalendar(DateTime.now().minus(Period.weeks(4)));
                
                System.out.println("Have the scheduling calendar: " + cal);
                
                Event event = RollingScheduler.scheduleFirst(cal, scheduledEvent, new SchedulingSettings());
                putEvent(event);
            }
            break;
        case RESULT_TEXTTOSPEECH:
            // tts.speak("gesundheit", TextToSpeech.QUEUE_ADD, null);
            ScheduledEvent scheduledEvent = 
                TextParser.getScheduledEvent("Data structures project due October 15th at 7 p.m. takes 5 hours and 37 minutes");
            Log.wtf("Event", scheduledEvent+"");
            Log.wtf("time", scheduledEvent.getDeadline().getHourOfDay()+"");
            
            EventFetcher fetch = new EventFetcher(getContentResolver());
            fetch.getCalendarID();
            SchedulingCalendar cal = fetch.getCalendar(DateTime.now().minus(Period.weeks(4)));
            
            Event event = RollingScheduler.scheduleFirst(cal, scheduledEvent, new SchedulingSettings());
            
            System.out.println("The thing was scheduled.");
            putEvent(event);
            break; 
        case RESULT_EVENTPUSHED:
            Log.wtf("hi", "event pushed");
            break;
        }
    }
    
    private OnClickListener listener = new OnClickListener(){

        @Override
        public void onClick(View v) {
            switch(v.getId()){
            case R.id.ibPlay: // speak text
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); 
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
                startActivityForResult(intent, RESULT_SPEECH);
                break;
                
            case R.id.ibRecord: // record speech
                startActivityForResult(new Intent(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA), RESULT_TEXTTOSPEECH);
                break;
            }
        }
        
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_example);
        
        findViewById(R.id.ibPlay).setOnClickListener(listener);
        findViewById(R.id.ibRecord).setOnClickListener(listener);
        
        // initializes text to speech engine
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    Log.wtf("Yay", "worked");
                }else Log.wtf("No", "didn't worked");
            }
        });
        
        Log.wtf("Email", getEmail());
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
    
    private void putEvent(Event event){
        startActivityForResult(EventPusher.insertEvent(event), RESULT_EVENTPUSHED);
    }

}
