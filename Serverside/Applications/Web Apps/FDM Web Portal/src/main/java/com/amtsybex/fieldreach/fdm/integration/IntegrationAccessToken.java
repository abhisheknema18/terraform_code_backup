package com.amtsybex.fieldreach.fdm.integration;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.deltaspike.core.api.scope.WindowScoped;
import org.primefaces.PrimeFaces;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.exception.UserNotFoundException;
import com.amtsybex.fieldreach.backend.model.AccessToken;
import com.amtsybex.fieldreach.backend.model.SystemUsers;
import com.amtsybex.fieldreach.exception.ConfigException;
import com.amtsybex.fieldreach.fdm.PageCodebase;
import com.amtsybex.fieldreach.fdm.web.jsf.util.MessageHelper;
import com.amtsybex.fieldreach.fdm.web.jsf.util.Properties;
import com.amtsybex.fieldreach.utils.AccessTokenAuthor;
import com.amtsybex.fieldreach.utils.AccessTokenAuthor.AUTHORITY;
import com.amtsybex.fieldreach.utils.AccessTokenAuthor.EXPIRY;
import com.amtsybex.fieldreach.utils.impl.Common;
import com.nimbusds.jose.JOSEException;

@Named
@WindowScoped
public class IntegrationAccessToken extends PageCodebase implements Serializable{

	private static final long serialVersionUID = 6346914831512065315L;

	private AccessToken selectedAccessToken;
	private List<AccessToken> accessList = null;
	private List<AccessToken> filteredAccessList = null;
	private int selectedExpiryVal;
	private String generatedToken;
	private List<SystemUsers> tokenCapableSystemUsers;
	

	@Inject
	private AccessTokenAuthor accessTokenAuthor;
	
	public void revokeAccessToken() {
		try {
				selectedAccessToken.setRevoked(1);
				selectedAccessToken.setRevokeDate(Common.generateFieldreachDBDate());
				selectedAccessToken.setRevokeUser(this.getUsername());
				this.getAccessTokenService().saveAccessToken(null, selectedAccessToken);
			
		} catch (FRInstanceException e) {
			
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
			return;
			
		}
		this.loadAccessTokenList();
	}

	public void loadAccessTokenList() {

		try {

			accessList = this.getAccessTokenService().searchAllAccessToken(null);
			filteredAccessList = accessList;

		} catch (FRInstanceException e) {

			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));

		}

		FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "accessTokenList");

	}

	public void addAccessToken() {
		init();
		selectedAccessToken.setId(UUID.randomUUID().toString());
		selectedAccessToken.setRevoked(0);
		selectedAccessToken.setCreateDate(Common.generateFieldreachDBDate());
		selectedAccessToken.setCreateTime(Common.generateFieldreachDBTime());
		selectedAccessToken.setCreateUser(this.getUsername());
	}


	public String generateToken() throws JOSEException, ConfigException {

		String token = this.accessTokenAuthor.generateJWT(selectedAccessToken.getId(),selectedAccessToken.getLinkedUserCode(),
				Common.convertFieldreachDate(selectedAccessToken.getExpiryDate()), this.getAuthority()); 

		return token;
	}

	private List<AUTHORITY> getAuthority(){

		List<AUTHORITY> authorities;

		authorities= Stream.of(AUTHORITY.values()).collect(Collectors.toList());

		return authorities;
	}

	public void calcExpiryDate() {

		Calendar cal = Calendar.getInstance(); 

		cal.add(Calendar.MONTH, this.getSelectedExpiryVal());

		selectedAccessToken.setExpiryDate(Common.generateFieldreachDBDate(cal.getTime()));

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

	public void saveAccessToken(){

		try {

			generatedToken = this.generateToken();

			selectedAccessToken.setChecksum(Common.generateSHA512Checksum(generatedToken.getBytes()));

		} catch (JOSEException e) {

			MessageHelper.setErrorMessage(null, Properties.get("fdm_token_generation_error"));
			FacesContext.getCurrentInstance().validationFailed();
			PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
			return;

		} catch (ConfigException e) {

			MessageHelper.setErrorMessage(null, Properties.get("fdm_token_generation_error"));
			FacesContext.getCurrentInstance().validationFailed();
			PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
			return;

		}

		try {

			this.getAccessTokenService().saveAccessToken(null, selectedAccessToken);

		} catch (FRInstanceException e) {

			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
			return;

		}

		PrimeFaces.current().executeScript("PF('showAccessTokenDialog').show();");

	} 

	
	public void redirect() {
		 if(FacesContext.getCurrentInstance().getViewRoot().getViewId().equals("/fdm/accessTokenList.xhtml")) {
			 this.loadAccessTokenList();
		 }
	}
	
	private void init() {

		selectedAccessToken = new AccessToken();
		this.selectedExpiryVal = 1;
		calcExpiryDate();

		try {
			tokenCapableSystemUsers = this.fetchSystemUsers();

		} catch (UserNotFoundException e) {

			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));

		} catch (FRInstanceException e) {

			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
		}
	}


	public List<SystemUsers> fetchSystemUsers() throws UserNotFoundException, FRInstanceException {

		List<SystemUsers> fetchedUsers = this.getUserService().searchTokenCapableSystemUsers(null);

		return fetchedUsers;
	}
	
	public void resetSelectionAfterFilter() {

		if (this.selectedAccessToken != null) {

			if (!this.filteredAccessList.contains(selectedAccessToken)) {
				this.filteredAccessList.remove(selectedAccessToken);

			}
		}
	}

	public String getTitle() {
		return  "Access Token List";
	}

	public List<AccessToken> getAccessList() {
		return accessList;
	}

	public void setAccessList(List<AccessToken> accessList) {
		this.accessList = accessList;
	}

	public AccessToken getSelectedAccessToken() {
		return selectedAccessToken;
	}

	public void setSelectedAccessToken(AccessToken selectedAccessToken) {
		this.selectedAccessToken = selectedAccessToken;
	}

	public int getSelectedExpiryVal() {
		return selectedExpiryVal;
	}

	public void setSelectedExpiryVal(int selectedExpiryVal) {
		this.selectedExpiryVal = selectedExpiryVal;
	}

	public String getGeneratedToken() {
		return generatedToken;
	}

	public void setGeneratedToken(String generatedToken) {
		this.generatedToken = generatedToken;
	}

	public List<SystemUsers> getTokenCapableSystemUsers() {
		return tokenCapableSystemUsers;
	}

	public void setTokenCapableSystemUsers(List<SystemUsers> tokenCapableSystemUsers) {
		this.tokenCapableSystemUsers = tokenCapableSystemUsers;
	}

	public List<AccessToken> getFilteredAccessList() {
		return filteredAccessList;
	}

	public void setFilteredAccessList(List<AccessToken> filteredAccessList) {
		this.filteredAccessList = filteredAccessList;
	}


}
