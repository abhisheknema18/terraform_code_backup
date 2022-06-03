/**
 * Author:  T Murray
 * Date:    29/04/2016
 * Project: FDP1170
 * 
 * Copyright AMT-Sybex 2016
 */
package com.amtsybex.fieldreach.services.exception;

public class BadRequestException extends Exception {

	private static final long serialVersionUID = -6740447813524714304L;

	public BadRequestException(String string) {
		super(string);
	}

	public BadRequestException() {
		super();
	}
}