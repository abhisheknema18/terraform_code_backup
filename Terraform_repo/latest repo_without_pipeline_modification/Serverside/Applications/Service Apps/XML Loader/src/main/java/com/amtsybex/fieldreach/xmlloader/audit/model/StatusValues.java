/**
 * Project: FDE026
 * Author:	T Murray
 * Date:	28/07/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.xmlloader.audit.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity class representing the status_values table in the XML loader audit
 * database.
 */
@Entity
@Table(name = "status_values")
public class StatusValues implements Serializable {

	/*-------------------------------------------
	 - Member Variables
	 --------------------------------------------*/

	private static final long serialVersionUID = 2655889217353116528L;

	@Id
	@Column(name = "id")
	private Integer id;

	@Column(name = "status")
	private String status;

	/*-------------------------------------------
	 - Constructor Methods
	 --------------------------------------------*/

	public StatusValues() {

	}

	/*-------------------------------------------
	 - Public Methods
	 --------------------------------------------*/

	public Integer getId() {

		return id;
	}

	public void setId(Integer id) {

		this.id = id;
	}

	public String getStatus() {

		return status;
	}

	public void setStatus(String status) {

		this.status = status;
	}

}
