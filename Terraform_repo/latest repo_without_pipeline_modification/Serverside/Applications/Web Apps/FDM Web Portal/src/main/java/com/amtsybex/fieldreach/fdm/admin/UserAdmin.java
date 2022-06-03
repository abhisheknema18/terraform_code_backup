package com.amtsybex.fieldreach.fdm.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.context.FacesContext;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.deltaspike.core.api.scope.WindowScoped;
import org.primefaces.PrimeFaces;
import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.exception.MaxResultsExceededException;
import com.amtsybex.fieldreach.backend.exception.UserNotFoundException;
import com.amtsybex.fieldreach.backend.model.HPCUsers;
import com.amtsybex.fieldreach.backend.model.HPCWorkGroups;
import com.amtsybex.fieldreach.backend.model.ReturnedScripts;
import com.amtsybex.fieldreach.backend.model.SystemParameters;
import com.amtsybex.fieldreach.backend.model.ValidationProperty;
import com.amtsybex.fieldreach.fdm.Environment;
import com.amtsybex.fieldreach.fdm.PageCodebase;
import com.amtsybex.fieldreach.fdm.web.jsf.util.ManagedBeanHelper;
import com.amtsybex.fieldreach.fdm.web.jsf.util.MessageHelper;
import com.amtsybex.fieldreach.fdm.web.jsf.util.Properties;
import com.amtsybex.fieldreach.utils.impl.Common;

