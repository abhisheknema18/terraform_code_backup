package com.amtsybex.fieldreach.fdm.details;

import java.io.Serializable;

import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.PrimeFaces;

import com.amtsybex.fieldreach.backend.model.ValFreeTextReq;
import com.amtsybex.fieldreach.fdm.web.jsf.util.MessageHelper;
import com.amtsybex.fieldreach.utils.impl.Common;

public class StringResultSet extends ResultSet implements Serializable {

	private String deltaResponse;

	private static final long serialVersionUID = -1765265511567934954L;

	public StringResultSet(Integer id, String script, String response, String freeText, Integer resOrderNo) {
		super(id, script, response, freeText, resOrderNo);
		this.deltaResponse = response;
	}

	public StringResultSet(int id, Integer scriptId, String script, int resOrderNo, Integer sequenceNumber,
			String itemType, String inputType) {
		super(id, scriptId, script, resOrderNo, sequenceNumber, itemType, inputType);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setEditPanelResponse() {
		if (this.isMandatorymodifierCheckBox()) {
			this.deltaResponse = "";
		} else {
			if(this.getResponse() != null && !this.getResponse().equalsIgnoreCase("N/A") && !this.getResponse().equalsIgnoreCase("O/R"))
			this.deltaResponse = this.getResponse();
			
			if(this.getExtendedResponse()!=null)
				this.deltaResponse = this.deltaResponse + this.getExtendedResponse();
		}
		
	}

	@Override
	public void setCurrentResponse() {
		
		if(this.deltaResponse.length() > 100) {
			
			this.setResponse(StringUtils.left(this.deltaResponse, 100));
			this.setExtendedResponse(StringUtils.right(this.deltaResponse, this.deltaResponse.length() - 100));
			
		}else {
			
			this.setResponse(this.deltaResponse);
			
		}
		
		
	}
	
	@Override
	public String generateSaveResponse() {
		return this.deltaResponse;
	}
	
	@Override
	public boolean isResponseValid() {
		// TODO validation should not return error messages from objects
		// have to update in upcoming sprints
		if (!this.isMandatorymodifierCheckBox()) {

			if ((this.getItemType().equals(Common.QUESTION_TYPE_SINGLE_PICK) || this.getItemType().equals(Common.QUESTION_TYPE_SINGLE_PICK_RULE)) && this.getValFreeTextReqList() != null) {

				for (ValFreeTextReq reqFreeTxt : this.getValFreeTextReqList()) {
					if (reqFreeTxt.getId().getResponse().equalsIgnoreCase(this.deltaResponse) && (this.getDeltaFreeText() == null || this.getDeltaFreeText().isEmpty())) {
						
						MessageHelper.setErrorMessage("messages", "Selected response needs free text");
						FacesContext.getCurrentInstance().validationFailed();
						PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
						return false;
					}
				}

			}
		}
		return true;

	}

	public String getDeltaResponse() {
		return deltaResponse;
	}

	public void setDeltaResponse(String deltaResponse) {
		this.deltaResponse = deltaResponse;
	}

}
