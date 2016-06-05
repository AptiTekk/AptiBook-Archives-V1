package com.cintriq.agenda.core.utilities;

import java.util.Calendar;

/**
 * Used to specify a time range, for example, all of April, first week of March,
 * 12 AM to 12 PM of a certain day... Contains a start time and an end time,
 * using the Calendar object.
 */
public class TimeRange {

	private Calendar startTime;
	private Calendar endTime;

	/**
	 * @param startTime The start of the time range
	 * @param endTime The end of the time range
	 */
	public TimeRange(Calendar startTime, Calendar endTime) {
		this.setStartTime(startTime);
		this.setEndTime(endTime);
	}

	public Calendar getStartTime() {
		return startTime;
	}

	public void setStartTime(Calendar startTime) {
		this.startTime = startTime;
	}

	public Calendar getEndTime() {
		return endTime;
	}

	public void setEndTime(Calendar endTime) {
		this.endTime = endTime;
	}

}
