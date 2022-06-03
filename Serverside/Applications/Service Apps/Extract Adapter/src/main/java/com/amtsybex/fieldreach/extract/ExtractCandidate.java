/**
 * Project: FDE030
 * Author:	T Murray
 * Date:	23/12/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.extract;

/**
 * Class that stores information on script results that are candidates for
 * extraction. As the extraction process progresses this class is also used to
 * store additional information.
 */
public class ExtractCandidate {

	private String instance;
	private int returnId;
	private int scriptCodeId;
	private String statusValue;
	private int eventId;

	// Id of audit record created in the embedded database to track the progress
	// of the script result an instance of this class represents.
	private String auditId;

	// XML extracted from the Fieldreach database for this extraction candidate.
	private String xml;

	private boolean isRedelivery;
	
	/*-------------------------------------------
	 - Constructor Methods
	 --------------------------------------------*/

	/**
	 * @param instance
	 *            Fieldreach instance the extraction candidate is associated
	 *            with.
	 * 
	 * @param returnId
	 *            ReturnId of the script result that is a candidate for
	 *            extraction.
	 * 
	 * @param scriptCodeId
	 *            ScriptCodeId of the script result that is a candidate for
	 *            extraction.
	 * 
	 * @param statusValue
	 *            The status of the script result that is a candidate for
	 *            extractions. This is the status that triggers the extraction.
	 * 
	 * @param eventId
	 *            The EventId that will be used to build the extract message
	 *            during the extraction process.
	 */
	public ExtractCandidate(String instance, int returnId, int scriptCodeId,
			String statusValue, int eventId) {

		this.instance = instance;
		this.returnId = returnId;
		this.scriptCodeId = scriptCodeId;
		this.statusValue = statusValue;
		this.eventId = eventId;

		this.auditId = null;
		this.xml = null;
		this.setRedelivery(false);
	}

	public ExtractCandidate() {
		
		this.instance = null;
		this.returnId = -1;
		this.scriptCodeId = -1;
		this.statusValue = null;
		this.eventId = -1;

		this.auditId = null;
		this.xml = null;
		this.setRedelivery(false);
	}
	
	/*-------------------------------------------
	 - Public Methods
	 --------------------------------------------*/

	public int getReturnId() {

		return this.returnId;
	}

	public void setReturnId(int returnId) {

		this.returnId = returnId;
	}

	public int getScriptCodeId() {

		return this.scriptCodeId;
	}

	public void setScriptCodeId(int scriptCodeId) {

		this.scriptCodeId = scriptCodeId;
	}

	public String getStatusValue() {

		return this.statusValue;
	}

	public void setStatusValue(String statusValue) {

		this.statusValue = statusValue;
	}

	public int getEventId() {

		return this.eventId;
	}

	public void setEventId(int eventId) {

		this.eventId = eventId;
	}

	public String getAuditId() {

		return auditId;
	}

	public void setAuditId(String auditId) {

		this.auditId = auditId;
	}

	public String getInstance() {

		return instance;
	}

	public void setInstance(String instance) {

		this.instance = instance;
	}

	public String getXml() {

		return xml;
	}

	public void setXml(String xml) {

		this.xml = xml;
	}

	public boolean isRedelivery() {
		
		return isRedelivery;
	}

	public void setRedelivery(boolean isRedelivery) {
		
		this.isRedelivery = isRedelivery;
	}

}
