package com.pennapps.smartschedule.scheduler;

import org.joda.time.DateTime;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.util.Log;

public class EventFetcher {
	public static String TEST_ACCOUNT = "blacksmithgu@gmail.com";
	public static String TEST_TYPE = "com.google";
	
	private long calendarID;
	private ContentResolver resolve;
	
	public static final String[] CALENDAR_PROJECTION = new String[] {
		Calendars._ID,
		Calendars.ACCOUNT_NAME,
		Calendars.ACCOUNT_TYPE,
		Calendars.OWNER_ACCOUNT
	};
	
	public static final String[] EVENT_PROJECTION = new String[] {
		Events._ID,
		Events.TITLE,
		Events.EVENT_LOCATION,
		Events.DESCRIPTION,
		Events.DTSTART,
		Events.DTEND,
		Events.DURATION,
		Events.ALL_DAY,
		Events.AVAILABILITY,
		Events.DELETED
	};
	
	public EventFetcher(ContentResolver resolver) {
		calendarID = -1L;
		resolve = resolver;
	}
	
	public long getCalendarID() {
		if(calendarID != -1) return calendarID;
		
		String selection = "((" + Calendars.ACCOUNT_NAME + " = ?) AND (" 
                + Calendars.ACCOUNT_TYPE + " = ?) AND ("
                + Calendars.OWNER_ACCOUNT + " = ?))";
        String[] selectionArgs = new String[] {"jsvangeffen@gmail.com", "com.google",
        "jsvangeffen@gmail.com"}; 
		
		Cursor cur = resolve.query(Calendars.CONTENT_URI, CALENDAR_PROJECTION,
				selection, selectionArgs, null);
		
		while(cur.moveToNext()) {
		    Log.wtf("Calendar Provided", "" + cur.getLong(0) + ", " + cur.getString(1) + ", " + cur.getString(2));
		    calendarID = cur.getLong(0);
		    
		    return calendarID;
		}
		
		return -1L; // No ID
	}
	
	public SchedulingCalendar getCalendar() {
		if(calendarID == -1) return null; // What are u doin
		
		String query = "(" + Events.CALENDAR_ID + " = ?)";
		
		Cursor cur = resolve.query(Events.CONTENT_URI, EVENT_PROJECTION, query,
				new String[] { "" + calendarID }, null);
		
		SchedulingCalendar cal = new SchedulingCalendar(calendarID);
		while(cur.moveToNext()) {
			if(cur.getInt(9) == 0) continue;
			
			DateTime start = new DateTime(cur.getLong(4));
			DateTime end = new DateTime(cur.getLong(5));
			
			Event evnt = new Event(cur.getLong(0), cur.getString(1), start, end);
			
			cal.addEvent(evnt);
			
			Log.wtf("Event Add", evnt.toString());
		}
		
		return cal;
	}
}
