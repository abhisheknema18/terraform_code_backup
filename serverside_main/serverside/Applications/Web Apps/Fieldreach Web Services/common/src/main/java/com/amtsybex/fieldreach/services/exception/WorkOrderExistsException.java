/**
 * Author:  T Murray
 * Date:    26/01/2015
 * Project: FDE029
 * 
 * Copyright AMT-Sybex 2015
 */

package com.amtsybex.fieldreach.services.exception;

public class WorkOrderExistsException extends Exception {

	private static final long serialVersionUID = -6608588922264021053L;

	public WorkOrderExistsException(String string) {
		
		super(string);
	}

	public WorkOrderExistsException() {
		
		super();
	}

}
