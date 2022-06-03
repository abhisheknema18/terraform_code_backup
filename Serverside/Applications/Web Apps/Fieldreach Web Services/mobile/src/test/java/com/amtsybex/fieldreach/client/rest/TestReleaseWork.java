package com.amtsybex.fieldreach.client.rest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;
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

public class TestReleaseWork extends CommonBase {

	@Test
	@TestDescription(desc="Call release work without districtCode parameter and request body. HTTP status" +
			"400 is returned.")
	@TestLabel(label="Reference: 3901")
	public void test_1() 
	{
		releaseWork(null, super.existingWO, "", HttpStatus.BAD_REQUEST);
	}
	
	@Test
	@TestDescription(desc="Call release work with the districtCode parameter and without a request body. " +
			"HTTP status 400 is returned.")
	@TestLabel(label="Reference: 3902")
	public void test_2() 
	{
		releaseWork(super.woDC, super.existingWO, "", HttpStatus.BAD_REQUEST);
	}
	
	@Test
	@TestDescription(desc="Call release work without the districtCode parameter. Supply a workorder that already exists" +
			" in the workissued table in the request body. A " + Utils.WORKORDER_ALREADY_EXISTS_EXCEPTION + 
			" message should be returned.")
	@TestLabel(label="Reference: 3903")
	public void test_3() 
	{
		log.debug(">>> test_3");
		
		File file = new File(StringUtils.cleanPath(super.uploadWorkOrderDir + "/" + 
				Utils.WORKORDER_FILE_PREFIX + super.existingWO + Utils.WORKORDER_FILE_EXTENSION));
		
		String woData = null;
		
		if (file.exists() && file.isFile())
		{
			try 
			{
				byte[] bytes = Common.getBytesFromFile(file);
				woData = new String(bytes);
			} 
			catch (FileNotFoundException e) 
			{

				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
			} 
			catch (IOException e)
			{
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
			}
		}
		else
		{
			log.error("Error creating request body from work order file");
			fail("Unable to extract work order content to build request body.");
		}
		
		
		CallResponse resp = releaseWork(null, super.existingWO, woData, HttpStatus.OK);
		
		
		//Ensure response was not a success
		assertTrue("Response was a success.", !resp.isSuccess());
		
		//Check for workorder already exists exception
		assertTrue("Workorder does not already exist in the workissued table.",
				resp.getError().getErrorCode().equals(Utils.WORKORDER_ALREADY_EXISTS_EXCEPTION));
		

		log.debug("<<< test_3");
	}
	
