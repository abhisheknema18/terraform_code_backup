package com.amtsybex.fieldreach.services.endpoint.rest.impl;

import com.amtsybex.fieldreach.backend.model.ValidationProperty;
import com.amtsybex.fieldreach.backend.model.ValidationType;
import com.amtsybex.fieldreach.backend.model.pk.ValidationPropertyId;
import com.amtsybex.fieldreach.backend.service.ValidationTypeService;
import com.amtsybex.fieldreach.services.messages.response.ValidationTypeResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class RestValidationTypeControllerImplTest {

    private MockMvc mockMvc;

    private final XmlMapper xmlMapper = new XmlMapper();

    private final ObjectMapper jsonMapper = new ObjectMapper();

    @Mock
    private ValidationTypeService validationTypeService;

    @Spy
    @InjectMocks
    @Autowired
    private RestValidationTypeControllerImpl restValidationTypeController;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(restValidationTypeController).build();
    }
    
    @Test
    void getValidationTypeList_shouldValidateValidationRequest_whenAcceptHeaderInXml() throws Exception {
        // Arrange
        ValidationProperty validationProperty = new ValidationProperty();
        validationProperty.setValidationProperty("Property");
        validationProperty.setValidationType("Type");
        validationProperty.setColour("Colour");
        validationProperty.setEquivValue("EquivValue");
        validationProperty.setWeightScore(100);
        validationProperty.setId(new ValidationPropertyId("Type", "Property"));
        ValidationType validationType = new ValidationType("1", "Description");
        when(validationTypeService.getValidationType(any(), any())).thenReturn(validationType);
        when(validationTypeService.getValidationPropertyByValidationTypeWeightScoreDesc(any(), any())).thenReturn(List.of(validationProperty));

        // Act
        MvcResult mvcResult = this.mockMvc.perform(post("/validationType/list")
                .content(getRequest())
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        ValidationTypeResponse response = xmlMapper.readValue(contentAsString, ValidationTypeResponse.class);
        assertTrue(response.isSuccess());
        assertEquals(validationType.getDescription(), response.getValidations().get(0).getValidationDesc());
        assertEquals(validationType.getId(), response.getValidations().get(0).getValidationType());
        assertEquals(validationProperty.getColour(), response.getValidations().get(0).getValidationProperties().get(0).getColor());
        assertEquals(validationProperty.getValidationProperty(), response.getValidations().get(0).getValidationProperties().get(0).getName());
        assertEquals(validationProperty.getWeightScore(), response.getValidations().get(0).getValidationProperties().get(0).getWeightScore());
        assertEquals(validationProperty.getEquivValue(), response.getValidations().get(0).getValidationProperties().get(0).getEquivValue());
    }

    @Test
    void getValidationTypeList_shouldValidateValidationRequest_whenAcceptHeaderInJson() throws Exception {
        // Arrange
        ValidationProperty validationProperty = new ValidationProperty();
        validationProperty.setValidationProperty("Property");
        validationProperty.setValidationType("Type");
        validationProperty.setColour("Colour");
        validationProperty.setEquivValue("EquivValue");
        validationProperty.setWeightScore(100);
        validationProperty.setId(new ValidationPropertyId("Type", "Property"));
        ValidationType validationType = new ValidationType("1", "Description");
        when(validationTypeService.getValidationType(any(), any())).thenReturn(validationType);
        when(validationTypeService.getValidationPropertyByValidationTypeWeightScoreDesc(any(), any())).thenReturn(List.of(validationProperty));

        // Act
        MvcResult mvcResult = this.mockMvc.perform(post("/validationType/list")
                .content(getRequest())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        ValidationTypeResponse response = jsonMapper.readValue(contentAsString, ValidationTypeResponse.class);
        assertTrue(response.isSuccess());
        assertEquals(validationType.getDescription(), response.getValidations().get(0).getValidationDesc());
        assertEquals(validationType.getId(), response.getValidations().get(0).getValidationType());
        assertEquals(validationProperty.getColour(), response.getValidations().get(0).getValidationProperties().get(0).getColor());
        assertEquals(validationProperty.getValidationProperty(), response.getValidations().get(0).getValidationProperties().get(0).getName());
        assertEquals(validationProperty.getWeightScore(), response.getValidations().get(0).getValidationProperties().get(0).getWeightScore());
        assertEquals(validationProperty.getEquivValue(), response.getValidations().get(0).getValidationProperties().get(0).getEquivValue());
    }
    
    @Test
    void getValidationTypeList_shouldReturnBadRequest_whenValidationTypeRequestMessageIsEmpty() throws Exception {
       
        // Act
        MvcResult mvcResult = this.mockMvc.perform(post("/validationType/list")
                .content(" ")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        // Assert
        assertNotNull(mvcResult.getResponse());
	    assertNotNull(mvcResult.getResolvedException().getMessage());
    }
    
    private String getRequest() {
        return "<VALIDATIONTYPEREQUEST>\n" +
                "    <VALIDATIONTYPELIST>\n" +
                "        <VALIDATIONTYPE>valType</VALIDATIONTYPE>\n" +
                "    </VALIDATIONTYPELIST>\n" +
                "    <CHECKSUM>checksum</CHECKSUM>\n" +
                "</VALIDATIONTYPEREQUEST>";
        
    }
}