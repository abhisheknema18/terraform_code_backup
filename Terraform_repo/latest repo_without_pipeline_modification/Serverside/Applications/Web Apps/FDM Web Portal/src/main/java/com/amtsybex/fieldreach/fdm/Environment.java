package com.amtsybex.fieldreach.fdm;

import java.io.Serializable;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.deltaspike.core.api.scope.WindowScoped;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.model.AccessGroups;
import com.amtsybex.fieldreach.backend.model.GroupFunctions;
import com.amtsybex.fieldreach.backend.model.GroupFunctions.GROUPFUNCTION;
import com.amtsybex.fieldreach.backend.model.SystemParameters;
import com.amtsybex.fieldreach.backend.model.SystemUsers;
import com.amtsybex.fieldreach.backend.service.SystemParametersService;
import com.amtsybex.fieldreach.backend.service.UserService;
import com.amtsybex.fieldreach.fdm.property.PortalPropertyUtil;
import com.amtsybex.fieldreach.services.endpoint.common.WorkOrderController;
import com.amtsybex.fieldreach.services.endpoint.common.WorkStatus.WORKSTATUSDESIGNATION;

@Named("fdmenv")
@WindowScoped
public class Environment implements Serializable{

	private static final long serialVersionUID = 7880871303724651195L;

	@Inject
	transient PortalPropertyUtil portalPropertyUtil;
	
	@Inject
	transient UserService userService;
	
	@Inject
	transient SystemParametersService systemParameterService;
	
	@Inject
	transient WorkOrderController workOrderController;
	
	private String environment;
	private SystemParameters systemParameters;
	
	//FDE049/51 - MC 
	private Boolean admin;
	private Boolean workManagement;
	private Boolean workView;
	private Boolean dashboardView;
	private Boolean accessTokenView;
	
	//33461 - KN - Dashboard access by function code
	private boolean scriptResultsDashboard;
	private boolean workInprogressDashboard;
	private boolean activeUserDashboard;
	private boolean workOverDueDashboard;
	private boolean atRiskDashboard;
    private boolean resultNotesView;
    private boolean uniqueReferenceView;
    private boolean equipmentView;
	private boolean personalView;
	private boolean resultsApproval;
	private boolean extractCSV;
	private boolean resultAnalysis;
	private boolean resultMetrics;
	private boolean userMonitor;
	private boolean viewPII;
	private boolean manageAlerts;
	//FDE063- KN - filterWorkGroupAdmin
	private boolean viewWorkGroupCat;
	private boolean viewWorkGroupAdmin;
	private boolean viewSharedAccessToken;

	private List<GroupFunctions> functionCodes;
	
	private SystemUsers user;
	
