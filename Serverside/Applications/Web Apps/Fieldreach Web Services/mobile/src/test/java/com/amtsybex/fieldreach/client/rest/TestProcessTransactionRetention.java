/**
 * Author:  T Murray
 * Date:    13/01/2014
 * Project: FDE022
 * 
 * Copyright AMT-Sybex 2014
 */
package com.amtsybex.fieldreach.client.rest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.slf4j.LoggerFactory; 
import org.slf4j.Logger;  
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.XmlMappingException;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import com.amtsybex.fieldreach.services.messages.response.CallResponse;
import com.amtsybex.fieldreach.services.test.annotations.TestDescription;
import com.amtsybex.fieldreach.services.test.annotations.TestLabel;
import com.amtsybex.fieldreach.utils.impl.Common;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import org.junit.Test;
import static org.junit.Assert.*;


public class TestProcessTransactionRetention extends CommonBase 
{

	static Logger log = LoggerFactory.getLogger(TestProcessTransactionRetention.class.getName());


	@Test
	@TestDescription(desc="This test case will process a Fieldreach transation and will verify that" +
			"it processes successfully.<br><br>" +
			"Manual steps will be required to verify the transaction has been retained correctly, or " +
			"not retained as expected.<br><Br>" +
			"These tests use the transaction file specifed by the test property " +
			"'test.validWorkStatusTrans'")
	@TestLabel(label="Reference: 3401")
	public void test_1() 
	{
		//
		// Headers
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("x-fws-appCode", appCode);
		
		File file = new File(StringUtils.cleanPath(uploadTransDir + "/" + validWorkStatusTrans));
		
		String transData = null;
		
		if (file.exists() && file.isFile())
		{
			try 
			{
				byte[] bytes = Common.getBytesFromFile(file);
				transData = new String(bytes);
			} 
			catch (FileNotFoundException e) 
			{

				log.error(e.getMessage());
				fail("Unable to extract transacton file content to build request body.");
			} 
			catch (IOException e)
			{
				log.error(e.getMessage());
				fail("Unable to extract transacton file content to build request body.");
			}
		}
		else
		{
			log.error("Error creating request body from transaction file");
			fail("Unable to extract transacton file content to build request body.");
		}
		
		
		CallResponse resp = processTransaction(validWorkStatusTrans, transData, requestHeaders, HttpStatus.OK);
		
		
		assertTrue("Request was not a success. Error Code: " +
				resp.getError().getErrorCode(), resp.isSuccess());
		
		
		assertTrue("Unexpected Exception returned: " + resp.getError().getErrorCode(),
				resp.getError().getErrorCode() == null);
			
	}
	
	
	private CallResponse processTransaction(String transactionFileName, 
			String data,
			HttpHeaders requestHeaders,
			HttpStatus expectedStatusCode)
	{
		final String wsURL = baseURL + "processTransaction";

		RestOperations restTemplate = null;
		restTemplate = new RestTemplate();
		
		StringBuffer restCall;
		CallResponse response = null;
		
		requestHeaders.set("x-fws-applicationidentifier", applicationIdentifier);
		
		try 
		{
			//
			// Build REST URL to call
			restCall = new StringBuffer(wsURL);
			
			if (transactionFileName!= null)
				restCall.append("?transactionFileName=" + transactionFileName);
		
			
			//
			// Make REST call and retrieve the results
			String requestBody = data;

			HttpEntity<String> requestEntity = new HttpEntity<String>(
					requestBody, requestHeaders);

			ResponseEntity<String> resultsExchange = restTemplate.exchange(
					restCall.toString(), HttpMethod.POST,requestEntity,
					String.class);

			String results = resultsExchange.getBody();
			
			log.debug("process transaction results : " + results);
			assertTrue("Result is null", results != null);
			
			
			//
			// Parse XML received from the response into an 
			// CallResponse object
			try 
			{
				XStream xstream = new XStream(new StaxDriver());
				xstream.alias("CallResponse", CallResponse.class);
				response = (CallResponse) xstream
						.fromXML(results);

				log.debug("Success : " + response.isSuccess());
			} 
			catch (XmlMappingException e) 
			{
				log.error("Client Mapping Exception :" + e.getMessage());
				fail("Client Mapping Exception :" + e.getMessage());
			}
		} 
		catch (HttpClientErrorException he) 
		{
			//
			//Check to see if the HHTP status code is what we expect
			if (he.getStatusCode() == expectedStatusCode)
			{
				log.debug(expectedStatusCode + " status code returned as expected.");
			}
			else
			{
				log.error("Client Exception :" + he.getMessage());
				fail("Client Exception :" + he.getMessage());
			}
		}
		
		return response;
	}

}
