package com.pennapps.smartschedule.test;

import org.joda.time.DateTime;
import org.joda.time.Period;

import com.pennapps.smartschedule.scheduler.Event;
import com.pennapps.smartschedule.scheduler.RollingScheduler;
import com.pennapps.smartschedule.scheduler.ScheduledEvent;
import com.pennapps.smartschedule.scheduler.SchedulingCalendar;
import com.pennapps.smartschedule.scheduler.SchedulingSettings;

public class CalendarTests {
	public static void main(String[] args) throws Exception {
		SchedulingCalendar cal = new SchedulingCalendar();
		ScheduledEvent evnt = new ScheduledEvent("Hello!", DateTime.now().plusDays(2), Period.hours(2));
		
		Event ev = RollingScheduler.scheduleFirst(cal, evnt, new SchedulingSettings());
		
		System.out.println(ev);
	}
}
