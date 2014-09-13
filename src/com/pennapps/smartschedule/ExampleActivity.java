package com.pennapps.smartschedule;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

public class ExampleActivity extends Activity {

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
            }
            break;
        case RESULT_TEXTTOSPEECH:
            tts.speak("Smart Schedule", TextToSpeech.QUEUE_ADD, null);
            break;
        }
    }
    
    private OnClickListener listener = new OnClickListener(){

        @Override
        public void onClick(View v) {
            switch(v.getId()){
            case R.id.ibPlay:
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); 
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
                startActivityForResult(intent, RESULT_SPEECH);
                break;
                
            case R.id.ibRecord:
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
        
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    Log.wtf("Yay", "worked");
                }else Log.wtf("No", "didn't worked");
            }
        });
    }

}
