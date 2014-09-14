package com.pennapps.smartschedule.scheduler;

import org.joda.time.Duration;

import android.content.Context;
import android.util.Log;

import com.pennapps.smartschedule.storage.StorageUtil;

public class SchedulingSettings {
	private boolean splittable;
	private boolean loadBalancing;
	private double weekendBias = 0.50;
	
	private Duration minimumLength;
	private Duration maximumLength;
	
	public SchedulingSettings(Context context) {
		splittable = loadBalancing = !StorageUtil.getLaziness(context);
		Log.wtf("Banacing", loadBalancing+"");
		minimumLength = Duration.standardMinutes(15);
		maximumLength = Duration.standardHours(StorageUtil.getMaxTime(context));
		Log.wtf("Max length", maximumLength+"");
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
	public Duration getMinimumLength() {
		return minimumLength;
	}
	public SchedulingSettings setMinimumLength(Duration minimumLength) {
		this.minimumLength = minimumLength;
		
		return this;
	}
	public Duration getMaximumLength() {
		return maximumLength;
	}
	public SchedulingSettings setMaximumLength(Duration maximumLength) {
		this.maximumLength = maximumLength;
		
		return this;
	}
	
	
}
