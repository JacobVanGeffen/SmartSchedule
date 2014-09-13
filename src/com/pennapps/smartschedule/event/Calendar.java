package com.pennapps.smartschedule.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;

public class Calendar {
	public static int getBlocks(Interval interval, int intervalWidth) {
		return (int) (interval.toDurationMillis() / 60000L / intervalWidth);
	}
	
	public static int getBlocks(Period period, int intervalWidth) {
		return (int) (period.toStandardMinutes().getMinutes() / intervalWidth);
	}
	
	private TreeSet<EventOccurence> events;

	public Calendar() {
		events = new TreeSet<EventOccurence>();
	}
	
	public Calendar(Calendar other) {
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
	
	public List<EventOccurence> getOccurences(DateTime day) {
		DateTime start = day.withTimeAtStartOfDay();
		DateTime end = day.plusDays(1).withTimeAtStartOfDay(); // End of the day or start of next day.
		
		return getOccurences(start, end);
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
