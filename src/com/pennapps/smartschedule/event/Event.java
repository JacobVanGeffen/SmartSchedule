package com.pennapps.smartschedule.event;

import java.util.List;


public class Event {
	public static enum Flags {
		Fixed,
		Required
	}
	
	public static enum Type {
		Repeating
	}
	
	private long eventID;
	private String eventName;
	
	private EventArchetype archetype;
	private EventMetadata fullMetadata;
	
	public Event(String name) {
		this.eventName = name;
		eventID = 0L;
		
		archetype = null;
		fullMetadata = new EventMetadata();
	}
	
	public EventOccurence singleton() {
		return new EventOccurence(this, true);
	}
	
	public long getID() {
		return eventID;
	}
	
	public void setID(long id) {
		this.eventID = id;
	}
	
	public String getName() {
		return eventName;
	}
	
	public void setName(String name) {
		this.eventName = name;
	}
	
	public EventArchetype getArchetype() {
		return archetype;
	}
	
	public void setArchetype(EventArchetype t) {
		this.archetype = t;
	}
	
	public EventMetadata getMetadata() {
		return fullMetadata;
	}
	
	@Override
	public String toString(){
	    return eventName+" "+fullMetadata;
	}
}
