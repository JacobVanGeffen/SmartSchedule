package com.pennapps.smartschedule.event;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Interval;

public class RollingScheduler {
	
	public List<DateTime> schedule(SchedulingCalendar calendar, Day start, Event nextEvent, SchedulerSettings settings) {
		// Look for first good opportunity to put problem.
		// Add splitting at a later time.
		
		List<DateTime> times = new ArrayList<DateTime>();
		
		Day deadline = new Day(nextEvent.getMetadata().getDeadline());
		Day current = start;
		while(current.compareTo(deadline) <= 0) {
			List<Interval> intervals = calendar.getAvailableIntervals(current); // TODO: Be the events deadline.
			for(Interval i : intervals) {
				int length = SchedulingCalendar.getBlocks(i);
				if(length >= SchedulingCalendar.getBlocks(nextEvent.getMetadata().getDuration())) // Available
					times.add(i.getStart());
			}
			
			current = current.next();
		}
		
		return times;
	}
}
