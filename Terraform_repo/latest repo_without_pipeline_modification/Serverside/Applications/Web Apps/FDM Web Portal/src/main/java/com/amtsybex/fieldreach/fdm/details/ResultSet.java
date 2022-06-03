package com.amtsybex.fieldreach.fdm.details;

import java.io.Serializable;
import java.sql.Blob;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import com.amtsybex.fieldreach.backend.model.DisplayColour;
import com.amtsybex.fieldreach.backend.model.FormatInputDef;
import com.amtsybex.fieldreach.backend.model.FormatInputDefMe;
import com.amtsybex.fieldreach.backend.model.GenNumValid;
import com.amtsybex.fieldreach.backend.model.Item;
import com.amtsybex.fieldreach.backend.model.ResLog;
import com.amtsybex.fieldreach.backend.model.ResultNotes;
import com.amtsybex.fieldreach.backend.model.ScriptRefItem;
import com.amtsybex.fieldreach.backend.model.ScriptResultBlb;
import com.amtsybex.fieldreach.backend.model.SuppResults;
import com.amtsybex.fieldreach.backend.model.TaskListRes;
import com.amtsybex.fieldreach.backend.model.UnitOfMeasure;
import com.amtsybex.fieldreach.backend.model.UomNumValid;
import com.amtsybex.fieldreach.backend.model.ValFreeTextReq;
import com.amtsybex.fieldreach.backend.model.ValidationProperty;
import com.amtsybex.fieldreach.utils.impl.Common;

/**
 * data for every row of the main table on the details screen
 */

//@Scope(WebApplicationContext.SCOPE_REQUEST)

public class ResultSet implements Serializable {
	private static final long serialVersionUID = 161012122197741987L;
	
	public enum MimeType {
		FLV("x-flv"), MP4("mp4"), M3U8("x-mpegURL"), TS("MP2T"), MOV("quicktime"), AVI("x-msvideo"), WMV("x-ms-wmv"), MP3("mpeg"), OGG("ogg"), WAV("wav"), M4A("mp4"), AAC("aac");

		private String value;

		private MimeType(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}
	
	/*****************************************
	 * Script Item Information
	 *****************************************/
	
	private String script;
	private Integer sequenceNo;
	
	private boolean heading;
	private boolean endMarker;
	private boolean bitmap;
	private boolean photograph;
	private boolean date;
	private boolean audio;
	private boolean video;
	private boolean list;
	private boolean taskList;
	private boolean multiPickList;
	//38437 - KN - To Display Result date time
	private Integer resultDate;
	private Integer resultTime; 
	//38437 - KN - To get script Input type
	private String inputType; 
	//38437 - KN - To get Each Question type
	private String itemType;
	//38437 - KN - To get response Type
	private String responseType;
	
	private boolean mandatorymodifierCheckBox;
	
	//40317 - toggle answered/unanswered
	private Integer iLevel;
	private boolean hasAnsweredQuestions;
	
	//47249 - KN - GDPR PII
	private boolean viewPII;
	
	Integer scriptId;
	
	GenNumValid genNumValid;
	List<UomNumValid> UOMNumValids;
	
	FormatInputDefMe formatInputDefMe;
	List<FormatInputDef> formatInputDefList;
	
	List<ValidationProperty> validationPropertyList;
	List<ValFreeTextReq> valFreeTextReqList;

	List<UnitOfMeasure> unitOfMeasureList;
	
	List<ScriptRefItem> scriptRefItemList;
	
	//end FDE058 - MC - edit Response
	HashMap<String, String> colorMap;
	
	/*****************************************
	 * Response Item Information
	 *****************************************/
	
	
	private String response;
	private String freeText;
	private Integer resOrderNo;
	private boolean answered;
	private Date dateValue;
	//private ScriptResultBlb blob;
	private byte[] blob;
	private String blobFileName;
	
	private String base64Bitmap;
	private String base64Photograph;
	private String base64Video;
	private String activeImage;
	private Integer returnId;
	private Integer id;
	
	private List<TaskListRes> taskListItems;
	private List<SuppResults> multiPickListItems;
	private Integer ootFlag;
	private String otCol;

	//FDE058 - Support for "Notes" associated to script results
	private List<ResultNotes> resultNotes;
	//FDP1420 - MC - add long response
	private String extendedResponse;
	
	//39044 - KN -for decimal Precision
	private Integer precision;
	//39044 - KN -field Size for Response
	private String fieldSize;
	
