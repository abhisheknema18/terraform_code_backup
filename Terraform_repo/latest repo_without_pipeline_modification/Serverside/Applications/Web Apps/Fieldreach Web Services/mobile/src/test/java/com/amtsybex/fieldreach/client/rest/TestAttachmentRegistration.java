/**
 * Author:  T Murray
 * Date:    04/02/2015
 * Project: FDE029
 * 
 * Copyright AMT-Sybex 2015
 */
package com.amtsybex.fieldreach.client.rest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;
import static org.junit.Assert.*;

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

/**
 * Class to facilitate automated testing of the facility to update workorders as
 * implemented for FDE019
 */
public class TestAttachmentRegistration extends CommonBase {

	@Test
	@TestDescription(desc = "Call attachment registration without request body. "
			+ "HTTP status 400 is returned.")
	@TestLabel(label = "Reference: 3601")
	public void test_1() {

		registerAttachment(null, attachmentRegInvalidWO, "",
				HttpStatus.BAD_REQUEST);
	}

	@Test
	@TestDescription(desc = "Call attachment registration specifying a request body with "
			+ "the 'fileName' element missing. HTTP status 400 is returned.")
	@TestLabel(label = "Reference: 3602")
	public void test_2() {

		String requestBody = this.createRequestBody(null,
				attachmentRegDescription, "DOC", false);

		registerAttachment(null, attachmentRegInvalidWO, requestBody,
				HttpStatus.BAD_REQUEST);
	}

	@Test
	@TestDescription(desc = "Call attachment registration specifying a request body with "
			+ "the 'fileContent' element missing. HTTP status 400 is returned.")
	@TestLabel(label = "Reference: 3603")
	public void test_3() {

		String requestBody = this.createRequestBody(attachmentRegWORFile,
				attachmentRegDescription, "DOC", false);

		registerAttachment(null, attachmentRegInvalidWO, requestBody,
				HttpStatus.BAD_REQUEST);
	}

	@Test
	@TestDescription(desc = "Call attachment registration specifying a work order that" +
			"does not exist in the WorkIssued table. HTTP status 200  returned and "
			+ "error code " + Utils.WORKORDER_NOT_FOUND_EXCEPTION
			+ " is returned.")
	@TestLabel(label = "Reference: 3604")
	public void test_4() {

		String requestBody = this.createRequestBody(attachmentRegWORFile,
				attachmentRegDescription, "DOC", true);

		CallResponse resp = registerAttachment(null, attachmentRegInvalidWO,
				requestBody, HttpStatus.OK);

		assertTrue("No response returned.", resp != null);

		assertTrue("Exception expected but not returned.", resp.getError()
				.getErrorCode() != null);

		assertTrue("Unexepected exception returned. Expected: "
				+ Utils.WORKORDER_NOT_FOUND_EXCEPTION + "<br>Returned: "
				+ resp.getError().getErrorCode(), resp.getError()
				.getErrorCode().equals(Utils.WORKORDER_NOT_FOUND_EXCEPTION));
	}
	
	@Test
	@TestDescription(desc = "Call attachment registration specifying a work order that" +
			"does  exist in the WorkIssued table but has a work status matching the configured" +
			"'Cant do' status. HTTP status 200 returned and  error code " + 
			Utils.ATTACHMENT_REGISTRATION_EXCEPTION + " is returned.")
	@TestLabel(label = "Reference: 3605")
	public void test_5() {

		String requestBody = this.createRequestBody(attachmentRegWORFile,
				attachmentRegDescription, "DOC", true);

		CallResponse resp = registerAttachment(null, attachmentRegCantDoWO,
				requestBody, HttpStatus.OK);

		assertTrue("No response returned.", resp != null);

		assertTrue("Exception expected but not returned.", resp.getError()
				.getErrorCode() != null);

		assertTrue("Unexepected exception returned. Expected: "
				+ Utils.ATTACHMENT_REGISTRATION_EXCEPTION + "<br>Returned: "
				+ resp.getError().getErrorCode(), resp.getError()
				.getErrorCode().equals(Utils.ATTACHMENT_REGISTRATION_EXCEPTION));
	}
	
