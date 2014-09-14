package com.pennapps.smartschedule.scheduler;

import org.joda.time.Period;

import android.content.Context;

import com.pennapps.smartschedule.storage.StorageUtil;

public class SchedulingSettings {
	private boolean splittable;
	private boolean loadBalancing;
	private double weekendBias = 0.50;
	
	private Period minimumLength;
	private Period maximumLength;
	
	public SchedulingSettings(Context context) { // TODO all events > 1 hour should be splittable
		splittable = loadBalancing = !StorageUtil.getLaziness(context);
		minimumLength = Period.minutes(15);
		maximumLength = Period.hours(StorageUtil.getMaxTime(context));
	}
	
	public boolean isLoadBalanced() {
		return loadBalancing;
	}
	
	public SchedulingSettings setBalanced(boolean balanced) {
		loadBalancing = balanced;
		
		return this;
	}
	
	public double getWeekendBias() {
		return weekendBias;
	}
	
	public SchedulingSettings setWeekendBias(double weekendBias) {
		this.weekendBias = weekendBias;
		
		return this;
	}
	
	public boolean isSplittable() {
		return splittable;
	}
	
	public SchedulingSettings setSplittable(boolean splittable) {
		this.splittable = splittable;
		
		return this;
	}
	public Period getMinimumLength() {
		return minimumLength;
	}
	public SchedulingSettings setMinimumLength(Period minimumLength) {
		this.minimumLength = minimumLength;
		
		return this;
	}
	public Period getMaximumLength() {
		return maximumLength;
	}
	public SchedulingSettings setMaximumLength(Period maximumLength) {
		this.maximumLength = maximumLength;
		
		return this;
	}
	
	
}
