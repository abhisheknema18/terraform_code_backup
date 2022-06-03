package com.amtsybex.fieldreach.services.exception;

/**
 * FDE051 - MC
 * @author CroninM
 *
 */
public class WorkOrderCloseException extends Exception {

	private static final long serialVersionUID = -8349264592323448544L;

	public WorkOrderCloseException(String string) {
		
		super(string);
	}

	public WorkOrderCloseException() {
		
		super();
	}
}
