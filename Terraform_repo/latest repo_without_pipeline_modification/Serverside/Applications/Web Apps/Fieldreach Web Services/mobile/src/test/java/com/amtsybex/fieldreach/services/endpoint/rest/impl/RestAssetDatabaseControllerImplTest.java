package com.amtsybex.fieldreach.services.endpoint.rest.impl;

import com.amtsybex.fieldreach.services.download.FileDownloadController;
import com.amtsybex.fieldreach.services.messages.response.AssetDatabaseResponse;
import com.amtsybex.fieldreach.services.messages.response.DownloadPartResponse;
import com.amtsybex.fieldreach.services.messages.response.InitiateDownloadResponse;
import com.amtsybex.fieldreach.services.resource.DatabaseController;
import com.amtsybex.fieldreach.services.resource.common.DatabaseDetails;
import com.amtsybex.fieldreach.services.utils.Utils;
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

import java.io.File;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class RestAssetDatabaseControllerImplTest {

    @Mock
    private DatabaseController databaseController;

    @Mock
    private FileDownloadController fileDownloadController;
    
    @Autowired
    @InjectMocks
    private RestAssetDatabaseControllerImpl assetDatabaseController;

    private final XmlMapper xmlMapper = new XmlMapper();
    
    private final ObjectMapper jsonMapper = new ObjectMapper();
    
    private MockMvc mockMvc;

    @Autowired
    private MappingJackson2HttpMessageConverter jackson2HttpMessageConverter;
    
    @Autowired
    private MappingJackson2XmlHttpMessageConverter jackson2XmlHttpMessageConverter;
    
    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(assetDatabaseController).setMessageConverters(jackson2HttpMessageConverter, jackson2XmlHttpMessageConverter).build();
    }

    @Test
    void getDatabaseFileDetails_shouldValidateDbClassAndGetDbDetails_whenRequestAcceptHeaderInXml() throws Exception {
        // Arrange
        DatabaseDetails stubDbDetails = this.getStubbedDbDetails();
        String dbClass = "DB01";
        Mockito.when(databaseController.getDbDetails(Utils.ASSETDB_TYPE, dbClass, null, null)).thenReturn(stubDbDetails);
        
        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/asset/database?dbClass=" + dbClass)
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();
        // Assert
      
        String expectedxml = "<AssetDatabaseResponse><error/><full><name>name</name><sizeBytes>100</sizeBytes><checksum>name100</checksum></full><success>true</success></AssetDatabaseResponse>";
        assertEquals(expectedxml, mvcResult.getResponse().getContentAsString());
        
        AssetDatabaseResponse assetDatabaseResponse = xmlMapper.readValue(mvcResult.getResponse().getContentAsString(), AssetDatabaseResponse.class);
        assertEquals(stubDbDetails.getName(), assetDatabaseResponse.getFull().getName());
        assertEquals(stubDbDetails.getSize(), assetDatabaseResponse.getFull().getSizeBytes());
        assertEquals(stubDbDetails.getChecksum(), assetDatabaseResponse.getFull().getChecksum());
        assertTrue(assetDatabaseResponse.isSuccess());
        Mockito.verify(databaseController, times(1)).getDbDetails(Utils.ASSETDB_TYPE, dbClass, null, null);
    }

    @Test
    void getDatabaseFileDetails_shouldValidateDbClassAndGetDbDetails_whenRequestAcceptHeaderInJson() throws Exception {
        // Arrange
        DatabaseDetails stubDbDetails = this.getStubbedDbDetails();
        String dbClass = "DB01";
        Mockito.when(databaseController.getDbDetails(Utils.ASSETDB_TYPE, dbClass, null, null)).thenReturn(stubDbDetails);

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/asset/database?dbClass=" + dbClass)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        AssetDatabaseResponse assetDatabaseResponse = jsonMapper.readValue(mvcResult.getResponse().getContentAsString(), AssetDatabaseResponse.class);
        assertEquals(stubDbDetails.getName(), assetDatabaseResponse.getFull().getName());
        assertEquals(stubDbDetails.getSize(), assetDatabaseResponse.getFull().getSizeBytes());
        assertEquals(stubDbDetails.getChecksum(), assetDatabaseResponse.getFull().getChecksum());
        assertTrue(assetDatabaseResponse.isSuccess());
        Mockito.verify(databaseController, times(1)).getDbDetails(Utils.ASSETDB_TYPE, dbClass, null, null);
    }

    @Test
    void initiateDbDownload_shouldValidateAndGetDbLocationAndReturnInitiateDownloadResponse_whenRequestAcceptHeaderInXml() throws Exception {
        String fileName = "test.zip";
        String dbClass = "DB01";
        String stubDbLocation = "/test/location";
        InitiateDownloadResponse stubDownloadResponse = getStubbedInitiateDownloadResponse(fileName);
        Mockito.when(databaseController.getDbLocation(Utils.ASSETDB_TYPE, dbClass, null, null)).thenReturn(stubDbLocation);
        Mockito.when(fileDownloadController.initiateFileSystemDownload(null, stubDbLocation + File.separator + fileName)).thenReturn(stubDownloadResponse);

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/asset/database/" + fileName + "/multipart?dbClass=" + dbClass)
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();
        				    //<InitiateDownloadResponse><success>true</success><error/><identifier>I</identifier><totalParts>1</totalParts><checksum>checksum</checksum><fileName>test.zip</fileName></InitiateDownloadResponse>
        // Assert
        String expectedxml = "<InitiateDownloadResponse><success>true</success><identifier>I</identifier><totalParts>1</totalParts><checksum>checksum</checksum><fileName>test.zip</fileName></InitiateDownloadResponse>";
        assertEquals(expectedxml, mvcResult.getResponse().getContentAsString());
        
        InitiateDownloadResponse response = xmlMapper.readValue(mvcResult.getResponse().getContentAsString(), InitiateDownloadResponse.class);
        assertEquals(stubDownloadResponse.getFileName(), response.getFileName());
        assertEquals(stubDownloadResponse.getIdentifier(), response.getIdentifier());
        assertEquals(stubDownloadResponse.getChecksum(), response.getChecksum());
        assertEquals(stubDownloadResponse.getTotalParts(), response.getTotalParts());
        assertTrue(response.isSuccess());
        assertNull(response.getError());
        Mockito.verify(databaseController, times(1)).getDbLocation(Utils.ASSETDB_TYPE, dbClass, null, null);
        Mockito.verify(fileDownloadController, times(1)).initiateFileSystemDownload(null, stubDbLocation + File.separator + fileName);
    }

    @Test
    void initiateDbDownload_shouldValidateAndGetDbLocationAndReturnInitiateDownloadResponse_whenRequestAcceptHeaderInJson() throws Exception {
        String fileName = "test.zip";
        String dbClass = "DB01";
        String stubDbLocation = "/test/location";
        InitiateDownloadResponse stubDownloadResponse = getStubbedInitiateDownloadResponse(fileName);
        Mockito.when(databaseController.getDbLocation(Utils.ASSETDB_TYPE, dbClass, null, null)).thenReturn(stubDbLocation);
        Mockito.when(fileDownloadController.initiateFileSystemDownload(null, stubDbLocation + File.separator + fileName)).thenReturn(stubDownloadResponse);

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/asset/database/" + fileName + "/multipart?dbClass=" + dbClass)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        InitiateDownloadResponse response = jsonMapper.readValue(mvcResult.getResponse().getContentAsString(), InitiateDownloadResponse.class);
        assertEquals(stubDownloadResponse.getFileName(), response.getFileName());
        assertEquals(stubDownloadResponse.getIdentifier(), response.getIdentifier());
        assertEquals(stubDownloadResponse.getChecksum(), response.getChecksum());
        assertEquals(stubDownloadResponse.getTotalParts(), response.getTotalParts());
        assertTrue(response.isSuccess());
        assertNull(response.getError());
        Mockito.verify(databaseController, times(1)).getDbLocation(Utils.ASSETDB_TYPE, dbClass, null, null);
        Mockito.verify(fileDownloadController, times(1)).initiateFileSystemDownload(null, stubDbLocation + File.separator + fileName);
    }

    @Test
    void downloadDbPart_shouldDownloadPartAndReturnResponse_whenRequestAcceptHeaderInXml() throws Exception {
        String filename = "test.zip";
        String identifier = "ID";
        int part = 2;
        DownloadPartResponse stubbedDownloadPartResponse = getStubbedDownloadPartResponse(identifier, part);
        Mockito.when(fileDownloadController.downloadPart(null, identifier, part)).thenReturn(stubbedDownloadPartResponse);

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/asset/database/" + filename + "/multipart/"+ identifier + "/" + part)
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();
        //<DownloadPartResponse><success>true</success><error/><identifier>ID</identifier><partData>2</partData></DownloadPartResponse>
        // Assert
        String expectedxml = "<DownloadPartResponse><success>true</success><identifier>ID</identifier><partData>2</partData></DownloadPartResponse>";
        assertEquals(expectedxml, mvcResult.getResponse().getContentAsString());
        
        DownloadPartResponse response = xmlMapper.readValue(mvcResult.getResponse().getContentAsString(), DownloadPartResponse.class);
        assertEquals(stubbedDownloadPartResponse.getIdentifier(), response.getIdentifier());
        assertEquals(stubbedDownloadPartResponse.getPartData(), response.getPartData());
        assertTrue(response.isSuccess());
        assertNull(response.getError());
    }

    @Test
    void downloadDbPart_shouldDownloadPartAndReturnResponse_whenRequestAcceptHeaderInJson() throws Exception {
        String filename = "test.zip";
        String identifier = "ID";
        int part = 2;
        DownloadPartResponse stubbedDownloadPartResponse = getStubbedDownloadPartResponse(identifier, part);
        Mockito.when(fileDownloadController.downloadPart(null, identifier, part)).thenReturn(stubbedDownloadPartResponse);

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/asset/database/" + filename + "/multipart/"+ identifier + "/" + part)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        DownloadPartResponse response = jsonMapper.readValue(mvcResult.getResponse().getContentAsString(), DownloadPartResponse.class);
        assertEquals(stubbedDownloadPartResponse.getIdentifier(), response.getIdentifier());
        assertEquals(stubbedDownloadPartResponse.getPartData(), response.getPartData());
        assertTrue(response.isSuccess());
        assertNull(response.getError());
    }

    private DownloadPartResponse getStubbedDownloadPartResponse(String identifier, int part) {
        DownloadPartResponse downloadPartResponse = new DownloadPartResponse();
        downloadPartResponse.setIdentifier(identifier);
        downloadPartResponse.setPartData(String.valueOf(part));
        downloadPartResponse.setSuccess(true);
        downloadPartResponse.setError(null);
        return downloadPartResponse;
    }

    private InitiateDownloadResponse getStubbedInitiateDownloadResponse(String fileName) {
        InitiateDownloadResponse initiateDownloadResponse = new InitiateDownloadResponse();
        initiateDownloadResponse.setFileName(fileName);
        initiateDownloadResponse.setIdentifier("I");
        initiateDownloadResponse.setChecksum("checksum");
        initiateDownloadResponse.setTotalParts(BigInteger.ONE);
        initiateDownloadResponse.setSuccess(true);
        initiateDownloadResponse.setError(null);
        return initiateDownloadResponse;
    }
    
    private DatabaseDetails getStubbedDbDetails() {
        DatabaseDetails databaseDetails = new DatabaseDetails();
        databaseDetails.setName("name");
        databaseDetails.setSize(100);
        databaseDetails.setChecksum("name100");
        return databaseDetails;
    }
}