/**
 * Author:  T Murray
 * Date:    14/08/2015
 * Project: FDE034
 * 
 * Copyright AMT-Sybex 2015
 */
package com.amtsybex.fieldreach.services.messages.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.amtsybex.fieldreach.services.messages.types.PersonalView;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import io.swagger.annotations.ApiModelProperty;

@XStreamAlias("GetPersonalViewResponse")
public class GetPersonalViewResponse implements Serializable {

	private static final long serialVersionUID = -1371493474159422322L;

    public static final String APPLICATION_VND_FIELDSMART_VIEW_1_JSON = "application/vnd.fieldsmart.view-1+json";
    public static final String APPLICATION_VND_FIELDSMART_VIEW_1_XML = "application/vnd.fieldsmart.view-1+xml";
    
	private ErrorMessage error;

    @ApiModelProperty(notes = "Call is either Successful (true) or Unsuccessful (false)")
    private boolean success;
    
    @JacksonXmlElementWrapper(localName = "personalViewList", useWrapping = true)
    @JacksonXmlProperty(localName = "PersonalView")
    private List<PersonalView> personalViewList = new ArrayList<PersonalView>();

    public ErrorMessage getError() {
        return error;
    }

    public void setError(ErrorMessage error) {
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

	public List<PersonalView> getPersonalViewList() {
		return personalViewList;
	}

	public void setPersonalViewList(List<PersonalView> personalViewList) {
		this.personalViewList = personalViewList;
	}
	
}
