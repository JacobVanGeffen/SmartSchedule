package com.pennapps.smartschedule;

import java.util.ArrayList;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;

import android.annotation.SuppressLint;
import android.util.Log;

import com.pennapps.smartschedule.scheduler.ScheduledEvent;

@SuppressLint("DefaultLocale")
public class TextParser {

    public static final String[] 
            eventNamePostMarkers = { "on", "due", "take", "last", "for", "spend", "due", "finish by", },
            eventNamePreMarkers = { "have", "there's", "got", "hey", "schedule" },
            deadlinePreMarkers = { "due", "finish by", "on" },
            deadlineTimeMarkers = { "at", "after" }, 
            durationPreMarkers = { "take", "last", "for", "spend" };

    public static ScheduledEvent getScheduledEvent(ArrayList<String> speech) {
        double maxScore = 0;
        ScheduledEvent best = null;
        for (String s : speech) {
            ScheduledEvent e = null;
            try {
                e = getScheduledEvent(s);
            } catch (Exception ex) {
                Log.wtf("Excepiton", ex.toString());
                continue;
            }
            if (score(e) > maxScore) {
                maxScore = score(e);
                best = e;
            }
        }
        return best;
    }

    private static double score(ScheduledEvent e) {
        double score = 0;
        if (e.getName() != "")
            score++;
        if (e.getDeadline() != null)
            score++;
        if (e.getDuration() != null)
            score++;
        return score;
    }

    public static ScheduledEvent getScheduledEvent(String speech) {
        Log.wtf("Speech", speech);
        speech = speech.toLowerCase();

        String name = null, deadline = null, duration = null;
        ScheduledEvent event;

        for (String splitter : eventNamePostMarkers) {
            if (name == null
                    || name.length() > speech.split(splitter)[0].length()) {
                name = speech.split(splitter)[0];
            }
        }

        for (String rep : eventNamePreMarkers)
            name = name.replaceAll(rep + ".*", "");
        name = name.toLowerCase();
        name = name.trim();
        if (name.length() > 0)
            name = name.substring(0, 1).toUpperCase() + name.substring(1);

        event = new ScheduledEvent(name);

        for (String splitter : deadlinePreMarkers) {
            if (deadline == null
                    || deadline.length() > speech.split(splitter)[speech
                            .split(splitter).length - 1].length()) {
                String[] ar = speech.split(splitter);
                deadline = ar[ar.length - 1];
            }
        }

        for (String rep : durationPreMarkers)
            deadline = deadline.replaceAll(rep + ".*", "");
        deadline = deadline.toLowerCase();
        deadline = deadline.trim();

        Log.wtf("deadline", deadline);
        
        event.setDeadline(interpretDeadline(deadline));

        for (String splitter : durationPreMarkers) {
            if (duration == null
                    || duration.length() > speech.split(splitter)[speech
                            .split(splitter).length - 1].length()) {
                String[] ar = speech.split(splitter);
                duration = ar[ar.length - 1];
            }
        }

        if (duration.equals(speech)) {
            event.setDuration(null);
            return event;
        }

        event.setDuration(time(duration));

        return event;
    }

    /**
     * Only accepts due dates w/ hour as highest accuracy (can't say
     * "due at 5:30")
     * 
     * Time zone seems to be messed up
     * 
     * TODO account for "on the 15th" w/o given month (assume current)
     * 
     * @param deadline
     * @return
     */
    private static DateTime interpretDeadline(String deadline) {
        // P("Deadline", deadline);
        DateTime ret = DateTime.now(), orig = ret;

        // Log.wtf("orig", orig + "");

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

        if (deadline.matches(".*\\Wday.*")) { // "in 5 days..." or "in a day"
            int start = deadline.indexOf("in"), end = deadline.indexOf("day");
            if (start != -1)
                ret = ret.plusDays(num(deadline.substring(start, end)));
        } else if (deadline.contains("week")) {
            int start = deadline.indexOf("in"), end = deadline.indexOf("week");
            if (start != -1)
                ret = ret.plusWeeks(num(deadline.substring(start, end)));
        } else if (deadline.contains("month")) {
            int start = deadline.indexOf("in"), end = deadline.indexOf("month");
            if (start != -1)
                ret = ret.plusMonths(num(deadline.substring(start, end)));
        } else if (deadline.contains("year")) {
            int start = deadline.indexOf("in"), end = deadline.indexOf("year");
            if (start != -1)
                ret = ret.plusYears(num(deadline.substring(start, end)));
        }

        if (hour(deadline) != -1) {
            if (orig.getHourOfDay() > hour(deadline))
                ret = ret.plusDays(1);
            ret = ret.withHourOfDay(hour(deadline)).withMinuteOfHour(0)
                    .withSecondOfMinute(0);
        }

        if (weekDay(deadline) != -1) {
            if (orig.getDayOfWeek() >= weekDay(deadline))
                ret = ret.plusWeeks(1);
            ret = ret.withDayOfWeek(weekDay(deadline));
        }

        if (month(deadline) != -1) {
            if (orig.getMonthOfYear() > month(deadline))
                ret = ret.plusYears(1);
            ret = ret.withMonthOfYear(month(deadline));
        }

        if (monthDay(deadline) != -1) {
            if (orig.getDayOfMonth() > monthDay(deadline))
                ret = ret.plusMonths(1);
            ret = ret.withDayOfMonth(monthDay(deadline));
        }

        // Log.wtf("ret", ret + "");

        return ret.equals(orig) ? null : ret;
    }

    /**
     * @return Index of day, -1 if str isn't a day
     */
    private static int weekDay(String str) {
        str = str.toLowerCase();
        String[] days = { "monday", "tuesday", "wednesday", "thursday",
                "friday", "saturday", "sunday" };
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
        for (int a = 31; a > 0; a--)
            if (str.matches(".*\\D?" + a + "[a-z]{2}.*"))
                return a;
        return -1;
    }

    /**
     * @return Index of hour, -1 if str isn't an hour
     */
    private static int hour(String str) {
        str = str.toLowerCase();
        for (int a = 12; a > 0; a--)
            if (str.matches(".*\\D?" + a + "\\D?.*"))
                return str.contains("p.m.") ? a % 12 + 12 : a % 12;
        return -1;
    }

    /**
     * @return The integer within this string
     */
    private static int num(String str) {
        try {
            return Integer.parseInt(str.replaceAll("\\D", ""));
        } catch (Exception e) {
            return 1;
        }
    }

    private static Duration time(String duration) {
        Period period = Period.ZERO;

        if (duration.contains("second"))
            period = period.plusSeconds(num(duration
                    .substring(0, duration.indexOf("second")).trim()
                    .replaceAll(".* ", "")));
        if (duration.contains("minute"))
            period = period.plusMinutes(num(duration
                    .substring(0, duration.indexOf("minute")).trim()
                    .replaceAll(".* ", "")));
        if (duration.contains("hour"))
            period = period.plusHours(num(duration
                    .substring(0, duration.indexOf("hour")).trim()
                    .replaceAll(".* ", "")));
        if (duration.contains("day"))
            period = period.plusDays(num(duration
                    .substring(0, duration.indexOf("day")).trim()
                    .replaceAll(".* ", "")));

        return period.equals(Period.ZERO) ? null : period.toStandardDuration();
    }

}
