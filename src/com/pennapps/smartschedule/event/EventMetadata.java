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
	
	public void addFlag(EventFlag f) {
		flags.add(f);
	}
	
	public Set<EventFlag> getFlags() {
		return flags;
	}
	
	public Period getDuration() {
		return timePeriod;
	}
	
	public void setDuration(Period per) {
		this.timePeriod = per;
	}
	
	public DateTime getStart() {
		return startTime;
	}
	
	public void setStart(DateTime time) {
		this.startTime = time;
	}
	
	public DateTime getEnd() {
		return getStart().plus(getDuration());
	}
	
	public DateTime getDeadline() {
		return deadline;
	}
	
	public void setDeadline(DateTime time) {
		this.deadline = time;
	}
	
	public int getPriority() {
		return priority;
	}
	
	public void setPriority(int pri) {
		this.priority = pri;
	}
}
