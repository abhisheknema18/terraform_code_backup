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
import com.amtsybex.fieldreach.services.utils.Utils;
import com.amtsybex.fieldreach.utils.impl.Common;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import org.junit.Test;
import static org.junit.Assert.*;


public class TestProcessTransaction extends CommonBase 
{

	static Logger log = LoggerFactory.getLogger(TestProcessTransaction.class.getName());

	
	@Test
	@TestDescription(desc="No date header and valid authorizaton token.")
	@TestLabel(label="Reference: 2401")
	public void test_1() 
	{

		//
		// Headers
	
		HttpHeaders requestHeaders = new HttpHeaders();
		String dateHeader = getDateHeader();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("x-fws-appCode", appCode);
		
		processTransaction(null,null, requestHeaders, HttpStatus.UNAUTHORIZED);
			
	}
	
	
		
	@Test
	@TestDescription(desc="Invalid header and valid authorizaton token.")
	@TestLabel(label="Reference: 2402")
	public void test_2() 
	{
		//
		// Headers
		String dateHeader = "invalid date";
		

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("x-fws-appCode", appCode);
		
		processTransaction(null,null, requestHeaders, HttpStatus.UNAUTHORIZED);
			
	}
	
	
	@Test
	@TestDescription(desc="Non RFC 2616 date header and valid authorizaton token.")
	@TestLabel(label="Reference: 2403")
	public void test_3() 
	{

		//
		// Headers
		String dateHeader = getNonRFC2616Date();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("x-fws-appCode", appCode);
		
		processTransaction(null,null, requestHeaders, HttpStatus.UNAUTHORIZED);
			
	}
	
	
	@Test
	@TestDescription(desc="Date header more than 15 minutes in front of the " +
						  "server time and valid authorizaton token.")
	@TestLabel(label="Reference: 2404")
	public void test_4() 
	{

		//
		// Headers
		String dateHeader = getDateAfter(1);
		

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("x-fws-appCode", appCode);
		
		processTransaction(null,null, requestHeaders, HttpStatus.UNAUTHORIZED);
			
	}
	
	
	
	@Test
	@TestDescription(desc="Date header more than 15 minutes behind the " +
						  "server time and valid authorizaton token.")
	@TestLabel(label="Reference: 2405")
	public void test_5() 
	{

		//
		// Headers
		String dateHeader = getDateBefore(1);
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("x-fws-appCode", appCode);
		
		processTransaction(null,null, requestHeaders, HttpStatus.UNAUTHORIZED);
			
	}
	
	
	@Test
	@TestDescription(desc="Valid date header and no authorization token.")
	@TestLabel(label="Reference: 2406")
	public void test_6() 
	{
		//
		// Headers
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", "FWS " + unrevokedUserCode + ":"
				+ null);
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("x-fws-appCode", appCode);
		
