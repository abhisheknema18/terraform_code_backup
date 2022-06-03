package com.amtsybex.fieldreach.services.messages.request;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * FDE051
 * @author CroninM
 *
 */
@XStreamAlias("WorkOrder")
public class WorkOrderStatusRequest implements Serializable {

	private static final long serialVersionUID = -4619498736209112622L;
	
    public static final String APPLICATION_VND_FIELDSMART_WORKORDERSTATUS_1_JSON = "application/vnd.fieldsmart.work-order-status-request-1+json";
    public static final String APPLICATION_VND_FIELDSMART_WORKORDERSTATUS_1_XML = "application/vnd.fieldsmart.work-order-status-request-1+xml";
	
	private String WorkStatusAdditionalText;
	
	public WorkOrderStatusRequest() {
		
	}

	public String getWorkStatusAdditionalText() {
		return WorkStatusAdditionalText;
	}

	public void setWorkStatusAdditionalText(String workStatusAdditionalText) {
		WorkStatusAdditionalText = workStatusAdditionalText;
	}


}
