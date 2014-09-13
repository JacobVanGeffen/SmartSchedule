package com.pennapps.smartschedule.scheduler;

import java.util.List;

import org.joda.time.Period;

public class DailySettings {
	private List<Event> staticEvents;
	private Period requestedFreeTime;
	
	public DailySettings(List<Event> statics, Period freeTime) {
		staticEvents = statics;
	}
	
	public List<Event> getStaticEvents() {
		return staticEvents;
	}
}
