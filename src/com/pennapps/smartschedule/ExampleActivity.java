package com.pennapps.smartschedule;

import java.util.ArrayList;
import java.util.TimeZone;
import java.util.regex.Pattern;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.pennapps.smartschedule.scheduler.Event;

public class ExampleActivity extends Activity {
	public TextParser thing;
    protected static final int RESULT_SPEECH = 1,
            RESULT_TEXTTOSPEECH = 0;
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
                Log.wtf("Event", TextParser.getScheduledEvent(text)+"");
            }
            break;
        case RESULT_TEXTTOSPEECH:
            tts.speak("gesundheit", TextToSpeech.QUEUE_ADD, null);
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
        setContentView(R.layout.activity_main);
        
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
        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();

        values.put(CalendarContract.Events.DTSTART, event.getStart());
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DESCRIPTION, comment);

        TimeZone timeZone = TimeZone.getDefault();
        values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());

        // default calendar
        values.put(CalendarContract.Events.CALENDAR_ID, 1);

        values.put(CalendarContract.Events.RRULE, "FREQ=DAILY;UNTIL="
                + dtUntill);
        //for one hour
        values.put(CalendarContract.Events.DURATION, "+P1H");

        values.put(CalendarContract.Events.HAS_ALARM, 1);

        // insert event to calendar
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
    }

}
