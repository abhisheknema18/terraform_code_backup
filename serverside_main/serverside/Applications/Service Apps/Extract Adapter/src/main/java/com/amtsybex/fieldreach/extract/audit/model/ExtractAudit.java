/**
 * Project: FDE030
 * Author:	T Murray
 * Date:	18/12/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.extract.audit.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;

/**
 * Entity class representing the extract_audit table in the 
 * Extract Adapter audit database.
 */
public class ExtractAudit implements Serializable {

	/*-------------------------------------------
	 - Member Variables
	 --------------------------------------------*/

	private static final long serialVersionUID = 8593254152785834334L;

	private String id;

	private Integer returnId;
		
	private String instance;
	
	private boolean extracted;
	
	private Date auditDate;
	
	private Time auditTime;

	private Redelivery redelivery;
	
	private ExtractErrors error;
	
	
	/*-------------------------------------------
	 - Constructor Methods
	 --------------------------------------------*/

	public ExtractAudit() {

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
	
	public Integer getReturnId() {
		
		return returnId;
	}

	public void setReturnId(Integer returnId) {
		
		this.returnId = returnId;
	}
	
	public String getInstance() {
		
		return instance;
	}

	public void setInstance(String instance) {
		
		this.instance = instance;
	}

	public boolean isExtracted() {
		
		return extracted;
	}

	public void setExtracted(boolean extracted) {
		
		this.extracted = extracted;
	}

	public Date getAuditDate() {
		
		return auditDate;
	}

	public void setAuditDate(Date auditDate) {
		
		this.auditDate = auditDate;
	}

	public Time getAuditTime() {
		
		return auditTime;
	}

	public void setAuditTime(Time auditTime) {
		
		this.auditTime = auditTime;
	}

	public Redelivery getRedelivery() {
		
		return redelivery;
	}

	public void setRedelivery(Redelivery redelivery) {
		
		this.redelivery = redelivery;
	}

	public ExtractErrors getError() {
		
		return error;
	}

	public void setError(ExtractErrors error) {
		
		this.error = error;
	}

}
