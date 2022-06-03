/**
 * Project: FDE026
 * Author:	T Murray
 * Date:	31/07/2014
 * 
 * Modified by T Murray
 * FDP1165
 * 20/11/2015
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.xmlloader.audit.message;

import java.util.Date;

/**
 * Class representing a message that is passed between XML loader components.
 * These messages will be used to update the audit database to log the process
 * of loading a script result file.
 */
public class AuditMessage {

	/*-------------------------------------------
	 - Member Variables
	 --------------------------------------------*/

	private String id;
	private String targetInstance;
	private String filename;
	private String fileUri;
	private String dispatcherDestination;
	private String errorText;
	private String workgroup;
	private String completeDate;
	private Date loaderStart;
	private Date LoaderEnd;

	/*-------------------------------------------
	 - Constructor Methods
	 --------------------------------------------*/

	public AuditMessage() {

		this.id = null;
		this.targetInstance = null;
		this.filename = null;
		this.fileUri = null;
		this.dispatcherDestination = null;
		this.errorText = null;
		this.workgroup = null;
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

	public String getTargetInstance() {

		return this.targetInstance;
	}

	public void setTargetInstance(String targetInstance) {

		this.targetInstance = targetInstance;
	}

	public String getFilename() {

		return this.filename;
	}

	public void setFilename(String filename) {

		this.filename = filename;
	}

	public String getFileUri() {

		return this.fileUri;
	}

	public void setFileUri(String fileUri) {

		this.fileUri = fileUri;
	}

	public String getDispatcherDestination() {

		return this.dispatcherDestination;
	}

	public void setDispatcherDestination(String dispatcherDestination) {

		this.dispatcherDestination = dispatcherDestination;
	}

	public String getWorkgroup() {

		return this.workgroup;
	}

	public void setWorkgroup(String workgroup) {

		this.workgroup = workgroup;
	}

	public String getErrorText() {

		return this.errorText;
	}

	public void setErrorText(String errorText) {

		this.errorText = errorText;
	}

	public Date getLoaderStart() {
		return loaderStart;
	}

	public void setLoaderStart(Date loaderStart) {
		this.loaderStart = loaderStart;
	}

	public Date getLoaderEnd() {
		return LoaderEnd;
	}

	public void setLoaderEnd(Date loaderEnd) {
		LoaderEnd = loaderEnd;
	}

	public String getCompleteDate() {
		return completeDate;
	}

	public void setCompleteDate(String completeDate) {
		this.completeDate = completeDate;
	}
	
}
