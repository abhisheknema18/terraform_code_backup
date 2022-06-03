/**
 * Project: FDE026
 * Author:	T Murray
 * Date:	05/08/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.xmlloader.scriptresult.oxm;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "title", "usercode", "deviceid",
		"workgroupcode", "runtype", "summdesc", "scriptstatus", "heading",
		"subheading", "sourcefile", "resassoccode", "resultsfile", "startdate",
		"starttime", "completedate", "completetime", "completecode",
		"completeuser", "namefield1", "namefield2", "namefield3", "namefield4",
		"namefield5", "namefield6", "namefield7", "namefield8", "namefield9",
		"namefield10", "completionmode", "elementNames" })
public class General {

	@XmlElement(name = "TITLE")
	protected String title;
	@XmlElement(name = "USERCODE")
	protected String usercode;
	@XmlElement(name = "DEVICEID")
	protected String deviceid;
	@XmlElement(name = "WORKGROUPCODE")
	protected String workgroupcode;
	@XmlElement(name = "RUNTYPE")
	protected String runtype;
	@XmlElement(name = "SUMMDESC")
	protected String summdesc;
	@XmlElement(name = "SCRIPTSTATUS")
	protected String scriptstatus;
	@XmlElement(name = "HEADING")
	protected String heading;
	@XmlElement(name = "SUBHEADING")
	protected String subheading;
	@XmlElement(name = "SOURCEFILE")
	protected String sourcefile;
	@XmlElement(name = "RESASSOCCODE")
	protected String resassoccode;
	@XmlElement(name = "RESULTSFILE")
	protected String resultsfile;
	@XmlElement(name = "STARTDATE")
	protected String startdate;
	@XmlElement(name = "STARTTIME")
	protected String starttime;
	@XmlElement(name = "COMPLETEDATE")
	protected String completedate;
	@XmlElement(name = "COMPLETETIME")
	protected String completetime;
	@XmlElement(name = "COMPLETECODE")
	protected String completecode;
	@XmlElement(name = "COMPLETEUSER")
	protected String completeuser;
	@XmlElement(name = "NAMEFIELD1")
	protected String namefield1;
	@XmlElement(name = "NAMEFIELD2")
	protected String namefield2;
	@XmlElement(name = "NAMEFIELD3")
	protected String namefield3;
	@XmlElement(name = "NAMEFIELD4")
	protected String namefield4;
	@XmlElement(name = "NAMEFIELD5")
	protected String namefield5;
	@XmlElement(name = "NAMEFIELD6")
	protected String namefield6;
	@XmlElement(name = "NAMEFIELD7")
	protected String namefield7;
	@XmlElement(name = "NAMEFIELD8")
	protected String namefield8;
	@XmlElement(name = "NAMEFIELD9")
	protected String namefield9;
	@XmlElement(name = "NAMEFIELD10")
	protected String namefield10;
	@XmlElement(name = "COMPLETIONMODE")
	protected String completionmode;

	protected List<String> elementNames = Arrays.asList("TITLE", "USERCODE",
			"DEVICEID", "WORKGROUPCODE", "RUNTYPE", "SUMMDESC", "SCRIPTSTATUS",
			"HEADING", "SUBHEADING", "SOURCEFILE", "RESASSOCCODE",
			"RESULTSFILE", "STARTDATE", "STARTTIME", "COMPLETEDATE",
			"COMPLETETIME", "COMPLETECODE", "COMPLETEUSER", "NAMEFIELD1",
			"NAMEFIELD2", "NAMEFIELD3", "NAMEFIELD4", "NAMEFIELD5",
			"NAMEFIELD6", "NAMEFIELD7", "NAMEFIELD8", "NAMEFIELD9",
			"NAMEFIELD10", "COMPLETIONMODE");

	public List<String> getElementNames() {
		return this.elementNames;
	}
	
	/**
	 * Gets the value of the title property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getTITLE() {
		return title;
	}

	/**
	 * Sets the value of the title property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setTITLE(String value) {
		this.title = value;
	}

	/**
	 * Gets the value of the usercode property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getUSERCODE() {
		return usercode;
	}

	/**
	 * Sets the value of the usercode property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setUSERCODE(String value) {
		this.usercode = value;
	}

	/**
	 * Gets the value of the deviceid property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getDEVICEID() {
		return deviceid;
	}

	/**
	 * Sets the value of the deviceid property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setDEVICEID(String value) {
		this.deviceid = value;
	}

	/**
	 * Gets the value of the workgroupcode property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getWORKGROUPCODE() {
		return workgroupcode;
	}

	/**
	 * Sets the value of the workgroupcode property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setWORKGROUPCODE(String value) {
		this.workgroupcode = value;
	}

	/**
	 * Gets the value of the runtype property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getRUNTYPE() {
		return runtype;
	}

	/**
	 * Sets the value of the runtype property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setRUNTYPE(String value) {
		this.runtype = value;
	}

	/**
	 * Gets the value of the summdesc property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSUMMDESC() {
		return summdesc;
	}

	/**
	 * Sets the value of the summdesc property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSUMMDESC(String value) {
		this.summdesc = value;
	}

	/**
	 * Gets the value of the scriptstatus property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSCRIPTSTATUS() {
		return scriptstatus;
	}

	/**
	 * Sets the value of the scriptstatus property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSCRIPTSTATUS(String value) {
		this.scriptstatus = value;
	}

	/**
	 * Gets the value of the heading property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getHEADING() {
		return heading;
	}

	/**
	 * Sets the value of the heading property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setHEADING(String value) {
		this.heading = value;
	}

	/**
	 * Gets the value of the subheading property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSUBHEADING() {
		return subheading;
	}

	/**
	 * Sets the value of the subheading property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSUBHEADING(String value) {
		this.subheading = value;
	}

	/**
	 * Gets the value of the sourcefile property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSOURCEFILE() {
		return sourcefile;
	}

	/**
	 * Sets the value of the sourcefile property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSOURCEFILE(String value) {
		this.sourcefile = value;
	}

	/**
	 * Gets the value of the resassoccode property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getRESASSOCCODE() {
		return resassoccode;
	}

	/**
	 * Sets the value of the resassoccode property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setRESASSOCCODE(String value) {
		this.resassoccode = value;
	}

	/**
	 * Gets the value of the resultsfile property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getRESULTSFILE() {
		return resultsfile;
	}

	/**
	 * Sets the value of the resultsfile property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setRESULTSFILE(String value) {
		this.resultsfile = value;
	}

	/**
	 * Gets the value of the startdate property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSTARTDATE() {
		return startdate;
	}

	/**
	 * Sets the value of the startdate property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSTARTDATE(String value) {
		this.startdate = value;
	}

	/**
	 * Gets the value of the starttime property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSTARTTIME() {
		return starttime;
	}

	/**
	 * Sets the value of the starttime property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSTARTTIME(String value) {
		this.starttime = value;
	}

	/**
	 * Gets the value of the completedate property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCOMPLETEDATE() {
		return completedate;
	}

	/**
	 * Sets the value of the completedate property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCOMPLETEDATE(String value) {
		this.completedate = value;
	}

	/**
	 * Gets the value of the completetime property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCOMPLETETIME() {
		return completetime;
	}

	/**
	 * Sets the value of the completetime property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCOMPLETETIME(String value) {
		this.completetime = value;
	}

	/**
	 * Gets the value of the completecode property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCOMPLETECODE() {
		return completecode;
	}

	/**
	 * Sets the value of the completecode property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCOMPLETECODE(String value) {
		this.completecode = value;
	}

	/**
	 * Gets the value of the completeuser property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCOMPLETEUSER() {
		return completeuser;
	}

	/**
	 * Sets the value of the completeuser property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCOMPLETEUSER(String value) {
		this.completeuser = value;
	}

	/**
	 * Gets the value of the namefield1 property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getNAMEFIELD1() {
		return namefield1;
	}

	/**
	 * Sets the value of the namefield1 property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setNAMEFIELD1(String value) {
		this.namefield1 = value;
	}

	/**
	 * Gets the value of the namefield2 property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getNAMEFIELD2() {
		return namefield2;
	}

	/**
	 * Sets the value of the namefield2 property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setNAMEFIELD2(String value) {
		this.namefield2 = value;
	}

	/**
	 * Gets the value of the namefield3 property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getNAMEFIELD3() {
		return namefield3;
	}

	/**
	 * Sets the value of the namefield3 property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setNAMEFIELD3(String value) {
		this.namefield3 = value;
	}

	/**
	 * Gets the value of the namefield4 property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getNAMEFIELD4() {
		return namefield4;
	}

	/**
	 * Sets the value of the namefield4 property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setNAMEFIELD4(String value) {
		this.namefield4 = value;
	}

	/**
	 * Gets the value of the namefield5 property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getNAMEFIELD5() {
		return namefield5;
	}

	/**
	 * Sets the value of the namefield5 property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setNAMEFIELD5(String value) {
		this.namefield5 = value;
	}

	/**
	 * Gets the value of the namefield6 property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getNAMEFIELD6() {
		return namefield6;
	}

	/**
	 * Sets the value of the namefield6 property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setNAMEFIELD6(String value) {
		this.namefield6 = value;
	}

	/**
	 * Gets the value of the namefield7 property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getNAMEFIELD7() {
		return namefield7;
	}

	/**
	 * Sets the value of the namefield7 property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setNAMEFIELD7(String value) {
		this.namefield7 = value;
	}

	/**
	 * Gets the value of the namefield8 property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getNAMEFIELD8() {
		return namefield8;
	}

	/**
	 * Sets the value of the namefield8 property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setNAMEFIELD8(String value) {
		this.namefield8 = value;
	}

	/**
	 * Gets the value of the namefield9 property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getNAMEFIELD9() {
		return namefield9;
	}

	/**
	 * Sets the value of the namefield9 property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setNAMEFIELD9(String value) {
		this.namefield9 = value;
	}

	/**
	 * Gets the value of the namefield10 property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getNAMEFIELD10() {
		return namefield10;
	}

	/**
	 * Sets the value of the namefield10 property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setNAMEFIELD10(String value) {
		this.namefield10 = value;
	}

	/**
	 * Gets the value of the completionmode property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCOMPLETIONMODE() {
		return completionmode;
	}

	/**
	 * Sets the value of the completionmode property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCOMPLETIONMODE(String value) {
		this.completionmode = value;
	}

}