	@Test
	@TestDescription(desc="Call release work with the districtCode parameter. Supply a workorder that already exists" +
			" in the workissued table in the request body. A " + Utils.WORKORDER_ALREADY_EXISTS_EXCEPTION + 
			" message should be returned.")
	@TestLabel(label="Reference: 3904")
	public void test_4() 
	{
		log.debug(">>> test_4");
		
		File file = new File(StringUtils.cleanPath(super.uploadWorkOrderDir + "/" + 
				Utils.WORKORDER_FILE_PREFIX + super.existingWO + Utils.WORKORDER_FILE_EXTENSION));
		
		String woData = null;
		
		if (file.exists() && file.isFile())
		{
			try 
			{
				byte[] bytes = Common.getBytesFromFile(file);
				woData = new String(bytes);
			} 
			catch (FileNotFoundException e) 
			{

				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
			} 
			catch (IOException e)
			{
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
			}
		}
		else
		{
			log.error("Error creating request body from work order file");
			fail("Unable to extract work order content to build request body.");
		}
		
		
		CallResponse resp = releaseWork(super.woDC, super.existingWO, woData, HttpStatus.OK);
		
		
		//Ensure response was not a success
		assertTrue("Response was a success.", !resp.isSuccess());
		
		//Check for workorder already exists exception
		assertTrue("Workorder does not already exist in the workissued table.",
				resp.getError().getErrorCode().equals(Utils.WORKORDER_ALREADY_EXISTS_EXCEPTION));
		

		log.debug("<<< test_4");
	}
	
	
	@Test
	@TestDescription(desc="Call release work without the districtCode parameter. Supply an invalid workorder that" +
			" does not already exist in the workissued table in the request body. " +
			"A " + Utils.WORKORDER_VALIDATION_EXCEPTION + " message should be returned.")
	@TestLabel(label="Reference: 3905")
	public void test_5() 
	{
		log.debug(">>> test_5");
		
		File file = new File(StringUtils.cleanPath(super.uploadWorkOrderDir + "/" + 
				Utils.WORKORDER_FILE_PREFIX + super.invalidWO + Utils.WORKORDER_FILE_EXTENSION));
		
		String woData = null;
		
		if (file.exists() && file.isFile())
		{
			try 
			{
				byte[] bytes = Common.getBytesFromFile(file);
				woData = new String(bytes);
			} 
			catch (FileNotFoundException e) 
			{

				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
			} 
			catch (IOException e)
			{
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
			}
		}
		else
		{
			log.error("Error creating request body from work order file");
			fail("Unable to extract work order content to build request body.");
		}
		
		
		CallResponse resp = releaseWork(null, super.invalidWO, woData, HttpStatus.OK);
		
		
		//Ensure response was not a success
		assertTrue("Response was a success.", !resp.isSuccess());
		
		//Ensure workorder validation was not passed
		assertTrue("Workorder file passsed validation.",
				resp.getError().getErrorCode().equals(Utils.WORKORDER_VALIDATION_EXCEPTION));
		

		log.debug("<<< test_5");
	}
	
	
	@Test
	@TestDescription(desc="Call issue work with the districtCode parameter. Supply an invalid workorder that" +
			" does not already exist in the workissued table in the request body. " +
			"A " + Utils.WORKORDER_VALIDATION_EXCEPTION + " message should be returned.")
	@TestLabel(label="Reference: 3906")
	public void test_6() 
	{
		log.debug(">>> test_6");
		
		File file = new File(StringUtils.cleanPath(super.uploadWorkOrderDir + "/" + 
				Utils.WORKORDER_FILE_PREFIX + super.invalidWO + Utils.WORKORDER_FILE_EXTENSION));
		
		String woData = null;
		
		if (file.exists() && file.isFile())
		{
			try 
			{
				byte[] bytes = Common.getBytesFromFile(file);
				woData = new String(bytes);
			} 
			catch (FileNotFoundException e) 
			{
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
			} 
			catch (IOException e)
			{
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
			}
		}
		else
		{
			log.error("Error creating request body from work order file");
			fail("Unable to extract work order content to build request body.");
		}
		
		
		CallResponse resp = releaseWork(super.woDC, super.invalidWO, woData, HttpStatus.OK);
		
		
		//Ensure response was not a success
		assertTrue("Response was a success.", !resp.isSuccess());
		
		//Ensure workorder validation was not passed
		assertTrue("Workorder file passsed validation.",
				resp.getError().getErrorCode().equals(Utils.WORKORDER_VALIDATION_EXCEPTION));
		

		log.debug("<<< test_6");
	}
		
	
	@Test
	@TestDescription(desc="Call release work without the districtCode parameter. Supply a valid workorder that" +
			" does not already exist in the workissued table in the request body. Ensure the file contains " +
			" no usercode or workgroupcode information. A " + Utils.WORKORDER_VALIDATION_EXCEPTION + " message should be returned.")
	@TestLabel(label="Reference: 3907")
	public void test_7() 
	{
		log.debug(">>> test_7");
		
		File file = new File(StringUtils.cleanPath(super.uploadWorkOrderDir + "/" + 
				Utils.WORKORDER_FILE_PREFIX + super.WONoUserCodeOrWGCode + Utils.WORKORDER_FILE_EXTENSION));
		
		String woData = null;
		
		if (file.exists() && file.isFile())
		{
			try 
			{
				byte[] bytes = Common.getBytesFromFile(file);
				woData = new String(bytes);
			} 
			catch (FileNotFoundException e) 
			{
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
			} 
			catch (IOException e)
			{
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
			}
		}
		else
		{
			log.error("Error creating request body from work order file");
			fail("Unable to extract work order content to build request body.");
		}
		
		
		CallResponse resp = releaseWork(super.woDC, super.WONoUserCodeOrWGCode, woData, HttpStatus.OK);
		
	
		//Ensure response was not a success
		assertTrue("Response was a success.", !resp.isSuccess());
		
		//Check for unexpected exception
		assertTrue("Unexpected Exception occured. Expected: " + Utils.WORKORDER_VALIDATION_EXCEPTION + 
				"<br>Recieved: " + resp.getError().getErrorCode(),
				resp.getError().getErrorCode().equals(Utils.WORKORDER_VALIDATION_EXCEPTION));
		

		log.debug("<<< test_7");
	}
	
	
	
