package com.amtsybex.fieldreach.fdm.web.jsf.util;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * helper class for accessing values in the properties file
 */
public class Properties {

	private static ResourceBundle properties = ResourceBundle.getBundle("com.amtsybex.fdm.web.jsf.messages.FdmResources");  
	
	public static String get(String property){
		return properties.getString(property);
	}
	
	public static String get(String property, Object[] args){
		return MessageFormat.format(properties.getString(property), args);
	}
}
