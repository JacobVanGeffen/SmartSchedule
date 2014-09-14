package com.pennapps.smartschedule.scheduler;

import org.joda.time.DateTime;
import org.joda.time.Duration;

public class ScheduledEvent {
	private String name;
	private DateTime deadline;
	private Duration duration;
	
	public ScheduledEvent(String name, DateTime deadline, Duration dur) {
		this.name = name;
		this.deadline = deadline;
		this.duration = dur;
	}
	
	public ScheduledEvent(String name) {
		this(name, DateTime.now(), Duration.standardMinutes(30));
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
	
	public Duration getDuration() {
		return duration;
	}
	
	public ScheduledEvent setDuration(Duration per) {
		this.duration = per;
		
		return this;
	}
	
	@Override
	public String toString() {
	    return name + ", " + deadline + ", " + duration;
	}
}
