package com.amtsybex.fieldreach.services.endpoint.rest.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.amtsybex.fieldreach.services.messages.response.DownloadPartResponse.APPLICATION_VND_FIELDSMART_DOWNLOAD_PART_1_XML;
import static com.amtsybex.fieldreach.services.messages.response.DownloadPartResponse.APPLICATION_VND_FIELDSMART_DOWNLOAD_PART_1_JSON;
import static com.amtsybex.fieldreach.services.messages.request.WorkOrderStatusRequest.APPLICATION_VND_FIELDSMART_WORKORDERSTATUS_1_JSON;
import static com.amtsybex.fieldreach.services.messages.request.WorkOrderStatusRequest.APPLICATION_VND_FIELDSMART_WORKORDERSTATUS_1_XML;
import static com.amtsybex.fieldreach.services.messages.request.RegisterAttachment.APPLICATION_VND_FIELDSMART_ATTACHMENT_1_XML;
import static com.amtsybex.fieldreach.services.messages.request.RegisterAttachment.APPLICATION_VND_FIELDSMART_ATTACHMENT_1_JSON;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.amtsybex.fieldreach.BaseTest;
import com.amtsybex.fieldreach.backend.model.WorkIssued;
import com.amtsybex.fieldreach.backend.model.pk.WorkIssuedId;
import com.amtsybex.fieldreach.backend.service.WorkOrderService;
import com.amtsybex.fieldreach.services.download.FileDownloadController;
import com.amtsybex.fieldreach.services.endpoint.common.WorkOrderController;
import com.amtsybex.fieldreach.services.endpoint.common.WorkStatus;
import com.amtsybex.fieldreach.services.messages.request.WorkOrderStatusRequest;
import com.amtsybex.fieldreach.services.messages.response.CallResponse;
import com.amtsybex.fieldreach.services.messages.response.DownloadPartResponse;
import com.amtsybex.fieldreach.services.messages.response.InitiateDownloadResponse;
import com.amtsybex.fieldreach.services.messages.response.RetrieveWorkOrderResponse;
import com.amtsybex.fieldreach.services.messages.response.WorkIssuedResponse;
import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.amtsybex.fieldreach.services.utils.xml.XMLUtils;
import com.amtsybex.fieldreach.utils.impl.Common;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class RestWorkOrderControllerImplTest extends BaseTest{

	private MockMvc mockMvc;

	private final XmlMapper xmlMapper = new XmlMapper();

	private final ObjectMapper jsonMapper = new ObjectMapper();

	@Mock//(answer = Answers.RETURNS_DEEP_STUBS)
	private WorkOrderController workOrderController;

	@Mock
	private FileDownloadController fileDownloadController;

	@Mock
	private WorkOrderService workOrderService;
	
	@Mock
	private WorkStatus workStatuses;

	@Autowired
	private MappingJackson2HttpMessageConverter jackson2HttpMessageConverter;

	@Autowired
	private MappingJackson2XmlHttpMessageConverter jackson2XmlHttpMessageConverter;

	@Autowired
	@InjectMocks
	private RestWorkOrderControllerImpl restIntegrationWorkOrderController;


	@BeforeEach
	public void setup() {

		super.setUp();

		this.mockMvc = MockMvcBuilders
				.webAppContextSetup(context)
				.apply(springSecurity())
				.build();
	}
	/*
    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(restIntegrationWorkOrderController)
                .setMessageConverters(jackson2HttpMessageConverter, jackson2XmlHttpMessageConverter)
                .apply(springSecurity())
                .build();
    }*/

	@Test
	void workIssuedIWS_shouldGetWorkOrderList_whenAcceptHeaderInXml() throws Exception {
		// Arrange
		String token = this.arrangeSuccessfulAuthentication();

		PodamFactory factory = new PodamFactoryImpl();
		WorkIssued workIssued = factory.manufacturePojo(WorkIssued.class);
		//doReturn(new SystemUsers()).when(restIntegrationWorkOrderController).getSystemUserFromUserPrincipal();

		when(workOrderController.getWorkOrdersByUserCode(null, "TEST", true)).thenReturn(List.of(workIssued));

		// Act
		MvcResult mvcResult = this.mockMvc.perform(get("/workorder/list?userCode=TEST")
				.header("Authorization", "Bearer " + token)
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
	void workIssuedIWS_shouldGetWorkOrderList_whenAcceptHeaderInJson() throws Exception {
		// Arrange
		String token = this.arrangeSuccessfulAuthentication();

		PodamFactory factory = new PodamFactoryImpl();
		WorkIssued workIssued = factory.manufacturePojo(WorkIssued.class);
		//doReturn(new SystemUsers()).when(restIntegrationWorkOrderController).getSystemUserFromUserPrincipal();

		when(workOrderController.getWorkOrdersByUserCode(null, "TEST", true)).thenReturn(List.of(workIssued));

		// Act
		MvcResult mvcResult = this.mockMvc.perform(get("/workorder/list?userCode=TEST")
				.header("Authorization", "Bearer " + token)
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
	void workIssuedIWS_shouldGetWorkOrderList_whenUserCodeIsEmpty() throws Exception {
		// Arrange
		String token = this.arrangeSuccessfulAuthentication();

		PodamFactory factory = new PodamFactoryImpl();
		WorkIssued workIssued = factory.manufacturePojo(WorkIssued.class);
		doReturn(true).when(userService).hasUnlimitedAccessibleWorkgroups(null, "FRADM");
		when(workOrderController.getWorkOrdersByWorkGroupCode(null, "TEST", true)).thenReturn(List.of(workIssued));

		// Act
		MvcResult mvcResult = this.mockMvc.perform(get("/workorder/list?workgroupCode=TEST")
				.header("Authorization", "Bearer " + token)
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
	void workIssuedIWS_shouldGetBadReqeust_WhenUserCodeAndworkGroupCodeIsEmpty() throws Exception {
		// Arrange
		String token = this.arrangeSuccessfulAuthentication();

		// Act
		MvcResult mvcResult = this.mockMvc.perform(get("/workorder/list")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		// Assert
		assertNotNull(mvcResult.getResponse());
	    assertNotNull(mvcResult.getResolvedException().getMessage());
	}

	@Test
	void retrieveWorkOrderSourceIWS_shouldGetWorkOrder_whenAcceptHeaderInXml() throws Exception {
		// Arrange
		String token = this.arrangeSuccessfulAuthentication();

		when(workOrderController.getWorkIssuedRecord(any(), anyString(), anyString())).thenReturn(new WorkIssued());
		when(workOrderController.checkStatusIsValidForRetrieval(any(), any(), anyBoolean())).thenReturn(true);
		when(workOrderController.getWorkOrderSourceContents(any(), any(), any())).thenReturn("Sample Contents");
		when(workOrderController.getWorkorderFileName(any())).thenReturn("Sample.xml");


		// Act
		MvcResult mvcResult = this.mockMvc.perform(get("/workorder/1")
				.header("Authorization", "Bearer " + token)
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
	void retrieveWorkOrderSourceIWS_shouldGetWorkOrder_whenAcceptHeaderInJson() throws Exception {
		// Arrange
		String token = this.arrangeSuccessfulAuthentication();

		when(workOrderController.getWorkIssuedRecord(any(), anyString(), anyString())).thenReturn(new WorkIssued());
		when(workOrderController.checkStatusIsValidForRetrieval(any(), any(), anyBoolean())).thenReturn(true);
		when(workOrderController.getWorkOrderSourceContents(any(), any(), any())).thenReturn("Sample Contents");
		when(workOrderController.getWorkorderFileName(any())).thenReturn("Sample.xml");


		// Act
		MvcResult mvcResult = this.mockMvc.perform(get("/workorder/1")
				.header("Authorization", "Bearer " + token)
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
	void initiateWorkOrderAttachmentDownloadIWS_shouldInitiateAttachmentDownload_whenAcceptHeaderInXml() throws Exception {
		// Arrange
		String token = this.arrangeSuccessfulAuthentication();

		String workOrderNo = "10101";
		String filename = "test.xml";
		InitiateDownloadResponse stubbedInitiateDownloadResponse = getStubbedInitiateDownloadResponse(filename);

		when(userService.hasUnlimitedAccessibleWorkgroups(any(), any())).thenReturn(true);
		when(workOrderController.getWorkIssuedRecord(null, workOrderNo, "AA")).thenReturn(new WorkIssued());
		when(workOrderController.getAttachmentDirURI(any(), any())).thenReturn("/temp");
		when(fileDownloadController.initiateFileSystemDownload(any(), anyString())).thenReturn(stubbedInitiateDownloadResponse);

		//doReturn(new SystemUsers()).when(restIntegrationWorkOrderController).getSystemUserFromUserPrincipal();

		// Act
		MvcResult mvcResult = this.mockMvc.perform(get("/workorder/" + workOrderNo + "/attachment/" + filename + "?districtCode=AA")
				.header("Authorization", "Bearer " + token)
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
	void initiateWorkOrderAttachmentDownloadIWS_shouldInitiateAttachmentDownload_whenAcceptHeaderInJson() throws Exception {
		// Arrange
		String token = this.arrangeSuccessfulAuthentication();

		String workOrderNo = "10101";
		String filename = "test.xml";
		InitiateDownloadResponse stubbedInitiateDownloadResponse = getStubbedInitiateDownloadResponse(filename);

		when(userService.hasUnlimitedAccessibleWorkgroups(any(), any())).thenReturn(true);
		when(workOrderController.getWorkIssuedRecord(null, workOrderNo, "AA")).thenReturn(new WorkIssued());
		when(workOrderController.getAttachmentDirURI(any(), any())).thenReturn("/temp");
		when(fileDownloadController.initiateFileSystemDownload(any(), anyString())).thenReturn(stubbedInitiateDownloadResponse);
		//doReturn(new SystemUsers()).when(restIntegrationWorkOrderController).getSystemUserFromUserPrincipal();

		// Act
		MvcResult mvcResult = this.mockMvc.perform(get("/workorder/" + workOrderNo + "/attachment/" + filename + "?districtCode=AA")
				.header("Authorization", "Bearer " + token)
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
	void downloadWorkOrderAttachmentPartIWS_shouldReturnDownloadPartResponse_whenAcceptHeaderInXml() throws Exception {
		// Arrange
		String token = this.arrangeSuccessfulAuthentication();

		String workOrderNo = "10101";
		String filename = "test.xml";
		String identifier = "1";
		int part = 1;
		DownloadPartResponse stubbedDownloadPartResponse = getStubbedDownloadPartResponse(identifier, part);
		when(fileDownloadController.downloadPart(null, identifier, part)).thenReturn(stubbedDownloadPartResponse);
		when(workOrderController.getWorkIssuedRecord(null, workOrderNo)).thenReturn(new WorkIssued());

		when(userService.hasUnlimitedAccessibleWorkgroups(any(), any())).thenReturn(true);
		//doReturn(new SystemUsers()).when(restIntegrationWorkOrderController).getSystemUserFromUserPrincipal();

		// Act
		MvcResult mvcResult = this.mockMvc.perform(get("/workorder/" + workOrderNo + "/attachment/" + filename + "/multipart/"+ identifier + "/" + part)
				.header("Authorization", "Bearer " + token)
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
    void downloadWorkOrderAttachmentPartIWS_shouldReturnDownloadPartResponse_whenAcceptHeaderInFieldSmartXml() throws Exception {
        // Arrange
        String token = this.arrangeSuccessfulAuthentication();

        String workOrderNo = "10101";
        String filename = "test.xml";
        String identifier = "1";
        int part = 1;
        DownloadPartResponse stubbedDownloadPartResponse = getStubbedDownloadPartResponse(identifier, part);
        when(fileDownloadController.downloadPart(null, identifier, part)).thenReturn(stubbedDownloadPartResponse);
        when(workOrderController.getWorkIssuedRecord(null, workOrderNo)).thenReturn(new WorkIssued());

        when(userService.hasUnlimitedAccessibleWorkgroups(any(), any())).thenReturn(true);
        //doReturn(new SystemUsers()).when(restIntegrationWorkOrderController).getSystemUserFromUserPrincipal();

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/workorder/" + workOrderNo + "/attachment/" + filename + "/multipart/"+ identifier + "/" + part)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_XML)
                .accept(APPLICATION_VND_FIELDSMART_DOWNLOAD_PART_1_XML))
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
	void downloadWorkOrderAttachmentPartIWS_shouldReturnDownloadPartResponse_whenAcceptHeaderInJson() throws Exception {
		// Arrange
		String token = this.arrangeSuccessfulAuthentication();

		String workOrderNo = "10101";
		String filename = "test.xml";
		String identifier = "1";
		int part = 1;
		DownloadPartResponse stubbedDownloadPartResponse = getStubbedDownloadPartResponse(identifier, part);
		when(fileDownloadController.downloadPart(null, identifier, part)).thenReturn(stubbedDownloadPartResponse);
		when(workOrderController.getWorkIssuedRecord(null, workOrderNo)).thenReturn(new WorkIssued());

		when(userService.hasUnlimitedAccessibleWorkgroups(any(), any())).thenReturn(true);
		//doReturn(new SystemUsers()).when(restIntegrationWorkOrderController).getSystemUserFromUserPrincipal();

		// Act
		MvcResult mvcResult = this.mockMvc.perform(get("/workorder/" + workOrderNo + "/attachment/" + filename + "/multipart/"+ identifier + "/" + part)
				.header("Authorization", "Bearer " + token)
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
	    void downloadWorkOrderAttachmentPartIWS_shouldReturnDownloadPartResponse_whenAcceptHeaderInFieldSmartJson() throws Exception {
	        // Arrange
	        String token = this.arrangeSuccessfulAuthentication();

	        String workOrderNo = "10101";
	        String filename = "test.xml";
	        String identifier = "1";
	        int part = 1;
	        DownloadPartResponse stubbedDownloadPartResponse = getStubbedDownloadPartResponse(identifier, part);
	        when(fileDownloadController.downloadPart(null, identifier, part)).thenReturn(stubbedDownloadPartResponse);
	        when(workOrderController.getWorkIssuedRecord(null, workOrderNo)).thenReturn(new WorkIssued());

	        when(userService.hasUnlimitedAccessibleWorkgroups(any(), any())).thenReturn(true);
	        //doReturn(new SystemUsers()).when(restIntegrationWorkOrderController).getSystemUserFromUserPrincipal();

	        // Act
	        MvcResult mvcResult = this.mockMvc.perform(get("/workorder/" + workOrderNo + "/attachment/" + filename + "/multipart/"+ identifier + "/" + part)
	                .header("Authorization", "Bearer " + token)
	                .contentType(MediaType.APPLICATION_JSON)
	                .accept(APPLICATION_VND_FIELDSMART_DOWNLOAD_PART_1_JSON))
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
	void dispatchWorkOrder_shouldDispatchWorkOrder_whenAcceptHeaderInXml() throws Exception {
		// Arrange
		String token = this.arrangeSuccessfulAuthentication();

		String workOrderNo = "10101";
		WorkIssued workIssued = new WorkIssued();
		workIssued.setWorkgroupCode("WGC");
		workIssued.setWorkStatus("RELEASED");
		when(workOrderController.getWorkIssuedRecord(null, workOrderNo, "NA")).thenReturn(workIssued);

		when(userService.hasUnlimitedAccessibleWorkgroups(any(), any())).thenReturn(true);

		doReturn(this.workStatuses).when(workOrderController).getWorkStatuses();
		doReturn(Collections.singletonList("RELEASED")).when(workStatuses).getPreDispatchStatusList(any());

		// Act
		MvcResult mvcResult = this.mockMvc.perform(put("/workorder/dispatch/" + workOrderNo)
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML))
				.andExpect(status().isOk())
				.andReturn();

		// Assert
		String contentAsString = mvcResult.getResponse().getContentAsString();
		assertEquals("<CallResponse><error/><success>true</success></CallResponse>", contentAsString);
		CallResponse response = xmlMapper.readValue(contentAsString, CallResponse.class);
		assertTrue(response.isSuccess());
		assertNull(response.getError());
		verify(workOrderController, times(1)).dispatchWorkOrder(any(), any(), any());
	}

	@Test
	void dispatchWorkOrder_shouldDispatchWorkOrder_whenAcceptHeaderInJson() throws Exception {
		// Arrange
		String token = this.arrangeSuccessfulAuthentication();

		String workOrderNo = "10101";
		WorkIssued workIssued = new WorkIssued();
		workIssued.setWorkgroupCode("WGC");
		workIssued.setWorkStatus("RELEASED");
		when(workOrderController.getWorkIssuedRecord(null, workOrderNo, "NA")).thenReturn(workIssued);

		when(userService.hasUnlimitedAccessibleWorkgroups(any(), any())).thenReturn(true);

		doReturn(this.workStatuses).when(workOrderController).getWorkStatuses();
		doReturn(Collections.singletonList("RELEASED")).when(workStatuses).getPreDispatchStatusList(any());
		
		// Act
		MvcResult mvcResult = this.mockMvc.perform(put("/workorder/dispatch/" + workOrderNo)
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();

		// Assert
		String contentAsString = mvcResult.getResponse().getContentAsString();
		CallResponse response = jsonMapper.readValue(contentAsString, CallResponse.class);
		assertTrue(response.isSuccess());
		assertNotNull(response.getError());
		verify(workOrderController, times(1)).dispatchWorkOrder(any(), any(), any());
	}

	@Test
	void issueWorkOrder_shouldValidateRequestAndIssueWorkOrder_whenAcceptHeaderInXml() throws Exception {
		// Arrange
		String token = this.arrangeSuccessfulAuthentication();

		String workOrderNo = "10101";
		WorkIssued workIssued = new WorkIssued();
		workIssued.setWorkgroupCode("WGC");
		workIssued.setWorkStatus("Status");
		String body = getXml();

		when(workOrderController.getWorkIssuedRecord(null, workOrderNo, "NA")).thenReturn(null);
		when(userService.hasUnlimitedAccessibleWorkgroups(any(), any())).thenReturn(true);
		doReturn(this.parseAndValidateWorkOrder(body)).when(workOrderController).parseAndValidateWorkOrder(any());
		
		String sha512 = Common.generateSHA512Checksum(token.getBytes());
		
		MockedStatic<Common> commonMockedStatic = mockStatic(Common.class);
		commonMockedStatic.when(() -> Common.writeBytesToFile(any(), any(), any())).thenReturn(true);
		commonMockedStatic.when(() -> Common.generateSHA512Checksum(any())).thenReturn(sha512);

		// Act
		MvcResult mvcResult = mockMvc.perform(put("/workorder/issue/" + workOrderNo)
				.content(body)
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML))
				.andExpect(status().isOk())
				.andReturn();

		// Assert
		commonMockedStatic.close();
		String contentAsString = mvcResult.getResponse().getContentAsString();
		CallResponse response = xmlMapper.readValue(contentAsString, CallResponse.class);
		assertTrue(response.isSuccess());
		verify(workOrderController, times(1)).logWorkOrderUpload(any(), any(), any(), any(), anyBoolean());

	}
	
	
	
	@Test
	void issueWorkOrder_shouldReturnBadRequest_WhenWorkOrderXMLContentEmpty() throws Exception{
		// Arrange
		String token = this.arrangeSuccessfulAuthentication();

		String workOrderNo = "10101";
		// Act
		MvcResult mvcResult = mockMvc.perform(put("/workorder/issue/" + workOrderNo)
				.content("")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML))
				.andExpect(status().isBadRequest())
				.andReturn();

		// Assert
		 assertNotNull(mvcResult.getResponse());
	     assertNotNull(mvcResult.getResolvedException().getMessage());

	}
	
	@Test
	void registerAttachment_shouldRerutnBadRequest_WhenFirstNameIsMissing() throws Exception {
		// Arrange
		String token = this.arrangeSuccessfulAuthentication();

		String workOrderNo = "10101";
		
		String bodyWithoutFileName = getRegisterAttachmentRequestWithoutFileName();

		// Act
		MvcResult mvcResult = this.mockMvc.perform(put("/workorder/" + workOrderNo + "/attachment")
				.header("Authorization", "Bearer " + token)
				.content(bodyWithoutFileName)
				.contentType(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML))
				.andExpect(status().isBadRequest())
				.andReturn();

		// Assert
		 String contentAsString = mvcResult.getResponse().getContentAsString();
		assertEquals("", contentAsString);
	}
	
	
	@Test
	void registerAttachment_shouldRerutnBadRequest_WhenDataIsMissing() throws Exception {
		// Arrange
		String token = this.arrangeSuccessfulAuthentication();

		String workOrderNo = "10101";
		
		String bodyWithoutData = getRegisterAttachmentRequestWithoutData();

		// Act
		MvcResult mvcResult = this.mockMvc.perform(put("/workorder/" + workOrderNo + "/attachment")
				.header("Authorization", "Bearer " + token)
				.content(bodyWithoutData)
				.contentType(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML))
				.andExpect(status().isBadRequest())
				.andReturn();

		// Assert
		 String contentAsString = mvcResult.getResponse().getContentAsString();
		assertEquals("", contentAsString);
	}

	@Test
	void issueWorkOrder_shouldValidateRequestAndIssueWorkOrder_whenAcceptHeaderInJson() throws Exception {
		// Arrange
		String token = this.arrangeSuccessfulAuthentication();

		String workOrderNo = "10101";
		WorkIssued workIssued = new WorkIssued();
		workIssued.setWorkgroupCode("WGC");
		workIssued.setWorkStatus("Status");
		String body = getXml();

		when(workOrderController.getWorkIssuedRecord(null, workOrderNo, "NA")).thenReturn(null);

		when(userService.hasUnlimitedAccessibleWorkgroups(any(), any())).thenReturn(true);

		doReturn(this.parseAndValidateWorkOrder(body)).when(workOrderController).parseAndValidateWorkOrder(any());

		String sha512 = Common.generateSHA512Checksum(token.getBytes());
		
		MockedStatic<Common> commonMockedStatic = mockStatic(Common.class);
		commonMockedStatic.when(() -> Common.writeBytesToFile(any(), any(), any())).thenReturn(true);
		commonMockedStatic.when(() -> Common.generateSHA512Checksum(any())).thenReturn(sha512);

		// Act

		MvcResult mvcResult = mockMvc.perform(put("/workorder/issue/" + workOrderNo)
				.content(body)
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();

		// Assert
		commonMockedStatic.close();
		String contentAsString = mvcResult.getResponse().getContentAsString();
		CallResponse response = jsonMapper.readValue(contentAsString, CallResponse.class);
		assertTrue(response.isSuccess());
		verify(workOrderController, times(1)).logWorkOrderUpload(any(), any(), any(), any(), anyBoolean());

	}

	@Test
	void releaseWorkOrder_shouldValidateRequestAndReleaseWorkOrder_whenAcceptHeaderInXml() throws Exception {
		// Arrange
		String token = this.arrangeSuccessfulAuthentication();

		String workOrderNo = "10101";
		String body = getXml();

		when(workOrderController.getWorkIssuedRecord(null, workOrderNo, "NA")).thenReturn(null);

		when(userService.hasUnlimitedAccessibleWorkgroups(any(), any())).thenReturn(true);
		doReturn(this.parseAndValidateWorkOrder(body)).when(workOrderController).parseAndValidateWorkOrder(any());
		
		MockedStatic<Common> commonMockedStatic = mockStatic(Common.class, CALLS_REAL_METHODS);
		commonMockedStatic.when(() -> Common.writeBytesToFile(any(), any(), any())).thenReturn(true);

		// Act
		MvcResult mvcResult = mockMvc.perform(put("/workorder/release/" + workOrderNo)
				.content(body)
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML))
				.andExpect(status().isOk())
				.andReturn();

		// Assert
		commonMockedStatic.close();
		String contentAsString = mvcResult.getResponse().getContentAsString();
		CallResponse response = xmlMapper.readValue(contentAsString, CallResponse.class);
		assertTrue(response.isSuccess());
		verify(workOrderController, times(1)).logWorkOrderUpload(any(), any(), any(), any(), anyBoolean());
	}

	@Test
	void releaseWorkOrder_shouldValidateRequestAndReleaseWorkOrder_whenAcceptHeaderInJson() throws Exception {
		// Arrange
		String token = this.arrangeSuccessfulAuthentication();

		String workOrderNo = "10101";
		String body = getXml();

		when(workOrderController.getWorkIssuedRecord(null, workOrderNo, "NA")).thenReturn(null);
		when(userService.hasUnlimitedAccessibleWorkgroups(any(), any())).thenReturn(true);
		doReturn(this.parseAndValidateWorkOrder(body)).when(workOrderController).parseAndValidateWorkOrder(any());
		
		String sha512 = Common.generateSHA512Checksum(token.getBytes());
		MockedStatic<Common> commonMockedStatic = mockStatic(Common.class);
		commonMockedStatic.when(() -> Common.writeBytesToFile(any(), any(), any())).thenReturn(true);
		commonMockedStatic.when(() -> Common.generateSHA512Checksum(any())).thenReturn(sha512);

		// Act
		MvcResult mvcResult = mockMvc.perform(put("/workorder/release/" + workOrderNo)
				.content(body)
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();

		// Assert
		commonMockedStatic.close();
		String contentAsString = mvcResult.getResponse().getContentAsString();
		CallResponse response = jsonMapper.readValue(contentAsString, CallResponse.class);
		assertTrue(response.isSuccess());
		verify(workOrderController, times(1)).logWorkOrderUpload(any(), any(), any(), any(), anyBoolean());
	}
	
	@Test
	void releaseWorkOrder_shouldReturnBadRequestException_WhenContentIsEmpty() throws Exception {
		// Arrange
		String token = this.arrangeSuccessfulAuthentication();

		String workOrderNo = "10101";
		String body = " ";

		// Act
		MvcResult mvcResult = mockMvc.perform(put("/workorder/release/" + workOrderNo)
				.content(body)
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		// Assert
		assertNotNull(mvcResult.getResponse());
		assertNotNull(mvcResult.getResolvedException().getMessage());
	}
	
	@Test
	void updateWorkOrder_shouldReturnBadRequestException_WhenContentIsEmpty() throws Exception {
		// Arrange
		String token = this.arrangeSuccessfulAuthentication();

		String workOrderNo = "10101";
		String body = " ";

		// Act
		MvcResult mvcResult = mockMvc.perform(put("/workorder/update/" + workOrderNo)
				.content(body)
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		// Assert
		assertNotNull(mvcResult.getResponse());
		assertNotNull(mvcResult.getResolvedException().getMessage());
	}

	@Test
	void updateWorkOrder_shouldValidateRequestAndUpdateWorkOrder_whenAcceptHeaderInXml() throws Exception {
		// Arrange
		String token = this.arrangeSuccessfulAuthentication();

		String workOrderNo = "10101";
		String body = getXml();
		WorkIssued workIssued = new WorkIssued();
		workIssued.setWorkgroupCode("WGC");
		workIssued.setWorkStatus("Status");

		when(workOrderController.getWorkIssuedRecord(null, workOrderNo, "NA")).thenReturn(workIssued);
		when(userService.hasUnlimitedAccessibleWorkgroups(any(), any())).thenReturn(true);
		doReturn(this.parseAndValidateWorkOrder(body)).when(workOrderController).parseAndValidateWorkOrder(any());
		
		doReturn(this.workStatuses).when(workOrderController).getWorkStatuses();
		
		// Act
		MvcResult mvcResult = mockMvc.perform(put("/workorder/update/" + workOrderNo)
				.content(body)
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML))
				.andExpect(status().isOk())
				.andReturn();

		// Assert
		String contentAsString = mvcResult.getResponse().getContentAsString();
		CallResponse response = xmlMapper.readValue(contentAsString, CallResponse.class);
		assertTrue(response.isSuccess());
		verify(workOrderController, times(1)).updateWorkOrder(any(), any(), any(), any(), any(), any());
	}

	@Test
	void updateWorkOrder_shouldValidateRequestAndUpdateWorkOrder_whenAcceptHeaderInJson() throws Exception {
		// Arrange
		String token = this.arrangeSuccessfulAuthentication();

		String workOrderNo = "10101";
		String body = getXml();
		WorkIssued workIssued = new WorkIssued();
		workIssued.setWorkgroupCode("WGC");
		workIssued.setWorkStatus("Status");

		when(workOrderController.getWorkIssuedRecord(null, workOrderNo, "NA")).thenReturn(workIssued);
		when(userService.hasUnlimitedAccessibleWorkgroups(any(), any())).thenReturn(true);
		doReturn(this.parseAndValidateWorkOrder(body)).when(workOrderController).parseAndValidateWorkOrder(any());
		doReturn(this.workStatuses).when(workOrderController).getWorkStatuses();
		doReturn(false).when(workStatuses).statusIsDesignated(any(), any(), any());
		
		// Act
		
		MvcResult mvcResult = mockMvc.perform(put("/workorder/update/" + workOrderNo)
				.content(body)
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();

		// Assert
		String contentAsString = mvcResult.getResponse().getContentAsString();
		CallResponse response = jsonMapper.readValue(contentAsString, CallResponse.class);
		assertTrue(response.isSuccess());
		verify(workOrderController, times(1)).updateWorkOrder(any(), any(), any(), any(), any(), any());
	}

	@Test
	void cancelWorkOrder_shouldValidateRequestAndCancelWorkOrder_whenAcceptHeaderInXml() throws Exception {
		// Arrange
		String token = this.arrangeSuccessfulAuthentication();

		String workOrderNo = "10101";
		WorkOrderStatusRequest request = new WorkOrderStatusRequest();
		request.setWorkStatusAdditionalText("Additional Text");
		WorkIssued workIssued = new WorkIssued();
		workIssued.setWorkgroupCode("WGC");
		workIssued.setWorkStatus("Status");

		when(workOrderController.getWorkIssuedRecord(null, workOrderNo, "NA")).thenReturn(workIssued);

		when(userService.hasUnlimitedAccessibleWorkgroups(any(), any())).thenReturn(true);
		//doReturn(new SystemUsers()).when(restIntegrationWorkOrderController).getSystemUserFromUserPrincipal();

		// Act
		MvcResult mvcResult = this.mockMvc.perform(put("/workorder/cancel/" + workOrderNo)
				.content(getWorkOrderStatusRequest())
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML))
				.andExpect(status().isOk())
				.andReturn();

		// Assert
		String contentAsString = mvcResult.getResponse().getContentAsString();
		assertEquals("<CallResponse><error/><success>true</success></CallResponse>", contentAsString);
		CallResponse response = xmlMapper.readValue(contentAsString, CallResponse.class);
		assertTrue(response.isSuccess());
		assertNull(response.getError());
		verify(workOrderController, times(1)).cancelWorkOrder(any(), any(), any());
	}
	
	   @Test
	    void cancelWorkOrder_shouldValidateRequestAndCancelWorkOrder_whenAcceptHeaderInFieldSmartXml() throws Exception {
	        // Arrange
	        String token = this.arrangeSuccessfulAuthentication();

	        String workOrderNo = "10101";
	        WorkOrderStatusRequest request = new WorkOrderStatusRequest();
	        request.setWorkStatusAdditionalText("Additional Text");
	        WorkIssued workIssued = new WorkIssued();
	        workIssued.setWorkgroupCode("WGC");
	        workIssued.setWorkStatus("Status");

	        when(workOrderController.getWorkIssuedRecord(null, workOrderNo, "NA")).thenReturn(workIssued);

	        when(userService.hasUnlimitedAccessibleWorkgroups(any(), any())).thenReturn(true);
	        //doReturn(new SystemUsers()).when(restIntegrationWorkOrderController).getSystemUserFromUserPrincipal();

	        // Act
	        MvcResult mvcResult = this.mockMvc.perform(put("/workorder/cancel/" + workOrderNo)
	                .content(getWorkOrderStatusRequest())
	                .header("Authorization", "Bearer " + token)
	                .contentType(APPLICATION_VND_FIELDSMART_WORKORDERSTATUS_1_XML)
	                .accept(MediaType.APPLICATION_XML))
	                .andExpect(status().isOk())
	                .andReturn();

	        // Assert
	        String contentAsString = mvcResult.getResponse().getContentAsString();
	        assertEquals("<CallResponse><error/><success>true</success></CallResponse>", contentAsString);
	        CallResponse response = xmlMapper.readValue(contentAsString, CallResponse.class);
	        assertTrue(response.isSuccess());
	        assertNull(response.getError());
	        verify(workOrderController, times(1)).cancelWorkOrder(any(), any(), any());
	    }

	@Test
	void cancelWorkOrder_shouldValidateRequestAndCancelWorkOrder_whenAcceptHeaderInJson() throws Exception {
		// Arrange
		String token = this.arrangeSuccessfulAuthentication();

		String workOrderNo = "10101";
		WorkOrderStatusRequest request = new WorkOrderStatusRequest();
		request.setWorkStatusAdditionalText("Additional Text");
		WorkIssued workIssued = new WorkIssued();
		workIssued.setWorkgroupCode("WGC");
		workIssued.setWorkStatus("Status");

		when(workOrderController.getWorkIssuedRecord(null, workOrderNo, "NA")).thenReturn(workIssued);

		when(userService.hasUnlimitedAccessibleWorkgroups(any(), any())).thenReturn(true);
		//doReturn(new SystemUsers()).when(restIntegrationWorkOrderController).getSystemUserFromUserPrincipal();

		// Act
		MvcResult mvcResult = this.mockMvc.perform(put("/workorder/cancel/" + workOrderNo)
				.content(getWorkOrderStatusRequestJson())
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();

		// Assert
		String contentAsString = mvcResult.getResponse().getContentAsString();
		CallResponse response = jsonMapper.readValue(contentAsString, CallResponse.class);
		assertTrue(response.isSuccess());
		assertNotNull(response.getError());
		verify(workOrderController, times(1)).cancelWorkOrder(any(), any(), any());
	}
	
	@Test
    void cancelWorkOrder_shouldValidateRequestAndCancelWorkOrder_whenAcceptHeaderInFieldSmartJson() throws Exception {
        // Arrange
        String token = this.arrangeSuccessfulAuthentication();

        String workOrderNo = "10101";
        WorkOrderStatusRequest request = new WorkOrderStatusRequest();
        request.setWorkStatusAdditionalText("Additional Text");
        WorkIssued workIssued = new WorkIssued();
        workIssued.setWorkgroupCode("WGC");
        workIssued.setWorkStatus("Status");

        when(workOrderController.getWorkIssuedRecord(null, workOrderNo, "NA")).thenReturn(workIssued);

        when(userService.hasUnlimitedAccessibleWorkgroups(any(), any())).thenReturn(true);
        //doReturn(new SystemUsers()).when(restIntegrationWorkOrderController).getSystemUserFromUserPrincipal();

        // Act
        MvcResult mvcResult = this.mockMvc.perform(put("/workorder/cancel/" + workOrderNo)
                .content(getWorkOrderStatusRequestJson())
                .header("Authorization", "Bearer " + token)
                .contentType(APPLICATION_VND_FIELDSMART_WORKORDERSTATUS_1_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        CallResponse response = jsonMapper.readValue(contentAsString, CallResponse.class);
        assertTrue(response.isSuccess());
        assertNotNull(response.getError());
        verify(workOrderController, times(1)).cancelWorkOrder(any(), any(), any());
    }

	@Test
	void recallWorkOrder_shouldValidateRequestAndRecallWorkOrder_whenAcceptHeaderInXml() throws Exception {
		// Arrange
		String token = this.arrangeSuccessfulAuthentication();

		String workOrderNo = "10101";
		WorkOrderStatusRequest request = new WorkOrderStatusRequest();
		request.setWorkStatusAdditionalText("Additional Text");
		WorkIssued workIssued = new WorkIssued();
		workIssued.setWorkgroupCode("WGC");
		workIssued.setWorkStatus("Status");
		workIssued.setId(new WorkIssuedId());
		workIssued.setWorkOrderNo(workOrderNo);
		workIssued.setDistrictCode("DC");

		when(workOrderController.getWorkIssuedRecord(null, workOrderNo, "NA")).thenReturn(workIssued);
		doNothing().when(workOrderController).recallWorkOrder(any(), any(), any(), any(), any());

		when(userService.hasUnlimitedAccessibleWorkgroups(any(), any())).thenReturn(true);
		//doReturn(new SystemUsers()).when(restIntegrationWorkOrderController).getSystemUserFromUserPrincipal();

		// Act
		MvcResult mvcResult = this.mockMvc.perform(put("/workorder/recall/" + workOrderNo)
				.content(getWorkOrderStatusRequest())
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML))
				.andExpect(status().isOk())
				.andReturn();

		// Assert
		String contentAsString = mvcResult.getResponse().getContentAsString();
		assertEquals("<CallResponse><error/><success>true</success></CallResponse>", contentAsString);
		CallResponse response = xmlMapper.readValue(contentAsString, CallResponse.class);
		assertTrue(response.isSuccess());
		assertNull(response.getError());
		verify(workOrderController, times(1)).recallWorkOrder(any(), any(), any(), any(), any());
	}
	
	   @Test
	    void recallWorkOrder_shouldValidateRequestAndRecallWorkOrder_whenAcceptHeaderInFieldSmartXml() throws Exception {
	        // Arrange
	        String token = this.arrangeSuccessfulAuthentication();

	        String workOrderNo = "10101";
	        WorkOrderStatusRequest request = new WorkOrderStatusRequest();
	        request.setWorkStatusAdditionalText("Additional Text");
	        WorkIssued workIssued = new WorkIssued();
	        workIssued.setWorkgroupCode("WGC");
	        workIssued.setWorkStatus("Status");
	        workIssued.setId(new WorkIssuedId());
	        workIssued.setWorkOrderNo(workOrderNo);
	        workIssued.setDistrictCode("DC");

	        when(workOrderController.getWorkIssuedRecord(null, workOrderNo, "NA")).thenReturn(workIssued);
	        doNothing().when(workOrderController).recallWorkOrder(any(), any(), any(), any(), any());

	        when(userService.hasUnlimitedAccessibleWorkgroups(any(), any())).thenReturn(true);
	        //doReturn(new SystemUsers()).when(restIntegrationWorkOrderController).getSystemUserFromUserPrincipal();

	        // Act
	        MvcResult mvcResult = this.mockMvc.perform(put("/workorder/recall/" + workOrderNo)
	                .content(getWorkOrderStatusRequest())
	                .header("Authorization", "Bearer " + token)
	                .contentType(APPLICATION_VND_FIELDSMART_WORKORDERSTATUS_1_XML)
	                .accept(MediaType.APPLICATION_XML))
	                .andExpect(status().isOk())
	                .andReturn();

	        // Assert
	        String contentAsString = mvcResult.getResponse().getContentAsString();
	        assertEquals("<CallResponse><error/><success>true</success></CallResponse>", contentAsString);
	        CallResponse response = xmlMapper.readValue(contentAsString, CallResponse.class);
	        assertTrue(response.isSuccess());
	        assertNull(response.getError());
	        verify(workOrderController, times(1)).recallWorkOrder(any(), any(), any(), any(), any());
	    }

	@Test
	void recallWorkOrder_shouldValidateRequestAndRecallWorkOrder_whenAcceptHeaderInJson() throws Exception {
		// Arrange
		String token = this.arrangeSuccessfulAuthentication();

		String workOrderNo = "10101";
		WorkOrderStatusRequest request = new WorkOrderStatusRequest();
		request.setWorkStatusAdditionalText("Additional Text");
		WorkIssued workIssued = new WorkIssued();
		workIssued.setWorkgroupCode("WGC");
		workIssued.setWorkStatus("Status");
		workIssued.setId(new WorkIssuedId());
		workIssued.setWorkOrderNo(workOrderNo);
		workIssued.setDistrictCode("DC");

		when(workOrderController.getWorkIssuedRecord(null, workOrderNo, "NA")).thenReturn(workIssued);
		doNothing().when(workOrderController).recallWorkOrder(any(), any(), any(), any(), any());

		when(userService.hasUnlimitedAccessibleWorkgroups(any(), any())).thenReturn(true);
		//doReturn(new SystemUsers()).when(restIntegrationWorkOrderController).getSystemUserFromUserPrincipal();

		// Act
		MvcResult mvcResult = this.mockMvc.perform(put("/workorder/recall/" + workOrderNo)
				.content(getWorkOrderStatusRequestJson())
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();

		// Assert
		String contentAsString = mvcResult.getResponse().getContentAsString();
		CallResponse response = jsonMapper.readValue(contentAsString, CallResponse.class);
		assertTrue(response.isSuccess());
		assertNotNull(response.getError());
		verify(workOrderController, times(1)).recallWorkOrder(any(), any(), any(), any(), any());
	}
	
	@Test
    void recallWorkOrder_shouldValidateRequestAndRecallWorkOrder_whenAcceptHeaderInFieldSmartJson() throws Exception {
        // Arrange
        String token = this.arrangeSuccessfulAuthentication();

        String workOrderNo = "10101";
        WorkOrderStatusRequest request = new WorkOrderStatusRequest();
        request.setWorkStatusAdditionalText("Additional Text");
        WorkIssued workIssued = new WorkIssued();
        workIssued.setWorkgroupCode("WGC");
        workIssued.setWorkStatus("Status");
        workIssued.setId(new WorkIssuedId());
        workIssued.setWorkOrderNo(workOrderNo);
        workIssued.setDistrictCode("DC");

        when(workOrderController.getWorkIssuedRecord(null, workOrderNo, "NA")).thenReturn(workIssued);
        doNothing().when(workOrderController).recallWorkOrder(any(), any(), any(), any(), any());

        when(userService.hasUnlimitedAccessibleWorkgroups(any(), any())).thenReturn(true);
        //doReturn(new SystemUsers()).when(restIntegrationWorkOrderController).getSystemUserFromUserPrincipal();

        // Act
        MvcResult mvcResult = this.mockMvc.perform(put("/workorder/recall/" + workOrderNo)
                .content(getWorkOrderStatusRequestJson())
                .header("Authorization", "Bearer " + token)
                .contentType(APPLICATION_VND_FIELDSMART_WORKORDERSTATUS_1_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        CallResponse response = jsonMapper.readValue(contentAsString, CallResponse.class);
        assertTrue(response.isSuccess());
        assertNotNull(response.getError());
        verify(workOrderController, times(1)).recallWorkOrder(any(), any(), any(), any(), any());
    }

	@Test
	void registerAttachment_shouldValidateRequestAndRegisterAttachment_whenAcceptHeaderInXml() throws Exception {
		// Arrange
		String token = this.arrangeSuccessfulAuthentication();

		String workOrderNo = "10101";
		WorkOrderStatusRequest request = new WorkOrderStatusRequest();
		request.setWorkStatusAdditionalText("Additional Text");
		WorkIssued workIssued = new WorkIssued();
		workIssued.setWorkgroupCode("WGC");
		workIssued.setWorkStatus("Status");

		when(workOrderController.getWorkIssuedRecord(null, workOrderNo, "NA")).thenReturn(workIssued);

		when(userService.hasUnlimitedAccessibleWorkgroups(any(), any())).thenReturn(true);
		//doReturn(new SystemUsers()).when(restIntegrationWorkOrderController).getSystemUserFromUserPrincipal();
		String body = getRegisterAttachmentRequest();

		// Act
		MvcResult mvcResult = this.mockMvc.perform(put("/workorder/" + workOrderNo + "/attachment")
				.content(body)
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML))
				.andExpect(status().isOk())
				.andReturn();

		// Assert
		String contentAsString = mvcResult.getResponse().getContentAsString();
		assertEquals("<CallResponse><error/><success>true</success></CallResponse>", contentAsString);
		CallResponse response = xmlMapper.readValue(contentAsString, CallResponse.class);
		assertTrue(response.isSuccess());
		assertNull(response.getError());
		verify(workOrderController, times(1)).registerAttachment(any(), any(), any());
	}
	
	@Test
	void registerAttachment_shouldRerutnBadRequest_WhenBodyIsMissing() throws Exception {
		// Arrange
		String token = this.arrangeSuccessfulAuthentication();

		String workOrderNo = "10101";

		// Act
		MvcResult mvcResult = this.mockMvc.perform(put("/workorder/" + workOrderNo + "/attachment")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML))
				.andExpect(status().isBadRequest())
				.andReturn();

		// Assert
		 String contentAsString = mvcResult.getResponse().getContentAsString();
		assertEquals("", contentAsString);
	}
	
	   @Test
	    void registerAttachment_shouldValidateRequestAndRegisterAttachment_whenAcceptHeaderInFieldSmartXml() throws Exception {
	        // Arrange
	        String token = this.arrangeSuccessfulAuthentication();

	        String workOrderNo = "10101";
	        WorkOrderStatusRequest request = new WorkOrderStatusRequest();
	        request.setWorkStatusAdditionalText("Additional Text");
	        WorkIssued workIssued = new WorkIssued();
	        workIssued.setWorkgroupCode("WGC");
	        workIssued.setWorkStatus("Status");

	        when(workOrderController.getWorkIssuedRecord(null, workOrderNo, "NA")).thenReturn(workIssued);

	        when(userService.hasUnlimitedAccessibleWorkgroups(any(), any())).thenReturn(true);
	        //doReturn(new SystemUsers()).when(restIntegrationWorkOrderController).getSystemUserFromUserPrincipal();
	        String body = getRegisterAttachmentRequest();

	        // Act
	        MvcResult mvcResult = this.mockMvc.perform(put("/workorder/" + workOrderNo + "/attachment")
	                .content(body)
	                .header("Authorization", "Bearer " + token)
	                .contentType(APPLICATION_VND_FIELDSMART_ATTACHMENT_1_XML)
	                .accept(MediaType.APPLICATION_XML))
	                .andExpect(status().isOk())
	                .andReturn();

	        // Assert
	        String contentAsString = mvcResult.getResponse().getContentAsString();
	        assertEquals("<CallResponse><error/><success>true</success></CallResponse>", contentAsString);
	        CallResponse response = xmlMapper.readValue(contentAsString, CallResponse.class);
	        assertTrue(response.isSuccess());
	        assertNull(response.getError());
	        verify(workOrderController, times(1)).registerAttachment(any(), any(), any());
	    }

	@Test
	void registerAttachment_shouldValidateRequestAndRegisterAttachment_whenAcceptHeaderInJson() throws Exception {
		// Arrange
		String token = this.arrangeSuccessfulAuthentication();

		String workOrderNo = "10101";
		WorkOrderStatusRequest request = new WorkOrderStatusRequest();
		request.setWorkStatusAdditionalText("Additional Text");
		WorkIssued workIssued = new WorkIssued();
		workIssued.setWorkgroupCode("WGC");
		workIssued.setWorkStatus("Status");

		when(workOrderController.getWorkIssuedRecord(null, workOrderNo, "NA")).thenReturn(workIssued);

		when(userService.hasUnlimitedAccessibleWorkgroups(any(), any())).thenReturn(true);
		//doReturn(new SystemUsers()).when(restIntegrationWorkOrderController).getSystemUserFromUserPrincipal();
		String body = getRegisterAttachmentRequestJson();

		// Act
		MvcResult mvcResult = this.mockMvc.perform(put("/workorder/" + workOrderNo + "/attachment")
				.content(body)
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();

		// Assert
		String contentAsString = mvcResult.getResponse().getContentAsString();
		CallResponse response = jsonMapper.readValue(contentAsString, CallResponse.class);
		assertTrue(response.isSuccess());
		assertNotNull(response.getError());
		verify(workOrderController, times(1)).registerAttachment(any(), any(), any());
	}
	
	@Test
    void registerAttachment_shouldValidateRequestAndRegisterAttachment_whenAcceptHeaderInFieldSmartJson() throws Exception {
        // Arrange
        String token = this.arrangeSuccessfulAuthentication();

        String workOrderNo = "10101";
        WorkOrderStatusRequest request = new WorkOrderStatusRequest();
        request.setWorkStatusAdditionalText("Additional Text");
        WorkIssued workIssued = new WorkIssued();
        workIssued.setWorkgroupCode("WGC");
        workIssued.setWorkStatus("Status");

        when(workOrderController.getWorkIssuedRecord(null, workOrderNo, "NA")).thenReturn(workIssued);

        when(userService.hasUnlimitedAccessibleWorkgroups(any(), any())).thenReturn(true);
        //doReturn(new SystemUsers()).when(restIntegrationWorkOrderController).getSystemUserFromUserPrincipal();
        String body = getRegisterAttachmentRequestJson();

        // Act
        MvcResult mvcResult = this.mockMvc.perform(put("/workorder/" + workOrderNo + "/attachment")
                .content(body)
                .header("Authorization", "Bearer " + token)
                .contentType(APPLICATION_VND_FIELDSMART_ATTACHMENT_1_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        CallResponse response = jsonMapper.readValue(contentAsString, CallResponse.class);
        assertTrue(response.isSuccess());
        assertNotNull(response.getError());
        verify(workOrderController, times(1)).registerAttachment(any(), any(), any());
    }


	private String getXml() {
		return
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<WorkOrder>\n" +
				"\t<WorkOrderDetail>\n" +
				"\t\t<WorkOrderNo>10101</WorkOrderNo>\n" +
				"\t\t<WorkOrderDesc>VEHICLE INSPECTION 10101</WorkOrderDesc>\n" +
				"\t\t<WorkGroupCode>WGC</WorkGroupCode>\n" +
				"\t\t<OperationType>A</OperationType>\n" +
				"\t\t<PlanStartDate>20130317</PlanStartDate>\n" +
				"\t\t<Latitude>12.985114</Latitude>\n" +
				"\t\t<Longitude>77.694293</Longitude>\n" +
				"\t\t<ReqFinishDate>20130317</ReqFinishDate>\n" +
				"\t\t<CreationDate>20140529</CreationDate>\n" +
				"\t\t<CreationTime>123105</CreationTime>\n" +
				"\t\t<DistrictCode>NA</DistrictCode>\n" +
				"\t\t<FuelType>PETROL</FuelType>\n" +
				"\t\t<Make>RENAULT</Make>\n" +
				"\t\t<Model>MEGANE DYNAMIQUE</Model>\n" +
				"\t\t<Owner>BORRd</Owner>\n" +
				"\t\t<Colour>BLUE</Colour>\n" +
				"\t\t<AccountNo>0714-96-5820</AccountNo>\n" +
				"\t\t<AccountClientNo>07/55/513916</AccountClientNo>\n" +
				"\t\t<Contact>000 8408 4101</Contact>\n" +
				"\t\t<Email>BORREGO@ACME.COM</Email>\n" +
				"\t\t<ReqFinishDate>null</ReqFinishDate>\n" +
				"\t\t<EquipDesc>DETACHED</EquipDesc>\n" +
				"\t\t<AdditionalText/>\n" +
				"\t\t<UserCode></UserCode>\n" +
				"\t</WorkOrderDetail>\n" +
				"</WorkOrder>";
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

	private String getWorkOrderStatusRequest() {
		return "<WorkOrder>" +
				"  <WorkStatusAdditionalText>Additional Text</WorkStatusAdditionalText>" +
				"</WorkOrder>";
	}

	private String getWorkOrderStatusRequestJson() {
		return "{\n" +
				"    \"WorkStatusAdditionalText\": \"Additional Text\"\n" +
				"  }";
	}
	
	private String getRegisterAttachmentRequestWithoutFileName() {
		return "<RegisterAttachment>" +
				"<fileDesc>descriptiondescription</fileDesc>" +
				"<fileType>xml</fileType>" +
				"<data>fileData</data>" +
				"<checksum>42A43E5EEB6B0C3A9D387ED17E428D9A</checksum>" + 
				"</RegisterAttachment>";
	}
	
	private String getRegisterAttachmentRequestWithoutData() {
		return "<RegisterAttachment>" +
				"<fileDesc>descriptiondescription</fileDesc>" +
				"<fileType>xml</fileType>" +
				"<checksum>42A43E5EEB6B0C3A9D387ED17E428D9A</checksum>" + 
				"</RegisterAttachment>";
	}

	private String getRegisterAttachmentRequest() {
		return "<RegisterAttachment>" +
				"<fileName>test.xml</fileName>" +
				"<fileDesc>descriptiondescription</fileDesc>" +
				"<fileType>xml</fileType>" +
				"<data>fileData</data>" +
				"<checksum>42A43E5EEB6B0C3A9D387ED17E428D9A</checksum>" + 
				"</RegisterAttachment>";
	}

	private String getRegisterAttachmentRequestJson() {
		return "{\n" +
				"    \"fileName\": \"test.xml\",\n" +
				"    \"fileDesc\": \"descriptiondescription\",\n" +
				"    \"fileType\": \"xml\",\n" +
				"    \"data\": \"fileData\",\n" +
				"    \"checksum\": \"42A43E5EEB6B0C3A9D387ED17E428D9A\"\n" +
				"  }";
	}
	
	public Document parseAndValidateWorkOrder(String workOrderXML)
			throws SAXException, ParserConfigurationException, IOException {


		Document doc = null;

		// Get the schema being used to validate the work order XML
		SchemaFactory factory = SchemaFactory
				.newInstance("http://www.w3.org/2001/XMLSchema");

		Schema schema = factory.newSchema(this.getClass().getClassLoader()
				.getResource("workorder.xsd"));

		// Parse and validate the XML file. Exceptions thrown if any error
		// occurs.
		XMLUtils xmlUtils = new XMLUtils(schema);
		doc = xmlUtils.parseXML(workOrderXML);

		return doc;
	}
}