	//33461 - KN - functions codes should be loaded before the dashboard page
	@PostConstruct
	public void init() {
		SystemUsers systemUser;

		try {

			URL issuer;

			if (SecurityContextHolder.getContext().getAuthentication() instanceof OAuth2AuthenticationToken) {
				OAuth2User oAuth2User = (OAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

				issuer = oAuth2User.getAttribute("iss");
				try {
					
					systemUser = this.userService.getSystemUser(null, oAuth2User.getAttribute("sub"),issuer.toString());
					
				} catch (FRInstanceException e) {
					throw new ViewExpiredException();
				}
			} else {
				throw new ViewExpiredException();
			}

			user = systemUser;
			this.setSystemParameters(this.getSystemParameterService().getSystemParams(null));
			
		} catch (FRInstanceException e) {
			throw new ViewExpiredException();
		}
		
		if (user != null) {

			try {
				
				AccessGroups accessGroup = this.getUserService().getAccessGroupsByGroupCode(null, user.getFdmGroupCode());
				
				if(accessGroup != null && accessGroup.getHpcWgpAdmin().equals(1)) {
					this.viewWorkGroupAdmin = true;
				}
				
				this.functionCodes = this.userService.findGroupFunctions(null, user.getFdmGroupCode());
				//Check user has access to Add/Edit work group Category				
				if(this.getUserService().findWGAdminByGroupCode(null, user.getFdmGroupCode()) == null) {
					this.viewWorkGroupCat = true;
				}
				
			} catch (FRInstanceException e) {
				throw new ViewExpiredException();
			}

			for (GroupFunctions fc : functionCodes) {
				if (fc.getId().getFunctionCode().equalsIgnoreCase(GROUPFUNCTION.DBVUA.toString())) {
					this.setActiveUserDashboard(true);
				}else if (fc.getId().getFunctionCode().equalsIgnoreCase(GROUPFUNCTION.DBVWS.toString())) {
					this.setWorkInprogressDashboard(true);
				}else if (fc.getId().getFunctionCode().equalsIgnoreCase(GROUPFUNCTION.DBVSR.toString())) {
					this.setScriptResultsDashboard(true);
				}else if (fc.getId().getFunctionCode().equalsIgnoreCase(GROUPFUNCTION.DBVOW.toString())) {
					this.setWorkOverDueDashboard(true);
				}else if (fc.getId().getFunctionCode().equalsIgnoreCase(GROUPFUNCTION.DBVAW.toString())) {
					this.setAtRiskDashboard(true);
				}else if (fc.getId().getFunctionCode().equalsIgnoreCase(GROUPFUNCTION.CN.toString())) {
                    this.setResultNotesView(true);
                }else if (fc.getId().getFunctionCode().equalsIgnoreCase(GROUPFUNCTION.SBUR.toString())) {
					this.setUniqueReferenceView(true);
				}else if (fc.getId().getFunctionCode().equalsIgnoreCase(GROUPFUNCTION.SBEN.toString())) {
					this.setEquipmentView(true);
				}else if (fc.getId().getFunctionCode().equalsIgnoreCase(GROUPFUNCTION.APPV.toString())) {
                    this.setResultsApproval(true);
                }else if (fc.getId().getFunctionCode().equalsIgnoreCase(GROUPFUNCTION.DV.toString())) {
					this.setPersonalView(true);
				}else if (fc.getId().getFunctionCode().equalsIgnoreCase(GROUPFUNCTION.CX.toString())) {
					this.setExtractCSV(true);
				}else if (fc.getId().getFunctionCode().equalsIgnoreCase(GROUPFUNCTION.SPM.toString())) {
					this.setResultMetrics(true);
				}else if (fc.getId().getFunctionCode().equalsIgnoreCase(GROUPFUNCTION.SPA.toString())) {
					this.setResultAnalysis(true);
				}else if (fc.getId().getFunctionCode().equalsIgnoreCase(GROUPFUNCTION.FUM.toString())) {
					this.setUserMonitor(true);
				}else if (fc.getId().getFunctionCode().equalsIgnoreCase(GROUPFUNCTION.PII.toString())) {
					this.setViewPII(true);
				}else if (fc.getId().getFunctionCode().equalsIgnoreCase(GROUPFUNCTION.MALT.toString())) {
					this.setManageAlerts(true);
				}else if (fc.getId().getFunctionCode().equalsIgnoreCase(GROUPFUNCTION.SAT.toString())) {
					this.setViewSharedAccessToken(true);;
				}

			}
		}
	}
	
	public String getEnvironment() {
		if (environment == null) {
			try {
				final java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
				environment = localMachine.getHostName();
			} catch (UnknownHostException e) {
				// handle exception
			}
		}
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}	

	public boolean isUniqueReferenceView() {
		return uniqueReferenceView;
	}

	public void setUniqueReferenceView(boolean uniqueReferenceView) {
		this.uniqueReferenceView = uniqueReferenceView;
	}
	
	public boolean isEquipmentView() {
		return equipmentView;
	}

	public void setEquipmentView(boolean equipmentView) {
		this.equipmentView = equipmentView;
	}
	 
	public boolean isResultsApproval() {
		return resultsApproval;
	}

	public void setResultsApproval(boolean resultsApproval) {
		this.resultsApproval = resultsApproval;
	}

	public boolean isUserMonitor() {
		return userMonitor;
	}

	public void setUserMonitor(boolean userMonitor) {
		this.userMonitor = userMonitor;
	}

	/**
	 * helper method to get the number of days between two dates
	 * 
	 * @param	start date
	 * @param 	end date
	 * @return	number of days between start and end dates
	 * @see		Date
	 */
	public int daysBetween(Date d1, Date d2){
		return (int)( (d2.getTime() - d1.getTime()) / (24 * 60 * 60 * 1000));
	}
	
	/**
	 * FDE049 - MC
	 * @return
	 */
	public boolean getAdmin() {
	if(admin == null) {
			admin = false;
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth!=null){
				for(GrantedAuthority authItem : auth.getAuthorities()) {
					if(authItem.getAuthority().equalsIgnoreCase("ROLE_ADMIN")) {
						admin = true;
						break;
					}
				}
				
			}
		}

		return admin;
	}
	
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	
	
