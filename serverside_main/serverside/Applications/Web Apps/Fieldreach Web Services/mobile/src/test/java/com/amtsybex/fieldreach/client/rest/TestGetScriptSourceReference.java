/**
 * Author:  T Murray
 * Date:    13/01/2014
 * Project: FDE022
 * 
 * Copyright AMT-Sybex 2014
 */
package com.amtsybex.fieldreach.client.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.amtsybex.fieldreach.services.messages.response.GetScriptReferenceResponse;
import com.amtsybex.fieldreach.services.messages.response.ScriptListResponse;
import com.amtsybex.fieldreach.services.messages.types.File;
import com.amtsybex.fieldreach.services.messages.types.Script;
import com.amtsybex.fieldreach.services.test.annotations.TestDescription;
import com.amtsybex.fieldreach.services.test.annotations.TestLabel;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestGetScriptSourceReference extends CommonBase {

	static Logger log = LoggerFactory.getLogger(TestGetScriptSourceReference.class
			.getName());
	

	@Test
	@TestDescription(desc = "No date header and valid authorizaton token.")
	@TestLabel(label = "Reference: 1601")
	public void test_1() {

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptReferenceFile);

		HttpHeaders requestHeaders = new HttpHeaders();
		String dateHeader = getDateHeader();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);

		getScriptSourceReference(headersVariables, requestHeaders,
				HttpStatus.UNAUTHORIZED);
	}

	
	@Test
	@TestDescription(desc = "Invalid header and valid authorizaton token.")
	@TestLabel(label = "Reference: 1602")
	public void test_2() {

		String dateHeader = "invalid date";

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptReferenceFile);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		getScriptSourceReference(headersVariables, requestHeaders,
				HttpStatus.UNAUTHORIZED);
	}

	
	@Test
	@TestDescription(desc = "Non RFC 2616 date header and valid authorizaton token.")
	@TestLabel(label = "Reference: 1603")
	public void test_3() {

		String dateHeader = getNonRFC2616Date();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptReferenceFile);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		getScriptSourceReference(headersVariables, requestHeaders,
				HttpStatus.UNAUTHORIZED);
	}

	
	@Test
	@TestDescription(desc = "Date header more than 15 minutes in front of the "
			+ "server time and valid authorizaton token.")
	@TestLabel(label = "Reference: 1604")
	public void test_4() {

		String dateHeader = getDateAfter(1);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptReferenceFile);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		getScriptSourceReference(headersVariables, requestHeaders,
				HttpStatus.UNAUTHORIZED);
	}

	
	@Test
	@TestDescription(desc = "Date header more than 15 minutes behind the "
			+ "server time and valid authorizaton token.")
	@TestLabel(label = "Reference: 1605")
	public void test_5() {

		String dateHeader = getDateBefore(1);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptReferenceFile);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		getScriptSourceReference(headersVariables, requestHeaders,
				HttpStatus.UNAUTHORIZED);
	}

	
	@Test
	@TestDescription(desc = "Valid date header and no authorization token.")
	@TestLabel(label = "Reference: 1606")
	public void test_6() {

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptReferenceFile);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", "FWS " + unrevokedUserCode + ":"
				+ null);
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		getScriptSourceReference(headersVariables, requestHeaders,
				HttpStatus.UNAUTHORIZED);
	}

	
	@Test
	@TestDescription(desc = "Valid date header and malformed authorization token.")
	@TestLabel(label = "Reference: 1607")
	public void test_7() {

		String dateHeader = getDateHeader();
		String authToken = getAuthToken(unrevokedPassword, dateHeader);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptReferenceFile);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", "FWS-malformed"
				+ unrevokedUserCode + ":" + authToken);
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		getScriptSourceReference(headersVariables, requestHeaders,
				HttpStatus.UNAUTHORIZED);
	}

	
	@Test
	@TestDescription(desc = "Valid date header and valid authorization token.<br>"
			+ "Invalid user specified in the authorization header.")
	@TestLabel(label = "Reference: 1608")
	public void test_8() {

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptReferenceFile);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(invalidUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		getScriptSourceReference(headersVariables, requestHeaders,
				HttpStatus.UNAUTHORIZED);
	}

	
	@Test
	@TestDescription(desc = "Valid date header and valid authorization token "
			+ "signed with incorrect password.")
	@TestLabel(label = "Reference: 1609")
	public void test_9() {

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptReferenceFile);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,invalidPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		getScriptSourceReference(headersVariables, requestHeaders,
				HttpStatus.UNAUTHORIZED);
	}

	
	@Test
	@TestDescription(desc = "Select an online script and add a reference only file to one of the " +
			"questions in the script. Make valid call to retrieve list of all online scripts."
			+ "<br/>Verify script and reference file are declared in script.")
	@TestLabel(label = "Reference: 1610")
	public void test_10() {

		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		ScriptListResponse resp = this.getScriptList(null, requestHeaders, HttpStatus.OK);
		
		
		assertTrue("Script List response is null", resp != null);

		assertTrue("Script List response<br><br><b>ErrorCode:</b><br>"
				+ resp.getError().getErrorCode()
				+ "<br><b>Detail:</b><br>"
				+ resp.getError().getErrorDescription(),
				resp.isSuccess());

		//
		// Look for returned reference...
		boolean scriptReferenceFound = false;
		log.debug("Reference file to find .. " + scriptReferenceFile);
		
		if (resp.getScriptList() != null) {
			List<Script> lstScript = resp.getScriptList();

			for (Script objScript : lstScript) {

				log.debug("Script .." + objScript.getScriptCode());
				//
				// Check for script we expect.
				if (scriptReferenceScriptCode.equals(objScript.getScriptCode())) {
					if (objScript.getFilerefs() != null) {
						List<File> lstFileRefs = objScript.getFilerefs();
						for (File objFile : lstFileRefs) {
							//
							// check for reference file ..
							log.debug("File .. " + objFile.getName());
							if (scriptReferenceFile.equals(objFile.getName())) {
								scriptReferenceFound = true;
								break;
							}
						}
					}

					break;
				}

			}
		}

		assertTrue("ScriptReference not found", scriptReferenceFound);
	}


	@Test
	@TestDescription(desc = "Select an online script and add a reference only file to one of the " +
			"questions in the script. Make valid call to retrieve list of all online scripts."
			+ "<br/>Verify script and reference file are declared in script."
			+ "<br/>Verify reference file only declared once in script.")
	@TestLabel(label = "Reference: 1611")
	public void test_11() {

		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		ScriptListResponse resp = this.getScriptList(null, requestHeaders, HttpStatus.OK);
		
		
		assertTrue("Script List response is null", resp != null);

		assertTrue("Script List response<br><br><b>ErrorCode:</b><br>"
				+ resp.getError().getErrorCode()
				+ "<br><b>Detail:</b><br>"
				+ resp.getError().getErrorDescription(),
				resp.isSuccess());

		//
		// Look for returned reference...
		boolean scriptReferenceFound = false;
		String referenceFileName = scriptReferenceFile;
		if (resp.getScriptList() != null) {
			List<Script> lstScript = resp.getScriptList();

			for (Script objScript : lstScript) {
				//
				// Check for script we expect.
				if (scriptReferenceScriptCode.equals(objScript.getScriptCode())) {
					if (objScript.getFilerefs() != null) {
						List<File> lstFileRefs = objScript.getFilerefs();
						for (File objFile : lstFileRefs) {
							//
							// check for reference file ..
							if (referenceFileName.equals(objFile.getName())) {
								//
								// Check not repeated..
								if (scriptReferenceFound)
								{
									assertTrue("Script reference file is repeated", scriptReferenceFound);
								}
								else
								{
									scriptReferenceFound = true;
								}
								
							}
						}
					}

					break;
				}

			}
		}

		assertTrue("ScriptReference not found", scriptReferenceFound);

	}
	
	
	@Test
	@TestDescription(desc = "Select an online script and add a reference only file to one of the " +
			"questions in the script. Make valid call to retrieve list of all online scripts."
			+ "<br/>Verify script and reference file are declared in script."
			+ "<br/>Verify Checksum of file declared is correct.")
	@TestLabel(label = "Reference: 1612")
	public void test_12() {
		
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		ScriptListResponse resp = this.getScriptList(null, requestHeaders, HttpStatus.OK);
		
		
		assertTrue("Script List response is null", resp != null);

		assertTrue("Script List response<br><br><b>ErrorCode:</b><br>"
				+ resp.getError().getErrorCode()
				+ "<br><b>Detail:</b><br>"
				+ resp.getError().getErrorDescription(),
				resp.isSuccess());

		//
		// Look for returned reference...
		boolean scriptReferenceFound = false;
		String referenceFileName = scriptReferenceFile;
		String authChecksum = null;
		if (resp.getScriptList() != null) {
			List<Script> lstScript = resp.getScriptList();

			for (Script objScript : lstScript) {
				//
				// Check for script we expect.
				if (scriptReferenceScriptCode.equals(objScript.getScriptCode())) {
					if (objScript.getFilerefs() != null) {
						List<File> lstFileRefs = objScript.getFilerefs();
						for (File objFile : lstFileRefs) {
							//
							// check for reference file ..
							if (referenceFileName.equals(objFile.getName())) {
								//
								// Check not repeated..
								if (scriptReferenceFound)
								{
									assertTrue("Script reference file is repeated", scriptReferenceFound);
								}
								else
								{
									scriptReferenceFound = true;
									authChecksum = objFile.getChecksum();
									
									assertTrue("Script Reference Checksum is null", authChecksum != null);
									
									assertTrue("Script Reference Checksum is empty", (authChecksum != null &&
											authChecksum.length() > 0));
								}
								
							}
						}
					}

					break;
				}

			}
		}

		assertTrue("ScriptReference not found", scriptReferenceFound);
		
		//
		// Get file and check checksum
		
		//
		// Headers
		dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptReferenceFile);

		requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		GetScriptReferenceResponse scriptReferenceResponse =
				getScriptSourceReference(headersVariables, requestHeaders,
				HttpStatus.OK);
		
		assertTrue("ScriptReference response is null", scriptReferenceResponse != null);

		String referenceFilebase64 =  scriptReferenceResponse.getReferenceFileSource();
		
		//
		// Calculate our own checksum and check against that returned earlier
		assertTrue("ScriptReference checksum is not correct", 
						verifyChecksum(authChecksum, referenceFilebase64));
		
	}

	
	@Test
	@TestDescription(desc = "Select an online script and add a reference only file to one of the " +
			"questions in the script.<br>"
			+ "<br/>Reference only file should NOT be in resource directory."
			+ "<br/>Make valid call to get script list."
			+ "<br/>Verify script and reference file are declared in script."
			+ "<br/>Verify Checksum of file declared is empty.")
	@TestLabel(label = "Reference: 1613")
	public void test_13() {
		
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		ScriptListResponse resp = this.getScriptList(null, requestHeaders, HttpStatus.OK);
		
		
		assertTrue("Script List response is null", resp != null);

		assertTrue("Script List response<br><br><b>ErrorCode:</b><br>"
				+ resp.getError().getErrorCode()
				+ "<br><b>Detail:</b><br>"
				+ resp.getError().getErrorDescription(),
				resp.isSuccess());

		//
		// Look for returned reference...
		boolean scriptReferenceFound = false;
		String referenceFileName = scriptReferenceFileNotInResource;
		String authChecksum = null;
		if (resp.getScriptList() != null) {
			List<Script> lstScript = resp.getScriptList();

			for (Script objScript : lstScript) {
				//
				// Check for script we expect.
				if (scriptReferenceScriptCode.equals(objScript.getScriptCode())) {
					if (objScript.getFilerefs() != null) {
						List<File> lstFileRefs = objScript.getFilerefs();
						for (File objFile : lstFileRefs) {
							//
							// check for reference file ..
							if (referenceFileName.equals(objFile.getName())) {
								//
								// Check not repeated..
								if (scriptReferenceFound)
								{
									assertTrue("Script reference file is repeated", scriptReferenceFound);
								}
								else
								{
									scriptReferenceFound = true;
									authChecksum = objFile.getChecksum();
									
									assertTrue("Script Reference Checksum is null", authChecksum != null);
									
									assertTrue("Script Reference Checksum not empty", (authChecksum != null &&
											authChecksum.length() == 0));
								}
								
							}
						}
					}

					break;
				}

			}
		}

		assertTrue("ScriptReference not found", scriptReferenceFound);
	}
	
	
	@Test
	@TestDescription(desc = "Select an online script and add a reference only file to one of " +
			"the questions in the script.<br/>"
			+ "<br/>Make valid script list call."
			+ "<br/>Verify script and reference file are declared in script."
			+ "<br/>Verify Checksum of file declared is correct."
			+ "<br/>Download file and validate checksum")
	@TestLabel(label = "Reference: 1614")
	public void test_14() {
		
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		ScriptListResponse resp = this.getScriptList(null, requestHeaders, HttpStatus.OK);
		
		
		assertTrue("Script List response is null", resp != null);

		assertTrue("Script List response<br><br><b>ErrorCode:</b><br>"
				+ resp.getError().getErrorCode()
				+ "<br><b>Detail:</b><br>"
				+ resp.getError().getErrorDescription(),
				resp.isSuccess());

		//
		// Look for returned reference...
		boolean scriptReferenceFound = false;
		String referenceFileName = scriptReferenceFile;
		String authChecksum = null;
		if (resp.getScriptList() != null) {
			List<Script> lstScript = resp.getScriptList();

			for (Script objScript : lstScript) {
				//
				// Check for script we expect.
				if (scriptReferenceScriptCode.equals(objScript.getScriptCode())) {
					if (objScript.getFilerefs() != null) {
						List<File> lstFileRefs = objScript.getFilerefs();
						for (File objFile : lstFileRefs) {
							//
							// check for reference file ..
							if (referenceFileName.equals(objFile.getName())) {
								//
								// Check not repeated..
								if (scriptReferenceFound)
								{
									assertTrue("Script reference file is repeated", scriptReferenceFound);
								}
								else
								{
									scriptReferenceFound = true;
									authChecksum = objFile.getChecksum();
									
									assertTrue("Script Reference Checksum is null", authChecksum != null);
									
									assertTrue("Script Reference Checksum is empty", (authChecksum != null &&
											authChecksum.length() > 0));
								}
								
							}
						}
					}

					break;
				}

			}
		}

		assertTrue("ScriptReference not found", scriptReferenceFound);
		
		//
		// Get file and check checksum
		
		//
		// Headers
		dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptReferenceFile);

		requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		GetScriptReferenceResponse scriptReferenceResponse =
				getScriptSourceReference(headersVariables, requestHeaders,
				HttpStatus.OK);
		
		assertTrue("ScriptReference response is null", scriptReferenceResponse != null);

		String referenceFilebase64 =  scriptReferenceResponse.getReferenceFileSource();
		
		//
		// Calculate our own checksum and check against that returned earlier
		assertTrue("ScriptReference checksum is not correct", 
						verifyChecksum(authChecksum, referenceFilebase64));
		
	}

	
	@Test
	@TestDescription(desc = "Attempt download of non-existing reference file "
			+ "<br/>Verify FILENOTFOUNDEXCEPTION returned.")
	@TestLabel(label = "Reference: 1615")
	public void test_15() {

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptReferenceFileMissing);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		GetScriptReferenceResponse scriptReferenceResponse =
				getScriptSourceReference(headersVariables, requestHeaders,
				HttpStatus.OK);
		
		assertTrue("ScriptReference response is null", scriptReferenceResponse != null);

		assertTrue("ScriptReference Success is true",scriptReferenceResponse.isSuccess() == false);
		
		assertTrue("ScriptReference error code incorrect " + scriptReferenceResponse.getError().getErrorCode(),
				"FILENOTFOUNDEXCEPTION".equalsIgnoreCase(scriptReferenceResponse.getError().getErrorCode()));
	}

	
	private GetScriptReferenceResponse getScriptSourceReference(
			Map<String, String> headersVariables, HttpHeaders requestHeaders,
			HttpStatus expectedStatusCode) {
		
		final String wsURL = baseURL + "script/reference/{id}";

		RestOperations restTemplate = null;
		restTemplate = new RestTemplate();

		StringBuffer restCall;
		GetScriptReferenceResponse response = null;

		requestHeaders.set("x-fws-applicationidentifier", applicationIdentifier);
		
		try {
			//
			// Build REST URL to call
			restCall = new StringBuffer(wsURL);

			//
			// Make REST call and retrieve the results
			String requestBody = null;

			HttpEntity<String> requestEntity = new HttpEntity<String>(
					requestBody, requestHeaders);

			ResponseEntity<String> resultsExchange = restTemplate.exchange(
					restCall.toString(), HttpMethod.GET, requestEntity,
					String.class, headersVariables);

			String results = resultsExchange.getBody();

			log.debug("get script reference results : " + results);
			assertTrue("Result is null", results != null);

			//
			// Parse XML received from the response into an
			// GetScriptResponse object
			try {
				XStream xstream = new XStream(new StaxDriver());
				xstream.alias("GetScriptReferenceResponse",
						GetScriptReferenceResponse.class);
				response = (GetScriptReferenceResponse) xstream
						.fromXML(results);

				log.debug("Success : " + response.isSuccess());
			} 
			catch (XmlMappingException e) {
				
				log.error("Client Mapping Exception :" + e.getMessage());
				fail("Client Mapping Exception :" + e.getMessage());
			}
		} 
		catch (HttpClientErrorException he) {
			//
			// Check to see if the HHTP status code is what we expect
			if (he.getStatusCode() == expectedStatusCode) {
				log.debug(expectedStatusCode
						+ " status code returned as expected.");
			} else {
				log.error("Client Exception :" + he.getMessage());
				fail("Client Exception :" + he.getMessage());
			}
		}

		return response;
	}

	
	public ScriptListResponse getScriptList(String wgCode,
			HttpHeaders requestHeaders, HttpStatus expectedStatusCode) {
		
		final String wsURL = baseURL + "script/list";

		RestOperations restTemplate = null;
		restTemplate = new RestTemplate();

		StringBuffer restCall;
		ScriptListResponse requestResponse = null;

		requestHeaders.set("x-fws-applicationidentifier", applicationIdentifier);
		
		try {
			//
			// Build REST URL to call
			restCall = new StringBuffer(wsURL);

			if(wgCode != null)
				restCall.append("?workgroupCode=" + wgCode);
			
			//
			// Make REST call and retrieve the results
			String requestBody = null;

			HttpEntity<String> requestEntity = new HttpEntity<String>(
					requestBody, requestHeaders);

			ResponseEntity<String> resultsExchange = restTemplate.exchange(
					restCall.toString(), HttpMethod.GET, requestEntity,
					String.class);

			String results = resultsExchange.getBody();


			log.debug("getConfigList results : " + results);
			assertTrue("Result is null", results != null);

			
			//
			// Parse XML received from the response into an 
			// GetConfigResponse object
			try {
				
				XStream xstream = new XStream(new StaxDriver());
				xstream.alias("ScriptListResponse", ScriptListResponse.class);
				xstream.alias("Script", Script.class);
				xstream.alias("File", File.class);
				requestResponse = (ScriptListResponse) xstream
						.fromXML(results);

				log.debug("Success : " + requestResponse.isSuccess());
			} 
			catch (XmlMappingException e) {
				
				log.error("Client Mapping Exception :" + e.getMessage());
				fail("Client Mapping Exception :" + e.getMessage());
			}
		} 
		catch (HttpClientErrorException he) {
			
			//
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
