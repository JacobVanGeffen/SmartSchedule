package com.pennapps.smartschedule.storage;

import java.util.Set;

import org.joda.time.Period;

import android.content.Context;
import android.content.SharedPreferences;

public class StorageUtil {

    private static final String pack = "com.pennapps.smartschedule";

    public static void putDuration(Context context, String event, Period period) {
        SharedPreferences prefs = context.getSharedPreferences(pack
                + ".duration", Context.MODE_PRIVATE);
        prefs.edit().putInt(event, period.getMillis()).commit();
    }

    public static Period getDuration(Context context, String event) {
        SharedPreferences prefs = context.getSharedPreferences(pack
                + ".duration", Context.MODE_PRIVATE);
        return Period.millis(prefs.getInt(event, 0));
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

    public static void setMaxTime(Context context, float val) {
        context.getSharedPreferences(pack + ".max_time", Context.MODE_PRIVATE)
                .edit().putFloat("max_time", val).commit();
    }

    public static float getMaxTime(Context context) {
        return context.getSharedPreferences(pack + ".max_time",
                Context.MODE_PRIVATE).getFloat("max_time", 0);
    }

    public static void setLaziness(Context context, String laziness) {
        context.getSharedPreferences(pack + ".laziness", Context.MODE_PRIVATE)
                .edit().putString("laziness", laziness).commit();
    }

    public static String getLaziness(Context context) {
        return context.getSharedPreferences(pack + ".laziness",
                Context.MODE_PRIVATE).getString("laziness", "");
    }

}
