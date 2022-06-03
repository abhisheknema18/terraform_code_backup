package com.amtsybex.fieldreach.services.messages.request;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("UploadInitiate")
public class UploadInitiate implements Serializable {
	
	private static final long serialVersionUID = -5085763283669380869L;

	private String fileName;
	private long totalSizeBytes;
	private String mimeType;
	private String identifier;
	private String fileDesc; // FDE029 TM 23/01/2015

	/**
	 * Get the 'fileName' element value.
	 * 
	 * @return value
	 */
	public String getFileName() {
		
		return fileName;
	}

	/**
	 * Set the 'fileName' element value.
	 * 
	 * @param fileName
	 */
	public void setFileName(String fileName) {
		
		this.fileName = fileName;
	}

	/**
	 * Get the 'totalSizeBytes' element value.
	 * 
	 * @return value
	 */
	public long getTotalSizeBytes() {
		
		return totalSizeBytes;
	}

	/**
	 * Set the 'totalSizeBytes' element value.
	 * 
	 * @param totalSizeBytes
	 */
	public void setTotalSizeBytes(long totalSizeBytes) {
		
		this.totalSizeBytes = totalSizeBytes;
	}

	/**
	 * Get the 'mimeType' element value.
	 * 
	 * @return value
	 */
	public String getMimeType() {
		
		return mimeType;
	}

	/**
	 * Set the 'mimeType' element value.
	 * 
	 * @param mimeType
	 */
	public void setMimeType(String mimeType) {
		
		this.mimeType = mimeType;
	}

	/**
	 * Get the 'identifier' element value.
	 * 
	 * @return value
	 */
	public String getIdentifier() {
		
		return identifier;
	}

	/**
	 * Set the 'identifier' element value.
	 * 
	 * @param identifier
	 */
	public void setIdentifier(String identifier) {
		
		this.identifier = identifier;
	}

	// FDE029 TM 23/01/2015

	public String getFileDesc() {

		return fileDesc;
	}

	public void setFileDesc(String fileDesc) {

		this.fileDesc = fileDesc;
	}

	// End FDE029
}
