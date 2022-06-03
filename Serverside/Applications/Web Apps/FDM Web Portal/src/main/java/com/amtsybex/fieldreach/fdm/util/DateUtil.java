package com.amtsybex.fieldreach.fdm.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amtsybex.fieldreach.utils.impl.Common;

/**
 * helper class used for converting times and dates
 * into readable formats
 */
public class DateUtil {
	
	private static final String DATE_FORMAT = "yyyyMMdd";
	private static final String TIME_FORMAT = "HHmmss";
	
	private static Logger _logger = LoggerFactory.getLogger(DateUtil.class.getName());
	
	public static Date intToDate(Integer date){
		return DateUtil.stringToDate(String.valueOf(date));
	}
	
	public static Date stringToDate(String dateStr){
		Date date = null;
		
		try {
			date = new SimpleDateFormat(DATE_FORMAT).parse(dateStr);
		} catch (ParseException e) {
			_logger.debug("unparseable date " + dateStr);
		}
		
		return date;
	}
	
	public static String formatDate(Date dateVal) {
		if (dateVal != null) {
			return new SimpleDateFormat(DATE_FORMAT).format(dateVal);
		}
		return null;
	}
	
	public static Integer formatDateInt(Date dateVal) {
		if (dateVal != null) {
			return Integer.valueOf(new SimpleDateFormat(DATE_FORMAT).format(dateVal));
		}
		return null;
	}
	
	public static String formatTime(Date dateVal) {
		if (dateVal != null) {
			return new SimpleDateFormat(TIME_FORMAT).format(dateVal);
		}
		return null;
	}

	/**
	 * method for converting time into hh:mm:ss or hh:mm format
	 * 
	 * @param time	- the time, as stored in the db
	 * @return		- formatted time
	 */
	public static String parseTime(String time){	
		if(time != null && !time.isEmpty()) {
		time = time.replace(":", "");

		String parsedTime = null;
		Date formattedTime = null;
		SimpleDateFormat existingDateFormat;
		SimpleDateFormat newDateFormat;
		
		if (time.length() <= 4){
			// pad to 4 characters with leading zeroes
			parsedTime = StringUtils.leftPad(time, 4, "0");
			existingDateFormat = new SimpleDateFormat("HHmm");
			
			try {
				formattedTime = existingDateFormat.parse(parsedTime);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			newDateFormat = new SimpleDateFormat("HH:mm");
		}
		else {
			// pad to 6 characters with leading zeroes
			parsedTime = StringUtils.leftPad(time, 6, "0");
			existingDateFormat = new SimpleDateFormat("HHmmss");
			
			try {
				formattedTime = existingDateFormat.parse(parsedTime);
			} catch (ParseException e) {
				_logger.debug("unparseable time " + time);
			}
			
			newDateFormat = new SimpleDateFormat("HH:mm:ss");
		}
		
		
		return newDateFormat.format(formattedTime);
		}
		return null;
	}
	
	
	public static String dateFormatter(Date dateVal) {
		String strDate = DateFormat.getDateInstance(DateFormat.MEDIUM).format(dateVal);
		return strDate;
	}
	
	public static Date combineDateTime(Integer fieldReachIntDate, String fieldReachStrTime) {
		if(fieldReachIntDate!=null && fieldReachStrTime!=null) {
			Date retDate = Common.convertFieldreachDate(fieldReachIntDate);
			Date retTime = Common.convertFieldreachTime(Integer.valueOf(fieldReachStrTime), true);
			return Common.combineDateTime(retDate, retTime);
		}
		return null;
	}
	
	 public static String intToDateString(Integer date) {
	       
	        String strDate = null;
	       
	        try {
	           
	        strDate = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM, FacesContext.getCurrentInstance().getViewRoot().getLocale()).format(new SimpleDateFormat(DATE_FORMAT).parse(String.valueOf(date)));
	       
	        } catch (ParseException e) {
	            _logger.debug("unparseable date " + date);
	        }
	        return strDate;
	    }
	
}
