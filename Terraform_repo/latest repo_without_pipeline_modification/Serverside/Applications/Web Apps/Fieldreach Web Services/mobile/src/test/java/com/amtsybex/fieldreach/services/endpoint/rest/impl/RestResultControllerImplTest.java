package com.amtsybex.fieldreach.services.endpoint.rest.impl;

import com.amtsybex.fieldreach.backend.model.*;
import com.amtsybex.fieldreach.backend.model.pk.NextStatusDefId;
import com.amtsybex.fieldreach.backend.service.ScriptResultsService;
import com.amtsybex.fieldreach.backend.service.ScriptService;
import com.amtsybex.fieldreach.backend.service.UserService;
import com.amtsybex.fieldreach.extract.core.ExtractEngine;
import com.amtsybex.fieldreach.services.download.FileDownloadController;
import com.amtsybex.fieldreach.services.endpoint.common.ActivityLogController;
import com.amtsybex.fieldreach.services.endpoint.common.ScriptController;
import com.amtsybex.fieldreach.services.endpoint.rest.impl.utils.HPCUsersUtil;
import com.amtsybex.fieldreach.services.messages.request.UpdateScriptResult;
import com.amtsybex.fieldreach.services.messages.response.*;
import com.amtsybex.fieldreach.utils.impl.Common;
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
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigInteger;
import java.util.List;

