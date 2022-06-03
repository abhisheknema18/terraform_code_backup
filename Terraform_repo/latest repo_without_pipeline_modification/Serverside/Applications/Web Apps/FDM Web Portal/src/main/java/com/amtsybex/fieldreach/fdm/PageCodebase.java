package com.amtsybex.fieldreach.fdm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import javax.inject.Inject;

import com.amtsybex.fieldreach.backend.service.AccessTokenService;
import com.amtsybex.fieldreach.backend.service.ConfigurationService;
import com.amtsybex.fieldreach.backend.service.ScriptResultsService;
import com.amtsybex.fieldreach.backend.service.ScriptService;
import com.amtsybex.fieldreach.backend.service.SystemParametersService;
import com.amtsybex.fieldreach.backend.service.UserService;
import com.amtsybex.fieldreach.backend.service.ValidationTypeService;
import com.amtsybex.fieldreach.services.endpoint.common.WorkOrderController;

public class PageCodebase implements Serializable{

	private static final long serialVersionUID = -8224679241265472253L;
	
	@Inject
	transient ScriptService scriptService;
	
	@Inject
	transient Environment environment;
	
	@Inject
	transient UserService userService;

	@Inject
	transient ScriptResultsService scriptResultsService;
	
	@Inject
	transient SystemParametersService systemParameterService;
	
	@Inject
	transient WorkOrderController workOrderController;
	
	@Inject
	transient ValidationTypeService validationTypeService;
	
	@Inject 
	transient AccessTokenService accessTokenService;	

	@Inject 
	transient ConfigurationService configurationService;
	
	public ScriptService getScriptService() {
		return scriptService;
	}

	public void setScriptService(ScriptService scriptService) {
		this.scriptService = scriptService;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public ScriptResultsService getScriptResultsService() {
		return scriptResultsService;
	}

	public void setScriptResultsService(ScriptResultsService scriptResultsService) {
		this.scriptResultsService = scriptResultsService;
	}

	public WorkOrderController getWorkOrderController() {
		return workOrderController;
	}

	public void setWorkOrderController(WorkOrderController workOrderController) {
		this.workOrderController = workOrderController;
	}	

	public ValidationTypeService getValidationTypeService() {
		return validationTypeService;
	}

	public void setValidationTypeService(ValidationTypeService validationTypeService) {
		this.validationTypeService = validationTypeService;
	}

	public String getUsername(){
		//return userd id
		return this.environment.getUser().getId();
	}
	
	public String getUserDetails() {
		//return userd name
		return this.environment.getUser().getUserName();
	}

    private void init() {
    }

    private void readObject(ObjectInputStream ois) 
                            throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        init();
    }

	public SystemParametersService getSystemParameterService() {
		return systemParameterService;
	}

	public void setSystemParameterService(SystemParametersService systemParameterService) {
		this.systemParameterService = systemParameterService;
	}

	public AccessTokenService getAccessTokenService() {
		return accessTokenService;
	}

	public void setAccessTokenService(AccessTokenService accessTokenService) {
		this.accessTokenService = accessTokenService;
	}

	public ConfigurationService getConfigurationService() {
		return configurationService;
	}

	public void setConfigurationService(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}
	
	
	
}