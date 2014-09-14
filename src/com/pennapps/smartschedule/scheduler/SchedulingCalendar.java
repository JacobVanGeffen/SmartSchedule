package com.pennapps.smartschedule.scheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.Period;

public class SchedulingCalendar {
    public static Duration sum(List<Interval> intervals) {
        long total = 0L;
        for(Interval dur : intervals)
            total += dur.toDurationMillis();
        
        return new Duration(total);
    }
    
    public static Duration sumLists(List<Event> events) {
    	long total = 0L;
    	for(Event evnt : events)
    		total += new Duration(evnt.getStart(), evnt.getEnd()).getMillis();
    	
    	return new Duration(total);
    }
    
    public static List<Interval> flatten(List<Interval> inputs) {
    	List<Interval> flattened = new ArrayList<Interval>(inputs);
    	if(inputs.size() == 0) return flattened;
    	
        // "Flatten" the intervals to have a single group of intervals.
        Iterator<Interval> flattener = flattened.iterator();
        Interval current = flattener.next();
        while(flattener.hasNext()) {
            Interval next = flattener.next();
            if(current.overlaps(next) || current.abuts(next)) {
                flattener.remove();
                current = current.withEndMillis(Math.max(current.getEndMillis(), next.getEndMillis()));
            }
            else
            {
                current = next;
            }
        }
        
        return flattened;
    }
    
    public static List<Event> flattenEvents(List<Event> input) {
        List<Interval> flattened = new ArrayList<Interval>();
        for(Event evnt : input) {
            DateTime e_start = new DateTime(evnt.getStart().getMillis());
            DateTime e_end = new DateTime(evnt.getEnd().getMillis());
            flattened.add(new Interval(e_start, e_end));
        }
        
        List<Event> output = new ArrayList<Event>();
        List<Interval> res = flatten(flattened);
        for(Interval intv : res)
        	output.add(new Event(-1, "Temporary Name", intv.getStart(), intv.getEnd()));
        
        return output;
    }
    
    private long calendarID;
    private TreeSet<Event> events;
    
    public SchedulingCalendar(long calID) {
        events = new TreeSet<Event>();
        calendarID = calID;
    }
    
    public SchedulingCalendar(SchedulingCalendar other) {
        events = new TreeSet<Event>(other.events);
    }
    
    public Period lookupDuration(String eventName) {
        for(Event evnt : events) {
            if(evnt.getName().equals(eventName))
                return new Period(evnt.getStart(), evnt.getEnd());
        }
        return null;
    }
    
    public void updateEvent(Event evnt) {
        events.remove(evnt);
        events.add(evnt);
    }
    
    public void addEvent(Event evnt) {
        events.add(evnt);
        evnt.setCalendarID(calendarID);
    }
    
    public boolean removeEvent(Event evnt) {
        return events.remove(evnt);
    }
    
    public boolean hasEvent(Event evnt) {
        return events.contains(evnt);
    }
    
    public List<Event> getEvents(Day day) {     
        return getEvents(day.getStart(), day.getEnd());
    }
    
    public long getCalendarID() {
        return calendarID;
    }
    
    public List<Interval> getAvailableIntervals(Day day) {
        return getAvailableIntervals(day.getUseStart(), day.getUseEnd());
    }
    
    public Map<Day, Duration> getFreeTime(Day start, Day stop, DateTime deadline) {
        Map<Day, Duration> free = new TreeMap<Day, Duration>();
        Day current = start;
        while(current.compareTo(stop) <= 1) {
            free.put(current, getFreeTime(current, deadline));
        }
        
        return free;
    }
    
    public Duration getFreeTime(Day day, DateTime deadline) {
    	DateTime e_start = day.getCalcStart();
        DateTime e_stop = day.getCalcStop(deadline);
        
        return sum(getAvailableIntervals(e_start, e_stop));
    }
    
    public List<Interval> getAvailableIntervals(DateTime start, DateTime end) {
        List<Interval> ints = new ArrayList<Interval>();
        List<Event> events = getEvents(start, end);
        
        if(events.size() == 0) {
            ints.add(new Interval(start, end));
            return ints;
        }
    	
        List<Interval> flattened = new ArrayList<Interval>();
        for(Event evnt : events) {
            DateTime e_start = new DateTime(Math.max(evnt.getStart().getMillis(), start.getMillis()));
            DateTime e_end = new DateTime(Math.min(evnt.getEnd().getMillis(), end.getMillis()));
            flattened.add(new Interval(e_start, e_end));
        }
        
        // "Flatten" the intervals to have a single group of intervals.
        Iterator<Interval> flattener = flattened.iterator();
        Interval current = flattener.next();
        while(flattener.hasNext()) {
            Interval next = flattener.next();
            if(current.overlaps(next)) {
                flattener.remove();
                current = current.withEndMillis(Math.max(current.getEndMillis(), next.getEndMillis()));
            }
            else
            {
                Interval gap = current.gap(next);
                if(gap != null) ints.add(gap);
                current = next;
            }
        }
        
        if(current.getEnd().compareTo(end) < 0)
            ints.add(new Interval(current.getEnd(), end));
        
        return ints;
    }
    
    public List<Event> getEvents(DateTime start, DateTime end) {
//    	Log.wtf("Event Count", "Total Events: " + events.size());
    	
        List<Event> occ = new ArrayList<Event>();
        Interval time = new Interval(start, end);
        
        for(Event evnt : events) {
//        	Log.wtf("Event thingy", evnt.getName() + ": " + evnt.getStart() + ", " + evnt.getEnd());
        	
            Interval eventInterval = new Interval(evnt.getStart(), evnt.getEnd());
            if(eventInterval.overlaps(time) || time.overlaps(eventInterval))
                occ.add(evnt);
        }
        
        Collections.sort(occ);

//        Log.wtf("Get Events", occ+"");
        
        return occ;
    }
}
