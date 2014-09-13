package com.pennapps.smartschedule.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.joda.time.Interval;

public class Scheduler {
	// Test case: Works 9 AM - 5 PM Monday - Friday
	// Wants a moderate spread on his work (with 15 minute breaks b/t blocks)
	
	public static Calendar schedule(Calendar input, List<Event> unscheduled, Interval schedulingInterval, SchedulerSettings settings) {
		Calendar tentative = new Calendar(input);
		
		List<Event> realUnscheduled = new ArrayList<Event>(unscheduled);
		Collections.sort(realUnscheduled, new Comparator<Event>() {
			@Override
			public int compare(Event first, Event second) {
				if(first.getMetadata().hasFlag(EventFlag.Atomic) && !second.getMetadata().hasFlag(EventFlag.Atomic))
					return -1;
				else if(!first.getMetadata().hasFlag(EventFlag.Atomic) && second.getMetadata().hasFlag(EventFlag.Atomic))
					return 1;
				return first.getMetadata().getDeadline().compareTo(second.getMetadata().getDeadline());
			}
		});
		
		for(Event event : realUnscheduled) {
			tentative = schedule(tentative, event, schedulingInterval, settings);
		}
		
		return tentative;
	}
	
	// This is patented "stupid scheduling"
	public static Calendar schedule(Calendar input, Event event, Interval interval, SchedulerSettings settings) {
		int blockWidth = Calendar.getBlocks(event.getMetadata().getDuration(), 15);
		List<Interval> availableIntervals = input.getAvailableIntervals(interval.getStart(), interval.getEnd());
		
		int remainingBlocks = blockWidth;
		for(Interval intv : availableIntervals) {
			int localBlocks = Calendar.getBlocks(intv, 15);
			if(localBlocks >= blockWidth) {
				// This actually involves adding the event.
				EventOccurence single = new EventOccurence(event, true);
				input.addEvent(single);
			}
		}
		
		return input;
	}
}
