package com.amtsybex.fieldreach.fdm.details;

import java.io.Serializable;
import java.sql.Blob;

import javax.faces.context.FacesContext;

import org.apache.commons.codec.binary.Base64;
import org.primefaces.PrimeFaces;

import com.amtsybex.fieldreach.backend.model.ScriptResultBlb;
import com.amtsybex.fieldreach.fdm.web.jsf.util.MessageHelper;
import com.amtsybex.fieldreach.utils.impl.Common;

public class PhotographResultSet extends ResultSet implements Serializable {
	
	private static final long serialVersionUID = -7583161406403968143L;
	private String deltaResponse;
	private Blob deltaBlob;

	public PhotographResultSet(Integer id, String script, ScriptResultBlb blob, String freeText, Integer resOrderNo,
			Integer returnId) {
		super(id, script, blob, freeText, resOrderNo, returnId);
		
	}

	public PhotographResultSet(int id, Integer scriptId, String script, int resOrderNo, Integer sequenceNumber,
			String itemType, String inputType) {
		super(id, scriptId, script, resOrderNo, sequenceNumber, itemType, inputType);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setEditPanelResponse() {
		if (this.isMandatorymodifierCheckBox()) {
			this.deltaResponse = "";
		} else {
			if(this.getResponseType() != null && this.getResponseType().equals("OK")){
				this.deltaResponse = this.getBase64Photograph();
			}else if(this.getResponseType() == null) {
				//for unanswered questions
				this.deltaResponse = "";
			}
		}
		
	}
	
	@Override
	public void setCurrentResponse() {
			this.setBlob(blobToByteArray(this.deltaBlob));
			this.setBase64Photograph(this.deltaResponse);
	}
	
	@Override
	public String generateSaveResponse() {
		return this.deltaResponse.replaceFirst(Common.BASE_64_JPEG_PREFIX, "");
	}
	
	@Override
	public boolean isResponseValid() {
		// TODO validation should not return error messages from objects
		// have to update in upcoming sprints
		if (!this.isMandatorymodifierCheckBox()) {
			if(this.deltaResponse == null || this.deltaResponse.equalsIgnoreCase("")){
				MessageHelper.setErrorMessage("messages", "You cannot add an empty response for this question.");
				FacesContext.getCurrentInstance().validationFailed();
				PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
				return false;
			}
		}
		return true;

	}
	
	public void updateNewPhotgraph(Blob blob) {
		this.deltaBlob = blob;
		if (this.deltaBlob != null) {
			byte[] blobByteArray = blobToByteArray(this.deltaBlob);
			if (blobByteArray != null) {
				this.deltaResponse = Common.BASE_64_JPEG_PREFIX + Base64.encodeBase64String(blobByteArray);
			}
		}
	}

	public String getDeltaResponse() {
		return deltaResponse;
	}

	public void setDeltaResponse(String deltaResponse) {
		this.deltaResponse = deltaResponse;
	}

	public Blob getDeltaBlob() {
		return deltaBlob;
	}

	public void setDeltaBlob(Blob deltaBlob) {
		this.deltaBlob = deltaBlob;
	}

}
