package com.amtsybex.fieldreach.fdm.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.deltaspike.core.api.scope.WindowScoped;
import org.primefaces.PrimeFaces;
import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.exception.UserNotFoundException;
import com.amtsybex.fieldreach.backend.model.AccessGroups;
import com.amtsybex.fieldreach.backend.model.FDMResEdit;
import com.amtsybex.fieldreach.backend.model.FDMResEditStatus;
import com.amtsybex.fieldreach.backend.model.FDMScriptAccess;
import com.amtsybex.fieldreach.backend.model.FDMWGPAccess;
import com.amtsybex.fieldreach.backend.model.FDMWGPAdmin;
import com.amtsybex.fieldreach.backend.model.GroupFunctions;
import com.amtsybex.fieldreach.backend.model.HPCWGCat;
import com.amtsybex.fieldreach.backend.model.HPCWorkGroups;
import com.amtsybex.fieldreach.backend.model.Script;
import com.amtsybex.fieldreach.backend.model.ScriptCategory;
import com.amtsybex.fieldreach.backend.model.ScriptStatusDef;
import com.amtsybex.fieldreach.backend.model.SystemFunctions;
import com.amtsybex.fieldreach.backend.model.SystemUsers;
import com.amtsybex.fieldreach.backend.service.AccessGroupService;
import com.amtsybex.fieldreach.fdm.Environment;
import com.amtsybex.fieldreach.fdm.PageCodebase;
import com.amtsybex.fieldreach.fdm.web.jsf.util.ManagedBeanHelper;
import com.amtsybex.fieldreach.fdm.web.jsf.util.MessageHelper;
import com.amtsybex.fieldreach.fdm.web.jsf.util.Properties;

@Named
@WindowScoped
public class AccessGroup extends PageCodebase implements Serializable {

	private static final long serialVersionUID = 3803454984103252406L;

	private static Logger _logger = LoggerFactory.getLogger(AccessGroup.class.getName());

	@Inject
	transient AccessGroupService accessGroupService;

	private List<AccessGroups> accessGroupsList;

	private List<AccessGroups> filteredAccessGroupsList;

	private AccessGroups selectedAccessGroup;
	private AccessGroups highlightedAccessGroup;

	private List<SystemFunctions> systemFunctions;

	private List<String> groupFunctionsList;

	// To be moved to criteria search
	private String selectedProgCode;
	private String selectedGroupCode;
	private String selectedGroupDesc;

	private boolean accessGroupEdit;

	// Access tab Variables
	private boolean allScriptsSelected;
	private boolean allWorkgroupsSelected;
	private List<Script> scripts;
	private List<HPCWorkGroups> workgroups;

	private List<HPCWGCat> workGroupCat;
	private List<HPCWGCat> workGroupCatList;
	private boolean allWorkgroupCatSelected;
	private List<HPCWGCat> selectedWorkGroupCatList;

	private TreeNode scriptRoot;
	private TreeNode[] selectedScriptNodes;

	private TreeNode scriptStatusRoot;

	private TreeNode selectedScriptStatusRoot;
	private TreeNode[] selectedScriptStatusNodes;

	private TreeNode workgroupRoot;
	private TreeNode[] selectedWorkgroupNodes;

	private int accessGroupActiveTab;

	private String resultEdit;

	private List<AccessGroups> allAccessGroups;

	private Set<FDMResEditStatus> fdmResEditStatusList;

	private boolean isSearchCriteria;

	private boolean workGroupAdmin;

	public boolean isAllScriptsSelected() {
		if (this.scripts != null && !this.scripts.isEmpty())
			return false;
		else
			return true;
	}

	public void setAllScriptsSelected(boolean allScriptsSelected) {
		this.allScriptsSelected = allScriptsSelected;
	}

	public boolean isAllWorkgroupsSelected() {
		if (this.workgroups != null && !this.workgroups.isEmpty())
			return false;
		else
			return true;
	}

	public void setAllWorkgroupsSelected(boolean allWorkgroupsSelected) {
		this.allWorkgroupsSelected = allWorkgroupsSelected;
	}

	public List<Script> getScripts() {
		if (scripts == null) {
			scripts = new ArrayList<Script>();
		}
		return scripts;
	}

	public void setScripts(List<Script> scripts) {
		this.scripts = scripts;
	}

	public List<HPCWorkGroups> getWorkgroups() {
		if (workgroups == null) {
			workgroups = new ArrayList<HPCWorkGroups>();
		}
		return workgroups;
	}

	public void setWorkgroups(List<HPCWorkGroups> workgroups) {
		this.workgroups = workgroups;
	}

	// End of Access tab Variables

	public AccessGroup() {

	}

