package com.amtsybex.fieldreach.security;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.model.GroupFunctions;
import com.amtsybex.fieldreach.backend.model.GroupFunctions.GROUPFUNCTION;
import com.amtsybex.fieldreach.backend.model.SystemUsers;
import com.amtsybex.fieldreach.backend.service.UserService;

@Controller
public class ApplicationAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler{

	@Autowired
	UserService userService;
	
	@Autowired
	LogoutHandler logoutHandler;
	
	
	@Autowired
    public ApplicationAuthenticationSuccessHandler() {
    }
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {

		
		if(authentication instanceof OAuth2AuthenticationToken) {

		  
			OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();

			String subject = oAuth2User.getAttribute("sub");
			URL issuer = oAuth2User.getAttribute("iss");
			 
			try {
				SystemUsers user = userService.getSystemUser(null, subject, issuer.toString());
				
				RequestCache requestCache = new HttpSessionRequestCache();
                SavedRequest savedRequest = requestCache.getRequest(request, response);
                String path ;
                try {
                  
                   path = new URI(savedRequest.getRedirectUrl()).getPath();
                } catch (URISyntaxException e) {
                  
                   path = StringUtils.EMPTY;
                }

                boolean isSystemUserInviteRequest = path.contains("Invite.xhtml") ? true : false;
				if(user == null && !isSystemUserInviteRequest) {	
					//if user was not found in Portal DB
					logoutHandler.logout(request, response, authentication);
					return;
					
				}
				
				if(!isSystemUserInviteRequest) {
	              Collection<? extends GrantedAuthority> authorities = getAuthorities(user, authentication);
	                
	                authentication = new OAuth2AuthenticationToken((OAuth2User)authentication.getPrincipal(), authorities, ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId());
				}

				
			} catch (FRInstanceException e) {
				e.printStackTrace();
			}

		}
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		super.onAuthenticationSuccess(request, response, authentication);


	}

	private Collection<? extends GrantedAuthority> getAuthorities(SystemUsers systemUser, Authentication authentication) throws FRInstanceException {

			Collection<GrantedAuthority> authList = new ArrayList<>();

			authList.addAll(authentication.getAuthorities());
			
			if (systemUser.getAdminUserInt() == 1){
				authList.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
				authList.add(new SimpleGrantedAuthority("ROLE_USER"));
			}
			else{
				authList.add(new SimpleGrantedAuthority("ROLE_USER"));
			}
			
			List <GroupFunctions> functions = this.userService.findGroupFunctions(null, systemUser.getFdmGroupCode());
			
			List<String> groupFunctions = new ArrayList<String>();
			
			//FDE053 - change this to get one list of function codes from DB instead of hitting the db for each one
			for(GroupFunctions function : functions) {
				
				groupFunctions.add(function.getId().getFunctionCode());
				
				if(function.getId().getFunctionCode().equals(GROUPFUNCTION.WOM.toString())) {
					authList.add(new SimpleGrantedAuthority("ROLE_WORK_MANAGEMENT"));
				}else if(function.getId().getFunctionCode().equals(GROUPFUNCTION.WOV.toString())) {
					authList.add(new SimpleGrantedAuthority("ROLE_WORK_VIEW"));
				}else if(function.getId().getFunctionCode().equals(GROUPFUNCTION.DBV.toString())) {
					authList.add(new SimpleGrantedAuthority("ROLE_DASHBOARD_VIEW"));
				}else if(function.getId().getFunctionCode().equals(GROUPFUNCTION.SAT.toString())) {
					authList.add(new SimpleGrantedAuthority("ROLE_ACCESS_TOKEN_VIEW"));
				}
				
			}
			
			//FDE053 - MC - remove dashboard items that are no longer needed
			if(!groupFunctions.contains(GROUPFUNCTION.DBV.toString())) {
				//FDE053 - MC - delete all dashboard items for user if dashboard view not allowed
				userService.removeDashboardUserItemsByUserCode(null, systemUser.getId());
			}else {
				//FDE053 - MC - clean up any unwanted dashboard items
				if(!groupFunctions.contains(GROUPFUNCTION.DBVAW.toString())) {
					userService.removeDashboardUserItemsByUserCodeAndMonitorType(null, systemUser.getId(), "WORK AT RISK");
				}
				if(!groupFunctions.contains(GROUPFUNCTION.DBVOW.toString())) {
					userService.removeDashboardUserItemsByUserCodeAndMonitorType(null, systemUser.getId(), "WORK OVERDUE");
				}
				if(!groupFunctions.contains(GROUPFUNCTION.DBVSR.toString())) {
					userService.removeDashboardUserItemsByUserCodeAndMonitorType(null, systemUser.getId(), "SCRIPT RESULT");
				}
				if(!groupFunctions.contains(GROUPFUNCTION.DBVUA.toString())) {
					userService.removeDashboardUserItemsByUserCodeAndMonitorType(null, systemUser.getId(), "USER ACCESS");
				}
				if(!groupFunctions.contains(GROUPFUNCTION.DBVWS.toString())) {
					userService.removeDashboardUserItemsByUserCodeAndMonitorType(null, systemUser.getId(), "WORK STATUS");
				}
			}

			return authList;
	}


}
