/**
 * Project: FDE030
 * Author:	T Murray
 * Date:	18/12/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.extract.audit.model;

import java.io.Serializable;

/**
 * Entity class representing the redelivery table in the 
 * Extract Adapter audit database.
 */
public class Redelivery implements Serializable {

	/*-------------------------------------------
	 - Member Variables
	 --------------------------------------------*/

	private static final long serialVersionUID = -3185817711328324026L;

	private String id;
	
	private Integer attempts;
	
	private String message;


	/*-------------------------------------------
	 - Constructor Methods
	 --------------------------------------------*/

	public Redelivery() {

	}

	/*-------------------------------------------
	 - Public Methods
	 --------------------------------------------*/

	public String getId() {

		return id;
	}

	public void setId(String id) {

		this.id = id;
	}
	
	public Integer getAttempts() {

		return attempts;
	}

	public void setAttempts(Integer attempts) {

		this.attempts = attempts;
	}

	public String getMessage() {
		
		return message;
	}

	public void setMessage(String message) {
		
		this.message = message;
	}

}
