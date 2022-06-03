package com.amtsybex.fieldreach;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.model.AccessToken;
import com.amtsybex.fieldreach.backend.model.SystemUsers;
import com.amtsybex.fieldreach.backend.service.AccessTokenService;
import com.amtsybex.fieldreach.backend.service.UserService;
import com.amtsybex.fieldreach.exception.ConfigException;
import com.amtsybex.fieldreach.services.utils.Utils;
import com.amtsybex.fieldreach.utils.AccessTokenAuthor;
import com.amtsybex.fieldreach.utils.AccessTokenAuthor.AUTHORITY;
import com.amtsybex.fieldreach.utils.impl.Common;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.EncryptedJWT;


/**
 * Request filter to validate AccessTokens for integration webservices
 * @author markcronin
 */
@Component
public class AccessTokenFilter extends OncePerRequestFilter {

	private static final Logger log = LoggerFactory.getLogger(AccessTokenFilter.class);
	
	@Autowired
	AccessTokenAuthor tokenAuthor;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AccessTokenService accessTokenService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		log.debug(">>> doFilterInternal");
		
		String authorizationHeader = request.getHeader("Authorization");
		
		String token = null;

        if (authorizationHeader != null) {
        	
        	if(!authorizationHeader.startsWith("Bearer ")) {
        		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  
				log.info("Unauthorised - token invalid");
			    return;
        	}
            token = authorizationHeader.substring(7);
            try {
            	
            	// Get application identifier
        		String applicationIdentifier = Utils.extractApplicationIdentifier(request);
        		
            	EncryptedJWT jwt = tokenAuthor.decodeJWT(token);
				
				if(jwt != null) {

					Date now = new Date();
					
					AccessToken dbAccessTokenInfo = accessTokenService.searchAccessTokenByUuid(applicationIdentifier, jwt.getJWTClaimsSet().getJWTID());

					if(dbAccessTokenInfo == null) {
						response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  
						log.info("Unauthorised - No token record found");
					    return;
					}
					
					//validate token hasn't been revoked
					if (dbAccessTokenInfo.getRevoked() == 1) {
						response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  
						log.info("Unauthorised - token revoked");
					    return;
					}

					//verify checksum (ensures token is the same as original created token)
					if (!dbAccessTokenInfo.getChecksum().equals(Common.generateSHA512Checksum(token.getBytes()))) {
						response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  
						log.info("Unauthorised - checksum mismatch");
					    return;
					}
					
					//Check expiry date
					if (now.after(jwt.getJWTClaimsSet().getExpirationTime())){
						response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  
						log.info("Unauthorised - expired token");
					    return;
					}
					
	                //Validate that the token has the required claims. for now all services are available if the authorities/IWS claim is present
	                Map<String,Object> claims = jwt.getJWTClaimsSet().getClaims();

					if(!((List<?>)claims.get("authorities")).contains(AUTHORITY.IWS.toString())) {
	                	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  
	                	log.info("Unauthorised - required authority missing");
					    return;
					}
					
					SystemUsers sysUser = userService.getSystemUser(applicationIdentifier, jwt.getJWTClaimsSet().getSubject());
					
					//validate linked user is valid and not revoked
	                if(sysUser == null || sysUser.getRevoked() == 1) {
	                	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  
	                	log.info("Unauthorised - system user invalid or revoked");
					    return;
	                }

	                //VALIDATION SUCCESS 
	                
					//Fake a UsernamePassword Token to continue with the filter stack.
					UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(sysUser, null, null);
				
	                SecurityContextHolder.getContext().setAuthentication(authReq);
	           

				}else {
					log.info("Unauthorised - token missing");
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  
				    return;
				}
				
			} catch (JOSEException e) {
				
				log.error("Error validating token", e);
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  
			    return;
			    
			} catch (ParseException e) {
				
				log.error("Error validating token", e);
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  
			    return;
			    
			} catch (FRInstanceException e) { 
				
				log.error("Error validating token", e);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); 
				return;
				
			} catch (ConfigException e) {
            	
            	log.error("Error validating token, possible key misconfiguration", e);
            	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); 
				return;
            	
			}
        }
        
        log.debug("<<< doFilterInternal");
        
        filterChain.doFilter(request, response);
	}

}
