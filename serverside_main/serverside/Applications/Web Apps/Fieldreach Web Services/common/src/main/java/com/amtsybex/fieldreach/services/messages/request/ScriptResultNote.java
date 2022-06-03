
package com.amtsybex.fieldreach.services.messages.request;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("ScriptResultNote")
public class ScriptResultNote implements Serializable {
		
	private static final long serialVersionUID = -321826684065336044L;
    public static final String APPLICATION_VND_FIELDSMART_SCRIPTRESULTNOTE_1_JSON = "application/vnd.fieldsmart.script-result-note-1+json";
    public static final String APPLICATION_VND_FIELDSMART_SCRIPTRESULTNOTE_1_XML = "application/vnd.fieldsmart.script-result-note-1+xml";
	
	private String noteText;
    private int sequenceNumber = 0;
    private int resOrderNo = 0;
    
	public String getNoteText() {
		
		return noteText;
	}
	public void setNoteText(String noteText) {
		
		this.noteText = noteText;
	}
	public int getSequenceNumber() {
		
		return sequenceNumber;
	}
	public void setSequenceNumber(int sequenceNumber) {
		
		this.sequenceNumber = sequenceNumber;
	}
	public int getResOrderNo() {
		
		return resOrderNo;
	}
	public void setResOrderNo(int resOrderNo) {
		
		this.resOrderNo = resOrderNo;
	}

}
