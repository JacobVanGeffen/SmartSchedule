package com.pennapps.smartschedule.scheduler;

import java.util.List;

public class DailySettings {
	private List<Event> staticEvents;
	private int requestedFreeBlocks;
	
	public DailySettings(List<Event> statics, int freeBlocks) {
		staticEvents = statics;
		requestedFreeBlocks = freeBlocks;
	}
	
	public List<Event> getStaticEvents() {
		return staticEvents;
	}
	
	public List<EventOccurence> getOccurences(Day actualDay) {
//		List<EventOccurence>
		for(Event event : staticEvents) {
			
		}
		return null;
	}
}
