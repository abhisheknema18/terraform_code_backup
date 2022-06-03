package com.amtsybex.fieldreach.fdm.details;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.context.FacesContext;

import org.primefaces.PrimeFaces;

import com.amtsybex.fieldreach.fdm.util.DateUtil;
import com.amtsybex.fieldreach.fdm.web.jsf.util.MessageHelper;
import com.amtsybex.fieldreach.utils.impl.Common;

public class DateResultSet extends ResultSet implements Serializable {

	// 38437- KN -String Response in edit panel
	private Date deltaResponse;

	// To set Lower and upper limits/warning for Date
	private Date dateLowerLimit;
	private Date dateUpperLimit;
	private Date dateLowerWarn;
	private Date dateUpperWarn;

	private static final long serialVersionUID = -1765265511567934954L;

	public DateResultSet(Integer id, String script, String response, String freeText, Integer resOrderNo) {
		super(id, script, response, freeText, resOrderNo);
		// this.setDeltaResponse(response);
	}

	public DateResultSet(int id, String itemText, Date stringToDate, String freeText, Integer resOrderNo) {
		super(id, itemText, stringToDate, freeText, resOrderNo);
		// this.deltaResponse = response;
	}

	public DateResultSet(int id, Integer scriptId, String script, int resOrderNo, Integer sequenceNumber,
			String itemType, String inputType) {
		super(id, scriptId, script, resOrderNo, sequenceNumber, itemType, inputType);
		
		if (this.getItemType().equals(Common.QUESTION_TYPE_DATE)) {
			this.setDate(true);
		}
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setEditPanelResponse() {
		if (this.isMandatorymodifierCheckBox()) {
			this.setDeltaResponse(null);
		} else {
			try {
				if (this.getResponse() != null && (!this.getResponse().equalsIgnoreCase("N/A") && !this.getResponse().equalsIgnoreCase("O/R"))) {
					
					if (this.getItemType().equals(Common.QUESTION_TYPE_DATE)) {
						this.deltaResponse = this.getDateValue();
					} else {
						
						String strTime = this.getResponse();
						DateFormat dateFormat = new SimpleDateFormat("HH:mm");
						this.deltaResponse = dateFormat.parse(strTime);
					}
				}
			} catch (ParseException e) {
				this.setDeltaResponse(null);
			}
		}
		
	}

	@Override
	public void setCurrentResponse() {
		if (this.getItemType().equals(Common.QUESTION_TYPE_DATE)) {

			this.setDateValue(this.deltaResponse);
		} else {

			this.setResponse(DateUtil.parseTime(DateUtil.formatTime(this.deltaResponse)));
		}
		
	}
	
	@Override
	public String generateSaveResponse() {
		
		if (this.getItemType().equals(Common.QUESTION_TYPE_DATE)) {
			return DateUtil.formatDate(this.deltaResponse);
		} else {
			return DateUtil.formatTime(this.deltaResponse);
		}
	}
	
	@Override
	public boolean isResponseValid() {
		// TODO validation should not return error messages from objects
		// have to update in upcoming sprints
		if (!this.isMandatorymodifierCheckBox() && this.deltaResponse != null) {
			
			DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
			
			if (this.dateLowerLimit != null && this.deltaResponse.compareTo(this.dateLowerLimit) < 0) {

				MessageHelper.setErrorMessage("messages", "Please select date after "+df.format(this.dateLowerLimit));
				FacesContext.getCurrentInstance().validationFailed();
				PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
				return false;
			}
			if (this.dateUpperLimit != null && this.deltaResponse.compareTo(this.dateUpperLimit) > 0) {

				MessageHelper.setErrorMessage("messages", "Please select date before "+df.format(this.dateUpperLimit));
				FacesContext.getCurrentInstance().validationFailed();
				PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
				return false;
			}

			if (this.dateLowerWarn != null && this.deltaResponse.compareTo(this.dateLowerWarn) > 0) {

				PrimeFaces.current().executeScript("PF('confirmSaveDlgVar').show();");
				FacesContext.getCurrentInstance().validationFailed();
				return false;
			}
			if (this.dateUpperWarn != null && this.deltaResponse.compareTo(this.dateUpperWarn) < 0) {

				PrimeFaces.current().executeScript("PF('confirmSaveDlgVar').show();");
				FacesContext.getCurrentInstance().validationFailed();
				return false;
			}
		
		}
		return true;
		
	}
	
	public Date getDeltaResponse() {
		return deltaResponse;
	}

	public void setDeltaResponse(Date deltaResponse) {
		this.deltaResponse = deltaResponse;
	}

	public Date getDateLowerLimit() {
		return dateLowerLimit;
	}

	public void setDateLowerLimit(Date dateLowerLimit) {
		this.dateLowerLimit = dateLowerLimit;
	}

	public Date getDateUpperLimit() {
		return dateUpperLimit;
	}

	public void setDateUpperLimit(Date dateUpperLimit) {
		this.dateUpperLimit = dateUpperLimit;
	}

	public Date getDateLowerWarn() {
		return dateLowerWarn;
	}

	public void setDateLowerWarn(Date dateLowerWarn) {
		this.dateLowerWarn = dateLowerWarn;
	}

	public Date getDateUpperWarn() {
		return dateUpperWarn;
	}

	public void setDateUpperWarn(Date dateUpperWarn) {
		this.dateUpperWarn = dateUpperWarn;
	}

}
