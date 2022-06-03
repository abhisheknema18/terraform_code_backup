/**
 * Project: FDE026
 * Author:	T Murray
 * Date:	30/07/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.xmlloader.exception.scriptresult;

/**
 * Exception handling for when an error occurs validating a file.
 */
public class ValidationException extends Throwable {

	private static final long serialVersionUID = -1278990786810575375L;

	public ValidationException(String ex) {

		super(ex);
	}

	public ValidationException(Exception ex) {

		super(ex);
	}

	public ValidationException() {

		super();
	}

}
