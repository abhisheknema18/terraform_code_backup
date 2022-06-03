package com.amtsybex.fieldreach.services.endpoint.rest.impl;


import com.amtsybex.fieldreach.backend.model.SystemEventLog;
import com.amtsybex.fieldreach.backend.service.SystemEventService;
import com.amtsybex.fieldreach.services.messages.response.CallResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.amtsybex.fieldreach.services.messages.request.SystemEvent.APPLICATION_VND_FIELDSMART_SYSTEMEVENT_1_JSON;
import static com.amtsybex.fieldreach.services.messages.request.SystemEvent.APPLICATION_VND_FIELDSMART_SYSTEMEVENT_1_XML;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class RestSystemEventLogImplTest {

    @Mock
    private SystemEventService systemEventService;

    @Autowired
    @InjectMocks
    private RestSystemEventLogImpl restSystemEventLog;

    private final XmlMapper xmlMapper = new XmlMapper();
    
    private final ObjectMapper jsonMapper = new ObjectMapper();
    
    private MockMvc mockMvc;

    @Autowired
    private MappingJackson2HttpMessageConverter jackson2HttpMessageConverter;
    
    @Autowired
    private MappingJackson2XmlHttpMessageConverter jackson2XmlHttpMessageConverter;
    
    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(restSystemEventLog).setMessageConverters(jackson2HttpMessageConverter, jackson2XmlHttpMessageConverter).build();
    }
    
    @Test
    public void addSystemEventLog_shouldValidateAndSaveEventLogXml_whenRequestAcceptHeaderInXml() throws Exception {
        // ARRANGE
        String body = getEventLogXML();
        doNothing().when(systemEventService).saveSystemEventLog(isNull(), isA(SystemEventLog.class));

        // ACT
        MvcResult mvcResult = this.mockMvc.perform(post("/systemevent")
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML)
                .content(body))
                .andExpect(status().isOk())
                .andReturn();
        
        // ASSERT
        String expectedxml = "<CallResponse><error/><success>true</success></CallResponse>";
        assertEquals(expectedxml, mvcResult.getResponse().getContentAsString());
        
        CallResponse callResponse = this.xmlMapper.readValue(mvcResult.getResponse().getContentAsString(), CallResponse.class);
        Mockito.verify(systemEventService, Mockito.times(1)).saveSystemEventLog(isNull(), isA(SystemEventLog.class));
        assertTrue(callResponse.isSuccess());
        assertNull(callResponse.getError());
    }
    
    @Test
    public void addSystemEventLog_shouldValidateAndSaveEventLogXml_whenRequestAcceptHeaderInFieldSmartXml() throws Exception {
        // ARRANGE
        String body = getEventLogXML();
        doNothing().when(systemEventService).saveSystemEventLog(isNull(), isA(SystemEventLog.class));

        // ACT
        MvcResult mvcResult = this.mockMvc.perform(post("/systemevent")
                .contentType(APPLICATION_VND_FIELDSMART_SYSTEMEVENT_1_XML)
                .accept(MediaType.APPLICATION_XML)
                .content(body))
                .andExpect(status().isOk())
                .andReturn();
        
        // ASSERT
        String expectedxml = "<CallResponse><error/><success>true</success></CallResponse>";
        assertEquals(expectedxml, mvcResult.getResponse().getContentAsString());
        
        CallResponse callResponse = this.xmlMapper.readValue(mvcResult.getResponse().getContentAsString(), CallResponse.class);
        Mockito.verify(systemEventService, Mockito.times(1)).saveSystemEventLog(isNull(), isA(SystemEventLog.class));
        assertTrue(callResponse.isSuccess());
        assertNull(callResponse.getError());
    }

    @Test
    public void addSystemEventLog_shouldValidateAndSaveEventLog_whenRequestAcceptHeaderInJson() throws Exception {
        // ARRANGE
        String body = getEventLogJSON();
        doNothing().when(systemEventService).saveSystemEventLog(isNull(), isA(SystemEventLog.class));

        // ACT
        MvcResult mvcResult = this.mockMvc.perform(post("/systemevent")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andReturn();

        // ASSERT
        CallResponse callResponse = this.jsonMapper.readValue(mvcResult.getResponse().getContentAsString(), CallResponse.class);
        Mockito.verify(systemEventService, Mockito.times(1)).saveSystemEventLog(isNull(), isA(SystemEventLog.class));
        assertTrue(callResponse.isSuccess());
        assertNotNull(callResponse.getError());
        assertNull(callResponse.getError().getErrorCode());
    }
    
    @Test
    public void addSystemEventLog_shouldValidateAndSaveEventLog_whenRequestAcceptHeaderInFieldSmartJson() throws Exception {
        // ARRANGE
        String body = getEventLogJSON();
        doNothing().when(systemEventService).saveSystemEventLog(isNull(), isA(SystemEventLog.class));

        // ACT
        MvcResult mvcResult = this.mockMvc.perform(post("/systemevent")
                .contentType(APPLICATION_VND_FIELDSMART_SYSTEMEVENT_1_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andReturn();

        // ASSERT
        CallResponse callResponse = this.jsonMapper.readValue(mvcResult.getResponse().getContentAsString(), CallResponse.class);
        Mockito.verify(systemEventService, Mockito.times(1)).saveSystemEventLog(isNull(), isA(SystemEventLog.class));
        assertTrue(callResponse.isSuccess());
        assertNotNull(callResponse.getError());
        assertNull(callResponse.getError().getErrorCode());
    }
    
    @Test
    public void addSystemEventLog_shouldReturnBadRequest_whenSystemEventApplicationIsEmpty() throws Exception {
        // ARRANGE
        String body = getEventLogXML().replaceFirst("FWOM", "");
        // ACT
        MvcResult mvcResult = this.mockMvc.perform(post("/systemevent")
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML)
                .content(body))
                .andExpect(status().isBadRequest())
                .andReturn();

        // ASSERT
        assertNotNull(mvcResult.getResponse());
	    assertNotNull(mvcResult.getResolvedException().getMessage());

    }
    
    @Test
    public void addSystemEventLog_shouldReturnBadRequest_whenSystemEventCategoryIsEmpty() throws Exception {
        // ARRANGE
        String body = getEventLogXML().replaceFirst("EXCEPTION", "");
        // ACT
        MvcResult mvcResult = this.mockMvc.perform(post("/systemevent")
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML)
                .content(body))
                .andExpect(status().isBadRequest())
                .andReturn();

        // ASSERT
        assertNotNull(mvcResult.getResponse());
	    assertNotNull(mvcResult.getResolvedException().getMessage());

    }
    
    @Test
    public void addSystemEventLog_shouldReturnBadRequest_whenSystemEventSummaryIsEmpty() throws Exception {
        // ARRANGE
        String body = getEventLogXML().replaceFirst("TEST EXCEPTION", "");
        // ACT
        MvcResult mvcResult = this.mockMvc.perform(post("/systemevent")
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML)
                .content(body))
                .andExpect(status().isBadRequest())
                .andReturn();

        // ASSERT
        assertNotNull(mvcResult.getResponse());
	    assertNotNull(mvcResult.getResolvedException().getMessage());

    }
    
    @Test
    public void addSystemEventLog_shouldReturnBadRequest_whenSystemEventTimeIsEmpty() throws Exception {
        // ARRANGE
        String body = getEventLogXML().replaceFirst("010101", "");
        // ACT
        MvcResult mvcResult = this.mockMvc.perform(post("/systemevent")
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML)
                .content(body))
                .andExpect(status().isBadRequest())
                .andReturn();

        // ASSERT
        assertNotNull(mvcResult.getResponse());
	    assertNotNull(mvcResult.getResolvedException().getMessage());

    }
    
    @Test
    public void addSystemEventLog_shouldReturnBadRequest_whenSystemEventTypeIsEmpty() throws Exception {
        // ARRANGE
        String body = getEventLogXML().replaceFirst("ERROR", "");
        // ACT
        MvcResult mvcResult = this.mockMvc.perform(post("/systemevent")
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML)
                .content(body))
                .andExpect(status().isBadRequest())
                .andReturn();

        // ASSERT
        assertNotNull(mvcResult.getResponse());
	    assertNotNull(mvcResult.getResolvedException().getMessage());

    }
    
    @Test
    public void addSystemEventLog_shouldReturnBadRequest_whenSystemEventSourceSystemIsEmpty() throws Exception {
        // ARRANGE
        String body = getEventLogXML().replaceFirst("FIELDREACH", "");
        // ACT
        MvcResult mvcResult = this.mockMvc.perform(post("/systemevent")
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML)
                .content(body))
                .andExpect(status().isBadRequest())
                .andReturn();

        // ASSERT
        assertNotNull(mvcResult.getResponse());
	    assertNotNull(mvcResult.getResolvedException().getMessage());

    }
    
    @Test
    public void addSystemEventLog_shouldReturnBadRequest_whenSystemEventDateIsEmpty() throws Exception {
        // ARRANGE
        String body = getEventLogXML().replaceFirst("20180101", "");
        // ACT
        MvcResult mvcResult = this.mockMvc.perform(post("/systemevent")
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML)
                .content(body))
                .andExpect(status().isBadRequest())
                .andReturn();

        // ASSERT
        assertNotNull(mvcResult.getResponse());
	    assertNotNull(mvcResult.getResolvedException().getMessage());

    }


    private String getEventLogXML() {
        return "<SystemEvent>" +
                "  <eventDate>20180101</eventDate>" +
                "  <eventTime>010101</eventTime >" +
                "  <eventCategory>EXCEPTION</eventCategory >" +
                "  <eventType>ERROR</eventType >" +
                "  <eventSummary>TEST EXCEPTION</eventSummary >" +
                "  <eventDesc>TEST EXCEPTION DETAIL</eventDesc >" +
                "  <sourceSystem>FIELDREACH</sourceSystem >" +
                "  <application>FWOM</application >" +
                "  <severity>3</severity >" +
                "  <userCode>FRADM</userCode >" +
                "  <errorCode>1</errorCode >" +
                "  <workOrderNo>20180101</workOrderNo>" +
                "  <districtCode>NA</districtCode>" +
                "</SystemEvent>";
    }
    
    private String getEventLogJSON() {
        return "{\n" +
                "    \"eventDate\": \"20180101\",\n" +
                "    \"eventTime\": \"010101\",\n" +
                "    \"eventCategory\": \"EXCEPTION\",\n" +
                "    \"eventType\": \"ERROR\",\n" +
                "    \"eventSummary\": \"TEST EXCEPTION\",\n" +
                "    \"eventDesc\": \"TEST EXCEPTION DETAIL\",\n" +
                "    \"sourceSystem\": \"FIELDREACH\",\n" +
                "    \"application\": \"FWOM\",\n" +
                "    \"severity\": \"3\",\n" +
                "    \"userCode\": \"FRADM\",\n" +
                "    \"errorCode\": \"1\",\n" +
                "    \"workOrderNo\": \"20180101\",\n" +
                "    \"districtCode\": \"NA\"\n" +
                "  }";
    }
    
  
}