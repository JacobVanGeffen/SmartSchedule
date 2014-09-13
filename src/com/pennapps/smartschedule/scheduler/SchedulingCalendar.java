package com.pennapps.smartschedule.scheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.base.AbstractDuration;

import android.util.Log;

public class SchedulingCalendar {
	public static Period sum(List<Interval> intervals) {
		long total = 0L;
		for(Interval dur : intervals)
			total += dur.toDurationMillis();
		
		return new Period(total);
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
		Log.wtf("Event Added", "An event was added to the calendar.");
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
	
	public Map<Day, Period> getFreeTime(Day start, Day stop) {
		Map<Day, Period> free = new TreeMap<Day, Period>();
		Day current = start;
		while(current.compareTo(stop) <= 1) {
			DateTime e_start = current.getCalcStart();
			DateTime e_stop = current.getUseEnd();
			
			free.put(current, sum(getAvailableIntervals(e_start, e_stop)));
			
			current = current.next();
		}
		
		return free;
	}
	
	public List<Interval> getAvailableIntervals(DateTime start, DateTime end) {
		List<Interval> ints = new ArrayList<Interval>();
		List<Event> events = getEvents(start, end);
		
		if(events.size() == 0) {
			ints.add(new Interval(start, end));
			Log.wtf("RollingScheduler emtpy", "EMPTY!");
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
		
		Log.wtf("SchedulingCalendar last", "" + current.toDurationMillis());
		
		if(current.getEnd().compareTo(end) < 0)
			ints.add(new Interval(current.getEnd(), end));
		
		return ints;
	}
	
	public List<Event> getEvents(DateTime start, DateTime end) {
		List<Event> occ = new ArrayList<Event>();
		Interval time = new Interval(start, end);
		
		for(Event evnt : events) {
		    Log.wtf("SchedulingCalendar listEvent", "" + evnt);
		    
		    Interval eventInterval = new Interval(evnt.getStart(), evnt.getEnd());
		    if(eventInterval.overlaps(time) || time.overlaps(eventInterval))
				occ.add(evnt);
		}
		
		Collections.sort(occ);
		
		return occ;
	}
}