		processTransaction(null,null, requestHeaders, HttpStatus.UNAUTHORIZED);
			
	}
	
	
	@Test
	@TestDescription(desc="Valid date header and malformed authorization token.")
	@TestLabel(label="Reference: 2407")
	public void test_7() 
	{
		//
		// Headers
		String dateHeader = getDateHeader();
		String authToken = getAuthToken(unrevokedPassword, dateHeader);
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", "FWS-malformed" + unrevokedUserCode + ":"
				+ authToken);
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("x-fws-appCode", appCode);
		
		processTransaction(null,null, requestHeaders, HttpStatus.UNAUTHORIZED);
			
	}
	
	
	@Test
	@TestDescription(desc="Valid date header and valid authorization token.<br>" +
						  "Invalid user specified in the authorization header.")
	@TestLabel(label="Reference: 2408")
	public void test_8() 
	{
		//
		// Headers
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(invalidUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("x-fws-appCode", appCode);
		
		processTransaction(null,null, requestHeaders, HttpStatus.UNAUTHORIZED);
			
	}
	

	@Test
	@TestDescription(desc="Valid date header and valid authorization token " +
						  "signed with incorrect password.")
	@TestLabel(label="Reference: 2409")
	public void test_9() 
	{
		//
		// Headers
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,invalidPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("x-fws-appCode", appCode);
		
		processTransaction(null,null, requestHeaders, HttpStatus.UNAUTHORIZED);
			
	}
	

	
	@Test
	@TestDescription(desc="Valid date header and valid authorization token. Do not specify " +
						  "a request body. Check that HTTP Status 400 Bad Request is returned.")
	@TestLabel(label="Reference: 2410")
	public void test_10() 
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
		
		processTransaction(null, null, requestHeaders, HttpStatus.BAD_REQUEST);
			
	}
	
	
	@Test
	@TestDescription(desc="Valid date header and valid authorization token. The request body should contain " +
						  "a malformed Fieldreach transaction message. Check that HTTP Status 400 Bad " +
						  "Request is returned.")
	@TestLabel(label="Reference: 2411")
	public void test_11() 
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
		
		File file = new File(StringUtils.cleanPath(uploadTransDir + "/" + invalidWorkStatusTrans));
		
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
		
		
		processTransaction(null, transData, requestHeaders, HttpStatus.BAD_REQUEST);
			
	}
	
	
	
	@Test
	@TestDescription(desc="Valid date header and valid authorization token. The request body should contain " +
						  "a valid Fieldreach transaction message that is not a WORKSTATUS message. " +
						  "Check that " + Utils.UNKNOWN_TRANSACTION_EXCEPTION + " exception is returned.")
	@TestLabel(label="Reference: 2412")
	public void test_12() 
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
		
		
		File file = new File(StringUtils.cleanPath(uploadTransDir + "/" + nonWorkStatusTrans));
		
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
		
		
		CallResponse resp = processTransaction(null, transData, requestHeaders, HttpStatus.OK);
		
		
		assertTrue("Request was a success", !resp.isSuccess());
		
		assertTrue("Unexpected Exception returned. Expected: " + Utils.UNKNOWN_TRANSACTION_EXCEPTION +
				"<br>Recieved: " + resp.getError().getErrorCode(),
				resp.getError().getErrorCode().equals(Utils.UNKNOWN_TRANSACTION_EXCEPTION));
			
	}
	
	
	
	@Test
	@TestDescription(desc="Valid date header and valid authorization token. The request body should contain " +
						  "a valid Fieldreach transaction message that has been previously processed. " +
						  "No exceptions are returned and the transaction is disgarded.")
	@TestLabel(label="Reference: 2413")
	public void test_13() 
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
		
		File file = new File(StringUtils.cleanPath(uploadTransDir + "/" + alreadyProcessedTrans));
		
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
		
		
		CallResponse resp = processTransaction(alreadyProcessedTrans, transData, requestHeaders, HttpStatus.OK);
		
		
		assertTrue("Request was a not a success: " + resp.getError().getErrorCode(), 
				resp.isSuccess());
		
		
		assertTrue("Unexpected Exception returned. Recieved: " + resp.getError().getErrorCode(),
				resp.getError().getErrorCode() == null);
			
	}

	
	@Test
	@TestDescription(desc="Valid date header and valid authorization token. The request body should contain " +
						  "a valid Fieldreach transaction message that has been not previously processed and does " +
						  "not have a corresponding entry in the work issued table. " +
						  "Check that " + Utils.WORKORDER_NOT_FOUND_EXCEPTION + " exception is returned.")
	@TestLabel(label="Reference: 2414")
	public void test_14() 
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
		
		File file = new File(StringUtils.cleanPath(uploadTransDir + "/" + notInWorkIssuedTrans));
		
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
		
		
		CallResponse resp = processTransaction(notInWorkIssuedTrans, transData, requestHeaders, HttpStatus.OK);
		
		
		assertTrue("Request was a success", !resp.isSuccess());
		
		assertTrue("Unexpected Exception returned. Expected: " + Utils.WORKORDER_NOT_FOUND_EXCEPTION +
				"<br>Recieved: " + resp.getError().getErrorCode(),
				resp.getError().getErrorCode().equals(Utils.WORKORDER_NOT_FOUND_EXCEPTION));
			
	}
		

	@Test
	@TestDescription(desc="Valid date header and valid authorization token. The request body should contain " +
						  "a valid Fieldreach transaction message that has been not previously processed and " +
						  "has a corresponding entry in the work issued table. The message should not be a workorder closure or " +
						  "can't do message. Check that no exceptions are returned by the web service.")
	@TestLabel(label="Reference: 2415")
	public void test_15() 
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
		
		
		CallResponse resp = processTransaction(null, transData, requestHeaders, HttpStatus.OK);
		
		
		assertTrue("Request was not a success. Error Code: " +
				resp.getError().getErrorCode(), resp.isSuccess());
		
		
		assertTrue("Unexpected Exception returned: " + resp.getError().getErrorCode(),
				resp.getError().getErrorCode() == null);
			
	}
	

	@Test
	@TestDescription(desc="Valid date header and valid authorization token. The request body should contain " +
						  "a valid Fieldreach transaction message that has been not previously processed and " +
						  "has a corresponding entry in the work issued table. The message should be a workorder closure " +
						  "message. Check that no exceptions are returned by the web service.")
	@TestLabel(label="Reference: 2416")
	public void test_16() 
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
		
		File file = new File(StringUtils.cleanPath(uploadTransDir + "/" + validWorkStatusTransCloseWork));
		
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
		
		
		CallResponse resp = processTransaction(null, transData, requestHeaders, HttpStatus.OK);
		
		
		assertTrue("Request was not a success. Error Code: " +
				resp.getError().getErrorCode(), resp.isSuccess());
		
		
		assertTrue("Unexpected Exception returned: " + resp.getError().getErrorCode(),
				resp.getError().getErrorCode() == null);
			
	}
	
	
	
	@Test
	@TestDescription(desc="Valid date header and valid authorization token. The request body should contain " +
						  "a valid Fieldreach transaction message that has been not previously processed and " +
						  "has a corresponding entry in the work issued table. The message should be a workorder cant do " +
						  "message. Check that no exceptions are returned by the web service.")
	@TestLabel(label="Reference: 2417")
	public void test_17() 
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
		
		File file = new File(StringUtils.cleanPath(uploadTransDir + "/" + validWorkStatusTransCantDoWork));
		
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
		
		
		CallResponse resp = processTransaction(null, transData, requestHeaders, HttpStatus.OK);
		
		
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
