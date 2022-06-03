package com.amtsybex.fieldreach.services.endpoint.rest.impl;

import static com.amtsybex.fieldreach.services.endpoint.common.UserController.AUTHENTICATING_USER_REVOKED;
import static com.amtsybex.fieldreach.services.endpoint.common.UserController.INACTIVE_WORKGROUP;
import static com.amtsybex.fieldreach.services.endpoint.common.UserController.WORKGROUP_DOES_NOT_EXIST;
import static com.amtsybex.fieldreach.services.endpoint.rest.impl.utils.JwtUtils.IDENTITY_TOKEN;
import static com.amtsybex.fieldreach.services.endpoint.rest.impl.utils.JwtUtils.INVITE_TOKEN;
import static com.amtsybex.fieldreach.services.messages.request.AuthenticationToken.APPLICATION_VND_FIELDSMART_AUTHENTICATIONTOKEN_1_JSON;
import static com.amtsybex.fieldreach.services.messages.request.AuthenticationToken.APPLICATION_VND_FIELDSMART_AUTHENTICATIONTOKEN_1_XML;
import static com.amtsybex.fieldreach.services.messages.response.CallResponse.APPLICATION_VND_FIELDSMART_CALL_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.CallResponse.APPLICATION_VND_FIELDSMART_CALL_1_XML;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.amtsybex.fieldreach.services.endpoint.common.UserController.USER_INVITATION_REVOKED;
import static com.amtsybex.fieldreach.services.endpoint.common.UserController.USER_INVITATION_EXPIRED;
import static com.amtsybex.fieldreach.user.utils.UserInviteIdentityToken.INVALID_INVITE_SIGNATURE;


