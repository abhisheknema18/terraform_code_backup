package com.amtsybex.fieldreach.services.endpoint.rest.impl;

import com.amtsybex.fieldreach.backend.model.ConfigurationFiles;
import com.amtsybex.fieldreach.backend.service.ConfigurationService;
import com.amtsybex.fieldreach.backend.service.UserService;
import com.amtsybex.fieldreach.services.endpoint.rest.impl.utils.HPCUsersUtil;
import com.amtsybex.fieldreach.services.endpoint.rest.impl.utils.JwtUtils;
import com.amtsybex.fieldreach.services.messages.response.ConfigListResponse;
import com.amtsybex.fieldreach.services.messages.response.GetConfigResponse;
import com.amtsybex.fieldreach.services.resource.FileResourceController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Blob;
import java.util.ArrayList;

import javax.sql.rowset.serial.SerialBlob;
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class RestConfigControllerImplTest {

    @Mock
    private UserService userService;
    
    @Mock
    private ConfigurationService configurationService;
    
    @Spy
    @Autowired
    @InjectMocks
    private RestConfigControllerImpl restConfigController;

    private final XmlMapper xmlMapper = new XmlMapper();

    private final ObjectMapper jsonMapper = new ObjectMapper();

    private MockMvc mockMvc;
    
    @Autowired
    private MappingJackson2HttpMessageConverter jackson2HttpMessageConverter;
    
    @Autowired
    private MappingJackson2XmlHttpMessageConverter jackson2XmlHttpMessageConverter;
    
    
    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(restConfigController).setMessageConverters(jackson2HttpMessageConverter, jackson2XmlHttpMessageConverter).build();
    }

    @Test
    void getConfigFile_shouldReturnFileContents_whenAcceptHeaderIsXml() throws Exception {
        // Arrange
        String fileName = "SCRIPTCONFIG.xml";
        String stubFileContent = "Test Content";
        ConfigurationFiles configFiles = prepareConfigurationFiles(fileName,stubFileContent);
        String encodedContents = "VGVzdCBDb250ZW50";
        when(configurationService.getConfigurationFilesByName(null,fileName)).thenReturn(configFiles);

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/config/" + fileName)
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();
        //<GetConfigResponse><error><errorCode/><errorDescription/></error><configFileSource>Test Content</configFileSource><success>true</success></GetConfigResponse>
        // Assert
        String expectedxml = "<GetConfigResponse><error/><configFileSource>VGVzdCBDb250ZW50</configFileSource><success>true</success></GetConfigResponse>";
        assertEquals(expectedxml, mvcResult.getResponse().getContentAsString());
        
        GetConfigResponse response = xmlMapper.readValue(mvcResult.getResponse().getContentAsString(), GetConfigResponse.class);
        assertEquals(encodedContents, response.getConfigFileSource());
        assertTrue(response.isSuccess());
        Mockito.verify(configurationService, times(1)).getConfigurationFilesByName(null, fileName);
    }

    @Test
    void getConfigFile_shouldReturnFileContents_whenAcceptHeaderIsJson() throws Exception {
        // Arrange
	   String fileName = "SCRIPTCONFIG.xml";
       String stubFileContent = "Test Content";
       ConfigurationFiles configFiles = prepareConfigurationFiles(fileName,stubFileContent);
       String encodedContents = "VGVzdCBDb250ZW50";
       when(configurationService.getConfigurationFilesByName(null,fileName)).thenReturn(configFiles);

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/config/" + fileName)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        GetConfigResponse response = jsonMapper.readValue(mvcResult.getResponse().getContentAsString(), GetConfigResponse.class);
        assertEquals(encodedContents, response.getConfigFileSource());
        assertTrue(response.isSuccess());
        Mockito.verify(configurationService, times(1)).getConfigurationFilesByName(null, fileName);
    }

    @Test
    void getConfigList_shouldReturnResponseWithEmptyConfigList_whenNoConfigFilesPresentAndAcceptHeaderIsXml() throws Exception {
        // Arrange
        when(userService.findHPCWorkGroup(any(), any())).thenReturn(null);
        when(configurationService.getConfigurationFiles(any())).thenReturn(new ArrayList<>());
        doReturn(HPCUsersUtil.getUserDetails()).when(restConfigController).getUserDetailsFromUserPrincipal(any());
        

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/config/list")
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();
        
        // Assert
        //String expectedxml = "<ConfigListResponse><success>true</success><error/><configList><ConfigFile><fileName>EQCONFIG.xml</fileName></ConfigFile><ConfigFile><fileName>EQCONFIG_SCOTLAND.xml</fileName></ConfigFile></configList></ConfigListResponse>";
        //assertEquals(expectedxml, mvcResult.getResponse().getContentAsString());
        
        ConfigListResponse response = xmlMapper.readValue(mvcResult.getResponse().getContentAsString(), ConfigListResponse.class);
        assertTrue(response.isSuccess());
        assertTrue(response.getConfigList().isEmpty());
        assertNull(response.getError());
    }

    @Test
    void getConfigList_shouldReturnResponseWithEmptyConfigList_whenNoConfigFilesPresentAndAcceptHeaderIsJson() throws Exception {
        
        when(userService.findHPCWorkGroup(any(), any())).thenReturn(null);
        doReturn(HPCUsersUtil.getUserDetails()).when(restConfigController).getUserDetailsFromUserPrincipal(any());
        when(configurationService.getConfigurationFiles(any())).thenReturn(new ArrayList<>());
        

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/config/list")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        ConfigListResponse response = jsonMapper.readValue(mvcResult.getResponse().getContentAsString(), ConfigListResponse.class);
        assertTrue(response.isSuccess());
        assertTrue(response.getConfigList().isEmpty());
        assertNull(response.getError().getErrorCode());
    }
    
    @Test
    void getConfigList_shouldReturnUnAuthorizedResponse_whenNoUserExists() throws Exception {
        
        JwtUtils.setMockSecurityContext();
       
        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/config/list")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }
    
    private ConfigurationFiles prepareConfigurationFiles(String fileName, String content ) {
    	ConfigurationFiles details = new ConfigurationFiles();
    	details.setConfigFileName(fileName);
    	byte byte_string[]=content.getBytes();
    	Blob blob = null;
		try {
			blob = new SerialBlob(byte_string);
		} catch (Exception e) {
			details.setConfigFile(null);
		}
    	details.setConfigFile(blob);
    	return details;
    }
}