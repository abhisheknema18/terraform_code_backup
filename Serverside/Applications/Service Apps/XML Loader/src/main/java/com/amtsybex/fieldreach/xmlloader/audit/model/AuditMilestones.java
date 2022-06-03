/**
 * Project: FDE026
 * Author:	T Murray
 * Date:	29/07/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.xmlloader.audit.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;

/**
 * Entity class representing the audit_milestones table in the XML loader audit
 * database.
 */
public class AuditMilestones implements Serializable {

	/*-------------------------------------------
	 - Member Variables
	 --------------------------------------------*/

	private static final long serialVersionUID = -5484317332745200474L;

	private String id;
	
	private int status;

	private int milestone;

	private Date milestoneDate;

	private Time milestoneTime;

	/*-------------------------------------------
	 - Constructor Methods
	 --------------------------------------------*/

	public AuditMilestones() {

	}

	/*-------------------------------------------
	 - Public Methods
	 --------------------------------------------*/

	public String getId() {

		return this.id;
	}

	public void setId(String id) {

		this.id = id;
	}
	
	public int getStatus() {

		return status;
	}

	public void setStatus(int status) {

		this.status = status;
	}

	public int getMilestone() {

		return milestone;
	}

	public void setMilestone(int milestone) {

		this.milestone = milestone;
	}

	public Date getMilestoneDate() {

		return milestoneDate;
	}

	public void setMilestoneDate(Date milestoneDate) {

		this.milestoneDate = milestoneDate;
	}

	public Time getMilestoneTime() {

		return milestoneTime;
	}

	public void setMilestoneTime(Time milestoneTime) {

		this.milestoneTime = milestoneTime;
	}

}
