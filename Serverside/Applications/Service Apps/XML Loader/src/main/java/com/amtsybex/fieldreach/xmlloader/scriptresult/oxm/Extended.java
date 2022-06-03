/**
 * Project: FDE026
 * Author:	T Murray
 * Date:	05/08/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.xmlloader.scriptresult.oxm;

import java.util.HashMap;
import java.util.Map;


/**
 * NON JAXB class to hold the contents of the extended section of a script result.
 */
public class Extended {

	private Map<String, String> values;
	
	public Extended() {
		
		this.values = new HashMap<String, String>();
	}

	/**
	 * 
	 * @param name
	 * @param value
	 */
	public void setValue(String name, String value) {
		
		values.put(name, value);
	}
		
	/**
	 * 
	 * @param name
	 * @return
	 */
	public String getValue(String name) {
		
		return this.values.get(name);
	}
	
	
	/**
	 * FDP1293 - need to return this because we need to do a case insensitive search
	 * @return Map<String, String> 
	 */
	public Map<String, String> getValues(){
		return values;
	}
	
	
}
