/**
 * Author:  T Murray
 * Date:    27/02/2015
 * Project: FDE029
 * 
 * Copyright AMT-Sybex 2015
 */
package com.amtsybex.fieldreach.services.exception;

public class ChecksumMismatchException extends Exception {

	private static final long serialVersionUID = 6631455203489879900L;

	public ChecksumMismatchException(String string) {
		
		super(string);
	}

	public ChecksumMismatchException() {
		
		super();
	}

}
