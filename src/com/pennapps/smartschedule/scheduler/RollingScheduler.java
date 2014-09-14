package com.pennapps.smartschedule.scheduler;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;

import android.util.Log;

public class RollingScheduler {
	
	public static List<Interval> getScheduleIntervals(SchedulingCalendar calendar, Day start, ScheduledEvent nextEvent, SchedulingSettings settings) {
		// Look for first good opportunity to put problem.
		// Add splitting at a later time.
		
		List<Interval> times = new ArrayList<Interval>();
		
		Day deadline = new Day(nextEvent.getDeadline());
		Day current = start;
		while(current.compareTo(deadline) <= 0) {
			DateTime dailyStart = current.getCalcStart();
			DateTime dailyStop = current.getCalcStop(nextEvent.getDeadline());
			
			if(dailyStop.isBefore(dailyStart)) {
				current = current.next();
				continue;
			}
			
			List<Interval> intervals = calendar.getAvailableIntervals(dailyStart, dailyStop); // TODO: Be the events deadline.
			for(Interval ints : intervals)
			    Log.wtf("RollingScheduler getScheduleIntervals", "" + ints.toDurationMillis());
			for(Interval i : intervals) {
				if(i.toPeriod().getMillis() >= nextEvent.getDuration().getMillis())
				{
					DateTime evnt_start = i.getStart();
					DateTime evnt_stop = evnt_start.plusMillis((int) (i.toPeriod().getMillis() - nextEvent.getDuration().getMillis()));
				
					times.add(new Interval(evnt_start, evnt_stop));
				}
			}
			
			current = current.next();
		}
		
		return times;
	}
	
	public static Event scheduleFirst(SchedulingCalendar calendar, Day start, ScheduledEvent event, SchedulingSettings settings) {
		List<Interval> intervals = getScheduleIntervals(calendar, start, event, settings);
		for(Interval in : intervals)
		    Log.wtf("RollingScheduler", "" + in.toDurationMillis());
		DateTime realStart = intervals.get(0).getStart();
		
		Event realEvent = new Event(-1L, event.getName(), realStart, realStart.plus(event.getDuration()));
		return realEvent;
	}
	
	public static Event scheduleFirst(SchedulingCalendar calendar, ScheduledEvent event, SchedulingSettings settings) {
		return scheduleFirst(calendar, Day.today(), event, settings);
	}
	
	public static List<Event> scheduleSplit(SchedulingCalendar calendar, Day start, ScheduledEvent event, SchedulingSettings settings) {
		List<Event> events = new ArrayList<Event>();
		
		Period eventTime = event.getDuration();
		
		int part = 1;
		
		Day current = start;
		Day stop = new Day(event.getDeadline());
		while(current.compareTo(stop) <= 0 && eventTime.getMillis() > 0) {
			List<Interval> intervals = calendar.getAvailableIntervals(current.getCalcStart(), current.getCalcStop(event.getDeadline()));
			
			Period dailyLimit = new Period(Math.min(eventTime.getMillis(), settings.isLoadBalanced() ? settings.getMaximumLength().getMillis() : Period.days(1).getMillis()));		
			for(Interval interval : intervals) {
				if(dailyLimit.getMillis() == 0) break;
				
				if(interval.toDuration().isLongerThan(settings.getMinimumLength().toStandardDuration())) {
					long realDuration = Math.min(dailyLimit.getMillis(), interval.toDurationMillis());
					dailyLimit = new Period(dailyLimit.getMillis() - realDuration);
					eventTime = new Period(eventTime.getMillis() - realDuration);
					
					Event eventFragment = new Event(-1, event.getName() + " (Part " + part + ")", interval.getStart(), 
							interval.getStart().plus(realDuration));
					
					events.add(eventFragment);
				}
			}
		}
		
		return events;
	}
	
	public static List<ScheduledEvent> split(ScheduledEvent event, int splits, Period minimumTime) {
		List<ScheduledEvent> events = new ArrayList<ScheduledEvent>();
		long millisPerSegment = event.getDuration().getMillis() / splits;
		Period segmentLength = new Period(Math.max(minimumTime.getMillis(), millisPerSegment));
		
		Period remaining = event.getDuration();
		while(remaining.getMillis() > 0) {
			ScheduledEvent evnt = new ScheduledEvent(event.getName(), event.getDeadline(), segmentLength);
			remaining = remaining.minus(segmentLength);
			
			events.add(evnt);
		}
		
		return events;
	}
	
	public static List<ScheduledEvent> split(ScheduledEvent event, int splits) {
		return split(event, splits, Period.millis(0));
	}
}
