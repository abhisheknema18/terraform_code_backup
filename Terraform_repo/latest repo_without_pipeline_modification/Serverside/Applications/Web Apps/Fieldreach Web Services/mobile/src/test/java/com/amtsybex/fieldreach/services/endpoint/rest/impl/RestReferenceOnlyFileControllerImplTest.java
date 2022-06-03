package com.amtsybex.fieldreach.services.endpoint.rest.impl;

import com.amtsybex.fieldreach.services.endpoint.common.ReferenceOnlyFileController;
import com.amtsybex.fieldreach.services.messages.response.GetScriptReferenceResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class RestReferenceOnlyFileControllerImplTest {

    @Mock
    private ReferenceOnlyFileController referenceOnlyFileController;
    
    @Autowired
    @InjectMocks
    private RestReferenceOnlyFileControllerImpl restReferenceOnlyFileController;

    private final XmlMapper xmlMapper = new XmlMapper();

    private final ObjectMapper jsonMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(restReferenceOnlyFileController).build();
    }

    @Test
    void getReferenceOnlyFile_shouldGetReferenceFileContentAndResponse_whenAcceptHeaderInXml() throws Exception {
        // ARRANGE
        String filename = "test.xml";
        String sampleFileContent = "Sample file content";
        when(referenceOnlyFileController.getReferenceOnlyFileContents(filename, null)).thenReturn(sampleFileContent);

        // ACT
        MvcResult mvcResult = this.mockMvc.perform(get("/script/reference/" + filename)
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();
        
        // ASSERT
        GetScriptReferenceResponse response = xmlMapper.readValue(mvcResult.getResponse().getContentAsString(), GetScriptReferenceResponse.class);
        assertTrue(response.isSuccess());
        assertEquals(sampleFileContent, response.getReferenceFileSource());
        verify(referenceOnlyFileController, times(1)).getReferenceOnlyFileContents(filename, null);
    }

    @Test
    void getReferenceOnlyFile_shouldGetReferenceFileContentAndResponse_whenAcceptHeaderInJson() throws Exception {
        // ARRANGE
        String filename = "test.xml";
        String sampleFileContent = "Sample file content";
        when(referenceOnlyFileController.getReferenceOnlyFileContents(filename, null)).thenReturn(sampleFileContent);

        // ACT
        MvcResult mvcResult = this.mockMvc.perform(get("/script/reference/" + filename)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // ASSERT
        GetScriptReferenceResponse response = jsonMapper.readValue(mvcResult.getResponse().getContentAsString(), GetScriptReferenceResponse.class);
        assertTrue(response.isSuccess());
        assertEquals(sampleFileContent, response.getReferenceFileSource());
        verify(referenceOnlyFileController, times(1)).getReferenceOnlyFileContents(filename, null);
    }
}