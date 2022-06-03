package com.amtsybex.fieldreach.services.endpoint.rest.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringBootTest
public class RestVersionControllerImplTest {
    
    private MockMvc mockMvc;
    
    @Autowired
    private RestVersionControllerImpl restVersionController;
    
    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(restVersionController).build();
    }
    
    @Test
    public void getVersion_shouldReturnTheVersionOfTheApplicationFromConfigurationFile_whenAcceptHeaderInXml() throws Exception {
        
        // ACT
        MvcResult mvcResult = this.mockMvc.perform(get("/version")
                .accept(MediaType.APPLICATION_XML)
                .contentType(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();
        
        // ASSERT
        String response = mvcResult.getResponse().getContentAsString();
        assertNotNull(response);
    }

    @Test
    public void getVersion_shouldReturnTheVersionOfTheApplicationFromConfigurationFile_whenAcceptHeaderInJson() throws Exception {

        // ACT
        MvcResult mvcResult = this.mockMvc.perform(get("/version")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // ASSERT
        String response = mvcResult.getResponse().getContentAsString();
        assertNotNull(response);
    }

}