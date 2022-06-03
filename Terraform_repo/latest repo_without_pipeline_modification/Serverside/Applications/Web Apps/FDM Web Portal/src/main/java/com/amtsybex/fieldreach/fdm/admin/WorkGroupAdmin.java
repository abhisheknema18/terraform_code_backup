package com.amtsybex.fieldreach.fdm.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.context.FacesContext;
import javax.inject.Named;

import org.apache.deltaspike.core.api.scope.WindowScoped;
import org.primefaces.PrimeFaces;
import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.exception.UserNotFoundException;
import com.amtsybex.fieldreach.backend.model.HPCWGCat;
import com.amtsybex.fieldreach.backend.model.HPCWorkGroups;
import com.amtsybex.fieldreach.backend.model.ScriptProfiles;
import com.amtsybex.fieldreach.backend.model.SystemParameters;
import com.amtsybex.fieldreach.backend.model.ValidationProperty;
import com.amtsybex.fieldreach.backend.model.pk.HPCWorkgroupsId;
import com.amtsybex.fieldreach.fdm.Environment;
import com.amtsybex.fieldreach.fdm.PageCodebase;
import com.amtsybex.fieldreach.fdm.web.jsf.util.ManagedBeanHelper;
import com.amtsybex.fieldreach.fdm.web.jsf.util.MessageHelper;
import com.amtsybex.fieldreach.fdm.web.jsf.util.Properties;

