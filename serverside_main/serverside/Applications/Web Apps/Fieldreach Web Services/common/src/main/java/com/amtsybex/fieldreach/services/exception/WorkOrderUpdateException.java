/**
 * Author:  T Murray
 * Date:    26/01/2015
 * Project: FDE029
 * 
 * Copyright AMT-Sybex 2015
 */
package com.amtsybex.fieldreach.services.exception;

public class WorkOrderUpdateException extends Exception {

	private static final long serialVersionUID = -4310020487815699558L;

	public WorkOrderUpdateException(String string) {
		
		super(string);
	}

	public WorkOrderUpdateException() {
		
		super();
	}

}
