/**
 * Project: FDE026
 * Author:	T Murray
 * Date:	28/07/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.xmlloader.audit.model;

import java.io.Serializable;

/**
 * Entity class representing the file_errors table in the XML loader audit
 * database.
 */
public class FileErrors implements Serializable {

	/*-------------------------------------------
	 - Member Variables
	 --------------------------------------------*/

	private static final long serialVersionUID = -5056898483438148088L;

	private String id;
	
	private String errorDetail;


	/*-------------------------------------------
	 - Constructor Methods
	 --------------------------------------------*/

	public FileErrors() {

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