	public String getTitle() {
		return Properties.get("fdm_access_group_search_results_label");
	}

	public String searchAccessGroups() {

		this.isSearchCriteria = false;
		try {
			accessGroupsList = accessGroupService.getAccessGroupsByCriteria(null, null, null, null);
		} catch (FRInstanceException e) {
			MessageHelper.setGlobalErrorMessage(Properties.get("fdm_error_unknown"));
		}
		this.filteredAccessGroupsList = accessGroupsList;
		if (accessGroupsList != null && accessGroupsList.size() > 0)
			return "accessGroupSearchResults";

		return null;
	}

	public String searchAccessGroupsCriteria() {
		this.isSearchCriteria = true;
		try {
			List<AccessGroups> accGroups = accessGroupService.getAccessGroupsByCriteria(null, selectedProgCode,
					selectedGroupCode, selectedGroupDesc);

			if (accGroups != null && accGroups.size() > 0) {
				this.accessGroupsList = accGroups;
				this.filteredAccessGroupsList = accessGroupsList;
				return "accessGroupSearchResults";

			} else {
				MessageHelper.setErrorMessage(null, "No Access Groups Found");
				FacesContext.getCurrentInstance().validationFailed();
				PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
				return null;
			}

		} catch (FRInstanceException e) {
			MessageHelper.setGlobalErrorMessage(Properties.get("fdm_error_unknown"));
		}
		return null;

	}

	public void reset() {
		selectedProgCode = "";
		selectedGroupCode = "";
		selectedGroupDesc = "";
	}

	public void resetSelectionAfterFilter() {

		if (this.highlightedAccessGroup != null) {

			if (!this.filteredAccessGroupsList.contains(highlightedAccessGroup)) {
				this.filteredAccessGroupsList.remove(highlightedAccessGroup);

			}
		}
	}

	public void editAccessGroup(AccessGroups selectedAccessGroup) {
		this.selectedAccessGroup = new AccessGroups(selectedAccessGroup);
		accessGroupEdit = true;
		loadAccessGroupEditPanel();
	}

	public void addAccessGroup() {
		this.selectedAccessGroup = new AccessGroups();
		this.selectedAccessGroup.setAllMappings();
		this.selectedAccessGroup.setProgCode("FDM");
		accessGroupEdit = false;
		loadAccessGroupEditPanel();
	}

	public void loadAccessGroupEditPanel() {
		systemFunctions = new ArrayList<SystemFunctions>();
		workGroupCat = new ArrayList<HPCWGCat>();
		groupFunctionsList = new ArrayList<String>();
		scripts = new ArrayList<Script>();
		workgroups = new ArrayList<HPCWorkGroups>();
		try {
			if (this.selectedAccessGroup.getHpcWgpAdmin()!=null && this.selectedAccessGroup.getHpcWgpAdmin()== 1) {
				this.workGroupAdmin = true;
			}
			else {
				this.workGroupAdmin = false;
			}
			allAccessGroups = accessGroupService.getAccessGroupsByCriteria(null, null, null, null);
			this.systemFunctions = accessGroupService.getSystemFunctionsByProgCode(null,
					accessGroupEdit ? selectedAccessGroup.getProgCode() : "FDM");

			this.resultEdit = "NE";
			if (this.selectedAccessGroup.getFdmResEdit() != null) {
				for (FDMResEdit resEdit : this.selectedAccessGroup.getFdmResEdit()) {
					this.setResultEdit(resEdit.getId().getEditAccessCode());
					break;
				}
			}

			if (this.selectedAccessGroup != null) {
				for (GroupFunctions gf : this.selectedAccessGroup.getGroupFunctions()) {
					if (gf.getId() != null) {
						groupFunctionsList.add(gf.getId().getFunctionCode());
					}
				}

				for (FDMWGPAccess accessWg : this.selectedAccessGroup.getFdmWGPAccess()) {
					if (accessWg.getId() != null) {

						workgroups.add(accessWg.getHpcWorkGroups());
					}
				}

				for (FDMScriptAccess accessScripts : this.selectedAccessGroup.getFdmScriptAccess()) {
					if (accessScripts.getId() != null) {

						scripts.add(accessScripts.getScript());
					}
				}

				for (FDMWGPAdmin wgA : this.selectedAccessGroup.getFdmWGPAdmin()) {
					if (wgA.getId() != null) {

						workGroupCat.add(wgA.getHpcWgCat());
					}
				}
				this.fdmResEditStatusList = selectedAccessGroup.getFdmResEditStatus();
				// to load script status in tree structure
				loadSelectedScriptStatusRoot(false);

			}

		} catch (FRInstanceException e) {
			MessageHelper.setGlobalErrorMessage(Properties.get("fdm_error_unknown"));
		}

	}

