package com.amtsybex.fieldreach.fdm.admin;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.deltaspike.core.api.scope.WindowScoped;
import org.primefaces.PrimeFaces;
import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.TreeNode;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.exception.UserNotFoundException;
import com.amtsybex.fieldreach.backend.model.AccessGroups;
import com.amtsybex.fieldreach.backend.model.HPCWorkGroups;
import com.amtsybex.fieldreach.backend.model.Invitations;
import com.amtsybex.fieldreach.backend.model.SystemParameters;
import com.amtsybex.fieldreach.backend.model.SystemUsers;
import com.amtsybex.fieldreach.backend.model.ValidationProperty;
import com.amtsybex.fieldreach.backend.service.UserInvitationService;
import com.amtsybex.fieldreach.exception.ConfigException;
import com.amtsybex.fieldreach.fdm.Environment;
import com.amtsybex.fieldreach.fdm.PageCodebase;
import com.amtsybex.fieldreach.fdm.util.InvitePayloadFactory;
import com.amtsybex.fieldreach.fdm.web.jsf.util.ManagedBeanHelper;
import com.amtsybex.fieldreach.fdm.web.jsf.util.MessageHelper;
import com.amtsybex.fieldreach.fdm.web.jsf.util.Properties;
import com.amtsybex.fieldreach.user.utils.InviteToken;
import com.amtsybex.fieldreach.user.utils.impl.InviteAuth;
import com.amtsybex.fieldreach.user.utils.impl.InviteClaims;
import com.amtsybex.fieldreach.user.utils.impl.InviteResources;
import com.amtsybex.fieldreach.user.utils.impl.MobileInviteToken;
import com.amtsybex.fieldreach.user.utils.impl.SystemInviteToken;
import com.amtsybex.fieldreach.utils.AccessTokenAuthor;
import com.amtsybex.fieldreach.utils.AccessTokenAuthor.EXPIRY;
import com.amtsybex.fieldreach.utils.impl.Common;
import com.nimbusds.jose.JOSEException;

@Named
@WindowScoped
public class UserInvitation  extends PageCodebase implements Serializable{
	
	private static final long serialVersionUID = -5158973874682108650L;
	
	private String fdmGroupCode;
	private List<HPCWorkGroups> workGroupList;
	private TreeNode workgroupRoot; 
	private TreeNode selectedSingleWorkgroupNode; 
	private boolean workgroupSelected;
	
	private Invitations selectedUserInvitation;	

	private SystemUsers selectedSystemUser;
	
	private int selectedExpiryVal;
	
	private String generatedInviteToken;
	
	private List<Invitations> invitationList;

	private List<Invitations> filteredInvitationList;
	
	private boolean onlyActiveMobileInvitations = true; 
	
	private boolean onlyActiveSystemInvitations = true; 
	
	private List<Invitations> activeInvitationList;
	
	private List<Invitations> allInvitationList;
		
	private List<ValidationProperty> validationProperty = null;
	
	private List<AccessGroups> sbAccessGroups;
	
	private List<AccessGroups> fdmAccessGroups;
	
	private List<AccessGroups> accessGroups;
	
	private boolean systemUserInvite;
	
	
	@Inject
	transient UserInvitationService userInvitationService;
	