	@Test
	@TestDescription(desc = "Call attachment registration specifying a work order that" +
			"does  exist in the WorkIssued table but has a work status matching the configured" +
			"'close work' status. HTTP status 200 returned and  error code " + 
			Utils.ATTACHMENT_REGISTRATION_EXCEPTION + " is returned.")
	@TestLabel(label = "Reference: 3606")
	public void test_6() {

		String requestBody = this.createRequestBody(attachmentRegWORFile,
				attachmentRegDescription, "DOC", true);

		CallResponse resp = registerAttachment(null, attachmentRegCloseWO,
				requestBody, HttpStatus.OK);

		assertTrue("No response returned.", resp != null);

		assertTrue("Exception expected but not returned.", resp.getError()
				.getErrorCode() != null);

		assertTrue("Unexepected exception returned. Expected: "
				+ Utils.ATTACHMENT_REGISTRATION_EXCEPTION + "<br>Returned: "
				+ resp.getError().getErrorCode(), resp.getError()
				.getErrorCode().equals(Utils.ATTACHMENT_REGISTRATION_EXCEPTION));
	}
	
	@Test
	@TestDescription(desc = "Call attachment registration specifying a work order that" +
			"does exist in the WorkIssued table but has a work status matching the configured" +
			"'cancel work' status. HTTP status 200 returned and  error code " + 
			Utils.ATTACHMENT_REGISTRATION_EXCEPTION + " is returned.")
	@TestLabel(label = "Reference: 3607")
	public void test_7() {

		String requestBody = this.createRequestBody(attachmentRegWORFile,
				attachmentRegDescription, "DOC", true);

		CallResponse resp = registerAttachment(null, attachmentRegCancelledWO,
				requestBody, HttpStatus.OK);

		assertTrue("No response returned.", resp != null);

		assertTrue("Exception expected but not returned.", resp.getError()
				.getErrorCode() != null);

		assertTrue("Unexepected exception returned. Expected: "
				+ Utils.ATTACHMENT_REGISTRATION_EXCEPTION + "<br>Returned: "
				+ resp.getError().getErrorCode(), resp.getError()
				.getErrorCode().equals(Utils.ATTACHMENT_REGISTRATION_EXCEPTION));
	}
	
	@Test
	@TestDescription(desc = "Call attachment registration specifying a work order that" +
			"exists in the WorkIssued table and has a status that allows attachment " +
			"registration. HTTP status 200 returned and no errors as the WorkIssuesFileRefs " +
			"table is updated and the attachment created on the file system. <br><br>" +
			"Manually verify the WorkIssuedFileRefs table has been updated correctly and the" +
			"attachment has been created in the correct location.")
	@TestLabel(label = "Reference: 3608")
	public void test_8() {

		String requestBody = this.createRequestBody(attachmentRegWORFile,
				attachmentRegDescription, "WOR", true);

		CallResponse resp = registerAttachment(null, attachmentRegValidWO,
				requestBody, HttpStatus.OK);

		assertTrue("No response returned.", resp != null);

		assertTrue("Request failed!.", resp.isSuccess());
	}
	
	@Test
	@TestDescription(desc = "Call attachment registration specifying a work order that" +
			"exists in the WorkIssued table and has a status that allows attachment " +
			"registration. HTTP status 200 returned and no errors as the WorkIssuesFileRefs " +
			"table is updated and the attachment created on the file system. <br><br>" +
			"Manually verify the WorkIssuedFileRefs table has been updated correctly and the" +
			"attachment has been created in the correct location.")
	@TestLabel(label = "Reference: 3609")
	public void test_9() {

		String requestBody = this.createRequestBody(attachmentRegDOCFile,
				attachmentRegDescription, "DOC", true);

		CallResponse resp = registerAttachment(null, attachmentRegValidWO,
				requestBody, HttpStatus.OK);

		assertTrue("No response returned.", resp != null);

		assertTrue("Request failed!.", resp.isSuccess());
	}
	
	@Test
	@TestDescription(desc = "Call attachment registration specifying a work order that" +
			"exists in the WorkIssued table and has a status that allows attachment " +
			"registration. Supply a FWS authorization header, an UnauthorizedException should be returned")
	@TestLabel(label = "Reference: 3610")
	public void test_10() {

		String requestBody = this.createRequestBody(attachmentRegDOCFile,
				attachmentRegDescription, "DOC", true);

		registerAttachment(null, attachmentRegValidWO,
				requestBody, HttpStatus.UNAUTHORIZED, false);

	}
	
