package com.pennapps.smartschedule.scheduler;

import org.joda.time.DateTime;

public class Event implements Comparable<Event> {
	private long eventID;
	private String eventName;
	
	private DateTime start, stop;
	
	public Event(long ID, String name, DateTime start, DateTime stop) {
		this.eventID = ID;
		this.eventName = name;
		this.start = start;
		this.stop = stop;
	}
	
	public long getID() {
		return eventID;
	}
	
	public Event setID(long id) {
		this.eventID = id;
		
		return this;
	}
	
	public String getName() {
		return eventName;
	}
	
	public Event setName(String name) {
		this.eventName = name;
		
		return this;
	}
	
	public DateTime getStart() {
		return start;
	}
	
	public Event setStart(DateTime start) {
		this.start = start;
		
		return this;
	}
	
	public DateTime getEnd() {
		return stop;
	}
	
	public Event setStop(DateTime stop) {
		this.stop = stop;
		
		return this;
	}
	
	@Override
	public String toString(){
	    return eventName;
	}
	
	@Override
	public int compareTo(Event other) {
		return start.compareTo(other.getStart());
	}
}
