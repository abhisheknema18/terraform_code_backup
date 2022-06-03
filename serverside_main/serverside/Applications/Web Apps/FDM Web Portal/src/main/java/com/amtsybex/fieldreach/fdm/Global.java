package com.amtsybex.fieldreach.fdm;

import java.util.TimeZone;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;


@Named
@ApplicationScoped
public class Global {
	
	private TimeZone timeZone = TimeZone.getDefault();

	public TimeZone getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

}
