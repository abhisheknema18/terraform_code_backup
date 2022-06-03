package com.amtsybex.fieldreach;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.amtsybex.fieldreach.backend.model.AccessToken;
import com.amtsybex.fieldreach.backend.model.SystemUsers;
import com.amtsybex.fieldreach.exception.ConfigException;
import com.amtsybex.fieldreach.services.endpoint.rest.impl.RestTimeControllerImpl;
import com.amtsybex.fieldreach.utils.AccessTokenAuthor.EXPIRY;
import com.amtsybex.fieldreach.utils.impl.Common;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class AuthenticationTokenFilterTest extends BaseTest{


	//going to use the time controller to do a base set of tests around authentication
	@InjectMocks
	@Autowired
    private RestTimeControllerImpl timeController;

    
    private MockMvc mockMvc;
    
    @BeforeEach
    public void setup() {
    	
    	super.setUp();
    	
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }
    
    @Test
    void serverTime_shouldReturnOKResponseWithServerTime_whenValidAuthenticationHeader() throws Exception {

    	// ARRANGE
    	String token = this.arrangeSuccessfulAuthentication();

        // ACT
        MvcResult mvcResult = this.mockMvc.perform(get("/time")
                .accept(MediaType.APPLICATION_XML)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();

        // ASSERT
        String response = mvcResult.getResponse().getContentAsString();
        assertNotNull(response);
    }
    
    @Test
    void serverTime_shouldReturnUnauthorized_whenTokenNotBearer() throws Exception {

    	// ARRANGE
    	AccessToken tokenObj = this.getAccessTokenDBObj();
    	String token = this.getAuthorizationToken(tokenObj);
    	tokenObj.setChecksum(Common.generateSHA512Checksum(token.getBytes()));

        // ACT
        MvcResult mvcResult = this.mockMvc.perform(get("/time")
                .accept(MediaType.APPLICATION_XML)
                .header("Authorization", "NOTBREARER " + token)
                .contentType(MediaType.APPLICATION_XML))
                .andExpect(status().isUnauthorized())
                .andReturn();

    }
    
    @Test
    void serverTime_shouldReturnUnauthorized_whenTokenNotValid() throws Exception {

    	// ARRANGE
    	AccessToken tokenObj = this.getAccessTokenDBObj();
    	String token = this.getAuthorizationToken(tokenObj);
    	tokenObj.setChecksum(Common.generateSHA512Checksum(token.getBytes()));

        // ACT
        MvcResult mvcResult = this.mockMvc.perform(get("/time")
                .accept(MediaType.APPLICATION_XML)
                .header("Authorization", "Bearer " + "some.invalid.token.value.entered")
                .contentType(MediaType.APPLICATION_XML))
                .andExpect(status().isUnauthorized())
                .andReturn();

    }
    
    @Test
    void serverTime_shouldReturnUnauthorized_whenTokenRevoked() throws Exception {

    	// ARRANGE
    	AccessToken tokenObj = this.getAccessTokenDBObj();
    	String token = this.getAuthorizationToken(tokenObj);
    	tokenObj.setChecksum(Common.generateSHA512Checksum(token.getBytes()));
    	//now revoke the token
    	tokenObj.setRevoked(1);
    	
    	when(accessTokenService.searchAccessTokenByUuid(null, tokenObj.getId())).thenReturn(tokenObj);
    	
        // ACT
        MvcResult mvcResult = this.mockMvc.perform(get("/time")
                .accept(MediaType.APPLICATION_XML)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_XML))
                .andExpect(status().isUnauthorized())
                .andReturn();

    }
    
    @Test
    void serverTime_shouldReturnUnauthorized_whenTokenExpired() throws Exception {

    	// ARRANGE
    	AccessToken tokenObj = this.getAccessTokenDBObj();
    	tokenObj.setExpiryDate(tokenObj.getCreateDate());
    	String token = this.getAuthorizationToken(tokenObj);
    	tokenObj.setChecksum(Common.generateSHA512Checksum(token.getBytes()));

    	
    	when(accessTokenService.searchAccessTokenByUuid(null, tokenObj.getId())).thenReturn(tokenObj);
    	
        // ACT
        MvcResult mvcResult = this.mockMvc.perform(get("/time")
                .accept(MediaType.APPLICATION_XML)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_XML))
                .andExpect(status().isUnauthorized())
                .andReturn();

    }
    
    @Test
    void serverTime_shouldReturnUnauthorized_whenNoIWSAuthority() throws Exception, ConfigException {

    	// ARRANGE
    	AccessToken tokenObj = this.getAccessTokenDBObj();
		String token = null;
		//token with no authority
		token = tokenAuthor.generateJWT(tokenObj.getId(), tokenObj.getLinkedUserCode(), Common.convertFieldreachDate(tokenObj.getExpiryDate()), Collections.singletonList(null));
		
    	tokenObj.setChecksum(Common.generateSHA512Checksum(token.getBytes()));

    	
    	when(accessTokenService.searchAccessTokenByUuid(null, tokenObj.getId())).thenReturn(tokenObj);
    	
        // ACT
        MvcResult mvcResult = this.mockMvc.perform(get("/time")
                .accept(MediaType.APPLICATION_XML)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_XML))
                .andExpect(status().isUnauthorized())
                .andReturn();

    }
    
    @Test
    void serverTime_shouldReturnUnauthorized_whenTokenChecksumNotValidExpiryModified() throws Exception {

    	// ARRANGE
    	AccessToken tokenObj = this.getAccessTokenDBObj();
    	
    	//try to copy a token and give yourself an extra 11 months. checksums should end up not matching
    	AccessToken tokenObjCopy = this.getAccessTokenDBObj();
		Date expiry = EXPIRY.MONTH_12.generateExpiry();
    	tokenObjCopy.setExpiryDate(Common.generateFieldreachDBDate(expiry));
    	tokenObjCopy.setId(tokenObj.getId());
    	
    	String tokenCopy = this.getAuthorizationToken(tokenObjCopy);
    	String token = this.getAuthorizationToken(tokenObjCopy);
    	
    	tokenObj.setChecksum(Common.generateSHA512Checksum(token.getBytes()));

    	when(accessTokenService.searchAccessTokenByUuid(null, tokenObj.getId())).thenReturn(tokenObj);
    	
        // ACT
        MvcResult mvcResult = this.mockMvc.perform(get("/time")
                .accept(MediaType.APPLICATION_XML)
                .header("Authorization", "Bearer " + tokenCopy)
                .contentType(MediaType.APPLICATION_XML))
                .andExpect(status().isUnauthorized())
                .andReturn();

    }
    
    @Test
    void serverTime_shouldReturnUnauthorized_whenTokenChecksumNotValidSwitchLinkedUser() throws Exception {

    	// ARRANGE
    	AccessToken tokenObj = this.getAccessTokenDBObj();
    	
    	//try to link a token to another user
    	AccessToken tokenObjCopy = this.getAccessTokenDBObj();
    	tokenObjCopy.setLinkedUserCode("FRADM2");
    	tokenObjCopy.setId(tokenObj.getId());
    	
    	String tokenCopy = this.getAuthorizationToken(tokenObjCopy);
    	String token = this.getAuthorizationToken(tokenObjCopy);
    	
    	tokenObj.setChecksum(Common.generateSHA512Checksum(token.getBytes()));

    	when(accessTokenService.searchAccessTokenByUuid(null, tokenObj.getId())).thenReturn(tokenObj);
    	
        // ACT
        MvcResult mvcResult = this.mockMvc.perform(get("/time")
                .accept(MediaType.APPLICATION_XML)
                .header("Authorization", "Bearer " + tokenCopy)
                .contentType(MediaType.APPLICATION_XML))
                .andExpect(status().isUnauthorized())
                .andReturn();

    }
    
    @Test
    void serverTime_shouldReturnForbidden_whenTokenNotPresent() throws Exception {

    	// ARRANGE
    	AccessToken tokenObj = this.getAccessTokenDBObj();
    	String token = this.getAuthorizationToken(tokenObj);
    	tokenObj.setChecksum(Common.generateSHA512Checksum(token.getBytes()));

        // ACT
        MvcResult mvcResult = this.mockMvc.perform(get("/time")
                .accept(MediaType.APPLICATION_XML)
                .contentType(MediaType.APPLICATION_XML))
                .andExpect(status().isForbidden())
                .andReturn();

    }
    
    @Test
    void serverTime_shouldReturnUnAuthorized_whenLinkedUserNotFound() throws Exception {

    	// ARRANGE
    	AccessToken tokenObj = this.getAccessTokenDBObj();
    	String token = this.getAuthorizationToken(tokenObj);
    	tokenObj.setChecksum(Common.generateSHA512Checksum(token.getBytes()));

    	when(userService.getSystemUser(any(), any())).thenReturn(null);
    	when(accessTokenService.searchAccessTokenByUuid(null, tokenObj.getId())).thenReturn(tokenObj);
    	
        // ACT
        MvcResult mvcResult = this.mockMvc.perform(get("/time")
                .accept(MediaType.APPLICATION_XML)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_XML))
                .andExpect(status().isUnauthorized())
                .andReturn();

        // ASSERT
        String response = mvcResult.getResponse().getContentAsString();
        assertNotNull(response);
    }
    
    @Test
    void serverTime_shouldReturnUnAuthorized_whenLinkedUserRevoked() throws Exception {

    	// ARRANGE
    	AccessToken tokenObj = this.getAccessTokenDBObj();
    	String token = this.getAuthorizationToken(tokenObj);
    	tokenObj.setChecksum(Common.generateSHA512Checksum(token.getBytes()));

    	SystemUsers user = this.getSystemUser();
    	user.setRevoked(1);
    	
    	when(userService.getSystemUser(any(), any())).thenReturn(user);
    	when(accessTokenService.searchAccessTokenByUuid(null, tokenObj.getId())).thenReturn(tokenObj);
    	
        // ACT
        MvcResult mvcResult = this.mockMvc.perform(get("/time")
                .accept(MediaType.APPLICATION_XML)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_XML))
                .andExpect(status().isUnauthorized())
                .andReturn();

        // ASSERT
        String response = mvcResult.getResponse().getContentAsString();
        assertNotNull(response);
    }
    
    

}
