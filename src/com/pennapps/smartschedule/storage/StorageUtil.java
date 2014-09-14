package com.pennapps.smartschedule.storage;

import java.util.Set;

import org.joda.time.Period;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class StorageUtil {

    private static final String pack = "com.pennapps.smartschedule";

    public static void removeDuration(Context context, String event){
        SharedPreferences prefs = context.getSharedPreferences(pack+".duration", Context.MODE_PRIVATE);
        prefs.edit().remove(event).commit();
    }
    
    public static void putDuration(Context context, String event, Period period) {
        Log.wtf("Duration prefs", event+" "+period);
        SharedPreferences prefs = context.getSharedPreferences(pack
                + ".duration", Context.MODE_PRIVATE);
        prefs.edit().putInt(event, period.getMillis()).commit();
        Log.wtf("New duration", Period.millis(prefs.getInt(event, 1000 * 60 * 15))+"");
    }

    public static Period getDuration(Context context, String event) {
        SharedPreferences prefs = context.getSharedPreferences(pack
                + ".duration", Context.MODE_PRIVATE);
        Log.wtf("Get duration", Period.millis(prefs.getInt(event, 0))+"");
        return Period.millis(prefs.getInt(event, 1000 * 60 * 15));
    }

    public static Set<String> getRecentTasks(Context context) {
        return context
                .getSharedPreferences(pack + ".duration", Context.MODE_PRIVATE)
                .getAll().keySet();
    }

    public static void setGoogleAccount(Context context, String email) {
        context.getSharedPreferences(pack + ".acct", Context.MODE_PRIVATE)
                .edit().putString("acct", email).commit();
    }

    public static String getGoogleAccount(Context context) {
        return context.getSharedPreferences(pack + ".acct",
                Context.MODE_PRIVATE).getString("acct", "");
    }

    public static void setMaxTime(Context context, int val) {
        context.getSharedPreferences(pack + ".max_time", Context.MODE_PRIVATE)
                .edit().putInt("max_time", val).commit();
    }

    public static int getMaxTime(Context context) {
        return context.getSharedPreferences(pack + ".max_time",
                Context.MODE_PRIVATE).getInt("max_time", 1);
    }

    public static void setLaziness(Context context, boolean isProactive) {
        context.getSharedPreferences(pack + ".laziness", Context.MODE_PRIVATE)
                .edit().putBoolean("laziness", isProactive).commit();
    }

    /**
     * @return True if proactice, false if balanced
     */
    public static boolean getLaziness(Context context) {
        return context.getSharedPreferences(pack + ".laziness",
                Context.MODE_PRIVATE).getBoolean("laziness", false);
    }
    
    public static String getLaziness(boolean isProactive){
        return isProactive ? "Proactive" : "Balanced";
    }

}
