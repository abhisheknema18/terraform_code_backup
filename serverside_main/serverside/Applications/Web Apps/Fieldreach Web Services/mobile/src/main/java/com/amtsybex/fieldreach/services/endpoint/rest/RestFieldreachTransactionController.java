/**
 * Author:  T Murray
 * Date:    27/09/2012
 * Project: FDE019
 * 
 * Copyright AMT-Sybex 2012
 */
package com.amtsybex.fieldreach.services.endpoint.rest;

import com.amtsybex.fieldreach.services.exception.BadRequestException;
import com.amtsybex.fieldreach.services.messages.request.FRTransaction;
import com.amtsybex.fieldreach.services.messages.response.CallResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;


public interface RestFieldreachTransactionController
{

	/**
	 * Processes the Fieldreach transaction file passed in the request 
	 * body accordingly depending on the transaction type.
	 * 
	 * @param httpHeaders
	 * servlet request
	 * 
	 * @param appCode
	 * A mobile app code.
	 * 
	 * @param transaction
	 * A Fieldreach transaction message.
	 * 
	 * @return 
	 * Returns a CallResponse message.
	 * 
	 * @throws BadRequestException
	 * If the request body s empty or does not contain a valid Fieldreach transaction
	 * message
	 * 
	 * @
	 * If invalid Authorization token is passed in the request.
	 */
	ResponseEntity<CallResponse> processTransaction(HttpHeaders httpHeaders, String appCode, FRTransaction transaction, String transactionFileName)
			throws BadRequestException;

	
}
