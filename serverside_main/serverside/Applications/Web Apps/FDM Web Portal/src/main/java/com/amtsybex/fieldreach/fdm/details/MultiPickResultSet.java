package com.amtsybex.fieldreach.fdm.details;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import org.primefaces.PrimeFaces;

import com.amtsybex.fieldreach.backend.model.SuppResults;
import com.amtsybex.fieldreach.backend.model.TaskListRes;
import com.amtsybex.fieldreach.backend.model.TaskListValues;
import com.amtsybex.fieldreach.backend.model.ValFreeTextReq;
import com.amtsybex.fieldreach.backend.model.ValidationProperty;
import com.amtsybex.fieldreach.fdm.web.jsf.util.MessageHelper;
import com.amtsybex.fieldreach.utils.impl.Common;

public class MultiPickResultSet extends ResultSet implements Serializable {

	private static final long serialVersionUID = 6374116965925986890L;

	private List<String> deltaResponse= new ArrayList<String>();

	public MultiPickResultSet(Integer id, String script, String response, String freeText, Integer resOrderNo) {
		super(id, script, response, freeText, resOrderNo);
		// this.deltaResponse = response;
	}

	public MultiPickResultSet(int id, Integer scriptId, String script, int resOrderNo, Integer sequenceNumber,
			String itemType, String inputType) {
		super(id, scriptId, script, resOrderNo, sequenceNumber, itemType, inputType);
		if (!this.getItemType().equals(Common.QUESTION_TYPE_TASK_LIST)) {
			this.setMultiPickList(true);
		}
	}

	@Override
	public void setEditPanelResponse() {
		if (this.isMandatorymodifierCheckBox()) {
			this.deltaResponse.clear();
		} else {
			this.deltaResponse.clear();
			
			if (this.getItemType().equals(Common.QUESTION_TYPE_TASK_LIST)) {
				
				if(this.getTaskListItems() != null) {
					for (TaskListRes selectedItem : this.getTaskListItems()) {
						if (selectedItem.getTask().getTaskCompleteCode().equalsIgnoreCase("TC"))
							this.deltaResponse.add(selectedItem.getTask().getTaskDesc());
					}
				}
			} else {
				this.deltaResponse.add(this.getResponse());
				
				if (this.getMultiPickListItems() != null) {
					for (SuppResults selectedItem : this.getMultiPickListItems()) {
						deltaResponse.add(selectedItem.getId().getResponse());
					}
				}
			}
		}
		
	}

	@Override
	public void setCurrentResponse() {
		
		
		if (this.getItemType().equals(Common.QUESTION_TYPE_TASK_LIST)) {
			int count = 0;
			if (this.getTaskListItems() != null) {
				
				for (TaskListRes taskItems : this.getTaskListItems()) {
					if (this.deltaResponse.contains(taskItems.getTask().getTaskDesc())) {
						taskItems.getTask().setTaskCompleteCode("TC");
						count++;
					} else {
						taskItems.getTask().setTaskCompleteCode("TI");
					}
				}
			} else {
				
				List<TaskListRes> taskListItems= new ArrayList<>();
				
				for (ValidationProperty tc : this.getValidationPropertyList()) {
				//if no taskList Items create a new one temp for session display	
					TaskListRes taskListItem = new TaskListRes();
					taskListItem.setTask(new TaskListValues());
					
					if (this.deltaResponse.contains(tc.getValidationProperty())) {		
						
						taskListItem.getTask().setTaskCompleteCode("TC");
						count++;
						
					} else {
						taskListItem.getTask().setTaskCompleteCode("TI");
						
					}
					taskListItem.getTask().setTaskDesc(tc.getValidationProperty());
					
					taskListItems.add(taskListItem);
				}
				
				this.setTaskListItems(taskListItems);
			}
			
			this.setResponse(count + " of " + this.getTaskListItems().size());
			
		}else {
			List<SuppResults> newSuppResultsList = new ArrayList<SuppResults>();
			if (!this.deltaResponse.isEmpty()) {

				this.setResponse(this.deltaResponse.get(0));
				this.deltaResponse.remove(this.getResponse());

				for (String multiPickItem : this.deltaResponse) {
					SuppResults suppRes = new SuppResults(this.getScriptId(), this.getResOrderNo(), multiPickItem);
					suppRes.setSequenceNo(this.getSequenceNo());
					newSuppResultsList.add(suppRes);
				}
			} else {
				this.setResponse(null);
			}
			this.setMultiPickListItems(newSuppResultsList);
		}
		
	}
	
	@Override
	public String generateSaveResponse() {
		String multiPickResponse = "";
		if (this.getItemType().equals(Common.QUESTION_TYPE_TASK_LIST)) {

			for (ValidationProperty tc : this.getValidationPropertyList()) {
				if (this.deltaResponse.contains(tc.getValidationProperty())) {
					multiPickResponse += Common.TASK_LIST_RESPONSE_DELIMITER + tc.getValidationProperty() + "=TC";
				} else {
					multiPickResponse += Common.TASK_LIST_RESPONSE_DELIMITER + tc.getValidationProperty() + "=TI";
				}
			}
		} else {
			for (String multiPickRes : this.deltaResponse) {
				multiPickResponse += Common.MULTI_PICK_RESPONSE_DELIMITER + multiPickRes;
			}
		}
		return multiPickResponse.replaceFirst("\\" + Common.MULTI_PICK_RESPONSE_DELIMITER, "");
	}

	@Override
	public boolean isResponseValid() {
		// TODO validation should not return error messages from objects
		// have to update in upcoming sprints
		if (!this.isMandatorymodifierCheckBox()) {

			if (this.getValFreeTextReqList() != null) {
				for (String selList : this.deltaResponse) {
					for (ValFreeTextReq reqFreeTxt : this.getValFreeTextReqList()) {
						if (reqFreeTxt.getId().getResponse().equalsIgnoreCase(selList) && (this.getDeltaFreeText() == null || this.getDeltaFreeText().isEmpty())) {

							MessageHelper.setErrorMessage("messages", "Selected response needs free text");
							FacesContext.getCurrentInstance().validationFailed();
							PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
							return false;
						}
					}
				}
			}
		}
		return true;

	}
	
	public List<String> getDeltaResponse() {
		return deltaResponse;
	}

	public void setDeltaResponse(List<String> deltaResponse) {
		this.deltaResponse = deltaResponse;
	}

}
