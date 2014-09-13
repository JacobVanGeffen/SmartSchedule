package com.pennapps.smartschedule.event;

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
	
	private TreeSet<EventOccurence> events;

	public SchedulingCalendar() {
		events = new TreeSet<EventOccurence>();
	}
	
	public SchedulingCalendar(SchedulingCalendar other) {
		events = new TreeSet<EventOccurence>(other.events);
	}
	
	public void updateEvent(EventOccurence evnt) {
		events.remove(evnt);
		events.add(evnt);
	}
	
	public void addEvent(EventOccurence evnt) {
		events.add(evnt);
	}
	
	public boolean removeEvent(EventOccurence evnt) {
		return events.remove(evnt);
	}
	
	public boolean hasEvent(EventOccurence evnt) {
		return events.contains(evnt);
	}
	
	public List<EventOccurence> getOccurences(Day day) {		
		return getOccurences(day.getStart(), day.getEnd());
	}
	
	public List<Interval> getAvailableIntervals(Day day) {
		return getAvailableIntervals(day.getStart(), day.getEnd());
	}
	
	public List<Interval> getAvailableIntervals(DateTime start, DateTime end) {
		List<Interval> ints = new ArrayList<Interval>();
		Iterator<EventOccurence> iter = events.iterator();
		
		EventOccurence cont = null, next = null;
		while(iter.hasNext() && (cont = iter.next()).getMetadata().getEnd().isBefore(start));
		
		while(iter.hasNext() && (next = iter.next()).getMetadata().getStart().isBefore(end)) {
			if(!cont.getMetadata().getEnd().equals(next.getMetadata().getStart()))
				ints.add(new Interval(cont.getMetadata().getEnd(), cont.getMetadata().getStart()));
			
			cont = next;
		}
		
		return ints;
	}
	
	public List<EventOccurence> getOccurences(DateTime start, DateTime end) {
		List<EventOccurence> occ = new ArrayList<EventOccurence>();
		for(EventOccurence evnt : events) {
			DateTime st = evnt.getMetadata().getStart();
			if(st.isAfter(start) && st.isBefore(end))
				occ.add(evnt);
			
			if(st.isAfter(end))
				break;
		}
		
		return occ;
	}
}