@Named
@WindowScoped
public class WorkGroupAdmin extends PageCodebase implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2110421725331906664L;

	private static Logger _logger = LoggerFactory.getLogger(WorkGroupAdmin.class.getName());
	
	private boolean allWorkgroupsSelected;
	private boolean workgroupSelected;
	private TreeNode workgroupRoot;  
	private TreeNode[] selectedWorkgroupNodes; 
	private TreeNode selectedSingleWorkgroupNode; 
	private List<HPCWorkGroups> workGroupList;

	private List<HPCWorkGroups> mobileWorkGroupList;
	private List<HPCWorkGroups> filteredMobileWorkGroupList;
	private HPCWorkGroups selectedMobileWorkGroup;
	private HPCWorkGroups highlightedMobileWorkGroup;
	
	private SystemParameters sysParams;
	private List<ValidationProperty> validationPropertyClassA;
	private List<ValidationProperty> validationPropertyClassB;

	private Boolean editWorkGroup;
	private UserAdminCriteria workGroupSearchCriteria;
	private UserAdminCriteria undoWorkGroupSearchCriteria;
	
	private List<HPCWGCat> hpcWGCatList;
	private List<ScriptProfiles> scriptProfilesList;
	
	private String workGroupCategory;
	private HPCWGCat selectedWorkGroupCategory;
	private boolean workGroupCatEdit;
	
	private boolean disableAddWGCat;
	
	private String fdmGroupCode;

	public WorkGroupAdmin() {
		super();
		reset();
	}
	
	public void reset() {
		this.workGroupSearchCriteria = new UserAdminCriteria();
		this.undoWorkGroupSearchCriteria = new UserAdminCriteria();
		this.allWorkgroupsSelected = true;
		this.mobileWorkGroupList = new ArrayList<>();
	}
	
	public void cancel() {
		this.workGroupSearchCriteria = new UserAdminCriteria(undoWorkGroupSearchCriteria);
		allWorkgroupsSelected = (workGroupSearchCriteria.getWorkgroups()).isEmpty() ? true : false;
	}
	
		
	public void loadDropdownValues() {
		try {
			fdmGroupCode = ManagedBeanHelper.findBean("fdmenv", Environment.class).getUser().getFdmGroupCode();
			setHpcWGCatList(this.getUserService().findAllHPCWGCat(null, fdmGroupCode));
			scriptProfilesList = this.getScriptService().findAllScriptProfiles(null);

			sysParams = this.getSystemParameterService().getSystemParams(null);
			this.validationPropertyClassA = this.getValidationTypeService().getValidationPropertyByValidationTypeWeightScoreDesc(null, sysParams.getWgLookUpClassA());
			this.validationPropertyClassB = this.getValidationTypeService().getValidationPropertyByValidationTypeWeightScoreDesc(null, sysParams.getWgLookUpClassB());

		} catch (FRInstanceException e) {
			MessageHelper.setGlobalErrorMessage(Properties.get("fdm_error_unknown"));
		}
	}
	
	public void saveMobileWorkGroupCategory() {
		
		HPCWGCat newWorkGroupCat = null;
		
		for(HPCWGCat cat : this.hpcWGCatList) {
			if(cat.getWgCatDesc().equalsIgnoreCase(workGroupCategory)){
				if(workGroupCatEdit && selectedWorkGroupCategory.getWgCatDesc().equalsIgnoreCase(workGroupCategory)) {
					FacesContext.getCurrentInstance().validationFailed();
		            PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
					MessageHelper.setGlobalWarnMessage("no changes in work group category");
					return;
				}
				FacesContext.getCurrentInstance().validationFailed();
	            PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
				MessageHelper.setGlobalWarnMessage(Properties.get("fdm_work_group_category_already_exists"));
				return;
			}
			newWorkGroupCat = new HPCWGCat(workGroupCategory);
		}

		if(!workGroupCatEdit) {
			newWorkGroupCat = new HPCWGCat(workGroupCategory);
		}else {
			selectedWorkGroupCategory.setWgCatDesc(workGroupCategory);
			newWorkGroupCat = selectedWorkGroupCategory;
		}

		try {
			this.getUserService().saveHPCWGCat(null, newWorkGroupCat);
			fdmGroupCode = ManagedBeanHelper.findBean("fdmenv", Environment.class).getUser().getFdmGroupCode();
			setHpcWGCatList(this.getUserService().findAllHPCWGCat(null, fdmGroupCode));
			//MessageHelper.setGlobalInfoMessage(Properties.get("fdm_work_group_cat_saved_msg"));
		} catch (FRInstanceException e) {
			FacesContext.getCurrentInstance().validationFailed();
            PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
			MessageHelper.setGlobalErrorMessage(Properties.get("fdm_error_unknown"));
		}
	}
	
	public void getMobileWorkGroupCategory() {
		try {
			fdmGroupCode = ManagedBeanHelper.findBean("fdmenv", Environment.class).getUser().getFdmGroupCode();
			if (this.getUserService().findWGAdminByGroupCode(null, fdmGroupCode) == null) {
				disableAddWGCat = false;
			} else {
				disableAddWGCat = true;
			}
			
			setHpcWGCatList(this.getUserService().findAllHPCWGCat(null, fdmGroupCode));
		} catch (FRInstanceException e) {
			FacesContext.getCurrentInstance().validationFailed();
			PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
			MessageHelper.setGlobalErrorMessage(Properties.get("fdm_error_unknown"));
		}
	}
	
	public void addMobileWGCat() {
		this.setWorkGroupCategory("");
		this.setWorkGroupCatEdit(false);
	}
	
	public void editMobileWGCat(HPCWGCat selectedWorkGroupCategory) {
		this.setSelectedWorkGroupCategory(selectedWorkGroupCategory);
		this.workGroupCategory = selectedWorkGroupCategory.getWgCatDesc();
		this.setWorkGroupCatEdit(true);
	}
	
	public void validateDeleteMobileWorkGroupCategory(HPCWGCat selectedWorkGroupCategory) {
		this.selectedWorkGroupCategory = selectedWorkGroupCategory;
		try {
			ArrayList<String> mobilwWorkGroupList = this.getUserService().findWorkgroupsByWGCatId(null, selectedWorkGroupCategory.getIdInt());
			if (mobilwWorkGroupList != null && mobilwWorkGroupList.size() > 0) {
				FacesContext.getCurrentInstance().validationFailed();
				PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
				MessageHelper.setGlobalErrorMessage(Properties.get("fdm_work_group_category_associated_with_work_group"));
			}else if (this.getUserService().hasFDMWGPAdminForWorkGroupCat(null, selectedWorkGroupCategory.getId())) {
				FacesContext.getCurrentInstance().validationFailed();
				PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
				MessageHelper.setGlobalErrorMessage(Properties.get("fdm_work_group_category_associated_with_work_group_admin"));
			}
		} catch (FRInstanceException e) {
			FacesContext.getCurrentInstance().validationFailed();
			PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
			MessageHelper.setGlobalErrorMessage(Properties.get("fdm_error_unknown"));
		}
		
	}
	
	public void deleteMobileWorkGroupCategory() {
		try {
			this.getUserService().removeHPCWGCat(null, selectedWorkGroupCategory);
			this.hpcWGCatList.remove(selectedWorkGroupCategory);

		} catch (FRInstanceException e) {
			FacesContext.getCurrentInstance().validationFailed();
			PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
			MessageHelper.setGlobalErrorMessage(Properties.get("fdm_error_unknown"));
		}
	}
	
	public void validateSaveWorkGroup() {
		
			if (!editWorkGroup) {
				
				HPCWorkGroups workGroup = null;
				
				try {
					workGroup = this.getUserService().findHPCWorkGroup(null, this.selectedMobileWorkGroup.getWorkgroupCode().trim());
					
					if (workGroup != null) {
						if (workGroup.getWorkgroupCode().equalsIgnoreCase(this.selectedMobileWorkGroup.getId().getWorkgroupCode())) {
							FacesContext.getCurrentInstance().validationFailed();
							PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
							MessageHelper.setGlobalErrorMessage(Properties.get("fdm_group_code_exists"));
							return;
						}
					}
					
				} catch (FRInstanceException e) {
					FacesContext.getCurrentInstance().validationFailed();
					PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
					MessageHelper.setGlobalErrorMessage(Properties.get("fdm_error_unknown"));
					return;
				}
	
			}
			
			if(this.selectedMobileWorkGroup.getWgCatId() != null && this.selectedMobileWorkGroup.getWgCatId() != 0) {
				for(HPCWGCat wgcat:this.hpcWGCatList) {
					if(wgcat.getId().equals(this.selectedMobileWorkGroup.getWgCatId())) {
						this.selectedMobileWorkGroup.setHpcWgCat(wgcat);
						break;
					}					
				}
			}else {
				return ;
			}
			
			if(this.selectedMobileWorkGroup.getProfileId() != null && this.selectedMobileWorkGroup.getProfileId()>0 ) {
				for(ScriptProfiles sp:this.scriptProfilesList) {
					if(sp.getId().equals(this.selectedMobileWorkGroup.getProfileId())) {
						this.selectedMobileWorkGroup.setScriptProfiles(sp);
					}
				}
			}else {
				this.selectedMobileWorkGroup.setScriptProfiles(null);
			}
			
			saveMobileWorkGroup();
			
	}
	
	public void saveMobileWorkGroup() {
		try {
			this.getUserService().saveHPCWorkGroup(null, this.selectedMobileWorkGroup);

			if (editWorkGroup) {
				int index = this.mobileWorkGroupList.indexOf(this.highlightedMobileWorkGroup);				
				this.mobileWorkGroupList.add(index,this.selectedMobileWorkGroup);
				this.mobileWorkGroupList.remove(this.highlightedMobileWorkGroup);
				this.highlightedMobileWorkGroup = this.selectedMobileWorkGroup;
				MessageHelper.setGlobalInfoMessage(Properties.get("fdm_work_group_saved_msg"));
				FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "mobileWorkGroupSearchList");
			} else {
				this.mobileWorkGroupList.add(this.selectedMobileWorkGroup);
				MessageHelper.setGlobalInfoMessage(Properties.get("fdm_work_group_saved_msg"));
			}
			this.workGroupList = null;
		} catch (FRInstanceException e) {
			MessageHelper.setGlobalErrorMessage(Properties.get("fdm_error_unknown"));
		}
	}
	
	public void confirmDeleteWorkGroup() {
		
		try {			
			
			if (this.getUserService().hasUsersForWorkgroups(null,this.selectedMobileWorkGroup.getId().getWorkgroupCode())) {
				MessageHelper.setGlobalWarnMessage(Properties.get("fdm_work_group_associated_with_users"));
				
			} else if (this.getScriptResultsService().hasScriptResultsForWorkGroup(null, this.selectedMobileWorkGroup.getId().getWorkgroupCode())) {
				MessageHelper.setGlobalWarnMessage(Properties.get("fdm_work_group_associated_with_results"));
				
			} else if (this.getWorkOrderController().hasWorkOrdersForWorkGroup(null, this.selectedMobileWorkGroup.getId().getWorkgroupCode())) {
				MessageHelper.setGlobalWarnMessage(Properties.get("fdm_work_group_associated_with_workOrders"));
				
			} else if (this.getUserService().hasFDMWGPAccessForWorkGroup(null, this.selectedMobileWorkGroup.getId().getWorkgroupCode())) {
				MessageHelper.setGlobalWarnMessage(Properties.get("fdm_work_group_associated_with_system_access"));
				
			} else {
				PrimeFaces.current().executeScript("PF('deleteWGDialogVar').show();");
			}
			
		} catch (FRInstanceException e) {
			FacesContext.getCurrentInstance().validationFailed();
            PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
			MessageHelper.setGlobalErrorMessage(Properties.get("fdm_error_unknown"));
		}
	}
	
	public void deleteMobileWorkGroup() {
		try {
			this.getUserService().removeHPCWorkGroup(null, this.highlightedMobileWorkGroup);
			this.mobileWorkGroupList.remove(this.highlightedMobileWorkGroup);
			this.workGroupList = null;
		} catch (FRInstanceException e) {
			FacesContext.getCurrentInstance().validationFailed();
            PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
			MessageHelper.setGlobalErrorMessage(Properties.get("fdm_work_group_delete_error"));
		}
	}
	
	public void editMobileWorkGroup(HPCWorkGroups workGroup) {
		this.loadDropdownValues();
		this.selectedMobileWorkGroup =  new HPCWorkGroups(workGroup.getWorkgroupCode(),workGroup.getWgCatId(),
				workGroup.getWorkgroupDesc(),workGroup.getEmailTo(),workGroup.getEmailCC(),workGroup.getCsv(),
				workGroup.getActive(),workGroup.getWgClassA(),workGroup.getWgClassB(),workGroup.getProfileId());
		this.selectedMobileWorkGroup.setHpcWgCat(workGroup.getHpcWgCat());
		this.selectedMobileWorkGroup.setScriptProfiles(workGroup.getScriptProfiles() != null ? workGroup.getScriptProfiles() : new ScriptProfiles());
		
		this.setHighlightedMobileWorkGroup(workGroup);
	}
	
	public void addMobileWorkGroup() {
		this.loadDropdownValues();
		this.selectedMobileWorkGroup =  new HPCWorkGroups();
		this.selectedMobileWorkGroup.setId(new HPCWorkgroupsId());
		this.selectedMobileWorkGroup.setHpcWgCat(new HPCWGCat());
		this.selectedMobileWorkGroup.setScriptProfiles(new ScriptProfiles());
	}

	public String searchMobileWorkGroup() {
		
		this.undoWorkGroupSearchCriteria = new UserAdminCriteria(this.workGroupSearchCriteria);
		
		ArrayList<String> groupCodes=null;
		if (this.workGroupSearchCriteria.getWorkgroups().size() > 0){
			groupCodes = new ArrayList<String>();
			for (HPCWorkGroups w : this.workGroupSearchCriteria.getWorkgroups()){
				groupCodes.add(w.getWorkgroupCode());
			}
		}
		fdmGroupCode = ManagedBeanHelper.findBean("fdmenv", Environment.class).getUser().getFdmGroupCode();
		try {
			mobileWorkGroupList = this.getUserService().getHPCWorkGroups(null,this.workGroupSearchCriteria.getWorkGroupCode(),this.workGroupSearchCriteria.getWorkGroupDesc(),groupCodes, fdmGroupCode);
			if(mobileWorkGroupList == null ||  mobileWorkGroupList.isEmpty()) {
				MessageHelper.setGlobalWarnMessage(Properties.get("fdm_work_group_not_found"));
				FacesContext.getCurrentInstance().validationFailed();
	            PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
				return null;
			}
		} catch (FRInstanceException e) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
			FacesContext.getCurrentInstance().validationFailed();
            PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
			return null;
		}
		PrimeFaces.current().multiViewState().clearAll();
		return "mobileWorkGroupSearchList";
	}
	
	
	
	public void resetMobileWorkGroupAfterFilter() {

		if (this.mobileWorkGroupList != null) {

			if (!this.filteredMobileWorkGroupList.contains(selectedMobileWorkGroup)) {
				this.filteredMobileWorkGroupList.remove(selectedMobileWorkGroup);

			}
		}
	}
	
	/**
	 * method for building the tree of workgroups
	 * 
	 * @throws UserNotFoundException
	 * @throws FRInstanceException 
	 */
	public void loadSelectedWorkgroups() {

		_logger.debug(">>> loadSelectedWorkgroups");

		try {
			//if (workgroupRoot == null){
			workgroupRoot = new CheckboxTreeNode("Root", null);

			// make a map for the workgroup categories
			Map<Integer, CheckboxTreeNode> workgroupCats = new TreeMap<Integer, CheckboxTreeNode>();

			List<HPCWorkGroups> workgroupList = fetchWorkgroups();

			for (HPCWorkGroups w : workgroupList){

				// check if we have created a parent node for the current workgroup's category
				CheckboxTreeNode cat = workgroupCats.get(w.getWgCatId());

				// if not, create a new parent node for the current workgroup's category
				if (cat == null){
					cat = new CheckboxTreeNode(w.getHpcWgCat().getWgCatDesc(), workgroupRoot);
					cat.setExpanded(true);
					workgroupCats.put(w.getWgCatId(), cat);
				}

				CheckboxTreeNode node = new CheckboxTreeNode(w, cat); 
				if(this.workGroupSearchCriteria.getWorkgroups() != null && this.workGroupSearchCriteria.getWorkgroups().contains(w)) {
					node.setSelected(true,true);
				}
			}
			//}
			//tempWorkgroupNodes = selectedWorkgroupNodes;
		}catch(Exception e) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
			_logger.error("loadSelectedWorkgroups Unknown error occurred" + e.getMessage());
		}

		_logger.debug("<<< loadSelectedWorkgroups");

	}


	public List<HPCWorkGroups> fetchWorkgroups() throws UserNotFoundException, FRInstanceException {

		_logger.debug(">>> fetchWorkgroups");

		if (workGroupList == null) {
			fdmGroupCode = ManagedBeanHelper.findBean("fdmenv", Environment.class).getUser().getFdmGroupCode();
			workGroupList = this.getUserService().getHPCWorkGroups(null,null,null,null, fdmGroupCode);

		}

		_logger.debug("<<< fetchWorkgroups");
		return workGroupList;
	}
	
	public void updateSelectedWorkgroups(){

		_logger.debug(">>> updateSelectedWorkgroups");

		List<TreeNode> selectedWorkgroupNodeList = Arrays.asList(selectedWorkgroupNodes);

		workGroupSearchCriteria.getWorkgroups().clear();

		for (TreeNode tn : selectedWorkgroupNodeList){
			// discard the parent nodes
			if (tn.getData() instanceof HPCWorkGroups)
				workGroupSearchCriteria.getWorkgroups().add((HPCWorkGroups)tn.getData());
		}
		allWorkgroupsSelected = (workGroupSearchCriteria.getWorkgroups()).isEmpty() ? true : false;

		_logger.debug("<<< updateSelectedWorkgroups");
	}
	
	public void clearSelectedWorkgroups(){
		
		_logger.debug(">>> clearSelectedWorkgroups");
		
		if(this.selectedWorkgroupNodes != null) {
			for (TreeNode tn : this.selectedWorkgroupNodes){
				tn.setSelected(false);
			}
		}

		this.selectedWorkgroupNodes = null;
		allWorkgroupsSelected = (workGroupSearchCriteria.getWorkgroups()).isEmpty() ? true : false;
		
		_logger.debug("<<< clearSelectedWorkgroups");
	
	}
	
	public String getTitle() {
		return Properties.get("fdm_mobile_work_group_search_results_label");
	}

	public List<HPCWorkGroups> getMobileWorkGroupList() {
		return mobileWorkGroupList;
	}

	public void setMobileWorkGroupList(List<HPCWorkGroups> mobileWorkGroupList) {
		this.mobileWorkGroupList = mobileWorkGroupList;
	}

	public List<HPCWorkGroups> getFilteredMobileWorkGroupList() {
		return filteredMobileWorkGroupList;
	}

	public void setFilteredMobileWorkGroupList(List<HPCWorkGroups> filteredMobileWorkGroupList) {
		this.filteredMobileWorkGroupList = filteredMobileWorkGroupList;
	}

	public HPCWorkGroups getSelectedMobileWorkGroup() {
		return selectedMobileWorkGroup;
	}

	public void setSelectedMobileWorkGroup(HPCWorkGroups selectedMobileWorkGroup) {
		this.selectedMobileWorkGroup = selectedMobileWorkGroup;
	}

	public Boolean getEditWorkGroup() {
		return editWorkGroup;
	}

	public void setEditWorkGroup(Boolean editWorkGroup) {
		this.editWorkGroup = editWorkGroup;
	}

	public UserAdminCriteria getWorkGroupSearchCriteria() {
		return workGroupSearchCriteria;
	}

	public void setWorkGroupSearchCriteria(UserAdminCriteria workGroupSearchCriteria) {
		this.workGroupSearchCriteria = workGroupSearchCriteria;
	}

	public boolean isAllWorkgroupsSelected() {
		return allWorkgroupsSelected;
	}

	public void setAllWorkgroupsSelected(boolean allWorkgroupsSelected) {
		this.allWorkgroupsSelected = allWorkgroupsSelected;
	}

	public boolean isWorkgroupSelected() {
		return workgroupSelected;
	}

	public void setWorkgroupSelected(boolean workgroupSelected) {
		this.workgroupSelected = workgroupSelected;
	}

	public TreeNode getWorkgroupRoot() {
		return workgroupRoot;
	}

	public void setWorkgroupRoot(TreeNode workgroupRoot) {
		this.workgroupRoot = workgroupRoot;
	}

	public TreeNode[] getSelectedWorkgroupNodes() {
		return selectedWorkgroupNodes;
	}

	public void setSelectedWorkgroupNodes(TreeNode[] selectedWorkgroupNodes) {
		this.selectedWorkgroupNodes = selectedWorkgroupNodes;
	}

	public TreeNode getSelectedSingleWorkgroupNode() {
		return selectedSingleWorkgroupNode;
	}

	public void setSelectedSingleWorkgroupNode(TreeNode selectedSingleWorkgroupNode) {
		this.selectedSingleWorkgroupNode = selectedSingleWorkgroupNode;
	}

	public List<HPCWorkGroups> getWorkGroupList() {
		return workGroupList;
	}

	public void setWorkGroupList(List<HPCWorkGroups> workGroupList) {
		this.workGroupList = workGroupList;
	}

	public List<HPCWGCat> getHpcWGCatList() {
		return hpcWGCatList;
	}

	public void setHpcWGCatList(List<HPCWGCat> hpcWGCatList) {
		this.hpcWGCatList = hpcWGCatList;
	}

	public List<ScriptProfiles> getScriptProfilesList() {
		return scriptProfilesList;
	}

	public void setScriptProfilesList(List<ScriptProfiles> scriptProfilesList) {
		this.scriptProfilesList = scriptProfilesList;
	}

	public List<ValidationProperty> getValidationPropertyClassA() {
		return validationPropertyClassA;
	}

	public void setValidationPropertyClassA(List<ValidationProperty> validationPropertyClassA) {
		this.validationPropertyClassA = validationPropertyClassA;
	}

	public List<ValidationProperty> getValidationPropertyClassB() {
		return validationPropertyClassB;
	}

	public void setValidationPropertyClassB(List<ValidationProperty> validationPropertyClassB) {
		this.validationPropertyClassB = validationPropertyClassB;
	}

	public HPCWorkGroups getHighlightedMobileWorkGroup() {
		return highlightedMobileWorkGroup;
	}

	public void setHighlightedMobileWorkGroup(HPCWorkGroups highlightedMobileWorkGroup) {
		this.highlightedMobileWorkGroup = highlightedMobileWorkGroup;
	}

	public String getWorkGroupCategory() {
		return workGroupCategory;
	}

	public void setWorkGroupCategory(String workGroupCategory) {
		this.workGroupCategory = workGroupCategory;
	}


	public UserAdminCriteria getUndoWorkGroupSearchCriteria() {
		return undoWorkGroupSearchCriteria;
	}

	public void setUndoWorkGroupSearchCriteria(UserAdminCriteria undoWorkGroupSearchCriteria) {
		this.undoWorkGroupSearchCriteria = undoWorkGroupSearchCriteria;
	}

	public HPCWGCat getSelectedWorkGroupCategory() {
		return selectedWorkGroupCategory;
	}

	public void setSelectedWorkGroupCategory(HPCWGCat selectedWorkGroupCategory) {
		this.selectedWorkGroupCategory = selectedWorkGroupCategory;
	}

	public boolean isWorkGroupCatEdit() {
		return workGroupCatEdit;
	}

	public void setWorkGroupCatEdit(boolean workGroupCatEdit) {
		this.workGroupCatEdit = workGroupCatEdit;
	}

	public String getFdmGroupCode() {
		return fdmGroupCode;
	}

	public void setFdmGroupCode(String fdmGroupCode) {
		this.fdmGroupCode = fdmGroupCode;
	}

	public boolean isDisableAddWGCat() {
		return disableAddWGCat;
	}

	public void setDisableAddWGCat(boolean disableAddWGCat) {
		this.disableAddWGCat = disableAddWGCat;
	}

}
