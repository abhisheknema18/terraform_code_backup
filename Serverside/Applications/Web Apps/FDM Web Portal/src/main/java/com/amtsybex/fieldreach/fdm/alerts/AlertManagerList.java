package com.amtsybex.fieldreach.fdm.alerts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.deltaspike.core.api.scope.WindowScoped;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.exception.UserNotFoundException;
import com.amtsybex.fieldreach.backend.model.HPCWorkGroups;
import com.amtsybex.fieldreach.backend.model.MonitorAlerts;
import com.amtsybex.fieldreach.backend.service.ScriptMonitorService;
import com.amtsybex.fieldreach.backend.service.UserService;
import com.amtsybex.fieldreach.fdm.PageCodebase;
import com.amtsybex.fieldreach.fdm.equipment.EquipmentInformation;
import com.amtsybex.fieldreach.fdm.web.jsf.util.MessageHelper;
import com.amtsybex.fieldreach.fdm.web.jsf.util.Properties;

@Named
@WindowScoped
public class AlertManagerList extends PageCodebase{

	private static final long serialVersionUID = 8341179321059509979L;
	
	@Inject
	protected ScriptMonitorService scriptMonitorService;
	
	@Inject
	transient private UserService userService;
	
	@Inject
	transient EquipmentInformation equipmentInformation;
	
	private List<MonitorAlerts> alerts;
	private List<MonitorAlerts> alertsFiltered;
	
	private List<MonitorAlerts> selectedAlerts;
	
	private long alertCount;
	private long alertCountLastUpdated;

	public String getTitle() {

		return Properties.get("fdm_breadcrumb_manage_alerts_heading");
		
	}
	
	public void loadCount(){
		try {
			if(alertCountLastUpdated == 0 || System.currentTimeMillis() - alertCountLastUpdated > 5000) {
				alertCountLastUpdated = System.currentTimeMillis();
				alertCount = scriptMonitorService.getActiveAlertsCountsForWorkGroups(null, fetchWorkgroupStrings());
			}
		} catch (Exception e) {
			//do nothing as this is just used to update the menu item
		} 
	}
	
	public void viewAlerts() {
		
		this.alerts = null;
		this.alertsFiltered = null;
		this.selectedAlerts = null;
				
		try {
			
			alerts = scriptMonitorService.getActiveAlertsForWorkGroups(null, fetchWorkgroupStrings());
			alertsFiltered = alerts;
			
			if(alerts == null) {
				//no alerts
				alertCount = 0;
			}else {
				//alerts found
				alertCount=alerts.size();
			}
			
		} catch (UserNotFoundException e) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_login_username_not_found_exception_label"));
			return;
		} catch (FRInstanceException e) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
			return;
		}

		FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "alertManagerList");

	}
	
	public void closeSelectedAlerts() {
		
		List<MonitorAlerts> successAlertUpdates = new ArrayList<MonitorAlerts>();
		List<MonitorAlerts> failedAlertUpdates = new ArrayList<MonitorAlerts>();
		
		if(this.selectedAlerts != null && this.selectedAlerts.size() > 0) {
			for(MonitorAlerts alert : this.selectedAlerts) {
				
				String originalStatus = alert.getAlertStatus();
				alert.setAlertStatus("C");
				
				try {
					scriptMonitorService.saveMonitorAlertAndStatusLog(null, alert, this.getUsername());
					
					successAlertUpdates.add(alert);
					
					this.alerts.remove(alert);
					this.alertsFiltered.remove(alert);
					
				} catch (FRInstanceException e) {
					//exception occured so item hasnt been updated, reset it
					alert.setAlertStatus(originalStatus);
					
					failedAlertUpdates.add(alert);
				}

			}
		}
		
		if(failedAlertUpdates.size() > 0) {
			this.selectedAlerts.removeAll(successAlertUpdates);
			this.setSelectedAlerts(failedAlertUpdates);

			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_alert_update_failure", new Object[] {successAlertUpdates.size(), failedAlertUpdates.size()}));
		}else {
			MessageHelper.setGlobalInfoMessage(Properties.get("fdm_alert_update_success", new Object[] {successAlertUpdates.size()}));
			this.setSelectedAlerts(null);
		}
		
		alertCount=alerts.size();

	}
	
	public void resetSelectionAfterFilter() {

		if(this.selectedAlerts != null) {
			Iterator<MonitorAlerts> alertsIt = this.selectedAlerts.iterator();
			while (alertsIt.hasNext()) {
				if(!this.alertsFiltered.contains(alertsIt.next())) {
					alertsIt.remove();
				}
			}
		}
	}
	
	public void loadAssetInformation(String equipNo) throws FRInstanceException {
		equipmentInformation.populateEquipmentAssetInformation(equipNo);
	}
	
	
	public List<String> fetchWorkgroupStrings() throws UserNotFoundException, FRInstanceException {
		
		if(!userService.hasUnlimitedAccessibleWorkgroups(null, getUsername())) {
			
			List<HPCWorkGroups> wgs = userService.getAccessibleWorkgroups(null, getUsername());

			if (wgs != null){
				
				return wgs.stream().map(HPCWorkGroups::getWorkgroupCode).collect(Collectors.toList());
			}
		}
		
		return null;
	}

	public List<MonitorAlerts> getAlerts() {
		return alerts;
	}

	public void setAlerts(List<MonitorAlerts> alerts) {
		this.alerts = alerts;
	}

	public List<MonitorAlerts> getAlertsFiltered() {
		return alertsFiltered;
	}

	public void setAlertsFiltered(List<MonitorAlerts> alertsFiltered) {
		this.alertsFiltered = alertsFiltered;
	}

	public List<MonitorAlerts> getSelectedAlerts() {
		return selectedAlerts;
	}

	public void setSelectedAlerts(List<MonitorAlerts> selectedAlerts) {
		this.selectedAlerts = selectedAlerts;
	}


	public long getAlertCount() {
		return alertCount;
	}


	public void setAlertCount(long alertCount) {
		this.alertCount = alertCount;
	}
	
	
}
