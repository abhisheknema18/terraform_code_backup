package com.amtsybex.fieldreach.services.endpoint.rest.impl;

import static com.amtsybex.fieldreach.services.messages.response.UploadInitiateResponse.APPLICATION_VND_FIELDSMART_UPLOAD_INITIATE_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.UploadInitiateResponse.APPLICATION_VND_FIELDSMART_UPLOAD_INITIATE_1_XML;
import static com.amtsybex.fieldreach.services.messages.response.UploadPartResponse.APPLICATION_VND_FIELDSMART_UPLOAD_PART_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.UploadPartResponse.APPLICATION_VND_FIELDSMART_UPLOAD_PART_1_XML;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigInteger;

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

import com.amtsybex.fieldreach.services.messages.request.UploadInitiate;
import com.amtsybex.fieldreach.services.messages.response.UploadInitiateResponse;
import com.amtsybex.fieldreach.services.messages.response.UploadPartResponse;
import com.amtsybex.fieldreach.services.upload.UploadController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class RestUploadControllerImplTest {

	@Mock
	private UploadController fileUploadController;

	@Autowired
	@InjectMocks
	private RestUploadControllerImpl restUploadController;

	private final XmlMapper xmlMapper = new XmlMapper();

	private final ObjectMapper jsonMapper = new ObjectMapper();

	private MockMvc mockMvc;

	@BeforeEach
	public void setup() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(restUploadController).build();
	}

	@Test
	void initiateUpload_shouldValidateUploadRequest_whenAcceptHeaderInXml() throws Exception {
		// ARRANGE
		String filename = "test.gfpkg";
		String id = "ID";
		int totalSizeBytes = 1024;
		UploadInitiate stubbedUploadInitiate = getStubbedUpdateInitiate(filename, id, totalSizeBytes);
		UploadInitiateResponse stubbedUploadResponse = getStubbedUploadInitiateResponse(id, totalSizeBytes);
		when(fileUploadController.initiate(null, id, filename, totalSizeBytes)).thenReturn(stubbedUploadResponse);

		// ACT
		MvcResult mvcResult = this.mockMvc.perform(post("/files/" + filename + "/multipart")
				.contentType(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.content(xmlMapper.writeValueAsString(stubbedUploadInitiate)))
				.andExpect(status().isOk())
				.andReturn();

		// ASSERT
		String contentAsString = mvcResult.getResponse().getContentAsString();
		//        assertEquals("<UploadInitiateResponse><identifier>ID</identifier><maxChunkSizeBytes>1024</maxChunkSizeBytes><startFromPart>1</startFromPart><success>true</success></UploadInitiateResponse>", contentAsString);
		UploadInitiateResponse response = xmlMapper.readValue(contentAsString, UploadInitiateResponse.class);
		assertTrue(response.isSuccess());
		assertEquals(stubbedUploadResponse.getIdentifier(), response.getIdentifier());
		assertEquals(stubbedUploadResponse.getMaxChunkSizeBytes(), response.getMaxChunkSizeBytes());
		assertEquals(stubbedUploadResponse.getStartFromPart(), response.getStartFromPart());
	}

	@Test
	void initiateUpload_shouldReturnValidContentType_whenRequestCOntentTypeInFusionXml() throws Exception {
		// ARRANGE
		String filename = "test.gfpkg";
		String id = "ID";
		int totalSizeBytes = 1024;
		UploadInitiate stubbedUploadInitiate = getStubbedUpdateInitiate(filename, id, totalSizeBytes);
		UploadInitiateResponse stubbedUploadResponse = getStubbedUploadInitiateResponse(id, totalSizeBytes);
		when(fileUploadController.initiate(null, id, filename, totalSizeBytes)).thenReturn(stubbedUploadResponse);

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
		assertEquals(stubbedUploadResponse.getIdentifier(), response.getIdentifier());
		assertEquals(stubbedUploadResponse.getMaxChunkSizeBytes(), response.getMaxChunkSizeBytes());
		assertEquals(stubbedUploadResponse.getStartFromPart(), response.getStartFromPart());
	}

	@Test
	void initiateUpload_shouldValidateUploadRequest_whenAcceptHeaderInJson() throws Exception {
		// ARRANGE
		String filename = "test.gfpkg";
		String id = "ID";
		int totalSizeBytes = 1024;
		UploadInitiate stubbedUploadInitiate = getStubbedUpdateInitiate(filename, id, totalSizeBytes);
		UploadInitiateResponse stubbedUploadResponse = getStubbedUploadInitiateResponse(id, totalSizeBytes);
		when(fileUploadController.initiate(null, id, filename, totalSizeBytes)).thenReturn(stubbedUploadResponse);

		// ACT
		MvcResult mvcResult = this.mockMvc.perform(post("/files/" + filename + "/multipart")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(jsonMapper.writeValueAsString(stubbedUploadInitiate)))
				.andExpect(status().isOk())
				.andReturn();

		// ASSERT
		String contentAsString = mvcResult.getResponse().getContentAsString();
		UploadInitiateResponse response = jsonMapper.readValue(contentAsString, UploadInitiateResponse.class);
		assertTrue(response.isSuccess());
		assertEquals(stubbedUploadResponse.getIdentifier(), response.getIdentifier());
		assertEquals(stubbedUploadResponse.getMaxChunkSizeBytes(), response.getMaxChunkSizeBytes());
		assertEquals(stubbedUploadResponse.getStartFromPart(), response.getStartFromPart());
	}

	@Test
	void initiateUpload_shouldReturnValidContentType_whenRequestCOntentTypeInFusionJson() throws Exception {
		// ARRANGE
		String filename = "test.gfpkg";
		String id = "ID";
		int totalSizeBytes = 1024;
		UploadInitiate stubbedUploadInitiate = getStubbedUpdateInitiate(filename, id, totalSizeBytes);
		UploadInitiateResponse stubbedUploadResponse = getStubbedUploadInitiateResponse(id, totalSizeBytes);
		when(fileUploadController.initiate(null, id, filename, totalSizeBytes)).thenReturn(stubbedUploadResponse);

		// ACT
		MvcResult mvcResult = this.mockMvc.perform(post("/files/" + filename + "/multipart")
				.contentType(APPLICATION_VND_FIELDSMART_UPLOAD_INITIATE_1_JSON)
				.accept(APPLICATION_VND_FIELDSMART_UPLOAD_INITIATE_1_JSON)
				.content(jsonMapper.writeValueAsString(stubbedUploadInitiate)))
				.andExpect(status().isOk())
				.andReturn();

		// ASSERT
		String contentAsString = mvcResult.getResponse().getContentAsString();
		UploadInitiateResponse response = jsonMapper.readValue(contentAsString, UploadInitiateResponse.class);
		String contentType = mvcResult.getResponse().getContentType();
		assertEquals(APPLICATION_VND_FIELDSMART_UPLOAD_INITIATE_1_JSON,contentType);
		assertTrue(response.isSuccess());
		assertEquals(stubbedUploadResponse.getIdentifier(), response.getIdentifier());
		assertEquals(stubbedUploadResponse.getMaxChunkSizeBytes(), response.getMaxChunkSizeBytes());
		assertEquals(stubbedUploadResponse.getStartFromPart(), response.getStartFromPart());
	}

	@Test
	void uploadPart_shouldValidateUploadPartRequestAndRespond_whenAcceptHeaderInOctetStream() throws Exception {
		// ARRANGE
		String filename = "test.gfpkg";
		String id = "ID";
		String partNumber = "1";
		String partLength = "10240";
		String partCheckSum = "checksumtestvalue";

		UploadPartResponse stubResponse = new UploadPartResponse();
		stubResponse.setSuccess(true);
		stubResponse.setComplete(true);
		stubResponse.setNextPart(BigInteger.ONE);
		when(fileUploadController.receiveIntPart(any(), any(), anyInt(), anyLong(), any(), any())).thenReturn(stubResponse);

		// ACT
		MvcResult mvcResult = this.mockMvc.perform(put("/files/" + filename + "/multipart/" + id)
				.header("x-iws-part-number", partNumber)
				.header("x-iws-part-length", partLength)
				.header("x-iws-checksum", partCheckSum)
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.accept(MediaType.APPLICATION_XML)
				.content("testccontent".getBytes()))
				.andExpect(status().isOk())
				.andReturn();


		// ASSERT
		String contentAsString = mvcResult.getResponse().getContentAsString();
		UploadPartResponse response = xmlMapper.readValue(contentAsString, UploadPartResponse.class);
		assertTrue(response.isSuccess());
		assertTrue(response.isComplete());
		assertEquals(stubResponse.getNextPart(), response.getNextPart());
	}

	@Test
	void uploadPart_shouldValidateContentType_whenContentTypeInXml() throws Exception {
		// ARRANGE
		String filename = "test.gfpkg";
		String id = "ID";
		String partNumber = "1";
		String partLength = "10240";
		String partCheckSum = "checksumtestvalue";
		UploadPartResponse stubResponse = new UploadPartResponse();
		stubResponse.setSuccess(true);
		stubResponse.setComplete(true);
		stubResponse.setNextPart(BigInteger.ONE);
		when(fileUploadController.receiveIntPart(any(), any(), anyInt(), anyLong(), any(), any())).thenReturn(stubResponse);

		// ACT
		MvcResult mvcResult = this.mockMvc.perform(put("/files/" + filename + "/multipart/" + id)
				.header("x-iws-part-number", partNumber)
				.header("x-iws-part-length", partLength)
				.header("x-iws-checksum", partCheckSum)
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.accept(APPLICATION_VND_FIELDSMART_UPLOAD_PART_1_XML)
				.content("testccontent".getBytes()))
				.andExpect(status().isOk())
				.andReturn();

		// ASSERT
		String contentAsString = mvcResult.getResponse().getContentAsString();
		String contentType = mvcResult.getResponse().getContentType();

		//        assertEquals("<UploadPartResponse><success>true</success><complete>true</complete><nextPart>1</nextPart></UploadPartResponse>", contentAsString);
		UploadPartResponse response = xmlMapper.readValue(contentAsString, UploadPartResponse.class);
		assertTrue(response.isSuccess());
		assertTrue(response.isComplete());
		assertEquals(stubResponse.getNextPart(), response.getNextPart());
		assertEquals(APPLICATION_VND_FIELDSMART_UPLOAD_PART_1_XML, contentType);
	}

	@Test
	void uploadPart_shouldInValidateUploadPartRequestAndRespond_whenAcceptHeaderInXml() throws Exception {
		// ARRANGE
		String filename = "test.gfpkg";
		String id = "ID";
		String partNumber = "1";
		String partLength = "10240";
		String partCheckSum = "checksumtestvalue";

		MvcResult mvcResult = this.mockMvc.perform(put("/files/" + filename + "/multipart/" + id)
				.header("x-iws-part-number", partNumber)
				.header("x-iws-part-length", partLength)
				.header("x-iws-checksum", partCheckSum)
				.contentType(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.content("testccontent".getBytes()))
				.andExpect(status().is4xxClientError())
				.andReturn();


	}
	
	@Test
	void uploadPart_shouldInValidateUploadPartRequestAndRespond_whenAcceptHeaderInJson() throws Exception {
		// ARRANGE
		String filename = "test.gfpkg";
		String id = "ID";
		String partNumber = "1";
		String partLength = "10240";
		String partCheckSum = "checksumtestvalue";

		// ACT
		MvcResult mvcResult = this.mockMvc.perform(put("/files/" + filename + "/multipart/" + id)
				.header("x-iws-part-number", partNumber)
				.header("x-iws-part-length", partLength)
				.header("x-iws-checksum", partCheckSum)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content("testccontent".getBytes()))
				.andExpect(status().is4xxClientError())
				.andReturn();

	}

	@Test
	void uploadPart_shouldValidateContentType_whenContentTypeInOctetStream() throws Exception {
		// ARRANGE
		String filename = "test.gfpkg";
		String id = "ID";
		String partNumber = "1";
		String partLength = "10240";
		String partCheckSum = "checksumtestvalue";
		UploadPartResponse stubResponse = new UploadPartResponse();
		stubResponse.setSuccess(true);
		stubResponse.setComplete(true);
		stubResponse.setNextPart(BigInteger.ONE);
		when(fileUploadController.receiveIntPart(any(), any(), anyInt(), anyLong(), any(), any())).thenReturn(stubResponse);

		// ACT
		MvcResult mvcResult = this.mockMvc.perform(put("/files/" + filename + "/multipart/" + id)
				.header("x-iws-part-number", partNumber)
				.header("x-iws-part-length", partLength)
				.header("x-iws-checksum", partCheckSum)
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.accept(APPLICATION_VND_FIELDSMART_UPLOAD_PART_1_JSON)
				.content("testccontent".getBytes()))
				.andExpect(status().isOk())
				.andReturn();

		// ASSERT
		String contentAsString = mvcResult.getResponse().getContentAsString();
		String contentType = mvcResult.getResponse().getContentType();
		//        assertEquals("<UploadPartResponse><success>true</success><complete>true</complete><nextPart>1</nextPart></UploadPartResponse>", contentAsString);
		UploadPartResponse response = jsonMapper.readValue(contentAsString, UploadPartResponse.class);
		assertTrue(response.isSuccess());
		assertTrue(response.isComplete());
		assertEquals(stubResponse.getNextPart(), response.getNextPart());
		assertEquals(APPLICATION_VND_FIELDSMART_UPLOAD_PART_1_JSON,contentType);
	}

	private UploadInitiateResponse getStubbedUploadInitiateResponse(String id, int totalSizeBytes) {
		UploadInitiateResponse response = new UploadInitiateResponse();
		response.setSuccess(true);
		response.setIdentifier(id);
		response.setMaxChunkSizeBytes(totalSizeBytes);
		response.setStartFromPart(BigInteger.ONE);
		return response;
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