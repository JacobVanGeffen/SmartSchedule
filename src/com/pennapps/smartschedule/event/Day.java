package com.pennapps.smartschedule.event;

import org.joda.time.DateTime;

public class Day implements Comparable<Day> {
	private DateTime startTime;
	
	public Day(DateTime someTime) {
		this.startTime = someTime.withTimeAtStartOfDay();
	}
	
	public DateTime getStart() {
		return startTime;
	}
	
	public DateTime getEnd() {
		return startTime.plusDays(1).withTimeAtStartOfDay();
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
