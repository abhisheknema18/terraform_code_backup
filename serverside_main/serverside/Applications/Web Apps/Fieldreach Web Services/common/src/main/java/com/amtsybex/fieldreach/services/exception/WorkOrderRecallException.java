/**
 * Author:  T Murray
 * Date:    26/01/2015
 * Project: FDE029
 * 
 * Copyright AMT-Sybex 2015
 */
package com.amtsybex.fieldreach.services.exception;

public class WorkOrderRecallException extends Exception {
	
	private static final long serialVersionUID = -5021094126744864582L;

	public WorkOrderRecallException(String string) {
		
		super(string);
	}

	public WorkOrderRecallException() {
		
		super();
	}

}
