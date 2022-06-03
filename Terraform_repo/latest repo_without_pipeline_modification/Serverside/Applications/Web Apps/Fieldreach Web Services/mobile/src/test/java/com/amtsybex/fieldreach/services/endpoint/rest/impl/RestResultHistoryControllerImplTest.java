package com.amtsybex.fieldreach.services.endpoint.rest.impl;

import com.amtsybex.fieldreach.backend.model.ResultHistoryExtract;
import com.amtsybex.fieldreach.backend.service.ScriptResultsService;
import com.amtsybex.fieldreach.services.download.FileDownloadController;
import com.amtsybex.fieldreach.services.messages.response.*;
import com.amtsybex.fieldreach.services.resource.DatabaseController;
import com.amtsybex.fieldreach.services.resource.common.DatabaseDetails;
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
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class RestResultHistoryControllerImplTest {

    @Mock
    private DatabaseController databaseController;

    @Mock
    private FileDownloadController fileDownloadController;

    @Mock
    private ScriptResultsService scriptResultsService;

    @Autowired
    private MappingJackson2HttpMessageConverter jackson2HttpMessageConverter;
    
    @Autowired
    private MappingJackson2XmlHttpMessageConverter jackson2XmlHttpMessageConverter;
    
    @Autowired
    @InjectMocks
    private RestResultHistoryControllerImpl restResultHistoryController;

    private final XmlMapper xmlMapper = new XmlMapper();
    
    private final ObjectMapper jsonMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(restResultHistoryController).setMessageConverters(jackson2HttpMessageConverter, jackson2XmlHttpMessageConverter).build();
    }

    @Test
    void getDatabaseFileDetails_shouldGetAndReturnDBDetails_whenAcceptHeaderInXml() throws Exception {
        // Arrange
        DatabaseDetails stubDbDetails = new DatabaseDetails();
        stubDbDetails.setName("testdb");
        stubDbDetails.setSize(100);
        stubDbDetails.setChecksum("testdb-100");
        when(databaseController.getDbDetails(any(), any(), any(), any())).thenReturn(stubDbDetails);

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/result/history/database?dbClass=DB01")
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();
        
        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertEquals("<ResultsHistoryDatabaseResponse><error/><full><name>testdb</name><sizeBytes>100</sizeBytes><checksum>testdb-100</checksum></full><success>true</success></ResultsHistoryDatabaseResponse>",
                contentAsString);
        ResultsHistoryDatabaseResponse response = xmlMapper.readValue(contentAsString, ResultsHistoryDatabaseResponse.class);
        assertTrue(response.isSuccess());
        assertNotNull(response.getFull());
        assertEquals(stubDbDetails.getName(), response.getFull().getName());
        assertEquals(stubDbDetails.getChecksum(), response.getFull().getChecksum());
        assertEquals(stubDbDetails.getSize(), response.getFull().getSizeBytes());
    }

    @Test
    void getDatabaseFileDetails_shouldGetAndReturnDBDetails_whenAcceptHeaderInJson() throws Exception {
        // Arrange
        DatabaseDetails stubDbDetails = new DatabaseDetails();
        stubDbDetails.setName("testdb");
        stubDbDetails.setSize(100);
        stubDbDetails.setChecksum("testdb-100");
        when(databaseController.getDbDetails(any(), any(), any(), any())).thenReturn(stubDbDetails);

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/result/history/database?dbClass=DB01")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        ResultsHistoryDatabaseResponse response = jsonMapper.readValue(contentAsString, ResultsHistoryDatabaseResponse.class);
        assertTrue(response.isSuccess());
        assertNotNull(response.getFull());
        assertEquals(stubDbDetails.getName(), response.getFull().getName());
        assertEquals(stubDbDetails.getChecksum(), response.getFull().getChecksum());
        assertEquals(stubDbDetails.getSize(), response.getFull().getSizeBytes());
    }

    @Test
    void initiateDbDownload_shouldInitiateDbDownload_whenAcceptHeaderInXml() throws Exception {
        // Arrange
        String filename = "test.xml";
        InitiateDownloadResponse stubDownloadResponse = getStubDownloadResponse(filename);
        when(databaseController.getDbLocation(any(), any(), any(), any())).thenReturn("/temp");
        when(fileDownloadController.initiateFileSystemDownload(any(), any())).thenReturn(stubDownloadResponse);
        
        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/result/history/database/" + filename + "/multipart?dbClass=DB01")
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();
        
        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertEquals("<InitiateDownloadResponse><success>true</success><identifier>id</identifier><totalParts>10</totalParts><checksum>abc-1</checksum><fileName>test.xml</fileName></InitiateDownloadResponse>", contentAsString);
        InitiateDownloadResponse response = xmlMapper.readValue(contentAsString, InitiateDownloadResponse.class);
        assertEquals(stubDownloadResponse.getFileName(), response.getFileName());
        assertEquals(stubDownloadResponse.getIdentifier(), response.getIdentifier());
        assertEquals(stubDownloadResponse.getChecksum(), response.getChecksum());
        assertEquals(stubDownloadResponse.getTotalParts(), response.getTotalParts());
        assertTrue(response.isSuccess());
    }

    @Test
    void initiateDbDownload_shouldInitiateDbDownload_whenAcceptHeaderInJson() throws Exception {
        // Arrange
        String filename = "test.xml";
        InitiateDownloadResponse stubDownloadResponse = getStubDownloadResponse(filename);
        when(databaseController.getDbLocation(any(), any(), any(), any())).thenReturn("/temp");
        when(fileDownloadController.initiateFileSystemDownload(any(), any())).thenReturn(stubDownloadResponse);

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/result/history/database/" + filename + "/multipart?dbClass=DB01")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        InitiateDownloadResponse response = jsonMapper.readValue(contentAsString, InitiateDownloadResponse.class);
        assertEquals(stubDownloadResponse.getFileName(), response.getFileName());
        assertEquals(stubDownloadResponse.getIdentifier(), response.getIdentifier());
        assertEquals(stubDownloadResponse.getChecksum(), response.getChecksum());
        assertEquals(stubDownloadResponse.getTotalParts(), response.getTotalParts());
        assertTrue(response.isSuccess());
    }

    @Test
    void downloadDbPart_shouldInitiatePartDbDownload_whenAcceptHeaderInXml() throws Exception {
        // Arrange
        String filename = "test.xml";
        String identifier = "id";
        int part = 1;
        DownloadPartResponse stubbedDownloadResponse = getStubbedDownloadResponse(identifier, part);
        when(fileDownloadController.downloadPart(null, identifier, part)).thenReturn(stubbedDownloadResponse);
        
        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/result/history/database/" + filename + "/multipart/" + identifier + "/" + part)
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertEquals("<DownloadPartResponse><success>true</success><identifier>id</identifier><partData>1</partData></DownloadPartResponse>", contentAsString);
        DownloadPartResponse response = xmlMapper.readValue(contentAsString, DownloadPartResponse.class);
        assertEquals(stubbedDownloadResponse.getIdentifier(), response.getIdentifier());
        assertEquals(stubbedDownloadResponse.getPartData(), response.getPartData());
        assertEquals(stubbedDownloadResponse.isSuccess(), response.isSuccess());
    }

    @Test
    void downloadDbPart_shouldInitiatePartDbDownload_whenAcceptHeaderInJson() throws Exception {
        // Arrange
        String filename = "test.xml";
        String identifier = "id";
        int part = 1;
        DownloadPartResponse stubbedDownloadResponse = getStubbedDownloadResponse(identifier, part);
        when(fileDownloadController.downloadPart(null, identifier, part)).thenReturn(stubbedDownloadResponse);

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/result/history/database/" + filename + "/multipart/" + identifier + "/" + part)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        DownloadPartResponse response = jsonMapper.readValue(contentAsString, DownloadPartResponse.class);
        assertEquals(stubbedDownloadResponse.getIdentifier(), response.getIdentifier());
        assertEquals(stubbedDownloadResponse.getPartData(), response.getPartData());
        assertEquals(stubbedDownloadResponse.isSuccess(), response.isSuccess());
    }

    @Test
    void getDeltaResultsList_shouldReturnDeltaResultListBasedOnCriteria_whenAcceptHeaderInXml() throws Exception {
        // Arrange
        String dbClass = "DB01";
        String deltaSearchDate = "10000000";
        String deltaSearchTime = "100000";
        when(scriptResultsService.getResultHistoryExtractIds(any(), any(), any(), any(), any())).thenReturn(List.of(1, 2));
        
        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/result/history/delta/search?dbClass=" + dbClass + "&deltaSearchDate=" + deltaSearchDate + "&deltaSearchTime=" + deltaSearchTime)
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();
        
        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertEquals("<ResultsHistoryDeltaResponse><success>true</success><error/><deltaResultList><returnId>1</returnId><returnId>2</returnId></deltaResultList></ResultsHistoryDeltaResponse>", contentAsString);
        ResultsHistoryDeltaResponse response = xmlMapper.readValue(contentAsString, ResultsHistoryDeltaResponse.class);
        assertTrue(response.isSuccess());
        assertFalse(response.getDeltaResultList().getDeltaResultList().isEmpty());
        assertTrue(response.getDeltaResultList().getDeltaResultList().contains(1));
        assertTrue(response.getDeltaResultList().getDeltaResultList().contains(2));
    }

    @Test
    void getDeltaResultsList_shouldReturnDeltaResultListBasedOnCriteria_whenAcceptHeaderInJson() throws Exception {
        // Arrange
        String dbClass = "DB01";
        String deltaSearchDate = "10000000";
        String deltaSearchTime = "100000";
        when(scriptResultsService.getResultHistoryExtractIds(any(), any(), any(), any(), any())).thenReturn(List.of(1, 2));

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/result/history/delta/search?dbClass=" + dbClass + "&deltaSearchDate=" + deltaSearchDate + "&deltaSearchTime=" + deltaSearchTime)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        ResultsHistoryDeltaResponse response = jsonMapper.readValue(contentAsString, ResultsHistoryDeltaResponse.class);
        assertTrue(response.isSuccess());
        assertFalse(response.getDeltaResultList().getDeltaResultList().isEmpty());
        assertTrue(response.getDeltaResultList().getDeltaResultList().contains(1));
        assertTrue(response.getDeltaResultList().getDeltaResultList().contains(2));
    }

    @Test
    void getDeltaResult_shouldReturnDeltaScriptResponse_whenAcceptHeaderInXml() throws Exception {
        // Arrange
        int returnId = 1;
        ResultHistoryExtract stubResultHistory = new ResultHistoryExtract();
        stubResultHistory.setFileName("test.xml");
        when(scriptResultsService.getResultHistoryExtract(null, returnId)).thenReturn(stubResultHistory);
        
        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/result/history/delta/" + returnId)
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();
        
        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertEquals("<GetDeltaScriptResultResponse><success>false</success><error><errorCode>ScriptResultNotFoundException</errorCode></error></GetDeltaScriptResultResponse>", contentAsString);
        GetDeltaScriptResultResponse response = xmlMapper.readValue(contentAsString, GetDeltaScriptResultResponse.class);
        assertFalse(response.isSuccess());
        assertNotNull(response.getError());
        assertEquals("ScriptResultNotFoundException", response.getError().getErrorCode());
    }

    @Test
    void getDeltaResult_shouldReturnDeltaScriptResponse_whenAcceptHeaderInJson() throws Exception {
        // Arrange
        int returnId = 1;
        ResultHistoryExtract stubResultHistory = new ResultHistoryExtract();
        stubResultHistory.setFileName("test.xml");
        when(scriptResultsService.getResultHistoryExtract(null, returnId)).thenReturn(stubResultHistory);

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/result/history/delta/" + returnId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        GetDeltaScriptResultResponse response = jsonMapper.readValue(contentAsString, GetDeltaScriptResultResponse.class);
        assertFalse(response.isSuccess());
        assertNotNull(response.getError());
        assertEquals("ScriptResultNotFoundException", response.getError().getErrorCode());
    }

    private DownloadPartResponse getStubbedDownloadResponse(String id, int part) {
        DownloadPartResponse downloadPartResponse = new DownloadPartResponse();
        downloadPartResponse.setIdentifier(id);
        downloadPartResponse.setSuccess(true);
        downloadPartResponse.setPartData(String.valueOf(part));
        return downloadPartResponse;
    }

    private InitiateDownloadResponse getStubDownloadResponse(String filename) {
        InitiateDownloadResponse stubDownloadResponse = new InitiateDownloadResponse();
        stubDownloadResponse.setIdentifier("id");
        stubDownloadResponse.setFileName(filename);
        stubDownloadResponse.setSuccess(true);
        stubDownloadResponse.setTotalParts(BigInteger.TEN);
        stubDownloadResponse.setChecksum("abc-1");
        return stubDownloadResponse;
    }
}