import static com.amtsybex.fieldreach.services.messages.response.CallResponse.APPLICATION_VND_FIELDSMART_CALL_1_XML;
import static com.amtsybex.fieldreach.services.messages.request.ScriptResultNote.APPLICATION_VND_FIELDSMART_SCRIPTRESULTNOTE_1_JSON;
import static com.amtsybex.fieldreach.services.messages.request.ScriptResultNote.APPLICATION_VND_FIELDSMART_SCRIPTRESULTNOTE_1_XML;
import static com.amtsybex.fieldreach.services.messages.request.UpdateScriptResult.APPLICATION_VND_FIELDSMART_UPDATESCRIPTRESULT_1_JSON;
import static com.amtsybex.fieldreach.services.messages.request.UpdateScriptResult.APPLICATION_VND_FIELDSMART_UPDATESCRIPTRESULT_1_XML;
import static com.amtsybex.fieldreach.services.messages.response.CallResponse.APPLICATION_VND_FIELDSMART_CALL_1_JSON;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class RestResultControllerImplTest {

    @Mock
    private ScriptController scriptController;

    @Mock
    private ActivityLogController activityLogController;

    @Mock
    private UserService userService;

    @Mock
    private ExtractEngine fieldreachExtractEngine;

    @Mock
    private FileDownloadController fileDownloadController;

    @Mock
    private ScriptResultsService scriptResultsService;

    @Mock
    private ScriptService scriptService;
    
    @Autowired
    private MappingJackson2HttpMessageConverter jackson2HttpMessageConverter;
    
    @Autowired
    private MappingJackson2XmlHttpMessageConverter jackson2XmlHttpMessageConverter;
    
    @Spy
    @Autowired
    @InjectMocks
    private RestResultControllerImpl restResultController;

    private final XmlMapper xmlMapper = new XmlMapper();
    
    private final ObjectMapper jsonMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(restResultController)
                .setMessageConverters(jackson2HttpMessageConverter, jackson2XmlHttpMessageConverter)
                .build();
    }

    @Test
    void searchScriptResults_shouldSearchAndGetScriptResults_whenAcceptHeaderInXml() throws Exception {
        // Arrange
        ReturnedScripts scripts = getStubbedReturnedScripts();
        when(scriptController.getScriptResults(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(List.of(scripts));
        when(scriptController.getOmitFromAHSearchList()).thenReturn(List.of("RandomCode"));
        doReturn(HPCUsersUtil.getUserDetails()).when(restResultController).getUserDetailsFromUserPrincipal(any());
       
        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/result/search?workOrderNo=1")
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();
        
        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        ScriptResultsSearchResponse response = xmlMapper.readValue(contentAsString, ScriptResultsSearchResponse.class);
        assertTrue(response.isSuccess());
        assertEquals(scripts.getAltEquipRef(), response.getScriptResultList().get(0).getAltEquipRef());
        assertEquals(scripts.getCompleteDate().toString(), response.getScriptResultList().get(0).getCompleteDate());
        assertEquals(scripts.getCompleteCode(), response.getScriptResultList().get(0).getCompleteCode());
        assertEquals(scripts.getResultStatus(), response.getScriptResultList().get(0).getResultStatus());
        assertEquals(scripts.getDeviceId(), response.getScriptResultList().get(0).getDeviceId());
        assertEquals(scripts.getEquipDesc(), response.getScriptResultList().get(0).getEquipDesc());
        assertEquals(scripts.getScriptCode(), response.getScriptResultList().get(0).getScriptCode());
    }

    @Test
    void searchScriptResults_shouldSearchAndGetScriptResults_whenAcceptHeaderInJson() throws Exception {
        // Arrange
        ReturnedScripts scripts = getStubbedReturnedScripts();
        when(scriptController.getScriptResults(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(List.of(scripts));
        when(scriptController.getOmitFromAHSearchList()).thenReturn(List.of("RandomCode"));
        doReturn(HPCUsersUtil.getUserDetails()).when(restResultController).getUserDetailsFromUserPrincipal(any());

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/result/search?workOrderNo=1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        ScriptResultsSearchResponse response = jsonMapper.readValue(contentAsString, ScriptResultsSearchResponse.class);
        assertTrue(response.isSuccess());
        assertEquals(scripts.getAltEquipRef(), response.getScriptResultList().get(0).getAltEquipRef());
        assertEquals(scripts.getCompleteDate().toString(), response.getScriptResultList().get(0).getCompleteDate());
        assertEquals(scripts.getCompleteCode(), response.getScriptResultList().get(0).getCompleteCode());
        assertEquals(scripts.getResultStatus(), response.getScriptResultList().get(0).getResultStatus());
        assertEquals(scripts.getDeviceId(), response.getScriptResultList().get(0).getDeviceId());
        assertEquals(scripts.getEquipDesc(), response.getScriptResultList().get(0).getEquipDesc());
        assertEquals(scripts.getScriptCode(), response.getScriptResultList().get(0).getScriptCode());
    }
    
    @Test
    void getScriptResult_shouldExtractScriptResultAndReturnResponse_whenAcceptHeaderInXml() throws Exception {
        // Arrange
        when(fieldreachExtractEngine.extractScriptResultAll(any(), anyInt(), any(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean())).thenReturn("Test");
        doReturn(HPCUsersUtil.getUserDetails()).when(restResultController).getUserDetailsFromUserPrincipal(any());

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/result/1")
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        GetScriptResultResponse response = xmlMapper.readValue(contentAsString, GetScriptResultResponse.class);
        assertTrue(response.isSuccess());
        assertEquals("VGVzdA==", response.getScriptResultSource());
        assertEquals("0CBC6611F5540BD0809A388DC95A615B", response.getChecksum());
        assertNull(response.getError());
        assertFalse(response.getEdit());

    }

    @Test
    void getScriptResult_shouldExtractScriptResultAndReturnResponse_whenAcceptHeaderInJson() throws Exception {
        // Arrange
        when(fieldreachExtractEngine.extractScriptResultAll(any(), anyInt(), any(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean())).thenReturn("Test");
        doReturn(HPCUsersUtil.getUserDetails()).when(restResultController).getUserDetailsFromUserPrincipal(any());

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/result/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        GetScriptResultResponse response = jsonMapper.readValue(contentAsString, GetScriptResultResponse.class);
        assertTrue(response.isSuccess());
        assertEquals("VGVzdA==", response.getScriptResultSource());
        assertEquals("0CBC6611F5540BD0809A388DC95A615B", response.getChecksum());
        assertNotNull(response.getError());
        assertFalse(response.getEdit());

    }
    
    @Test
    void getBinaryScriptResponse_shouldInitiateDownloadAndRespond_whenAcceptHeaderInXml() throws Exception {
        // Arrange
        InitiateDownloadResponse stubbedDownloadResponse = getStubbedDownloadResponse();
        when(fileDownloadController.initiateScriptResultsBlbDownload(any(), anyInt(), anyInt())).thenReturn(stubbedDownloadResponse);
        doReturn(HPCUsersUtil.getUserDetails()).when(restResultController).getUserDetailsFromUserPrincipal(any());

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/result/" + 1 + "/binaryresponse/" + 2)
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        InitiateDownloadResponse response = xmlMapper.readValue(contentAsString, InitiateDownloadResponse.class);
        assertTrue(response.isSuccess());
        assertEquals(stubbedDownloadResponse.isSuccess(), response.isSuccess());
        assertEquals(stubbedDownloadResponse.getIdentifier(), response.getIdentifier());
        assertEquals(stubbedDownloadResponse.getTotalParts(), response.getTotalParts());
        assertEquals(stubbedDownloadResponse.getChecksum(), response.getChecksum());
        assertEquals(stubbedDownloadResponse.getFileName(), response.getFileName());
    }

    @Test
    void getBinaryScriptResponse_shouldInitiateDownloadAndRespond_whenAcceptHeaderInJson() throws Exception {
        // Arrange
        InitiateDownloadResponse stubbedDownloadResponse = getStubbedDownloadResponse();
        when(fileDownloadController.initiateScriptResultsBlbDownload(any(), anyInt(), anyInt())).thenReturn(stubbedDownloadResponse);
        doReturn(HPCUsersUtil.getUserDetails()).when(restResultController).getUserDetailsFromUserPrincipal(any());

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/result/" + 1 + "/binaryresponse/" + 2)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        InitiateDownloadResponse response = jsonMapper.readValue(contentAsString, InitiateDownloadResponse.class);
        assertTrue(response.isSuccess());
        assertEquals(stubbedDownloadResponse.isSuccess(), response.isSuccess());
        assertEquals(stubbedDownloadResponse.getIdentifier(), response.getIdentifier());
        assertEquals(stubbedDownloadResponse.getTotalParts(), response.getTotalParts());
        assertEquals(stubbedDownloadResponse.getChecksum(), response.getChecksum());
        assertEquals(stubbedDownloadResponse.getFileName(), response.getFileName());
    }
    
    @Test
    void downloadBinaryScriptResponsePart_shouldDownloadPartAndRespond_whenAcceptHeaderInXml() throws Exception {
        // Arrange
        String identifier = "3";
        int part = 4;
        DownloadPartResponse stubbedDownloadPartResponse = getStubbedDownloadPartResponse(identifier, part);
        when(fileDownloadController.downloadPart(null, identifier, part)).thenReturn(stubbedDownloadPartResponse);

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/result/" + 1 + "/binaryresponse/" + 2 +"/multipart/" + identifier + "/" + part)
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        DownloadPartResponse response = xmlMapper.readValue(contentAsString, DownloadPartResponse.class);
        assertEquals(stubbedDownloadPartResponse.isSuccess(), response.isSuccess());
        assertEquals(stubbedDownloadPartResponse.getPartData(), response.getPartData());
        assertEquals(stubbedDownloadPartResponse.getIdentifier(), response.getIdentifier());
        assertEquals(stubbedDownloadPartResponse.getError(), response.getError());
    }

    @Test
    void downloadBinaryScriptResponsePart_shouldDownloadPartAndRespond_whenAcceptHeaderInJson() throws Exception {
        // Arrange
        String identifier = "3";
        int part = 4;
        DownloadPartResponse stubbedDownloadPartResponse = getStubbedDownloadPartResponse(identifier, part);
        when(fileDownloadController.downloadPart(null, identifier, part)).thenReturn(stubbedDownloadPartResponse);

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/result/" + 1 + "/binaryresponse/" + 2 +"/multipart/" + identifier + "/" + part)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        DownloadPartResponse response = jsonMapper.readValue(contentAsString, DownloadPartResponse.class);
        assertEquals(stubbedDownloadPartResponse.isSuccess(), response.isSuccess());
        assertEquals(stubbedDownloadPartResponse.getPartData(), response.getPartData());
        assertEquals(stubbedDownloadPartResponse.getIdentifier(), response.getIdentifier());
        assertEquals(stubbedDownloadPartResponse.getError(), response.getError());
    }
    
    @Test
    void getNextResultWorkflowStatus_shouldGetScriptAndGetNextStatusAndScriptStatus_whenAcceptHeaderInXml() throws Exception {
        // Arrange
        Integer returnId = 1;
        String status = "status";
        NextStatusDef nextStatusDef = new NextStatusDef();
        nextStatusDef.setId(new NextStatusDefId(1, 2, status));
        nextStatusDef.setNextStatusValue("next");
        ScriptVersions versions = new ScriptVersions();
        versions.setScriptCodeId(1);
        ScriptStatusDef statusDef = new ScriptStatusDef(1, 1);
        statusDef.setStatusValue("new");
        when(scriptService.findScriptVersion(any(), any())).thenReturn(versions);
        when(scriptResultsService.getReturnedScript(null, returnId)).thenReturn(new ReturnedScripts());
        when(scriptResultsService.getNextStatusDefByScriptCodeID(any(), any())).thenReturn(List.of(nextStatusDef));
        when(scriptResultsService.getScriptStatusDefByScriptCodeID(any(), anyInt())).thenReturn(List.of(statusDef));

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/result/" + returnId + "/status/" + status +"/next")
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertEquals("<NextResultStatusResponse><success>true</success><error/><resultStatusList><Status OrderNo=\"2\" System=\"0\">next</Status></resultStatusList></NextResultStatusResponse>", contentAsString);
        NextResultStatusResponse response = xmlMapper.readValue(contentAsString, NextResultStatusResponse.class);
        assertTrue(response.isSuccess());
        assertEquals("next", response.getResultStatusList().get(0).getStatus());
        assertEquals("2", response.getResultStatusList().get(0).getOrderNo());
        assertEquals("0", response.getResultStatusList().get(0).getSystem());
    }

    @Test
    void getNextResultWorkflowStatus_shouldGetScriptAndGetNextStatusAndScriptStatus_whenAcceptHeaderInJson() throws Exception {
        // Arrange
        Integer returnId = 1;
        String status = "status";
        NextStatusDef nextStatusDef = new NextStatusDef();
        nextStatusDef.setId(new NextStatusDefId(1, 2, status));
        nextStatusDef.setNextStatusValue("next");
        ScriptVersions versions = new ScriptVersions();
        versions.setScriptCodeId(1);
        ScriptStatusDef statusDef = new ScriptStatusDef(1, 1);
        statusDef.setStatusValue("new");
        when(scriptService.findScriptVersion(any(), any())).thenReturn(versions);
        when(scriptResultsService.getReturnedScript(null, returnId)).thenReturn(new ReturnedScripts());
        when(scriptResultsService.getNextStatusDefByScriptCodeID(any(), any())).thenReturn(List.of(nextStatusDef));
        when(scriptResultsService.getScriptStatusDefByScriptCodeID(any(), anyInt())).thenReturn(List.of(statusDef));

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/result/" + returnId + "/status/" + status +"/next")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        NextResultStatusResponse response = jsonMapper.readValue(contentAsString, NextResultStatusResponse.class);
        assertTrue(response.isSuccess());
        assertEquals("next", response.getResultStatusList().get(0).getStatus());
        assertEquals("2", response.getResultStatusList().get(0).getOrderNo());
        assertEquals("0", response.getResultStatusList().get(0).getSystem());
    }
    
    @Test
    void updateScriptResultStatus_shouldUpdateResultStatus_whenAcceptHeaderInXml() throws Exception {
        // Arrange
        int returnId = 1;
        String status = "status";
        
        // Act
        MvcResult mvcResult = this.mockMvc.perform(put("/result/" + returnId + "/status/" + status)
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
        verify(scriptResultsService, times(1)).updateResultStatus(null, returnId, status, null);
    }

    @Test
    void updateScriptResultStatus_shouldUpdateResultStatus_whenAcceptHeaderInJson() throws Exception {
        // Arrange
        int returnId = 1;
        String status = "status";

        // Act
        MvcResult mvcResult = this.mockMvc.perform(put("/result/" + returnId + "/status/" + status)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        CallResponse response = jsonMapper.readValue(contentAsString, CallResponse.class);
        assertTrue(response.isSuccess());
        assertNotNull(response.getError());
        verify(scriptResultsService, times(1)).updateResultStatus(null, returnId, status, null);
    }
    
    @Test
    void updateScriptResultResponse_shouldUpdateResultResponse_whenAcceptHeaderInXml() throws Exception {
        // Arrange
        int returnId = 1;
        int resOrderNo = 2;
        String body = this.buildRequestBody(resOrderNo);

        // Act
        MvcResult mvcResult = this.mockMvc.perform(put("/result/" + returnId + "/response/" + resOrderNo)
                .content(body)
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
        verify(scriptController, times(1)).updateResultResponse(any(), anyInt(), anyInt(), any(UpdateScriptResult.class));
    }

    @Test
    void updateScriptResultResponse_shouldUpdateResultResponse_whenAcceptHeaderInJson() throws Exception {
        // Arrange
        int returnId = 1;
        int resOrderNo = 2;
        UpdateScriptResult request = new UpdateScriptResult();
        request.setUSERCODE("FWS");
        com.amtsybex.fieldreach.services.messages.types.Item value = new com.amtsybex.fieldreach.services.messages.types.Item();
        value.setDATE("12345678");
        value.setFREETEXT("free text");
        value.setResOrderNo(2);
        value.setSeqNo(1);
        value.setPREV("Prev");
        value.setRESPONSEFILENAME("test.xml");
        value.setTIME("123456");
        request.setITEM(value);
        
        // Act
        MvcResult mvcResult = this.mockMvc.perform(put("/result/" + returnId + "/response/" + resOrderNo)
                .content(jsonMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        CallResponse response = jsonMapper.readValue(contentAsString, CallResponse.class);
        assertTrue(response.isSuccess());
        assertNotNull(response.getError());
        verify(scriptController, times(1)).updateResultResponse(any(), anyInt(), anyInt(), any(UpdateScriptResult.class));
    }
    
    @Test
    void createResultNote_shouldGetScriptAndCreateNote_whenAcceptHeaderInXml() throws Exception {
        // Arrange
        int returnId = 1;
        String body = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
        "<ScriptResultNote>" +
        "<noteText>Test Note</noteText>" + 
        "<sequenceNumber>2</sequenceNumber>" +
        "<resOrderNo>1</resOrderNo>" +
        "</ScriptResultNote>";

        ReturnedScripts returnedScripts = new ReturnedScripts();
        returnedScripts.setScriptId(10);
        when(scriptResultsService.getReturnedScript(null, returnId)).thenReturn(returnedScripts);
        when(scriptService.findScriptItem(any(), anyInt(), anyInt())).thenReturn(new Item());

        // Act
        MvcResult mvcResult = this.mockMvc.perform(post("/result/" + returnId + "/note")
                .content(body)
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
        verify(scriptResultsService, times(1)).createResultNote(any(), any(ResultNotes.class));
    }
    
    @Test
    void createResultNote_shouldReturnValidCOntentType_whenRequestContentTyeInXml() throws Exception {
        // Arrange
        int returnId = 1;
        String body = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
        "<ScriptResultNote>" +
        "<noteText>Test Note</noteText>" + 
        "<sequenceNumber>2</sequenceNumber>" +
        "<resOrderNo>1</resOrderNo>" +
        "</ScriptResultNote>";

        ReturnedScripts returnedScripts = new ReturnedScripts();
        returnedScripts.setScriptId(10);
        when(scriptResultsService.getReturnedScript(null, returnId)).thenReturn(returnedScripts);
        when(scriptService.findScriptItem(any(), anyInt(), anyInt())).thenReturn(new Item());

        // Act
        MvcResult mvcResult = this.mockMvc.perform(post("/result/" + returnId + "/note")
                .content(body)
                .contentType(APPLICATION_VND_FIELDSMART_SCRIPTRESULTNOTE_1_XML)
                .accept(APPLICATION_VND_FIELDSMART_CALL_1_XML))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        String contentType = mvcResult.getResponse().getContentType();
        assertEquals(APPLICATION_VND_FIELDSMART_CALL_1_XML,contentType );
        assertEquals("<CallResponse><error/><success>true</success></CallResponse>", contentAsString);
        CallResponse response = xmlMapper.readValue(contentAsString, CallResponse.class);
        assertTrue(response.isSuccess());
        assertNull(response.getError());
        verify(scriptResultsService, times(1)).createResultNote(any(), any(ResultNotes.class));
    }

    @Test
    void createResultNote_shouldGetScriptAndCreateNote_whenAcceptHeaderInJson() throws Exception {
        // Arrange
        int returnId = 1;
        String body = "{\n" +
                "    \"noteText\": \"Test Note\",\n" +
                "    \"sequenceNumber\": \"2\",\n" +
                "    \"resOrderNo\": \"1\"\n" +
                "  }";

        ReturnedScripts returnedScripts = new ReturnedScripts();
        returnedScripts.setScriptId(10);
        when(scriptResultsService.getReturnedScript(null, returnId)).thenReturn(returnedScripts);
        when(scriptService.findScriptItem(any(), anyInt(), anyInt())).thenReturn(new Item());

        // Act
        MvcResult mvcResult = this.mockMvc.perform(post("/result/" + returnId + "/note")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        CallResponse response = jsonMapper.readValue(contentAsString, CallResponse.class);
        assertTrue(response.isSuccess());
        assertNotNull(response.getError());
        verify(scriptResultsService, times(1)).createResultNote(any(), any(ResultNotes.class));
    }
    
    @Test
    void createResultNote_shouldReturnValidCOntentType_whenRequestContentTyeInJson() throws Exception {
        // Arrange
        int returnId = 1;
        String body = "{\n" +
                "    \"noteText\": \"Test Note\",\n" +
                "    \"sequenceNumber\": \"2\",\n" +
                "    \"resOrderNo\": \"1\"\n" +
                "  }";

        ReturnedScripts returnedScripts = new ReturnedScripts();
        returnedScripts.setScriptId(10);
        when(scriptResultsService.getReturnedScript(null, returnId)).thenReturn(returnedScripts);
        when(scriptService.findScriptItem(any(), anyInt(), anyInt())).thenReturn(new Item());

        // Act
        MvcResult mvcResult = this.mockMvc.perform(post("/result/" + returnId + "/note")
                .content(body)
                .contentType(APPLICATION_VND_FIELDSMART_SCRIPTRESULTNOTE_1_JSON)
                .accept(APPLICATION_VND_FIELDSMART_CALL_1_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        String contentType = mvcResult.getResponse().getContentType();
        assertEquals(APPLICATION_VND_FIELDSMART_CALL_1_JSON, contentType);
        CallResponse response = jsonMapper.readValue(contentAsString, CallResponse.class);
        assertTrue(response.isSuccess());
        assertNotNull(response.getError());
        verify(scriptResultsService, times(1)).createResultNote(any(), any(ResultNotes.class));
    }

    private InitiateDownloadResponse getStubbedDownloadResponse() {
        InitiateDownloadResponse initiateDownloadResponse = new InitiateDownloadResponse();
        initiateDownloadResponse.setChecksum("87621");
        initiateDownloadResponse.setTotalParts(BigInteger.TEN);
        initiateDownloadResponse.setSuccess(true);
        initiateDownloadResponse.setFileName("test.xml");
        initiateDownloadResponse.setIdentifier("id");
        return initiateDownloadResponse;
    }
    
    @Test
    void updateScriptResultResponse_shouldReturnResponseInXML_whenContentTypeInXml() throws Exception {
        // Arrange
        int returnId = 1;
        int resOrderNo = 2;
        String body = this.buildRequestBody(resOrderNo);

        // Act
        MvcResult mvcResult = this.mockMvc.perform(put("/result/" + returnId + "/response/" + resOrderNo)
                .content(body)
                .contentType(APPLICATION_VND_FIELDSMART_UPDATESCRIPTRESULT_1_XML)
                .accept(APPLICATION_VND_FIELDSMART_CALL_1_XML))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertEquals("<CallResponse><error/><success>true</success></CallResponse>", contentAsString);
        CallResponse response = xmlMapper.readValue(contentAsString, CallResponse.class);
        assertTrue(response.isSuccess());
        assertNull(response.getError());
        verify(scriptController, times(1)).updateResultResponse(any(), anyInt(), anyInt(), any(UpdateScriptResult.class));
    }
    
    
    @Test
    void updateScriptResultResponse_shouldReturnResponseInJSON_whenContentTypeInJson() throws Exception {
        // Arrange
        int returnId = 1;
        int resOrderNo = 2;
        UpdateScriptResult request = new UpdateScriptResult();
        request.setUSERCODE("FWS");
        com.amtsybex.fieldreach.services.messages.types.Item value = new com.amtsybex.fieldreach.services.messages.types.Item();
        value.setDATE("12345678");
        value.setFREETEXT("free text");
        value.setResOrderNo(2);
        value.setSeqNo(1);
        value.setPREV("Prev");
        value.setRESPONSEFILENAME("test.xml");
        value.setTIME("123456");
        request.setITEM(value);
        
        // Act
        MvcResult mvcResult = this.mockMvc.perform(put("/result/" + returnId + "/response/" + resOrderNo)
                .content(jsonMapper.writeValueAsString(request))
                .contentType(APPLICATION_VND_FIELDSMART_UPDATESCRIPTRESULT_1_JSON)
                .accept(APPLICATION_VND_FIELDSMART_CALL_1_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        String contentType = mvcResult.getResponse().getContentType();
        assertEquals(contentType, APPLICATION_VND_FIELDSMART_CALL_1_JSON);
        CallResponse response = jsonMapper.readValue(contentAsString, CallResponse.class);
        assertTrue(response.isSuccess());
        assertNotNull(response.getError());
        verify(scriptController, times(1)).updateResultResponse(any(), anyInt(), anyInt(), any(UpdateScriptResult.class));
    }

    private ReturnedScripts getStubbedReturnedScripts() {
        ReturnedScripts scripts = new ReturnedScripts();
        scripts.setScriptId(1);
        scripts.setAltEquipRef("altref");
        scripts.setCompleteCode("code");
        scripts.setCompleteDate(100000);
        scripts.setScriptStatus("Status");
        scripts.setDeviceId("DevId");
        scripts.setEquipDesc("Desc");
        scripts.setId(100);
        scripts.setScriptCode("SCode");
        return scripts;
    }

    private DownloadPartResponse getStubbedDownloadPartResponse(String identifier, int part) {
        DownloadPartResponse downloadPartResponse = new DownloadPartResponse();
        downloadPartResponse.setIdentifier(identifier);
        downloadPartResponse.setPartData(String.valueOf(part));
        downloadPartResponse.setSuccess(true);
        downloadPartResponse.setError(null);
        return downloadPartResponse;
    }

    private String buildRequestBody(int resOrderNo) {

        return "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + "<UpdateScriptResult>" +
                "<ITEM SeqNo=\"" + 1 + "\" ResOrderNo=\"" + resOrderNo + "\">" +
                "<TIME>" + Common.generateFieldreachDBTime() + "</TIME>" + 
                "<DATE>" + Common.generateFieldreachDBDate() + "</DATE>" +
                "<FREETEXT>" + "freetext" + "</FREETEXT>" +
                "<UOM/><PREV/><RESPONSEFILE/><RESPONSEFILENAME/>" +
                "<RESPONSE Type=\"OK\">" + "a response" + "</RESPONSE>" +
                "</ITEM>" +
                "<USERCODE>FWS</USERCODE>" +
                "</UpdateScriptResult>";
    }

}