	@Inject
    private AccessTokenAuthor accessTokenAuthor;


	
	public void addUserInvitation(boolean isSystemUserInvite) {
		
		this.systemUserInvite = isSystemUserInvite;
		
		init();
		
		selectedUserInvitation.setId(UUID.randomUUID().toString());
		selectedUserInvitation.setCreateUser(this.getUsername());
		selectedUserInvitation.setCreateDate(Common.generateFieldreachDBDate());
		selectedUserInvitation.setCreateTime(StringUtils.leftPad(String.valueOf(Common.generateFieldreachDBTime()), 6, '0'));
		selectedUserInvitation.setInvitationStatus(INVITATIONSTATUS.ACTIVE.name());

		if (!this.systemUserInvite) {
			selectedUserInvitation.setInvitationType(INVITATIONTYPE.M.name());
			
		} else {

			selectedUserInvitation.setInvitationType(INVITATIONTYPE.S.name());

			selectedSystemUser = new SystemUsers();

			List<AccessGroups> accessGroups = new ArrayList<>();
			List<AccessGroups> sbAccessGroups = new ArrayList<>();
			List<AccessGroups> fdmAccessGroups = new ArrayList<>();

			try {

				SystemParameters sysParams = this.getSystemParameterService().getSystemParams(null);

				setValidationProperty(this.getValidationTypeService().getValidationPropertyByValidationTypeWeightScoreDesc(null, sysParams.getSysUserClass()));

				accessGroups = this.getUserService().getAccessGroups(null);

				for (AccessGroups accessGroup : accessGroups) {

					if (accessGroup.getProgCode().equalsIgnoreCase("SB")) {

						sbAccessGroups.add(accessGroup);

					} else if (accessGroup.getProgCode().equalsIgnoreCase("FDM")) {

						fdmAccessGroups.add(accessGroup);

					}
				}
				this.setAccessGroups(accessGroups);
				this.setSbAccessGroups(sbAccessGroups);
				this.setFdmAccessGroups(fdmAccessGroups);

			} catch (FRInstanceException e) {
				MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
			}

		}

	}
	
	public void onSBAccessGroupChange() {
		if(selectedSystemUser.getSbGroupCode()==null || selectedSystemUser.getSbGroupCode().equals("")){
			selectedSystemUser.setWinLogin(null);
		}
	}
	
	public void saveInvitation() {
		
		try {
			
			if(selectedUserInvitation.getName()==null || selectedUserInvitation.getName().isEmpty()) {
				MessageHelper.setErrorMessage(null,Properties.get("fdm_invitation_token_description_required_message"));
				FacesContext.getCurrentInstance().validationFailed();
				PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
				return;
			}
			
			if(selectedUserInvitation.getInvitationNotes()==null || selectedUserInvitation.getInvitationNotes().isEmpty()) {
				MessageHelper.setErrorMessage(null,Properties.get("fdm_invitation_token_notes_required_message"));
                FacesContext.getCurrentInstance().validationFailed();
                PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
                return;
			}
			
			if(this.systemUserInvite) {
				//system user invite
				if (StringUtils.isEmpty(selectedSystemUser.getId().trim())) {

					MessageHelper.setErrorMessage(null, Properties.get("fdm_systemuser_invite_usercode_message"));
					FacesContext.getCurrentInstance().validationFailed();
					PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
					return;

				}

				if (StringUtils.isEmpty(selectedSystemUser.getUserName().trim())) {

					MessageHelper.setErrorMessage(null, Properties.get("fdm_systemuser_invite_username_message"));
					FacesContext.getCurrentInstance().validationFailed();
					PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
					return;

				}
				
				if (!StringUtils.isEmpty(selectedSystemUser.getSbGroupCode()) && StringUtils.isEmpty(selectedSystemUser.getWinLogin())) {

					MessageHelper.setErrorMessage(null, Properties.get("fdm_system_user_admin_windows_login_required_msg"));
					FacesContext.getCurrentInstance().validationFailed();
					PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
					return;

				}
				
				boolean inviteExists = this.userInvitationService.activeInvitationExistsForUserCode(null, this.selectedSystemUser.getId() , this.selectedUserInvitation.getInvitationType());
				
				if (inviteExists) {

					MessageHelper.setErrorMessage(null, Properties.get("fdm_systemuser_invitation_already_exists_error_message"));
					FacesContext.getCurrentInstance().validationFailed();
					PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
					return;

				}
				
				if(this.getUserService().getSystemUser(null, this.selectedSystemUser.getId()) != null) {
					MessageHelper.setErrorMessage(null, Properties.get("fdm_systemuser_already_exists_error_message"));
					FacesContext.getCurrentInstance().validationFailed();
					PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
					return;
				}
				
				this.selectedUserInvitation.setSystemUser(this.selectedSystemUser.getId());				
				
				this.generatedInviteToken = sysGenerateToken();
				
			} else {		
				//Mobile user Invite
				if(selectedUserInvitation.getWorkGroupCode()==null || selectedUserInvitation.getWorkGroupCode().isEmpty()) {
					MessageHelper.setErrorMessage(null,Properties.get("fdm_invitation_token_work_group_required_message"));
					FacesContext.getCurrentInstance().validationFailed();
					PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
					return;
				}
				this.generatedInviteToken = generateToken();
			}
	        
			this.userInvitationService.saveUserInvitation(null, selectedUserInvitation);
			
		} catch (FRInstanceException e) {
			
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
			return;
			
		} catch (JOSEException | ParseException | ConfigException e) {
			
			MessageHelper.setErrorMessage(null, Properties.get("fdm_token_generation_error"));
			FacesContext.getCurrentInstance().validationFailed();
			PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
			return;
			
		}
			
		PrimeFaces.current().executeScript("PF('showUserInvitationDialog').show();");
		
		
	}
	
