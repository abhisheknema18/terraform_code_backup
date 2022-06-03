
package com.amtsybex.fieldreach.services.messages.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.amtsybex.fieldreach.services.messages.types.ScriptResult;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import io.swagger.annotations.ApiModelProperty;

@XStreamAlias("ScriptResultsSearchResponse")
public class ScriptResultsSearchResponse implements Serializable {

	private static final long serialVersionUID = 5159539035838838491L;
    public static final String APPLICATION_VND_FIELDSMART_SCRIPT_RESULT_SEARCH_1_JSON = "application/vnd.fieldsmart.script-result-search-1+json";
    public static final String APPLICATION_VND_FIELDSMART_SCRIPT_RESULT_SEARCH_1_XML = "application/vnd.fieldsmart.script-result-search-1+xml";
    
	private ErrorMessage error;

    @ApiModelProperty(notes = "Call is either Successful (true) or Unsuccessful (false)")
    private boolean success;
    
    private int resultsetCount;
    
    public int getResultsetCount() {
		return resultsetCount;
	}

	public void setResultsetCount(int resultsetCount) {
		this.resultsetCount = resultsetCount;
	}

	@JacksonXmlElementWrapper(useWrapping = true, localName = "scriptResultList")
    @JacksonXmlProperty(localName = "ScriptResult")
	private List<ScriptResult> scriptResultList = new ArrayList<ScriptResult>();

    /** 
     * Get the 'error' element value.
     * 
     * @return value
     */
    public ErrorMessage getError() {
        return error;
    }

    /** 
     * Set the 'error' element value.
     * 
     * @param error
     */
    public void setError(ErrorMessage error) {
        this.error = error;
    }

    /** 
     * Get the 'success' element value.
     * 
     * @return value
     */
    public boolean isSuccess() {
        return success;
    }

    /** 
     * Set the 'success' element value.
     * 
     * @param success
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /** 
     * Get the list of 'scriptResultList' element items.
     * 
     * @return list
     */
    public List<ScriptResult> getScriptResultList() {
        return scriptResultList;
    }

    /** 
     * Set the list of 'scriptResultList' element items.
     * 
     * @param list
     */
    public void setScriptResultList(List<ScriptResult> list) {
        scriptResultList = list;
    }
}
