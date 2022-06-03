/**
 * Author:  T Murray
 * Date:    11/06/2014
 * Project: FDE026
 * 
 * Copyright AMT-Sybex 2014
 */
package com.amtsybex.fieldreach.client.rest;

import org.slf4j.LoggerFactory; 
import org.slf4j.Logger;  
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.XmlMappingException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import com.amtsybex.fieldreach.services.messages.response.SupportFileListResponse;
import com.amtsybex.fieldreach.services.messages.types.SupportFile;
import com.amtsybex.fieldreach.services.test.annotations.TestDescription;
import com.amtsybex.fieldreach.services.test.annotations.TestLabel;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import org.junit.* ;
import static org.junit.Assert.* ;


public class TestGetSupportFileList extends CommonBase {

	static Logger log = LoggerFactory.getLogger(TestGetSupportFileList.class.getName());


	@Test
	@TestDescription(desc="No date header and valid authorizaton token.")
	@TestLabel(label="Reference: 3201")
	public void test_1() {

		HttpHeaders requestHeaders = new HttpHeaders();
		String dateHeader = getDateHeader();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getSupportFileList(requestHeaders, HttpStatus.UNAUTHORIZED);
	}
	
	
	@Test
	@TestDescription(desc="Invalid header and valid authorizaton token.")
	@TestLabel(label="Reference: 3202")
	public void test_2() {
		

		String dateHeader = "invalid date";

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getSupportFileList(requestHeaders, HttpStatus.UNAUTHORIZED);
	}
	
	
	@Test
	@TestDescription(desc="Non RFC 2616 date header and valid authorizaton token.")
	@TestLabel(label="Reference: 3203")
	public void test_3() {

		String dateHeader = getNonRFC2616Date();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getSupportFileList(requestHeaders, HttpStatus.UNAUTHORIZED);
	}
	
	
	@Test
	@TestDescription(desc="Date header more than 15 minutes in front of the " +
						  "server time and valid authorizaton token.")
	@TestLabel(label="Reference: 3204")
	public void test_4() {
		
		String dateHeader = getDateAfter(1);
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getSupportFileList(requestHeaders, HttpStatus.UNAUTHORIZED);
	}
	
	
	
	@Test
	@TestDescription(desc="Date header more than 15 minutes behind the " +
						  "server time and valid authorizaton token.")
	@TestLabel(label="Reference: 3205")
	public void test_5() {

		String dateHeader = getDateBefore(1);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		getSupportFileList(requestHeaders, HttpStatus.UNAUTHORIZED);
	}
	
	
	@Test
	@TestDescription(desc="Valid date header and no authorization token.")
	@TestLabel(label="Reference: 3206")
	public void test_6() {

		String dateHeader = getDateHeader();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", "FWS " + unrevokedUserCode + ": : ");
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getSupportFileList(requestHeaders, HttpStatus.UNAUTHORIZED);	
	}
	
	
	@Test
	@TestDescription(desc="Valid date header and malformed authorization token.")
	@TestLabel(label="Reference: 3207")
	public void test_7() {

		String dateHeader = getDateHeader();
		String authToken = getAuthToken(unrevokedPassword, dateHeader);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", "FWS-malformed" + unrevokedUserCode + ":"
				+ authToken);
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getSupportFileList(requestHeaders, HttpStatus.UNAUTHORIZED);
	}
	
	
	@Test
	@TestDescription(desc="Valid date header and valid authorization token.<br>" +
						  "Invalid user specified in the authorization header.")
	@TestLabel(label="Reference: 3208")
	public void test_8() {

		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(invalidUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getSupportFileList(requestHeaders, HttpStatus.UNAUTHORIZED);
	}
	

	@Test
	@TestDescription(desc="Valid date header and valid authorization token " +
						  "signed with incorrect password.")
	@TestLabel(label="Reference: 3209")
	public void test_9() {

		String dateHeader = getDateHeader();
	
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,invalidPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getSupportFileList(requestHeaders, HttpStatus.UNAUTHORIZED);
	}
	
	
	@Test
	@TestDescription(desc="Valid date header and valid authorization token. Request list of " +
			"support files. Check HTTP response is 200 OK. " +
			"Verify no exceptions have been returned and list of files matches " +
			"that expected. Verify checksums are also as expected.")
	@TestLabel(label="Reference: 3210")
	public void test_10() {

		String dateHeader = getDateHeader();
		
		SupportFileListResponse response = null;
				
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		response = getSupportFileList(requestHeaders, HttpStatus.UNAUTHORIZED);
		
		assertTrue("Support file list request was not a success.", response.isSuccess());
		
		assertTrue("Unexpected exception returned: " + response.getError().getErrorCode(), 
				response.getError().getErrorCode() == null);
		
		assertTrue("No support files found.", 
				response.getSupportFileList() != null);

		
		for (SupportFile support : response.getSupportFileList()) {
			
			String supportFileName = support.getFileName();
			
			assertTrue("Unexpected support file returned: " + supportFileName, 
					supportList_expectedSupportFiles.contains(supportFileName));
			
			String returnedChecksum = support.getCheckSum();
			
			for (int i = 0; i < supportList_expectedSupportFiles.size(); i++) {
				
				if (supportList_expectedSupportFiles.get(i).equals(supportFileName)) {
					
					String expectedChecksum = suportList_expectedChecksums.get(i);
					
					assertTrue("Checksums do not match. Expected: " + expectedChecksum +
							"<br>Recieved: " + returnedChecksum, 
							expectedChecksum.equals(returnedChecksum));
				}
			}
		}
	}
	
	
	

	
	public SupportFileListResponse getSupportFileList(HttpHeaders requestHeaders, 
			HttpStatus expectedStatusCode) {
		
		final String wsURL = baseURL + "support/resource";

		RestOperations restTemplate = null;
		restTemplate = new RestTemplate();

		StringBuffer restCall;
		SupportFileListResponse requestResponse = null;

		requestHeaders.set("x-fws-applicationidentifier", applicationIdentifier);
		
		try {
			
			// Build REST URL to call
			restCall = new StringBuffer(wsURL);

			// Make REST call and retrieve the results
			String requestBody = null;

			HttpEntity<String> requestEntity = new HttpEntity<String>(
					requestBody, requestHeaders);

			ResponseEntity<String> resultsExchange = restTemplate.exchange(
					restCall.toString(), HttpMethod.GET, requestEntity,
					String.class);

			String results = resultsExchange.getBody();


			log.debug("getSupportFileList results : " + results);
			assertTrue("Result is null", results != null);

		
			// Parse XML received from the response into an 
			// GetSupportFileListResponse object
			try {
				
				XStream xstream = new XStream(new StaxDriver());
				xstream.alias("SupportFileListResponse", SupportFileListResponse.class);
				xstream.alias("SupportFile", SupportFile.class);
				requestResponse = (SupportFileListResponse) xstream
						.fromXML(results);

				log.debug("Success : " + requestResponse.isSuccess());
			} 
			catch (XmlMappingException e) {
				
				log.error("Client Mapping Exception :" + e.getMessage());
				fail("Client Mapping Exception :" + e.getMessage());
			}
		} 
		catch (HttpClientErrorException he) {
			
			//Check to see if the HHTP status code is what we expect
			if (he.getStatusCode() == expectedStatusCode)
				log.debug(expectedStatusCode + " status code returned as expected.");
			else {
				
				log.error("Client Exception :" + he.getMessage());
				fail("Client Exception :" + he.getMessage());
			}
		}
		
		return requestResponse;
	}

	
}
