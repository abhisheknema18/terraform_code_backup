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
 * Entity class representing the extract_errors table in the 
 * Extract Adapter audit database.
 */
public class ExtractErrors implements Serializable {

	/*-------------------------------------------
	 - Member Variables
	 --------------------------------------------*/

	private static final long serialVersionUID = 6499088148790426948L;

	private String id;
	
	private String errorDetail;


	/*-------------------------------------------
	 - Constructor Methods
	 --------------------------------------------*/

	public ExtractErrors() {

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
	
	public String getErrorDetail() {

		return errorDetail;
	}

	public void setErrorDetail(String errorDetail) {

		this.errorDetail = errorDetail;
	}

}