	@Test
	@TestDescription(desc="Call release work with the districtCode parameter. Supply a valid workorder that" +
			" does not already exist in the workissued table in the request body. Ensure the file contains " +
			" no usercode or workgroupcode information. A " + Utils.WORKORDER_VALIDATION_EXCEPTION + " message should be returned.")
	@TestLabel(label="Reference: 3908")
	public void test_8() 
	{
		log.debug(">>> test_8");
		
		File file = new File(StringUtils.cleanPath(super.uploadWorkOrderDir + "/" + 
				Utils.WORKORDER_FILE_PREFIX + super.WONoUserCodeOrWGCode + Utils.WORKORDER_FILE_EXTENSION));
		
		String woData = null;
		
		if (file.exists() && file.isFile())
		{
			try 
			{
				byte[] bytes = Common.getBytesFromFile(file);
				woData = new String(bytes);
			} 
			catch (FileNotFoundException e) 
			{

				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
			} 
			catch (IOException e)
			{
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
			}
		}
		else
		{
			log.error("Error creating request body from work order file");
			fail("Unable to extract work order content to build request body.");
		}
		
		
		CallResponse resp = releaseWork(super.woDC, super.WONoUserCodeOrWGCode, woData, HttpStatus.OK);
		
	
		//Ensure response was not a success
		assertTrue("Response was a success.", !resp.isSuccess());
		
		//Check for unexpected exception
		assertTrue("Unexpected Exception occured. Expected: " + Utils.WORKORDER_VALIDATION_EXCEPTION + 
				"<br>Recieved: " + resp.getError().getErrorCode(),
				resp.getError().getErrorCode().equals(Utils.WORKORDER_VALIDATION_EXCEPTION));
		

		log.debug("<<< test_8");
	}
	
	@Test
	@TestDescription(desc="Call release work without the districtCode parameter. Supply a valid workorder that" +
			" does not already exist in the workissued table in the request body. Ensure the file contains " +
			" no usercode only workgroupcode information. No exceptions will be returned.")
	@TestLabel(label="Reference: 3909")
	public void test_9() 
	{
		log.debug(">>> test_9");
		
		File file = new File(StringUtils.cleanPath(super.uploadWorkOrderDir + "/" + 
				Utils.WORKORDER_FILE_PREFIX + super.validRELWO1 + Utils.WORKORDER_FILE_EXTENSION));
		
		String woData = null;
		
		if (file.exists() && file.isFile())
		{
			try 
			{
				byte[] bytes = Common.getBytesFromFile(file);
				woData = new String(bytes);
			} 
			catch (FileNotFoundException e) 
			{
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
			} 
			catch (IOException e)
			{
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
			}
		}
		else
		{
			log.error("Error creating request body from work order file");
			fail("Unable to extract work order content to build request body.");
		}
		
		
		CallResponse resp = releaseWork(null, super.validRELWO1, woData, HttpStatus.OK);
		
	
		//Ensure response was a success
		assertTrue("Response was not a success. Error Code: " + resp.getError().getErrorCode(), 
				resp.isSuccess());
		
		//Check no exceptions are returned
		assertTrue("Unexpected Exception occured: " + resp.getError().getErrorCode(),
				resp.getError().getErrorCode() == null);
		

		log.debug("<<< test_9");
	}
	
	
	@Test
	@TestDescription(desc="Call release work with the districtCode parameter. Supply a valid workorder that" +
			" does not already exist in the workissued table in the request body. Ensure the file contains " +
			" no usercode only workgroupcode information. No exceptions will be returned.")
	@TestLabel(label="Reference: 3910")
	public void test_10() 
	{
		log.debug(">>> test_10");
		
		File file = new File(StringUtils.cleanPath(super.uploadWorkOrderDir + "/" + 
				Utils.WORKORDER_FILE_PREFIX + super.validRELWO1 + Utils.WORKORDER_FILE_EXTENSION));
		
		String woData = null;
		
		if (file.exists() && file.isFile())
		{
			try 
			{
				byte[] bytes = Common.getBytesFromFile(file);
				woData = new String(bytes);
			} 
			catch (FileNotFoundException e) 
			{
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
			} 
			catch (IOException e)
			{
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
			}
		}
		else
		{
			log.error("Error creating request body from work order file");
			fail("Unable to extract work order content to build request body.");
		}
		
		
		CallResponse resp = releaseWork(super.woDC, super.validRELWO1, woData, HttpStatus.OK);
		
	
		//Ensure response was a success
		assertTrue("Response was not a success. Error Code: " + resp.getError().getErrorCode(), 
				resp.isSuccess());
		
		//Check no exceptions are returned
		assertTrue("Unexpected Exception occured: " + resp.getError().getErrorCode(),
				resp.getError().getErrorCode() == null);
		

		log.debug("<<< test_10");
	}
	
