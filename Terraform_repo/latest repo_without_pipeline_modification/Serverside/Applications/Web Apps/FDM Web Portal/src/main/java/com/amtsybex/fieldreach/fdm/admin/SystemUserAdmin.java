package com.amtsybex.fieldreach.fdm.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.deltaspike.core.api.scope.WindowScoped;
import org.primefaces.PrimeFaces;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.exception.UserNotFoundException;
import com.amtsybex.fieldreach.backend.model.AccessGroups;
import com.amtsybex.fieldreach.backend.model.HPCUsers;
import com.amtsybex.fieldreach.backend.model.HPCWorkGroups;
import com.amtsybex.fieldreach.backend.model.SystemParameters;
import com.amtsybex.fieldreach.backend.model.SystemUsers;
import com.amtsybex.fieldreach.backend.model.ValidationProperty;
import com.amtsybex.fieldreach.backend.service.ValidationTypeService;
import com.amtsybex.fieldreach.fdm.PageCodebase;
import com.amtsybex.fieldreach.fdm.web.jsf.util.MessageHelper;
import com.amtsybex.fieldreach.fdm.web.jsf.util.Properties;
import com.amtsybex.fieldreach.utils.impl.Common;

@Named
@WindowScoped
public class SystemUserAdmin extends PageCodebase implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6097134770949807413L;

	private static Logger _logger = LoggerFactory.getLogger(SystemUserAdmin.class.getName());

	@Inject
	transient ValidationTypeService validationTypeService;
	
	List<ValidationProperty> validationProperty = null;

	private SystemUserAdminCriteria systemUserAdminCriteria;
	private SystemUserAdminCriteria undoSystemUserAdminCriteria;
	
	private List<AccessGroups> sbAccessGroups;
	private List<AccessGroups> fdmAccessGroups;
	private List<AccessGroups> accessGroups;

	private List<SystemUsers> users;
	private List<SystemUsers> filteredSystemUsers;

	private Boolean isEditUser;
	private SystemUsers selectedSystemUser;

	private SystemParameters sysParams;

	private List<HPCWorkGroups> workGroupList;

	private TreeNode[] selectedStatusNodes;

	private TreeNode workgroupAssignedRoot;
	private TreeNode userAssignedRoot;
	private TreeNode selectedAssignedWorkgroupNode;
	private TreeNode selectedAssigneduserNode;

	private Map<HPCWorkGroups, List<HPCUsers>> userMap;
	
	private boolean navigateAccessGroup;
	
	private AccessGroups selectedAccessGroups;

	public SystemUserAdmin() {
		super();
		reset();
	}
	
	public String navigateReturn() {
		return "accessGroupSearchResults";
	}
	
	public void cancel() {
		this.systemUserAdminCriteria = new SystemUserAdminCriteria(this.undoSystemUserAdminCriteria);
	}

	public void searchUsers() {
		
		this.undoSystemUserAdminCriteria = new SystemUserAdminCriteria(this.systemUserAdminCriteria);
		
		List<SystemUsers> users = null;
		try {
			
			this.navigateAccessGroup = false;
			
			users = this.getUserService().searchUsers(null, this.getSystemUserAdminCriteria().getUserCode(),
					this.getSystemUserAdminCriteria().getUserName());		

			if (users != null && users.size() > 0) {
	
				this.users = users;
				this.filteredSystemUsers = users;
				FacesContext.getCurrentInstance().getApplication().getNavigationHandler()
				.handleNavigation(FacesContext.getCurrentInstance(), null, "systemUserAdminSearchList");
	
			} else {
	
				MessageHelper.setGlobalWarnMessage(Properties.get("fdm_search_no_system_user_results_found"));
	
			}
		
		} catch (FRInstanceException e) {

			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));

		}

	}
	
	public void searchFromAccessGroup(AccessGroups accessGroups) {
		
		this.selectedAccessGroups = accessGroups;

		this.navigateAccessGroup = true;

		List<SystemUsers> users = null;
		try {
			
			users = this.getUserService().searchUsersByGroupCode(null, accessGroups.getGroupCode(), accessGroups.getProgCode());
			
			this.users = users != null ? users : new ArrayList<SystemUsers>();
			this.filteredSystemUsers = users;
			FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "systemUserAdminSearchList");

		} catch (FRInstanceException e) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
		}
		
	}

	public void editSystemUser(SystemUsers usr) {
		init();
		selectedSystemUser = new SystemUsers();

		this.selectedSystemUser = new SystemUsers(usr.getId(), usr.getUserName(), 
				usr.getDateAdded(), usr.getSbGroupCode(), usr.getFdmGroupCode(), usr.getRevoked(), usr.getAdminUser(),
				usr.getWinLogin(), usr.getSmGroupCode(), usr.getUserClass(),
				usr.getLastModDate(), usr.getLastModTime());

	}

	public void onSBAccessGroupChange() {
		if(selectedSystemUser.getSbGroupCode()==null || selectedSystemUser.getSbGroupCode().equals("")){
			selectedSystemUser.setWinLogin(null);
		}
	}

	public String saveSystemUser() {

		

		if (StringUtils.isBlank(selectedSystemUser.getId()) || StringUtils.isEmpty(selectedSystemUser.getId())) {

			MessageHelper.setErrorMessage(null, Properties.get("fdm_user_admin_usercode_message"));
			FacesContext.getCurrentInstance().validationFailed();
			PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
			return null;

		}

		if (StringUtils.isBlank(selectedSystemUser.getUserName()) || StringUtils.isEmpty(selectedSystemUser.getUserName())) {

			MessageHelper.setErrorMessage(null, Properties.get("fdm_user_admin_username_message"));
			FacesContext.getCurrentInstance().validationFailed();
			PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
			return null;

		}

		if(!isEditUser) {
			SystemUsers sysUser;
			try {
				sysUser = this.getUserService().getSystemUser(null, selectedSystemUser.getId());

				if(sysUser!=null) {
					MessageHelper.setErrorMessage(null, Properties.get("fdm_user_code_used_error"));
					FacesContext.getCurrentInstance().validationFailed();
					PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
					return null;
				}
			} catch (FRInstanceException e) {

				MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
				FacesContext.getCurrentInstance().validationFailed();
				_logger.error("Unknown error occurred" + e.getMessage());

			}


		}

		selectedSystemUser.setRevoked(selectedSystemUser.getRevokedBool() == false ? 0 : 1);

		selectedSystemUser.setAdminUser(selectedSystemUser.getAdminUserBool() == false ? 0 : 1);

		this.selectedSystemUser.setLastModDate(Common.generateFieldreachDBDate());

		this.selectedSystemUser.setLastModTime(StringUtils.leftPad(String.valueOf(Common.generateFieldreachDBTime()), 6, '0'));
		
		this.selectedSystemUser.setDateAdded(Common.generateFieldreachDBDate());

		
		try {
			this.getUserService().saveSystemUser(null, selectedSystemUser, null);

			MessageHelper.setInfoMessage(null, Properties.get("fdm_user_admin_user_saved_message"));

			if(FacesContext.getCurrentInstance().getViewRoot().getViewId().equals("/fdm/systemUserAdminSearchList.xhtml")) {
				if(!this.navigateAccessGroup) {
					this.searchUsers();
				}else {
					this.searchFromAccessGroup(this.selectedAccessGroups);
				}
				return "systemUserAdminSearchList";

			}
		}catch (FRInstanceException e) {

			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
			FacesContext.getCurrentInstance().validationFailed();
			_logger.error("Unknown error occurred" + e.getMessage());

		} 
		finally {
			reset();
		}
		return null;

	}
	
	public void isDeleteUser(){

		if(this.getLoggedUsername().equals(this.selectedSystemUser.getId())) {

			MessageHelper.setErrorMessage(null, Properties.get("fdm_delete_own_user"));

			FacesContext.getCurrentInstance().validationFailed();

			PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);

			return;
		}
		else {
			PrimeFaces.current().executeScript("PF('deleteDialogVar').show();");
		}

	}

	public void deleteSystemUser() {
		try {

			for(SystemUsers usr:this.users) {
				
				if(usr.getId().equals(selectedSystemUser.getId())) {
					this.getUserService().deleteSystemDbUser(null, usr);
					break;
				}
			}

			this.searchUsers();
		} catch (FRInstanceException e) {

			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
			_logger.error("Unknown error occurred" + e.getMessage());

		}
	}

	public void init() {
		List<AccessGroups> accessGroups = new ArrayList<>();
		List<AccessGroups> sbAccessGroups = new ArrayList<>();
		List<AccessGroups> fdmAccessGroups = new ArrayList<>();

		try {

			sysParams = this.getSystemParameterService().getSystemParams(null);

			validationProperty = this.validationTypeService.getValidationPropertyByValidationTypeWeightScoreDesc(null,
					sysParams.getSysUserClass());

			accessGroups = this.getUserService().getAccessGroups(null);

			for (AccessGroups accessGroup : accessGroups) {

				if (accessGroup.getProgCode().equalsIgnoreCase("SB")) {

					sbAccessGroups.add(accessGroup);

				} else if (accessGroup.getProgCode().equalsIgnoreCase("FDM")) {

					fdmAccessGroups.add(accessGroup);

				}
			}
			this.accessGroups = accessGroups;
			this.sbAccessGroups = sbAccessGroups;
			this.fdmAccessGroups = fdmAccessGroups;

		} catch (FRInstanceException e) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
		}
	}

	public void resetSelectionAfterFilter() {

		if (this.selectedSystemUser != null) {

			if (!this.filteredSystemUsers.contains(selectedSystemUser)) {
				this.filteredSystemUsers.remove(selectedSystemUser);

			}
		}
	}


	public void reset() {

		_logger.debug(">>> reset");

		this.systemUserAdminCriteria = new SystemUserAdminCriteria();
		this.undoSystemUserAdminCriteria = new SystemUserAdminCriteria();

		_logger.debug("<<< reset");
	}

	public String getTitle() {
		return  Properties.get("fdm_system_user_admin_title");
	}

	public SystemUserAdminCriteria getSystemUserAdminCriteria() {
		return systemUserAdminCriteria;
	}

	public void setSystemUserAdminCriteria(SystemUserAdminCriteria systemUserAdminCriteria) {
		this.systemUserAdminCriteria = systemUserAdminCriteria;
	}

	public List<SystemUsers> getUsers() {
		return users;
	}

	public void setUsers(List<SystemUsers> users) {
		this.users = users;
	}

	public void setFilteredMobileUser(List<SystemUsers> filteredMobileUser) {
		this.filteredSystemUsers = filteredMobileUser;
	}

	public Boolean getIsEditUser() {
		return isEditUser;
	}

	public void setIsEditUser(Boolean isEditUser) {
		this.isEditUser = isEditUser;
	}

	public SystemParameters getSysParams() {
		return sysParams;
	}

	public void setSysParams(SystemParameters sysParams) {
		this.sysParams = sysParams;
	}

	public List<ValidationProperty> getValidationProperty() {
		return validationProperty;
	}

	public void setValidationProperty(List<ValidationProperty> validationProperty) {
		this.validationProperty = validationProperty;
	}

	public List<SystemUsers> getFilteredSystemUsers() {
		return filteredSystemUsers;
	}

	public void setFilteredSystemUsers(List<SystemUsers> filteredSystemUsers) {
		this.filteredSystemUsers = filteredSystemUsers;
	}

	public SystemUsers getSelectedSystemUser() {
		return selectedSystemUser;
	}

	public void setSelectedSystemUser(SystemUsers selectedSystemUser) {
		this.selectedSystemUser = selectedSystemUser;
	}

	public List<AccessGroups> getSbAccessGroups() {
		return sbAccessGroups;
	}

	public void setSbAccessGroups(List<AccessGroups> sbAccessGroups) {
		this.sbAccessGroups = sbAccessGroups;
	}

	public List<AccessGroups> getFdmAccessGroups() {
		return fdmAccessGroups;
	}

	public void setFdmAccessGroups(List<AccessGroups> fdmAccessGroups) {
		this.fdmAccessGroups = fdmAccessGroups;
	}

	// WOrk group, user selection begins

	public List<HPCWorkGroups> getWorkGroupList() {
		return workGroupList;
	}

	public void setWorkGroupList(List<HPCWorkGroups> workGroupList) {
		this.workGroupList = workGroupList;
	}

	public TreeNode[] getSelectedStatusNodes() {
		return selectedStatusNodes;
	}

	public void setSelectedStatusNodes(TreeNode[] selectedStatusNodes) {
		this.selectedStatusNodes = selectedStatusNodes;
	}

	public TreeNode getWorkgroupAssignedRoot() {
		return workgroupAssignedRoot;
	}

	public void setWorkgroupAssignedRoot(TreeNode workgroupAssignedRoot) {
		this.workgroupAssignedRoot = workgroupAssignedRoot;
	}

	public TreeNode getUserAssignedRoot() {
		return userAssignedRoot;
	}

	public void setUserAssignedRoot(TreeNode userAssignedRoot) {
		this.userAssignedRoot = userAssignedRoot;
	}

	public TreeNode getSelectedAssigneduserNode() {
		return selectedAssigneduserNode;
	}

	public void setSelectedAssigneduserNode(TreeNode selectedAssigneduserNode) {
		this.selectedAssigneduserNode = selectedAssigneduserNode;
	}

	public List<AccessGroups> getAccessGroups() {
		return accessGroups;
	}

	public void setAccessGroups(List<AccessGroups> accessGroups) {
		this.accessGroups = accessGroups;
	}

	public Map<HPCWorkGroups, List<HPCUsers>> getUserMap() throws UserNotFoundException, FRInstanceException {
			userMap = getUserService().getFDMHPCWorkgroupToUserMap(null, getUsername(), false);
		return userMap;
	}

	public String getLoggedUsername(){
		return this.getUsername();
	}

	public void setUserMap(Map<HPCWorkGroups, List<HPCUsers>> userMap) {
		this.userMap = userMap;
	}
	// WOrk group, user selection ends

	public TreeNode getSelectedAssignedWorkgroupNode() {
		return selectedAssignedWorkgroupNode;
	}

	

	public SystemUserAdminCriteria getUndoSystemUserAdminCriteria() {
		return undoSystemUserAdminCriteria;
	}

	public void setUndoSystemUserAdminCriteria(SystemUserAdminCriteria undoSystemUserAdminCriteria) {
		this.undoSystemUserAdminCriteria = undoSystemUserAdminCriteria;
	}

	public boolean isNavigateAccessGroup() {
		return navigateAccessGroup;
	}

	public void setNavigateAccessGroup(boolean navigateAccessGroup) {
		this.navigateAccessGroup = navigateAccessGroup;
	}

	public AccessGroups getSelectedAccessGroups() {
		return selectedAccessGroups;
	}

	public void setSelectedAccessGroups(AccessGroups selectedAccessGroups) {
		this.selectedAccessGroups = selectedAccessGroups;
	}

	
}