	private String uom;
	private String deltaFreeText;
	private String deltaUom;
	
	private List<ResLog> resLogList;
	private Boolean isLogPresent;
	
	public boolean isDate() {
		return date;
	}

	public void setDate(boolean date) {
		this.date = date;
	}

	public Date getDateValue() {
		return dateValue;
	}

	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}

	public String getActiveImage() {
		return activeImage;
	}

	public void setActiveImage(String activeImage) {
		this.activeImage = activeImage;
	}

	public boolean isList() {
		return list;
	}

	public void setList(boolean list) {
		this.list = list;
	}
	
	public boolean isTaskList() {
		return taskList;
	}

	public void setTaskList(boolean taskList) {
		this.taskList = taskList;
	}

	public boolean isMultiPickList() {
		return multiPickList;
	}

	public void setMultiPickList(boolean multiPickList) {
		this.multiPickList = multiPickList;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public ResultSet(){
	}
	
	public ResultSet(Integer id){
		this.id = id;
	}
	
	public ResultSet(Integer id, Integer scriptId, String script, Integer resOrderNo, Integer sequenceNo, String itemType, String inputType){
		this.id = id;
		this.scriptId = scriptId;
		this.script = script;
		this.resOrderNo = resOrderNo;
		this.sequenceNo = sequenceNo;
		this.itemType = itemType;
		this.inputType = inputType;
	}

	public ResultSet(Integer id, String script, String response, String freeText, Integer resOrderNo) {
		this.id = id;
		this.script = script;
		this.response = response;
		this.freeText = freeText;
		this.resOrderNo = resOrderNo;
		this.answered = true;
	}
	
	public ResultSet(Integer id, String script, Date dateValue, String freeText, Integer resOrderNo) {
		this.id = id;
		this.script = script;
		this.dateValue = dateValue;
		this.freeText = freeText;
		this.resOrderNo = resOrderNo;
		this.answered = true;
	}
	
	public ResultSet(Integer id, String script, ScriptResultBlb blob, String freeText, Integer resOrderNo, Integer returnId) {
		this.id = id;
		this.script = script;
		if(blob != null) {
			this.blob = blobToByteArray(blob.getBlobResult());
			this.blobFileName = blob.getFileName();
		}
		this.freeText = freeText;
		this.resOrderNo = resOrderNo;
		this.returnId = returnId;
		this.answered = true;
	}
	
	public static byte[] blobToByteArray(Blob blob){
		byte[] blobBytes = null;

		int blobLength = 0;
		try {
			blobLength = (int) blob.length();
			blobBytes = blob.getBytes(1, blobLength);
		} catch (Exception e) {
			//do nothing
		}
		
		return blobBytes;
	}
	
	public ResultSet(String script){
		this.script = script;
		this.answered = false;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getFreeText() {
		return freeText;
	}

	public void setFreeText(String freeText) {
		this.freeText = freeText;
	}

	public Integer getResOrderNo() {
		return resOrderNo;
	}

	public void setResOrderNo(Integer resOrderNo) {
		this.resOrderNo = resOrderNo;
	}

	public boolean isAnswered() {
		return answered;
	}

	public void setAnswered(boolean answered) {
		this.answered = answered;
	}

	public boolean isHeading() {
		return heading;
	}
	
	public void setHeading(boolean heading) {
		this.heading = heading;
	}

	public boolean isEndMarker() {
		return endMarker;
	}

	public void setEndMarker(boolean endMarker) {
		this.endMarker = endMarker;
	}

	/*public ScriptResultBlb getBlob() {
		return blob;
	}

	public void setBlob(ScriptResultBlb blob) {
		this.blob = blob;
	}*/
	
	//FDE058 - Support for "Notes" associated to script results
	public Integer getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(Integer sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	public String getBase64Bitmap() {
		if(blob != null) {
			base64Bitmap = Common.getBase64Bitmap(blob);
		}
		return base64Bitmap;
	}

	public void setBase64Bitmap(String base64Bitmap) {
		this.base64Bitmap = base64Bitmap;
	}

	public String getBase64Photograph() {
		if(blob != null) { 
			base64Photograph = Common.getBase64Photograph(blob);
		}
		return base64Photograph;
	}

	public void setBase64Photograph(String base64Photograph) {
		this.base64Photograph = base64Photograph;
	}

	public Integer getReturnId() {
		return returnId;
	}

	public void setReturnId(Integer returnId) {
		this.returnId = returnId;
	}

	public boolean isBitmap() {
		return bitmap;
	}

	public void setBitmap(boolean bitmap) {
		this.bitmap = bitmap;
	}

	public boolean isPhotograph() {
		return photograph;
	}

	public void setPhotograph(boolean photograph) {
		this.photograph = photograph;
	}

	public String getExtendedResponse() {
		return extendedResponse;
	}

	public void setExtendedResponse(String extendedResponse) {
		this.extendedResponse = extendedResponse;
	}

	//FDE058 - Support for "Notes" associated to script results
	public List<ResultNotes> getResultNotes() {
		return resultNotes;
	}

	public void setResultNotes(List<ResultNotes> resultNotes) {
		this.resultNotes = resultNotes;
	}

	public List<TaskListRes> getTaskListItems() {
		return taskListItems;
	}

	public void setTaskListItems(List<TaskListRes> taskListItems) {
		this.taskListItems = taskListItems;
	}

	public List<SuppResults> getMultiPickListItems() {
		return multiPickListItems;
	}

	public void setMultiPickListItems(List<SuppResults> multiPickListItems) {
		this.multiPickListItems = multiPickListItems;
	}
	
		public String getBase64Video() {
		if (blob != null) {

			// EWP594 19/03/2018 MC Change method for Base64 encoding
			// base64Bitmap = BASE_64_BMP_PREFIX + Base64.encode(blobByteArray);
			if (blob != null) {
				if (audio) {
					base64Video = "data:audio/" + MimeType.valueOf(blobFileName.substring(blobFileName.lastIndexOf(".") + 1).toUpperCase()).getValue() + ";base64,"
							+ Base64.encodeBase64String(blob);
				} else if (video) {
					base64Video = "data:video/" + MimeType.valueOf(blobFileName.substring(blobFileName.lastIndexOf(".") + 1).toUpperCase()).getValue() + ";base64,"
							+ Base64.encodeBase64String(blob);
				}
			}

		}

		return base64Video;
	}
		
		public void generateColorMap(Item item) {
			HashMap<String,String> colorMap = new HashMap<>();
			List<DisplayColour> displayColorList = item.getDisplayColorList();
			List<ValidationProperty> validationPropertyList = item.getValidationPropertyList();
			if(validationPropertyList != null) {
				for(ValidationProperty validationProperty : validationPropertyList) {
						if(validationProperty.getColour() != null) {
							 colorMap.put(validationProperty.getValidationProperty(),"font-weight:bold;color:#"+Common.convertVbToHexStrColorCode(Integer.valueOf(validationProperty.getColour())));
						}
				}
			}
			if(displayColorList != null) {
				for(DisplayColour color : displayColorList) {
						colorMap.put(color.getId().getResponse(),"font-weight:bold;color:#"+Common.convertVbToHexStrColorCode(Integer.valueOf(color.getColour())));
				}
			}
			
			setColorMap(colorMap);
		}
		
	
	
	public void setBase64Video(String base64Video) {
		this.base64Video = base64Video;
	}

	public boolean isAudio() {
		return audio;
	}

	public void setAudio(boolean audio) {
		this.audio = audio;
	}

	public boolean isVideo() {
		return video;
	}

	public void setVideo(boolean video) {
		this.video = video;
	}

	public GenNumValid getGenNumValid() {
		return genNumValid;
	}

	public void setGenNumValid(GenNumValid genNumValid) {
		this.genNumValid = genNumValid;
	}

	public List<UomNumValid> getUOMNumValids() {
		return UOMNumValids;
	}

	public void setUOMNumValids(List<UomNumValid> uOMNumValids) {
		UOMNumValids = uOMNumValids;
	}

	public List<FormatInputDef> getFormatInputDefList() {
		return formatInputDefList;
	}

	public void setFormatInputDefList(List<FormatInputDef> formatInputDefList) {
		this.formatInputDefList = formatInputDefList;
	}

	public List<ValidationProperty> getValidationPropertyList() {
		return validationPropertyList;
	}

	public void setValidationPropertyList(List<ValidationProperty> validationPropertyList) {
		this.validationPropertyList = validationPropertyList;
	}

	public List<ValFreeTextReq> getValFreeTextReqList() {
		return valFreeTextReqList;
	}

	public void setValFreeTextReqList(List<ValFreeTextReq> valFreeTextReqList) {
		this.valFreeTextReqList = valFreeTextReqList;
	}

	public List<UnitOfMeasure> getUnitOfMeasureList() {
		return unitOfMeasureList;
	}

	public void setUnitOfMeasureList(List<UnitOfMeasure> unitOfMeasureList) {
		this.unitOfMeasureList = unitOfMeasureList;
	}

	public List<ScriptRefItem> getScriptRefItemList() {
		return scriptRefItemList;
	}

	public void setScriptRefItemList(List<ScriptRefItem> scriptRefItemList) {
		this.scriptRefItemList = scriptRefItemList;
	}

	public Integer getScriptId() {
		return scriptId;
	}

	public void setScriptId(Integer scriptId) {
		this.scriptId = scriptId;
	}

	public FormatInputDefMe getFormatInputDefMe() {
		return formatInputDefMe;
	}

	public void setFormatInputDefMe(FormatInputDefMe formatInputDefMe) {
		this.formatInputDefMe = formatInputDefMe;
	}

	public Integer getOotFlag() {
		return ootFlag;
	}

	public void setOotFlag(Integer ootFlag) {
		this.ootFlag = ootFlag;
	}

	public String getOtCol() {
		return otCol;
	}

	public void setOtCol(String otCol) {
		this.otCol = otCol;
	}

	public HashMap<String, String> getColorMap() {
		return colorMap;
	}

	public void setColorMap(HashMap<String, String> colorMap) {
		this.colorMap = colorMap;
	}

	public Integer getResultDate() {
		return resultDate;
	}

	public void setResultDate(Integer resultDate) {
		this.resultDate = resultDate;
	}

	public Integer getResultTime() {
		return resultTime;
	}

	public void setResultTime(Integer resultTime) {
		this.resultTime = resultTime;
	}

	public String getInputType() {
		return inputType;
	}

	public void setInputType(String inputType) {
		this.inputType = inputType;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public String getResponseType() {
		return responseType;
	}

	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}

	public Integer getPrecision() {
		return precision;
	}

	public void setPrecision(Integer precision) {
		this.precision = precision;
	}

	public String getFieldSize() {
		return fieldSize;
	}

	public void setFieldSize(String fieldSize) {
		this.fieldSize = fieldSize;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}
	
	public boolean isMandatorymodifierCheckBox() {
		return mandatorymodifierCheckBox;
	}

	public void setMandatorymodifierCheckBox(boolean mandatorymodifierCheckBox) {
		this.mandatorymodifierCheckBox = mandatorymodifierCheckBox;
	}

	public void setEditPanelResponse() {
		//later to be changed abstract
	}

	public String generateSaveResponse() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void setCurrentResponse() {
		
	}
	
	public boolean isResponseValid() {
		return false;
		
	}

	public String getDeltaFreeText() {
		return deltaFreeText;
	}

	public void setDeltaFreeText(String deltaFreeText) {
		this.deltaFreeText = deltaFreeText;
	}

	public String getDeltaUom() {
		return deltaUom;
	}

	public void setDeltaUom(String deltaUom) {
		this.deltaUom = deltaUom;
	}

	public Integer getiLevel() {
		return iLevel;
	}

	public void setiLevel(Integer iLevel) {
		this.iLevel = iLevel;
	}

	public boolean isHasAnsweredQuestions() {
		return hasAnsweredQuestions;
	}

	public void setHasAnsweredQuestions(boolean hasAnsweredQuestions) {
		this.hasAnsweredQuestions = hasAnsweredQuestions;
	}

	public boolean isViewPII() {
		return viewPII;
	}

	public void setViewPII(boolean viewPII) {
		this.viewPII = viewPII;
	}

	public List<ResLog> getResLogList() {
		return resLogList;
	}

	public void setResLogList(List<ResLog> resLogList) {
		this.resLogList = resLogList;
	}

	public Boolean getIsLogPresent() {
		return isLogPresent;
	}

	public void setIsLogPresent(Boolean isLogPresent) {
		this.isLogPresent = isLogPresent;
	}

	public byte[] getBlob() {
		return blob;
	}

	public void setBlob(byte[] blob) {
		this.blob = blob;
	}

	public String getBlobFileName() {
		return blobFileName;
	}

	public void setBlobFileName(String blobFileName) {
		this.blobFileName = blobFileName;
	}
	
	
}