	@Test
	@TestDescription(desc="Call release work without the districtCode parameter. Supply a valid workorder that" +
			" does not already exist in the workissued table in the request body. Ensure the file contains " +
			" workgroupcode and usercode information. No exceptions will be returned.")
	@TestLabel(label="Reference: 3911")
	public void test_11() 
	{
		log.debug(">>> test_11");
		
		File file = new File(StringUtils.cleanPath(super.uploadWorkOrderDir + "/" + 
				Utils.WORKORDER_FILE_PREFIX + super.validRELWO2 + Utils.WORKORDER_FILE_EXTENSION));
		
		String woData = null;
		
		if (file.exists() && file.isFile())
		{
			try 
			{
				byte[] bytes = Common.getBytesFromFile(file);
				woData = new String(bytes);
			} 
			catch (FileNotFoundException e) 
			{
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
			} 
			catch (IOException e)
			{
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
			}
		}
		else
		{
			log.error("Error creating request body from work order file");
			fail("Unable to extract work order content to build request body.");
		}
		
		
		CallResponse resp = releaseWork(null, super.validRELWO2, woData, HttpStatus.OK);
		
	
		//Ensure response was a success
		assertTrue("Response was not a success. Error Code: " + resp.getError().getErrorCode(), 
				resp.isSuccess());
		
		//Check no exceptions are returned
		assertTrue("Unexpected Exception occured: " + resp.getError().getErrorCode(),
				resp.getError().getErrorCode() == null);
		

		log.debug("<<< test_11");
	}
	
	

	@Test
	@TestDescription(desc="Call release work with the districtCode parameter. Supply a valid workorder that" +
			" does not already exist in the workissued table in the request body. Ensure the file contains " +
			" workgroupcode and usercode information. No exceptions will be returned.")
	@TestLabel(label="Reference: 3912")
	public void test_12() 
	{
		log.debug(">>> test_12");
		
		File file = new File(StringUtils.cleanPath(super.uploadWorkOrderDir + "/" + 
				Utils.WORKORDER_FILE_PREFIX + super.validRELWO2 + Utils.WORKORDER_FILE_EXTENSION));
		
		String woData = null;
		
		if (file.exists() && file.isFile())
		{
			try 
			{
				byte[] bytes = Common.getBytesFromFile(file);
				woData = new String(bytes);
			} 
			catch (FileNotFoundException e) 
			{
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
			} 
			catch (IOException e)
			{
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
			}
		}
		else
		{
			log.error("Error creating request body from work order file");
			fail("Unable to extract work order content to build request body.");
		}
		
		
		CallResponse resp = releaseWork(super.woDC, super.validRELWO2, woData, HttpStatus.OK);
		
	
		//Ensure response was a success
		assertTrue("Response was not a success. Error Code: " + resp.getError().getErrorCode(), 
				resp.isSuccess());
		
		//Check no exceptions are returned
		assertTrue("Unexpected Exception occured: " + resp.getError().getErrorCode(),
				resp.getError().getErrorCode() == null);
		

		log.debug("<<< test_12");
	}
	
