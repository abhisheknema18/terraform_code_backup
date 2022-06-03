/**
 * Author:  T Murray
 * Date:    20/08/2012
 * Project: FDE019
 * 
 * Copyright AMT-Sybex 2012
 * 
 * FDE020 TM 01/03/2013
 * Modified code for improved exception handling and to allow for easier support
 * of other transaction types should the need arise, and to use functionality
 * moved to common transaction controller.
 * 
 * Amended:
 * FDE026 TM 26/06/2014
 * Multiple Fieldreach Instance Support
 * 
 */
package com.amtsybex.fieldreach.services.endpoint.rest.impl;

import static com.amtsybex.fieldreach.services.messages.response.CallResponse.APPLICATION_VND_FIELDSMART_CALL_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.CallResponse.APPLICATION_VND_FIELDSMART_CALL_1_XML;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.APPLICATION_JSON;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.APPLICATION_XML;
import static com.amtsybex.fieldreach.services.messages.request.FRTransaction.APPLICATION_VND_FIELDSMART_TRANSACTION_1_JSON;
import static com.amtsybex.fieldreach.services.messages.request.FRTransaction.APPLICATION_VND_FIELDSMART_TRANSACTION_1_XML;


import java.io.IOException;
import java.io.StringWriter;

import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.XmlMappingException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.model.UserActivityStatus;
import com.amtsybex.fieldreach.backend.model.pk.UserActivityStatusId;
import com.amtsybex.fieldreach.backend.service.UserService;
import com.amtsybex.fieldreach.services.endpoint.common.FieldreachTransactionController;
import com.amtsybex.fieldreach.services.endpoint.rest.RestFieldreachTransactionController;
import com.amtsybex.fieldreach.services.exception.BadRequestException;
import com.amtsybex.fieldreach.services.exception.TransacationAlreadyProcessedException;
import com.amtsybex.fieldreach.services.exception.UnknownTransactionTypeException;
import com.amtsybex.fieldreach.services.exception.WorkOrderNotFoundException;
import com.amtsybex.fieldreach.services.messages.request.FRTransaction;
import com.amtsybex.fieldreach.services.messages.response.CallResponse;
import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.amtsybex.fieldreach.services.utils.Utils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@Api(tags = "File Upload")
public class RestFieldreachTransactionControllerImpl extends BaseControllerImpl
		implements RestFieldreachTransactionController {

	private static final Logger log = LoggerFactory.getLogger(RestFieldreachTransactionControllerImpl.class.getName());
	
	@Autowired
	FieldreachTransactionController fieldreachTransactionController;
	
	// FDP1255 TM 01/11/2016
	@Autowired
	UserService userService;

	/*-------------------------------------------
	 - Interface Implementation
	 --------------------------------------------*/

	/**
	 * Processes the Fieldreach transaction file passed in the request body
	 * accordingly depending on the transaction type.
	 * 
	 * @param httpHeaders
	 *            servlet request
	 * 
	 * @param transaction
	 *            A Fieldreach transaction message.
	 * 
	 * @return Returns a CallResponse message.
	 * 
	 * @throws BadRequestException
	 *             If the request body s empty or does not contain a valid
	 *             Fieldreach transaction message
	 * 
	 * @
	 *             If invalid Authorization token is passed in the request.
	 */
	@PostMapping(value = "/processTransaction",
	        consumes = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_TRANSACTION_1_JSON, APPLICATION_VND_FIELDSMART_TRANSACTION_1_XML},
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_CALL_1_JSON, APPLICATION_VND_FIELDSMART_CALL_1_XML})
	
	@ApiOperation(value = "Upload FieldSmart Transaction", 
			notes = "This web service provides clients with a facility to upload FieldSmart transactions files. " +
					"The web service will consume the transaction file and update the database accordingly")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Ok - Possible error codes include UnknownTransactionTypeException, WorkOrderNotFoundException, DatabaseException, FRInstanceException EXCEPTION(General Exception)"),
			@ApiResponse(code = 400, message = "Bad Request - Malformed or missing XML in the Request body."),
	})
	public ResponseEntity<CallResponse> processTransaction(@RequestHeader HttpHeaders httpHeaders, @RequestHeader(value=Utils.APPCODE_HEADER, required = true) String appCode, @RequestBody(required = true) FRTransaction transaction,
														   @RequestParam(name = "transactionFileName", required = false) String transactionFileName)
			throws BadRequestException {

		log.debug(">>> processTransaction requestBody=XXX");

		CallResponse objResponse = new CallResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		objResponse.setError(errorMessage);

		// get application identifier
		String applicationIdentifier = Utils.extractApplicationIdentifier(httpHeaders);

		try {

			// Debug headers
			Utils.debugHeaders(log, httpHeaders);
			
			if(log.isDebugEnabled())
				log.debug("Body :" + transaction.toString());
			
			// End FDP1255

			String transtype = null;
			
			try {
				this.validateTransaction(transaction);
				transtype = transaction.getItem().getTrans().getType();

				log.debug("Transaction is valid. Transaction type = " + transtype);
				
			} catch (Exception e) {

				log.error("Invalid transaction in /processTransaction" + e.getMessage());
				throw new BadRequestException(e.getMessage());
			}

			// FDP1255 TM 01/11/2016 - New Transaction Type added.
			// Verify transaction type is recognised
			if (!transtype.equals(Utils.TRANSACTION_WORKSTATUS) && !transtype.equals(Utils.TRANSACTION_HEARTBEAT)) {

				throw new UnknownTransactionTypeException(
						"Unknown transaction type: " + transaction.getItem().getTrans().getType());
			}
			
			// Determine transaction type and call appropriate method
			if (transtype.equals(Utils.TRANSACTION_WORKSTATUS)) {

				try {
					//update work status transaction if latitude/longitudepresent
					fieldreachTransactionController.processHeartbeatTransaction(applicationIdentifier, transaction, appCode);
				} catch (FRInstanceException e) {
					//We can get a unique constraint exception if transactions are processed quickly as the key is on time to the second.
					log.error("Exception updating user location from work status transaction " + e.getMessage());
				}
				
				// process workstatus transaction
				errorMessage = this.processWorkStatusTransaction(applicationIdentifier, transaction, transactionFileName);

				objResponse.setError(errorMessage);

				// FDP1255 TM 01/11/2016
			} else if (transtype.equals(Utils.TRANSACTION_HEARTBEAT)) {
				// process heartbeat transaction
				errorMessage = this.processHeartbeatTransaction(applicationIdentifier, transaction, appCode);

				objResponse.setError(errorMessage);

			} 
						
			// Update the UserActivityStatus table for all transactions
			UserActivityStatus uas = new UserActivityStatus();
			UserActivityStatusId id = new UserActivityStatusId(transaction.getItem().getUserCode(), appCode, transaction.getItem().getDeviceId());
			
			uas.setId(id);
			uas.setActivityDate(transaction.getItem().getLogDate().intValue());
			uas.setActivityTime(transaction.getItem().getLogTime());
			uas.setActivityType(transaction.getItem().getTrans().getType());
			
			this.userService.updateUserActivityStatus(applicationIdentifier, uas);
			
			// End FDP1255
			
		} catch (BadRequestException e) {

			// In case of BadRequestException failure.. rethrow for handler to
			// set.
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, e.getLocalizedMessage(), e);
			
		} catch (UnknownTransactionTypeException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.UNKNOWN_TRANSACTION_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			
		} catch (FRInstanceException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.FR_INSTANCE_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			
		} finally {

			objResponse.setSuccess(errorMessage.getErrorCode() == null);
		}

		log.debug("<<< processTransaction");

		return ResponseEntity.ok(objResponse);
	}

	/*-------------------------------------------
	 - Private Methods
	 --------------------------------------------*/

	/**
	 * Process a WORKSTATUS transaction message.
	 * 
	 * @param trans
	 *            FRTransaction object.
	 *
	 * @param transFileName
	 *            Name of the transaction file
	 * 
	 * @param transFileName
	 *            Transaction filename
	 * 
	 * @return an ErrorMessage object populated with any error information
	 *         generated during the processing of the WORKSTATUS transaction.
	 */
	private ErrorMessage processWorkStatusTransaction(String frInstance, FRTransaction trans, String transFileName) {

		log.debug(">>> processWorkStatusTransaction");

		// return value
		ErrorMessage errorMessage = new ErrorMessage();

		try {

			fieldreachTransactionController.processWorkStatusTransaction(frInstance, trans, transFileName);

		} catch (WorkOrderNotFoundException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.WORKORDER_NOT_FOUND_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			
		} catch (TransacationAlreadyProcessedException e) {

			log.debug(e.getMessage());
			
		} catch (FRInstanceException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.FR_INSTANCE_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			
		} catch (Exception e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			
		}

		// If no errors thrown then attempt to dispatch the transaction.
		if (errorMessage.getErrorCode() == null) {

			// Dispatch WORKSTATUS transaction if necessary.
			if (fieldreachTransactionController.retainWorkStatusTrans()) {

				// Generate transaction file name if one does not exist.
				String fileName = transFileName;

				if (transFileName == null || transFileName.trim().equals("")) {

					String userCode = trans.getItem().getUserCode();
					String completeDate = trans.getItem().getLogDate().toString();
					String completeTime = trans.getItem().getLogTime().toString();

					fileName = "TRNS.WORKSTATUS." + userCode + "." + completeDate + completeTime + ".xml";
				}

				// Get workstatus in the message.
				String workStatus = trans.getItem().getTrans().getWorkStatus().trim();

				String xmlString = this.buildXml(trans);
				// Attempt to dispatch the transaction to the configured
				// location
				fieldreachTransactionController.dispatchWorkStatusTransaction(workStatus, Utils.prettyXML(xmlString),
						fileName);
			} else
				log.debug("WORKSTATUS transactions are not retained.");
		}

		log.debug("<<< processWorkStatusTransaction");

		return errorMessage;
	}

	private String buildXml(Object object) {

		log.debug(">>> buildXml object=XXX");

		String response = "";
		StringWriter ioStream = new StringWriter();

		try {

			restApiMarshaller.marshal(object, new StreamResult(ioStream));
			response = ioStream.getBuffer().toString();
			ioStream.close();

		} catch (XmlMappingException e) {
			log.error("Mapping Exception in buildXml: " + e.getMessage());
		} catch (IOException e) {
			log.error("IOException in buildXml: " + e.getMessage());
		}

		log.debug("<<< buildXml");

		return response;
	}

	// FDP1255 TM 01/11/2016 - New Transaction Type added.
	/**
	 * Process a HEARTBEAT transaction message.
	 * 
	 * @param frInstance
	 *            The fieldreach instance to update
	 * 
	 * @param trans
	 *            FRTransaction object.
	 * 
	 * @param appCode
	 *            The code of the application the request originated from.
	 * 
	 * @return an ErrorMessage object populated with any error information
	 *         generated during the processing of the HEARTBEAT transaction.
	 */
	private ErrorMessage processHeartbeatTransaction(String frInstance, FRTransaction trans, String appCode) {

		log.debug(">>> processHeartbeatTransaction");

		// return value
		ErrorMessage errorMessage = new ErrorMessage();

		try {

			fieldreachTransactionController.processHeartbeatTransaction(frInstance, trans, appCode);
			
		} catch (FRInstanceException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.FR_INSTANCE_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			
		} catch (Exception e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
		}

		log.debug("<<< processHeartbeatTransaction");

		return errorMessage;
	}
	// End FDP1255

	/**
	 * Validate transaction object
	 * 
	 * @param transaction
	 *           	
	 * @throws Exception
	 *             If any exceptions occur during the unmarshalling process or
	 *             if any of the required attributes can not be found.
	 */
	private void validateTransaction(FRTransaction transaction) throws Exception {
		
		if (transaction.getItem() != null && transaction.getItem().getTrans() != null) {
			String transType = transaction.getItem().getTrans().getType();
			if (transType == null || transType.trim().equals("")) {

				log.error("Invalid transaction message. <Trans> 'type' attribute element is missing or empty.");
				throw new Exception(
						"Invalid transaction message. <Trans> 'type' attribute element is missing or empty.");
			}
		}
	}

}
