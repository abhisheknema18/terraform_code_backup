package com.amtsybex.fieldreach.services.endpoint.rest.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.transform.stream.StreamSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.exception.ScriptDefinitionNotFoundException;
import com.amtsybex.fieldreach.extract.core.ExtractEngine;
import com.amtsybex.fieldreach.services.messages.response.GetScriptReportResponse;
import com.amtsybex.fieldreach.utils.impl.Common;

/**
 * Unit Test Class to test RestScriptControllerImpl class.
 * @See RestScriptControllerImpl
 * @author CroninM
 */

@RunWith(MockitoJUnitRunner.class)
public class RestScriptControllerImplTest {

	@Mock
    private ExtractEngine mockExtract;
	
	@Autowired
	@Spy
	protected XStreamMarshaller mockRestApiMarshaller;
	
	 
	@InjectMocks
	private RestScriptControllerImpl mockScriptController;
	
	@Before
	public void init() {
		MockitoAnnotations.openMocks(this);
	}
	
    @Test
    //TODO leaving this as a todo - Junit5 allows for this display name to allow more information to show on the results than just the function name.
    //@DisplayName("Test with valid script id, valid response should be returned")
    public void getScriptReport_Should_ReturnValid_When_ValidScriptIdSupplied() throws Exception {

    	// [arrange]
        Mockito.when(mockExtract.getScriptReport(ArgumentMatchers.isNull(), ArgumentMatchers.anyInt())).thenReturn("excelreportdata".getBytes());
        
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(mockScriptController).build();
		
        String expectedScriptReportBase64 = Common.encodeBase64("excelreportdata".getBytes());
        String expectedChecksum = Common.generateMD5Checksum("excelreportdata".getBytes());
	
		// [act]
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/script/1/exceldefreport"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
   
        GetScriptReportResponse response = this.parseResponseToGetScriptReportResponseUsingXStream(result.getResponse().getContentAsString());
        
        // [assert]
        assertTrue(response.isSuccess());
        
        assertEquals(expectedScriptReportBase64, response.getScriptReportSource());

        assertEquals(expectedChecksum, response.getChecksum());

    }
    
    @Test
    //@DisplayName("Test with invalid script invalid, ScriptDefinitionNotFoundException should be returned")
    public void getScriptReport_Should_ReturnScriptDefinitionNotFoundException_When_InvalidScriptIdSupplied() throws Exception {
        
    	// [arrange]
        Mockito.when(mockExtract.getScriptReport(ArgumentMatchers.isNull(), ArgumentMatchers.anyInt())).thenThrow(ScriptDefinitionNotFoundException.class);
        
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(mockScriptController).build();
	
		// [act]
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/script/2/exceldefreport"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
   
        GetScriptReportResponse response = this.parseResponseToGetScriptReportResponseUsingXStream(result.getResponse().getContentAsString());
        
        // [assert]
        assertFalse(response.isSuccess());
        
        assertEquals("ScriptDefinitionNotFoundException", response.getError().getErrorCode());
        
    }
    
    @Test
    //@DisplayName("Test with valid script invalid but template not available, General Exception should be returned")
    public void getScriptReport_Should_ReturnEXCEPTION_When_TemplateFileNotFound() throws Exception {
        
    	// [arrange]
        Mockito.when(mockExtract.getScriptReport(ArgumentMatchers.isNull(), ArgumentMatchers.anyInt())).thenThrow(FileNotFoundException.class);
        
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(mockScriptController).build();
	
		// [act]
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/script/1/exceldefreport"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
   
        GetScriptReportResponse response = this.parseResponseToGetScriptReportResponseUsingXStream(result.getResponse().getContentAsString());
        
        // [assert]
        assertFalse(response.isSuccess());
        
        assertEquals("EXCEPTION", response.getError().getErrorCode());
        
    }
    
    
    @Test
    //@DisplayName("Test with valid script invalid Mock DB Exception, FRInstnaceException should be returned")
    public void getScriptReport_Should_ReturnFRInstanceException_When_DatabaseExceptionsOccur() throws Exception {

    	// [arrange]
        Mockito.when(mockExtract.getScriptReport(ArgumentMatchers.isNull(), ArgumentMatchers.anyInt())).thenThrow(FRInstanceException.class);
        
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(mockScriptController).build();
	
		// [act]
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/script/1/exceldefreport"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        GetScriptReportResponse response = this.parseResponseToGetScriptReportResponseUsingXStream(result.getResponse().getContentAsString());
        
        // [assert]
        assertFalse(response.isSuccess());
        
        assertEquals("FRInstanceException", response.getError().getErrorCode());
        
        
    }
    
    
    private GetScriptReportResponse parseResponseToGetScriptReportResponseUsingXStream(String response) throws XmlMappingException, IOException{
    	return (GetScriptReportResponse) mockRestApiMarshaller.unmarshal(new StreamSource(new StringReader(response)));
    }
    
}
