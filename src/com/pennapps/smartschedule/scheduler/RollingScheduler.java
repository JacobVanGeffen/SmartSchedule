package com.pennapps.smartschedule.scheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;

import android.util.Log;

public class RollingScheduler {
	
	public static List<Interval> getScheduleIntervals(SchedulingCalendar calendar, Day start, ScheduledEvent nextEvent, SchedulingSettings settings) {
		// Look for first good opportunity to put problem.
		// Add splitting at a later time.
		
		List<Interval> times = new ArrayList<Interval>();
		
		Day deadline = new Day(nextEvent.getDeadline());
		Day current = start;
		while(current.compareTo(deadline) <= 0) {
			times.addAll(getDailyIntervals(calendar, current, nextEvent, settings));
			
			current = current.next();
		}
		
		return times;
	}
	
	public static Interval getFirstInterval(SchedulingCalendar calendar, Day start, ScheduledEvent nextEvent, SchedulingSettings settings) {
		Day deadline = new Day(nextEvent.getDeadline());
		Day current = start;
		
		while(current.compareTo(deadline) <= 0) {
			List<Interval> temp = getDailyIntervals(calendar, current, nextEvent, settings);
			if(temp.size() > 0)
				return temp.get(0);

			current = current.next();
		}
		
		return null;
	}
	
	public static List<Interval> getDailyIntervals(SchedulingCalendar calendar, Day current, ScheduledEvent nextEvent, SchedulingSettings settings) {
		List<Interval> times = new ArrayList<Interval>();
		
		
		DateTime dailyStart = current.getCalcStart();
		DateTime dailyStop = current.getCalcStop(nextEvent.getDeadline());
		
		if(dailyStop.isBefore(dailyStart)) {
			return new ArrayList<Interval>();
		}
		
		List<Interval> intervals = calendar.getAvailableIntervals(dailyStart, dailyStop); // TODO: Be the events deadline.
		
		for(Interval i : intervals) {
			if(i.toDuration().getMillis() >= nextEvent.getDuration().getMillis())
			{
				DateTime evnt_start = i.getStart();
				DateTime evnt_stop = evnt_start.plusMillis((int) (i.toDuration().getMillis() - nextEvent.getDuration().getMillis()));
			
				times.add(new Interval(evnt_start, evnt_stop));
			}
		}
		
		return times;
	}
	
	public static Event scheduleFirst(SchedulingCalendar calendar, Day start, ScheduledEvent event, SchedulingSettings settings) {
		Interval interval = getFirstInterval(calendar, start, event, settings);
		
		if(interval == null)
		    return null;
		
		DateTime realStart = interval.getStart();
		
		Event realEvent = new Event(-1L, event.getName(), realStart, realStart.plus(event.getDuration()));
		return realEvent;
	}
	
	public static Event scheduleFirst(SchedulingCalendar calendar, ScheduledEvent event, SchedulingSettings settings) {
		return scheduleFirst(calendar, Day.today(), event, settings);
	}
	
	public static List<Event> scheduleSplit(SchedulingCalendar calendar, Day start, ScheduledEvent event, SchedulingSettings settings) {
		List<Event> events = new ArrayList<Event>();
		
		Duration eventTime = event.getDuration();
		Log.wtf("Event Time", "" + eventTime.getMillis());
		
		int part = 1;
		
		Day current = start;
		Day stop = new Day(event.getDeadline());
		
		Duration runoverTime = new Duration(0L);
		while(current.compareTo(stop) <= 0 && eventTime.getMillis() > 0) {
			DateTime e_start = current.getCalcStart();
			DateTime e_stop = current.getCalcStop(event.getDeadline());
			
			if(e_start.isAfter(e_stop)) {
				current = current.next();
				continue;
			}
			
			List<Interval> intervals = calendar.getAvailableIntervals(e_start, e_stop);

			Duration dailyLimit = new Duration(Math.min(eventTime.getMillis(), settings.isLoadBalanced() ? settings.getMaximumLength().getMillis() : Duration.standardDays(1).getMillis()))
									.plus(runoverTime);		
			for(Interval interval : intervals) {
				if(dailyLimit.getMillis() <= 0) break;
				
				if(interval.toDurationMillis() > settings.getMinimumLength().getMillis()) {
					long realDuration = Math.min(dailyLimit.getMillis(), interval.toDurationMillis());
					dailyLimit = new Duration(dailyLimit.getMillis() - realDuration);
					eventTime = new Duration(eventTime.getMillis() - realDuration);
					
					Event eventFragment = new Event(-1, event.getName(), interval.getStart(), 
							interval.getStart().plus(realDuration));
					
					events.add(eventFragment);
				}
			}
			
			runoverTime = dailyLimit;
			current = current.next();
		}
		
		if(events.size() == 0) {
			return events;
		}
		
		if(eventTime.getMillis() > 0 && settings.isLoadBalanced()) {
			ScheduledEvent tempEvent = new ScheduledEvent(event.getName(), event.getDeadline(), eventTime);
			SchedulingCalendar tempCalendar = new SchedulingCalendar(calendar);
			for(Event evnt : events)
				tempCalendar.addEvent(evnt);
			
			List<Event> res = scheduleSplit(tempCalendar, start, tempEvent, settings);
			
			if(res.size() > 0) {
				events.addAll(res);
				events = SchedulingCalendar.flattenEvents(events);
			}
		}
		
		Collections.sort(events);
		
		if(events.size() > 1) {
			for(Event ev : events) {
				ev.setName(event.getName());
				ev.setName(ev.getName() + " (Part " + part++ + " )");
			}
		}
		
		return events;
	}
	
	public static List<ScheduledEvent> split(ScheduledEvent event, int splits, Duration minimumTime) {
		List<ScheduledEvent> events = new ArrayList<ScheduledEvent>();
		long millisPerSegment = event.getDuration().getMillis() / splits;
		Duration segmentLength = new Duration(Math.max(minimumTime.getMillis(), millisPerSegment));
		
		Duration remaining = event.getDuration();
		while(remaining.getMillis() > 0) {
			ScheduledEvent evnt = new ScheduledEvent(event.getName(), event.getDeadline(), segmentLength);
			remaining = remaining.minus(segmentLength);
			
			events.add(evnt);
		}
		
		return events;
	}
	
	public static List<ScheduledEvent> split(ScheduledEvent event, int splits) {
		return split(event, splits, Duration.millis(0));
	}
}
