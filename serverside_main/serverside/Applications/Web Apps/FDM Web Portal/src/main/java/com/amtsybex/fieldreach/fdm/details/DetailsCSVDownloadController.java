package com.amtsybex.fieldreach.fdm.details;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Named;

import org.apache.deltaspike.core.api.scope.WindowScoped;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.model.Carrythrough;
import com.amtsybex.fieldreach.backend.model.CarrythroughRes;
import com.amtsybex.fieldreach.backend.model.ReturnedScripts;
import com.amtsybex.fieldreach.utils.impl.Common;

@Named
@WindowScoped
public class DetailsCSVDownloadController extends MultiViewBase implements Serializable {

	private static final long serialVersionUID = -3485142573832732078L;

	public StreamedContent downloadCSV(List<ReturnedScripts> scripts) throws FRInstanceException{
		
		super.initialise(scripts, true);

		StringBuilder csvString = new StringBuilder();
		
		DetailsBase firstDetail = this.details.get(0);
		//diaply headers and header information
		
		csvString.append("Script Id,Script Code,Script Description,Version Number,Complete Date,Complete Time,Return Id,Start Date,Start Time,Workgroup Code,Workgroup Desc,Completed User Name,Completed User Code,Return Date,Work Order Number,Work Order Description,Equip No,Equip Desc,Device Id,Result Status,AltEquip Ref,Summary Desc,Total Weight Score");
		
		List<Carrythrough>carryThrough = this.details.get(0).getCarryThroughDef();
		
		if(carryThrough != null && carryThrough.size() > 0) {
			for(Carrythrough carryItem :carryThrough ) {
				csvString.append(",");
				csvString.append(cleanString(carryItem.getId().getFieldName()));
			}
		}
		
		for(ResultSet answer : this.details.get(0).getAnswers()) {
			if(!answer.isPhotograph() && !answer.isAudio() && !answer.isBitmap() && !answer.isEndMarker() && !answer.isVideo()) {
				csvString.append(",");
				csvString.append(cleanString(answer.getScript()));
			}
		}
		
		for(DetailsBase detail : this.details) {
			
			//HEADER INFO
			
			csvString.append("\n");
			
			csvString.append(detail.getScript().getId());
			
			csvString.append(",");
			
			csvString.append(cleanString(detail.getScript().getScriptCode()));
			
			csvString.append(",");
			
			csvString.append(cleanString(detail.getScript().getScript().getScriptDesc()));
			
			csvString.append(",");
			
			csvString.append(detail.getScript().getScriptVersions().getVersionNumber());
			
			csvString.append(",");
			
			csvString.append(detail.getCompleteDate());
			
			csvString.append(",");
			
			csvString.append(detail.getCompleteTime());
			
			csvString.append(",");
			
			csvString.append(detail.getReturnId());
			
			csvString.append(",");
			
			csvString.append(detail.getScript().getStartDate());
			
			csvString.append(",");
			
			csvString.append(detail.getScript().getStartTime());
			
			csvString.append(",");
			
			csvString.append(cleanString(detail.getScript().getWorkGroup()));
			
			csvString.append(",");
			
			csvString.append((detail.getScript().getHpcUser() == null || detail.getScript().getHpcUser().getWorkGroup() == null) ? "" : cleanString(detail.getScript().getHpcUser().getWorkGroup().getWorkgroupDesc()));
			
			csvString.append(",");
			
			csvString.append(detail.getScript().getHpcUser() == null ? "" : cleanString(detail.getScript().getHpcUser().getUserName()));
			
			csvString.append(",");
			
			csvString.append(cleanString(detail.getScript().getCompleteUser()));
			
			csvString.append(",");
			
			csvString.append(detail.getScript().getReturnDate());
			
			csvString.append(",");
			
			csvString.append(cleanString(detail.getScript().getWorkOrderNo()));
			
			csvString.append(",");
			
			csvString.append(cleanString(detail.getScript().getWorkOrderDesc()));
			
			csvString.append(",");
			
			csvString.append(cleanString(detail.getScript().getEquipNo()));
			
			csvString.append(",");
			
			csvString.append(cleanString(detail.getScript().getEquipDesc()));
			
			csvString.append(",");
			
			csvString.append(cleanString(detail.getScript().getDeviceId()));
			
			csvString.append(",");
			
			csvString.append(cleanString(detail.getScript().getResultStatus()));
			
			csvString.append(",");
			
			csvString.append(cleanString(detail.getScript().getAltEquipRef()));
			
			csvString.append(",");
			
			csvString.append(cleanString(detail.getScript().getSummaryDesc()));
			
			csvString.append(",");
			
			csvString.append(detail.getScript().getTotalWeightScore() == null ? "" : detail.getScript().getTotalWeightScore());
			

			if(carryThrough != null && carryThrough.size() > 0) {
				List<CarrythroughRes> cResItems = detail.filterAdditionalInfo();
				for(Carrythrough carryItem :carryThrough ) {
					csvString.append(",");
					for(CarrythroughRes carryResItem : cResItems) {
						if(carryResItem.getId().getFieldName().equals(carryItem.getId().getFieldName())) {
							csvString.append(cleanString(carryResItem.getFieldValue()));
							break;
						}
					}
				}
			}
			
			for(ResultSet answer : detail.getAnswers()) {

				//RESPONSE INFO
				if(answer.isHeading()) {
					csvString.append(",");
				}else {
					
					if(!answer.isPhotograph() && !answer.isAudio() && !answer.isBitmap() && !answer.isEndMarker() && !answer.isVideo()) {
					
						csvString.append(",");
						if(answer.getResponseType() != null && answer.getResponseType().equals(Common.RESPONSE_TYPE_OK)) {
							
							if(answer.isViewPII()) {
								csvString.append("**********");
								
							}else if (answer.getItemType().equals(Common.QUESTION_TYPE_FORMATTED_DATA_INPUT)) {

								csvString.append(cleanString(answer.getResponse()));

							}else {
								answer.setEditPanelResponse();
								csvString.append(cleanString(answer.generateSaveResponse()));
							}
							
						}
					}
				}
			}
		}
		
		DateFormat df = new SimpleDateFormat("dd-MM-yy-HH:mm:ss");
		
		DefaultStreamedContent csvFile = DefaultStreamedContent.builder()
				.contentType("text/csv")
				.name(firstDetail.getScript().getScriptVersions().getVersionNumber() + "-" + firstDetail.getScript().getScriptCode() + "-" + df.format(new Date()) + ".csv")
				.stream(() -> new ByteArrayInputStream(csvString.toString().getBytes())).build();		
		
		return csvFile;
		
	}
	
	private String cleanString(String string) {
		if(string != null && string.length() > 0) {
			string = string.replace(",", "");
			string = string.replaceAll("\\R+", " ");
		}
		
		return string == null ? "" : string;
	}

	
}
