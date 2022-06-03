/**
 * Author:  T Murray
 * Date:    26/01/2015
 * Project: FDE029
 * 
 * Copyright AMT-Sybex 2015
 */

package com.amtsybex.fieldreach.services.exception;

public class WorkOrderDownloadException extends Exception {
	
	private static final long serialVersionUID = -7868051213946735537L;

	public WorkOrderDownloadException(String string) {
		super(string);
	}

	public WorkOrderDownloadException() {
		super();
	}

}
