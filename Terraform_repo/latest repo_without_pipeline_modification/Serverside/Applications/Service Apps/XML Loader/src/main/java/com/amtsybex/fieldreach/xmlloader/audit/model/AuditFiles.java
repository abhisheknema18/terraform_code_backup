/**
 * Project: FDE026
 * Author:	T Murray
 * Date:	29/07/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.xmlloader.audit.model;

import java.io.Serializable;

/**
 * Entity class representing the audit_files table in the XML loader audit
 * database.
 */
public class AuditFiles implements Serializable {

	/*-------------------------------------------
	 - Member Variables
	 --------------------------------------------*/

	private static final long serialVersionUID = -3988619741334847469L;

	private String id;

	private String filename;

	private String fileUri;
	
	private String targetInstance;

	private String workgroup;
	
	private boolean dispatched;

	private AuditMilestones milestone;

	private FileErrors fileError;

	/*-------------------------------------------
	 - Constructor Methods
	 --------------------------------------------*/

	public AuditFiles() {

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

	public String getFilename() {

		return filename;
	}

	public void setFilename(String filename) {

		this.filename = filename;
	}

	public String getTargetInstance() {

		return targetInstance;
	}

	public void setTargetInstance(String targetInstance) {

		this.targetInstance = targetInstance;
	}

	public String getWorkgroup() {

		return workgroup;
	}

	public void setWorkgroup(String workgroup) {

		this.workgroup = workgroup;
	}
	
	public boolean isDispatched() {

		return dispatched;
	}

	public void setDispatched(boolean dispatched) {

		this.dispatched = dispatched;
	}

	public AuditMilestones getMilestone() {

		return milestone;
	}

	public void setMilestone(AuditMilestones milestone) {

		this.milestone = milestone;
	}

	public FileErrors getFileError() {

		return fileError;
	}

	public void setFileError(FileErrors fileError) {

		this.fileError = fileError;
	}
	
	public String getFileUri() {
		
		return fileUri;
	}

	public void setFileUri(String fileUri) {
		
		this.fileUri = fileUri;
	}
	
}
