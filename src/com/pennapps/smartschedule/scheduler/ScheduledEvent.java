package com.pennapps.smartschedule.scheduler;

import org.joda.time.DateTime;
import org.joda.time.Period;

public class ScheduledEvent {
	private String name;
	private DateTime deadline;
	private Period duration;
	
	public ScheduledEvent(String name, DateTime deadline, Period dur) {
		this.name = name;
		this.deadline = deadline;
		this.duration = dur;
	}
	
	public ScheduledEvent(String name) {
		this(name, DateTime.now(), Period.minutes(30));
	}
	
	public String getName() {
		return name;
	}
	
	public ScheduledEvent setName(String name) {
		this.name = name;
		
		return this;
	}
	
	public DateTime getDeadline() {
		return deadline;
	}
	
	public ScheduledEvent setDeadline(DateTime dead) {
		this.deadline = dead;
		
		return this;
	}
	
	public Period getDuration() {
		return duration;
	}
	
	public ScheduledEvent setDuration(Period per) {
		this.duration = per;
		
		return this;
	}
	
	@Override
	public String toString() {
	    return name + ", " + deadline + ", " + duration;
	}
}