import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.amtsybex.fieldreach.backend.model.HPCUsers;
import com.amtsybex.fieldreach.backend.model.PersonalViews;
import com.amtsybex.fieldreach.backend.model.SystemParameters;
import com.amtsybex.fieldreach.backend.model.SystemUsers;
import com.amtsybex.fieldreach.backend.service.SystemParametersService;
import com.amtsybex.fieldreach.backend.service.UserService;
import com.amtsybex.fieldreach.exception.InvalidRequestTokenException;
import com.amtsybex.fieldreach.services.endpoint.common.ActivityLogController;
import com.amtsybex.fieldreach.services.endpoint.common.ActivityLogController.ACTIVITYTYPE;
import com.amtsybex.fieldreach.services.endpoint.common.UserController;
import com.amtsybex.fieldreach.services.endpoint.rest.impl.utils.HPCUsersUtil;
import com.amtsybex.fieldreach.services.endpoint.rest.impl.utils.JwtUtils;
import com.amtsybex.fieldreach.services.exception.AuthenticationException;
import com.amtsybex.fieldreach.services.exception.ExpiredMobileInvitationException;
import com.amtsybex.fieldreach.services.exception.InvalidWorkgroupException;
import com.amtsybex.fieldreach.services.exception.RevokedMobileInvitationException;
import com.amtsybex.fieldreach.services.messages.response.CallResponse;
import com.amtsybex.fieldreach.services.messages.response.GetPersonalViewResponse;
import com.amtsybex.fieldreach.services.messages.response.UserInfoResponse;
import com.amtsybex.fieldreach.services.messages.types.PersonalView;
import com.amtsybex.fieldreach.services.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class RestUserControllerImplTest {

    @Mock
    private UserService userService;
    
    @Mock
    private SystemParametersService systemParametersService;
    
    @Mock
    private ActivityLogController activityLogController;
    
    @Mock
    private UserController userController;
    
    @Spy
    @Autowired
    @InjectMocks
    private RestUserControllerImpl restUserController;

    private final XmlMapper xmlMapper = new XmlMapper();

    private final ObjectMapper jsonMapper = new ObjectMapper();

    private MockMvc mockMvc;
    


    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(restUserController).build();
    }
    
    @Test
    void getPersonalViews_shouldReturnPersonalViews_whenRequestAcceptHeaderInXml() throws Exception {
        // Arrange
        String user = "TEST";
        List<PersonalViews> stubbedPersonalViews = this.getStubbedPersonalViews();
        List<HPCUsers> hpcUsersList = new ArrayList<>();
        hpcUsersList.add(HPCUsersUtil.getUserDetails());
        when(userService.getPersonalViews(any(), any())).thenReturn(stubbedPersonalViews);
        when(userService.findHPCUsersByUserCode(any(), any())).thenReturn(hpcUsersList);
        when(userService.getSystemUser(any(), any(), any())).thenReturn(new SystemUsers());

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/user/" + user + "/personalViews")
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();
        
        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        GetPersonalViewResponse response = xmlMapper.readValue(contentAsString, GetPersonalViewResponse.class);
//        assertEquals(contentAsString, "<GetPersonalViewResponse><error/><success>true</success><personalViewList><PersonalView><viewId>1</viewId><viewName>Name</viewName><viewDesc>Description</viewDesc><viewDefault>false</viewDefault></PersonalView></personalViewList></GetPersonalViewResponse>");
        assertTrue(response.isSuccess());
        assertFalse(response.getPersonalViewList().isEmpty());
        PersonalView personalView = response.getPersonalViewList().get(0);
        assertEquals(personalView.getViewId(), stubbedPersonalViews.get(0).getId().toString());
        assertEquals(personalView.getViewName(), stubbedPersonalViews.get(0).getViewName());
        assertEquals(personalView.getViewDesc(), stubbedPersonalViews.get(0).getViewDesc());
    }
    
    @Test
    void getPersonalViews_shouldReturnPersonalViews_whenRequestAcceptHeaderInJson() throws Exception {
        // Arrange
        String user = "TEST";
        List<PersonalViews> stubbedPersonalViews = this.getStubbedPersonalViews();
        List<HPCUsers> hpcUsersList = new ArrayList<>();
        hpcUsersList.add(HPCUsersUtil.getUserDetails());
        when(userService.getPersonalViews(any(), any())).thenReturn(stubbedPersonalViews);
        when(userService.findHPCUsersByUserCode(any(), any())).thenReturn(hpcUsersList);
        when(userService.getSystemUser(any(), any(), any())).thenReturn(new SystemUsers());

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/user/" + user + "/personalViews")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        GetPersonalViewResponse response = jsonMapper.readValue(contentAsString, GetPersonalViewResponse.class);
        assertTrue(response.isSuccess());
        assertFalse(response.getPersonalViewList().isEmpty());
        PersonalView personalView = response.getPersonalViewList().get(0);
        assertEquals(personalView.getViewId(), stubbedPersonalViews.get(0).getId().toString());
        assertEquals(personalView.getViewName(), stubbedPersonalViews.get(0).getViewName());
        assertEquals(personalView.getViewDesc(), stubbedPersonalViews.get(0).getViewDesc());
    }

    @Test
    void getUserInfo_shouldReturnUserNotFound_whenNotAuthenticated() throws Exception {
        // Arrange
        doReturn(HPCUsersUtil.getUserDetails()).when(restUserController).getUserDetailsFromUserPrincipal(any());
        
        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/user/info")
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();
        
        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        UserInfoResponse response = xmlMapper.readValue(contentAsString, UserInfoResponse.class);
        assertEquals(response.getError().getErrorCode(), "AuthenticationException");
        assertEquals(response.getError().getErrorDescription(), "User cannot be authenticated");
    }
    
    @Test
    void getUserInfo_shouldReturnUserInfo_whenAuthenticated() throws Exception {
        // Arrange
        when(systemParametersService.getSystemParams(any())).thenReturn(new SystemParameters());
        when(userService.getSystemUser(any(), any(), any())).thenReturn(new SystemUsers());
        when(userService.getMobileAppCodes(any(),any())).thenReturn(new ArrayList<String>());
        doReturn(HPCUsersUtil.getUserDetails()).when(restUserController).getUserDetailsFromUserPrincipal(any());

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/user/info")
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();
        
        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        UserInfoResponse response = xmlMapper.readValue(contentAsString, UserInfoResponse.class);
        assertTrue(response.isSuccess());
        assertNotNull(response);
        assertNotNull(response.getUserCode());
        assertNotNull(response.getUserName());
    }
    
    @Test
    void getUserInfo_shouldReturnMapKeyPresent_whenAuthenticated() throws Exception {
    	
    	SystemParameters systemParameters = new SystemParameters();
    	systemParameters.setMapKeyBing("testBingMapKey");
        // Arrange
        when(systemParametersService.getSystemParams(any())).thenReturn(systemParameters);
        when(userService.getSystemUser(any(), any(), any())).thenReturn(new SystemUsers());
        when(userService.getMobileAppCodes(any(),any())).thenReturn(new ArrayList<String>());
        doReturn(HPCUsersUtil.getUserDetails()).when(restUserController).getUserDetailsFromUserPrincipal(any());

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/user/info")
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();
        
        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        UserInfoResponse response = xmlMapper.readValue(contentAsString, UserInfoResponse.class);
        assertTrue(response.isSuccess());
        assertNotNull(response);
        assertEquals(response.getMapkeyBing(), "testBingMapKey");

    }
    
    @Test
    void getUserInfo_shouldReturnBlankMapKey_whenBingMapKeyNotSet() throws Exception {
    	
        // Arrange
        when(systemParametersService.getSystemParams(any())).thenReturn(new SystemParameters());
        when(userService.getSystemUser(any(), any(), any())).thenReturn(new SystemUsers());
        when(userService.getMobileAppCodes(any(),any())).thenReturn(new ArrayList<String>());
        doReturn(HPCUsersUtil.getUserDetails()).when(restUserController).getUserDetailsFromUserPrincipal(any());

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/user/info")
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();
        
        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        UserInfoResponse response = xmlMapper.readValue(contentAsString, UserInfoResponse.class);
        assertTrue(response.isSuccess());
        assertNotNull(response);
        assertEquals(response.getMapkeyBing(), "");
    }
    
    @Test
    void authenticateUser_shouldReturnsuccess_whenValidRequest() 
            throws Exception {
        
        // Arrange
        doReturn(HPCUsersUtil.getUserDetails()).when(restUserController).getUserDetailsFromUserPrincipal(any());

        // Act
        MvcResult mvcResult = this.mockMvc.perform(put("/user/authenticated")
                .content(JwtUtils.getAuthenticatedRequestXml(IDENTITY_TOKEN))
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();
        
        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        CallResponse response = xmlMapper.readValue(contentAsString, CallResponse.class);
        assertTrue(response.isSuccess());
        assertNotNull(response);
    }
    
    @Test
    void authenticateUser_shouldReturnError_whenExistingUserIsRevoked() 
            throws Exception {
        
        // Arrange
        HPCUsers userDetails = HPCUsersUtil.getUserDetails();
        userDetails.setRevokedBool(true);
        doReturn(userDetails).when(restUserController).getUserDetailsFromUserPrincipal(any());

        // Act
        MvcResult mvcResult = this.mockMvc.perform(put("/user/authenticated")
                .content(JwtUtils.getAuthenticatedRequestXml(IDENTITY_TOKEN))
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();
        
        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        CallResponse response = xmlMapper.readValue(contentAsString, CallResponse.class);
        assertFalse(response.isSuccess());
        assertEquals(response.getError().getErrorCode(),Utils.USER_REVOKED);
        assertEquals(response.getError().getErrorDescription(),AUTHENTICATING_USER_REVOKED);
        assertNotNull(response);
    }
    
    @Test
    void authenticateUser_shouldAddActivity_whenExistingUserIsCalling() 
            throws Exception {
        
        // Arrange
        HPCUsers userDetails = HPCUsersUtil.getUserDetails();
        doReturn(userDetails).when(restUserController).getUserDetailsFromUserPrincipal(any());

        // Act
        MvcResult mvcResult = this.mockMvc.perform(put("/user/authenticated")
                .content(JwtUtils.getAuthenticatedRequestXml(IDENTITY_TOKEN))
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();
        
        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        CallResponse response = xmlMapper.readValue(contentAsString, CallResponse.class);
        assertTrue(response.isSuccess());
        verify(activityLogController, times(1)).recordActivityLog(null, ACTIVITYTYPE.MOBILELOGIN, userDetails.getId().getUserCode(), null, null, "Mobile User " + userDetails.getId().getUserCode() + " Login", null);
        assertNotNull(response);
    }
    
    
    @Test
    void authenticateUser_shouldReturnUnauthorised_whenNewUserIsCallingWithoutInviteToken() 
            throws Exception {
        
        // Arrange
        doThrow(AuthenticationException.class).when(restUserController).getUserDetailsFromUserPrincipal(any());
        
        when(userController.addHPCUser(any(), any()))
        .thenThrow(new InvalidRequestTokenException("Empty invite token"));

        // Act
        MvcResult mvcResult = this.mockMvc.perform(put("/user/authenticated")
                .content(JwtUtils.getAuthenticatedRequestXml(IDENTITY_TOKEN))
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }
    
    @Test
    void authenticateUser_shouldReturnWorkGroupNotExists_whenCalledWithInvlidWorkGroup() 
            throws Exception {
        
        // Arrange
        doThrow(AuthenticationException.class).when(restUserController).getUserDetailsFromUserPrincipal(any());
        when(userController.addHPCUser(any(),  any()))
        .thenThrow(new InvalidWorkgroupException(WORKGROUP_DOES_NOT_EXIST));

        // Act
        MvcResult mvcResult = this.mockMvc.perform(put("/user/authenticated")
                .content(JwtUtils.getAuthenticatedRequestXml(IDENTITY_TOKEN, INVITE_TOKEN))
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();
        
        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        CallResponse response = xmlMapper.readValue(contentAsString, CallResponse.class);
        assertFalse(response.isSuccess());
        assertEquals(response.getError().getErrorCode(),Utils.INVALID_WORKGROUP_EXCEPTION);
        assertEquals(response.getError().getErrorDescription(),WORKGROUP_DOES_NOT_EXIST);
        assertNotNull(response);
    }
    
    @Test
    void authenticateUser_shouldReturnWorkGroupNotActive_whenCalledWithInactiveWorkGroup() 
            throws Exception {
        
        // Arrange
        doThrow(AuthenticationException.class).when(restUserController).getUserDetailsFromUserPrincipal(any());
        when(userController.addHPCUser(any(), any()))
        .thenThrow(new InvalidWorkgroupException(INACTIVE_WORKGROUP));

        // Act
        MvcResult mvcResult = this.mockMvc.perform(put("/user/authenticated")
                .content(JwtUtils.getAuthenticatedRequestXml(IDENTITY_TOKEN, INVITE_TOKEN))
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();
        
        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        CallResponse response = xmlMapper.readValue(contentAsString, CallResponse.class);
        assertFalse(response.isSuccess());
        assertEquals(response.getError().getErrorCode(),Utils.INVALID_WORKGROUP_EXCEPTION);
        assertEquals(response.getError().getErrorDescription(),INACTIVE_WORKGROUP);
        assertNotNull(response);
    }
    
    
    
    @Test
    void authenticateUser_shouldReturnSuccess_whenCalledWithValidIdentityToken() 
            throws Exception {
        
        // Arrange
        doThrow(AuthenticationException.class).when(restUserController).getUserDetailsFromUserPrincipal(any());

        when(userController.addHPCUser(any(), any()))
            .thenReturn(HPCUsersUtil.getUserDetails());
        // Act
        MvcResult mvcResult = this.mockMvc.perform(put("/user/authenticated")
                .content(JwtUtils.getAuthenticatedRequestXml(IDENTITY_TOKEN, INVITE_TOKEN))
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();
        
        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        CallResponse response = xmlMapper.readValue(contentAsString, CallResponse.class);
        assertTrue(response.isSuccess());
        verify(userController, times(1)).addHPCUser(any(), any());
        verify(activityLogController, times(1)).recordActivityLog(any(), any(), any(), any(),any(), any(), any());
        assertNotNull(response);
    }  
    
    @Test
    void authenticateUser_shouldReturnsuccess_whenValidRequestContentTypeXML() 
            throws Exception {
        
        // Arrange
        doReturn(HPCUsersUtil.getUserDetails()).when(restUserController).getUserDetailsFromUserPrincipal(any());

        // Act
        MvcResult mvcResult = this.mockMvc.perform(put("/user/authenticated")
                .content(JwtUtils.getAuthenticatedRequestXml(IDENTITY_TOKEN))
                .contentType(APPLICATION_VND_FIELDSMART_AUTHENTICATIONTOKEN_1_XML)
                .accept(APPLICATION_VND_FIELDSMART_CALL_1_XML))
                .andExpect(status().isOk())
                .andReturn();
        
        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        String contentType = mvcResult.getResponse().getContentType();
        CallResponse response = xmlMapper.readValue(contentAsString, CallResponse.class);
        assertTrue(response.isSuccess());
        assertNotNull(response);
        assertEquals(APPLICATION_VND_FIELDSMART_CALL_1_XML,contentType);
    }
    
    @Test
    void authenticateUser_shouldReturnsuccess_whenValidRequestContentTypeJSON() 
            throws Exception {
        
        // Arrange
        doReturn(HPCUsersUtil.getUserDetails()).when(restUserController).getUserDetailsFromUserPrincipal(any());

        // Act
        MvcResult mvcResult = this.mockMvc.perform(put("/user/authenticated")
                .content(JwtUtils.getAuthenticatedRequestJson(IDENTITY_TOKEN))
                .contentType(APPLICATION_VND_FIELDSMART_AUTHENTICATIONTOKEN_1_JSON)
                .accept(APPLICATION_VND_FIELDSMART_CALL_1_JSON))
                .andExpect(status().isOk())
                .andReturn();
        
        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        String contentType = mvcResult.getResponse().getContentType();
        CallResponse response = jsonMapper.readValue(contentAsString, CallResponse.class);
        assertTrue(response.isSuccess());
        assertNotNull(response);
        assertEquals(APPLICATION_VND_FIELDSMART_CALL_1_JSON,contentType);
    }
    
    @Test
    void authenticateUser_shouldReturnBadRequest_whenCalledWithInvalidInviteSignature() 
            throws Exception {
        
        // Arrange
        doThrow(AuthenticationException.class).when(restUserController).getUserDetailsFromUserPrincipal(any());

        when(userController.addHPCUser(any(), any()))
        .thenThrow(new InvalidRequestTokenException(INVALID_INVITE_SIGNATURE));
            
        // Act
        MvcResult mvcResult = this.mockMvc.perform(put("/user/authenticated")
                .content(JwtUtils.getAuthenticatedRequestXml(IDENTITY_TOKEN, INVITE_TOKEN))
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isBadRequest())
                .andReturn();
        

        String contentAsString = mvcResult.getResponse().getContentAsString();
        verify(userController, times(1)).addHPCUser(any(), any());
        assertTrue(contentAsString.isEmpty());
    }  
    
    @Test
    void authenticateUser_shouldReturnException_whenCalledWithRevokedInvitation() 
            throws Exception {
        
        // Arrange
        doThrow(AuthenticationException.class).when(restUserController).getUserDetailsFromUserPrincipal(any());

        when(userController.addHPCUser(any(), any()))
        .thenThrow(new RevokedMobileInvitationException(USER_INVITATION_REVOKED));
            
        // Act
        MvcResult mvcResult = this.mockMvc.perform(put("/user/authenticated")
                .content(JwtUtils.getAuthenticatedRequestXml(IDENTITY_TOKEN, INVITE_TOKEN))
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();
        

        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        CallResponse response = xmlMapper.readValue(contentAsString, CallResponse.class);
        assertFalse(response.isSuccess());
        assertEquals(response.getError().getErrorCode(),Utils.REVOKED_MOBILE_INVITATION_EXCEPTION);
        assertEquals(USER_INVITATION_REVOKED, response.getError().getErrorDescription());
        assertNotNull(response);
    }  
    
    @Test
    void authenticateUser_shouldReturnException_whenCalledWithExpiredInvitation() 
            throws Exception {
        
        // Arrange
        doThrow(AuthenticationException.class).when(restUserController).getUserDetailsFromUserPrincipal(any());

        when(userController.addHPCUser(any(), any()))
        .thenThrow(new ExpiredMobileInvitationException(USER_INVITATION_EXPIRED));
            
        // Act
        MvcResult mvcResult = this.mockMvc.perform(put("/user/authenticated")
                .content(JwtUtils.getAuthenticatedRequestXml(IDENTITY_TOKEN, INVITE_TOKEN))
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();
        

        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        CallResponse response = xmlMapper.readValue(contentAsString, CallResponse.class);
        assertFalse(response.isSuccess());
        assertEquals(response.getError().getErrorCode(),Utils.EXPIRED_MOBILE_INVITATION_EXCEPTION);
        assertEquals(USER_INVITATION_EXPIRED, response.getError().getErrorDescription());
        assertNotNull(response);
    }
    
    private List<PersonalViews> getStubbedPersonalViews() {
        
        PersonalViews personalView = new PersonalViews();
        personalView.setId(1);
        personalView.setViewName("Name");
        personalView.setViewDesc("Description");
        ArrayList<PersonalViews> list = new ArrayList<>();
        list.add(personalView);
        return list;
    }
    
   
   
}