	public boolean getWorkManagement() {
	
	if(workManagement == null) {
			workManagement = false;
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth!=null){
				for(GrantedAuthority authItem : auth.getAuthorities()) {
					if(authItem.getAuthority().equalsIgnoreCase("ROLE_WORK_MANAGEMENT")) {
						workManagement = true;
						break;
					}
				}
				
			}
		}

		return workManagement;
	}
	
	public void setWorkManagement(boolean workManagement) {
		this.workManagement = workManagement;
	}

	public boolean getWorkView() {
		
	if(workView == null) {
			workView = false;
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth!=null){
				for(GrantedAuthority authItem : auth.getAuthorities()) {
					if(authItem.getAuthority().equalsIgnoreCase("ROLE_WORK_VIEW")) {
						workView = true;
						break;
					}
				}
				
			}
		}

		return workView;
	}
	
	public void setWorkView(boolean workView) {
		this.workView = workView;
	}
	
	public Boolean getDashboardView() {
		
		if(dashboardView == null) {
			dashboardView = false;
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth!=null){
				for(GrantedAuthority authItem : auth.getAuthorities()) {
					if(authItem.getAuthority().equalsIgnoreCase("ROLE_DASHBOARD_VIEW")) {
						dashboardView = true;
						break;
					}
				}
				
			}
		}

		return dashboardView;
	}
	

	public void setDashboardView(Boolean dashboardView) {
		this.dashboardView = dashboardView;
	}
	
	public Boolean getAccessTokenView() {
		
		if(accessTokenView == null) {
			accessTokenView = false;
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth!=null){
				for(GrantedAuthority authItem : auth.getAuthorities()) {
					if(authItem.getAuthority().equalsIgnoreCase("ROLE_ACCESS_TOKEN_VIEW")) {
						accessTokenView = true;
						break;
					}
				}
			}
		}

		return accessTokenView;
	}

	public void setAccessTokenView(Boolean accessTokenView) {
		this.accessTokenView = accessTokenView;
	}

	public PortalPropertyUtil getPortalPropertyUtil() {
		return portalPropertyUtil;
	}

	public void setPortalPropertyUtil(PortalPropertyUtil portalPropertyUtil) {
		this.portalPropertyUtil = portalPropertyUtil;
	}

	public boolean isCloseApprovalActive() {
		
		try {
			return this.getWorkOrderController().getWorkStatuses().getSystemWorkStatusByDesignation(null, WORKSTATUSDESIGNATION.PRECLOSEAPPROVAL) != null;
		} catch (FRInstanceException e) {
			return false;
		}

	}
	
	public Integer idleTimeoutms() {
		
		return (portalPropertyUtil.server().getServlet().getSession().getTimeout() * 1000) - 60000;
		
	}
	
	public void logout() {
			FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "login");
	}
	
	public void onActive() {
		
	}

	public List<GroupFunctions> getFunctionCodes() {
		return functionCodes;
	}

	public void setFuctionCodes(List<GroupFunctions> functionCodes) {
		this.functionCodes = functionCodes;
	}

	public boolean isScriptResultsDashboard() {
		return scriptResultsDashboard;
	}

	public void setScriptResultsDashboard(boolean scriptResultsDashboard) {
		this.scriptResultsDashboard = scriptResultsDashboard;
	}

	public boolean isWorkInprogressDashboard() {
		return workInprogressDashboard;
	}

	public void setWorkInprogressDashboard(boolean workInprogressDashboard) {
		this.workInprogressDashboard = workInprogressDashboard;
	}

	public boolean isActiveUserDashboard() {
		return activeUserDashboard;
	}

	public void setActiveUserDashboard(boolean activeUserDashboard) {
		this.activeUserDashboard = activeUserDashboard;
	}

	public boolean isAtRiskDashboard() {
		return atRiskDashboard;
	}

	public void setAtRiskDashboard(boolean atRiskDashboard) {
		this.atRiskDashboard = atRiskDashboard;
	}

	public boolean isWorkOverDueDashboard() {
		return workOverDueDashboard;
	}

	public void setWorkOverDueDashboard(boolean workOverDueDashboard) {
		this.workOverDueDashboard = workOverDueDashboard;
	}

    public void setResultNotesView(boolean resultNotesView) {
        this.resultNotesView = resultNotesView;
    }

	public SystemParameters getSystemParameters() {
		return systemParameters;
	}

	public void setSystemParameters(SystemParameters systemParameters) {
		this.systemParameters = systemParameters;
	}
	
	public boolean isResultNotesView() {
		return resultNotesView;
	}

	public boolean isPersonalView() {
		return personalView;
	}

	public void setPersonalView(boolean personalView) {
		this.personalView = personalView;
	}

	public boolean isExtractCSV() {
		return extractCSV;
	}

	public void setExtractCSV(boolean extractCSV) {
		this.extractCSV = extractCSV;
	}

	public boolean isResultAnalysis() {
		return resultAnalysis;
	}

	public void setResultAnalysis(boolean resultAnalysis) {
		this.resultAnalysis = resultAnalysis;
	}

	public boolean isResultMetrics() {
		return resultMetrics;
	}

	public void setResultMetrics(boolean resultMetrics) {
		this.resultMetrics = resultMetrics;
	}

	public boolean isViewPII() {
		return viewPII;
	}

	public void setViewPII(boolean viewPII) {
		this.viewPII = viewPII;
	}

	public SystemUsers getUser() {
		return user;
	}

	public void setUser(SystemUsers user) {
		this.user = user;
	}

	public boolean isManageAlerts() {
		return manageAlerts;
	}

	public void setManageAlerts(boolean manageAlerts) {
		this.manageAlerts = manageAlerts;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public SystemParametersService getSystemParameterService() {
		return systemParameterService;
	}

	public void setSystemParameterService(SystemParametersService systemParameterService) {
		this.systemParameterService = systemParameterService;
	}

	public WorkOrderController getWorkOrderController() {
		return workOrderController;
	}

	public void setWorkOrderController(WorkOrderController workOrderController) {
		this.workOrderController = workOrderController;
	}

	public boolean isViewWorkGroupCat() {
		return viewWorkGroupCat;
	}

	public void setViewWorkGroupCat(boolean viewWorkGroupCat) {
		this.viewWorkGroupCat = viewWorkGroupCat;
	}

	public boolean isViewWorkGroupAdmin() {
		return viewWorkGroupAdmin;
	}

	public void setViewWorkGroupAdmin(boolean viewWorkGroupAdmin) {
		this.viewWorkGroupAdmin = viewWorkGroupAdmin;
	}

	public boolean isViewSharedAccessToken() {
		return viewSharedAccessToken;
	}

	public void setViewSharedAccessToken(boolean viewSharedAccessToken) {
		this.viewSharedAccessToken = viewSharedAccessToken;
	}

}