	public void onChangeProgCode() {
		this.setAccessGroupActiveTab(0);
		this.workGroupAdmin = false;
		this.workgroups = null;
		this.scripts = null;
		this.workGroupCat = null;
		this.resultEdit = "NE";
		this.setSelectedScriptStatusRoot(new CheckboxTreeNode("Root", null));

		this.fdmResEditStatusList = new HashSet<FDMResEditStatus>();
		try {
			this.systemFunctions = accessGroupService.getSystemFunctionsByProgCode(null,
					selectedAccessGroup.getProgCode());
		} catch (FRInstanceException e) {
			MessageHelper.setGlobalErrorMessage(Properties.get("fdm_error_unknown"));
		}
	}

	public String saveAccessGroups() {

		if (this.selectedAccessGroup != null) {

			try {
				if (!accessGroupEdit) {
					this.highlightedAccessGroup = new AccessGroups();
					this.highlightedAccessGroup.setAllMappings();
				}

				String groupCode = this.selectedAccessGroup.getGroupCode();
				String previousGroupCode = this.highlightedAccessGroup.getGroupCode();

				for (AccessGroups acc : allAccessGroups) {
					if (acc.getGroupCode().equalsIgnoreCase(groupCode)
							&& !acc.getGroupCode().equalsIgnoreCase(getHighlightedAccessGroup().getGroupCode())) {
						MessageHelper.setGlobalWarnMessage(
								Properties.get("fdm_access_group_existing_group_code_error_label"));
						FacesContext.getCurrentInstance().validationFailed();
						PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
						return null;
					}
				}

				if (!groupCode.equalsIgnoreCase(previousGroupCode)) {
					// creating a new obj if groupcode is changed
					this.selectedAccessGroup.setGroupCode(previousGroupCode);
					this.highlightedAccessGroup = new AccessGroups();
					this.highlightedAccessGroup.setAllMappings();
				}

				this.highlightedAccessGroup.setProgCode(this.selectedAccessGroup.getProgCode());
				this.highlightedAccessGroup.setGroupCode(groupCode);
				this.highlightedAccessGroup.setGroupDesc(this.selectedAccessGroup.getGroupDesc());
				this.highlightedAccessGroup.setHpcWgpAdmin(this.workGroupAdmin?1:0);

				this.highlightedAccessGroup.getGroupFunctions().clear();
				for (String gf : this.groupFunctionsList) {
					GroupFunctions gfAdd = new GroupFunctions(groupCode, gf);

					this.highlightedAccessGroup.getGroupFunctions().add(gfAdd);
				}
				if (!this.highlightedAccessGroup.getProgCode().equalsIgnoreCase("SB")) {

					this.highlightedAccessGroup.getFdmWGPAccess().clear();
					if (this.workgroups != null) {
						for (HPCWorkGroups wg : this.workgroups) {
							FDMWGPAccess wgAccess = new FDMWGPAccess(groupCode, wg.getWorkgroupCode());
							wgAccess.setHpcWorkGroups(wg);
							this.highlightedAccessGroup.getFdmWGPAccess().add(wgAccess);
						}
					}

					if (this.scripts != null) {
						this.highlightedAccessGroup.getFdmScriptAccess().clear();
						for (Script script : this.scripts) {
							FDMScriptAccess scriptAdd = new FDMScriptAccess(groupCode, script.getId());
							scriptAdd.setScript(script);
							this.highlightedAccessGroup.getFdmScriptAccess().add(scriptAdd);
						}
					}

					this.highlightedAccessGroup.getFdmResEditStatus().clear();
					if (fdmResEditStatusList != null) {
						for (FDMResEditStatus fdmScriptStatus : this.fdmResEditStatusList) {
							fdmScriptStatus.getId().setGroupCode(groupCode);
							this.highlightedAccessGroup.getFdmResEditStatus().add(fdmScriptStatus);
						}
					}

					this.highlightedAccessGroup.getFdmWGPAdmin().clear();
					if (this.workGroupCat != null) {
						for (HPCWGCat wgCat : this.workGroupCat) {
							FDMWGPAdmin fdmWGPAdmin = new FDMWGPAdmin(groupCode, wgCat.getId());
							fdmWGPAdmin.setHpcWgCat(wgCat);
							this.highlightedAccessGroup.getFdmWGPAdmin().add(fdmWGPAdmin);
						}
					}

					this.highlightedAccessGroup.getFdmResEdit().clear();
					this.highlightedAccessGroup.getFdmResEdit().add(new FDMResEdit(groupCode, resultEdit));
				}
				if (!groupCode.equalsIgnoreCase(previousGroupCode) && accessGroupEdit) {
					this.accessGroupService.deleteAccessGroups(null, this.selectedAccessGroup);
					groupCode = previousGroupCode;
				}
					this.accessGroupService.saveAccessGroups(null, this.highlightedAccessGroup);

				MessageHelper.setGlobalInfoMessage("Access Group Saved Successfully");
				
				String userGroupCode = ManagedBeanHelper.findBean("fdmenv", Environment.class).getUser().getFdmGroupCode();

				if(userGroupCode.equals(groupCode)) {
					if(this.highlightedAccessGroup.getHpcWgpAdmin().equals(1)) {
						ManagedBeanHelper.findBean("fdmenv", Environment.class).setViewWorkGroupAdmin(true);
					}
					else {
						ManagedBeanHelper.findBean("fdmenv", Environment.class).setViewWorkGroupAdmin(false); 
					}
				}
				
				
				// newly added access Group will be added to the search list if the page is
				// already opened
				if (FacesContext.getCurrentInstance().getViewRoot().getViewId()
						.equals("/fdm/accessGroupSearchResults.xhtml")) {

					if (isSearchCriteria) {
						this.searchAccessGroupsCriteria();
					} else {
						this.searchAccessGroups();
					}

					// FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(),
					// null, "accessGroupSearchResults");
					return "accessGroupSearchResults";
				}

			} catch (FRInstanceException e) {
				MessageHelper.setGlobalErrorMessage(Properties.get("fdm_error_unknown"));
				FacesContext.getCurrentInstance().validationFailed();
				PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
			}

		} else {

			MessageHelper.setGlobalErrorMessage(Properties.get("fdm_error_unknown"));
			FacesContext.getCurrentInstance().validationFailed();
			PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);

		}

