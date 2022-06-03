package com.amtsybex.fieldreach.fdm.dashboard;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.deltaspike.core.api.scope.WindowScoped;

import com.amtsybex.fieldreach.backend.service.ScriptResultsService;
import com.amtsybex.fieldreach.backend.service.ScriptService;
import com.amtsybex.fieldreach.backend.service.UserService;
import com.amtsybex.fieldreach.backend.service.ValidationTypeService;
import com.amtsybex.fieldreach.backend.service.WorkOrderService;
import com.amtsybex.fieldreach.fdm.property.PortalPropertyUtil;
import com.amtsybex.fieldreach.fdm.search.Search;
import com.amtsybex.fieldreach.fdm.user.UserList;
import com.amtsybex.fieldreach.fdm.work.WorkList;
import com.amtsybex.fieldreach.services.endpoint.common.WorkOrderController;

@Named
@WindowScoped
public class DashboardServiceManager implements Serializable {

	private static final long serialVersionUID = -6212956155134913073L;
    
	@Inject
	transient private PortalPropertyUtil portalPropertyUtil;

	@Inject
	transient private ScriptResultsService scriptResultsService;
	
	@Inject
	transient private ScriptService scriptService;
	
	@Inject
	transient private UserService userService;
	
	@Inject
	transient WorkOrderService workService;
	
	@Inject
	transient WorkOrderController workOrderController;
	
	@Inject
	transient private Search search;
	
	@Inject
	transient private WorkList workList;
	
	@Inject
	transient private UserList userList;
	
	@Inject
	private ValidationTypeService validationTypeService;
	
	public DashboardServiceManager() {
		super();
	}
	
	

	public ScriptResultsService getScriptResultsService() {
		return scriptResultsService;
	}

	public void setScriptResultsService(ScriptResultsService scriptResultsService) {
		this.scriptResultsService = scriptResultsService;
	}

	public ScriptService getScriptService() {
		return scriptService;
	}

	public void setScriptService(ScriptService scriptService) {
		this.scriptService = scriptService;
	}

	public Search getSearch() {
		return search;
	}

	public void setSearch(Search search) {
		this.search = search;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public WorkOrderService getWorkService() {
		return workService;
	}

	public void setWorkService(WorkOrderService workService) {
		this.workService = workService;
	}

	public PortalPropertyUtil getPortalPropertyUtil() {
		return portalPropertyUtil;
	}

	public void setPortalPropertyUtil(PortalPropertyUtil portalPropertyUtil) {
		this.portalPropertyUtil = portalPropertyUtil;
	}

	public ValidationTypeService getValidationTypeService() {
		return validationTypeService;
	}

	public void setValidationTypeService(ValidationTypeService validationTypeService) {
		this.validationTypeService = validationTypeService;
	}

	public WorkList getWorkList() {
		return workList;
	}

	public void setWorkList(WorkList workList) {
		this.workList = workList;
	}

	public UserList getUserList() {
		return userList;
	}

	public void setUserList(UserList userList) {
		this.userList = userList;
	}

	public WorkOrderController getWorkOrderController() {
		return workOrderController;
	}

	public void setWorkOrderController(WorkOrderController workOrderController) {
		this.workOrderController = workOrderController;
	}
	
	
	
}
