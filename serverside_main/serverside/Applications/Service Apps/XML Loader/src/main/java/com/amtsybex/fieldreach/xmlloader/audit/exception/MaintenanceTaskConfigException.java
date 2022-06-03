/**
 * Project: FDE026
 * Author:	T Murray
 * Date:	30/07/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.xmlloader.audit.exception;

/**
 * Exception handling for when an error occurs with configuration
 * of an audit database maintenance task.
 */
public class MaintenanceTaskConfigException extends Throwable {

	private static final long serialVersionUID = -894793890577374947L;

	public MaintenanceTaskConfigException(String ex) {

		super(ex);
	}

	public MaintenanceTaskConfigException(Exception ex) {

		super(ex);
	}

	public MaintenanceTaskConfigException() {

		super();
	}

}
