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
	
	public Event(String name, EventMetadata meta) {
		this.eventName = name;
		
		archetype = null;
		fullMetadata = meta;
	}
	
	public EventOccurence singleton() {
		return new EventOccurence(this, true);
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
	
	public EventArchetype getArchetype() {
		return archetype;
	}
	
	public Event setArchetype(EventArchetype t) {
		this.archetype = t;
		
		return this;
	}
	
	public EventMetadata getMetadata() {
		return fullMetadata;
	}
	
	@Override
	public String toString(){
	    return eventName+" "+fullMetadata;
	}
}
