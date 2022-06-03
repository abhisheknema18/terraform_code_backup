package com.amtsybex.fieldreach.services.messages.types.scriptdef;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("ITEMTYPE")
public class ItemType {

	@XStreamAlias("SCRIPTID")
    @JacksonXmlProperty(localName = "SCRIPTID")
    protected int scriptid;
    
	@XStreamAlias("SEQUENCENUMBER")
    @JacksonXmlProperty(localName = "SEQUENCENUMBER")
    protected int sequencenumber;
    
	@XStreamAlias("ALTERNATEREF")
    @JacksonXmlProperty(localName = "ALTERNATEREF")
    protected String alternateref;
    
	@XStreamAlias("ITEMTEXT")
    @JacksonXmlProperty(localName = "ITEMTEXT")
    protected String itemtext;
    
	@XStreamAlias("ITEMTYPE")
    @JacksonXmlProperty(localName = "ITEMTYPE")
    protected String itemtype;
    
	@XStreamAlias("INPUTTYPE")
    @JacksonXmlProperty(localName = "INPUTTYPE")
    protected String inputtype;
    
	@XStreamAlias("ILEVEL")
    @JacksonXmlProperty(localName = "ILEVEL")
    protected int ilevel;
    
	@XStreamAlias("FIELDSIZE")
    @JacksonXmlProperty(localName = "FIELDSIZE")
    protected String fieldsize;
    
	@XStreamAlias("PRECISION")
    @JacksonXmlProperty(localName = "PRECISION")
    protected Integer precision;
    
	@XStreamAlias("VALIDATION")
    @JacksonXmlProperty(localName = "VALIDATION")
    protected String validation;
    
	@XStreamAlias("DATAREF")
    @JacksonXmlProperty(localName = "DATAREF")
    protected String dataref;
    
	@XStreamAlias("DEFECTSETNAME")
    @JacksonXmlProperty(localName = "DEFECTSETNAME")
    protected String defectsetname;
    
	@XStreamAlias("UOMCATID")
    @JacksonXmlProperty(localName = "UOMCATID")
    protected Integer uomcatid;
    
	@XStreamImplicit
    @JacksonXmlElementWrapper(localName = "VALFREETEXTREQ", useWrapping = false)
    @JacksonXmlProperty(localName = "VALFREETEXTREQ")
    protected List<ValFreeTextReq> valfreetextreq;
    
	@XStreamImplicit
    @JacksonXmlElementWrapper(localName = "UOMNUMVALID", useWrapping = false)
    @JacksonXmlProperty(localName = "UOMNUMVALID")
    protected List<UomNumValid> uomnumvalid;

	@XStreamImplicit
    @JacksonXmlElementWrapper(localName = "FORMATINPUTDEF", useWrapping = false)
    @JacksonXmlProperty(localName = "FORMATINPUTDEF")
    protected List<FormatInputDef> formatinputdef;
    
	@XStreamAlias("GENNUMVALID")
    @JacksonXmlProperty(localName = "GENNUMVALID")
    protected GenNumValid gennumvalid;
    
	@XStreamAlias("RELWEIGHT")
    @JacksonXmlProperty(localName = "RELWEIGHT")
    protected Integer relweight;


    public int getSCRIPTID() {
        return scriptid;
    }

    public void setSCRIPTID(int value) {
        this.scriptid = value;
    }

    public int getSEQUENCENUMBER() {
        return sequencenumber;
    }

    public void setSEQUENCENUMBER(int value) {
        this.sequencenumber = value;
    }

    public String getALTERNATEREF() {
        return alternateref;
    }

    public void setALTERNATEREF(String value) {
        this.alternateref = value;
    }

    public String getITEMTEXT() {
        return itemtext;
    }

    public void setITEMTEXT(String value) {
        this.itemtext = value;
    }

    public String getITEMTYPE() {
        return itemtype;
    }

    public void setITEMTYPE(String value) {
        this.itemtype = value;
    }

    public String getINPUTTYPE() {
        return inputtype;
    }

    public void setINPUTTYPE(String value) {
        this.inputtype = value;
    }

    public int getILEVEL() {
        return ilevel;
    }

    public void setILEVEL(int value) {
        this.ilevel = value;
    }

    public String getFIELDSIZE() {
        return fieldsize;
    }

    public void setFIELDSIZE(String value) {
        this.fieldsize = value;
    }

    public Integer getPRECISION() {
        return precision;
    }

    public void setPRECISION(Integer value) {
        this.precision = value;
    }

    public String getVALIDATION() {
        return validation;
    }

    public void setVALIDATION(String value) {
        this.validation = value;
    }

    public String getDATAREF() {
        return dataref;
    }

    public void setDATAREF(String value) {
        this.dataref = value;
    }

    public String getDEFECTSETNAME() {
        return defectsetname;
    }

    public void setDEFECTSETNAME(String value) {
        this.defectsetname = value;
    }

    public Integer getUOMCATID() {
        return uomcatid;
    }

    public void setUOMCATID(Integer value) {
        this.uomcatid = value;
    }

    public List<ValFreeTextReq> getVALFREETEXTREQ() {
        if (valfreetextreq == null) {
        	valfreetextreq = new ArrayList<ValFreeTextReq>();
        }
        return this.valfreetextreq;
	}

	public void setVALFREETEXTREQ(List<ValFreeTextReq> valfreetextreq) {
		this.valfreetextreq = valfreetextreq;
	}

	public List<UomNumValid> getUOMNUMVALID() {
        if (uomnumvalid == null) {
        	uomnumvalid = new ArrayList<UomNumValid>();
        }
        return this.uomnumvalid;
	}

	public void setUOMNUMVALID(List<UomNumValid> uomnumvalid) {
		this.uomnumvalid = uomnumvalid;
	}
	

    public List<FormatInputDef> getFORMATINPUTDEF() {
        if (formatinputdef == null) {
            formatinputdef = new ArrayList<FormatInputDef>();
        }
        return this.formatinputdef;
    }

    public GenNumValid getGENNUMVALID() {
        return gennumvalid;
    }

    public void setGENNUMVALID(GenNumValid value) {
        this.gennumvalid = value;
    }

    public Integer getRELWEIGHT() {
        return relweight;
    }

    public void setRELWEIGHT(Integer value) {
        this.relweight = value;
    }

}