	@Test
	@TestDescription(desc="Call release work with the districtCode parameter. Supply a valid workorder." +
			"  Send the request aas a FWS Authentication request. An Unauthorised will be returned.")
	@TestLabel(label="Reference: 3913")
	public void test_13() 
	{
		log.debug(">>> test_13");
		
		File file = new File(StringUtils.cleanPath(super.uploadWorkOrderDir + "/" + 
				Utils.WORKORDER_FILE_PREFIX + super.validRELWO2 + Utils.WORKORDER_FILE_EXTENSION));
		
		String woData = null;
		
		if (file.exists() && file.isFile())
		{
			try 
			{
				byte[] bytes = Common.getBytesFromFile(file);
				woData = new String(bytes);
			} 
			catch (FileNotFoundException e) 
			{
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
			} 
			catch (IOException e)
			{
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
			}
		}
		else
		{
			log.error("Error creating request body from work order file");
			fail("Unable to extract work order content to build request body.");
		}
		
		
		releaseWork(super.woDC, super.validRELWO2, woData, HttpStatus.UNAUTHORIZED, false);

		log.debug("<<< test_13");
	}
	
	protected CallResponse releaseWork(String districtCode, String woNo, String body,
			HttpStatus expectedStatusCode) {
		return this.releaseWork(districtCode, woNo, body, expectedStatusCode, true);
	}
	
	/**
	 * Method to call the release work web service
	 * 
	 * @param districtCode
	 * value of the option districtCode parameter. Null means it should not be used.
	 * 
	 * @param woNo
	 * workorderno of the work order being released.
	 * 
	 * @param body
	 * Body of the request
	 * 
	 * @param expectedStatusCode
	 * expected HTTP status code of the request being made
	 * 
	 */
	protected CallResponse releaseWork(String districtCode, String woNo, String body,
			HttpStatus expectedStatusCode, boolean iwsType)
	{
		
		log.debug(">>> releaseWork districtCode= " + districtCode +
				" body=xxxx");
		
		final String wsURL = baseURL + "releasework/" + woNo;

		RestOperations restTemplate = null;
		restTemplate = new RestTemplate();

		StringBuffer restCall;
		CallResponse resp = null;

		
		try 
		{
			//
			// Build REST URL to call
			restCall = new StringBuffer(wsURL);
			
			if (districtCode != null)
				restCall.append("?districtCode=" + districtCode);

			//
			// Request Headers
			HttpHeaders requestHeaders = new HttpHeaders();
			String dateHeader = getDateHeader();
			log.debug("Date header=" + dateHeader);
			requestHeaders.set("Date", dateHeader);
			requestHeaders.set("x-fws-deviceid", deviceid);

			requestHeaders.set("x-fws-applicationidentifier", applicationIdentifier);
			
			if(iwsType) {
				requestHeaders.set("Authorization", getIWSAuthorisationHeaderValue(super.workissuedUserCode, super.workissuedUserPassword, dateHeader));
			}else {
				requestHeaders.set("Authorization", getAuthorisationHeaderValue(unrevokedUserCode, unrevokedPassword, dateHeader));
			}
			
			requestHeaders.set("x-fws-appCode", "FWOM");
			
			//
			// Build request Body
			String requestBody = body;

			HttpEntity<String> requestEntity = new HttpEntity<String>(
					requestBody, requestHeaders);

			//
			// Make REST call and retrieve the results
			ResponseEntity<String> resultsExchange = restTemplate.exchange(
					restCall.toString(), HttpMethod.PUT, requestEntity,
					String.class);


			String results = resultsExchange.getBody();

			log.debug("issuework results : " + results);
			log.debug("HTTP Status Code: " + resultsExchange.getStatusCode());

			assertTrue("Result is null", results != null);
			assertTrue("Unexpected HTTP Status Code. Expected: " + expectedStatusCode.value() + 
					"<br>Recieved: ", resultsExchange.getStatusCode().value() == expectedStatusCode.value());

			//
			// Parse XML received from the response into an
			// CallResponse object
			try {
				XStream xstream = new XStream(new StaxDriver());
				
				xstream.alias("CallResponse",
						CallResponse.class);

				resp = (CallResponse) xstream.fromXML(results);
				
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
			// Check to see if the HHTP status code is what we expect
			if (he.getStatusCode() == expectedStatusCode) 
			{
				log.debug(expectedStatusCode
						+ " status code returned as expected.");
			}
			else 
			{
				log.error("Client Exception :" + he.getMessage());
				fail("Client Exception :" + he.getMessage());
			}
		}
		catch (Exception e) 
		{
			log.error("Client Exception :" + e.getMessage());
			fail("Client Exception :" + e.getMessage());
		}

		log.debug("<<< releaseWork");
		
		return resp;
	}
}
