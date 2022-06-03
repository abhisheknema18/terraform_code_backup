package com.amtsybex.fieldreach.fdm.details;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.faces.context.FacesContext;

import org.primefaces.PrimeFaces;

import com.amtsybex.fieldreach.fdm.web.jsf.util.MessageHelper;
import com.amtsybex.fieldreach.utils.impl.Common;

public class NumericResultSet extends ResultSet implements Serializable {

	// 38437- KN -String Response in edit panel
	private Integer deltaResponseInt;
	private BigDecimal  deltaResponseDes;
	
	//To set Lower and upper limits/warning
	private BigDecimal inputLowerLimit;
	private BigDecimal inputUpperLimit;
	private BigDecimal inputLowerWarn;
	private BigDecimal inputUpperWarn;

	private static final long serialVersionUID = -1765265511567934954L;

	public NumericResultSet(Integer id, String script, String response, String freeText, Integer resOrderNo) {
		super(id, script, response, freeText, resOrderNo);
	}

	public NumericResultSet(int id, Integer scriptId, String script, int resOrderNo, Integer sequenceNumber,
			String itemType, String inputType) {
		super(id, scriptId, script, resOrderNo, sequenceNumber, itemType, inputType);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setEditPanelResponse() {
		if (this.isMandatorymodifierCheckBox()) {
			this.setDeltaResponseInt(null);
			this.setDeltaResponseDes(null);

		} else {
			try {
				if (this.getItemType().equals(Common.QUESTION_TYPE_NUMERIC)) {
					
					this.deltaResponseInt = Integer.parseInt(this.getResponse());
				} else {
					
					this.setDeltaResponseDes(this.getResponse() != null ? new BigDecimal(this.getResponse()) : null);
				}
			} catch (NumberFormatException e) {
				this.deltaResponseInt = null;
				this.deltaResponseDes = null;
			}
		}
		
	}
	
	@Override
	public void setCurrentResponse() {
		if (this.getItemType().equals(Common.QUESTION_TYPE_NUMERIC)) {
			 this.setResponse(this.deltaResponseInt != null ? this.deltaResponseInt.toString() : null);
		}else {
			this.setResponse(this.deltaResponseDes!= null ? this.deltaResponseDes.toString() : null);
		}
		
	}
	
	@Override
	public String generateSaveResponse() {
		if (this.getItemType().equals(Common.QUESTION_TYPE_NUMERIC)) {
			 return this.deltaResponseInt != null ? this.deltaResponseInt.toString() : null;
		}else {
			 return this.deltaResponseDes != null ? this.deltaResponseDes.toString() : null;
		}
	}
	
	
	@Override
	public boolean isResponseValid() {
		// TODO validation should not return error messages from objects
		// have to update in upcoming sprints
		if (!this.isMandatorymodifierCheckBox()) {

			if (this.getItemType().equals(Common.QUESTION_TYPE_NUMERIC)) {
				if (this.deltaResponseInt != null) {
					if (this.inputLowerLimit != null && this.inputLowerLimit.intValue() > this.deltaResponseInt) {
						// always check Limit first then warning

						MessageHelper.setErrorMessage("messages", "Please enter Value above " + this.inputLowerLimit);
						FacesContext.getCurrentInstance().validationFailed();
						PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
						return false;

					}

					if (this.inputUpperLimit != null && this.inputUpperLimit.intValue() < this.deltaResponseInt) {

						MessageHelper.setErrorMessage("messages", "Please enter Value below " + this.inputUpperLimit);
						FacesContext.getCurrentInstance().validationFailed();
						PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
						return false;

					}

					if ((this.inputLowerWarn != null && this.inputLowerWarn.intValue() > this.deltaResponseInt)
							|| (this.inputUpperWarn != null && this.inputUpperWarn.intValue() < this.deltaResponseInt)) {

						PrimeFaces.current().executeScript("PF('confirmSaveDlgVar').show();");
						FacesContext.getCurrentInstance().validationFailed();
						return false;
					}

				}
			} else {

				if (this.deltaResponseDes != null) {
					int decimalprecision = this.getPrecision() != null ? this.getPrecision() : 1;
					
					//Changing decimal places round up value as mentioned in FS
					
					this.deltaResponseDes = this.deltaResponseDes.setScale(decimalprecision, RoundingMode.HALF_UP).stripTrailingZeros();
					
					//previously used to throw validation error for decimal places
						/*StringBuilder sb = new StringBuilder(
								this.deltaResponseDes.remainder(BigDecimal.ONE).toString());
						sb.delete(0, 2);
						
						if (sb.toString().length() > decimalprecision) {
							MessageHelper.setErrorMessage("messages",
									"Decimal value should be max " + decimalprecision);
							FacesContext.getCurrentInstance().validationFailed();
							PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
							return false;
						}*/

					
					
					if (this.inputLowerLimit != null && this.deltaResponseDes.compareTo(this.inputLowerLimit) != 1 && this.deltaResponseDes.compareTo(this.inputLowerLimit) != 0) {
						// always check Limit first then warning
						MessageHelper.setErrorMessage("messages", "Please enter Value above " + this.inputLowerLimit);
						FacesContext.getCurrentInstance().validationFailed();
						PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
						return false;

					}

					if (this.inputUpperLimit != null && this.deltaResponseDes.compareTo(this.inputUpperLimit) != -1 && this.deltaResponseDes.compareTo(this.inputUpperLimit) != 0) {

						// always check Limit first then warning
						MessageHelper.setErrorMessage("messages", "Please enter Value below " + this.inputUpperLimit);
						FacesContext.getCurrentInstance().validationFailed();
						PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
						return false;

					}

					if ((this.inputLowerWarn != null && this.deltaResponseDes.compareTo(this.inputLowerWarn) != 1)
							|| (this.inputUpperWarn != null && this.deltaResponseDes.compareTo(this.inputUpperWarn) != -1)) {

						PrimeFaces.current().executeScript("PF('confirmSaveDlgVar').show();");
						FacesContext.getCurrentInstance().validationFailed();
						return false;
					}
				}
			}
		}
		
		return true;
	}

	public Integer getDeltaResponseInt() {
		return deltaResponseInt;
	}

	public void setDeltaResponseInt(Integer deltaResponseInt) {
		this.deltaResponseInt = deltaResponseInt;
	}

	public BigDecimal getDeltaResponseDes() {
		return deltaResponseDes;
	}

	public void setDeltaResponseDes(BigDecimal deltaResponseDes) {
		this.deltaResponseDes = deltaResponseDes;
	}

	public BigDecimal getInputLowerLimit() {
		return inputLowerLimit;
	}

	public void setInputLowerLimit(BigDecimal inputLowerLimit) {
		this.inputLowerLimit = inputLowerLimit;
	}

	public BigDecimal getInputUpperLimit() {
		return inputUpperLimit;
	}

	public void setInputUpperLimit(BigDecimal inputUpperLimit) {
		this.inputUpperLimit = inputUpperLimit;
	}

	public BigDecimal getInputLowerWarn() {
		return inputLowerWarn;
	}

	public void setInputLowerWarn(BigDecimal inputLowerWarn) {
		this.inputLowerWarn = inputLowerWarn;
	}

	public BigDecimal getInputUpperWarn() {
		return inputUpperWarn;
	}

	public void setInputUpperWarn(BigDecimal inputUpperWarn) {
		this.inputUpperWarn = inputUpperWarn;
	}

}