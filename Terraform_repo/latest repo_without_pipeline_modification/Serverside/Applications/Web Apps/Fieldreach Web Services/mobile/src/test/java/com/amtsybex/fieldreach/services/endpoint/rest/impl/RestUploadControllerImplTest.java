package com.amtsybex.fieldreach.services.endpoint.rest.impl;

import static com.amtsybex.fieldreach.services.messages.response.UploadInitiateResponse.APPLICATION_VND_FIELDSMART_UPLOAD_INITIATE_1_XML;
import static com.amtsybex.fieldreach.services.messages.response.UploadInitiateResponse.APPLICATION_VND_FIELDSMART_UPLOAD_INITIATE_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.UploadPartResponse.APPLICATION_VND_FIELDSMART_UPLOAD_PART_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.UploadPartResponse.APPLICATION_VND_FIELDSMART_UPLOAD_PART_1_XML;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.math.BigInteger;
import java.util.HashSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.model.InProgressUploads;
import com.amtsybex.fieldreach.backend.model.UploadParts;
import com.amtsybex.fieldreach.backend.model.pk.UploadPartsId;
import com.amtsybex.fieldreach.services.endpoint.common.FileTransferController;
import com.amtsybex.fieldreach.services.messages.request.UploadInitiate;
import com.amtsybex.fieldreach.services.messages.request.UploadPart;
import com.amtsybex.fieldreach.services.messages.response.UploadInitiateResponse;
import com.amtsybex.fieldreach.services.messages.response.UploadPartResponse;
import com.amtsybex.fieldreach.services.upload.UploadController;
import com.amtsybex.fieldreach.utils.impl.Common;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class RestUploadControllerImplTest {

	private MockMvc mockMvc;
	 
	private ObjectMapper xmlMapper;
	
	@Autowired
    private MappingJackson2XmlHttpMessageConverter xmlConverter;
	
	@Autowired
    private ObjectMapper jacksonObjectMapper;
	
	@Autowired
    private RestUploadControllerImpl restUploadController;
	
	@Autowired
	private UploadController fileUploadController;
    
    @MockBean
	protected FileTransferController fileTransferController;
    
    
    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(restUploadController).build();
        fileUploadController.setMaxUploadSize("0");
        fileUploadController.setMaxChunkSize(1024000);
        xmlMapper = xmlConverter.getObjectMapper();
    }
    
    
    /*
     * 
     * File Initiate Upload TESTS
     * 
     * 
     */
    
    @Test
    void initiateUpload_shouldValidateUploadRequest_whenAcceptHeaderInXml() throws Exception {
        // ARRANGE
        String filename = "test.xml";
        String id = "ID";
        int totalSizeBytes = 1024;
        UploadInitiate stubbedUploadInitiate = getStubbedUpdateInitiate(filename, id, totalSizeBytes);

        // ACT
        MvcResult mvcResult = this.mockMvc.perform(post("/files/" + filename + "/multipart")
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML)
                .content(xmlMapper.writeValueAsString(stubbedUploadInitiate)))
                .andExpect(status().isOk())
                .andReturn();
        
        // ASSERT
        String contentAsString = mvcResult.getResponse().getContentAsString();
        
        UploadInitiateResponse response = xmlMapper.readValue(contentAsString, UploadInitiateResponse.class);
       
        assertTrue(response.isSuccess());
        assertNull(response.getError().getErrorCode());
        assertEquals(1024000, response.getMaxChunkSizeBytes());
        assertEquals(BigInteger.ONE, response.getStartFromPart());
    }
    
    
    @Test
    void initiateUpload_shouldReturnValidContentType_whenRequestContentTypeInFusionXml() throws Exception {
        // ARRANGE
        String filename = "test.xml";
        String id = "ID";
        int totalSizeBytes = 1024;
        UploadInitiate stubbedUploadInitiate = getStubbedUpdateInitiate(filename, id, totalSizeBytes);

        // ACT
        MvcResult mvcResult = this.mockMvc.perform(post("/files/" + filename + "/multipart")
                .contentType(APPLICATION_VND_FIELDSMART_UPLOAD_INITIATE_1_XML)
                .accept(APPLICATION_VND_FIELDSMART_UPLOAD_INITIATE_1_XML)
                .content(xmlMapper.writeValueAsString(stubbedUploadInitiate)))
                .andExpect(status().isOk())
                .andReturn();
        
        // ASSERT
        String contentAsString = mvcResult.getResponse().getContentAsString();
        String contentType = mvcResult.getResponse().getContentType();
        
        assertEquals(APPLICATION_VND_FIELDSMART_UPLOAD_INITIATE_1_XML,contentType);
        
        UploadInitiateResponse response = xmlMapper.readValue(contentAsString, UploadInitiateResponse.class);
        
        assertTrue(response.isSuccess());
        assertNull(response.getError().getErrorCode());
        assertEquals(1024000, response.getMaxChunkSizeBytes());
        assertEquals(BigInteger.ONE, response.getStartFromPart());
    }
    
    @Test
    void initiateUpload_shouldValidateUploadRequest_whenAcceptHeaderInJSON() throws Exception {
        // ARRANGE
        String filename = "test.xml";
        String id = "ID";
        int totalSizeBytes = 1024;
        UploadInitiate stubbedUploadInitiate = getStubbedUpdateInitiate(filename, id, totalSizeBytes);

        // ACT
        MvcResult mvcResult = this.mockMvc.perform(post("/files/" + filename + "/multipart")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jacksonObjectMapper.writeValueAsString(stubbedUploadInitiate)))
                .andExpect(status().isOk())
                .andReturn();
        
        // ASSERT
        String contentAsString = mvcResult.getResponse().getContentAsString();
        
        UploadInitiateResponse response = jacksonObjectMapper.readValue(contentAsString, UploadInitiateResponse.class);
        
        assertTrue(response.isSuccess());
        assertNull(response.getError().getErrorCode());
        assertEquals(1024000, response.getMaxChunkSizeBytes());
        assertEquals(BigInteger.ONE, response.getStartFromPart());
    }
    
    
    @Test
    void initiateUpload_shouldReturnValidContentType_whenRequestContentTypeInFusionJSON() throws Exception {
        // ARRANGE
        String filename = "test.xml";
        String id = "ID";
        int totalSizeBytes = 1024;
        UploadInitiate stubbedUploadInitiate = getStubbedUpdateInitiate(filename, id, totalSizeBytes);

        // ACT
        MvcResult mvcResult = this.mockMvc.perform(post("/files/" + filename + "/multipart")
                .contentType(APPLICATION_VND_FIELDSMART_UPLOAD_INITIATE_1_JSON)
                .accept(APPLICATION_VND_FIELDSMART_UPLOAD_INITIATE_1_JSON)
                .content(jacksonObjectMapper.writeValueAsString(stubbedUploadInitiate)))
                .andExpect(status().isOk())
                .andReturn();
        
        // ASSERT
        String contentAsString = mvcResult.getResponse().getContentAsString();
        
        String contentType = mvcResult.getResponse().getContentType();
        
        assertEquals(APPLICATION_VND_FIELDSMART_UPLOAD_INITIATE_1_JSON,contentType);
        
        UploadInitiateResponse response = jacksonObjectMapper.readValue(contentAsString, UploadInitiateResponse.class);
        
        assertTrue(response.isSuccess());
        assertNull(response.getError().getErrorCode());
        assertEquals(1024000, response.getMaxChunkSizeBytes());
        assertEquals(BigInteger.ONE, response.getStartFromPart());
    }

    @Test
    void initiateUpload_shouldBadRequest_whenBodyMissing() throws Exception {
        // ARRANGE
        String filename = "test.xml";

        // ACT & ASSERT
        this.mockMvc.perform(post("/files/" + filename + "/multipart")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest())
                .andReturn();

    }
    
    @Test
    void initiateUpload_shouldBadRequest_whenFileNameMissing() throws Exception {
        // ARRANGE
        String filename = "test.xml";
        String id = "ID";
        int totalSizeBytes = 1024;
        UploadInitiate stubbedUploadInitiate = getStubbedUpdateInitiate(null, id, totalSizeBytes);

        // ACT & ASSERT
        this.mockMvc.perform(post("/files/" + filename + "/multipart")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(xmlMapper.writeValueAsString(stubbedUploadInitiate)))
                .andExpect(status().isBadRequest())
                .andReturn();

    }
    
    @Test
    void initiateUpload_shouldBadRequest_whenTotalSizeInvalid() throws Exception {
        // ARRANGE
        String filename = "test.xml";
        String id = "ID";
        int totalSizeBytes = -1;
        UploadInitiate stubbedUploadInitiate = getStubbedUpdateInitiate(null, id, totalSizeBytes);

        // ACT & ASSERT
        this.mockMvc.perform(post("/files/" + filename + "/multipart")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(xmlMapper.writeValueAsString(stubbedUploadInitiate)))
                .andExpect(status().isBadRequest())
                .andReturn();

    }
    
    @Test
    void initiateUpload_shouldBadRequest_whenTotalSizeAboveMaxAllowed() throws Exception {
        // ARRANGE
        String filename = "test.xml";
        String id = "ID";
        int totalSizeBytes = 100000000;
        fileUploadController.setMaxUploadSize("1000000");
        
        UploadInitiate stubbedUploadInitiate = getStubbedUpdateInitiate(filename, id, totalSizeBytes);

        // ACT
        MvcResult mvcResult = this.mockMvc.perform(post("/files/" + filename + "/multipart")
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML)
                .content(xmlMapper.writeValueAsString(stubbedUploadInitiate)))
                .andExpect(status().isOk())
                .andReturn();
        
        //ASSERT
        String contentAsString = mvcResult.getResponse().getContentAsString();
        UploadInitiateResponse response = xmlMapper.readValue(contentAsString, UploadInitiateResponse.class);
        assertFalse(response.isSuccess());
        assertEquals("MaximumFileSizeExceededException", response.getError().getErrorCode());

    }
    
    @Test
    void initiateUpload_shouldReturnNewUpload_whenNoPreviousUpload() throws Exception {
        // ARRANGE
        String filename = "test.xml";
        String id = "ID";
        int totalSizeBytes = 1000;
        
        UploadInitiate stubbedUploadInitiate = getStubbedUpdateInitiate(filename, id, totalSizeBytes);
        when(fileTransferController.isExistingUpload(any(), any())).thenReturn(false);
        

        // ACT
        MvcResult mvcResult = this.mockMvc.perform(post("/files/" + filename + "/multipart")
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML)
                .content(xmlMapper.writeValueAsString(stubbedUploadInitiate)))
                .andExpect(status().isOk())
                .andReturn();
        
        //ASSERT
        String contentAsString = mvcResult.getResponse().getContentAsString();
        
        UploadInitiateResponse response = xmlMapper.readValue(contentAsString, UploadInitiateResponse.class);
        
        assertTrue(response.isSuccess());
        assertEquals(BigInteger.valueOf(1), response.getStartFromPart());

    }
    
    @Test
    void initiateUpload_shouldReturnExistingUpload_whenPreviousUpload() throws Exception {
        // ARRANGE
        String filename = "test.xml";
        String id = "ID";
        int totalSizeBytes = 1000;
        
        UploadInitiate stubbedUploadInitiate = getStubbedUpdateInitiate(filename, id, totalSizeBytes);
        when(fileTransferController.isExistingUpload(any(), any())).thenReturn(true);
        when(fileTransferController.findUpload(any(), any())).thenReturn(this.getStubbedUpdateInProgressUpload(3, 2));

        // ACT
        MvcResult mvcResult = this.mockMvc.perform(post("/files/" + filename + "/multipart")
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML)
                .content(xmlMapper.writeValueAsString(stubbedUploadInitiate)))
                .andExpect(status().isOk())
                .andReturn();
        
        //ASSERT
        String contentAsString = mvcResult.getResponse().getContentAsString();
        
        UploadInitiateResponse response = xmlMapper.readValue(contentAsString, UploadInitiateResponse.class);
        
        assertTrue(response.isSuccess());
        assertEquals(BigInteger.valueOf(2), response.getStartFromPart());

    }
    
    
    /*
     * 
     * File Part Upload TESTS
     * 
     * 
     */

    @Test
    void uploadPart_shouldValidateUploadPartRequestAndRespond_whenAcceptHeaderInXml() throws Exception {
        // ARRANGE
        String filename = "test.xml";
        String id = "ID";
        UploadPart uploadPart = getUploadPart(BigInteger.ONE);

        when(fileTransferController.isExistingUpload(any(), any())).thenReturn(true);
        when(fileTransferController.findUpload(any(), any())).thenReturn(this.getStubbedUpdateInProgressUpload(1, 1));
        
        // ACT
        MvcResult mvcResult = this.mockMvc.perform(put("/files/" + filename + "/multipart/" + id)
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML)
                .content(xmlMapper.writeValueAsString(uploadPart)))
                .andExpect(status().isOk())
                .andReturn();
        
        // ASSERT
        String contentAsString = mvcResult.getResponse().getContentAsString();
        
        UploadPartResponse response = xmlMapper.readValue(contentAsString, UploadPartResponse.class);
        
        assertTrue(response.isSuccess());
        assertTrue(response.isComplete());
    }
    
    @Test
    void uploadPart_shouldValidateContentType_whenContentTypeInXml() throws Exception {
        // ARRANGE
        String filename = "test.xml";
        String id = "ID";
        UploadPart uploadPart = getUploadPart(BigInteger.ONE);
        
        when(fileTransferController.isExistingUpload(any(), any())).thenReturn(true);
        when(fileTransferController.findUpload(any(), any())).thenReturn(this.getStubbedUpdateInProgressUpload(1, 1));
        
        // ACT
        MvcResult mvcResult = this.mockMvc.perform(put("/files/" + filename + "/multipart/" + id)
                .contentType(APPLICATION_VND_FIELDSMART_UPLOAD_PART_1_XML)
                .accept(APPLICATION_VND_FIELDSMART_UPLOAD_PART_1_XML)
                .content(xmlMapper.writeValueAsString(uploadPart)))
                .andExpect(status().isOk())
                .andReturn();
        
        // ASSERT
        String contentAsString = mvcResult.getResponse().getContentAsString();
        String contentType = mvcResult.getResponse().getContentType();
        
        UploadPartResponse response = xmlMapper.readValue(contentAsString, UploadPartResponse.class);
        
        assertTrue(response.isSuccess());
        assertTrue(response.isComplete());
        assertEquals(APPLICATION_VND_FIELDSMART_UPLOAD_PART_1_XML, contentType);
    }

    @Test
    void uploadPart_shouldValidateUploadPartRequestAndRespond_whenAcceptHeaderInJson() throws Exception {
        // ARRANGE
        String filename = "test.xml";
        String id = "ID";
        UploadPart uploadPart = getUploadPart(BigInteger.ONE);

        when(fileTransferController.isExistingUpload(any(), any())).thenReturn(true);
        when(fileTransferController.findUpload(any(), any())).thenReturn(this.getStubbedUpdateInProgressUpload(1, 1));
        
        // ACT
        MvcResult mvcResult = this.mockMvc.perform(put("/files/" + filename + "/multipart/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jacksonObjectMapper.writeValueAsString(uploadPart)))
                .andExpect(status().isOk())
                .andReturn();

        // ASSERT
        String contentAsString = mvcResult.getResponse().getContentAsString();
        
        UploadPartResponse response = jacksonObjectMapper.readValue(contentAsString, UploadPartResponse.class);
        
        assertTrue(response.isSuccess());
        assertTrue(response.isComplete());

    }
    
    @Test
    void uploadPart_shouldValidateContentType_whenContentTypeInJson() throws Exception {
        // ARRANGE
        String filename = "test.xml";
        String id = "ID";
        UploadPart uploadPart = getUploadPart(BigInteger.ONE);

        when(fileTransferController.isExistingUpload(any(), any())).thenReturn(true);
        when(fileTransferController.findUpload(any(), any())).thenReturn(this.getStubbedUpdateInProgressUpload(1, 1));
        
        // ACT
        MvcResult mvcResult = this.mockMvc.perform(put("/files/" + filename + "/multipart/" + id)
                .contentType(APPLICATION_VND_FIELDSMART_UPLOAD_PART_1_JSON)
                .accept(APPLICATION_VND_FIELDSMART_UPLOAD_PART_1_JSON)
                .content(jacksonObjectMapper.writeValueAsString(uploadPart)))
                .andExpect(status().isOk())
                .andReturn();

        // ASSERT
        String contentAsString = mvcResult.getResponse().getContentAsString();
        
        String contentType = mvcResult.getResponse().getContentType();
        
        UploadPartResponse response = jacksonObjectMapper.readValue(contentAsString, UploadPartResponse.class);
        assertTrue(response.isSuccess());
        assertTrue(response.isComplete());

        assertEquals(APPLICATION_VND_FIELDSMART_UPLOAD_PART_1_JSON,contentType);
    }
    
    
    @Test
    void uploadPart_shouldValidateUploadPartRequestAndRespondComplete_whenSinglePartUpload() throws Exception {
        // ARRANGE
        String filename = "test.xml";
        String id = "ID";
        UploadPart uploadPart = getUploadPart(BigInteger.ONE);

        when(fileTransferController.isExistingUpload(any(), any())).thenReturn(true);
        when(fileTransferController.findUpload(any(), any())).thenReturn(this.getStubbedUpdateInProgressUpload(1, 1));
        
        // ACT
        MvcResult mvcResult = this.mockMvc.perform(put("/files/" + filename + "/multipart/" + id)
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML)
                .content(xmlMapper.writeValueAsString(uploadPart)))
                .andExpect(status().isOk())
                .andReturn();
        
        // ASSERT
        String contentAsString = mvcResult.getResponse().getContentAsString();
       
        UploadPartResponse response = xmlMapper.readValue(contentAsString, UploadPartResponse.class);
        
        assertTrue(response.isSuccess());
        assertTrue(response.isComplete());

    }
    
    @Test
    void uploadPart_shouldValidateUploadPartRequestAndRespondNotComplete_whenMultiPartUpload() throws Exception {
        // ARRANGE
        String filename = "test.xml";
        String id = "ID";
        UploadPart uploadPart = getUploadPart(BigInteger.ONE);

        when(fileTransferController.isExistingUpload(any(), any())).thenReturn(true);
        when(fileTransferController.findUpload(any(), any())).thenReturn(this.getStubbedUpdateInProgressUpload(2, 1));
        
        // ACT
        MvcResult mvcResult = this.mockMvc.perform(put("/files/" + filename + "/multipart/" + id)
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML)
                .content(xmlMapper.writeValueAsString(uploadPart)))
                .andExpect(status().isOk())
                .andReturn();
        
        // ASSERT
        String contentAsString = mvcResult.getResponse().getContentAsString();
        UploadPartResponse response = xmlMapper.readValue(contentAsString, UploadPartResponse.class);
        assertTrue(response.isSuccess());
        assertFalse(response.isComplete());
        assertEquals(BigInteger.TWO, response.getNextPart());
        assertNull(response.getError().getErrorCode());

    }
    
    @Test
    void uploadPart_shouldValidateUploadPartRequestAndRespondComplete_whenMultiPartUploadComplete() throws Exception {
        // ARRANGE
        String filename = "test.xml";
        String id = "ID";
        UploadPart uploadPart = getUploadPart(BigInteger.TWO);

        Common.writeBytesToFile("TEST".getBytes(), "1_1.tmp", "/home/fusion/fieldsmart/temp/fileuploads/");

        when(fileTransferController.isExistingUpload(any(), any())).thenReturn(true);
        when(fileTransferController.findUpload(any(), any())).thenReturn(this.getStubbedUpdateInProgressUpload(2, 2));
        
        // ACT
        MvcResult mvcResult = this.mockMvc.perform(put("/files/" + filename + "/multipart/" + id)
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML)
                .content(xmlMapper.writeValueAsString(uploadPart)))
                .andExpect(status().isOk())
                .andReturn();
        
        // ASSERT
        String contentAsString = mvcResult.getResponse().getContentAsString();
        UploadPartResponse response = xmlMapper.readValue(contentAsString, UploadPartResponse.class);
        assertTrue(response.isSuccess());
        assertTrue(response.isComplete());

    }
    
    
    @Test
    void uploadPart_shouldBadRequest_whenMultiPartUploadMissingIdentifier() throws Exception {
        // ARRANGE
        String filename = "test.xml";
        String id = "ID";
        UploadPart uploadPart = getUploadPart(BigInteger.TWO);
        uploadPart.setIdentifier(null);

        when(fileTransferController.isExistingUpload(any(), any())).thenReturn(true);
        when(fileTransferController.findUpload(any(), any())).thenReturn(this.getStubbedUpdateInProgressUpload(2, 2));
        
        // ACT & ASSERT
        this.mockMvc.perform(put("/files/" + filename + "/multipart/" + id)
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML)
                .content(xmlMapper.writeValueAsString(uploadPart)))
                .andExpect(status().isBadRequest())
                .andReturn();

    }
    
    @Test
    void uploadPart_shouldBadRequest_whenMultiPartUploadMissingPartNo() throws Exception {
        // ARRANGE
        String filename = "test.xml";
        String id = "ID";
        UploadPart uploadPart = getUploadPart(BigInteger.TWO);
        uploadPart.setPartNumber(null);

        when(fileTransferController.isExistingUpload(any(), any())).thenReturn(true);
        when(fileTransferController.findUpload(any(), any())).thenReturn(this.getStubbedUpdateInProgressUpload(2, 2));
        
        // ACT & ASSERT
        this.mockMvc.perform(put("/files/" + filename + "/multipart/" + id)
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML)
                .content(xmlMapper.writeValueAsString(uploadPart)))
                .andExpect(status().isBadRequest())
                .andReturn();

    }
    
    @Test
    void uploadPart_shouldBadRequest_whenMultiPartUploadMissingChecksum() throws Exception {
        // ARRANGE
        String filename = "test.xml";
        String id = "ID";
        UploadPart uploadPart = getUploadPart(BigInteger.TWO);
        uploadPart.setChecksum(null);

        when(fileTransferController.isExistingUpload(any(), any())).thenReturn(true);
        when(fileTransferController.findUpload(any(), any())).thenReturn(this.getStubbedUpdateInProgressUpload(2, 2));
        
        // ACT & ASSERT
        this.mockMvc.perform(put("/files/" + filename + "/multipart/" + id)
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML)
                .content(xmlMapper.writeValueAsString(uploadPart)))
                .andExpect(status().isBadRequest())
                .andReturn();

    }
    
    @Test
    void uploadPart_shouldBadRequest_whenMultiPartUploadMissingData() throws Exception {
        // ARRANGE
        String filename = "test.xml";
        String id = "ID";
        UploadPart uploadPart = getUploadPart(BigInteger.TWO);
        uploadPart.setData(null);

        when(fileTransferController.isExistingUpload(any(), any())).thenReturn(true);
        when(fileTransferController.findUpload(any(), any())).thenReturn(this.getStubbedUpdateInProgressUpload(2, 2));
        
        // ACT & ASSERT
        this.mockMvc.perform(put("/files/" + filename + "/multipart/" + id)
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML)
                .content(xmlMapper.writeValueAsString(uploadPart)))
                .andExpect(status().isBadRequest())
                .andReturn();

    }
    
    @Test
    void uploadPart_shouldBadRequest_whenMultiPartUploadInvalidLength() throws Exception {
        // ARRANGE
        String filename = "test.xml";
        String id = "ID";
        UploadPart uploadPart = getUploadPart(BigInteger.TWO);
        uploadPart.setDataLength(0);

        when(fileTransferController.isExistingUpload(any(), any())).thenReturn(true);
        when(fileTransferController.findUpload(any(), any())).thenReturn(this.getStubbedUpdateInProgressUpload(2, 2));
        
        // ACT & ASSERT
        this.mockMvc.perform(put("/files/" + filename + "/multipart/" + id)
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML)
                .content(xmlMapper.writeValueAsString(uploadPart)))
                .andExpect(status().isBadRequest())
                .andReturn();

    }
    
    @Test
    void uploadPart_shouldReturnError_whenExistingMultiPartUploadNotFound() throws Exception {
        // ARRANGE
        String filename = "test.xml";
        String id = "ID";
        UploadPart uploadPart = getUploadPart(BigInteger.TWO);

        when(fileTransferController.isExistingUpload(any(), any())).thenReturn(true);
        when(fileTransferController.findUpload(any(), any())).thenReturn(null);
        
        // ACT
        MvcResult mvcResult = this.mockMvc.perform(put("/files/" + filename + "/multipart/" + id)
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML)
                .content(xmlMapper.writeValueAsString(uploadPart)))
                .andExpect(status().isOk())
                .andReturn();
        
        // ASSERT
        String contentAsString = mvcResult.getResponse().getContentAsString();
        UploadPartResponse response = xmlMapper.readValue(contentAsString, UploadPartResponse.class);
        assertFalse(response.isSuccess());
        assertEquals("InvalidUploadIdentifierException", response.getError().getErrorCode());

    }
    
    @Test
    void uploadPart_shouldReturnError_whenNextPartInvalid() throws Exception {
        // ARRANGE
        String filename = "test.xml";
        String id = "ID";
        UploadPart uploadPart = getUploadPart(BigInteger.ONE);

        when(fileTransferController.isExistingUpload(any(), any())).thenReturn(true);
        when(fileTransferController.findUpload(any(), any())).thenReturn(this.getStubbedUpdateInProgressUpload(2, 2));
        
        // ACT
        MvcResult mvcResult = this.mockMvc.perform(put("/files/" + filename + "/multipart/" + id)
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML)
                .content(xmlMapper.writeValueAsString(uploadPart)))
                .andExpect(status().isOk())
                .andReturn();
        
        // ASSERT
        String contentAsString = mvcResult.getResponse().getContentAsString();
        UploadPartResponse response = xmlMapper.readValue(contentAsString, UploadPartResponse.class);
        assertFalse(response.isSuccess());
        assertEquals("PartNumberSequenceException", response.getError().getErrorCode());

    }
    
    @Test
    void uploadPart_shouldReturnError_whenNextPartAlreadyUploaded() throws Exception {
        // ARRANGE
        String filename = "test.xml";
        String id = "ID";
        UploadPart uploadPart = getUploadPart(BigInteger.ONE);

        when(fileTransferController.isExistingUpload(any(), any())).thenReturn(true);
        when(fileTransferController.findUpload(any(), any())).thenReturn(this.getStubbedUpdateInProgressUpload(4, 2));
        
        // ACT
        MvcResult mvcResult = this.mockMvc.perform(put("/files/" + filename + "/multipart/" + id)
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML)
                .content(xmlMapper.writeValueAsString(uploadPart)))
                .andExpect(status().isOk())
                .andReturn();
        
        // ASSERT
        String contentAsString = mvcResult.getResponse().getContentAsString();
        UploadPartResponse response = xmlMapper.readValue(contentAsString, UploadPartResponse.class);
        assertFalse(response.isSuccess());
        assertEquals("PartNumberSequenceException", response.getError().getErrorCode());

    }
    
    @Test
    void uploadPart_shouldReturnError_whenMaxChunkExceeded() throws Exception {
        // ARRANGE
        String filename = "test.xml";
        String id = "ID";
        UploadPart uploadPart = getUploadPart(BigInteger.ONE);
        uploadPart.setDataLength(10240000);

        when(fileTransferController.isExistingUpload(any(), any())).thenReturn(true);
        when(fileTransferController.findUpload(any(), any())).thenReturn(this.getStubbedUpdateInProgressUpload(2, 1));

        
        // ACT
        MvcResult mvcResult = this.mockMvc.perform(put("/files/" + filename + "/multipart/" + id)
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML)
                .content(xmlMapper.writeValueAsString(uploadPart)))
                .andExpect(status().isOk())
                .andReturn();
        
        // ASSERT
        String contentAsString = mvcResult.getResponse().getContentAsString();
        UploadPartResponse response = xmlMapper.readValue(contentAsString, UploadPartResponse.class);
        assertFalse(response.isSuccess());
        assertEquals("MaximumChunkSizeExceededException", response.getError().getErrorCode());

    }
    
    
    @Test
    void uploadPart_shouldReturnError_whenFileSizeExceeded() throws Exception {
        // ARRANGE
        String filename = "test.xml";
        String id = "ID";
        UploadPart uploadPart = getUploadPart(BigInteger.TWO);
        uploadPart.setDataLength(1024000);

        when(fileTransferController.isExistingUpload(any(), any())).thenReturn(true);
        when(fileTransferController.findUpload(any(), any())).thenReturn(this.getStubbedUpdateInProgressUpload(2, 2));

        fileUploadController.setMaxUploadSize("1024000");
        
        // ACT
        MvcResult mvcResult = this.mockMvc.perform(put("/files/" + filename + "/multipart/" + id)
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML)
                .content(xmlMapper.writeValueAsString(uploadPart)))
                .andExpect(status().isOk())
                .andReturn();
        
        // ASSERT
        String contentAsString = mvcResult.getResponse().getContentAsString();
        UploadPartResponse response = xmlMapper.readValue(contentAsString, UploadPartResponse.class);
        assertFalse(response.isSuccess());
        assertEquals("MaximumFileSizeExceededException", response.getError().getErrorCode());

    }
    
    @Test
    void uploadPart_shouldReturnError_whenChecksumMissmatch() throws Exception {
        // ARRANGE
        String filename = "test.xml";
        String id = "ID";
        UploadPart uploadPart = getUploadPart(BigInteger.TWO);
        uploadPart.setChecksum("INVALIDCHECKSUM");

        when(fileTransferController.isExistingUpload(any(), any())).thenReturn(true);
        when(fileTransferController.findUpload(any(), any())).thenReturn(this.getStubbedUpdateInProgressUpload(2, 2));

        // ACT
        MvcResult mvcResult = this.mockMvc.perform(put("/files/" + filename + "/multipart/" + id)
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML)
                .content(xmlMapper.writeValueAsString(uploadPart)))
                .andExpect(status().isOk())
                .andReturn();
        
        // ASSERT
        String contentAsString = mvcResult.getResponse().getContentAsString();
        UploadPartResponse response = xmlMapper.readValue(contentAsString, UploadPartResponse.class);
        assertFalse(response.isSuccess());
        assertEquals("ChecksumMismatchException", response.getError().getErrorCode());

    }
    
    @Test
    void uploadPart_shouldReturnError_whenErrorUpadatingUploadInDB() throws Exception {
        // ARRANGE
        String filename = "test.xml";
        String id = "ID";
        UploadPart uploadPart = getUploadPart(BigInteger.TWO);

        when(fileTransferController.isExistingUpload(any(), any())).thenReturn(true);
        when(fileTransferController.findUpload(any(), any())).thenReturn(this.getStubbedUpdateInProgressUpload(2, 2));
        doThrow(new FRInstanceException()).when(fileTransferController).updateUpload(any(), any());
        
        
        // ACT
        MvcResult mvcResult = this.mockMvc.perform(put("/files/" + filename + "/multipart/" + id)
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML)
                .content(xmlMapper.writeValueAsString(uploadPart)))
                .andExpect(status().isOk())
                .andReturn();
        
        // ASSERT
        String contentAsString = mvcResult.getResponse().getContentAsString();
        UploadPartResponse response = xmlMapper.readValue(contentAsString, UploadPartResponse.class);
        assertFalse(response.isSuccess());
        assertEquals("FRInstanceException", response.getError().getErrorCode());

    }
    
    @Test
    void uploadPart_shouldReturnError_whenErrorCompletingUploadInDB() throws Exception {
        // ARRANGE
        String filename = "test.xml";
        String id = "ID";
        UploadPart uploadPart = getUploadPart(BigInteger.TWO);

        when(fileTransferController.isExistingUpload(any(), any())).thenReturn(true);
        when(fileTransferController.findUpload(any(), any())).thenReturn(this.getStubbedUpdateInProgressUpload(2, 2));
        doThrow(new FRInstanceException()).when(fileTransferController).cancelUpload(any(), any());
        
        
        // ACT
        MvcResult mvcResult = this.mockMvc.perform(put("/files/" + filename + "/multipart/" + id)
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML)
                .content(xmlMapper.writeValueAsString(uploadPart)))
                .andExpect(status().isOk())
                .andReturn();
        
        // ASSERT
        String contentAsString = mvcResult.getResponse().getContentAsString();
        UploadPartResponse response = xmlMapper.readValue(contentAsString, UploadPartResponse.class);
        assertFalse(response.isSuccess());
        assertEquals("FRInstanceException", response.getError().getErrorCode());

    }
    
    /*
     * 
     * Stubbing Functions
     * 
     * 
     */
    
    
    private InProgressUploads getStubbedUpdateInProgressUpload(Integer expectedParts, Integer nextPart ) {
    	
    	InProgressUploads inProgressUpload = new InProgressUploads();
    	inProgressUpload.setExpectedParts(expectedParts);
    	inProgressUpload.setFileName("SPR.UNITTEST.XML");
    	inProgressUpload.setId("ID");
    	inProgressUpload.setNextPart(nextPart);
    	inProgressUpload.setTotalSize(Long.valueOf(10000));
    	inProgressUpload.setUploadStarted(1);
    	
    	inProgressUpload.setUploadParts(new HashSet<UploadParts>());
    	
    	for(int i=1; i<nextPart; i++) {
    		UploadParts part = new UploadParts();
    		part.setFileName("1_"+nextPart+".tmp");
    		part.setChecksum("TEST");
    		part.setFileSize(Long.valueOf(1024000));
    		part.setId(new UploadPartsId("ID",nextPart));
    		
    		inProgressUpload.getUploadParts().add(part);
    	}

        return inProgressUpload;
    }
    
    private UploadPart getUploadPart(BigInteger partNumber) {
        UploadPart uploadPart = new UploadPart();
        uploadPart.setChecksum("DD9F55FA4CEDB0083D547941970C26AD");
        uploadPart.setIdentifier("1");
        uploadPart.setData("MS1hYmM=");
        uploadPart.setPartNumber(partNumber);
        uploadPart.setDataLength(1024);
        return uploadPart;
    }
    

    private UploadInitiate getStubbedUpdateInitiate(String filename, String id, int totalSizeBytes) {
        UploadInitiate uploadInitiate = new UploadInitiate();
        uploadInitiate.setFileName(filename);
        uploadInitiate.setIdentifier(id);
        uploadInitiate.setFileDesc("Description");
        uploadInitiate.setTotalSizeBytes(totalSizeBytes);
        return uploadInitiate;
    }
}
