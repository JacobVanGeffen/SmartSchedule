package com.pennapps.smartschedule.storage;

import org.joda.time.Period;

import android.content.Context;
import android.content.SharedPreferences;

public class StorageUtil {

    public static void putDuration(Context context, String event, Period period){
        SharedPreferences prefs = context.getSharedPreferences("com.pennapps.smartschedule.duration", Context.MODE_PRIVATE);
        prefs.edit().putInt(event, period.getMillis());
    }
    
    public static Period getDuration(Context context, String event){
        SharedPreferences prefs = context.getSharedPreferences("com.pennapps.smartschedule.duration", Context.MODE_PRIVATE);
        return Period.millis(prefs.getInt(event, 0));
    }
    
}
