package com.pennapps.smartschedule.event;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Period;

public class EventMetadata {
	private Period timePeriod;
	private DateTime startTime;
	private DateTime deadline;
	
	private Set<EventFlag> flags;
	private int priority;
	
	public EventMetadata() {
		timePeriod = null;
		startTime = deadline = null;
		
		flags = new HashSet<EventFlag>();
		priority = 0;
	}
	
	public EventMetadata(EventMetadata other) {
		this.timePeriod = new Period(other.timePeriod);
		this.startTime = new DateTime(other.startTime);
		if(other.deadline != null)
			this.deadline = new DateTime(other.deadline);
		
		flags = new HashSet<EventFlag>(other.flags);
		priority = other.priority;
	}
	
	public boolean hasFlag(EventFlag f) {
		return flags.contains(f);
	}
	
	public EventMetadata addFlag(EventFlag f) {
		flags.add(f);
		
		return this;
	}
	
	public Set<EventFlag> getFlags() {
		return flags;
	}
	
	public Period getDuration() {
		return timePeriod;
	}
	
	public EventMetadata setDuration(Period per) {
		this.timePeriod = per;
		
		return this;
	}
	
	public DateTime getStart() {
		return startTime;
	}
	
	public EventMetadata setStart(DateTime time) {
		this.startTime = time;
		
		return this;
	}
	
	public DateTime getEnd() {
		return getStart().plus(getDuration());
	}
	
	public DateTime getDeadline() {
		return deadline;
	}
	
	public EventMetadata setDeadline(DateTime time) {
		this.deadline = time;
		
		return this;
	}
	
	public int getPriority() {
		return priority;
	}
	
	public EventMetadata setPriority(int pri) {
		this.priority = pri;
		
		return this;
	}
	
	@Override
	public String toString(){
	    return "Start: "+startTime+", Deadline: "+deadline+", Time Period: "+timePeriod;
	}
}