@Named
@WindowScoped
public class UserAdmin extends PageCodebase implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6097134770949807413L;

	private static Logger _logger = LoggerFactory.getLogger(UserAdmin.class.getName());

	List<ValidationProperty> validationProperty = null;

	private UserAdminCriteria userAdminCriteria;
	private UserAdminCriteria undoUserAdminCriteria;

	private boolean allWorkgroupsSelected;
	private boolean workgroupSelected;
	private TreeNode workgroupRoot;  
	private TreeNode[] selectedWorkgroupNodes; 
	private TreeNode selectedSingleWorkgroupNode; 
	private List<HPCWorkGroups> workGroupList;

	private List<HPCUsers> users;
	private List<HPCUsers> filteredMobileUser;

	private Boolean isEditUser;
	private HPCUsers selectedMobileUser;
	
	private HPCWorkGroups selectedMobileWorkGroups;

	private SystemParameters sysParams;
	
	private Boolean navigateWorkGroup;
	
	private String fdmGroupCode;

	public UserAdmin() {
		super();
		reset();
	}
	
	public String navigateReturn() {
		
		return "mobileWorkGroupSearchList";
	}
	
	public void searchUsersFromWorkGroup(HPCWorkGroups mobileWorkGroup) {
		
		fdmGroupCode = ManagedBeanHelper.findBean("fdmenv", Environment.class).getUser().getFdmGroupCode();
		this.navigateWorkGroup = true;
		this.selectedMobileWorkGroups = mobileWorkGroup;
		
		ArrayList<String> groupCodes = new ArrayList<String>();
		groupCodes.add(mobileWorkGroup.getId().getWorkgroupCode());
		List<HPCUsers> users = null;
		try {
			users = this.getUserService().searchUsers(null, null, null, groupCodes,fdmGroupCode);
			
		} catch (FRInstanceException e) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
		}
			this.users = users!=null?users: new ArrayList<HPCUsers>();
			this.filteredMobileUser = users;
			FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "userAdminSearchList");
	}

	public void searchUsers(){
		ArrayList<String> groupCodes=null;
		this.undoUserAdminCriteria = new UserAdminCriteria(this.userAdminCriteria);
		if (this.userAdminCriteria.getWorkgroups().size() > 0){
			groupCodes = new ArrayList<String>();
			for (HPCWorkGroups w : this.userAdminCriteria.getWorkgroups()){
				groupCodes.add(w.getWorkgroupCode());
			}
		}
		fdmGroupCode = ManagedBeanHelper.findBean("fdmenv", Environment.class).getUser().getFdmGroupCode();
		List<HPCUsers> users = null;
		try {
			users = this.getUserService().searchUsers(null, this.userAdminCriteria.getUserCode(), this.userAdminCriteria.getUserName(), groupCodes,fdmGroupCode);
			
		} catch (FRInstanceException e) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
		}
		if(users != null && users.size() > 0) {
			this.users = users;
			this.filteredMobileUser = users;
			FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "userAdminSearchList");
		}else {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_search_no_results_user_found"));
		}

	}
	
	public void EditMobileUser(HPCUsers usr) {
		initiateDropDownValues();
		this.selectedMobileUser = new HPCUsers(usr.getId().getUserCode(), usr.getId().getWorkgroupCode(), usr.getUserName(),
				usr.getAltRef(), usr.getDeviceid(), usr.getRevoked(),
				usr.getLastModDate(), usr.getUserClass(), usr.getLastModTime());
		this.selectedMobileUser.setWorkGroup(usr.getWorkGroup());
	}
	
	public void addMobileUser() {
		initiateDropDownValues();
		this.selectedMobileUser = new HPCUsers(null, null);
	}

	
	 public String saveMobileUser() {
	        
         try {
	         if(!isEditUser) {
	        	 
	        	 List<HPCUsers> users = this.getUserService().findHPCUsersByUserCode(null,this.selectedMobileUser.getId().getUserCode());
	        	 if(users != null && !users.isEmpty()) {
	        		 MessageHelper.setErrorMessage(null,this.selectedMobileUser.getId().getUserCode()+" "+Properties.get("fdm_user_already_exists_msg"));
	                 FacesContext.getCurrentInstance().validationFailed();
	                 PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
	                 return null;
	        	 }
	         }
             
             if(selectedMobileUser.getId().getUserCode()==null || selectedMobileUser.getId().getUserCode().isEmpty()) {
                 MessageHelper.setErrorMessage(null, Properties.get("fdm_user_admin_usercode_message"));
                 FacesContext.getCurrentInstance().validationFailed();
                 PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
                 return null;
             }
                     
             if(selectedMobileUser.getUserName()==null || selectedMobileUser.getUserName().isEmpty() || StringUtils.isBlank(selectedMobileUser.getUserName())) {
                 MessageHelper.setErrorMessage(null, Properties.get("fdm_user_admin_username_message"));
                 FacesContext.getCurrentInstance().validationFailed();
                 PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
                 return null;
             }
             
             if(selectedMobileUser.getWorkGroup()!=null && !selectedMobileUser.getWorkGroup().getId().getWorkgroupCode().isEmpty()) {
                 selectedMobileUser.getId().setWorkgroupCode(selectedMobileUser.getWorkGroup().getId().getWorkgroupCode());
             }
             else {
                 MessageHelper.setErrorMessage(null,Properties.get("fdm_user_admin_workgroup_message"));
                 FacesContext.getCurrentInstance().validationFailed();
                 PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
                 return null;
             }
             
             selectedMobileUser.setRevoked(selectedMobileUser.getRevokedBool() == false ? 0 : 1 );         
 			
 			HPCUsers newUsr = selectedMobileUser;
			if (isEditUser) {
				List<HPCUsers> existingUser = this.getUserService().findHPCUsersByUserCode(null, selectedMobileUser.getId().getUserCode());

				if (existingUser != null) {

					newUsr = existingUser.get(0);
					if (!newUsr.getId().equals(selectedMobileUser.getId())) {
						// Temp fix if the ID is mismatch then delete the user and save it
						// ID is mismatched if the work group is changed for the user
						this.getUserService().deleteUser(null, selectedMobileUser.getId().getUserCode());
						newUsr = selectedMobileUser;
					} else {
						newUsr.setAltRef(selectedMobileUser.getAltRef());						
						newUsr.setDeviceid(selectedMobileUser.getDeviceid());
						newUsr.setId(selectedMobileUser.getId());
						newUsr.setLastModDate(Common.generateFieldreachDBDate());
						newUsr.setLastModTime(StringUtils.leftPad(String.valueOf(Common.generateFieldreachDBTime()), 6, '0'));
						newUsr.setRevoked(selectedMobileUser.getRevoked());
						newUsr.setUserClass(selectedMobileUser.getUserClass());
						newUsr.setUserName(selectedMobileUser.getUserName());
					}

				}
			}
             
             this.getUserService().saveHPCUser(null, newUsr);
             MessageHelper.setInfoMessage(null, Properties.get("fdm_user_admin_user_saved_message"));
             if(FacesContext.getCurrentInstance().getViewRoot().getViewId().equals("/fdm/userAdminSearchList.xhtml")) {
            	 if(navigateWorkGroup) {
            		 this.searchUsersFromWorkGroup(selectedMobileWorkGroups);
            	 }else {
            		 this.searchUsers();
            	 }
            	 
                 return "userAdminSearchList";
             }
         }
         catch (FRInstanceException e) {
             MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
             FacesContext.getCurrentInstance().validationFailed();
             PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
         }
         return null;



 }

	public void deleteMobileUser() {
		try {
			this.getUserService().deleteUser(null, selectedMobileUser.getId().getUserCode());
			this.searchUsers();
		} catch (FRInstanceException e) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
		}
	}

	public void isDeleteUser(){
		List<String> deleteUserCode = new ArrayList<String>();
		List<ReturnedScripts> results;
		deleteUserCode.add(this.selectedMobileUser.getId().getUserCode());
		try {
			results = this.getScriptResultsService().getScriptResults(null, null, null, null, null,
					null, null, deleteUserCode,
					null, null);

			if(results!=null && results.size()>0) {
				MessageHelper.setErrorMessage(null, Properties.get("fdm_user_admin_result_set_exists_message"));
				FacesContext.getCurrentInstance().validationFailed();
				PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
				return;
			}
			else {
				PrimeFaces.current().executeScript("PF('deleteDialogVar').show();");
			}

		} catch (MaxResultsExceededException e) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
		} catch (FRInstanceException e) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
		}
	}

	public void initiateDropDownValues() {
		try {
			sysParams = this.getSystemParameterService().getSystemParams(null);
			validationProperty = this.getValidationTypeService().getValidationPropertyByValidationTypeWeightScoreDesc(null, sysParams.getHpcUserClass());

		} catch (FRInstanceException e) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
		}
	}


	/**
	 * method for building the tree of workgroups
	 * 
	 * @throws UserNotFoundException
	 * @throws FRInstanceException 
	 */
	public void loadSelectedWorkgroups(Boolean singleSelection) {

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
				
				if (singleSelection) {
					if (this.selectedMobileUser.getWorkGroup() != null && this.selectedMobileUser.getWorkGroup().getId().equals(w.getId())) {
						node.setSelected(true, true);
					}
				} else {
					if (this.userAdminCriteria.getWorkgroups() != null && this.userAdminCriteria.getWorkgroups().contains(w)) {
						node.setSelected(true, true);
					}
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

	public List<HPCWorkGroups> fetchWorkgroups(){

		_logger.debug(">>> fetchWorkgroups");

			try {
				fdmGroupCode = ManagedBeanHelper.findBean("fdmenv", Environment.class).getUser().getFdmGroupCode();
				workGroupList = this.getUserService().getHPCWorkGroups(null,null,null,null, fdmGroupCode);
			} catch (FRInstanceException e) {
				
				_logger.error("fetchWorkgroups Unknown error occurred" + e.getMessage());
		}

		_logger.debug("<<< fetchWorkgroups");
		return workGroupList;
	}

	/**
	 * parse the array of selected nodes into a list for the corresponding drop down menu
	 */
	public void updateSelectedWorkgroups(){

		_logger.debug(">>> updateSelectedWorkgroups");

		List<TreeNode> selectedWorkgroupNodeList = Arrays.asList(selectedWorkgroupNodes);

		userAdminCriteria.getWorkgroups().clear();

		for (TreeNode tn : selectedWorkgroupNodeList){
			// discard the parent nodes
			if (tn.getData() instanceof HPCWorkGroups)
				userAdminCriteria.getWorkgroups().add((HPCWorkGroups)tn.getData());
		}
		allWorkgroupsSelected = (userAdminCriteria.getWorkgroups()).isEmpty() ? true : false;

		_logger.debug("<<< updateSelectedWorkgroups");
	}

	public void updateSelectedSingleWorkgroup() {

		_logger.debug(">>> updateSelectedWorkgroups");

		selectedMobileUser.setWorkGroup(null);
		if(selectedSingleWorkgroupNode != null) {
			if (selectedSingleWorkgroupNode.getData() instanceof HPCWorkGroups)
				selectedMobileUser.setWorkGroup(((HPCWorkGroups) selectedSingleWorkgroupNode.getData()));
		}
		workgroupSelected = selectedMobileUser.getWorkGroup() != null ? true : false;

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
		allWorkgroupsSelected = (userAdminCriteria.getWorkgroups()).isEmpty() ? true : false;
		
		_logger.debug("<<< clearSelectedWorkgroups");
	
	}
	
	public void clearSelectedSingleWorkgroup(){
		
		_logger.debug(">>> clearSelectedWorkgroups");
		
		if(this.selectedSingleWorkgroupNode != null) {
			selectedSingleWorkgroupNode.setSelected(false);
		}

		this.selectedSingleWorkgroupNode = null;
		workgroupSelected = (userAdminCriteria.getWorkgroups()).isEmpty() ? true : false;
		
		_logger.debug("<<< clearSelectedWorkgroups");
	
	}

	public void resetSelectionAfterFilter() {

		if (this.selectedMobileUser != null) {

			if (!this.filteredMobileUser.contains(selectedMobileUser)) {
				this.filteredMobileUser.remove(selectedMobileUser);

			}
		}
	}

	
	public void reset() {

		_logger.debug(">>> reset");
		this.userAdminCriteria = new UserAdminCriteria();
		this.undoUserAdminCriteria = new UserAdminCriteria();
		this.selectedMobileUser=null;
		this.selectedSingleWorkgroupNode = null;

		selectedWorkgroupNodes = null; 
		
		allWorkgroupsSelected = true;
		
		workgroupRoot = null;  
		selectedWorkgroupNodes = null; 
		
		_logger.debug("<<< reset");
	}
	
	public void cancel() {
		this.userAdminCriteria = new UserAdminCriteria(undoUserAdminCriteria);
		this.allWorkgroupsSelected = (this.userAdminCriteria.getWorkgroups()).isEmpty() ? true : false;
	}


	public String getTitle() {
		return  Properties.get("fdm_user_admin_title");
	}
	public UserAdminCriteria getUserAdminCriteria() {
		return userAdminCriteria;
	}

	public void setUserAdminCriteria(UserAdminCriteria userAdminCriteria) {
		this.userAdminCriteria = userAdminCriteria;
	}

	public boolean isAllWorkgroupsSelected() {
		return allWorkgroupsSelected;
	}

	public void setAllWorkgroupsSelected(boolean allWorkgroupsSelected) {
		this.allWorkgroupsSelected = allWorkgroupsSelected;
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

	public List<HPCWorkGroups> getWorkGroupList() {
		return workGroupList;
	}

	public void setWorkGroupList(List<HPCWorkGroups> workGroupList) {
		this.workGroupList = workGroupList;
	}

	public List<HPCUsers> getUsers() {
		return users;
	}

	public void setUsers(List<HPCUsers> users) {
		this.users = users;
	}

	public Boolean getIsEditUser() {
		return isEditUser;
	}

	public void setIsEditUser(Boolean isEditUser) {
		this.isEditUser = isEditUser;
	}

	public HPCUsers getSelectedMobileUser() {
		return selectedMobileUser;
	}

	public void setSelectedMobileUser(HPCUsers selectedMobileUser) {
		this.selectedMobileUser = selectedMobileUser;
	}

	public TreeNode getSelectedSingleWorkgroupNode() {
		return selectedSingleWorkgroupNode;
	}

	public void setSelectedSingleWorkgroupNode(TreeNode selectedSingleWorkgroupNode) {
		this.selectedSingleWorkgroupNode = selectedSingleWorkgroupNode;
	}

	public SystemParameters getSysParams() {
		return sysParams;
	}

	public void setSysParams(SystemParameters sysParams) {
		this.sysParams = sysParams;
	}

	public boolean isWorkgroupSelected() {
		return workgroupSelected;
	}

	public void setWorkgroupSelected(boolean workgroupSelected) {
		this.workgroupSelected = workgroupSelected;
	}

	public List<ValidationProperty> getValidationProperty() {
		return validationProperty;
	}

	public void setValidationProperty(List<ValidationProperty> validationProperty) {
		this.validationProperty = validationProperty;
	}

	public List<HPCUsers> getFilteredMobileUser() {
		return filteredMobileUser;
	}

	public void setFilteredMobileUser(List<HPCUsers> filteredMobileUser) {
		this.filteredMobileUser = filteredMobileUser;
	}
	

	public Boolean getNavigateWorkGroup() {
		return navigateWorkGroup;
	}

	public void setNavigateWorkGroup(Boolean navigateWorkGroup) {
		this.navigateWorkGroup = navigateWorkGroup;
	}

	public HPCWorkGroups getSelectedMobileWorkGroups() {
		return selectedMobileWorkGroups;
	}

	public void setSelectedMobileWorkGroups(HPCWorkGroups selectedMobileWorkGroups) {
		this.selectedMobileWorkGroups = selectedMobileWorkGroups;
	}

	public UserAdminCriteria getUndoUserAdminCriteria() {
		return undoUserAdminCriteria;
	}

	public void setUndoUserAdminCriteria(UserAdminCriteria undoUserAdminCriteria) {
		this.undoUserAdminCriteria = undoUserAdminCriteria;
	}

	public String getFdmGroupCode() {
		return fdmGroupCode;
	}

	public void setFdmGroupCode(String fdmGroupCode) {
		this.fdmGroupCode = fdmGroupCode;
	}

}