	private String sysGenerateToken() throws JOSEException, ParseException, ConfigException {

		InviteToken inviteTokenPayload = InvitePayloadFactory.getSystemInviteTokenObj(selectedSystemUser,
				selectedUserInvitation.getId(), selectedUserInvitation.getExpiryDate());

		String baseUri = ((SystemInviteToken) inviteTokenPayload).getUri()+"/Invite.xhtml?inviteToken=";

		return baseUri + this.accessTokenAuthor.generateInviteJWT(inviteTokenPayload, AccessTokenAuthor.CLAIMS);
	}
	
	private String generateToken() throws JOSEException, ParseException, ConfigException {
		
		String baseUri="fieldsmart://onboard?inviteToken=";
		InviteResources payloadResources = InvitePayloadFactory.getInviteResourcesObj();
        InviteAuth payloadAuth = InvitePayloadFactory.getInviteAuthObj();
        InviteClaims payloadClaims = InvitePayloadFactory.getInviteClaimsObj(selectedUserInvitation.getWorkGroupCode(), selectedUserInvitation.getId(), selectedUserInvitation.getExpiryDate());
        
        MobileInviteToken inviteTokenPayload = InvitePayloadFactory.getInviteTokenObj(payloadClaims, payloadAuth, payloadResources);
        
        return baseUri+this.accessTokenAuthor.generateInviteJWT(inviteTokenPayload, AccessTokenAuthor.CFG);
	}
	
	public void showCopySuccessMessage() {

		if (!this.systemUserInvite) {
			MessageHelper.setGlobalInfoMessage(Properties.get("fdm_inviration_token_url_copy_success_message"));

		} else {
			MessageHelper.setGlobalInfoMessage(Properties.get("fdm_systemuser_inviration_token_url_copy_success_message"));

		}

	}
	
	public List<EXPIRY> loadExpiry() {

		List<EXPIRY> expiryMonths;

		expiryMonths= Stream.of(EXPIRY.values()).collect(Collectors.toList());

		return expiryMonths;
	}

	public void onExpiryChange(ValueChangeEvent event) {

		selectedExpiryVal = Integer.valueOf(event.getNewValue().toString());

		calcExpiryDate();

	}
	
	public void calcExpiryDate() {

		Calendar cal = Calendar.getInstance(); 

		cal.add(Calendar.MONTH, this.getSelectedExpiryVal());

		selectedUserInvitation.setExpiryDate(Common.generateFieldreachDBDate(cal.getTime()));

	}
	
	private void init() {

		this.selectedUserInvitation = new Invitations();
		this.selectedExpiryVal = 1;
		calcExpiryDate();
		
	}
	private static enum INVITATIONTYPE {
		M,
		S;
	}
	
	public static enum INVITATIONSTATUS {
		ACTIVE,
		COMPLETE,
		REVOKED;
	}
	
