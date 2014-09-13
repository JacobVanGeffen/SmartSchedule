package com.pennapps.smartschedule.scheduler;

import java.util.ArrayList;
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
		return getAvailableIntervals(day.getStart(), day.getEnd());
	}
	
	public List<Interval> getAvailableIntervals(DateTime start, DateTime end) {
		List<Interval> ints = new ArrayList<Interval>();
		Iterator<Event> iter = events.iterator();
		
		Event cont = null, next = null;
		while(iter.hasNext() && (cont = iter.next()).getEnd().isBefore(start));
		
		while(iter.hasNext() && (next = iter.next()).getStart().isBefore(end)) {
			if(!cont.getEnd().equals(next.getStart()))
				ints.add(new Interval(cont.getEnd(), cont.getStart()));
			
			cont = next;
		}
		
		return ints;
	}
	
	public List<Event> getEvents(DateTime start, DateTime end) {
		List<Event> occ = new ArrayList<Event>();
		for(Event evnt : events) {
			DateTime st = evnt.getStart();
			if(st.isAfter(start) && st.isBefore(end))
				occ.add(evnt);
			
			if(st.isAfter(end))
				break;
		}
		
		return occ;
	}
}
