package com.amtsybex.fieldreach.services.endpoint.rest.impl;

import com.amtsybex.fieldreach.backend.model.SystemEventLog;
import com.amtsybex.fieldreach.backend.service.SystemEventService;
import com.amtsybex.fieldreach.services.endpoint.rest.RestSystemEventLog;
import com.amtsybex.fieldreach.services.exception.BadRequestException;
import com.amtsybex.fieldreach.services.messages.request.SystemEvent;
import com.amtsybex.fieldreach.services.messages.response.CallResponse;
import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.amtsybex.fieldreach.services.utils.Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.XmlMappingException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

import static com.amtsybex.fieldreach.services.messages.response.CallResponse.APPLICATION_VND_FIELDSMART_CALL_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.CallResponse.APPLICATION_VND_FIELDSMART_CALL_1_XML;
import static com.amtsybex.fieldreach.services.messages.request.SystemEvent.APPLICATION_VND_FIELDSMART_SYSTEMEVENT_1_JSON;
import static com.amtsybex.fieldreach.services.messages.request.SystemEvent.APPLICATION_VND_FIELDSMART_SYSTEMEVENT_1_XML;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.*;

@Controller
@Api(tags = "System Event Integration")
public class RestSystemEventLogImpl extends BaseControllerImpl implements RestSystemEventLog {

	private static final Logger log = LoggerFactory.getLogger(RestSystemEventLogImpl.class.getName());

	@Autowired
	private SystemEventService mSystemEventLogger;

	@Override
	@Deprecated
	@PostMapping(value = "/systemevent",
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_CALL_1_JSON, APPLICATION_VND_FIELDSMART_CALL_1_XML},
			consumes = {APPLICATION_JSON, APPLICATION_XML,APPLICATION_VND_FIELDSMART_SYSTEMEVENT_1_JSON, APPLICATION_VND_FIELDSMART_SYSTEMEVENT_1_XML})
	@ApiOperation(value = "Allows third party systems to log exceptions in FieldSmart", 
			notes = "Allows third party systems to log exceptions in FieldSmart")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include EXCEPTION(General Exception)"),
			@ApiResponse(code = 200, message = "Bad Request - Malformed or missing XML in the Request body.")
	})
	public ResponseEntity<CallResponse> addSystemEventLog(@RequestHeader HttpHeaders httpHeaders, @RequestBody SystemEvent systemEvent) throws BadRequestException {

		if (log.isDebugEnabled()) {

			log.debug(">>> systemEventLog requestBody=XXX");
		}
		
		CallResponse callResponse = new CallResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		callResponse.setError(errorMessage);
			
		// Get application identifier
		String applicationIdentifier = Utils.extractApplicationIdentifier(httpHeaders);
		
		try {

			if(systemEvent == null) {
				throw new BadRequestException("System Event has not been supplied.");
			}
			
			if( StringUtils.isEmpty(systemEvent.getApplication()))
				throw new BadRequestException("System Event Application has not been supplied.");
			
			if( StringUtils.isEmpty(systemEvent.getEventCategory()))
				throw new BadRequestException("System Event EventCategory has not been supplied.");
			
			if( StringUtils.isEmpty(systemEvent.getEventSummary()))
				throw new BadRequestException("System Event Application has not been supplied.");
			
			if( StringUtils.isEmpty(systemEvent.getEventTime()))
				throw new BadRequestException("System Event EventTime has not been supplied.");
			
			if( StringUtils.isEmpty(systemEvent.getEventType()))
				throw new BadRequestException("System Event EventType has not been supplied.");
			
			if( StringUtils.isEmpty(systemEvent.getSourceSystem()))
				throw new BadRequestException("System Event SourceSystem has not been supplied.");
			
			if(systemEvent.getEventDate() == 0 || String.valueOf(systemEvent.getEventDate()).length() != 8)
				throw new BadRequestException("System Event EventDate() has not been supplied or is invalid.");
			
			//if(systemEventlog.getSeverity() == 0)
				//throw new BadRequestException("System Event Severity has not been supplied.");
			
			SystemEventLog logItem = new SystemEventLog();
			
			logItem.setApplication(systemEvent.getApplication());
			logItem.setDistrictCode(systemEvent.getDistrictCode());
			logItem.setErrorCode(systemEvent.getErrorCode());
			logItem.setEventCategory(systemEvent.getEventCategory());
			logItem.setEventDate(systemEvent.getEventDate());
			logItem.setEventDesc(systemEvent.getEventDesc());
			logItem.setEventSummary(systemEvent.getEventSummary());
			logItem.setEventTime(systemEvent.getEventTime());
			logItem.setEventType(systemEvent.getEventType());
			logItem.setReturnId(systemEvent.getReturnId());
			logItem.setSeverity(systemEvent.getSeverity());
			logItem.setSourceSystem(systemEvent.getSourceSystem());
			logItem.setUserCode(systemEvent.getUserCode());
			logItem.setWorkOrderNo(systemEvent.getWorkOrderNo());
			
			mSystemEventLogger.saveSystemEventLog(applicationIdentifier, logItem);
			
		} catch(BadRequestException e) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, e.getLocalizedMessage(), e);
		} catch (XmlMappingException e) {
			log.error("XmlMappingException in /systemEventLog: " + e.getMessage());
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, e.getLocalizedMessage(), e);
		} catch (Exception e) {

			log.error("Unexpected error occured: "
					+ e.getMessage());

			errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} finally {
			
			callResponse.setSuccess(errorMessage.getErrorCode() == null);
		}
		
		if (log.isDebugEnabled()) {

			log.debug("<<< systemEventLog");
		}
		
		return ResponseEntity.ok(callResponse);
	}
}