	public void loadSelectedWorkgroups() {

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
				
					if (this.selectedUserInvitation.getWorkGroupCode() != null && this.selectedUserInvitation.getWorkGroupCode().equals(w.getId().getWorkgroupCode())) {
						node.setSelected(true, true);
					}
			}
			//}
			//tempWorkgroupNodes = selectedWorkgroupNodes;
		}catch(Exception e) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
		}
	
	}
	
	public void updateSelectedSingleWorkgroup() {

		selectedUserInvitation.setWorkGroupCode(null);
		if(selectedSingleWorkgroupNode != null) {
			HPCWorkGroups wg = null;
			if (selectedSingleWorkgroupNode.getData() instanceof HPCWorkGroups)
				wg = (((HPCWorkGroups) selectedSingleWorkgroupNode.getData()));
			selectedUserInvitation.setWorkGroupCode(wg!=null? wg.getId().getWorkgroupCode():null);
			selectedUserInvitation.setWorkGroupDesc(wg!=null?wg.getWorkgroupDesc():null);
		}
		workgroupSelected = selectedUserInvitation.getWorkGroupCode() != null ? true : false;

	}
	
	public void clearSelectedSingleWorkgroup(){
	
		
		if(this.selectedSingleWorkgroupNode != null) {
			selectedSingleWorkgroupNode.setSelected(false);
		}

		this.selectedSingleWorkgroupNode = null;
		this.selectedUserInvitation.setWorkGroupDesc(null);
		//workgroupSelected = (userAdminCriteria.getWorkgroups()).isEmpty() ? true : false;
		
	
	}
	
	
	public List<HPCWorkGroups> fetchWorkgroups(){
			try {
				fdmGroupCode = ManagedBeanHelper.findBean("fdmenv", Environment.class).getUser().getFdmGroupCode();
				workGroupList = this.getUserService().getHPCWorkGroups(null,null,null,null, fdmGroupCode);
			} catch (FRInstanceException e) {
		}
		return workGroupList;
	}
	
	
	public String loadInvitationsList(boolean isViewSystemUserInvite) {

          try {
        	  
        	systemUserInvite = isViewSystemUserInvite;

			if (systemUserInvite) {
				
				allInvitationList = this.getUserInvitationService().getAllUserInvitationsByInviteType(null, INVITATIONTYPE.S.toString());

			} else {
				
				List<HPCWorkGroups> accessibleWorkGroups = this.getUserService().getAccessibleWorkgroups(null, getUsername());
				
				List<String> workGroupList = accessibleWorkGroups.parallelStream().map(awg -> awg.getWorkgroupCode()).collect(Collectors.toList());

				allInvitationList = this.getUserInvitationService().getAllUserInvitationsByTypeAndWorkGroups(null, INVITATIONTYPE.M.toString(), workGroupList);
			}
           
            
            activeInvitationList = allInvitationList.stream()
                .filter(Invitations::isInvitationActive)
                .collect(Collectors.toList());
            
            setListInvitations();
      
          } catch (FRInstanceException | UserNotFoundException e) {
      
            MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
      
          }
          
          return "inviteTokenList";

    }
	

	public String revokeInviteToken() {
	  
      try {
              selectedUserInvitation.setInvitationStatus(INVITATIONSTATUS.REVOKED.toString());
              selectedUserInvitation.setRevokeDate(Common.generateFieldreachDBDate());
              selectedUserInvitation.setRevokeUser(this.getUsername());
              this.getUserInvitationService().saveUserInvitation(null, selectedUserInvitation);
          
      } catch (FRInstanceException e) {
          
          MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
          return null;
          
      }
      
      return loadInvitationsList(systemUserInvite);
      
  }
	
	public String redirect() {
		
		if (FacesContext.getCurrentInstance().getViewRoot().getViewId().equals("/fdm/inviteTokenList.xhtml")) {
			return loadInvitationsList(systemUserInvite);
		}

		showCopySuccessMessage();
		return null;
	}
	
	public void setListInvitations() {
	  if(systemUserInvite) {
		  this.filteredInvitationList = this.invitationList = this.onlyActiveSystemInvitations ? this.activeInvitationList : this.allInvitationList;
		  
	  }else{
		  this.filteredInvitationList = this.invitationList = this.onlyActiveMobileInvitations ? this.activeInvitationList : this.allInvitationList;
	  }
	}
	
	public Invitations getSelectedUserInvitation() {
		return selectedUserInvitation;
	}

	public void setSelectedUserInvitation(Invitations selectedUserInvitation) {
		this.selectedUserInvitation = selectedUserInvitation;
	}

	public int getSelectedExpiryVal() {
		return selectedExpiryVal;
	}

	public void setSelectedExpiryVal(int selectedExpiryVal) {
		this.selectedExpiryVal = selectedExpiryVal;
	}

	public String getFdmGroupCode() {
		return fdmGroupCode;
	}

	public void setFdmGroupCode(String fdmGroupCode) {
		this.fdmGroupCode = fdmGroupCode;
	}

	public List<HPCWorkGroups> getWorkGroupList() {
		return workGroupList;
	}

	public void setWorkGroupList(List<HPCWorkGroups> workGroupList) {
		this.workGroupList = workGroupList;
	}

	public TreeNode getWorkgroupRoot() {
		return workgroupRoot;
	}

	public void setWorkgroupRoot(TreeNode workgroupRoot) {
		this.workgroupRoot = workgroupRoot;
	}

	public TreeNode getSelectedSingleWorkgroupNode() {
		return selectedSingleWorkgroupNode;
	}

	public void setSelectedSingleWorkgroupNode(TreeNode selectedSingleWorkgroupNode) {
		this.selectedSingleWorkgroupNode = selectedSingleWorkgroupNode;
	}

	public boolean isWorkgroupSelected() {
		return workgroupSelected;
	}

	public void setWorkgroupSelected(boolean workgroupSelected) {
		this.workgroupSelected = workgroupSelected;
	}

	public UserInvitationService getUserInvitationService() {
		return userInvitationService;
	}

	public void setUserInvitationService(UserInvitationService userInvitationService) {
		this.userInvitationService = userInvitationService;
	}

	public String getGeneratedInviteToken() {
		return generatedInviteToken;
	}

	public void setGeneratedInviteToken(String generatedInviteToken) {
		this.generatedInviteToken = generatedInviteToken;
	}

	public AccessTokenAuthor getAccessTokenAuthor() {
		return accessTokenAuthor;
	}

	public void setAccessTokenAuthor(AccessTokenAuthor accessTokenAuthor) {
		this.accessTokenAuthor = accessTokenAuthor;
	}
	
	public List<Invitations> getInvitationList() {
      return invitationList;
  }

  public void setInvitationList(List<Invitations> accessList) {
      this.invitationList = accessList;
  }
  
  public List<Invitations> getFilteredInvitationList() {
    return filteredInvitationList;
  }
  
  public String getTitle() {
    return this.systemUserInvite ? Properties.get("fdm_systemuser_invitation_token_tab_title") : Properties.get("fdm_mobile_user_invitation_token_title");
  }

  public void setFilteredInvitationList(List<Invitations> filteredInviteList) {
    this.filteredInvitationList = filteredInviteList;
  }

  public boolean isOnlyActiveMobileInvitations() {
    return onlyActiveMobileInvitations;
  }

  public void setOnlyActiveMobileInvitations(boolean onlyActiveMobileInvitations) {
    this.onlyActiveMobileInvitations = onlyActiveMobileInvitations;
  }

public SystemUsers getSelectedSystemUser() {
	return selectedSystemUser;
}

public void setSelectedSystemUser(SystemUsers selectedSystemUser) {
	this.selectedSystemUser = selectedSystemUser;
}

public List<ValidationProperty> getValidationProperty() {
	return validationProperty;
}

public void setValidationProperty(List<ValidationProperty> validationProperty) {
	this.validationProperty = validationProperty;
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

public List<AccessGroups> getAccessGroups() {
	return accessGroups;
}

public void setAccessGroups(List<AccessGroups> accessGroups) {
	this.accessGroups = accessGroups;
}

public boolean isSystemUserInvite() {
	return systemUserInvite;
}

public void setSystemUserInvite(boolean systemUserInvite) {
	this.systemUserInvite = systemUserInvite;
}

public boolean isOnlyActiveSystemInvitations() {
	return onlyActiveSystemInvitations;
}

public void setOnlyActiveSystemInvitations(boolean onlyActiveSystemInvitations) {
	this.onlyActiveSystemInvitations = onlyActiveSystemInvitations;
}
  
  

}
