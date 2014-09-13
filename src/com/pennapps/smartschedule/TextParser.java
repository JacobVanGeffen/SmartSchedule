package com.pennapps.smartschedule;

import java.util.ArrayList;

import org.joda.time.DateTime;

import android.annotation.SuppressLint;

import com.pennapps.smartschedule.event.Event;
import com.pennapps.smartschedule.event.EventMetadata;

@SuppressLint("DefaultLocale")
public class TextParser {

    public static final String[] eventNamePostMarkers = { "on", "due", },
            eventNamePreMarkers = { "i", "have", "there's", "got", "hey",
                    "schedule" }, // this is very inaccurate
            deadlinePreMarkers = { "due", "finish by", "on" },
            deadlineTimeMarkers = { "at", "after" };

    public static Event getEvent(ArrayList<String> speech){
        double maxScore = 0;
        Event best = null;
        for(String s : speech){
            Event e = getEvent(s);
            if(score(e) > maxScore){
                maxScore = score(e);
                best = e;
            }
        }
        return best;
    }
    
    private static double score(Event e){
        double score = 0;
        if(e.getName() != "")
            score++;
        if(e.getMetadata().getDeadline() != null)
            score++;
        if(e.getMetadata().getDuration() != null)
            score++;
        if(e.getMetadata().getStart() != null)
            score++;
        return score;
    }
    
    // TODO currently does not include duration
    public static Event getEvent(String speech) {
        String name = null, deadline = null;
        Event event;

        for (String splitter : eventNamePostMarkers) {
            if (name == null
                    || name.length() > speech.split(splitter)[0].length()) {
                name = speech.split(splitter)[0];
            }
        }

        for (String rep : eventNamePreMarkers)
            name.replaceAll(rep + ".*", "");
        name = name.toLowerCase();
        name = name.trim();
        name = name.substring(0, 1).toUpperCase() + name.substring(1);

        event = new Event(name);

        EventMetadata data = event.getMetadata();

        for (String splitter : eventNamePostMarkers) {
            if (deadline == null
                    || deadline.length() > speech.split(splitter)[0].length()) {
                String[] ar = speech.split(splitter);
                deadline = ar[ar.length - 1];
            }
        }

        data.setDeadline(interpretDeadline(deadline));
        data.setStart(DateTime.now());
        
        // data.setDuration(new Period());

        return event;
    }

    /**
     * Only accepts due dates w/ hour as highest accuracy (can't say
     * "due at 5:30"
     * 
     * @param deadline
     * @return
     */
    private static DateTime interpretDeadline(String deadline) {
        DateTime ret = DateTime.now();

        deadline = deadline.toLowerCase().trim();

        if (deadline.contains("tomorrow")) {
            ret = ret.plusDays(1);

            if (deadline.contains("afternoon") || deadline.contains("evening"))
                ret = ret.withHourOfDay(12).withMinuteOfHour(0)
                        .withSecondOfMinute(0);
            else
                ret = ret.withHourOfDay(0).withMinuteOfHour(0)
                        .withSecondOfMinute(0);

        }

        if (deadline.contains("day")) { // "in 5 days..." or "in a day"
            ret = ret.plusDays(num(deadline.substring(deadline.indexOf("in"),
                    deadline.indexOf("day")))); // DEFINITELY not accurate
        } else if (deadline.contains("week")) {
            ret = ret.plusWeeks(num(deadline.substring(deadline.indexOf("in"),
                    deadline.indexOf("week"))));
        } else if (deadline.contains("month")) {
            ret = ret.plusMonths(num(deadline.substring(deadline.indexOf("in"),
                    deadline.indexOf("month"))));
        } else if (deadline.contains("year")) {
            ret = ret.plusYears(num(deadline.substring(deadline.indexOf("in"),
                    deadline.indexOf("year"))));
        }

        if (hour(deadline) != -1) {
            ret = ret.withHourOfDay(hour(deadline)).withMinuteOfHour(0)
                    .withSecondOfMinute(0);
        }

        if (weekDay(deadline) != -1)
            ret = ret.withDayOfWeek(weekDay(deadline)); // but make sure it
                                                        // always loops
                                                        // FORWARD!!!

        if (month(deadline) != -1 && monthDay(deadline) != -1)
            ret = ret.withMonthOfYear(month(deadline)).withDayOfMonth(
                    monthDay(deadline));

        return ret;
    }

    /**
     * @return Index of day, -1 if str isn't a day
     */
    private static int weekDay(String str) {
        str = str.toLowerCase();
        String[] days = { "sunday", "monday", "tuesday", "wednesday",
                "thursday", "friday", "saturday" };
        for (int a = 0; a < days.length; a++)
            if (str.contains(days[a]))
                return a + 1;
        return -1;
    }

    /**
     * @return Index of month, -1 if str isn't a month
     */
    private static int month(String str) {
        str = str.toLowerCase();
        String[] months = { "january", "february", "march", "april", "may",
                "june", "july", "august", "september", "october", "november",
                "december" };
        for (int a = 0; a < months.length; a++)
            if (str.contains(months[a]))
                return a + 1;
        return -1;
    }

    /**
     * @return Index of month day, -1 if str isn't a month day
     */
    private static int monthDay(String str) {
        str = str.toLowerCase();
        for (int a = 1; a <= 31; a++)
            if (str.contains(a + "") && !str.contains("2" + a)
                    && !str.contains("1" + a))
                return a;
        return -1;
    }

    /**
     * @return Index of month, -1 if str isn't a month
     */
    private static int hour(String str) {
        str = str.toLowerCase();
        for (int a = 1; a <= 12; a++)
            if (str.contains(a + "") && !str.contains("1" + a))
                return str.contains("p.m.") ? a + 12 : a;
        return -1;
    }

    private static int num(String str) {
        try {
            return Integer.parseInt(str.replaceAll("\\D", ""));
        } catch (Exception e) {
            return 1;
        }
    }

}
