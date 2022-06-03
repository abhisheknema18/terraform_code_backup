package com.amtsybex.fieldreach.services.endpoint.rest.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.model.HPCUsers;
import com.amtsybex.fieldreach.backend.service.UserService;
import com.amtsybex.fieldreach.services.endpoint.rest.impl.utils.HPCUsersUtil;
import com.amtsybex.fieldreach.services.endpoint.rest.impl.utils.JwtUtils;
import com.amtsybex.fieldreach.services.exception.AuthenticationException;
import com.amtsybex.fieldreach.services.utils.Utils;

@ExtendWith(MockitoExtension.class)
public class BaseControllerImplTest {
    
    @Mock
    private UserService userService;
    
    @Autowired
    @InjectMocks
    private BaseControllerImpl baseControllerImpl;
    
    @Test
    public void getUserDetailsFromUserPrincipal_checkValidUserReturns() throws FRInstanceException, AuthenticationException {
        
        JwtUtils.setMockSecurityContext();
        String applicationIdentifier = null;
        HPCUsers user = HPCUsersUtil.getUserDetails();
        
        JwtAuthenticationToken authentication  =  (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        when(authentication.getPrincipal()).thenReturn(user);

        HPCUsers loggendInUser = baseControllerImpl.getUserDetailsFromUserPrincipal(applicationIdentifier);
        
        assertTrue(loggendInUser.getId().getUserCode().equals(user.getId().getUserCode()));
        assertTrue(loggendInUser.getId().getWorkgroupCode().equals(user.getId().getWorkgroupCode()));
    }
    
    @Test
    public void getUserDetailsFromUserPrincipal_checkExceptiononNullUser() throws FRInstanceException, AuthenticationException {
        JwtUtils.setMockSecurityContext();
        String applicationIdentifier = null;
        
        JwtAuthenticationToken authentication  =  (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        when(authentication.getPrincipal()).thenReturn(null);
        
        Exception exception = assertThrows(AuthenticationException.class, () -> {
            baseControllerImpl.getUserDetailsFromUserPrincipal(applicationIdentifier);
        });

        String expectedMessage =Utils.USER_DOES_NOT_EXISTS;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

}
