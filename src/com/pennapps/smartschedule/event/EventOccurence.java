package com.pennapps.smartschedule.event;

import org.joda.time.DateTime;

public class EventOccurence implements Comparable<EventOccurence> {
	private long occurenceID;
	
	private Event event;
	private EventMetadata meta;
	
	public EventOccurence(Event event) {
		this.event = event;
		
		meta = new EventMetadata();
	}
	
	public EventOccurence(Event event, boolean totalCopy) {
		
	}
	
	public long getID() {
		return occurenceID;
	}
	
	public void setEvent(long id) {
		this.occurenceID = id;
	}
	
	public Event getEvent() {
		return event;
	}
	
	public EventMetadata getMetadata() {
		return meta;
	}
	
	@Override
	public int compareTo(EventOccurence other) {
		return getMetadata().getStart().compareTo(other.getMetadata().getStart());
	}
	
	@Override
	public boolean equals(Object o) {
		EventOccurence ev = (EventOccurence) o;
		return ev.occurenceID == this.occurenceID;
	}
}