	protected CallResponse registerAttachment(String districtCode, String woNo, String body, HttpStatus expectedStatusCode) {
		return this.registerAttachment(districtCode, woNo, body, expectedStatusCode, true);
	}
	/**
	 * Method to call the register attachment web service
	 * 
	 * @param districtCode
	 *            value of the option districtCode parameter. Null means it
	 *            should not be used.
	 * 
	 * @param woNo
	 *            workorderno of the work order being issued.
	 * 
	 * @param body
	 *            Body of the request
	 * 
	 * @param expectedStatusCode
	 *            expected HTTP status code of the request being made
	 * 
	 */
	protected CallResponse registerAttachment(String districtCode, String woNo,
			String body, HttpStatus expectedStatusCode, boolean iwsType) {

		log.debug(">>> registerAttachment districtCode= " + districtCode
				+ " body=xxxx");

		final String wsURL = baseURL + "workorder/" + woNo + "/attachment";

		RestOperations restTemplate = null;
		restTemplate = new RestTemplate();

		StringBuffer restCall;
		CallResponse resp = null;

		try {

			// Build REST URL to call
			restCall = new StringBuffer(wsURL);

			if (districtCode != null)
				restCall.append("?districtCode=" + districtCode);

			// Request Headers
			HttpHeaders requestHeaders = new HttpHeaders();
			requestHeaders.set("x-fws-deviceid", deviceid);

			String dateHeader = getDateHeader();
			log.debug("Date header=" + dateHeader);
			requestHeaders.set("Date", dateHeader);
			
			if(iwsType) {
				requestHeaders.set("Authorization", getIWSAuthorisationHeaderValue(super.workissuedUserCode, super.workissuedUserPassword, dateHeader));
			}else {
				requestHeaders.set("Authorization", getAuthorisationHeaderValue(unrevokedUserCode, unrevokedPassword, dateHeader));
			}
			
			requestHeaders.set("x-fws-applicationidentifier",
					applicationIdentifier);

			// Build request Body
			String requestBody = body;

			HttpEntity<String> requestEntity = new HttpEntity<String>(
					requestBody, requestHeaders);

			// Make REST call and retrieve the results
			ResponseEntity<String> resultsExchange = restTemplate.exchange(
					restCall.toString(), HttpMethod.PUT, requestEntity,
					String.class);

			String results = resultsExchange.getBody();

			log.debug("update work results : " + results);
			log.debug("HTTP Status Code: " + resultsExchange.getStatusCode());

			assertTrue("Result is null", results != null);
			assertTrue(
					"Unexpected HTTP Status Code. Expected: "
							+ expectedStatusCode.value() + "<br>Recieved: ",
					resultsExchange.getStatusCode().value() == expectedStatusCode
							.value());

			// Parse XML received from the response into an
			// CallResponse object
			try {

				XStream xstream = new XStream(new StaxDriver());

				xstream.alias("CallResponse", CallResponse.class);

				resp = (CallResponse) xstream.fromXML(results);

			} catch (XmlMappingException e) {

				log.error("Client Mapping Exception :" + e.getMessage());
				fail("Client Mapping Exception :" + e.getMessage());
			}

		} catch (HttpClientErrorException he) {

			// Check to see if the HHTP status code is what we expect
			if (he.getStatusCode() == expectedStatusCode) {

				log.debug(expectedStatusCode
						+ " status code returned as expected.");

			} else {

				log.error("Client Exception :" + he.getMessage());
				fail("Client Exception :" + he.getMessage());
			}

		} catch (Exception e) {

			log.error("Client Exception :" + e.getMessage());
			fail("Client Exception :" + e.getMessage());
		}

		log.debug("<<< registerAttachment");

		return resp;
	}

	private String createRequestBody(String filename, String description, String fileType,
			boolean getContent) {

		String retVal = "<RegisterAttachment><fileName>";


		if(filename != null)
			retVal = retVal + filename;
		
		retVal = retVal + "</fileName><fileDesc>";
		
		if(description != null)
			retVal = retVal + description;
		
		retVal = retVal + description + "</fileDesc>" +
				"<fileType>" + fileType + "</fileType><data>";
		
		String checksum = "";
		if (getContent) {

			File file = new File(
					StringUtils.cleanPath(super.attachmentRegUploadDir
							+ File.separator + filename));

			try {

				byte[] fileBytes = Common
						.getBytesFromFile(file);
				
				checksum = Common.generateMD5Checksum(fileBytes);
				String fileData = Common.encodeBase64(fileBytes);

				retVal = retVal + fileData;
				
			} catch (FileNotFoundException e) {

				log.error(e.getMessage());
				fail("Unable to build request body: " + e.getMessage());

			} catch (IOException e) {

				log.error(e.getMessage());
				fail("Unable to build request body: " + e.getMessage());
			}

		}
		
		retVal = retVal + "</data><checksum>" + checksum + "</checksum></RegisterAttachment>";

		return retVal;
	}

}
