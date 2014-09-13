package com.pennapps.smartschedule.scheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;

public class SchedulingCalendar {
	public static int INTERVAL_WIDTH = 15;
	
	public static int getBlocks(Interval interval) {
		return (int) (interval.toDurationMillis() / 60000L / INTERVAL_WIDTH);
	}
	
	public static int getBlocks(Period period) {
		return (int) (period.toStandardMinutes().getMinutes() / INTERVAL_WIDTH);
	}
	
	public static int countAvailable(List<Interval> intervals) {
		int total = 0;
		for(Interval intv : intervals)
			total += SchedulingCalendar.getBlocks(intv);
		
		return total;
	}
	
	private TreeSet<Event> events;

	public SchedulingCalendar() {
		events = new TreeSet<Event>();
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
	
	public List<Interval> getAvailableIntervals(Day day) {
		return getAvailableIntervals(day.getUseStart(), day.getUseEnd());
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
		List<Event> occ = new ArrayList<Event>();
		Interval time = new Interval(start, end);
		
		for(Event evnt : events) {
			if(new Interval(evnt.getStart(), evnt.getEnd()).overlaps(time))
				occ.add(evnt);
		}
		
		Collections.sort(occ);
		
		return occ;
	}
}
