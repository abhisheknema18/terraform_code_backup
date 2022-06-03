
package com.amtsybex.fieldreach.services.messages.types;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Attachment")
public class Attachment implements Serializable {

	private static final long serialVersionUID = -7369736570467976836L;
	
	private String fileName;
    private String fileDesc;
    private String type;
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
	
	public String getType() {
		
		return type;
	}
	
	public void setType(String type) {
		
		this.type = type;
	}
	
	public String getChecksum() {
		
		return checksum;
	}
	
	public void setChecksum(String checksum) {
		
		this.checksum = checksum;
	}
    
}
