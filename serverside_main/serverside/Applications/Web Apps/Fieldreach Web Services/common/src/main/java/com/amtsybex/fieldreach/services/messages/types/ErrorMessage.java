
package com.amtsybex.fieldreach.services.messages.types;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@XStreamAlias("ErrorMessage")
@ApiModel(description = "Error Response")
public class ErrorMessage implements Serializable {

	private static final long serialVersionUID = 8411998757716158990L;
	
	@ApiModelProperty(notes = "FUSION Error Code", name = "errorCode")
	private String errorCode;
    @ApiModelProperty(notes = "Detailed error description", name = "errorCode")
    private String errorDescription;

    /** 
     * Get the 'errorCode' element value.
     * 
     * @return value
     */
    public String getErrorCode() {
        return errorCode;
    }

    /** 
     * Set the 'errorCode' element value.
     * 
     * @param errorCode
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    /** 
     * Get the 'errorDescription' element value.
     * 
     * @return value
     */
    public String getErrorDescription() {
        return errorDescription;
    }

    /** 
     * Set the 'errorDescription' element value.
     * 
     * @param errorDescription
     */
    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }
}
