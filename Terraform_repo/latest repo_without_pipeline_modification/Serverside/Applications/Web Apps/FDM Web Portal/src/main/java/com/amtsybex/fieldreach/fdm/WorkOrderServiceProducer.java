package com.amtsybex.fieldreach.fdm;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletContext;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.amtsybex.fieldreach.backend.service.WorkOrderService;
import com.amtsybex.fieldreach.services.endpoint.common.WorkOrderController;

public class WorkOrderServiceProducer {


	@Produces @Named @ApplicationScoped 
	public WorkOrderService getWorkOrderService(){

		return (WorkOrderService) WebApplicationContextUtils.getRequiredWebApplicationContext((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getBean("workOrderService");
	}

	@Produces @Named @ApplicationScoped 
	public WorkOrderController getWorkOrderController(){

		return (WorkOrderController) WebApplicationContextUtils.getRequiredWebApplicationContext((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getBean("workOrderController");
	}
	
	


}
