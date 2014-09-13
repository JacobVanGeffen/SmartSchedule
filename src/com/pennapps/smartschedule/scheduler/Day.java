package com.pennapps.smartschedule.scheduler;

import org.joda.time.DateTime;

public class Day implements Comparable<Day> {
	public static Day today() {
		return new Day(DateTime.now());
	}
	
	private DateTime startTime;
	
	public Day(DateTime someTime) {
		this.startTime = someTime.withTimeAtStartOfDay();
	}
	
	public DateTime getStart() {
		return startTime;
	}
	
	public DateTime getUseStart() {
		return getStart().plusHours(8);
	}
	
	public DateTime getEnd() {
		return startTime.plusDays(1).withTimeAtStartOfDay();
	}
	
	public DateTime getUseEnd() {
		return getEnd().minusHours(4);
	}
	
	public Day next() {
		return new Day(getEnd());
	}
	
	@Override
	public boolean equals(Object other) {
		Day day = (Day) other;
		
		return startTime.equals(day.startTime);
	}

	@Override
	public int compareTo(Day other) {
		return startTime.compareTo(other.startTime);
	}
}
