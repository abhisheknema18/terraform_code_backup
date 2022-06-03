package com.amtsybex.fieldreach.services.endpoint.common.impl;

import static com.amtsybex.fieldreach.services.endpoint.rest.impl.utils.JwtUtils.IDENTITY_TOKEN;
import static com.amtsybex.fieldreach.services.endpoint.rest.impl.utils.JwtUtils.INVITE_TOKEN;
import static com.amtsybex.fieldreach.services.endpoint.common.UserController.INACTIVE_WORKGROUP;
import static com.amtsybex.fieldreach.services.endpoint.common.UserController.INVITE_TOKEN_MISSING_CLAIMS;
import static com.amtsybex.fieldreach.services.endpoint.common.UserController.INVITE_TOKEN_PARSE_ERROR;
import static com.amtsybex.fieldreach.services.endpoint.common.UserController.USERNAME_MAX_LENGTH;
import static com.amtsybex.fieldreach.services.endpoint.common.UserController.USER_CODE_COULD_NOT_GENERATED;
import static com.amtsybex.fieldreach.services.endpoint.common.UserController.WORKGROUP_DOES_NOT_EXIST;
import static com.amtsybex.fieldreach.services.endpoint.common.UserController.USER_INVITATION_REVOKED;
import static com.amtsybex.fieldreach.services.endpoint.common.UserController.USER_INVITATION_EXPIRED;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;


import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.exception.InvalidUserCodeException;
import com.amtsybex.fieldreach.backend.model.HPCUsers;
import com.amtsybex.fieldreach.backend.model.HPCWorkGroups;
import com.amtsybex.fieldreach.backend.model.Invitations;
import com.amtsybex.fieldreach.backend.service.UserInvitationService;
import com.amtsybex.fieldreach.backend.service.UserService;
import com.amtsybex.fieldreach.exception.InvalidRequestTokenException;
import com.amtsybex.fieldreach.services.endpoint.common.UserController;
import com.amtsybex.fieldreach.services.endpoint.common.UserInviteIdentityToken;
import com.amtsybex.fieldreach.services.endpoint.rest.impl.utils.HPCUsersUtil;
import com.amtsybex.fieldreach.services.endpoint.rest.impl.utils.HPCWorkGroupsUtil;
import com.amtsybex.fieldreach.services.endpoint.rest.impl.utils.InvitationsUtil;
import com.amtsybex.fieldreach.services.endpoint.rest.impl.utils.JwtUtils;
import com.amtsybex.fieldreach.services.exception.ExpiredMobileInvitationException;
import com.amtsybex.fieldreach.services.exception.InvalidWorkgroupException;
import com.amtsybex.fieldreach.services.exception.RevokedMobileInvitationException;
import com.amtsybex.fieldreach.services.exception.UserRevokedException;
import com.amtsybex.fieldreach.services.messages.request.AuthenticationToken;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.minidev.json.JSONObject;

@ExtendWith(MockitoExtension.class)
public class UserControllerImplTest {
    
    @Mock
    private UserService userService;
    
    @Mock
    private UserInviteIdentityToken userInviteIdentityToken;
    
    @Mock
    private UserInvitationService userInvitationService;
    
    @Spy
    private  ObjectMapper objectMapper;
    
    @Autowired
    @InjectMocks
    private UserControllerImpl userController;
    
    @BeforeEach
    public void setup() {
        userController.setUserCode("nickname");
        userController.setUserName("name");
    }  
    
