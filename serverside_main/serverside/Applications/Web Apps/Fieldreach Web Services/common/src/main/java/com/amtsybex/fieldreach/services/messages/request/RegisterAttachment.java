
package com.amtsybex.fieldreach.services.messages.request;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("RegisterAttachment")
public class RegisterAttachment implements Serializable {
	
	private static final long serialVersionUID = 3424574158979047951L;
	
	public static final String APPLICATION_VND_FIELDSMART_ATTACHMENT_1_JSON = "application/vnd.fieldsmart.attachment-1+json";
    public static final String APPLICATION_VND_FIELDSMART_ATTACHMENT_1_XML = "application/vnd.fieldsmart.attachment-1+xml";
	
	private String fileName;
    private String fileDesc;
    private String fileType;
    private String data;
    private String checksum;
    
	public String getFileName() {
		
		return fileName;
	}
	
	public void setFileName(String fileName) {
		
		this.fileName = fileName;
	}
	
	public String getFileDesc() {
		
		return fileDesc;
	}
	
	public void setFileDesc(String fileDesc) {
		
		this.fileDesc = fileDesc;
	}
	
	public String getFileType() {
		
		return fileType;
	}
	
	public void setFileType(String fileType) {
		
		this.fileType = fileType;
	}
	
	public String getData() {
		
		return data;
	}
	
	public void setData(String data) {
		
		this.data = data;
	}
	
	public String getChecksum() {
		
		return checksum;
	}
	
	public void setChecksum(String checksum) {
		
		this.checksum = checksum;
	}
	
}
