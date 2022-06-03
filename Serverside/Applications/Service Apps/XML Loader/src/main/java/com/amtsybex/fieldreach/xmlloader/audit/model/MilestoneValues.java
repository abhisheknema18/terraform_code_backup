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
 * Entity class representing the milestone_values table in the XML loader audit
 * database.
 */
public class MilestoneValues implements Serializable {

	/*-------------------------------------------
	 - Member Variables
	 --------------------------------------------*/

	private static final long serialVersionUID = -2527677807119148106L;

	private Integer id;

	private String milestone;

	/*-------------------------------------------
	 - Constructor Methods
	 --------------------------------------------*/

	public MilestoneValues() {

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

	public String getMilestone() {

		return milestone;
	}

	public void setMilestone(String milestone) {

		this.milestone = milestone;
	}

}
