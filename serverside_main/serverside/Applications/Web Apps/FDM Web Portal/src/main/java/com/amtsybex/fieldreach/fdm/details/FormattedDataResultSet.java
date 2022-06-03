package com.amtsybex.fieldreach.fdm.details;

import java.io.Serializable;

import javax.faces.context.FacesContext;

import org.primefaces.PrimeFaces;

import com.amtsybex.fieldreach.backend.model.FormatInputDef;
import com.amtsybex.fieldreach.fdm.web.jsf.util.MessageHelper;

public class FormattedDataResultSet extends ResultSet implements Serializable {

	private String[] deltaResponse;

	private int mandateFormatFields;

	private static final long serialVersionUID = -1765265511567934954L;

	public FormattedDataResultSet(Integer id, String script, String response, String freeText, Integer resOrderNo) {
		super(id, script, response, freeText, resOrderNo);
		//this.deltaResponse = response;
	}

	public FormattedDataResultSet(int id, Integer scriptId, String script, int resOrderNo, Integer sequenceNumber,
			String itemType, String inputType) {
		super(id, scriptId, script, resOrderNo, sequenceNumber, itemType, inputType);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setEditPanelResponse() {
	//if any partially answered is there
		boolean outOfTempSize = false;
		if (this.isMandatorymodifierCheckBox()) {
			// Empty response for NA and OR
			this.deltaResponse = new String[this.getFormatInputDefList().size()];

		} else {
			
			String[] temp;
			if (this.getResponse() != null && (!this.getResponse().equalsIgnoreCase("N/A") && !this.getResponse().equalsIgnoreCase("O/R"))) {
				temp = this.getResponse().split("");
				
			} else {
				temp = new String[this.getFormatInputDefList().size()];
			}

			deltaResponse = new String[this.getFormatInputDefList().size()];

			for (int i = 0; i < this.getFormatInputDefList().size(); i++) {
				if( i >= temp.length)
					outOfTempSize = true;

				if (this.getFormatInputDefList().get(i).getCharType().equals("SC")) {
					
					//setting default value which is blocked in Front end
					deltaResponse[i] = this.getFormatInputDefList().get(i).getAllowedChars();

				} else {  
					
					if (!outOfTempSize ) {
						
							deltaResponse[i] = temp[i];
		
					} else {
					//setting value for the Input which are not answered previously
						deltaResponse[i] = "";
					}
				}
			}

			// setting default size as mandatory
			setMandateFormatFields(this.getFormatInputDefList().size());
			if (this.getFormatInputDefMe() != null) {
				// replacing with the mandatory inputdef
				setMandateFormatFields(this.getFormatInputDefMe().getMinEntry());
			}

		}
	}
	
	@Override
	public void setCurrentResponse() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < deltaResponse.length; i++) {
			if(deltaResponse[i]!=null) {
				sb.append(deltaResponse[i]);
	    		 
	    	  }else {
	    		  sb.append("");
	    	  }
		}
		this.setResponse(sb.toString());
		
	}
	
	@Override
	public String generateSaveResponse() {
		StringBuffer sb = new StringBuffer();
	      for(int i = 0; i < deltaResponse.length; i++) {
	    	  if(deltaResponse[i]!=null) {
	    		  sb.append(deltaResponse[i]);
	    		  
	    	  }else {
	    		  sb.append("");
	    	  }
	      }
	      return sb.toString();
	}
	
	@Override
	public boolean isResponseValid() {
		// TODO validation should not return error messages from objects
		// have to update in upcoming sprints

		boolean startValidate = false;
		if (!this.isMandatorymodifierCheckBox() && this.deltaResponse != null) {

			for (int i = this.deltaResponse.length-1; i > 0; i--) {
				
				if(this.mandateFormatFields-1 == i) {
					//start validating if defined mandatory field reached
					startValidate = true;
				}

				if (!startValidate) {
					if (this.deltaResponse[i] != null && this.deltaResponse[i] != "" && this.deltaResponse[i] != " ") {
						// start validate when the first valid char is found from the back
						startValidate = true;

					}
				} else {

					if (this.deltaResponse[i] != null && this.deltaResponse[i] != "" && this.deltaResponse[i] != " ") {

						continue;

					} else {

						// if any empty or not valid char is found return error
						MessageHelper.setErrorMessage("messages", "Formatted Input Not Complete");
						FacesContext.getCurrentInstance().validationFailed();
						PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
						return false;

					}
				}
			}
		}

		return true;

	}

	public String formatPattern(FormatInputDef formatInput) {
		
		String patType = formatInput !=null ? formatInput.getCharType() : "";
		
		if(patType.equalsIgnoreCase("AN")) {
			return "a-zA-Z0-9";
		}else if(patType.equalsIgnoreCase("AA")) {
			return "a-zA-Z";
		}else if(patType.equals("N")) {
			return "0-9";
		}else if(patType.equals("CL")) {
			return formatInput.getAllowedChars();
		}else if(patType.equals("SC")) {
			return formatInput.getAllowedChars();
		}
		return patType;
	
	}
	
	public String toolTipDisplay(FormatInputDef formatInput) {

		String patType = formatInput != null ? formatInput.getCharType() : "";
		String caseField = formatInput != null ? formatInput.getCaseField() : "";

		if (patType.equalsIgnoreCase("AN")) {
			if (caseField.equalsIgnoreCase("U")) {
				return "Alphanumeric characters only, upper case";
			} else if (caseField.equalsIgnoreCase("L")) {
				return "Alphanumeric characters only, lower case";
			} else {
				return "Alphanumeric characters only, mixed case";
			}
		} else if (patType.equalsIgnoreCase("AA")) {
			if (caseField.equalsIgnoreCase("U")) {
				return "Alpha characters only, upper case";
			} else if (caseField.equalsIgnoreCase("L")) {
				return "Alpha characters only, lower case";
			} else {
				return "Alpha characters only, mixed case";
			}
		} else if (patType.equals("N")) {
			return "Numeric characters only";
		} else if (patType.equals("CL")) {
			return "Limited to defined set of characters " + formatInput.getAllowedChars();
		} else if (patType.equals("SC")) {
			return "Limited to defined set of characters " + formatInput.getAllowedChars();
		}

		return patType;

	}
	

	public String[] getDeltaResponse() {
		return deltaResponse;
	}

	public void setDeltaResponse(String[] deltaResponse) {
		this.deltaResponse = deltaResponse;
	}

	public int getMandateFormatFields() {
		return mandateFormatFields;
	}

	public void setMandateFormatFields(int mandateFormatFields) {
		this.mandateFormatFields = mandateFormatFields;
	}

}