		return null;
	}

	public void deleteAccessGroup() {
		try {
			this.accessGroupService.deleteAccessGroups(null, this.selectedAccessGroup);

			this.accessGroupsList.remove(this.highlightedAccessGroup);
			FacesContext.getCurrentInstance().getApplication().getNavigationHandler()
					.handleNavigation(FacesContext.getCurrentInstance(), null, "accessGroupSearchResults");

		} catch (FRInstanceException e) {
			MessageHelper.setGlobalErrorMessage(Properties.get("fdm_error_unknown"));
			FacesContext.getCurrentInstance().validationFailed();
			PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
		}
	}

	public void loadSelectedScripts() {

		_logger.debug(">>> loadSelectedScripts");

		try {
			this.setScriptRoot(new CheckboxTreeNode("Root", null));

			// make a map for the script categories
			Map<Integer, CheckboxTreeNode> scriptCats = new TreeMap<Integer, CheckboxTreeNode>();

			List<Script> userScripts = fetchScripts();

			// for every script
			for (Script s : userScripts) {

				// check if we have created a parent node for the current script's category
				CheckboxTreeNode cat = scriptCats.get(s.getScriptCategory().getId());

				// if not, create a new parent node for the current script's category
				if (cat == null) {
					cat = new CheckboxTreeNode(s.getScriptCategory(), this.getScriptRoot());
					cat.setExpanded(true);
					scriptCats.put(s.getScriptCategory().getId(), cat);
				}

				// create a node for the current script, parenting it to the node we just
				// retrieved/created
				CheckboxTreeNode node = new CheckboxTreeNode(s, cat);

				if (this.getScripts() != null) {
					for (Script script : this.getScripts()) {
						if (script.getScriptCode().equalsIgnoreCase(s.getScriptCode())) {
							node.setSelected(true, true);
							break;
						}

					}
				}

			}
		} catch (Exception e) {
			MessageHelper.setGlobalErrorMessage(Properties.get("fdm_error_unknown"));
			_logger.error("loadSelectedScripts Unknown error occurred" + e.getMessage());
		}

		_logger.debug("<<< loadSelectedScripts");

	}

	public List<Script> fetchScripts() throws UserNotFoundException, FRInstanceException {
		return getScriptService().findAllScripts(null);
	}

	/**
	 * parse the array of selected nodes into a list for the corresponding drop down
	 * menu
	 */
	public void updateSelectedScripts() {

		_logger.debug(">>> updateSelectedScripts");

		this.getScripts().clear();

		for (TreeNode tn : this.getSelectedScriptNodes()) {
			// discard the parent nodes
			if (tn.getData() instanceof Script) {
				Script script = (Script) tn.getData();
				this.getScripts().add(script);
			}
		}

		loadSelectedScriptStatusRoot(true);
		_logger.debug("<<< updateSelectedScripts");
	}

	public void clearSelectedScripts() {

		_logger.debug(">>> clearSelectedScripts");

		if (this.getSelectedScriptNodes() != null) {
			for (TreeNode tn : this.getSelectedScriptNodes()) {
				tn.setSelected(false);
			}
		}

		this.setSelectedScriptNodes(null);

		_logger.debug("<<< clearSelectedScripts");
	}

	public void loadSelectedWorkGroupCat() {

		_logger.debug(">>> loadSelectedWorkGroupCat");
		try {

			workGroupCatList = this.getUserService().findAllHPCWGCat(null, null);
			selectedWorkGroupCatList = new ArrayList<HPCWGCat>();
			
			if(this.workGroupCat != null) {
				for (HPCWGCat wgpCat : this.workGroupCat) {
					selectedWorkGroupCatList.add(wgpCat);
				}
			}
			
		} catch (Exception e) {
			MessageHelper.setGlobalErrorMessage(Properties.get("fdm_error_unknown"));
			_logger.error("loadSelectedWorkGroupCat Unknown error occurred" + e.getMessage());
		}

		_logger.debug("<<< loadSelectedWorkGroupCat");
	}

	public void clearSelectedWorkgroupCategory() {

		_logger.debug(">>> clearSelectedWorkgroupCategory");

		selectedWorkGroupCatList.clear();

		_logger.debug("<<< clearSelectedWorkgroupCategory");
	}

	/**
	 * parse the array of selected nodes into a list for the corresponding drop down
	 * menu
	 */
	public void updateSelectedWorkgroupCategory() {

		_logger.debug(">>> updateSelectedWorkgroupCategory");
		this.workGroupCat = new ArrayList<>();
		for (HPCWGCat wgCat : selectedWorkGroupCatList) {
			workGroupCat.add(wgCat);
		}

		_logger.debug("<<< updateSelectedWorkgroupCategory");
	}

	public void loadSelectedWorkgroups() {

		_logger.debug(">>> loadSelectedWorkgroups");
		try {
			this.setWorkgroupRoot(new CheckboxTreeNode("Root", null));

			// make a map for the workgroup categories
			Map<Integer, CheckboxTreeNode> workgroupCats = new TreeMap<Integer, CheckboxTreeNode>();

			List<HPCWorkGroups> workgroupList = fetchWorkgroups();

			for (HPCWorkGroups w : workgroupList) {

				// check if we have created a parent node for the current workgroup's category
				CheckboxTreeNode cat = workgroupCats.get(w.getWgCatId());

				// if not, create a new parent node for the current workgroup's category
				if (cat == null) {
					cat = new CheckboxTreeNode(w.getHpcWgCat().getWgCatDesc(), this.getWorkgroupRoot());
					cat.setExpanded(true);
					workgroupCats.put(w.getWgCatId(), cat);
				}

				// create a node for the current script, parenting it to the node we just
				// retrieved/created
				CheckboxTreeNode node = new CheckboxTreeNode(w, cat);

				if (this.selectedAccessGroup.getFdmWGPAccess() != null) {

					for (HPCWorkGroups workgrps : this.workgroups) {
						if (workgrps.getWorkgroupCode().equalsIgnoreCase(w.getWorkgroupCode())) {
							node.setSelected(true, true);
							break;
						}

					}

				}

			}
		} catch (Exception e) {
			MessageHelper.setGlobalErrorMessage(Properties.get("fdm_error_unknown"));
			_logger.error("loadSelectedWorkgroups Unknown error occurred" + e.getMessage());
		}

		_logger.debug("<<< loadSelectedWorkgroups");
	}

	/**
	 * get a list of workgroups from the workgroup/user map
	 * 
	 * @throws FRInstanceException
	 */
	public List<HPCWorkGroups> fetchWorkgroups() throws UserNotFoundException, FRInstanceException {

		return this.getUserService().getAllHPCWorkGroups(null);
	}

	public void clearSelectedWorkgroups() {

		_logger.debug(">>> clearSelectedWorkgroups");

		if (this.getSelectedWorkgroupNodes() != null) {
			for (TreeNode tn : this.getSelectedWorkgroupNodes()) {
				tn.setSelected(false);
			}
		}

		this.setSelectedWorkgroupNodes(null);

		_logger.debug("<<< clearSelectedWorkgroups");
	}

	/**
	 * parse the array of selected nodes into a list for the corresponding drop down
	 * menu
	 */
	public void updateSelectedWorkgroups() {

		_logger.debug(">>> updateSelectedWorkgroups");

		List<TreeNode> selectedWorkgroupNodeList = Arrays.asList(this.getSelectedWorkgroupNodes());

		this.getWorkgroups().clear();

		for (TreeNode tn : selectedWorkgroupNodeList) {
			// discard the parent nodes
			if (tn.getData() instanceof HPCWorkGroups) {
				HPCWorkGroups work = (HPCWorkGroups) tn.getData();
				this.getWorkgroups().add(work);
			}
		}

		_logger.debug("<<< updateSelectedWorkgroups");
	}

	public void loadSelectedScriptsStatus() {

		_logger.debug(">>> loadSelectedScriptsStatus");

		try {
			this.setScriptStatusRoot(new CheckboxTreeNode("Root", null));

			// make a map for the script categories
			Map<Integer, CheckboxTreeNode> scriptCats = new TreeMap<Integer, CheckboxTreeNode>();

			List<Script> userScripts = this.getScriptService().findAllScriptStatus(null, this.getScripts());

			// for every script
			for (Script s : userScripts) {

				// check if we have created a parent node for the current script's category
				CheckboxTreeNode cat = scriptCats.get(s.getScriptCategory().getId());

				// if not, create a new parent node for the current script's category
				if (cat == null) {
					cat = new CheckboxTreeNode(s.getScriptCategory(), this.getScriptStatusRoot());
					scriptCats.put(s.getScriptCategory().getId(), cat);
				}

				// create a node for the current script, parenting it to the node we just
				// retrieved/created
				CheckboxTreeNode node = new CheckboxTreeNode(s, cat);

				if (s.getScriptStatusDef() != null) {
					for (ScriptStatusDef status : s.getScriptStatusDef()) {
						CheckboxTreeNode childNode = new CheckboxTreeNode(status, node);
						for (FDMResEditStatus selstatus : this.fdmResEditStatusList) {
							if (selstatus.getId().getScriptCodeId().equals(s.getId())
									&& selstatus.getId().getStatusValue().equalsIgnoreCase(status.getStatusValue())) {
								childNode.setSelected(true);
								break;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			MessageHelper.setGlobalErrorMessage(Properties.get("fdm_error_unknown"));
			_logger.error("loadSelectedScriptsStatus Unknown error occurred" + e.getMessage());
		}

		_logger.debug("<<< loadSelectedScriptsStatus");

	}

	public void loadSelectedScriptStatusRoot(boolean isAccessScriptUpdated) {
		this.setSelectedScriptStatusRoot(new CheckboxTreeNode("Root", null));

		try {
			// make a map for the script categories

			// Set<FDMResEditStatus> selectedStatus =
			// selectedAccessGroup.getFdmResEditStatus();

			Set<FDMResEditStatus> updatedFdmResEditStatus = new HashSet<FDMResEditStatus>();

			List<Script> userScripts = new ArrayList<Script>();

			if (fdmResEditStatusList != null && fdmResEditStatusList.size() > 0) {

				userScripts = this.getScriptService().findAllScriptStatus(null, this.getScripts());
			}

			Map<Integer, CheckboxTreeNode> scriptCats = new TreeMap<Integer, CheckboxTreeNode>();

			Map<Integer, CheckboxTreeNode> scriptNodes = new TreeMap<Integer, CheckboxTreeNode>();

			// for every script
			for (Script s : userScripts) {
				// check if we have created a parent node for the current script's category
				CheckboxTreeNode cat = scriptCats.get(s.getScriptCategory().getId());

				CheckboxTreeNode node = scriptNodes.get(s.getId());

				for (FDMResEditStatus selstatus : fdmResEditStatusList) {

					if (selstatus.getId().getScriptCodeId().equals(s.getId())) {

						if (cat == null) {
							cat = new CheckboxTreeNode(s.getScriptCategory(), this.getSelectedScriptStatusRoot());
							cat.setExpanded(true);
							scriptCats.put(s.getScriptCategory().getId(), cat);

						}

						if (node == null) {
							node = new CheckboxTreeNode(s, cat);
							node.setExpanded(true);
							scriptNodes.put(s.getId(), node);
						}

						for (ScriptStatusDef status : s.getScriptStatusDef()) {
							if (status.getStatusValue().equalsIgnoreCase(selstatus.getId().getStatusValue())) {
								new CheckboxTreeNode(status, node);
								updatedFdmResEditStatus
										.add(new FDMResEditStatus(this.selectedAccessGroup.getGroupCode(),
												status.getId().getScriptCodeId(), status.getStatusValue()));
							}
						}
					}

				}

			}

			if (isAccessScriptUpdated) {
				fdmResEditStatusList = updatedFdmResEditStatus;
			}
		} catch (Exception e) {
			MessageHelper.setGlobalErrorMessage(Properties.get("fdm_error_unknown"));
			_logger.error("loadSelectedScriptStatusRoot Unknown error occurred" + e.getMessage());
		}
	}

	/**
	 * parse the array of selected nodes into a list for the corresponding drop down
	 * menu
	 */
	public void updateSelectedScriptsStatus() {

		_logger.debug(">>> updateSelectedScriptsStatus");
		this.fdmResEditStatusList = new HashSet<FDMResEditStatus>();
		this.setSelectedScriptStatusRoot(new CheckboxTreeNode("Root", null));

		Map<Integer, CheckboxTreeNode> scriptCats = new TreeMap<Integer, CheckboxTreeNode>();

		Map<Integer, CheckboxTreeNode> scriptNodes = new TreeMap<Integer, CheckboxTreeNode>();

		for (TreeNode tn : this.selectedScriptStatusNodes) {
			if (tn.getData() instanceof ScriptStatusDef) {

				ScriptStatusDef scriptStatusData = (ScriptStatusDef) tn.getData();

				Script scriptData = (Script) tn.getParent().getData();

				ScriptCategory scriptCatData = (ScriptCategory) tn.getParent().getParent().getData();

				CheckboxTreeNode scriptCat = scriptCats.get(scriptCatData.getId());

				CheckboxTreeNode script = scriptNodes.get(scriptData.getId());

				if (scriptCat == null) {
					scriptCat = new CheckboxTreeNode(scriptCatData, this.getSelectedScriptStatusRoot());
					scriptCat.setSelected(false);
					scriptCat.setExpanded(true);
					scriptCats.put(scriptCatData.getId(), scriptCat);

				}

				if (script == null) {
					script = new CheckboxTreeNode(scriptData, scriptCat);
					script.setSelected(false);
					script.setExpanded(true);
					scriptNodes.put(scriptData.getId(), script);
				}

				CheckboxTreeNode scriptStatus = new CheckboxTreeNode(scriptStatusData, script);
				scriptStatus.setSelected(false);
				scriptStatus.setExpanded(true);

				fdmResEditStatusList.add(new FDMResEditStatus(this.selectedAccessGroup.getGroupCode(),
						scriptStatusData.getId().getScriptCodeId(), scriptStatusData.getStatusValue()));
			}
		}

		_logger.debug("<<< updateSelectedScriptsStatus");
	}

	public void clearSelectedScriptsStatus() {

		_logger.debug(">>> clearSelectedScriptsStatus");

		if (this.getSelectedScriptStatusNodes() != null) {
			for (TreeNode tn : this.getSelectedScriptStatusNodes()) {
				tn.setSelected(false);
			}
		}

		this.selectedScriptStatusNodes = null;

		this.fdmResEditStatusList.clear();
		;

		_logger.debug("<<< clearSelectedScriptsStatus");
	}

	public void setResultEditstatus() {
		if (resultEdit.equalsIgnoreCase("NE")) {
			this.selectedScriptStatusRoot = new CheckboxTreeNode();
			this.fdmResEditStatusList.clear();
		}

	}

	public void isDeleteUser() {

		List<SystemUsers> results;

		try {
			results = this.getUserService().searchUsersByGroupCode(null, this.highlightedAccessGroup.getGroupCode(),
					this.highlightedAccessGroup.getProgCode());

			if (results != null && results.size() > 0) {
				MessageHelper.setErrorMessage(null, "Users are associated with this Access Group. Unable to delete");
				FacesContext.getCurrentInstance().validationFailed();
				PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
				return;
			} else {
				PrimeFaces.current().executeScript("PF('deleteAccessGroupDialogVar').show();");
			}

		} catch (FRInstanceException e) {
			MessageHelper.setGlobalErrorMessage(Properties.get("fdm_error_unknown"));
		}
	}

	public AccessGroupService getAccessGroupService() {
		return accessGroupService;
	}

	public void setAccessGroupService(AccessGroupService accessGroupService) {
		this.accessGroupService = accessGroupService;
	}

	public List<AccessGroups> getAccessGroupsList() {
		return accessGroupsList;
	}

	public void setAccessGroupsList(List<AccessGroups> accessGroupsList) {
		this.accessGroupsList = accessGroupsList;
	}

	public List<AccessGroups> getFilteredAccessGroupsList() {
		return filteredAccessGroupsList;
	}

	public void setFilteredAccessGroupsList(List<AccessGroups> filteredAccessGroupsList) {
		this.filteredAccessGroupsList = filteredAccessGroupsList;
	}

	public AccessGroups getSelectedAccessGroup() {
		return selectedAccessGroup;
	}

	public void setSelectedAccessGroup(AccessGroups selectedAccessGroup) {
		this.selectedAccessGroup = selectedAccessGroup;
	}

	public String getSelectedProgCode() {
		return selectedProgCode;
	}

	public void setSelectedProgCode(String selectedProgCode) {
		this.selectedProgCode = selectedProgCode;
	}

	public String getSelectedGroupCode() {
		return selectedGroupCode;
	}

	public void setSelectedGroupCode(String selectedGroupCode) {
		this.selectedGroupCode = selectedGroupCode;
	}

	public String getSelectedGroupDesc() {
		return selectedGroupDesc;
	}

	public void setSelectedGroupDesc(String selectedGroupDesc) {
		this.selectedGroupDesc = selectedGroupDesc;
	}

	public boolean isAccessGroupEdit() {
		return accessGroupEdit;
	}

	public void setAccessGroupEdit(boolean accessGroupEdit) {
		this.accessGroupEdit = accessGroupEdit;
	}

	public List<SystemFunctions> getSystemFunctions() {
		return systemFunctions;
	}

	public void setSystemFunctions(List<SystemFunctions> systemFunctions) {
		this.systemFunctions = systemFunctions;
	}

	public List<String> getGroupFunctionsList() {
		return groupFunctionsList;
	}

	public void setGroupFunctionsList(List<String> groupFunctionsList) {
		this.groupFunctionsList = groupFunctionsList;
	}

	public TreeNode getScriptRoot() {
		return scriptRoot;
	}

	public void setScriptRoot(TreeNode scriptRoot) {
		this.scriptRoot = scriptRoot;
	}

	public TreeNode[] getSelectedScriptNodes() {
		return selectedScriptNodes;
	}

	public void setSelectedScriptNodes(TreeNode[] selectedScriptNodes) {
		this.selectedScriptNodes = selectedScriptNodes;
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

	public List<HPCWGCat> getWorkGroupCatList() {
		return workGroupCatList;
	}

	public void setWorkGroupCatList(List<HPCWGCat> workGroupCatList) {
		this.workGroupCatList = workGroupCatList;
	}

	public List<HPCWGCat> getSelectedWorkGroupCatList() {
		return selectedWorkGroupCatList;
	}

	public void setSelectedWorkGroupCatList(List<HPCWGCat> selectedWorkGroupCatList) {
		this.selectedWorkGroupCatList = selectedWorkGroupCatList;
	}

	public boolean isAllWorkgroupCatSelected() {
		if (this.workGroupCat != null && !this.workGroupCat.isEmpty())
			return false;
		else
			return true;
	}

	public void setAllWorkgroupCatSelected(boolean allWorkgroupCatSelected) {
		this.allWorkgroupCatSelected = allWorkgroupCatSelected;
	}

	public AccessGroups getHighlightedAccessGroup() {
		return highlightedAccessGroup;
	}

	public void setHighlightedAccessGroup(AccessGroups highlightedAccessGroup) {
		this.highlightedAccessGroup = highlightedAccessGroup;
	}

	public TreeNode getScriptStatusRoot() {
		return scriptStatusRoot;
	}

	public void setScriptStatusRoot(TreeNode scriptStatusRoot) {
		this.scriptStatusRoot = scriptStatusRoot;
	}

	public TreeNode[] getSelectedScriptStatusNodes() {
		return selectedScriptStatusNodes;
	}

	public void setSelectedScriptStatusNodes(TreeNode[] selectedScriptStatusNodes) {
		this.selectedScriptStatusNodes = selectedScriptStatusNodes;
	}

	public List<HPCWGCat> getWorkGroupCat() {
		return workGroupCat;
	}

	public void setWorkGroupCat(List<HPCWGCat> workGroupCat) {
		this.workGroupCat = workGroupCat;
	}

	public int getAccessGroupActiveTab() {
		return accessGroupActiveTab;
	}

	public void setAccessGroupActiveTab(int accessGroupActiveTab) {
		this.accessGroupActiveTab = accessGroupActiveTab;
	}

	public String getResultEdit() {
		return resultEdit;
	}

	public void setResultEdit(String resultEdit) {
		this.resultEdit = resultEdit;
	}

	public TreeNode getSelectedScriptStatusRoot() {
		return selectedScriptStatusRoot;
	}

	public void setSelectedScriptStatusRoot(TreeNode selectedScriptStatusRoot) {
		this.selectedScriptStatusRoot = selectedScriptStatusRoot;
	}

	public boolean isSearchCriteria() {
		return isSearchCriteria;
	}

	public void setSearchCriteria(boolean isSearchCriteria) {
		this.isSearchCriteria = isSearchCriteria;
	}

	public boolean isWorkGroupAdmin() {
		return workGroupAdmin;
	}

	public void setWorkGroupAdmin(boolean workGroupAdmin) {
		this.workGroupAdmin = workGroupAdmin;
	}

}
