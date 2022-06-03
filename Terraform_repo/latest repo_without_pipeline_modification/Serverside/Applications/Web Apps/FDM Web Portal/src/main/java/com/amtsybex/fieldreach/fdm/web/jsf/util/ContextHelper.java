package com.amtsybex.fieldreach.fdm.web.jsf.util;

import java.io.IOException;
import java.io.OutputStream;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

/**
 * helper class used when downloading files such as images
 */
public abstract class ContextHelper {
	
	public enum MimeType {
        IMAGE_JPEG("image/jpeg"), IMAGE_BMP("image/bmp"), VIDEO_MP4("video/mp4");

		private String value;

		private MimeType(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}
	
	/**
	 * helper method for downloading files
	 * 
	 * @param 	contentType - mime type of the content to be downloaded
	 * @param 	fileName	- file name to use for downloading the file
	 * @param 	data		- byte array of the data to be downloaded	
	 * @throws 	IOException
	 */
	public static void download(final MimeType contentType, final String fileName, byte[] data) throws IOException{
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		response.reset();
 		response.setContentType(contentType.getValue()); // http://www.iana.org/assignments/media-types all types 
    	response.setContentLength(data.length);
    	response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\""); 

    	OutputStream outputStream = response.getOutputStream();
    	outputStream.write(data);
    	FacesContext.getCurrentInstance().responseComplete(); 
	}
}
