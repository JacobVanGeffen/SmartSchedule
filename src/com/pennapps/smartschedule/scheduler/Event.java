package com.pennapps.smartschedule.scheduler;

import org.joda.time.DateTime;

public class Event implements Comparable<Event> {
	private long eventID;
	private long calendarID;
	private String eventName;
	
	private DateTime start, stop;
	
	public Event(long ID, long calendarID, String name, DateTime start, DateTime stop) {
		this.eventID = ID;
		this.calendarID = calendarID;
		this.eventName = name;
		this.start = start;
		this.stop = stop;
	}
	
	public Event(long ID, String name, DateTime start, DateTime stop) {
	    this(ID, -1, name, start, stop);
	}
	
	public long getID() {
		return eventID;
	}
	
	public long getCalendarID() {
	    return calendarID;
	}
	
	public void setCalendarID(long id) {
	    this.calendarID = id;
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