    @ParameterizedTest
    @ValueSource(strings = {"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjR4dEotLTVCMzU3aW1zNlRoQTF5SiJ9.ewogICJuaWNrbmFtZSI6ICJnYW5lc2gucGF0aWwiLAogICJwaWN0dXJlIjogImh0dHBzOi8vcy5ncmF2YXRhci5jb20vYXZhdGFyLzhlODY4ODJmMDJjZmE3YmYxYjZkNmVhMGQwNjRlZjc3P3M9NDgwJnI9cGcmZD1odHRwcyUzQSUyRiUyRmNkbi5hdXRoMC5jb20lMkZhdmF0YXJzJTJGZ2EucG5nIiwKICAidXBkYXRlZF9hdCI6ICIyMDIxLTExLTE4VDEwOjIzOjU2LjE2MVoiLAogICJpc3MiOiAiaHR0cHM6Ly9tY2FtdC5ldS5hdXRoMC5jb20vIiwKICAic3ViIjogImF1dGgwfDYxOTQ5MjllZTg3YTlhMDA2ODcxMDA5OCIsCiAgImF1ZCI6ICJ0bjQ3bU5PNEo0Z3lTWERHM2JodWMxMW1pWUk1SmduaCIsCiAgImlhdCI6IDE2MzczMDA3NTAsCiAgImV4cCI6IDE2Njg4NTgzNTAKfQ==.GdjsIVQjUbsSsX8ZtacGbm8TgR3o-tdG49GtPFvzYlT8VYaNjjHtwPq6yCR9qrp7lotaEfJpz5B63KbVD4zuc-GXqXXNuukvQWs0UnX-kMnLtldJuJk7qh4BGVKbDFGftf7STxmWPvEvcQ3zMNosE-l5nZiaH_VTtRQkJjIibW7pcKtoeWwWg6r_11NcZ_FbeNVh3bHEiLVIBM_UDU5_jiAXDJ0xHOTGhqZsffyxDqiRoXfWinq6Y3bq0kb6LHwzg5JT-1lElafgoRwxRkvxyZNsLc34V7BswemjDW5_Zb8pk8hvQXG1Z2u67i9ARjCCrPNCB7C7TwvHUNPlzCq1ow", // Identity Token without username field
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjR4dEotLTVCMzU3aW1zNlRoQTF5SiJ9.ewogICJuaWNrbmFtZSI6ICJnYW5lc2gucGF0aWwiLAogICJuYW1lIjogIiIsCiAgInBpY3R1cmUiOiAiaHR0cHM6Ly9zLmdyYXZhdGFyLmNvbS9hdmF0YXIvOGU4Njg4MmYwMmNmYTdiZjFiNmQ2ZWEwZDA2NGVmNzc/cz00ODAmcj1wZyZkPWh0dHBzJTNBJTJGJTJGY2RuLmF1dGgwLmNvbSUyRmF2YXRhcnMlMkZnYS5wbmciLAogICJ1cGRhdGVkX2F0IjogIjIwMjEtMTEtMThUMTA6MjM6NTYuMTYxWiIsCiAgImlzcyI6ICJodHRwczovL21jYW10LmV1LmF1dGgwLmNvbS8iLAogICJzdWIiOiAiYXV0aDB8NjE5NDkyOWVlODdhOWEwMDY4NzEwMDk4IiwKICAiYXVkIjogInRuNDdtTk80SjRneVNYREczYmh1YzExbWlZSTVKZ25oIiwKICAiaWF0IjogMTYzNzMwMDc1MCwKICAiZXhwIjogMTY2ODg1ODM1MAp9.GdjsIVQjUbsSsX8ZtacGbm8TgR3o-tdG49GtPFvzYlT8VYaNjjHtwPq6yCR9qrp7lotaEfJpz5B63KbVD4zuc-GXqXXNuukvQWs0UnX-kMnLtldJuJk7qh4BGVKbDFGftf7STxmWPvEvcQ3zMNosE-l5nZiaH_VTtRQkJjIibW7pcKtoeWwWg6r_11NcZ_FbeNVh3bHEiLVIBM_UDU5_jiAXDJ0xHOTGhqZsffyxDqiRoXfWinq6Y3bq0kb6LHwzg5JT-1lElafgoRwxRkvxyZNsLc34V7BswemjDW5_Zb8pk8hvQXG1Z2u67i9ARjCCrPNCB7C7TwvHUNPlzCq1ow", // Identity Token with empty username
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjR4dEotLTVCMzU3aW1zNlRoQTF5SiJ9.ewogICJuaWNrbmFtZSI6ICJnYW5lc2gucGF0aWwiLAogICJuYW1lIjogIioqJiYlJSUiLAogICJwaWN0dXJlIjogImh0dHBzOi8vcy5ncmF2YXRhci5jb20vYXZhdGFyLzhlODY4ODJmMDJjZmE3YmYxYjZkNmVhMGQwNjRlZjc3P3M9NDgwJnI9cGcmZD1odHRwcyUzQSUyRiUyRmNkbi5hdXRoMC5jb20lMkZhdmF0YXJzJTJGZ2EucG5nIiwKICAidXBkYXRlZF9hdCI6ICIyMDIxLTExLTE4VDEwOjIzOjU2LjE2MVoiLAogICJpc3MiOiAiaHR0cHM6Ly9tY2FtdC5ldS5hdXRoMC5jb20vIiwKICAic3ViIjogImF1dGgwfDYxOTQ5MjllZTg3YTlhMDA2ODcxMDA5OCIsCiAgImF1ZCI6ICJ0bjQ3bU5PNEo0Z3lTWERHM2JodWMxMW1pWUk1SmduaCIsCiAgImlhdCI6IDE2MzczMDA3NTAsCiAgImV4cCI6IDE2Njg4NTgzNTAKfQ==.GdjsIVQjUbsSsX8ZtacGbm8TgR3o-tdG49GtPFvzYlT8VYaNjjHtwPq6yCR9qrp7lotaEfJpz5B63KbVD4zuc-GXqXXNuukvQWs0UnX-kMnLtldJuJk7qh4BGVKbDFGftf7STxmWPvEvcQ3zMNosE-l5nZiaH_VTtRQkJjIibW7pcKtoeWwWg6r_11NcZ_FbeNVh3bHEiLVIBM_UDU5_jiAXDJ0xHOTGhqZsffyxDqiRoXfWinq6Y3bq0kb6LHwzg5JT-1lElafgoRwxRkvxyZNsLc34V7BswemjDW5_Zb8pk8hvQXG1Z2u67i9ARjCCrPNCB7C7TwvHUNPlzCq1ow", // Identity Token  username with only special characters 
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjR4dEotLTVCMzU3aW1zNlRoQTF5SiJ9.ewogICJuaWNrbmFtZSI6ICJnYW5lc2gucGF0aWwiLAogICJuYW1lIjogIiAgICAgICAgICAiLAogICJwaWN0dXJlIjogImh0dHBzOi8vcy5ncmF2YXRhci5jb20vYXZhdGFyLzhlODY4ODJmMDJjZmE3YmYxYjZkNmVhMGQwNjRlZjc3P3M9NDgwJnI9cGcmZD1odHRwcyUzQSUyRiUyRmNkbi5hdXRoMC5jb20lMkZhdmF0YXJzJTJGZ2EucG5nIiwKICAidXBkYXRlZF9hdCI6ICIyMDIxLTExLTE4VDEwOjIzOjU2LjE2MVoiLAogICJpc3MiOiAiaHR0cHM6Ly9tY2FtdC5ldS5hdXRoMC5jb20vIiwKICAic3ViIjogImF1dGgwfDYxOTQ5MjllZTg3YTlhMDA2ODcxMDA5OCIsCiAgImF1ZCI6ICJ0bjQ3bU5PNEo0Z3lTWERHM2JodWMxMW1pWUk1SmduaCIsCiAgImlhdCI6IDE2MzczMDA3NTAsCiAgImV4cCI6IDE2Njg4NTgzNTAKfQ==.GdjsIVQjUbsSsX8ZtacGbm8TgR3o-tdG49GtPFvzYlT8VYaNjjHtwPq6yCR9qrp7lotaEfJpz5B63KbVD4zuc-GXqXXNuukvQWs0UnX-kMnLtldJuJk7qh4BGVKbDFGftf7STxmWPvEvcQ3zMNosE-l5nZiaH_VTtRQkJjIibW7pcKtoeWwWg6r_11NcZ_FbeNVh3bHEiLVIBM_UDU5_jiAXDJ0xHOTGhqZsffyxDqiRoXfWinq6Y3bq0kb6LHwzg5JT-1lElafgoRwxRkvxyZNsLc34V7BswemjDW5_Zb8pk8hvQXG1Z2u67i9ARjCCrPNCB7C7TwvHUNPlzCq1ow", // Identity Token username with only spaces 
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjR4dEotLTVCMzU3aW1zNlRoQTF5SiJ9.ewogICJuaWNrbmFtZSI6ICIiLAogICJuYW1lIjogIkdhbmVzaCBQYXRpbCIsCiAgInBpY3R1cmUiOiAiaHR0cHM6Ly9zLmdyYXZhdGFyLmNvbS9hdmF0YXIvOGU4Njg4MmYwMmNmYTdiZjFiNmQ2ZWEwZDA2NGVmNzc/cz00ODAmcj1wZyZkPWh0dHBzJTNBJTJGJTJGY2RuLmF1dGgwLmNvbSUyRmF2YXRhcnMlMkZnYS5wbmciLAogICJ1cGRhdGVkX2F0IjogIjIwMjEtMTEtMThUMTA6MjM6NTYuMTYxWiIsCiAgImlzcyI6ICJodHRwczovL21jYW10LmV1LmF1dGgwLmNvbS8iLAogICJzdWIiOiAiYXV0aDB8NjE5NDkyOWVlODdhOWEwMDY4NzEwMDk4IiwKICAiYXVkIjogInRuNDdtTk80SjRneVNYREczYmh1YzExbWlZSTVKZ25oIiwKICAiaWF0IjogMTYzNzMwMDc1MCwKICAiZXhwIjogMTY2ODg1ODM1MAp9.GdjsIVQjUbsSsX8ZtacGbm8TgR3o-tdG49GtPFvzYlT8VYaNjjHtwPq6yCR9qrp7lotaEfJpz5B63KbVD4zuc-GXqXXNuukvQWs0UnX-kMnLtldJuJk7qh4BGVKbDFGftf7STxmWPvEvcQ3zMNosE-l5nZiaH_VTtRQkJjIibW7pcKtoeWwWg6r_11NcZ_FbeNVh3bHEiLVIBM_UDU5_jiAXDJ0xHOTGhqZsffyxDqiRoXfWinq6Y3bq0kb6LHwzg5JT-1lElafgoRwxRkvxyZNsLc34V7BswemjDW5_Zb8pk8hvQXG1Z2u67i9ARjCCrPNCB7C7TwvHUNPlzCq1ow", // Identity Token usercode with wmpty value 
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjR4dEotLTVCMzU3aW1zNlRoQTF5SiJ9.ewogICJuaWNrbmFtZSI6ICIgICAgICAgICAgICAiLAogICJuYW1lIjogIkdhbmVzaCBQYXRpbCIsCiAgInBpY3R1cmUiOiAiaHR0cHM6Ly9zLmdyYXZhdGFyLmNvbS9hdmF0YXIvOGU4Njg4MmYwMmNmYTdiZjFiNmQ2ZWEwZDA2NGVmNzc/cz00ODAmcj1wZyZkPWh0dHBzJTNBJTJGJTJGY2RuLmF1dGgwLmNvbSUyRmF2YXRhcnMlMkZnYS5wbmciLAogICJ1cGRhdGVkX2F0IjogIjIwMjEtMTEtMThUMTA6MjM6NTYuMTYxWiIsCiAgImlzcyI6ICJodHRwczovL21jYW10LmV1LmF1dGgwLmNvbS8iLAogICJzdWIiOiAiYXV0aDB8NjE5NDkyOWVlODdhOWEwMDY4NzEwMDk4IiwKICAiYXVkIjogInRuNDdtTk80SjRneVNYREczYmh1YzExbWlZSTVKZ25oIiwKICAiaWF0IjogMTYzNzMwMDc1MCwKICAiZXhwIjogMTY2ODg1ODM1MAp9.GdjsIVQjUbsSsX8ZtacGbm8TgR3o-tdG49GtPFvzYlT8VYaNjjHtwPq6yCR9qrp7lotaEfJpz5B63KbVD4zuc-GXqXXNuukvQWs0UnX-kMnLtldJuJk7qh4BGVKbDFGftf7STxmWPvEvcQ3zMNosE-l5nZiaH_VTtRQkJjIibW7pcKtoeWwWg6r_11NcZ_FbeNVh3bHEiLVIBM_UDU5_jiAXDJ0xHOTGhqZsffyxDqiRoXfWinq6Y3bq0kb6LHwzg5JT-1lElafgoRwxRkvxyZNsLc34V7BswemjDW5_Zb8pk8hvQXG1Z2u67i9ARjCCrPNCB7C7TwvHUNPlzCq1ow", // Identity Token usercode with only spaces 
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjR4dEotLTVCMzU3aW1zNlRoQTF5SiJ9.ewogICJuaWNrbmFtZSI6ICIqKiomJiYqIiwKICAibmFtZSI6ICJHYW5lc2ggUGF0aWwiLAogICJwaWN0dXJlIjogImh0dHBzOi8vcy5ncmF2YXRhci5jb20vYXZhdGFyLzhlODY4ODJmMDJjZmE3YmYxYjZkNmVhMGQwNjRlZjc3P3M9NDgwJnI9cGcmZD1odHRwcyUzQSUyRiUyRmNkbi5hdXRoMC5jb20lMkZhdmF0YXJzJTJGZ2EucG5nIiwKICAidXBkYXRlZF9hdCI6ICIyMDIxLTExLTE4VDEwOjIzOjU2LjE2MVoiLAogICJpc3MiOiAiaHR0cHM6Ly9tY2FtdC5ldS5hdXRoMC5jb20vIiwKICAic3ViIjogImF1dGgwfDYxOTQ5MjllZTg3YTlhMDA2ODcxMDA5OCIsCiAgImF1ZCI6ICJ0bjQ3bU5PNEo0Z3lTWERHM2JodWMxMW1pWUk1SmduaCIsCiAgImlhdCI6IDE2MzczMDA3NTAsCiAgImV4cCI6IDE2Njg4NTgzNTAKfQ==.GdjsIVQjUbsSsX8ZtacGbm8TgR3o-tdG49GtPFvzYlT8VYaNjjHtwPq6yCR9qrp7lotaEfJpz5B63KbVD4zuc-GXqXXNuukvQWs0UnX-kMnLtldJuJk7qh4BGVKbDFGftf7STxmWPvEvcQ3zMNosE-l5nZiaH_VTtRQkJjIibW7pcKtoeWwWg6r_11NcZ_FbeNVh3bHEiLVIBM_UDU5_jiAXDJ0xHOTGhqZsffyxDqiRoXfWinq6Y3bq0kb6LHwzg5JT-1lElafgoRwxRkvxyZNsLc34V7BswemjDW5_Zb8pk8hvQXG1Z2u67i9ARjCCrPNCB7C7TwvHUNPlzCq1ow", // Identity Token usercode with only speacial characters 
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjR4dEotLTVCMzU3aW1zNlRoQTF5SiJ9.ewogICJuYW1lIjogIkdhbmVzaCBQYXRpbCIsCiAgInBpY3R1cmUiOiAiaHR0cHM6Ly9zLmdyYXZhdGFyLmNvbS9hdmF0YXIvOGU4Njg4MmYwMmNmYTdiZjFiNmQ2ZWEwZDA2NGVmNzc/cz00ODAmcj1wZyZkPWh0dHBzJTNBJTJGJTJGY2RuLmF1dGgwLmNvbSUyRmF2YXRhcnMlMkZnYS5wbmciLAogICJ1cGRhdGVkX2F0IjogIjIwMjEtMTEtMThUMTA6MjM6NTYuMTYxWiIsCiAgImlzcyI6ICJodHRwczovL21jYW10LmV1LmF1dGgwLmNvbS8iLAogICJzdWIiOiAiYXV0aDB8NjE5NDkyOWVlODdhOWEwMDY4NzEwMDk4IiwKICAiYXVkIjogInRuNDdtTk80SjRneVNYREczYmh1YzExbWlZSTVKZ25oIiwKICAiaWF0IjogMTYzNzMwMDc1MCwKICAiZXhwIjogMTY2ODg1ODM1MAp9.GdjsIVQjUbsSsX8ZtacGbm8TgR3o-tdG49GtPFvzYlT8VYaNjjHtwPq6yCR9qrp7lotaEfJpz5B63KbVD4zuc-GXqXXNuukvQWs0UnX-kMnLtldJuJk7qh4BGVKbDFGftf7STxmWPvEvcQ3zMNosE-l5nZiaH_VTtRQkJjIibW7pcKtoeWwWg6r_11NcZ_FbeNVh3bHEiLVIBM_UDU5_jiAXDJ0xHOTGhqZsffyxDqiRoXfWinq6Y3bq0kb6LHwzg5JT-1lElafgoRwxRkvxyZNsLc34V7BswemjDW5_Zb8pk8hvQXG1Z2u67i9ARjCCrPNCB7C7TwvHUNPlzCq1ow" // Identity Token wihtout usercode field
            })
    public void addHPCUser_shouldThrowException_IfInvalidUserCodeOrUserName(String token) 
            throws FRInstanceException, InvalidUserCodeException, InvalidWorkgroupException, InvalidRequestTokenException, ParseException {
        
        String workGroupCode = "Test";
        JSONObject identityPayload = JwtUtils.getPayload(token);
        JSONObject inviteTokenPayload = JwtUtils.getInvitePayload(INVITE_TOKEN);
        AuthenticationToken getAuthenticationTokenDetails = JwtUtils.getAuthenticationTokenDetails(INVITE_TOKEN, token);
        
      
        Invitations userInvitation = InvitationsUtil.getActiveInvitation();

        when(userInviteIdentityToken.getInviteTokenPayload(INVITE_TOKEN))
        .thenReturn(inviteTokenPayload);
        
        when(userInviteIdentityToken.getIdentityTokenPayload(token))
        .thenReturn(identityPayload);
        
        when(userService.findHPCWorkGroup(any(), any()))
        .thenReturn(HPCWorkGroupsUtil.getHPCWorkGroups(workGroupCode));
        
        
        when(userInvitationService.getInvitationById(any(), any())).thenReturn(userInvitation);
        
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            userController.addHPCUser(null, getAuthenticationTokenDetails);
        });
    }
    
    @Test
    public void addHPCUser_shouldReturnCodeInvalidUserCodeException_whenUserCodeIsAlreadyExists() 
            throws FRInstanceException, InvalidUserCodeException, ParseException, InvalidRequestTokenException {
       
        List<HPCUsers> existingHpcIUser = new ArrayList<>();
        existingHpcIUser.add(HPCUsersUtil.getUserDetails());
        String workGroupCode = "Test";
        String identityTokenWithSpecialCharacters = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjR4dEotLTVCMzU3aW1zNlRoQTF5SiJ9.ewogICJuaWNrbmFtZSI6ICJjcm9uaW4qY3JvbmluIiwKICAibmFtZSI6ICJDUk9OSU4iLAogICJwaWN0dXJlIjogImh0dHBzOi8vcy5ncmF2YXRhci5jb20vYXZhdGFyL2VhMWMxZDlhNGNhMDMyMmUwYzQwZWYyMGRkYWMyZDRlP3M9NDgwJnI9cGcmZD1odHRwcyUzQSUyRiUyRmNkbi5hdXRoMC5jb20lMkZhdmF0YXJzJTJGY3IucG5nIiwKICAidXBkYXRlZF9hdCI6ICIyMDIxLTEwLTI3VDEyOjU0OjEyLjUxOFoiLAogICJpc3MiOiAiaHR0cHM6Ly9tY2FtdC5ldS5hdXRoMC5jb20vIiwKICAic3ViIjogImF1dGgwfDYwNjFiNjdmMjNlMWM3MDA2YWNkN2E3MyIsCiAgImF1ZCI6ICJ0bjQ3bU5PNEo0Z3lTWERHM2JodWMxMW1pWUk1SmduaCIsCiAgImlhdCI6IDE2MzUzNDAxMDgsCiAgImV4cCI6IDE2MzU3MDAxMDgKfQ==.BlGra0vRqZ9F_4aiAxEhUX2ewIM5qqzmF1TdJnjPrlqhRItbzh7Z33biawd-7wRNw8hNsJbVRoFspNKyQQlRYPJ2MntWEWeOoyzObV5me5H26kjnWYS1XPyMylYLcP3Pns8a2zv83XXOO70XkXtVlczgzMxCXJPGbzuxdpJ4qzGpg56Lti5JsiNnGP86qK8V2Lw3SXOgMI3sOc3RGNRsdlUyAfrIwvE-kop1kWvWwXqkrcJiwOdl_QNhzORdJ9GFzkYY6iX0VS8JOempAnBafnXTDJzLmADAs0hM16ReQYK-3itA_-JrWDYmxeAUW9ShXFkgZCy-g8O9LQo3yeo_wQ";
        JSONObject identityPayload = JwtUtils.getPayload(identityTokenWithSpecialCharacters);
        JSONObject inviteTokenPayload = JwtUtils.getInvitePayload(INVITE_TOKEN);
        AuthenticationToken getAuthenticationTokenDetails = JwtUtils.getAuthenticationTokenDetails(INVITE_TOKEN, identityTokenWithSpecialCharacters);
        Invitations userInvitation = InvitationsUtil.getActiveInvitation();
        
        when(userService.generateUniqueUserCode(any(), any()))
        .thenThrow(new InvalidUserCodeException(USER_CODE_COULD_NOT_GENERATED));
      
        when(userInviteIdentityToken.getInviteTokenPayload(INVITE_TOKEN))
        .thenReturn(inviteTokenPayload);
        
        when(userInviteIdentityToken.getIdentityTokenPayload(identityTokenWithSpecialCharacters))
        .thenReturn(identityPayload);
        
        when(userService.findHPCWorkGroup(any(), any()))
        .thenReturn(HPCWorkGroupsUtil.getHPCWorkGroups(workGroupCode));
        
        when(userInvitationService.getInvitationById(any(), any())).thenReturn(userInvitation);
        
        Exception exception = assertThrows(InvalidUserCodeException.class, () -> {
            userController.addHPCUser(null, getAuthenticationTokenDetails);
        });
        

        
        assertEquals(exception.getMessage(), USER_CODE_COULD_NOT_GENERATED);
    }
    
    
    @Test
    public void addHPCUser_shouldReturnUserName_fromNameField() 
            throws FRInstanceException, InvalidUserCodeException, ParseException, InvalidRequestTokenException, InvalidWorkgroupException, RevokedMobileInvitationException, ExpiredMobileInvitationException {
        
        List<HPCUsers> existingHpcIUser = new ArrayList<>();
        existingHpcIUser.add(HPCUsersUtil.getUserDetails());
        List<HPCUsers> emptyHpcIUser = new ArrayList<>();
        String workGroupCode = "Test";
        JSONObject identityPayload = JwtUtils.getPayload(IDENTITY_TOKEN);
        JSONObject inviteTokenPayload = JwtUtils.getInvitePayload(INVITE_TOKEN);
        AuthenticationToken getAuthenticationTokenDetails = JwtUtils.getAuthenticationTokenDetails(INVITE_TOKEN, IDENTITY_TOKEN);
        Invitations userInvitation = InvitationsUtil.getActiveInvitation();
        
        when(userService.generateUniqueUserCode(any(), any()))
        .thenReturn("Test");
        
        when(userInviteIdentityToken.getInviteTokenPayload(INVITE_TOKEN))
        .thenReturn(inviteTokenPayload);
        
        when(userInviteIdentityToken.getIdentityTokenPayload(IDENTITY_TOKEN))
        .thenReturn(identityPayload);
    
        when(userService.findHPCWorkGroup(any(), any()))
        .thenReturn(HPCWorkGroupsUtil.getHPCWorkGroups(workGroupCode));
        
        when(userInvitationService.getInvitationById(any(), any())).thenReturn(userInvitation);

        HPCUsers hpcUser =userController.addHPCUser(null, getAuthenticationTokenDetails);
        
      
        
        assertEquals("CRONIN", hpcUser.getUserName());
    }
    
    
    
    @Test
    public void addHPCUser_shouldReturnUserName_withOnlyAllowedLength() 
            throws FRInstanceException, InvalidUserCodeException, ParseException, InvalidRequestTokenException, InvalidWorkgroupException, RevokedMobileInvitationException, ExpiredMobileInvitationException {
        
        
        String identityTokenWithUserNameLengthGreaterThan40 = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjR4dEotLTVCMzU3aW1zNlRoQTF5SiJ9.ewogICJuaWNrbmFtZSI6ICJjcm9uaW4iLAogICJuYW1lIjogIkNST05JTkNST05JTkNST05JTkNST05JTkNST05JTkNST05JTkNST05JTiIsCiAgInBpY3R1cmUiOiAiaHR0cHM6Ly9zLmdyYXZhdGFyLmNvbS9hdmF0YXIvZWExYzFkOWE0Y2EwMzIyZTBjNDBlZjIwZGRhYzJkNGU/cz00ODAmcj1wZyZkPWh0dHBzJTNBJTJGJTJGY2RuLmF1dGgwLmNvbSUyRmF2YXRhcnMlMkZjci5wbmciLAogICJ1cGRhdGVkX2F0IjogIjIwMjEtMTAtMjdUMTI6NTQ6MTIuNTE4WiIsCiAgImlzcyI6ICJodHRwczovL21jYW10LmV1LmF1dGgwLmNvbS8iLAogICJzdWIiOiAiYXV0aDB8NjA2MWI2N2YyM2UxYzcwMDZhY2Q3YTczIiwKICAiYXVkIjogInRuNDdtTk80SjRneVNYREczYmh1YzExbWlZSTVKZ25oIiwKICAiaWF0IjogMTYzNTM0MDEwOCwKICAiZXhwIjogMTYzNTcwMDEwOAp9.BlGra0vRqZ9F_4aiAxEhUX2ewIM5qqzmF1TdJnjPrlqhRItbzh7Z33biawd-7wRNw8hNsJbVRoFspNKyQQlRYPJ2MntWEWeOoyzObV5me5H26kjnWYS1XPyMylYLcP3Pns8a2zv83XXOO70XkXtVlczgzMxCXJPGbzuxdpJ4qzGpg56Lti5JsiNnGP86qK8V2Lw3SXOgMI3sOc3RGNRsdlUyAfrIwvE-kop1kWvWwXqkrcJiwOdl_QNhzORdJ9GFzkYY6iX0VS8JOempAnBafnXTDJzLmADAs0hM16ReQYK-3itA_-JrWDYmxeAUW9ShXFkgZCy-g8O9LQo3yeo_wQ";
        List<HPCUsers> existingHpcIUser = new ArrayList<>();
        existingHpcIUser.add(HPCUsersUtil.getUserDetails());
        List<HPCUsers> emptyHpcIUser = new ArrayList<>();
        String workGroupCode = "Test";
        JSONObject identityPayload = JwtUtils.getPayload(identityTokenWithUserNameLengthGreaterThan40);
        JSONObject inviteTokenPayload = JwtUtils.getInvitePayload(INVITE_TOKEN);
        AuthenticationToken getAuthenticationTokenDetails = JwtUtils.getAuthenticationTokenDetails(INVITE_TOKEN, identityTokenWithUserNameLengthGreaterThan40);
        Invitations userInvitation = InvitationsUtil.getActiveInvitation();
        
        when(userService.generateUniqueUserCode(any(), any()))
        .thenReturn("Test");
        
      
        when(userInviteIdentityToken.getInviteTokenPayload(INVITE_TOKEN))
        .thenReturn(inviteTokenPayload);
        
        when(userInviteIdentityToken.getIdentityTokenPayload(identityTokenWithUserNameLengthGreaterThan40))
        .thenReturn(identityPayload);
    
        when(userService.findHPCWorkGroup(any(), any()))
        .thenReturn(HPCWorkGroupsUtil.getHPCWorkGroups(workGroupCode));
        
        when(userInvitationService.getInvitationById(any(), any())).thenReturn(userInvitation);
        
        HPCUsers hpcUser =userController.addHPCUser(null, getAuthenticationTokenDetails);
        

        
        assertTrue((hpcUser.getUserName().length() ==  USERNAME_MAX_LENGTH) );
    }
    
    
    @Test
    public void addHPCUser_shouldReturnWorkGroup_withValidInviteToken() 
            throws  InvalidRequestTokenException, FRInstanceException, InvalidWorkgroupException, ParseException, InvalidUserCodeException, RevokedMobileInvitationException, ExpiredMobileInvitationException {
        
        List<HPCUsers> existingHpcIUser = new ArrayList<>();
        existingHpcIUser.add(HPCUsersUtil.getUserDetails());
        List<HPCUsers> emptyHpcIUser = new ArrayList<>();
        String workGroupCode = "CRONIN";
        JSONObject identityPayload = JwtUtils.getPayload(IDENTITY_TOKEN);
        JSONObject inviteTokenPayload = JwtUtils.getInvitePayload(INVITE_TOKEN);
        AuthenticationToken getAuthenticationTokenDetails = JwtUtils.getAuthenticationTokenDetails(INVITE_TOKEN, IDENTITY_TOKEN);
        Invitations userInvitation = InvitationsUtil.getActiveInvitation();
        
        when(userService.generateUniqueUserCode(any(), any()))
        .thenReturn("Test");
      
        when(userInviteIdentityToken.getInviteTokenPayload(INVITE_TOKEN))
        .thenReturn(inviteTokenPayload);
        
        when(userInviteIdentityToken.getIdentityTokenPayload(IDENTITY_TOKEN))
        .thenReturn(identityPayload);
    
        when(userService.findHPCWorkGroup(any(), any()))
        .thenReturn(HPCWorkGroupsUtil.getHPCWorkGroups(workGroupCode));
        
        when(userInvitationService.getInvitationById(any(), any())).thenReturn(userInvitation);

        HPCUsers hpcUser =userController.addHPCUser(null, getAuthenticationTokenDetails);
        
      
        
        assertEquals("CRONIN", hpcUser.getWorkgroupCode());
    }
    
    
    @Test
    public void addHPCUser_shouldThrowInvalidRequestTokenException_withInvalidInviteToken() 
            throws  InvalidRequestTokenException, FRInstanceException, InvalidWorkgroupException, ParseException, InvalidUserCodeException {
        
        
        List<HPCUsers> existingHpcIUser = new ArrayList<>();
        existingHpcIUser.add(HPCUsersUtil.getUserDetails());
        AuthenticationToken getAuthenticationTokenDetails = JwtUtils.getAuthenticationTokenDetails(INVITE_TOKEN, IDENTITY_TOKEN);

        
        when(userInviteIdentityToken.getInviteTokenPayload(INVITE_TOKEN))
        .thenThrow(ParseException.class);
        
        Exception exception = assertThrows(InvalidRequestTokenException.class, () -> {
            userController.addHPCUser(null, getAuthenticationTokenDetails);
        });
        
   
        
        assertEquals(INVITE_TOKEN_PARSE_ERROR, exception.getMessage());
    }
    
    
    @Test
    public void addHPCUser_shouldThrowInvalidWorkgroupException_withNonExistsWorkGroup() 
            throws  InvalidRequestTokenException, FRInstanceException, InvalidWorkgroupException, ParseException {
        
        List<HPCUsers> existingHpcIUser = new ArrayList<>();
        existingHpcIUser.add(HPCUsersUtil.getUserDetails());
        HPCWorkGroups workGroupDetails = null ;
        JSONObject inviteTokenPayload = JwtUtils.getInvitePayload(INVITE_TOKEN);
        AuthenticationToken getAuthenticationTokenDetails = JwtUtils.getAuthenticationTokenDetails(INVITE_TOKEN, IDENTITY_TOKEN);
        Invitations userInvitation = InvitationsUtil.getActiveInvitation();
      
        when(userInviteIdentityToken.getInviteTokenPayload(INVITE_TOKEN))
        .thenReturn(inviteTokenPayload);
        
    
        when(userService.findHPCWorkGroup(any(), any()))
        .thenReturn(workGroupDetails);
        
        when(userInvitationService.getInvitationById(any(), any())).thenReturn(userInvitation);
        
        Exception exception = assertThrows(InvalidWorkgroupException.class, () -> {
            userController.addHPCUser(null, getAuthenticationTokenDetails);
        });
        
        
        
        assertEquals(WORKGROUP_DOES_NOT_EXIST, exception.getMessage());
    }
    
    
    @Test
    public void addHPCUser_shouldThrowInvalidWorkgroupException_withInactiveWorkGroup() 
            throws  InvalidRequestTokenException, FRInstanceException, InvalidWorkgroupException, ParseException {
        
        
        List<HPCUsers> existingHpcIUser = new ArrayList<>();
        existingHpcIUser.add(HPCUsersUtil.getUserDetails());
        String workGroupCode = "CRONIN";
        HPCWorkGroups workGroupDetails = HPCWorkGroupsUtil.getHPCWorkGroups(workGroupCode) ;
        workGroupDetails.setActive(0);
        JSONObject inviteTokenPayload = JwtUtils.getInvitePayload(INVITE_TOKEN);
        AuthenticationToken getAuthenticationTokenDetails = JwtUtils.getAuthenticationTokenDetails(INVITE_TOKEN, IDENTITY_TOKEN);
        Invitations userInvitation = InvitationsUtil.getActiveInvitation();
      
        when(userInviteIdentityToken.getInviteTokenPayload(INVITE_TOKEN))
        .thenReturn(inviteTokenPayload);
        
    
        when(userService.findHPCWorkGroup(any(), any()))
        .thenReturn(workGroupDetails);
        
        when(userInvitationService.getInvitationById(any(), any())).thenReturn(userInvitation);
        
        Exception exception = assertThrows(InvalidWorkgroupException.class, () -> {
            userController.addHPCUser(null, getAuthenticationTokenDetails);
        });
        assertEquals(INACTIVE_WORKGROUP, exception.getMessage());
    }
    
    
    @Test
    public void addHPCUser_shouldThrowInvalidRequestTokenException_withEmptyClaims() 
            throws  InvalidRequestTokenException, FRInstanceException, InvalidWorkgroupException, ParseException {
        
        List<HPCUsers> existingHpcIUser = new ArrayList<>();
        existingHpcIUser.add(HPCUsersUtil.getUserDetails());
        String inviteTokenWithoutClaims = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.ewogICJjZmciOiB7CiAgICAiYXV0aCI6IFsKICAgICAgewogICAgICAgICJhdXRob3JpdHlfdXJpIjogImh0dHBzOi8vbWNhbXQuZXUuYXV0aDAuY29tLyIsCiAgICAgICAgImlkIjogImRlZmF1bHQiLAogICAgICAgICJ0eXBlIjogIm9pZGMiLAogICAgICAgICJjbGllbnRfaWQiOiAidG40N21OTzRKNGd5U1hERzNiaHVjMTFtaVlJNUpnbmgiCiAgICAgIH0KICAgIF0sCiAgICAicmVzb3VyY2VzIjogWwogICAgICB7CiAgICAgICAgImF1dGgiOiBbCiAgICAgICAgICAiZGVmYXVsdCIKICAgICAgICBdLAogICAgICAgICJ0eXBlIjogInRlbmFudCIsCiAgICAgICAgInVyaSI6ICJodHRwOi8vbG9jYWxob3N0OjgwNzAvbW9iaWxlLyIKICAgICAgfQogICAgXQogIH0KfQ==.a_hu_iws-uQ0wYEu7Uy8OlrOPNjm3-6yEVEYB2GccWkJoamdSvexl3Nxt5ssfN3IQZzpCw_fkFNJnoxATs_SetmLRTYbYoFojZAUXrGomKcmBRjV_42PxXOwK39uq3wIVB-Ad_hVOzPjbXB08-Rmg_XIQPc-SzhK0fNqZhsA1Jh_cMGQ-x91go5mklsSMouS8uO7PUBvtbq8Mdcdr6rm1WGFdc2EYISGxdWw4N3dKrDoJpTm4e4s1frKA5pk5zi8aflerHldJAybEmFKXzjsur_hywix2N6kUZycg0WUn2U4N-uh2smTHC9YqCS0TYiuJLCNUcVAdMna_vOfoVpcAQ";
        JSONObject inviteTokenPayload = JwtUtils.getInvitePayload(inviteTokenWithoutClaims);
        AuthenticationToken getAuthenticationTokenDetails = JwtUtils.getAuthenticationTokenDetails(inviteTokenWithoutClaims, IDENTITY_TOKEN);

      
        when(userInviteIdentityToken.getInviteTokenPayload(inviteTokenWithoutClaims))
        .thenReturn(inviteTokenPayload);

        
        Exception exception = assertThrows(InvalidRequestTokenException.class, () -> {
            userController.addHPCUser(null, getAuthenticationTokenDetails);
        });
        assertEquals(INVITE_TOKEN_MISSING_CLAIMS, exception.getMessage());
    }  
    
    @Test
    public void getActiveUserDetails_shouldReturnUserDetails_WithValidUser() throws FRInstanceException, UserRevokedException {
        HPCUsers user = HPCUsersUtil.getUserDetails();
        
        Map<String, Object> attrts = JwtUtils.setMockSecurityContext();
        JwtAuthenticationToken authentication   = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        when(authentication.getTokenAttributes()).thenReturn(attrts);
        
        when( userService.findHPCUserByOAuthPrincipal(null, attrts.get(UserController.ISSUER).toString(), attrts.get(UserController.SUBJECT).toString())).thenReturn(user);
        Optional<HPCUsers> userDetails =  userController.getActiveUserDetails();
        
        assertEquals(user.getId().getUserCode(),userDetails.get().getId().getUserCode());
    }  
    
    @Test
    public void getActiveUserDetails_shouldThrowUserRevokedException_WithInValidUser() throws FRInstanceException {
        HPCUsers user = HPCUsersUtil.getUserDetails();
        user.setRevokedBool(true);
        
        Map<String, Object> attrts = JwtUtils.setMockSecurityContext();
        JwtAuthenticationToken authentication   = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        when(authentication.getTokenAttributes()).thenReturn(attrts);
        when( userService.findHPCUserByOAuthPrincipal(null, attrts.get(UserController.ISSUER).toString(), attrts.get(UserController.SUBJECT).toString())).thenReturn(user);
        
        Exception exception = assertThrows(UserRevokedException.class, () -> {
            Optional<HPCUsers> userDetails =  userController.getActiveUserDetails();
        });
    }  
    
    @Test
    public void addHPCUser_shouldThrowRevokedMobileInvitationException_withRevokedInvitation() 
            throws  InvalidRequestTokenException, FRInstanceException, InvalidWorkgroupException, ParseException {
        
        
        List<HPCUsers> existingHpcIUser = new ArrayList<>();
        existingHpcIUser.add(HPCUsersUtil.getUserDetails());
        String workGroupCode = "CRONIN";
        HPCWorkGroups workGroupDetails = HPCWorkGroupsUtil.getHPCWorkGroups(workGroupCode) ;
        workGroupDetails.setActive(0);
        JSONObject inviteTokenPayload = JwtUtils.getInvitePayload(INVITE_TOKEN);
        AuthenticationToken getAuthenticationTokenDetails = JwtUtils.getAuthenticationTokenDetails(INVITE_TOKEN, IDENTITY_TOKEN);
        Invitations revokedUserInvitation = InvitationsUtil.getRevokedInvitation();
      
        when(userInviteIdentityToken.getInviteTokenPayload(INVITE_TOKEN))
        .thenReturn(inviteTokenPayload);
        
        when(userInvitationService.getInvitationById(any(), any())).thenReturn(revokedUserInvitation);
        
        Exception exception = assertThrows(RevokedMobileInvitationException.class, () -> {
            userController.addHPCUser(null, getAuthenticationTokenDetails);
        });
        assertEquals(USER_INVITATION_REVOKED, exception.getMessage());
    }
    
    @Test
    public void addHPCUser_shouldThrowRevokedMobileInvitationException_withExpiredInvitation() 
            throws  InvalidRequestTokenException, FRInstanceException, InvalidWorkgroupException, ParseException {
        
        
        List<HPCUsers> existingHpcIUser = new ArrayList<>();
        existingHpcIUser.add(HPCUsersUtil.getUserDetails());
        String workGroupCode = "CRONIN";
        HPCWorkGroups workGroupDetails = HPCWorkGroupsUtil.getHPCWorkGroups(workGroupCode) ;
        workGroupDetails.setActive(0);
        String inviteTokenWithExpiredDate = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.ewogICJjZmciOiB7CiAgICAiYXV0aCI6IFsKICAgICAgewogICAgICAgICJhdXRob3JpdHlfdXJpIjogImh0dHBzOi8vbWNhbXQuZXUuYXV0aDAuY29tLyIsCiAgICAgICAgImlkIjogImRlZmF1bHQiLAogICAgICAgICJ0eXBlIjogIm9pZGMiLAogICAgICAgICJjbGllbnRfaWQiOiAidG40N21OTzRKNGd5U1hERzNiaHVjMTFtaVlJNUpnbmgiCiAgICAgIH0KICAgIF0sCiAgICAiY2xhaW1zIjogWwogICAgICB7CiAgICAgICAgIndvcmtncm91cCI6ICJDUk9OSU4iLAogICAgICAgICJpbnZpdGVpZCI6ICJmNWJjZWVkNi1lYTc5LTRkNTctOTQ3YS1hNjEyNjI2MjU1OTAiLAogICAgICAgICJleHBpcnkiOiAyMDIyMDEwNywKICAgICAgICAidHlwZSI6ICJ1c2VyL2luZm8iCiAgICAgIH0KICAgIF0sCiAgICAicmVzb3VyY2VzIjogWwogICAgICB7CiAgICAgICAgImF1dGgiOiBbCiAgICAgICAgICAiZGVmYXVsdCIKICAgICAgICBdLAogICAgICAgICJ0eXBlIjogInRlbmFudCIsCiAgICAgICAgInVyaSI6ICJodHRwOi8vbG9jYWxob3N0OjgwNzAvbW9iaWxlLyIKICAgICAgfQogICAgXQogIH0KfQ==.a_hu_iws-uQ0wYEu7Uy8OlrOPNjm3-6yEVEYB2GccWkJoamdSvexl3Nxt5ssfN3IQZzpCw_fkFNJnoxATs_SetmLRTYbYoFojZAUXrGomKcmBRjV_42PxXOwK39uq3wIVB-Ad_hVOzPjbXB08-Rmg_XIQPc-SzhK0fNqZhsA1Jh_cMGQ-x91go5mklsSMouS8uO7PUBvtbq8Mdcdr6rm1WGFdc2EYISGxdWw4N3dKrDoJpTm4e4s1frKA5pk5zi8aflerHldJAybEmFKXzjsur_hywix2N6kUZycg0WUn2U4N-uh2smTHC9YqCS0TYiuJLCNUcVAdMna_vOfoVpcAQ";
        JSONObject inviteTokenPayload = JwtUtils.getInvitePayload(inviteTokenWithExpiredDate);
        AuthenticationToken getAuthenticationTokenDetails = JwtUtils.getAuthenticationTokenDetails(inviteTokenWithExpiredDate, IDENTITY_TOKEN);
        Invitations userInvitation = InvitationsUtil.getActiveInvitation();
      
        when(userInviteIdentityToken.getInviteTokenPayload(inviteTokenWithExpiredDate))
        .thenReturn(inviteTokenPayload);
        
        when(userInvitationService.getInvitationById(any(), any())).thenReturn(userInvitation);
        
        Exception exception = assertThrows(ExpiredMobileInvitationException.class, () -> {
            userController.addHPCUser(null, getAuthenticationTokenDetails);
        });
        assertEquals(USER_INVITATION_EXPIRED, exception.getMessage());
    }
}
