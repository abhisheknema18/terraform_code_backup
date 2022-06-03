/**
 * Project: FDE026
 * Author:	T Murray
 * Date:	29/10/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.xmlloader.exception.scriptresult;

/**
 * Exception handling for when a element cannot be found when parsing an XML document.
 */
public class ElementNotFoundException extends Exception {
	
	private static final long serialVersionUID = -516614383111346865L;

	public ElementNotFoundException(String ex) {

		super(ex);
	}
	
	public ElementNotFoundException() {

		super();
	}

}
