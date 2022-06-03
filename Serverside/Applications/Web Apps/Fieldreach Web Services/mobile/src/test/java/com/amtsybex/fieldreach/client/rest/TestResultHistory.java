package com.amtsybex.fieldreach.client.rest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.XmlMappingException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import com.amtsybex.fieldreach.services.messages.response.AssetDatabaseResponse;
import com.amtsybex.fieldreach.services.messages.response.DownloadPartResponse;
import com.amtsybex.fieldreach.services.messages.response.GetDeltaScriptResultResponse;
import com.amtsybex.fieldreach.services.messages.response.InitiateDownloadResponse;
import com.amtsybex.fieldreach.services.messages.response.ResultsHistoryDatabaseResponse;
import com.amtsybex.fieldreach.services.messages.response.ResultsHistoryDeltaResponse;
import com.amtsybex.fieldreach.services.test.annotations.TestDescription;
import com.amtsybex.fieldreach.services.test.annotations.TestLabel;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

public class TestResultHistory extends CommonBase {

	static Logger log = LoggerFactory.getLogger(TestResultHistory.class.getName());
	
	
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////HISTORIC DB TESTS/////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Test
	@TestDescription(desc = "Attempt get result history database file details without Date header and "
			+ "valid Authorization header.<br><br>"
			+ "Check HTTP status code is 401 Unauthorised")
	@TestLabel(label = "Reference: 1501")
	public void test_1() {

		String dateHeader = null;

		Map<String, String> headersVariables = new HashMap<String, String>();
		

		HttpHeaders requestHeaders = new HttpHeaders();

		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));

		requestHeaders.set("x-fws-deviceid", deviceid);

		getDBDetails(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED,
				"CL1", "BA1");
	}

	@Test
	@TestDescription(desc = "Attempt get result history database file details with invalid Date header "
			+ "and valid Authorization header.<br><br>"
			+ "Check HTTP status code is 401 Unauthorised")
	@TestLabel(label = "Reference: 1502")
	public void test_2() {

		String dateHeader = "sdgds";

		Map<String, String> headersVariables = new HashMap<String, String>();
		

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		getDBDetails(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED,
				"CL1", "BA1");
	}

	@Test
	@TestDescription(desc = "Attempt get result history database file details with date header not "
			+ "formatted one of the RFC 2616 formats and valid Authorization header.<br><br>"
			+ "Check HTTP status code is 401 Unauthorised.")
	@TestLabel(label = "Reference: 1503")
	public void test_3() {

		String dateHeader = getNonRFC2616Date();

		Map<String, String> headersVariables = new HashMap<String, String>();
		

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		getDBDetails(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED,
				"CL1", "BA1");
	}

	@Test
	@TestDescription(desc = "Get Server time. Attempt get result history database file details "
			+ "with date header more than 15 minutes in future of server time and "
			+ "valid Authorization header.<br><br>"
			+ "Check HTTP status code is 401 Unauthorised.")
	@TestLabel(label = "Reference: 1504")
	public void test_4() {

		String dateHeader = getDateAfter(1);

		Map<String, String> headersVariables = new HashMap<String, String>();
		

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		getDBDetails(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED,
				"CL1", "BA1");
	}

	@Test
	@TestDescription(desc = "Get Server time. Attempt get result history database file details "
			+ "with date header more than 15 minutes in past of server time and "
			+ "valid Authorization header.<br><br>"
			+ "Check HTTP status code is 401 Unauthorised.")
	@TestLabel(label = "Reference: 1505")
	public void test_5() {

		String dateHeader = getDateBefore(1);

		Map<String, String> headersVariables = new HashMap<String, String>();
		

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		getDBDetails(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED,
				"CL1", "BA1");
	}

	@Test
	@TestDescription(desc = "Attempt get result history database file details with valid "
			+ "Date header and no Authorization header.<br><br>"
			+ "Check HTTP status code is 401 Unauthorised ")
	@TestLabel(label = "Reference: 1506")
	public void test_6() {

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		getDBDetails(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED,
				"CL1", "BA1");
	}

	@Test
	@TestDescription(desc = "Attempt get result history database file details with valid "
			+ "Date header and malformed Authorization header.<br><br>"
			+ "Check HTTP status code is 401 Unauthorised.")
	@TestLabel(label = "Reference: 1507")
	public void test_7() {

		String dateHeader = getDateHeader();
		String authToken = getAuthToken(unrevokedPassword, dateHeader);

		Map<String, String> headersVariables = new HashMap<String, String>();
		

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", "malformed" + unrevokedUserCode
				+ ":" + authToken);
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		getDBDetails(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED,
				"CL1", "BA1");
	}

	@Test
	@TestDescription(desc = "Attempt get result history database file details with valid "
			+ "Date header and Authorization header containing an invalid user."
			+ "<br><br>Check HTTP status code is 401 Unauthorised")
	@TestLabel(label = "Reference: 1508")
	public void test_8() {

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		

		HttpHeaders requestHeaders = new HttpHeaders();

		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(invalidUserCode, unrevokedPassword,
						dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		getDBDetails(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED,
				"CL1", "BA1");
	}

	@Test
	@TestDescription(desc = "Attempt get result history database file details with valid "
			+ "Date header and Authorization header signed using incorrect password.<br><br>"
			+ "Check HTTP status code is 401 Unauthorised")
	@TestLabel(label = "Reference: 1509")
	public void test_9() {

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		

		HttpHeaders requestHeaders = new HttpHeaders();

		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode, invalidPassword,
						dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		getDBDetails(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED,
				"CL1", "BA1");
	}
	
	
	@Test
	@TestDescription(desc = "Attempt get Result History database file details with "
			+ "IWS Authorization details and passing dbClass parameter equal "
			+ "to DU01.<br>"
			+ "Check HTTP status code is 401 Unauthorised")
	@TestLabel(label = "Reference: 1510")
	public void test_10() {

		AssetDatabaseResponse resp = null;

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getIWSAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		getDBDetails(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED,
				"CL1", "BA1");
	}
	
	@Test
	@TestDescription(desc = "Attempt get Result History database file details with "
			+ "valid Authorization details and parameters"
			+ "Check HTTP status code is 200 OK.<br><br>"
			+ "Check message corresponds to ResultsHistoryDatabaseResponse message with "
			+ "non-empty database name, size and checksum.")
	@TestLabel(label = "Reference: 1511")
	public void test_11() {

		ResultsHistoryDatabaseResponse resp = null;

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		resp = getDBDetails(headersVariables, requestHeaders, HttpStatus.OK,
				"CL1", "BA1");

		assertTrue("A Null response was returned.", resp != null);

		assertTrue("Database name is empty.", !resp.getFull().getName().trim()
				.equals(""));
		assertTrue("Database checksum is empty.", !resp.getFull().getChecksum()
				.trim().equals(""));
		assertTrue("Database size has not been set", resp.getFull()
				.getSizeBytes() != -1);

		assertTrue("Success element was not true.<br><br>" + "Error Code: "
				+ resp.getError().getErrorCode()
				+ "<br><br> Error Description: "
				+ resp.getError().getErrorDescription(), resp.isSuccess());
	}
	
	@Test
	@TestDescription(desc = "Attempt get Result History database file details with "
			+ "valid Authorization details and incorrect DBArea"
			+ "Check HTTP status code is 200 OK.<br><br>"
			+ "Check DB Not Found Exception is returned")
	@TestLabel(label = "Reference: 1512")
	public void test_12() {

		ResultsHistoryDatabaseResponse resp = null;

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		resp = getDBDetails(headersVariables, requestHeaders, HttpStatus.OK,
				"CL1", "BAA1");

		assertTrue("A Null response was returned.", resp != null);

		assertTrue("Database Missing Exception.", resp.getError().getErrorCode().equals("ResultHistoryDBNotFoundException"));

	}
	
	@Test
	@TestDescription(desc = "Attempt get Result History database file details with "
			+ "valid Authorization details and incorrect DBClass"
			+ "Check HTTP status code is 200 OK.<br><br>"
			+ "Check DB Not Found Exception is returned")
	@TestLabel(label = "Reference: 1513")
	public void test_13() {

		ResultsHistoryDatabaseResponse resp = null;

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		resp = getDBDetails(headersVariables, requestHeaders, HttpStatus.OK,
				"CLL1", "BA1");

		assertTrue("A Null response was returned.", resp != null);

		assertTrue("Database Missing Exception.", resp.getError().getErrorCode().equals("ResultHistoryDBNotFoundException"));

	}
	
	
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////HISTORIC RESULT SEARCH TESTS///////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Test
	@TestDescription(desc = "Attempt search historic results without Date header and "
			+ "valid Authorization header.<br><br>"
			+ "Check HTTP status code is 401 Unauthorised")
	@TestLabel(label = "Reference: 1514")
	public void test_14() {

		String dateHeader = null;

		Map<String, String> headersVariables = new HashMap<String, String>();
		

		HttpHeaders requestHeaders = new HttpHeaders();

		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));

		requestHeaders.set("x-fws-deviceid", deviceid);

		searchScriptResult(requestHeaders, headersVariables, "20180101", "010000", "CL1", "BA1", HttpStatus.UNAUTHORIZED, null);
	}

	@Test
	@TestDescription(desc = "Attempt search historic results with invalid Date header "
			+ "and valid Authorization header.<br><br>"
			+ "Check HTTP status code is 401 Unauthorised")
	@TestLabel(label = "Reference: 1515")
	public void test_15() {

		String dateHeader = "sdgds";

		Map<String, String> headersVariables = new HashMap<String, String>();
		

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		searchScriptResult(requestHeaders, headersVariables, "20180101", "010000", "CL1", "BA1", HttpStatus.UNAUTHORIZED, null);
	}

	@Test
	@TestDescription(desc = "Attempt search historic results with date header not "
			+ "formatted one of the RFC 2616 formats and valid Authorization header.<br><br>"
			+ "Check HTTP status code is 401 Unauthorised.")
	@TestLabel(label = "Reference: 1516")
	public void test_16() {

		String dateHeader = getNonRFC2616Date();

		Map<String, String> headersVariables = new HashMap<String, String>();
		

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		searchScriptResult(requestHeaders, headersVariables, "20180101", "010000", "CL1", "BA1", HttpStatus.UNAUTHORIZED, null);
	}

	@Test
	@TestDescription(desc = "Get Server time. Attempt search historic results "
			+ "with date header more than 15 minutes in future of server time and "
			+ "valid Authorization header.<br><br>"
			+ "Check HTTP status code is 401 Unauthorised.")
	@TestLabel(label = "Reference: 1517")
	public void test_17() {

		String dateHeader = getDateAfter(1);

		Map<String, String> headersVariables = new HashMap<String, String>();
		

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		searchScriptResult(requestHeaders, headersVariables, "20180101", "010000", "CL1", "BA1", HttpStatus.UNAUTHORIZED, null);
	}

	@Test
	@TestDescription(desc = "Get Server time. Attempt search historic results "
			+ "with date header more than 15 minutes in past of server time and "
			+ "valid Authorization header.<br><br>"
			+ "Check HTTP status code is 401 Unauthorised.")
	@TestLabel(label = "Reference: 1518")
	public void test_18() {

		String dateHeader = getDateBefore(1);

		Map<String, String> headersVariables = new HashMap<String, String>();
		

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		searchScriptResult(requestHeaders, headersVariables, "20180101", "010000", "CL1", "BA1", HttpStatus.UNAUTHORIZED, null);
	}

	@Test
	@TestDescription(desc = "Attempt search historic results with valid "
			+ "Date header and no Authorization header.<br><br>"
			+ "Check HTTP status code is 401 Unauthorised ")
	@TestLabel(label = "Reference: 1519")
	public void test_19() {

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		searchScriptResult(requestHeaders, headersVariables, "20180101", "010000", "CL1", "BA1", HttpStatus.UNAUTHORIZED, null);
	}

	@Test
	@TestDescription(desc = "Attempt search historic results with valid "
			+ "Date header and malformed Authorization header.<br><br>"
			+ "Check HTTP status code is 401 Unauthorised.")
	@TestLabel(label = "Reference: 1520")
	public void test_20() {

		String dateHeader = getDateHeader();
		String authToken = getAuthToken(unrevokedPassword, dateHeader);

		Map<String, String> headersVariables = new HashMap<String, String>();
		

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", "malformed" + unrevokedUserCode
				+ ":" + authToken);
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		searchScriptResult(requestHeaders, headersVariables, "20180101", "010000", "CL1", "BA1", HttpStatus.UNAUTHORIZED, null);
	}

	@Test
	@TestDescription(desc = "Attempt search historic results with valid "
			+ "Date header and Authorization header containing an invalid user."
			+ "<br><br>Check HTTP status code is 401 Unauthorised")
	@TestLabel(label = "Reference: 1521")
	public void test_21() {

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		

		HttpHeaders requestHeaders = new HttpHeaders();

		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(invalidUserCode, unrevokedPassword,
						dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		searchScriptResult(requestHeaders, headersVariables, "20180101", "010000", "CL1", "BA1", HttpStatus.UNAUTHORIZED, null);
	}

	@Test
	@TestDescription(desc = "Attempt search historic results with valid "
			+ "Date header and Authorization header signed using incorrect password.<br><br>"
			+ "Check HTTP status code is 401 Unauthorised")
	@TestLabel(label = "Reference: 1522")
	public void test_22() {

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		

		HttpHeaders requestHeaders = new HttpHeaders();

		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode, invalidPassword,
						dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		searchScriptResult(requestHeaders, headersVariables, "20180101", "010000", "CL1", "BA1", HttpStatus.UNAUTHORIZED, null);
	}
	
	
	@Test
	@TestDescription(desc = "Attempt search historic results with "
			+ "IWS Authorization details and passing dbClass parameter equal "
			+ "to DU01.<br>"
			+ "Check HTTP status code is 401 Unauthorised")
	@TestLabel(label = "Reference: 1523")
	public void test_23() {

		AssetDatabaseResponse resp = null;

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getIWSAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		searchScriptResult(requestHeaders, headersVariables, "20180101", "010000", "CL1", "BA1", HttpStatus.UNAUTHORIZED, null);
	}	
	
	@Test
	@TestDescription(desc = "Attempt get Result History database file details with "
			+ "valid Authorization details and parameters"
			+ "Check HTTP status code is 200 OK.<br><br>"
			+ "Check message corresponds to ResultsHistoryDeltaResponse message with "
			+ "return list of size 1.")
	@TestLabel(label = "Reference: 1524")
	public void test_24() {

		ResultsHistoryDeltaResponse resp = null;

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		assertTrue("A Null response was returned.", searchScriptResult(requestHeaders, headersVariables, "20180101", "010000", "CL1", "BA1", HttpStatus.OK,  "<ResultsHistoryDeltaResponse><success>true</success><error/><deltaResultList><returnId>1</returnId><returnId>2</returnId></deltaResultList></ResultsHistoryDeltaResponse>"));

	}	
	
	
	@Test
	@TestDescription(desc = "Attempt get Result History database file details with "
			+ "valid Authorization details and parameters"
			+ "Check HTTP status code is 200 OK.<br><br>"
			+ "Check message corresponds to ResultsHistoryDeltaResponse message with "
			+ "return list of size 0.")
	@TestLabel(label = "Reference: 1525")
	public void test_25() {

		ResultsHistoryDeltaResponse resp = null;

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		assertTrue("A Null response was returned.", searchScriptResult(requestHeaders, headersVariables, "20180601", "010000", "CL1", "BA1", HttpStatus.OK, "<ResultsHistoryDeltaResponse><success>true</success><error/><deltaResultList/></ResultsHistoryDeltaResponse>"));

	}	
	
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////HISTORIC RESULT FILES TESTS////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	@Test
	@TestDescription(desc = "Attempt get result history file without Date header and "
			+ "valid Authorization header.<br><br>"
			+ "Check HTTP status code is 401 Unauthorised")
	@TestLabel(label = "Reference: 1526")
	public void test_26() {

		String dateHeader = null;

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "1");

		HttpHeaders requestHeaders = new HttpHeaders();

		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));

		requestHeaders.set("x-fws-deviceid", deviceid);

		retrieveScriptResult(requestHeaders, headersVariables, HttpStatus.UNAUTHORIZED);
	}

	@Test
	@TestDescription(desc = "Attempt get result history file with invalid Date header "
			+ "and valid Authorization header.<br><br>"
			+ "Check HTTP status code is 401 Unauthorised")
	@TestLabel(label = "Reference: 1527")
	public void test_27() {

		String dateHeader = "sdgds";

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "1");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		retrieveScriptResult(requestHeaders, headersVariables, HttpStatus.UNAUTHORIZED);
	}

	@Test
	@TestDescription(desc = "Attempt get result history file with date header not "
			+ "formatted one of the RFC 2616 formats and valid Authorization header.<br><br>"
			+ "Check HTTP status code is 401 Unauthorised.")
	@TestLabel(label = "Reference: 1528")
	public void test_28() {

		String dateHeader = getNonRFC2616Date();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "1");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		retrieveScriptResult(requestHeaders, headersVariables, HttpStatus.UNAUTHORIZED);
	}

	@Test
	@TestDescription(desc = "Get Server time. Attempt get result history file "
			+ "with date header more than 15 minutes in future of server time and "
			+ "valid Authorization header.<br><br>"
			+ "Check HTTP status code is 401 Unauthorised.")
	@TestLabel(label = "Reference: 1529")
	public void test_29() {

		String dateHeader = getDateAfter(1);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "1");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		retrieveScriptResult(requestHeaders, headersVariables, HttpStatus.UNAUTHORIZED);
	}

	@Test
	@TestDescription(desc = "Get Server time. Attempt getresult history file "
			+ "with date header more than 15 minutes in past of server time and "
			+ "valid Authorization header.<br><br>"
			+ "Check HTTP status code is 401 Unauthorised.")
	@TestLabel(label = "Reference: 1530")
	public void test_30() {

		String dateHeader = getDateBefore(1);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "1");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		retrieveScriptResult(requestHeaders, headersVariables, HttpStatus.UNAUTHORIZED);
	}

	@Test
	@TestDescription(desc = "Attempt get result history file with valid "
			+ "Date header and no Authorization header.<br><br>"
			+ "Check HTTP status code is 401 Unauthorised ")
	@TestLabel(label = "Reference: 1531")
	public void test_31() {

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "1");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		retrieveScriptResult(requestHeaders, headersVariables, HttpStatus.UNAUTHORIZED);
	}

	@Test
	@TestDescription(desc = "Attempt get result history file with valid "
			+ "Date header and malformed Authorization header.<br><br>"
			+ "Check HTTP status code is 401 Unauthorised.")
	@TestLabel(label = "Reference: 1532")
	public void test_32() {

		String dateHeader = getDateHeader();
		String authToken = getAuthToken(unrevokedPassword, dateHeader);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "1");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", "malformed" + unrevokedUserCode
				+ ":" + authToken);
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		retrieveScriptResult(requestHeaders, headersVariables, HttpStatus.UNAUTHORIZED);
	}

	@Test
	@TestDescription(desc = "Attempt get result history file with valid "
			+ "Date header and Authorization header containing an invalid user."
			+ "<br><br>Check HTTP status code is 401 Unauthorised")
	@TestLabel(label = "Reference: 1533")
	public void test_34() {

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "1");

		HttpHeaders requestHeaders = new HttpHeaders();

		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(invalidUserCode, unrevokedPassword,
						dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		retrieveScriptResult(requestHeaders, headersVariables, HttpStatus.UNAUTHORIZED);
	}

	@Test
	@TestDescription(desc = "Attempt get result history file with valid "
			+ "Date header and Authorization header signed using incorrect password.<br><br>"
			+ "Check HTTP status code is 401 Unauthorised")
	@TestLabel(label = "Reference: 1535")
	public void test_35() {

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "1");

		HttpHeaders requestHeaders = new HttpHeaders();

		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode, invalidPassword,
						dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		retrieveScriptResult(requestHeaders, headersVariables, HttpStatus.UNAUTHORIZED);
	}
	
	
	@Test
	@TestDescription(desc = "Attempt get result history file with "
			+ "IWS Authorization details and passing dbClass parameter equal "
			+ "to DU01.<br>"
			+ "Check HTTP status code is 401 Unauthorised")
	@TestLabel(label = "Reference: 1536")
	public void test_36() {

		AssetDatabaseResponse resp = null;

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "1");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getIWSAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		retrieveScriptResult(requestHeaders, headersVariables, HttpStatus.UNAUTHORIZED);
	}
	
	@Test
	@TestDescription(desc = "Attempt get result history file with "
			+ "valid Authorization details and parameters"
			+ "Check HTTP status code is 200 OK.<br><br>"
			+ "Check message corresponds to ResultsHistoryDatabaseResponse message with "
			+ "non-empty database name, size and checksum.")
	@TestLabel(label = "Reference: 1537")
	public void test_37() {

		GetDeltaScriptResultResponse resp = null;

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "1");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		resp = retrieveScriptResult(requestHeaders, headersVariables, HttpStatus.UNAUTHORIZED);

		assertTrue("A Null response was returned.", resp != null);

		assertTrue("File name is empty.", !resp.getFileName().trim()
				.equals(""));
		assertTrue("File checksum is empty.", !resp.getChecksum()
				.trim().equals(""));

		assertTrue("Success element was not true.<br><br>" + "Error Code: "
				+ resp.getError().getErrorCode()
				+ "<br><br> Error Description: "
				+ resp.getError().getErrorDescription(), resp.isSuccess());
	}
	
	@Test
	@TestDescription(desc = "Attempt get result history file with "
			+ "valid Authorization details and incorrect DBArea"
			+ "Check HTTP status code is 200 OK.<br><br>"
			+ "Check DB Not Found Exception is returned")
	@TestLabel(label = "Reference: 1538")
	public void test_38() {

		GetDeltaScriptResultResponse resp = null;

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "2");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		resp = retrieveScriptResult(requestHeaders, headersVariables, HttpStatus.UNAUTHORIZED);

		assertTrue("A Null response was returned.", resp != null);

		assertTrue("Database Missing Exception.", resp.getError().getErrorCode().equals("ScriptResultNotFoundException"));

	}
	
	
	
	
	
	
	// Get details of the latest available asset database.
	public ResultsHistoryDatabaseResponse getDBDetails(
			Map<String, String> headersVariables, HttpHeaders requestHeaders,
			HttpStatus expectedStatusCode, String dbClass, String dbArea) {

		log.debug(">>> getDBDetails ");

		final String wsURL = baseURL + "/database/resultshistory";

		RestOperations restTemplate = null;
		restTemplate = new RestTemplate();

		StringBuffer restCall;
		ResultsHistoryDatabaseResponse requestResponse = null;

		requestHeaders
				.set("x-fws-applicationidentifier", applicationIdentifier);

		try {

			// Build REST URL to call
			restCall = new StringBuffer(wsURL);

			restCall.append("?dbClass=" + dbClass);
			restCall.append("&dbArea=" + dbArea);
			
			// Make REST call and retrieve the results
			String requestBody = null;

			HttpEntity<String> requestEntity = new HttpEntity<String>(
					requestBody, requestHeaders);

			ResponseEntity<String> resultsExchange = restTemplate.exchange(
					restCall.toString(), HttpMethod.GET, requestEntity,
					String.class, headersVariables);

			String results = resultsExchange.getBody();

			log.debug("Get DB  results : " + results);
			assertTrue("Result is null", results != null);

			// Parse XML received from the response into an
			// AuthenticateUserResponse object
			try {

				XStream xstream = new XStream(new StaxDriver());
				xstream.alias("ResultsHistoryDatabaseResponse",
						ResultsHistoryDatabaseResponse.class);
				requestResponse = (ResultsHistoryDatabaseResponse) xstream
						.fromXML(results);

				log.debug("Success : " + requestResponse.isSuccess());

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
		} catch (Exception ex) {

			log.debug("Error :" + ex);
			fail("Exception :" + ex.getMessage());
		}

		log.debug("<<< getDBDetails ");

		return requestResponse;

	}

	public InitiateDownloadResponse initiate(HttpHeaders requestHeaders,
			Map<String, String> headersVariables, HttpStatus expectedStatusCode, String dbClass, String dbArea) {

		final String wsURL = baseURL
				+ "database/resultshistory/{filename:.*}/multipart";

		RestOperations restTemplate = null;
		restTemplate = new RestTemplate();

		StringBuffer restCall;
		InitiateDownloadResponse requestResponse = null;

		log.debug("URL = " + wsURL);

		requestHeaders
				.set("x-fws-applicationidentifier", applicationIdentifier);

		
		try {

			// Build REST URL to call
			restCall = new StringBuffer(wsURL);

			restCall.append("?dbClass=" + dbClass);
			restCall.append("?dbArea=" + dbArea);
			
			// Make REST call and retrieve the results
			String requestBody = null;

			HttpEntity<String> requestEntity = new HttpEntity<String>(
					requestBody, requestHeaders);

			ResponseEntity<String> resultsExchange = restTemplate.exchange(
					restCall.toString(), HttpMethod.GET, requestEntity,
					String.class, headersVariables);

			String results = resultsExchange.getBody();

			log.debug("initiate results : " + results);
			assertTrue("Result is null", results != null);

			// Parse XML received from the response into an
			// GetConfigResponse object
			try {

				XStream xstream = new XStream(new StaxDriver());
				xstream.alias("InitiateDownloadResponse",
						InitiateDownloadResponse.class);
				requestResponse = (InitiateDownloadResponse) xstream
						.fromXML(results);

				log.debug("Success : " + requestResponse.isSuccess());
			} catch (XmlMappingException e) {

				log.error("Client Mapping Exception :" + e.getMessage());
				fail("Client Mapping Exception :" + e.getMessage());
			}
		} catch (HttpClientErrorException he) {

			// Check to see if the HTTP status code is what we expect
			if (he.getStatusCode() == expectedStatusCode)
				log.debug(expectedStatusCode
						+ " status code returned as expected.");
			else {

				log.error("Client Exception :" + he.getMessage());
				fail("Client Exception :" + he.getMessage());
			}
		}

		return requestResponse;
	}

	public DownloadPartResponse retrievePart(HttpHeaders requestHeaders,
			Map<String, String> headersVariables, HttpStatus expectedStatusCode) {

		final String wsURL = baseURL
				+ "database/resultshistory/{filename:.*}/multipart/{identifier}/{part}";

		RestOperations restTemplate = null;
		restTemplate = new RestTemplate();

		StringBuffer restCall;
		DownloadPartResponse requestResponse = null;

		requestHeaders
				.set("x-fws-applicationidentifier", applicationIdentifier);

		try {

			// Build REST URL to call
			restCall = new StringBuffer(wsURL);

			// Make REST call and retrieve the results
			String requestBody = null;

			HttpEntity<String> requestEntity = new HttpEntity<String>(
					requestBody, requestHeaders);

			ResponseEntity<String> resultsExchange = restTemplate.exchange(
					restCall.toString(), HttpMethod.GET, requestEntity,
					String.class, headersVariables);

			String results = resultsExchange.getBody();

			log.debug("retrievePart results : " + results);
			assertTrue("Result is null", results != null);

			// Parse XML received from the response into an
			// GetConfigResponse object
			try {

				XStream xstream = new XStream(new StaxDriver());
				xstream.alias("DownloadPartResponse",
						DownloadPartResponse.class);
				requestResponse = (DownloadPartResponse) xstream
						.fromXML(results);

				log.debug("Success : " + requestResponse.isSuccess());
				
			} catch (XmlMappingException e) {

				log.error("Client Mapping Exception :" + e.getMessage());
				fail("Client Mapping Exception :" + e.getMessage());
			}
		} catch (HttpClientErrorException he) {

			// Check to see if the HTTP status code is what we expect
			if (he.getStatusCode() == expectedStatusCode)
				log.debug(expectedStatusCode
						+ " status code returned as expected.");
			else {

				log.error("Client Exception :" + he.getMessage());
				fail("Client Exception :" + he.getMessage());
			}
		}

		return requestResponse;
	}
	
	

	public boolean searchScriptResult(HttpHeaders requestHeaders, 
			Map<String, String> headersVariables, String searchDate, String searchTime, String dbClass, String dbArea, HttpStatus expectedStatusCode, String expectedResponse)
	{
		final String wsURL = baseURL + "/resultshistory/deltasearch";

		RestOperations restTemplate = null;
		restTemplate = new RestTemplate();

		StringBuffer restCall;
		ResultsHistoryDeltaResponse requestResponse = null;

		requestHeaders.set("x-fws-applicationidentifier", applicationIdentifier);
		
		try 
		{
			//
			// Build REST URL to call
			restCall = new StringBuffer(wsURL);
			
			restCall.append("?dbClass=" + dbClass);
			restCall.append("&dbArea=" + dbArea);
			restCall.append("&deltaSearchDate=" + searchDate);
			restCall.append("&deltaSearchTime=" + searchTime);
			
			
			HttpEntity<String> requestEntity = new HttpEntity<String>(
					null, requestHeaders);


			ResponseEntity<String> resultsExchange = restTemplate.exchange(
					restCall.toString(), HttpMethod.GET, requestEntity,
					String.class, headersVariables);

			String results = resultsExchange.getBody();


			log.debug("retrieveScriptResult results : " + results);
			assertTrue("Result is null", results != null);

			
			//
			// Parse XML received from the response into an 
			// GetConfigResponse object
			/*try 
			{
				XStream xstream = new XStream(new StaxDriver());
				xstream.alias("ResultsHistoryDeltaResponse", ResultsHistoryDeltaResponse.class);
				requestResponse = (ResultsHistoryDeltaResponse) xstream.fromXML(results);

				log.debug("Success : " + requestResponse.isSuccess());
			} 
			catch (XmlMappingException e) 
			{
				log.error("Client Mapping Exception :" + e.getMessage());
				fail("Client Mapping Exception :" + e.getMessage());
			}*/
			
			if(results.equalsIgnoreCase(expectedResponse)) {
				return true;
			}
		} 
		catch (HttpClientErrorException he) 
		{
			//
			//Check to see if the HTTP status code is what we expect
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
		
		return false;
		
	}
	
	public GetDeltaScriptResultResponse retrieveScriptResult(HttpHeaders requestHeaders, 
			Map<String, String> headersVariables, HttpStatus expectedStatusCode)
	{
		final String wsURL = baseURL + "resultshistory/getdeltaresult/{id}";

		RestOperations restTemplate = null;
		restTemplate = new RestTemplate();

		StringBuffer restCall;
		GetDeltaScriptResultResponse  requestResponse = null;

		requestHeaders.set("x-fws-applicationidentifier", applicationIdentifier);
		
		try 
		{
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


			log.debug("retrieveScriptResult results : " + results);
			assertTrue("Result is null", results != null);

			
			//
			// Parse XML received from the response into an 
			// GetConfigResponse object
			try 
			{
				XStream xstream = new XStream(new StaxDriver());
				xstream.alias("GetDeltaScriptResultResponse", GetDeltaScriptResultResponse.class);
				requestResponse = (GetDeltaScriptResultResponse) xstream.fromXML(results);

				log.debug("Success : " + requestResponse.isSuccess());
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
			//Check to see if the HTTP status code is what we expect
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
		
		return requestResponse;
		
	}
}
