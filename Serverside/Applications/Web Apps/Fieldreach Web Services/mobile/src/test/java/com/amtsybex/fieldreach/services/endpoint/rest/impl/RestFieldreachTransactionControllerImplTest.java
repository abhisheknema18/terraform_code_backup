package com.amtsybex.fieldreach.services.endpoint.rest.impl;

import static com.amtsybex.fieldreach.services.messages.request.FRTransaction.APPLICATION_VND_FIELDSMART_TRANSACTION_1_JSON;
import static com.amtsybex.fieldreach.services.messages.request.FRTransaction.APPLICATION_VND_FIELDSMART_TRANSACTION_1_XML;
import static com.amtsybex.fieldreach.services.messages.response.CallResponse.APPLICATION_VND_FIELDSMART_CALL_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.CallResponse.APPLICATION_VND_FIELDSMART_CALL_1_XML;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.math.BigInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.instance.InstanceManager;
import com.amtsybex.fieldreach.backend.model.WorkIssued;
import com.amtsybex.fieldreach.backend.model.WorkStatusHistory;
import com.amtsybex.fieldreach.backend.service.FieldreachTransactionService;
import com.amtsybex.fieldreach.backend.service.UserService;
import com.amtsybex.fieldreach.services.endpoint.common.WorkOrderController;
import com.amtsybex.fieldreach.services.endpoint.common.WorkStatus;
import com.amtsybex.fieldreach.services.endpoint.common.WorkStatus.WORKSTATUSDESIGNATION;
import com.amtsybex.fieldreach.services.jms.FieldreachTransactionJMSController;
import com.amtsybex.fieldreach.services.messages.request.FRTransaction;
import com.amtsybex.fieldreach.services.messages.response.CallResponse;
import com.amtsybex.fieldreach.services.messages.types.TransType;
import com.amtsybex.fieldreach.services.messages.types.TransactionItem;
import com.amtsybex.fieldreach.services.utils.Utils;
import com.amtsybex.fieldreach.utils.impl.Common;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class RestFieldreachTransactionControllerImplTest {

	@MockBean
    private UserService userService;
    
	@MockBean
    private InstanceManager instanceManager;
	@MockBean
	private FieldreachTransactionService fieldreachTransactionService;
	@MockBean
	private FieldreachTransactionJMSController fieldreachTransactionJMSController;
	@MockBean
	private WorkOrderController workOrderController;
	@MockBean
	private WorkStatus workStatuses;

    @Autowired
    private RestFieldreachTransactionControllerImpl transactionController;

    private final XmlMapper xmlMapper = new XmlMapper();

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
    	
        this.mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();
        
    }
    
    @Test
    void processTransaction_shouldValidateRequestBodyAndProcessTransaction_whenRequestAcceptHeaderInXml() throws Exception {
    	
        // Arrange
        String requestBody = getFRTransactionXML();
        doNothing().when(userService).updateUserActivityStatus(any(), any());
        
        // Act
        MvcResult mvcResult = this.mockMvc.perform(post("/processTransaction")
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML)
                .header(Utils.APPCODE_HEADER, "FWS")
                .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        CallResponse response = xmlMapper.readValue(mvcResult.getResponse().getContentAsString(), CallResponse.class);
        assertTrue(response.isSuccess());
        assertNull(response.getError().getErrorCode());
        verify(userService, times(1)).updateUserLocationHistory(any(), any());
        verify(userService, times(1)).updateUserActivityStatus(any(), any());
        
    }
    
    @Test
    void processTransaction_shouldReturnValidContents_whenRequestContentTypeInFusionXMl() throws Exception {
        // Arrange
        String requestBody = getFRTransactionXML();
        
        doNothing().when(userService).updateUserActivityStatus(any(), any());
        doNothing().when(userService).updateUserLocationHistory(any(), any());
        
        // Act
        MvcResult mvcResult = this.mockMvc.perform(post("/processTransaction")
                .contentType(APPLICATION_VND_FIELDSMART_TRANSACTION_1_XML)
                .accept(APPLICATION_VND_FIELDSMART_CALL_1_XML)
                .header(Utils.APPCODE_HEADER, "FWS")
                .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        CallResponse response = xmlMapper.readValue(mvcResult.getResponse().getContentAsString(), CallResponse.class);
        String contentType = mvcResult.getResponse().getContentType();
        assertTrue(response.isSuccess());
        assertNull(response.getError().getErrorCode());
        assertEquals(APPLICATION_VND_FIELDSMART_CALL_1_XML,contentType);
        verify(userService, times(1)).updateUserLocationHistory(any(), any());
        verify(userService, times(1)).updateUserActivityStatus(any(), any());
        
    }

    @Test
    void processTransaction_shouldValidateRequestBodyAndProcessTransaction_whenRequestAcceptHeaderInJson() throws Exception {
    	
        // Arrange
    	FRTransaction  trans = this.getTransaction(Utils.TRANSACTION_HEARTBEAT, "device1", "user1", null, null, null, null, "53", "-6");
        
    	doNothing().when(userService).updateUserLocationHistory(any(), any());
        doNothing().when(userService).updateUserActivityStatus(any(), any());

        // Act
        MvcResult mvcResult = this.mockMvc.perform(post("/processTransaction?transactionFileName=test.xml")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(Utils.APPCODE_HEADER, "FWS")
                .content(jacksonObjectMapper.writeValueAsString(trans)))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        CallResponse response = jacksonObjectMapper.readValue(mvcResult.getResponse().getContentAsString(), CallResponse.class);
        assertTrue(response.isSuccess());
        assertNull(response.getError().getErrorCode());
        verify(userService, times(1)).updateUserActivityStatus(any(), any());
        verify(userService, times(1)).updateUserLocationHistory(any(), any());
        
    }
    
    @Test
    void processTransaction_shouldReturnValidContents_whenRequestContentTypeInFusionJson() throws Exception {
        
    	// Arrange
    	FRTransaction  trans = this.getTransaction(Utils.TRANSACTION_HEARTBEAT, "device1", "user1", null, null, null, null, "53", "-6");
        
    	doNothing().when(userService).updateUserLocationHistory(any(), any());
        doNothing().when(userService).updateUserActivityStatus(any(), any());

        // Act
        MvcResult mvcResult = this.mockMvc.perform(post("/processTransaction?transactionFileName=test.xml")
                .contentType(APPLICATION_VND_FIELDSMART_TRANSACTION_1_JSON)
                .accept(APPLICATION_VND_FIELDSMART_CALL_1_JSON)
                .header(Utils.APPCODE_HEADER, "FWS")
                .content(jacksonObjectMapper.writeValueAsString(trans)))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        CallResponse response = jacksonObjectMapper.readValue(mvcResult.getResponse().getContentAsString(), CallResponse.class);
        String contentTypes = mvcResult.getResponse().getContentType();
        assertEquals(APPLICATION_VND_FIELDSMART_CALL_1_JSON, contentTypes);
        assertTrue(response.isSuccess());
        assertNull(response.getError().getErrorCode());
        verify(userService, times(1)).updateUserLocationHistory(any(), any());
        verify(userService, times(1)).updateUserActivityStatus(any(), any());
        
    }
    
    
    @Test
    void processTransaction_shouldReturnOK_whenHeartbeatTransaction() throws Exception {

        // Arrange
    	FRTransaction  trans = this.getTransaction(Utils.TRANSACTION_HEARTBEAT, "device1", "user1", null, null, null, null, "53", "-6");

    	doNothing().when(userService).updateUserActivityStatus(any(), any());
    	doNothing().when(userService).updateUserLocationHistory(any(), any());

        // Act
        MvcResult mvcResult = this.mockMvc.perform(post("/processTransaction")
                .contentType(APPLICATION_VND_FIELDSMART_TRANSACTION_1_JSON)
                .accept(APPLICATION_VND_FIELDSMART_CALL_1_JSON)
                .header(Utils.APPCODE_HEADER, "FWS")
                .content(jacksonObjectMapper.writeValueAsString(trans)))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        CallResponse response = jacksonObjectMapper.readValue(mvcResult.getResponse().getContentAsString(), CallResponse.class);
        String contentTypes = mvcResult.getResponse().getContentType();
        assertEquals(APPLICATION_VND_FIELDSMART_CALL_1_JSON, contentTypes);
        assertTrue(response.isSuccess());
        assertNull(response.getError().getErrorCode());
        verify(userService, times(1)).updateUserLocationHistory(any(), any());
        verify(userService, times(1)).updateUserActivityStatus(any(), any());

    }   
    
    @Test
    void processTransaction_shouldReturnOKNoLocationUpdate_whenHeartbeatTransactionHasNoLocation() throws Exception {

        // Arrange
    	FRTransaction  trans = this.getTransaction(Utils.TRANSACTION_HEARTBEAT, "device1", "user1", null, null, null, null, null, null);

    	doNothing().when(userService).updateUserActivityStatus(any(), any());
    	doNothing().when(userService).updateUserLocationHistory(any(), any());

        // Act 
    	MvcResult mvcResult = this.mockMvc.perform(post("/processTransaction")
                .contentType(APPLICATION_VND_FIELDSMART_TRANSACTION_1_JSON)
                .accept(APPLICATION_VND_FIELDSMART_CALL_1_JSON)
                .header(Utils.APPCODE_HEADER, "FWS")
                .content(jacksonObjectMapper.writeValueAsString(trans)))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        CallResponse response = jacksonObjectMapper.readValue(mvcResult.getResponse().getContentAsString(), CallResponse.class);
        String contentTypes = mvcResult.getResponse().getContentType();
        assertEquals(APPLICATION_VND_FIELDSMART_CALL_1_JSON, contentTypes);
        assertTrue(response.isSuccess());
        assertNull(response.getError().getErrorCode());
        verify(userService, times(0)).updateUserLocationHistory(any(), any());
        verify(userService, times(1)).updateUserActivityStatus(any(), any());
        
    }
    
    @Test
    void processTransaction_shouldReturnOKNoLocationUpdate_whenHeartbeatTransactionHasNoLat() throws Exception {

        // Arrange
    	FRTransaction  trans = this.getTransaction(Utils.TRANSACTION_HEARTBEAT, "device1", "user1", null, null, null, null, null, "-6");

    	doNothing().when(userService).updateUserActivityStatus(any(), any());
    	doNothing().when(userService).updateUserLocationHistory(any(), any());

        // Act 
    	MvcResult mvcResult = this.mockMvc.perform(post("/processTransaction")
                .contentType(APPLICATION_VND_FIELDSMART_TRANSACTION_1_JSON)
                .accept(APPLICATION_VND_FIELDSMART_CALL_1_JSON)
                .header(Utils.APPCODE_HEADER, "FWS")
                .content(jacksonObjectMapper.writeValueAsString(trans)))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        CallResponse response = jacksonObjectMapper.readValue(mvcResult.getResponse().getContentAsString(), CallResponse.class);
        String contentTypes = mvcResult.getResponse().getContentType();
        assertEquals(APPLICATION_VND_FIELDSMART_CALL_1_JSON, contentTypes);
        assertTrue(response.isSuccess());
        assertNull(response.getError().getErrorCode());
        verify(userService, times(0)).updateUserLocationHistory(any(), any());
        verify(userService, times(1)).updateUserActivityStatus(any(), any());

    }
    
    @Test
    void processTransaction_shouldReturnOKNoLocationUpdate_whenHeartbeatTransactionHasNoLon() throws Exception {

        // Arrange
    	FRTransaction  trans = this.getTransaction(Utils.TRANSACTION_HEARTBEAT, "device1", "user1", null, null, null, null, "53", null);

    	doNothing().when(userService).updateUserActivityStatus(any(), any());
    	doNothing().when(userService).updateUserLocationHistory(any(), any());

        // Act 
    	MvcResult mvcResult = this.mockMvc.perform(post("/processTransaction")
                .contentType(APPLICATION_VND_FIELDSMART_TRANSACTION_1_JSON)
                .accept(APPLICATION_VND_FIELDSMART_CALL_1_JSON)
                .header(Utils.APPCODE_HEADER, "FWS")
                .content(jacksonObjectMapper.writeValueAsString(trans)))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        CallResponse response = jacksonObjectMapper.readValue(mvcResult.getResponse().getContentAsString(), CallResponse.class);
        String contentTypes = mvcResult.getResponse().getContentType();
        assertEquals(APPLICATION_VND_FIELDSMART_CALL_1_JSON, contentTypes);
        assertTrue(response.isSuccess());
        assertNull(response.getError().getErrorCode());
        verify(userService, times(0)).updateUserLocationHistory(any(), any());
        verify(userService, times(1)).updateUserActivityStatus(any(), any());

    }
    
    @Test
    void processTransaction_shouldReturnOK_whenWorkStatusTransaction() throws Exception {
    	
        // Arrange
    	FRTransaction  trans = this.getTransaction(Utils.TRANSACTION_WORKSTATUS, "device1", "user1", "D1", "WO1", "IN PROGRESS", null, null, null);
	
    	doNothing().when(userService).updateUserActivityStatus(any(), any());
    	
    	doNothing().when(workOrderController).saveWorkIssued(any(), any());
    	doNothing().when(workOrderController).saveWorkStatusHistory(any(), any());
    	
    	doReturn(this.workStatuses).when(this.workOrderController).getWorkStatuses();
    	
    	doReturn(this.getWorkIssued("WO1", "D1", "ISSUED")).when(workOrderController).getWorkIssuedRecord(any(), any(), any());
    	
    	doReturn(false).when(workStatuses).statusIsDesignated(any(), any(), eq(WORKSTATUSDESIGNATION.CLOSED));
    	doReturn(false).when(workStatuses).statusIsDesignated(any(), any(), eq(WORKSTATUSDESIGNATION.CANTDO));
    	doReturn(false).when(workStatuses).statusIsDesignated(any(), any(), eq(WORKSTATUSDESIGNATION.OPERATIONAL));
    	
    	doReturn(true).when(workStatuses).isStatusConfigured(any(), eq("IN PROGRESS"));
    	
        // Act
        MvcResult mvcResult = this.mockMvc.perform(post("/processTransaction")
                .contentType(APPLICATION_VND_FIELDSMART_TRANSACTION_1_JSON)
                .accept(APPLICATION_VND_FIELDSMART_CALL_1_JSON)
                .header(Utils.APPCODE_HEADER, "FWS")
                .content(jacksonObjectMapper.writeValueAsString(trans)))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        CallResponse response = jacksonObjectMapper.readValue(mvcResult.getResponse().getContentAsString(), CallResponse.class);
        String contentTypes = mvcResult.getResponse().getContentType();
        assertEquals(APPLICATION_VND_FIELDSMART_CALL_1_JSON, contentTypes);
        assertTrue(response.isSuccess());
        assertNull(response.getError().getErrorCode());

        verify(workOrderController, times(1)).saveWorkIssued(any(), any());
        verify(workOrderController, times(1)).saveWorkStatusHistory(any(), any());
        
        verify(userService, times(1)).updateUserActivityStatus(any(), any());
        verify(userService, times(0)).updateUserLocationHistory(any(), any());
    }
    
    @Test
    void processTransaction_shouldProcessHeartBeatAndReturnOK_whenWorkStatusContainsLatLonTransaction() throws Exception {

        // Arrange
    	FRTransaction trans = this.getTransaction(Utils.TRANSACTION_WORKSTATUS, "device1", "user1", "D1", "WO1", "IN PROGRESS", null, "53", "-6");
       
    	doNothing().when(userService).updateUserActivityStatus(any(), any());
    	doNothing().when(workOrderController).saveWorkIssued(any(), any());
    	doNothing().when(workOrderController).saveWorkStatusHistory(any(), any());
    	
    	doReturn(this.workStatuses).when(this.workOrderController).getWorkStatuses();
    	
    	doReturn(this.getWorkIssued("WO1", "D1", "ISSUED")).when(workOrderController).getWorkIssuedRecord(any(), any(), any());
    	
    	doReturn(false).when(workStatuses).statusIsDesignated(any(), any(), eq(WORKSTATUSDESIGNATION.CLOSED));
    	doReturn(false).when(workStatuses).statusIsDesignated(any(), any(), eq(WORKSTATUSDESIGNATION.CANTDO));
    	doReturn(false).when(workStatuses).statusIsDesignated(any(), any(), eq(WORKSTATUSDESIGNATION.OPERATIONAL));
    	
    	doReturn(true).when(workStatuses).isStatusConfigured(any(), eq("IN PROGRESS"));

        // Act
        MvcResult mvcResult = this.mockMvc.perform(post("/processTransaction")
                .contentType(APPLICATION_VND_FIELDSMART_TRANSACTION_1_JSON)
                .accept(APPLICATION_VND_FIELDSMART_CALL_1_JSON)
                .header(Utils.APPCODE_HEADER, "FWS")
                .content(jacksonObjectMapper.writeValueAsString(trans)))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        CallResponse response = jacksonObjectMapper.readValue(mvcResult.getResponse().getContentAsString(), CallResponse.class);
        String contentTypes = mvcResult.getResponse().getContentType();
        assertEquals(APPLICATION_VND_FIELDSMART_CALL_1_JSON, contentTypes);
        assertTrue(response.isSuccess());
        assertNull(response.getError().getErrorCode());

        verify(workOrderController, times(1)).saveWorkIssued(any(), any());
        verify(workOrderController, times(1)).saveWorkStatusHistory(any(), any());
        
        verify(userService, times(1)).updateUserActivityStatus(any(), any());
        verify(userService, times(1)).updateUserLocationHistory(any(), any());

    }
    
    @Test
    void processTransaction_shouldReturnUnknownTransactionType_whenBadTransTypeSent() throws Exception {
       
    	// Arrange
    	FRTransaction trans = this.getTransaction("BADTRANS", "device1", "user1", "D1", "WO1", "IN PROGRESS", null, "53", "-6");
       
        // Act
        MvcResult mvcResult = this.mockMvc.perform(post("/processTransaction")
                .contentType(APPLICATION_VND_FIELDSMART_TRANSACTION_1_JSON)
                .accept(APPLICATION_VND_FIELDSMART_CALL_1_JSON)
                .header(Utils.APPCODE_HEADER, "FWS")
                .content(jacksonObjectMapper.writeValueAsString(trans)))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        CallResponse response = jacksonObjectMapper.readValue(mvcResult.getResponse().getContentAsString(), CallResponse.class);
        String contentTypes = mvcResult.getResponse().getContentType();
        assertEquals(APPLICATION_VND_FIELDSMART_CALL_1_JSON, contentTypes);
        assertFalse(response.isSuccess());
        assertEquals("UnknownTransactionTypeException", response.getError().getErrorCode());

    }
    
    @Test
    void processTransaction_shouldReturnBadRequest_whenNoTransType() throws Exception {
        
    	// Arrange
    	FRTransaction trans = this.getTransaction(null, "device1", "user1", "D1", "WO1", "IN PROGRESS", null, "53", "-6");
       
    	//Act & Assert
        this.mockMvc.perform(post("/processTransaction")
                .contentType(APPLICATION_VND_FIELDSMART_TRANSACTION_1_JSON)
                .accept(APPLICATION_VND_FIELDSMART_CALL_1_JSON)
                .header(Utils.APPCODE_HEADER, "FWS")
                .content(jacksonObjectMapper.writeValueAsString(trans)))
                .andExpect(status().isBadRequest())
                .andReturn();

    }
    
    @Test
    void processTransaction_shouldReturnBadRequest_whenMissingAppCode() throws Exception {
        
    	// Arrange
    	FRTransaction trans = this.getTransaction(Utils.TRANSACTION_WORKSTATUS, "device1", "user1", "D1", "WO1", "IN PROGRESS", null, "53", "-6");
       
    	//Act & Assert
        this.mockMvc.perform(post("/processTransaction")
                .contentType(APPLICATION_VND_FIELDSMART_TRANSACTION_1_JSON)
                .accept(APPLICATION_VND_FIELDSMART_CALL_1_JSON)
                .content(jacksonObjectMapper.writeValueAsString(trans)))
                .andExpect(status().isBadRequest())
                .andReturn();
    }
    
    @Test
    void processTransaction_shouldReturnBadRequest_whenMissingTransactionBody() throws Exception {
    	
    	//Act & Assert
        this.mockMvc.perform(post("/processTransaction")
                .contentType(APPLICATION_VND_FIELDSMART_TRANSACTION_1_JSON)
                .accept(APPLICATION_VND_FIELDSMART_CALL_1_JSON)
                .header(Utils.APPCODE_HEADER, "FWS")
                .content("".getBytes()))
                .andExpect(status().isBadRequest())
                .andReturn();
    }
  
    @Test
    void processTransaction_shouldReturnOKWithError_whenFRInstanceException() throws Exception {
    	
        // Arrange
    	FRTransaction trans = this.getTransaction(Utils.TRANSACTION_WORKSTATUS, "device1", "user1", "D1", "WO1", "IN PROGRESS", null, "53", "-6");
       
    	doNothing().when(userService).updateUserActivityStatus(any(), any());
    	doThrow(new FRInstanceException("fr instance")).when(workOrderController).getWorkIssuedRecord(any(), any(), any());

        // Act
        MvcResult mvcResult = this.mockMvc.perform(post("/processTransaction")
                .contentType(APPLICATION_VND_FIELDSMART_TRANSACTION_1_JSON)
                .accept(APPLICATION_VND_FIELDSMART_CALL_1_JSON)
                .header(Utils.APPCODE_HEADER, "FWS")
                .content(jacksonObjectMapper.writeValueAsString(trans)))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        CallResponse response = jacksonObjectMapper.readValue(mvcResult.getResponse().getContentAsString(), CallResponse.class);
        String contentTypes = mvcResult.getResponse().getContentType();
        assertEquals(APPLICATION_VND_FIELDSMART_CALL_1_JSON, contentTypes);
        assertFalse(response.isSuccess());
        assertEquals("FRInstanceException", response.getError().getErrorCode());

        verify(workOrderController, times(0)).saveWorkIssued(any(), any());
        verify(workOrderController, times(0)).saveWorkStatusHistory(any(), any());
        
        verify(userService, times(1)).updateUserActivityStatus(any(), any());
        verify(userService, times(1)).updateUserLocationHistory(any(), any());
        
    }
    
    @Test
    void processTransaction_shouldReturnOK_whenExceptionInHeartbeat() throws Exception {
    	
        // Arrange
    	FRTransaction trans = this.getTransaction(Utils.TRANSACTION_WORKSTATUS, "device1", "user1", "D1", "WO1", "IN PROGRESS", null, "53", "-6");
       
    	doNothing().when(userService).updateUserActivityStatus(any(), any());
    	doNothing().when(workOrderController).saveWorkIssued(any(), any());
    	doNothing().when(workOrderController).saveWorkStatusHistory(any(), any());
    	
    	doReturn(this.workStatuses).when(this.workOrderController).getWorkStatuses();
    	
    	doReturn(this.getWorkIssued("WO1", "D1", "ISSUED")).when(workOrderController).getWorkIssuedRecord(any(), any(), any());
    	
    	doReturn(false).when(workStatuses).statusIsDesignated(any(), any(), eq(WORKSTATUSDESIGNATION.CLOSED));
    	doReturn(false).when(workStatuses).statusIsDesignated(any(), any(), eq(WORKSTATUSDESIGNATION.CANTDO));
    	doReturn(false).when(workStatuses).statusIsDesignated(any(), any(), eq(WORKSTATUSDESIGNATION.OPERATIONAL));
    	
    	doReturn(true).when(workStatuses).isStatusConfigured(any(), eq("IN PROGRESS"));

    	doThrow(new FRInstanceException("fr instance")).when(userService).updateUserLocationHistory(any(), any());

        // Act
        MvcResult mvcResult = this.mockMvc.perform(post("/processTransaction")
                .contentType(APPLICATION_VND_FIELDSMART_TRANSACTION_1_JSON)
                .accept(APPLICATION_VND_FIELDSMART_CALL_1_JSON)
                .header(Utils.APPCODE_HEADER, "FWS")
                .content(jacksonObjectMapper.writeValueAsString(trans)))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        CallResponse response = jacksonObjectMapper.readValue(mvcResult.getResponse().getContentAsString(), CallResponse.class);
        String contentTypes = mvcResult.getResponse().getContentType();
        assertEquals(APPLICATION_VND_FIELDSMART_CALL_1_JSON, contentTypes);
        assertTrue(response.isSuccess());
        assertNull(response.getError().getErrorCode());

        verify(workOrderController, times(1)).saveWorkIssued(any(), any());
        verify(workOrderController, times(1)).saveWorkStatusHistory(any(), any());
        
        verify(userService, times(1)).updateUserActivityStatus(any(), any());
        verify(userService, times(1)).updateUserLocationHistory(any(), any());
        
    }
    
    @Test
    void processTransaction_shouldReturnWorkOrderNotFound_whenInvalidWOInBody() throws Exception {
    	
        // Arrange
    	FRTransaction trans = this.getTransaction(Utils.TRANSACTION_WORKSTATUS, "device1", "user1", "D1", "WO1", "IN PROGRESS", null, "53", "-6");
       
    	doNothing().when(userService).updateUserActivityStatus(any(), any());
    	
    	doReturn(null).when(workOrderController).getWorkIssuedRecord(any(), any(), any());

        // Act
        MvcResult mvcResult = this.mockMvc.perform(post("/processTransaction")
                .contentType(APPLICATION_VND_FIELDSMART_TRANSACTION_1_JSON)
                .accept(APPLICATION_VND_FIELDSMART_CALL_1_JSON)
                .header(Utils.APPCODE_HEADER, "FWS")
                .content(jacksonObjectMapper.writeValueAsString(trans)))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        CallResponse response = jacksonObjectMapper.readValue(mvcResult.getResponse().getContentAsString(), CallResponse.class);
        String contentTypes = mvcResult.getResponse().getContentType();
        assertEquals(APPLICATION_VND_FIELDSMART_CALL_1_JSON, contentTypes);
        assertFalse(response.isSuccess());
        assertEquals("WorkOrderNotFoundException", response.getError().getErrorCode());

        verify(workOrderController, times(0)).saveWorkIssued(any(), any());
        verify(workOrderController, times(0)).saveWorkStatusHistory(any(), any());
        
        verify(userService, times(1)).updateUserActivityStatus(any(), any());
        verify(userService, times(1)).updateUserLocationHistory(any(), any());
        
    }
    
    @Test
    void processTransaction_shouldReturnOKButNotProcess_whenTransactionAlreadyProcessed() throws Exception {
    	
        // Arrange
    	FRTransaction trans = this.getTransaction(Utils.TRANSACTION_WORKSTATUS, "device1", "user1", "D1", "WO1", "IN PROGRESS", null, "53", "-6");
       
    	doNothing().when(userService).updateUserActivityStatus(any(), any());
    	
    	doNothing().when(workOrderController).saveWorkIssued(any(), any());
    	doNothing().when(workOrderController).saveWorkStatusHistory(any(), any());

    	doReturn(this.getWorkIssued("WO1", "D1", "ISSUED")).when(workOrderController).getWorkIssuedRecord(any(), any(), any());
    	
    	doReturn(true).when(fieldreachTransactionService).workStatusTransactionExists(any(), any());

        // Act
        MvcResult mvcResult = this.mockMvc.perform(post("/processTransaction")
                .contentType(APPLICATION_VND_FIELDSMART_TRANSACTION_1_JSON)
                .accept(APPLICATION_VND_FIELDSMART_CALL_1_JSON)
                .header(Utils.APPCODE_HEADER, "FWS")
                .content(jacksonObjectMapper.writeValueAsString(trans)))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        CallResponse response = jacksonObjectMapper.readValue(mvcResult.getResponse().getContentAsString(), CallResponse.class);
        String contentTypes = mvcResult.getResponse().getContentType();
        assertEquals(APPLICATION_VND_FIELDSMART_CALL_1_JSON, contentTypes);
        assertTrue(response.isSuccess());
        assertNull(response.getError().getErrorCode());

        verify(workOrderController, times(0)).saveWorkIssued(any(), any());
        verify(workOrderController, times(0)).saveWorkStatusHistory(any(), any());
        
        verify(userService, times(1)).updateUserActivityStatus(any(), any());
        verify(userService, times(1)).updateUserLocationHistory(any(), any());
 
    }
    
    @Test
    void processTransaction_shouldReturnOKButNotProcess_whenWOClosed() throws Exception {
    	
        // Arrange
    	FRTransaction trans = this.getTransaction(Utils.TRANSACTION_WORKSTATUS, "device1", "user1", "D1", "WO1", "IN PROGRESS", null, "53", "-6");
       
    	doNothing().when(userService).updateUserActivityStatus(any(), any());
    	
    	doNothing().when(workOrderController).saveWorkIssued(any(), any());
    	doNothing().when(workOrderController).saveWorkStatusHistory(any(), any());
    	
    	doReturn(this.workStatuses).when(this.workOrderController).getWorkStatuses();
    	doReturn(this.getWorkIssued("WO1", "D1", "ISSUED")).when(workOrderController).getWorkIssuedRecord(any(), any(), any());
    	doReturn(true).when(workStatuses).statusIsDesignated(any(), any(), eq(WORKSTATUSDESIGNATION.CLOSED));

        // Act
        MvcResult mvcResult = this.mockMvc.perform(post("/processTransaction")
                .contentType(APPLICATION_VND_FIELDSMART_TRANSACTION_1_JSON)
                .accept(APPLICATION_VND_FIELDSMART_CALL_1_JSON)
                .header(Utils.APPCODE_HEADER, "FWS")
                .content(jacksonObjectMapper.writeValueAsString(trans)))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        CallResponse response = jacksonObjectMapper.readValue(mvcResult.getResponse().getContentAsString(), CallResponse.class);
        String contentTypes = mvcResult.getResponse().getContentType();
        assertEquals(APPLICATION_VND_FIELDSMART_CALL_1_JSON, contentTypes);
        assertTrue(response.isSuccess());
        assertNull(response.getError().getErrorCode());

        verify(workOrderController, times(0)).saveWorkIssued(any(), any());
        
        ArgumentCaptor<WorkStatusHistory> argument = ArgumentCaptor.forClass(WorkStatusHistory.class);
        verify(workOrderController, times(1)).saveWorkStatusHistory(any(), argument.capture());
        
        assertEquals("[*] IN PROGRESS", argument.getValue().getId().getWorkStatus());
        
        verify(userService, times(1)).updateUserActivityStatus(any(), any());
        verify(userService, times(1)).updateUserLocationHistory(any(), any());
  
    }
   
    @Test
    void processTransaction_shouldReturnOKButNotUpdate_whenOperational() throws Exception {
    	
        // Arrange
    	FRTransaction trans = this.getTransaction(Utils.TRANSACTION_WORKSTATUS, "device1", "user1", "D1", "WO1", "OPERATIONAL STATUS", null, "53", "-6");
       
    	doNothing().when(userService).updateUserActivityStatus(any(), any());
    	doNothing().when(workOrderController).saveWorkStatusHistory(any(), any());
    	
    	doReturn(this.workStatuses).when(this.workOrderController).getWorkStatuses();
    	
    	doReturn(this.getWorkIssued("WO1", "D1", "ISSUED")).when(workOrderController).getWorkIssuedRecord(any(), any(), any());
    	
    	doReturn(false).when(workStatuses).statusIsDesignated(any(), any(), eq(WORKSTATUSDESIGNATION.CLOSED));
    	doReturn(false).when(workStatuses).statusIsDesignated(any(), any(), eq(WORKSTATUSDESIGNATION.CANTDO));
    	doReturn(true).when(workStatuses).statusIsDesignated(any(), any(), eq(WORKSTATUSDESIGNATION.OPERATIONAL));

        // Act
        MvcResult mvcResult = this.mockMvc.perform(post("/processTransaction")
                .contentType(APPLICATION_VND_FIELDSMART_TRANSACTION_1_JSON)
                .accept(APPLICATION_VND_FIELDSMART_CALL_1_JSON)
                .header(Utils.APPCODE_HEADER, "FWS")
                .content(jacksonObjectMapper.writeValueAsString(trans)))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        CallResponse response = jacksonObjectMapper.readValue(mvcResult.getResponse().getContentAsString(), CallResponse.class);
        String contentTypes = mvcResult.getResponse().getContentType();
        assertEquals(APPLICATION_VND_FIELDSMART_CALL_1_JSON, contentTypes);
        assertTrue(response.isSuccess());
        assertNull(response.getError().getErrorCode());

        verify(workOrderController, times(0)).saveWorkIssued(any(), any());
        verify(workOrderController, times(1)).saveWorkStatusHistory(any(), any());
        
        verify(userService, times(1)).updateUserActivityStatus(any(), any());
        verify(userService, times(1)).updateUserLocationHistory(any(), any());
  
    }
    
    @Test
    void processTransaction_shouldReturnOKButNotUpdate_whenWONotCantDoByAll() throws Exception {
    	
        // Arrange
    	FRTransaction trans = this.getTransaction(Utils.TRANSACTION_WORKSTATUS, "device1", "user1", "D1", "WO1", "CANTDO", null, "53", "-6");
       
    	doNothing().when(userService).updateUserActivityStatus(any(), any());
    	doNothing().when(workOrderController).saveWorkStatusHistory(any(), any());
    	
    	doReturn(this.workStatuses).when(this.workOrderController).getWorkStatuses();
    	
    	doReturn(this.getWorkIssued("WO1", "D1", "ISSUED")).when(workOrderController).getWorkIssuedRecord(any(), any(), any());
    	
    	doReturn(false).when(workStatuses).statusIsDesignated(any(), any(), eq(WORKSTATUSDESIGNATION.CLOSED));
    	doReturn(true).when(workStatuses).statusIsDesignated(any(), any(), eq(WORKSTATUSDESIGNATION.CANTDO));

    	doReturn(false).when(fieldreachTransactionService).workStatusCantDoByAllOtherUsersInWorkGroup(any(), any(), any(), any(), any(), any());

        // Act
        MvcResult mvcResult = this.mockMvc.perform(post("/processTransaction")
                .contentType(APPLICATION_VND_FIELDSMART_TRANSACTION_1_JSON)
                .accept(APPLICATION_VND_FIELDSMART_CALL_1_JSON)
                .header(Utils.APPCODE_HEADER, "FWS")
                .content(jacksonObjectMapper.writeValueAsString(trans)))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        CallResponse response = jacksonObjectMapper.readValue(mvcResult.getResponse().getContentAsString(), CallResponse.class);
        String contentTypes = mvcResult.getResponse().getContentType();
        assertEquals(APPLICATION_VND_FIELDSMART_CALL_1_JSON, contentTypes);
        assertTrue(response.isSuccess());
        assertNull(response.getError().getErrorCode());

        verify(workOrderController, times(0)).saveWorkIssued(any(), any());
        verify(workOrderController, times(1)).saveWorkStatusHistory(any(), any());
        
        verify(userService, times(1)).updateUserActivityStatus(any(), any());
        verify(userService, times(1)).updateUserLocationHistory(any(), any());
  
    }
    
    @Test
    void processTransaction_shouldReturnOK_whenWOCantDoByAll() throws Exception {
    	
        // Arrange
    	FRTransaction trans = this.getTransaction(Utils.TRANSACTION_WORKSTATUS, "device1", "user1", "D1", "WO1", "IN PROGRESS", null, "53", "-6");
       
    	doNothing().when(userService).updateUserActivityStatus(any(), any());
    	
    	doNothing().when(workOrderController).saveWorkIssued(any(), any());
    	doNothing().when(workOrderController).saveWorkStatusHistory(any(), any());
    	
    	doReturn(this.workStatuses).when(this.workOrderController).getWorkStatuses();
    	
    	doReturn(this.getWorkIssued("WO1", "D1", "ISSUED")).when(workOrderController).getWorkIssuedRecord(any(), any(), any());
    	
    	doReturn(false).when(workStatuses).statusIsDesignated(any(), any(), eq(WORKSTATUSDESIGNATION.CLOSED));
    	doReturn(true).when(workStatuses).statusIsDesignated(any(), any(), eq(WORKSTATUSDESIGNATION.CANTDO));

    	doReturn(true).when(fieldreachTransactionService).workStatusCantDoByAllOtherUsersInWorkGroup(any(), any(), any(), any(), any(), any());

        // Act
        MvcResult mvcResult = this.mockMvc.perform(post("/processTransaction")
                .contentType(APPLICATION_VND_FIELDSMART_TRANSACTION_1_JSON)
                .accept(APPLICATION_VND_FIELDSMART_CALL_1_JSON)
                .header(Utils.APPCODE_HEADER, "FWS")
                .content(jacksonObjectMapper.writeValueAsString(trans)))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        CallResponse response = jacksonObjectMapper.readValue(mvcResult.getResponse().getContentAsString(), CallResponse.class);
        String contentTypes = mvcResult.getResponse().getContentType();
        assertEquals(APPLICATION_VND_FIELDSMART_CALL_1_JSON, contentTypes);
        assertTrue(response.isSuccess());
        assertNull(response.getError().getErrorCode());

        verify(workOrderController, times(1)).saveWorkIssued(any(), any());
        verify(workOrderController, times(1)).saveWorkStatusHistory(any(), any());
        
        verify(userService, times(1)).updateUserActivityStatus(any(), any());
        verify(userService, times(1)).updateUserLocationHistory(any(), any());
  
    }
    
    @Test
    void processTransaction_shouldReturnOK_whenStatusNotConfigured() throws Exception {
    	
        // Arrange
    	FRTransaction trans = this.getTransaction(Utils.TRANSACTION_WORKSTATUS, "device1", "user1", "D1", "WO1", "IN PROGRESS", null, "53", "-6");
       
    	doNothing().when(userService).updateUserActivityStatus(any(), any());
    	
    	doNothing().when(workOrderController).saveWorkIssued(any(), any());
    	doNothing().when(workOrderController).saveWorkStatusHistory(any(), any());
    	
    	doReturn(this.workStatuses).when(this.workOrderController).getWorkStatuses();
    	
    	doReturn(this.getWorkIssued("WO1", "D1", "ISSUED")).when(workOrderController).getWorkIssuedRecord(any(), any(), any());
    	
    	doReturn(false).when(workStatuses).statusIsDesignated(any(), any(), eq(WORKSTATUSDESIGNATION.CLOSED));
    	doReturn(false).when(workStatuses).statusIsDesignated(any(), any(), eq(WORKSTATUSDESIGNATION.CANTDO));
    	doReturn(false).when(workStatuses).statusIsDesignated(any(), any(), eq(WORKSTATUSDESIGNATION.OPERATIONAL));
    	doReturn(false).when(workStatuses).isStatusConfigured(any(), eq("IN PROGRESS"));

        // Act
        MvcResult mvcResult = this.mockMvc.perform(post("/processTransaction")
                .contentType(APPLICATION_VND_FIELDSMART_TRANSACTION_1_JSON)
                .accept(APPLICATION_VND_FIELDSMART_CALL_1_JSON)
                .header(Utils.APPCODE_HEADER, "FWS")
                .content(jacksonObjectMapper.writeValueAsString(trans)))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        CallResponse response = jacksonObjectMapper.readValue(mvcResult.getResponse().getContentAsString(), CallResponse.class);
        String contentTypes = mvcResult.getResponse().getContentType();
        assertEquals(APPLICATION_VND_FIELDSMART_CALL_1_JSON, contentTypes);
        assertTrue(response.isSuccess());
        assertNull(response.getError().getErrorCode());

        ArgumentCaptor<WorkStatusHistory> argument = ArgumentCaptor.forClass(WorkStatusHistory.class);
        verify(workOrderController, times(1)).saveWorkStatusHistory(any(), argument.capture());
        
        assertEquals("[*] IN PROGRESS", argument.getValue().getId().getWorkStatus());
        
        verify(workOrderController, times(0)).saveWorkIssued(any(), any());
        
        verify(userService, times(1)).updateUserActivityStatus(any(), any());
        verify(userService, times(1)).updateUserLocationHistory(any(), any());
  
    }
    
    private FRTransaction getTransaction(String transType, String deviceId, String userCode, String districtCode, String workOrderNo, String workStatus, String additionalText, String latitude, String longitude) {
    	
    	FRTransaction trans = new FRTransaction();
    	TransactionItem item = new TransactionItem();
    	TransType type = new TransType();
    	item.setDeviceId(deviceId);
    	item.setLogDate(BigInteger.valueOf(Common.generateFieldreachDBDate()));
    	item.setLogTime(String.valueOf(Common.generateFieldreachDBTime()));
    	item.setUserCode(userCode);
    	
    	if (transType != null && transType.equals(Utils.TRANSACTION_WORKSTATUS)) {
    		type.setDistrictCode(districtCode);
    		type.setWorkOrderNo(workOrderNo);
    		type.setWorkStatus(workStatus);
    	}
    	
    	type.setAdditionalText(additionalText);
    	
    	type.setType(transType);
    	type.setLatitude(latitude);
    	type.setLongitude(longitude);
    	
    	item.setTrans(type);
    	
    	trans.setItem(item);
    	
    	return trans;
    }
    
    private String getFRTransactionXML() {
        return "<FRTransaction>\n" +
                "  <Item>\n" +
                "    <UserCode>KBRADY</UserCode>\n" +
                "    <LogDate>20121101</LogDate>\n" +
                "    <LogTime>160000</LogTime>\n" +
                "    <DeviceId>IntermecCN3</DeviceId>\n" +
                "    <Trans type=\"HEARTBEAT\">\n" +
                "      <Latitude>53</Latitude>\n" +
                "      <Longitude>-6</Longitude>\n" +
                "    </Trans>\n" +
                "  </Item>\n" +
                "</FRTransaction>";
    }
    
    private WorkIssued getWorkIssued(String workOrderNo, String districtCode, String status) {
    	
    	WorkIssued wo = new WorkIssued(workOrderNo, districtCode);
    	
    	wo.setWorkStatus(status);
    	
    	return wo;
    }

}