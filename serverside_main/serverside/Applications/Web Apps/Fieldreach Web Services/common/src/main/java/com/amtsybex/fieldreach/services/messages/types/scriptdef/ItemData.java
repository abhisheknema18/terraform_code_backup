package com.amtsybex.fieldreach.services.messages.types.scriptdef;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("ITEMDATA")
public class ItemData {

	@XStreamImplicit
    @JacksonXmlElementWrapper(localName = "DEFECTS", useWrapping = false)
    @JacksonXmlProperty(localName = "DEFECTS")
    protected List<Defects> defects;
	
	@XStreamAlias("DEFECTSET")
	@JacksonXmlProperty(localName = "DEFECTSET")
    protected DefectSet defectset;

	@XStreamImplicit
    @JacksonXmlElementWrapper(localName = "FORMATINPUTDEFME", useWrapping = false)
    @JacksonXmlProperty(localName = "FORMATINPUTDEFME")
    protected List<FormatInputDefMe> formatinputdefme;
	
	@XStreamAlias("ITEM")
    @JacksonXmlElementWrapper(localName = "ITEM", useWrapping = false)
    @JacksonXmlProperty(localName = "ITEM")
    protected ItemType item;
	
	@XStreamImplicit
    @JacksonXmlElementWrapper(localName = "VALIDATION", useWrapping = false)
    @JacksonXmlProperty(localName = "VALIDATION")
    protected List<Validation> validation;
	
	@XStreamImplicit
    @JacksonXmlElementWrapper(localName = "MEASURECATEGORIES", useWrapping = false)
    @JacksonXmlProperty(localName = "MEASURECATEGORIES")
    protected List<MeasureCategories> measurecategories;
	
	@XStreamImplicit
    @JacksonXmlElementWrapper(localName = "REFCAT", useWrapping = false)
    @JacksonXmlProperty(localName = "REFCAT")
    protected List<RefCat> refcat;
	
	@XStreamImplicit
    @JacksonXmlElementWrapper(localName = "SCRIPTREFITEM", useWrapping = false)
    @JacksonXmlProperty(localName = "SCRIPTREFITEM")
    protected List<ScriptRefItem> scriptrefitem;


    public List<Defects> getDEFECTS() {
        if (defects == null) {
            defects = new ArrayList<Defects>();
        }
        return this.defects;
    }
    
    public void setDEFECTS(List<Defects> defects) {

       this.defects = defects;
    }
    
	public DefectSet getDEFECTSET() {
		return defectset;
	}

	public void setDEFECTSET(DefectSet defectset) {
		this.defectset = defectset;
	}

    public List<FormatInputDefMe> getFORMATINPUTDEFME() {
        if (formatinputdefme == null) {
            formatinputdefme = new ArrayList<FormatInputDefMe>();
        }
        return this.formatinputdefme;
    }

    public void getFORMATINPUTDEFME(List<FormatInputDefMe> formatinputdefme) {

        this.formatinputdefme = formatinputdefme;
    }
    
    public ItemType getITEM() {
        return item;
    }

    public void setITEM(ItemType value) {
        this.item = value;
    }

    public List<Validation> getVALIDATION() {
        if (validation == null) {
            validation = new ArrayList<Validation>();
        }
        return this.validation;
    }

    public void setVALIDATION(List<Validation> validation) {

        this.validation = validation;
    } 
    
    public List<MeasureCategories> getMEASURECATEGORIES() {
        if (measurecategories == null) {
            measurecategories = new ArrayList<MeasureCategories>();
        }
        return this.measurecategories;
    }

    public void setMEASURECATEGORIES(List<MeasureCategories> measureCategories) {

        this.measurecategories = measureCategories;
    }
    
    public List<RefCat> getREFCAT() {
        if (refcat == null) {
            refcat = new ArrayList<RefCat>();
        }
        return this.refcat;
    }

    public void setREFCAT(List<RefCat> refcat) {
        this.refcat = refcat;
    }
    
    public List<ScriptRefItem> getSCRIPTREFITEM() {
        if (scriptrefitem == null) {
            scriptrefitem = new ArrayList<ScriptRefItem>();
        }
        return this.scriptrefitem;
    }

    public void setSCRIPTREFITEM(List<ScriptRefItem> scriptrefitem) {

        this.scriptrefitem = scriptrefitem;
    }
    
}
