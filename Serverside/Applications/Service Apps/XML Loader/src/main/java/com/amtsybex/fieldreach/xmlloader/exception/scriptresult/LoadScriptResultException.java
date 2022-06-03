/**
 * Project: FDE026
 * Author:	T Murray
 * Date:	15/08/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.xmlloader.exception.scriptresult;

/**
 * Exception handling for when an error occurs loading a script result
 * file into the Fieldreach database.
 */
public class LoadScriptResultException extends Throwable {

	private static final long serialVersionUID = -8809062822334921191L;

	public LoadScriptResultException(String ex) {

		super(ex);
	}

	public LoadScriptResultException(Exception ex) {

		super(ex);
	}

	public LoadScriptResultException() {

		super();
	}

}
