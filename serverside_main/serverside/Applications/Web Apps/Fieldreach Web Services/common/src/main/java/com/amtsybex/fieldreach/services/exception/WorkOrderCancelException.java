/**
 * Author:  T Murray
 * Date:    26/01/2015
 * Project: FDE029
 * 
 * Copyright AMT-Sybex 2015
 */
package com.amtsybex.fieldreach.services.exception;

public class WorkOrderCancelException extends Exception {
	
	private static final long serialVersionUID = 636147718792001118L;

	public WorkOrderCancelException(String string) {
		
		super(string);
	}

	public WorkOrderCancelException() {
		
		super();
	}

}
