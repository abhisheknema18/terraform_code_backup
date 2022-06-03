package com.amtsybex.fieldreach.services.messages.response;

import java.io.Serializable;

import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.amtsybex.fieldreach.services.messages.types.scriptdef.ItemData;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import io.swagger.annotations.ApiModelProperty;

@XStreamAlias("ScriptQuestionDefinitionResponse")
public class ScriptQuestionDefinitionResponse implements Serializable {

	private static final long serialVersionUID = -527053591259619890L;
    public static final String APPLICATION_VND_FIELDSMART_SCRIPT_QUESTION_1_JSON = "application/vnd.fieldsmart.script-question-1+json";
    public static final String APPLICATION_VND_FIELDSMART_SCRIPT_QUESTION_1_XML = "application/vnd.fieldsmart.script-question-1+xml";
    
	protected ErrorMessage error;

    @ApiModelProperty(notes = "Call is either Successful (true) or Unsuccessful (false)")
    protected boolean success;
    
    protected ItemData itemData;

    
    public ErrorMessage getError() {
        return error;
    }

    public void setError(ErrorMessage value) {
        this.error = value;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean value) {
        this.success = value;
    }

    public ItemData getItemData() {
        return itemData;
    }

    public void setItemData(ItemData value) {
        this.itemData = value;
    }

}
