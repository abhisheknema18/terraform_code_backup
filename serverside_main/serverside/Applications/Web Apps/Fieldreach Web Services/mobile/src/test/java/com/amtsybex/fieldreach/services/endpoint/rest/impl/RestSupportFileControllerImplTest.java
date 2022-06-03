package com.amtsybex.fieldreach.services.endpoint.rest.impl;

import com.amtsybex.fieldreach.services.download.FileDownloadController;
import com.amtsybex.fieldreach.services.endpoint.common.SupportFileController;
import com.amtsybex.fieldreach.services.messages.response.DownloadPartResponse;
import com.amtsybex.fieldreach.services.messages.response.InitiateDownloadResponse;
import com.amtsybex.fieldreach.services.messages.response.SupportFileListResponse;
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

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class RestSupportFileControllerImplTest {

    @Mock
    private SupportFileController supportFileController;

    @Mock
    private FileDownloadController fileDownloadController;
    
    @Autowired
    @InjectMocks
    private RestSupportFileControllerImpl restSupportFileController;

    private final XmlMapper xmlMapper = new XmlMapper();

    private final ObjectMapper jsonMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(restSupportFileController).build();
    }
    
    @Test
    void getSupportFileList_shouldReturnSupportFileList_whenAcceptsHeaderInXml() throws Exception {
        // Arrange
        ArrayList<String> strings = new ArrayList<>();
        strings.add("TestFile");
        when(supportFileController.supportFileListing(null)).thenReturn(strings);
        
        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/support/resource")
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();
        
        // Assert
        SupportFileListResponse supportFileListResponse = xmlMapper.readValue(mvcResult.getResponse().getContentAsString(), SupportFileListResponse.class);
        assertTrue(supportFileListResponse.isSuccess());
        assertFalse(supportFileListResponse.getSupportFileList().isEmpty());
        assertEquals("TestFile", supportFileListResponse.getSupportFileList().get(0).getFileName());
    }

    @Test
    void getSupportFileList_shouldReturnSupportFileList_whenAcceptsHeaderInJson() throws Exception {
        // Arrange
        ArrayList<String> strings = new ArrayList<>();
        strings.add("TestFile");
        when(supportFileController.supportFileListing(null)).thenReturn(strings);

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/support/resource")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        SupportFileListResponse supportFileListResponse = jsonMapper.readValue(mvcResult.getResponse().getContentAsString(), SupportFileListResponse.class);
        assertTrue(supportFileListResponse.isSuccess());
        assertFalse(supportFileListResponse.getSupportFileList().isEmpty());
        assertEquals("TestFile", supportFileListResponse.getSupportFileList().get(0).getFileName());
    }

    @Test
    void initiateSupportFileDownload_shouldInitiateFileSystemDownload_whenAcceptsHeaderInXml() throws Exception {
        // Arrange
        String filename = "test.xml";
        InitiateDownloadResponse stubbedInitiateDownloadResponse = getStubbedInitiateDownloadResponse(filename);
        when(supportFileController.getSupportFileDir(null)).thenReturn("/temp");
        when(fileDownloadController.initiateFileSystemDownload(any(), any())).thenReturn(stubbedInitiateDownloadResponse);
        
        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/support/resource/" + filename)
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();
        
        // Assert
        InitiateDownloadResponse initiateDownloadResponse = xmlMapper.readValue(mvcResult.getResponse().getContentAsString(), InitiateDownloadResponse.class);
        assertTrue(initiateDownloadResponse.isSuccess());
        assertEquals(stubbedInitiateDownloadResponse.getIdentifier(), initiateDownloadResponse.getIdentifier());
        assertEquals(stubbedInitiateDownloadResponse.getChecksum(), initiateDownloadResponse.getChecksum());
        assertEquals(stubbedInitiateDownloadResponse.getFileName(), initiateDownloadResponse.getFileName());
    }

    @Test
    void initiateSupportFileDownload_shouldInitiateFileSystemDownload_whenAcceptsHeaderInJson() throws Exception {
        // Arrange
        String filename = "test.xml";
        InitiateDownloadResponse stubbedInitiateDownloadResponse = getStubbedInitiateDownloadResponse(filename);
        when(supportFileController.getSupportFileDir(null)).thenReturn("/temp");
        when(fileDownloadController.initiateFileSystemDownload(any(), any())).thenReturn(stubbedInitiateDownloadResponse);

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/support/resource/" + filename)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        InitiateDownloadResponse initiateDownloadResponse = jsonMapper.readValue(mvcResult.getResponse().getContentAsString(), InitiateDownloadResponse.class);
        assertTrue(initiateDownloadResponse.isSuccess());
        assertEquals(stubbedInitiateDownloadResponse.getIdentifier(), initiateDownloadResponse.getIdentifier());
        assertEquals(stubbedInitiateDownloadResponse.getChecksum(), initiateDownloadResponse.getChecksum());
        assertEquals(stubbedInitiateDownloadResponse.getFileName(), initiateDownloadResponse.getFileName());
    }

    @Test
    void downloadSupportFilePart_shouldDownloadPartAndReturnResponse_whenAcceptsHeaderInXml() throws Exception {
        // Arrange
        String filename = "test.xml";
        String id = "ID";
        String part = "1";
        DownloadPartResponse stubbedResponse = getStubbedResponse(id, part);
        when(fileDownloadController.downloadPart(null, id, 1)).thenReturn(stubbedResponse);
        
        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/support/resource/" + filename + "/multipart/" + id + "/" + part)
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();
        
        // Assert
        DownloadPartResponse response = xmlMapper.readValue(mvcResult.getResponse().getContentAsString(), DownloadPartResponse.class);
        assertTrue(response.isSuccess());
        assertEquals(stubbedResponse.getPartData(), response.getPartData());
        assertEquals(stubbedResponse.getIdentifier(), response.getIdentifier());
    }

    @Test
    void downloadSupportFilePart_shouldDownloadPartAndReturnResponse_whenAcceptsHeaderInJson() throws Exception {
        // Arrange
        String filename = "test.xml";
        String id = "ID";
        String part = "1";
        DownloadPartResponse stubbedResponse = getStubbedResponse(id, part);
        when(fileDownloadController.downloadPart(null, id, 1)).thenReturn(stubbedResponse);

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/support/resource/" + filename + "/multipart/" + id + "/" + part)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        DownloadPartResponse response = jsonMapper.readValue(mvcResult.getResponse().getContentAsString(), DownloadPartResponse.class);
        assertTrue(response.isSuccess());
        assertEquals(stubbedResponse.getPartData(), response.getPartData());
        assertEquals(stubbedResponse.getIdentifier(), response.getIdentifier());
    }

    private DownloadPartResponse getStubbedResponse(String id, String part) {
        DownloadPartResponse stubbedResponse = new DownloadPartResponse();
        stubbedResponse.setSuccess(true);
        stubbedResponse.setPartData(part);
        stubbedResponse.setIdentifier(id);
        return stubbedResponse;
    }

    private InitiateDownloadResponse getStubbedInitiateDownloadResponse(String filename) {
        InitiateDownloadResponse initiateDownloadResponse = new InitiateDownloadResponse();
        initiateDownloadResponse.setSuccess(true);
        initiateDownloadResponse.setChecksum("1234");
        initiateDownloadResponse.setFileName(filename);
        initiateDownloadResponse.setIdentifier("ID");
        return initiateDownloadResponse;
    }
}