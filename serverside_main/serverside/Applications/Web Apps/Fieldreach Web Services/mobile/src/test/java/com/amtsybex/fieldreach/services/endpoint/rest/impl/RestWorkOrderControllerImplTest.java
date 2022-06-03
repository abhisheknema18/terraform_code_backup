package com.amtsybex.fieldreach.services.endpoint.rest.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigInteger;
import java.util.List;

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

import com.amtsybex.fieldreach.backend.model.HPCUsers;
import com.amtsybex.fieldreach.backend.model.WorkIssued;
import com.amtsybex.fieldreach.services.download.FileDownloadController;
import com.amtsybex.fieldreach.services.endpoint.common.WorkOrderController;
import com.amtsybex.fieldreach.services.endpoint.rest.impl.utils.HPCUsersUtil;
import com.amtsybex.fieldreach.services.messages.response.DownloadPartResponse;
import com.amtsybex.fieldreach.services.messages.response.InitiateDownloadResponse;
import com.amtsybex.fieldreach.services.messages.response.RetrieveWorkOrderResponse;
import com.amtsybex.fieldreach.services.messages.response.WorkIssuedResponse;
import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class RestWorkOrderControllerImplTest {

    private MockMvc mockMvc;

    private final XmlMapper xmlMapper = new XmlMapper();

    private final ObjectMapper jsonMapper = new ObjectMapper();

    @Mock
    private WorkOrderController workOrderController;

    @Mock
    private FileDownloadController fileDownloadController;

    @Autowired
    private MappingJackson2HttpMessageConverter jackson2HttpMessageConverter;

    @Autowired
    private MappingJackson2XmlHttpMessageConverter jackson2XmlHttpMessageConverter;

    @Spy
    @InjectMocks
    @Autowired
    private RestWorkOrderControllerImpl restWorkOrderController;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(restWorkOrderController)
                .setMessageConverters(jackson2HttpMessageConverter, jackson2XmlHttpMessageConverter)
                .build();
    }

	@Test
	void workIssued_shouldGetWorkOrders_whenAcceptHeadersInXml() throws Exception {
	    // Arrange
	    HPCUsers user = HPCUsersUtil.getUserDetails();
	    PodamFactory factory = new PodamFactoryImpl();
	    WorkIssued workIssued = factory.manufacturePojo(WorkIssued.class);
	    when(workOrderController.getWorkOrdersByUserCode(null, user.getId().getUserCode(),false)).thenReturn(List.of(workIssued));
	    doReturn(user).when(restWorkOrderController).getUserDetailsFromUserPrincipal(any());
	
	    // Act
	    MvcResult mvcResult = this.mockMvc.perform(get("/workorder/issued")
	            .contentType(MediaType.APPLICATION_XML)
	            .accept(MediaType.APPLICATION_XML))
	            .andExpect(status().isOk())
	            .andReturn();
	    
	    // Assert
	    WorkIssuedResponse response = xmlMapper.readValue(mvcResult.getResponse().getContentAsString(), WorkIssuedResponse.class);
	    assertTrue(response.isSuccess());
	    com.amtsybex.fieldreach.services.messages.types.WorkIssued issuedResponse = response.getWorkIssuedList().get(0);
	    assertEquals(workIssued.getAdditionalText(), issuedResponse.getAdditionalText());
	    assertEquals(workIssued.getAltEquipRef(), issuedResponse.getAltEquipRef());
	    assertEquals(workIssued.getEquipNo(), issuedResponse.getEquipNo());
	    assertEquals(workIssued.getEquipDesc(), issuedResponse.getEquipDesc());
	    assertEquals(workIssued.getDistrictCode(), issuedResponse.getDistrictCode());
	    assertEquals(workIssued.getIssuedTime().toString(), issuedResponse.getIssuedTime());
	    assertEquals(workIssued.getIssuedDate(), issuedResponse.getIssuedDate());
	    assertEquals(workIssued.getSourceFileName(), issuedResponse.getSourceFileName());
	    assertEquals(workIssued.getUserCode(), issuedResponse.getUserCode());
	    assertEquals(workIssued.getWorkgroupCode(), issuedResponse.getWorkgroupCode());
	    assertEquals(workIssued.getWorkOrderNo(), issuedResponse.getWorkOrderNo());
	    assertEquals(workIssued.getWorkOrderDesc(), issuedResponse.getWorkOrderDesc());
	    assertEquals(workIssued.getWorkStatus(), issuedResponse.getWorkStatus());
	    assertEquals(workIssued.getWorkStatusTime().toString(), issuedResponse.getWorkStatusTime());
	    assertEquals(workIssued.getWorkStatusDate(), issuedResponse.getWorkStatusDate());
	    assertEquals(workIssued.getWoType(), issuedResponse.getWoType());
	}
	
	@Test
	void workIssued_shouldGetWorkOrders_whenAcceptHeadersInJson() throws Exception {
	    // Arrange
	    HPCUsers user = HPCUsersUtil.getUserDetails();
	    PodamFactory factory = new PodamFactoryImpl();
	    WorkIssued workIssued = factory.manufacturePojo(WorkIssued.class);
	    when(workOrderController.getWorkOrdersByUserCode(null, user.getId().getUserCode(), false)).thenReturn(List.of(workIssued));
	    doReturn(user).when(restWorkOrderController).getUserDetailsFromUserPrincipal(any());
	
	    // Act
	    MvcResult mvcResult = this.mockMvc.perform(get("/workorder/issued")
	            .contentType(MediaType.APPLICATION_JSON)
	            .accept(MediaType.APPLICATION_JSON))
	            .andExpect(status().isOk())
	            .andReturn();
	
	    // Assert
	    WorkIssuedResponse response = jsonMapper.readValue(mvcResult.getResponse().getContentAsString(), WorkIssuedResponse.class);
	    assertTrue(response.isSuccess());
	    com.amtsybex.fieldreach.services.messages.types.WorkIssued issuedResponse = response.getWorkIssuedList().get(0);
	    assertEquals(workIssued.getAdditionalText(), issuedResponse.getAdditionalText());
	    assertEquals(workIssued.getAltEquipRef(), issuedResponse.getAltEquipRef());
	    assertEquals(workIssued.getEquipNo(), issuedResponse.getEquipNo());
	    assertEquals(workIssued.getEquipDesc(), issuedResponse.getEquipDesc());
	    assertEquals(workIssued.getDistrictCode(), issuedResponse.getDistrictCode());
	    assertEquals(workIssued.getIssuedTime().toString(), issuedResponse.getIssuedTime());
	    assertEquals(workIssued.getIssuedDate(), issuedResponse.getIssuedDate());
	    assertEquals(workIssued.getSourceFileName(), issuedResponse.getSourceFileName());
	    assertEquals(workIssued.getUserCode(), issuedResponse.getUserCode());
	    assertEquals(workIssued.getWorkgroupCode(), issuedResponse.getWorkgroupCode());
	    assertEquals(workIssued.getWorkOrderNo(), issuedResponse.getWorkOrderNo());
	    assertEquals(workIssued.getWorkOrderDesc(), issuedResponse.getWorkOrderDesc());
	    assertEquals(workIssued.getWorkStatus(), issuedResponse.getWorkStatus());
	    assertEquals(workIssued.getWorkStatusTime().toString(), issuedResponse.getWorkStatusTime());
	    assertEquals(workIssued.getWorkStatusDate(), issuedResponse.getWorkStatusDate());
	    assertEquals(workIssued.getWoType(), issuedResponse.getWoType());
	}
	
	@Test
	void workIssued_shouldUseWorkGroupCodeFromUserDetails_toGetWorkOrders() throws Exception {
	    // Arrange
	    HPCUsers user = HPCUsersUtil.getUserDetails();
	    PodamFactory factory = new PodamFactoryImpl();
	    WorkIssued workIssued = factory.manufacturePojo(WorkIssued.class);
	    when(workOrderController.getWorkOrdersByWorkGroupCode(null, user.getId().getWorkgroupCode(), false)).thenReturn(List.of(workIssued));
	    doReturn(user).when(restWorkOrderController).getUserDetailsFromUserPrincipal(any());
	
	    // Act
	    MvcResult mvcResult = this.mockMvc.perform(get("/workorder/issued?workAllocationMode=W")
	            .contentType(MediaType.APPLICATION_JSON)
	            .accept(MediaType.APPLICATION_JSON))
	            .andExpect(status().isOk())
	            .andReturn();
	
	    // Assert
	    WorkIssuedResponse response = jsonMapper.readValue(mvcResult.getResponse().getContentAsString(), WorkIssuedResponse.class);
	    assertTrue(response.isSuccess());
	    com.amtsybex.fieldreach.services.messages.types.WorkIssued issuedResponse = response.getWorkIssuedList().get(0);
	    assertEquals(workIssued.getAdditionalText(), issuedResponse.getAdditionalText());
	    assertEquals(workIssued.getAltEquipRef(), issuedResponse.getAltEquipRef());
	    assertEquals(workIssued.getEquipNo(), issuedResponse.getEquipNo());
	    assertEquals(workIssued.getEquipDesc(), issuedResponse.getEquipDesc());
	    assertEquals(workIssued.getDistrictCode(), issuedResponse.getDistrictCode());
	    assertEquals(workIssued.getIssuedTime().toString(), issuedResponse.getIssuedTime());
	    assertEquals(workIssued.getIssuedDate(), issuedResponse.getIssuedDate());
	    assertEquals(workIssued.getSourceFileName(), issuedResponse.getSourceFileName());
	    assertEquals(workIssued.getUserCode(), issuedResponse.getUserCode());
	    assertEquals(workIssued.getWorkgroupCode(), issuedResponse.getWorkgroupCode());
	    assertEquals(workIssued.getWorkOrderNo(), issuedResponse.getWorkOrderNo());
	    assertEquals(workIssued.getWorkOrderDesc(), issuedResponse.getWorkOrderDesc());
	    assertEquals(workIssued.getWorkStatus(), issuedResponse.getWorkStatus());
	    assertEquals(workIssued.getWorkStatusTime().toString(), issuedResponse.getWorkStatusTime());
	    assertEquals(workIssued.getWorkStatusDate(), issuedResponse.getWorkStatusDate());
	    assertEquals(workIssued.getWoType(), issuedResponse.getWoType());
	}
	
	@Test
	void workIssued_shouldReturnBadRequestOnIllegalParameter() throws Exception {
	         
	    // Act
	    MvcResult mvcResult = this.mockMvc.perform(get("/workorder/issued?workAllocationMode=w")
	            .contentType(MediaType.APPLICATION_JSON)
	            .accept(MediaType.APPLICATION_JSON))
	            .andExpect(status().isBadRequest())
	            .andReturn();
	
	    // Assert
	    assertNotNull(mvcResult.getResponse());
	    assertNotNull(mvcResult.getResolvedException().getMessage());
	}
	
	
	@Test
	void workIssued_shouldReturnSuccessIfUserDontHaveCodeAssigned() throws Exception {
	    // Arrange
	    HPCUsers user = HPCUsersUtil.getUserDetails();
	    user.getId().setUserCode("");
	    doReturn(user).when(restWorkOrderController).getUserDetailsFromUserPrincipal(any());
	
	    // Act
	    MvcResult mvcResult = this.mockMvc.perform(get("/workorder/issued?workAllocationMode=W")
	            .contentType(MediaType.APPLICATION_JSON)
	            .accept(MediaType.APPLICATION_JSON))
	            .andExpect(status().isOk())
	            .andReturn();
	}
	
	@Test
	void workIssued_shouldReturnSuccessIfUserDontHaveWorkGroupCodeAssigned() throws Exception {
	    // Arrange
	    HPCUsers user = HPCUsersUtil.getUserDetails();
	    user.getId().setWorkgroupCode("");
	    doReturn(user).when(restWorkOrderController).getUserDetailsFromUserPrincipal(any());
	
	    // Act
	    MvcResult mvcResult = this.mockMvc.perform(get("/workorder/issued?workAllocationMode=W")
	            .contentType(MediaType.APPLICATION_JSON)
	            .accept(MediaType.APPLICATION_JSON))
	            .andExpect(status().isOk())
	            .andReturn();
	}
	
	
	@Test
	void retrieveWorkOrderSource_shouldWorkOrderDetailsAndRespond_whenAcceptHeadersInXml() throws Exception {
	    // Arrange
	    when(workOrderController.getWorkIssuedRecord(any(), anyString(), anyString())).thenReturn(new WorkIssued());
	    when(workOrderController.checkStatusIsValidForRetrieval(any(), any(), anyBoolean())).thenReturn(true);
	    when(workOrderController.getWorkOrderSourceContents(any(), any(), any())).thenReturn("Sample Contents");
	    when(workOrderController.getWorkorderFileName(any())).thenReturn("Sample.xml");
	
	    // Act
	    MvcResult mvcResult = this.mockMvc.perform(get("/workorder/1")
	            .contentType(MediaType.APPLICATION_XML)
	            .accept(MediaType.APPLICATION_XML))
	            .andExpect(status().isOk())
	            .andReturn();
	    
	    // Assert
	    String contentAsString = mvcResult.getResponse().getContentAsString();
	    assertEquals("<RetrieveWorkOrderResponse><success>true</success><error/><workOrderData>Sample Contents</workOrderData><checksum>3FEEF3A60BCA2A6BEFA81EF2A186A0BB</checksum><filename>Sample.xml</filename></RetrieveWorkOrderResponse>", contentAsString);
	    RetrieveWorkOrderResponse response = xmlMapper.readValue(contentAsString, RetrieveWorkOrderResponse.class);
	    assertTrue(response.isSuccess());
	    assertEquals("3FEEF3A60BCA2A6BEFA81EF2A186A0BB", response.getChecksum());
	    assertEquals("Sample.xml", response.getFilename());
	    assertEquals("Sample Contents", response.getWorkOrderData());
	}
	
	@Test
	void retrieveWorkOrderSource_shouldWorkOrderDetailsAndRespond_whenAcceptHeadersInJson() throws Exception {
	    // Arrange
	    when(workOrderController.getWorkIssuedRecord(any(), anyString(), anyString())).thenReturn(new WorkIssued());
	    when(workOrderController.checkStatusIsValidForRetrieval(any(), any(), anyBoolean())).thenReturn(true);
	    when(workOrderController.getWorkOrderSourceContents(any(), any(), any())).thenReturn("Sample Contents");
	    when(workOrderController.getWorkorderFileName(any())).thenReturn("Sample.xml");
	
	    // Act
	    MvcResult mvcResult = this.mockMvc.perform(get("/workorder/1")
	            .contentType(MediaType.APPLICATION_JSON)
	            .accept(MediaType.APPLICATION_JSON))
	            .andExpect(status().isOk())
	            .andReturn();
	
	    // Assert
	    String contentAsString = mvcResult.getResponse().getContentAsString();
	    RetrieveWorkOrderResponse response = jsonMapper.readValue(contentAsString, RetrieveWorkOrderResponse.class);
	    assertTrue(response.isSuccess());
	    assertEquals("3FEEF3A60BCA2A6BEFA81EF2A186A0BB", response.getChecksum());
	    assertEquals("Sample.xml", response.getFilename());
	    assertEquals("Sample Contents", response.getWorkOrderData());
	}
	
	@Test
	void initiateWorkOrderAttachmentDownload_shouldInitiateDownloadAndRespond_whenAcceptHeaderInXml() throws Exception {
	    // Arrange
	    String workOrderNo = "1";
	    String filename = "test.xml";
	    when(workOrderController.getWorkIssuedRecord(null, workOrderNo, "AA")).thenReturn(new WorkIssued());
	    when(workOrderController.getAttachmentDirURI(any(), any())).thenReturn("/temp");
	    InitiateDownloadResponse stubbedInitiateDownloadResponse = getStubbedInitiateDownloadResponse(filename);
	    when(fileDownloadController.initiateFileSystemDownload(any(), anyString())).thenReturn(stubbedInitiateDownloadResponse);
	
	    // Act
	    MvcResult mvcResult = this.mockMvc.perform(get("/workorder/" + workOrderNo + "/attachment/" + filename + "?districtCode=AA")
	            .contentType(MediaType.APPLICATION_XML)
	            .accept(MediaType.APPLICATION_XML))
	            .andExpect(status().isOk())
	            .andReturn();
	    
	    // Assert
	    InitiateDownloadResponse response = xmlMapper.readValue(mvcResult.getResponse().getContentAsString(), InitiateDownloadResponse.class);
	    assertEquals(stubbedInitiateDownloadResponse.getFileName(), response.getFileName());
	    assertEquals(stubbedInitiateDownloadResponse.getIdentifier(), response.getIdentifier());
	    assertEquals(stubbedInitiateDownloadResponse.getChecksum(), response.getChecksum());
	    assertEquals(stubbedInitiateDownloadResponse.getTotalParts(), response.getTotalParts());
	    assertTrue(response.isSuccess());
	    assertNull(response.getError());
	}
	
	@Test
	void initiateWorkOrderAttachmentDownload_shouldInitiateDownloadAndRespond_whenAcceptHeaderInJson() throws Exception {
	    // Arrange
	    String workOrderNo = "1";
	    String filename = "test.xml";
	    when(workOrderController.getWorkIssuedRecord(null, workOrderNo, "AA")).thenReturn(new WorkIssued());
	    when(workOrderController.getAttachmentDirURI(any(), any())).thenReturn("/temp");
	    InitiateDownloadResponse stubbedInitiateDownloadResponse = getStubbedInitiateDownloadResponse(filename);
	    when(fileDownloadController.initiateFileSystemDownload(any(), anyString())).thenReturn(stubbedInitiateDownloadResponse);
	
	    // Act
	    MvcResult mvcResult = this.mockMvc.perform(get("/workorder/" + workOrderNo + "/attachment/" + filename + "?districtCode=AA")
	            .contentType(MediaType.APPLICATION_JSON)
	            .accept(MediaType.APPLICATION_JSON))
	            .andExpect(status().isOk())
	            .andReturn();
	
	    // Assert
	    InitiateDownloadResponse response = jsonMapper.readValue(mvcResult.getResponse().getContentAsString(), InitiateDownloadResponse.class);
	    assertEquals(stubbedInitiateDownloadResponse.getFileName(), response.getFileName());
	    assertEquals(stubbedInitiateDownloadResponse.getIdentifier(), response.getIdentifier());
	    assertEquals(stubbedInitiateDownloadResponse.getChecksum(), response.getChecksum());
	    assertEquals(stubbedInitiateDownloadResponse.getTotalParts(), response.getTotalParts());
	    assertTrue(response.isSuccess());
	    assertNotNull(response.getError());
	}
	
	@Test
	void downloadWorkOrderAttachmentPart_shouldDownloadPartAndReturnResponse_whenRequestAcceptHeaderInXml() throws Exception {
	    // Arrange
	    String workOrderNo = "1";
	    String filename = "test.xml";
	    String identifier = "1";
	    int part = 1;
	    DownloadPartResponse stubbedDownloadPartResponse = getStubbedDownloadPartResponse(identifier, part);
	    Mockito.when(fileDownloadController.downloadPart(null, identifier, part)).thenReturn(stubbedDownloadPartResponse);
	    
	    // Act
	    MvcResult mvcResult = this.mockMvc.perform(get("/workorder/" + workOrderNo + "/attachment/" + filename + "/multipart/"+ identifier + "/" + part)
	            .contentType(MediaType.APPLICATION_XML)
	            .accept(MediaType.APPLICATION_XML))
	            .andExpect(status().isOk())
	            .andReturn();
	    
	    // Assert
	    DownloadPartResponse response = xmlMapper.readValue(mvcResult.getResponse().getContentAsString(), DownloadPartResponse.class);
	    assertEquals(stubbedDownloadPartResponse.getIdentifier(), response.getIdentifier());
	    assertEquals(stubbedDownloadPartResponse.getPartData(), response.getPartData());
	    assertTrue(response.isSuccess());
	    assertNull(response.getError());
	}

    @Test
    void downloadWorkOrderAttachmentPart_shouldDownloadPartAndReturnResponse_whenRequestAcceptHeaderInJson() throws Exception {
        // Arrange
        String workOrderNo = "1";
        String filename = "test.xml";
        String identifier = "1";
        int part = 1;
        DownloadPartResponse stubbedDownloadPartResponse = getStubbedDownloadPartResponse(identifier, part);
        Mockito.when(fileDownloadController.downloadPart(null, identifier, part)).thenReturn(stubbedDownloadPartResponse);

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/workorder/" + workOrderNo + "/attachment/" + filename + "/multipart/"+ identifier + "/" + part)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        DownloadPartResponse response = jsonMapper.readValue(mvcResult.getResponse().getContentAsString(), DownloadPartResponse.class);
        assertEquals(stubbedDownloadPartResponse.getIdentifier(), response.getIdentifier());
        assertEquals(stubbedDownloadPartResponse.getPartData(), response.getPartData());
        assertTrue(response.isSuccess());
        assertNotNull(response.getError());
    }
    
    @Test
    void downloadWorkOrderAttachmentPart_shouldThrowInvalidIdExceptionForInvalidHeaders_whenRequestAcceptHeaderInXml() throws Exception {
    	// Arrange
    	String workOrderNo = "1";
    	String filename = "test.xml";
    	String identifier = " ";
    	int part = 1 ;
    	String errorMesage = "InvalidDownloadIdentifierException";
    	DownloadPartResponse stubbedDownloadPartResponse = getStubbedErrorDownloadPartResponse(errorMesage);
    	Mockito.when(fileDownloadController.downloadPart(null, identifier, part)).thenReturn(stubbedDownloadPartResponse);

    	// Act
    	MvcResult mvcResult = this.mockMvc.perform(get("/workorder/" + workOrderNo + "/attachment/" + filename + "/multipart/"+ identifier + "/" + part)
    			.contentType(MediaType.APPLICATION_XML)
    			.accept(MediaType.APPLICATION_XML))
    			.andExpect(status().isOk())
    			.andReturn();

    	// Assert
    	DownloadPartResponse response = xmlMapper.readValue(mvcResult.getResponse().getContentAsString(), DownloadPartResponse.class);
    	assertFalse(response.isSuccess());
    	assertNotNull(response.getError());
    	assertEquals(stubbedDownloadPartResponse.getError().getErrorCode(), "InvalidDownloadIdentifierException");
    }
    
    @Test
    void downloadWorkOrderAttachmentPart_shouldThrowPartNumberExceptionForInvalidHeaders_whenRequestAcceptHeaderInXml() throws Exception {
    	// Arrange
    	String workOrderNo = "1";
    	String filename = "test.xml";
    	String identifier = "1";
    	int part = 0 ;
    	String errorMesage = "PartNumberException";
    	DownloadPartResponse stubbedDownloadPartResponse = getStubbedErrorDownloadPartResponse(errorMesage);
    	Mockito.when(fileDownloadController.downloadPart(null, identifier, part)).thenReturn(stubbedDownloadPartResponse);

    	// Act
    	MvcResult mvcResult = this.mockMvc.perform(get("/workorder/" + workOrderNo + "/attachment/" + filename + "/multipart/"+ identifier + "/" + part)
    			.contentType(MediaType.APPLICATION_XML)
    			.accept(MediaType.APPLICATION_XML))
    			.andExpect(status().isOk())
    			.andReturn();

    	// Assert
    	DownloadPartResponse response = xmlMapper.readValue(mvcResult.getResponse().getContentAsString(), DownloadPartResponse.class);
    	assertFalse(response.isSuccess());
    	assertNotNull(response.getError());
    	assertEquals(stubbedDownloadPartResponse.getError().getErrorCode(), "PartNumberException");
    }
    
    @Test
    void downloadWorkOrderAttachmentPart_shouldThrowFileNotFoundForInvalidHeaders_whenRequestAcceptHeaderInXml() throws Exception {
    	// Arrange
    	String workOrderNo = "1";
    	String filename = "invalidFile.xml";
    	String identifier = "1";
    	int part = 1;
    	String errorMesage = "FileNotFoundException";
    	DownloadPartResponse stubbedDownloadPartResponse = getStubbedErrorDownloadPartResponse(errorMesage);
    	Mockito.when(fileDownloadController.downloadPart(null, identifier, part)).thenReturn(stubbedDownloadPartResponse);

    	// Act
    	MvcResult mvcResult = this.mockMvc.perform(get("/workorder/" + workOrderNo + "/attachment/" + filename + "/multipart/"+ identifier + "/" + part)
    			.contentType(MediaType.APPLICATION_XML)
    			.accept(MediaType.APPLICATION_XML))
    			.andExpect(status().isOk())
    			.andReturn();

    	// Assert
    	DownloadPartResponse response = xmlMapper.readValue(mvcResult.getResponse().getContentAsString(), DownloadPartResponse.class);
    	assertFalse(response.isSuccess());
    	assertNotNull(response.getError());
    	assertEquals(stubbedDownloadPartResponse.getError().getErrorCode(), "FileNotFoundException");
    }

    private InitiateDownloadResponse getStubbedInitiateDownloadResponse(String fileName) {
        InitiateDownloadResponse initiateDownloadResponse = new InitiateDownloadResponse();
        initiateDownloadResponse.setFileName(fileName);
        initiateDownloadResponse.setIdentifier("I");
        initiateDownloadResponse.setChecksum("checksum");
        initiateDownloadResponse.setTotalParts(BigInteger.ONE);
        initiateDownloadResponse.setSuccess(true);
        initiateDownloadResponse.setError(new ErrorMessage());
        return initiateDownloadResponse;
    }
    
    private DownloadPartResponse getStubbedDownloadPartResponse(String identifier, int part) {
        DownloadPartResponse downloadPartResponse = new DownloadPartResponse();
        downloadPartResponse.setIdentifier(identifier);
        downloadPartResponse.setPartData(String.valueOf(part));
        downloadPartResponse.setSuccess(true);
        downloadPartResponse.setError(new ErrorMessage());
        return downloadPartResponse;
    }
    
    private DownloadPartResponse getStubbedErrorDownloadPartResponse(String message) {
        DownloadPartResponse downloadPartResponse = new DownloadPartResponse();
        
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setErrorCode(message);
        
        downloadPartResponse.setSuccess(false);
        downloadPartResponse.setError(errorMessage);
        return downloadPartResponse;
    }
    
}