/**
 * Author:  T Murray
 * Date:    04/02/2014
 * Project: FDE029
 * 
 * Copyright AMT-Sybex 2015
 */
package com.amtsybex.fieldreach.client.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
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

import com.amtsybex.fieldreach.services.messages.response.DownloadPartResponse;
import com.amtsybex.fieldreach.services.messages.response.InitiateDownloadResponse;
import com.amtsybex.fieldreach.services.messages.response.WorkBankDatabaseResponse;
import com.amtsybex.fieldreach.services.test.annotations.TestDescription;
import com.amtsybex.fieldreach.services.test.annotations.TestLabel;
import com.amtsybex.fieldreach.services.utils.Utils;
import com.amtsybex.fieldreach.utils.impl.Common;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestWorkBankDatabase extends CommonBase {

	static Logger log = LoggerFactory.getLogger(TestWorkBankDatabase.class.getName());

	private boolean test3514 = false;

	private static String databaseName;

	private static String m_initiatedIdentifier = null;
	private static int m_totalParts = 0;
	private static String m_checksum = null;
	private static String m_fileName = null;

	@Test
	@TestDescription(desc = "Attempt get work bank database file details without Date header and "
			+ "valid Authorization header.<br><br>"
			+ "Check HTTP status code is 401 Unauthorised")
	@TestLabel(label = "Reference: 3501")
	public void test_1() {

		String dateHeader = null;

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", unrevokedUserCode);

		HttpHeaders requestHeaders = new HttpHeaders();

		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));

		requestHeaders.set("x-fws-deviceid", deviceid);

		getDBDetails(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED,
				"DU01");
	}

	@Test
	@TestDescription(desc = "Attempt get work bank database file details with invalid Date header "
			+ "and valid Authorization header.<br><br>"
			+ "Check HTTP status code is 401 Unauthorised")
	@TestLabel(label = "Reference: 3502")
	public void test_2() {

		String dateHeader = "sdgds";

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", unrevokedUserCode);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		getDBDetails(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED,
				"DU01");
	}

	@Test
	@TestDescription(desc = "Attempt get work bank database file details with date header not "
			+ "formatted one of the RFC 2616 formats and valid Authorization header.<br><br>"
			+ "Check HTTP status code is 401 Unauthorised.")
	@TestLabel(label = "Reference: 3503")
	public void test_3() {

		String dateHeader = getNonRFC2616Date();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", unrevokedUserCode);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		getDBDetails(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED,
				"DU01");
	}

	@Test
	@TestDescription(desc = "Get Server time. Attempt get work bank database file details "
			+ "with date header more than 15 minutes in future of server time and "
			+ "valid Authorization header.<br><br>"
			+ "Check HTTP status code is 401 Unauthorised.")
	@TestLabel(label = "Reference: 3504")
	public void test_4() {

		String dateHeader = getDateAfter(1);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", unrevokedUserCode);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		getDBDetails(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED,
				"DU01");
	}

	@Test
	@TestDescription(desc = "Get Server time. Attempt get work bank database file details "
			+ "with date header more than 15 minutes in past of server time and "
			+ "valid Authorization header.<br><br>"
			+ "Check HTTP status code is 401 Unauthorised.")
	@TestLabel(label = "Reference: 3505")
	public void test_5() {

		String dateHeader = getDateBefore(1);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", unrevokedUserCode);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		getDBDetails(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED,
				"DU01");
	}

	@Test
	@TestDescription(desc = "Attempt get work bank database file details with valid "
			+ "Date header and no Authorization header.<br><br>"
			+ "Check HTTP status code is 401 Unauthorised ")
	@TestLabel(label = "Reference: 3506")
	public void test_6() {

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", unrevokedUserCode);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		getDBDetails(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED,
				"DU01");
	}

	@Test
	@TestDescription(desc = "Attempt get work bank database file details with valid "
			+ "Date header and malformed Authorization header.<br><br>"
			+ "Check HTTP status code is 401 Unauthorised.")
	@TestLabel(label = "Reference: 3507")
	public void test_7() {

		String dateHeader = getDateHeader();
		String authToken = getAuthToken(unrevokedPassword, dateHeader);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", unrevokedUserCode);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", "malformed" + unrevokedUserCode
				+ ":" + authToken);
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		getDBDetails(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED,
				"DU01");
	}

	@Test
	@TestDescription(desc = "Attempt get work bank database file details with valid "
			+ "Date header and Authorization header containing an invalid user."
			+ "<br><br>Check HTTP status code is 401 Unauthorised")
	@TestLabel(label = "Reference: 3508")
	public void test_8() {

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", unrevokedUserCode);

		HttpHeaders requestHeaders = new HttpHeaders();

		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(invalidUserCode, unrevokedPassword,
						dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		getDBDetails(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED,
				"DU01");
	}

	@Test
	@TestDescription(desc = "Attempt get work bank database file details with valid "
			+ "Date header and Authorization header signed using incorrect password.<br><br>"
			+ "Check HTTP status code is 401 Unauthorised")
	@TestLabel(label = "Reference: 3509")
	public void test_9() {

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", unrevokedUserCode);

		HttpHeaders requestHeaders = new HttpHeaders();

		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode, invalidPassword,
						dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		getDBDetails(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED,
				"DU01");
	}

	@Test
	@TestDescription(desc = "Attempt get work bank database file details with "
			+ "valid Authorization details and passing wbClass parameter equal "
			+ "to DU01.<br>"
			+ "Check HTTP status code is 200 OK.<br><br>"
			+ "Check message corresponds to WorkBankDatabaseResponse message with "
			+ "non-empty database name, size and checksum. Check these details "
			+ "match the expected file.<br>Check success element is true. ")
	@TestLabel(label = "Reference: 3510")
	public void test_10() {

		WorkBankDatabaseResponse resp = null;

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", unrevokedUserCode);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		resp = getDBDetails(headersVariables, requestHeaders, HttpStatus.OK,
				"DU01");

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

		assertTrue("File name not as expected. <br> <b>Expected:</b> "
				+ wbDU01FileName + "<br><b>Recieved: </b>"
				+ resp.getFull().getName(),
				resp.getFull().getName().equals(wbDU01FileName));

		assertTrue("Checksum not as expected. <br> <b>Expected:</b> "
				+ wbDU01Checksum + "<br><b>Recieved: </b>"
				+ resp.getFull().getChecksum(), resp.getFull().getChecksum()
				.equals(wbDU01Checksum));

		assertTrue("Size not as expected. <br> <b>Expected:</b> " + wbDU01Size
				+ "<br><b>Recieved: </b>" + resp.getFull().getSizeBytes(), resp
				.getFull().getSizeBytes() == wbDU01Size);
	}

	@Test
	@TestDescription(desc = "Attempt get work bank database file details with "
			+ "valid Authorization details and passing wbClass parameter equal "
			+ "to du01.<br>"
			+ "Check HTTP status code is 200 OK.<br><br>"
			+ "Check message corresponds to WorkBankDatabaseResponse message with "
			+ "non-empty database name, size and checksum. Check these details "
			+ "match the expected file.<br>Check success element is true. ")
	@TestLabel(label = "Reference: 3511")
	public void test_11() {

		WorkBankDatabaseResponse resp = null;

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", unrevokedUserCode);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		resp = getDBDetails(headersVariables, requestHeaders, HttpStatus.OK,
				"du01");

		assertTrue("A Null response was returned", resp != null);

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

		assertTrue("File name not as expected. <br> <b>Expected:</b> "
				+ wbDU01FileName + "<br><b>Recieved: </b>"
				+ resp.getFull().getName(),
				resp.getFull().getName().equals(wbDU01FileName));

		assertTrue("Checksum not as expected. <br> <b>Expected:</b> "
				+ wbDU01Checksum + "<br><b>Recieved: </b>"
				+ resp.getFull().getChecksum(), resp.getFull().getChecksum()
				.equals(wbDU01Checksum));

		assertTrue("Size not as expected. <br> <b>Expected:</b> " + wbDU01Size
				+ "<br><b>Recieved: </b>" + resp.getFull().getSizeBytes(), resp
				.getFull().getSizeBytes() == wbDU01Size);
	}

	@Test
	@TestDescription(desc = "Attempt get work bank database file details with "
			+ "valid Authorization details and passing wbClass parameter equal "
			+ "to Du01.<br>"
			+ "Check HTTP status code is 200 OK.<br><br>"
			+ "Check message corresponds to WorkBankDatabaseResponse message with "
			+ "non-empty database name, size and checksum. Check these details "
			+ "match the expected file.<br>Check success element is true. ")
	@TestLabel(label = "Reference: 3512")
	public void test_12() {

		WorkBankDatabaseResponse resp = null;

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", unrevokedUserCode);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		resp = getDBDetails(headersVariables, requestHeaders, HttpStatus.OK,
				"Du01");

		assertTrue("A Null response was returned", resp != null);

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

		assertTrue("File name not as expected. <br> <b>Expected:</b> "
				+ wbDU01FileName + "<br><b>Recieved: </b>"
				+ resp.getFull().getName(),
				resp.getFull().getName().equals(wbDU01FileName));

		assertTrue("Checksum not as expected. <br> <b>Expected:</b> "
				+ wbDU01Checksum + "<br><b>Recieved: </b>"
				+ resp.getFull().getChecksum(), resp.getFull().getChecksum()
				.equals(wbDU01Checksum));

		assertTrue("Size not as expected. <br> <b>Expected:</b> " + wbDU01Size
				+ "<br><b>Recieved: </b>" + resp.getFull().getSizeBytes(), resp
				.getFull().getSizeBytes() == wbDU01Size);

	}

	@Test
	@TestDescription(desc = "Attempt get work bank database file details with valid "
			+ "Authorization details and passing dbClass set to non-existant database "
			+ "class literal (wbClass=NOTEXIST).<br>"
			+ "Check HTTP status code is 200 OK.<br><br>"
			+ "Check message corresponds to WorkBankDatabaseResponse message Check success "
			+ "element is false. Check error code is set to "
			+ Utils.WORKBANK_DB_NOT_FOUND)
	@TestLabel(label = "Reference: 3513")
	public void test_13() {

		WorkBankDatabaseResponse resp = null;

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", unrevokedUserCode);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		resp = getDBDetails(headersVariables, requestHeaders, HttpStatus.OK,
				"NOTEXIST");

		assertTrue("A Null response was returned", resp != null);

		assertTrue("Success element was true.<br><br>", !resp.isSuccess());

		assertTrue("Unexcepted exception returned. expected "
				+ Utils.WORKBANK_DB_NOT_FOUND + " recieved "
				+ resp.getError().getErrorCode(), resp.getError()
				.getErrorCode().equals(Utils.WORKBANK_DB_NOT_FOUND));
	}

	@Test
	@TestDescription(desc = "Attempt get work bank database file details with valid Authorization"
			+ "details and passing wbClass parameter equal to DU01. "
			+ "Check HTTP status code is 200 OK.<br><br>"
			+ "Check message corresponds to WorkBankDatabaseResponse message with non-empty database name, "
			+ "size date and checksum. Check these details match the expected.<br>"
			+ "Check success element is true.<br><br>"
			+ "Save the name of the latest available asset database.")
	@TestLabel(label = "Reference: 3514")
	public void test_14() {

		WorkBankDatabaseResponse resp = null;

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", unrevokedUserCode);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		requestHeaders.set("Date", dateHeader);

		resp = getDBDetails(headersVariables, requestHeaders, HttpStatus.OK,
				"DU01");

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

		assertTrue("File name not as expected. <br> <b>Expected:</b> "
				+ wbDU01FileName + "<br><b>Recieved: </b>"
				+ resp.getFull().getName(),
				resp.getFull().getName().equals(wbDU01FileName));

		assertTrue("Checksum not as expected. <br> <b>Expected:</b> "
				+ wbDU01Checksum + "<br><b>Recieved: </b>"
				+ resp.getFull().getChecksum(), resp.getFull().getChecksum()
				.equals(wbDU01Checksum));

		assertTrue("Size not as expected. <br> <b>Expected:</b> " + wbDU01Size
				+ "<br><b>Recieved: </b>" + resp.getFull().getSizeBytes(), resp
				.getFull().getSizeBytes() == wbDU01Size);

		// Store name of database for use later
		databaseName = resp.getFull().getName();

	}

	@Test
	@TestDescription(desc = "Attempt to initate download with no date header and valid "
			+ "authorizaton token.")
	@TestLabel(label = "Reference: 3515")
	public void test_15() {

		HttpHeaders requestHeaders = new HttpHeaders();
		String dateHeader = getDateHeader();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("filename", "XXX");

		initiate(requestHeaders, headersVariables, HttpStatus.UNAUTHORIZED, "DU01");
	}

	@Test
	@TestDescription(desc = "Attempt to initate download with invalid header and valid "
			+ "authorizaton token.")
	@TestLabel(label = "Reference: 3516")
	public void test_16() {

		String dateHeader = "invalid date";

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("filename", "XXX");

		initiate(requestHeaders, headersVariables, HttpStatus.UNAUTHORIZED, "DU01");
	}

	@Test
	@TestDescription(desc = "Attempt to initate download with non RFC 2616 date header and "
			+ "valid authorizaton token.")
	@TestLabel(label = "Reference: 3517")
	public void test_17() {

		String dateHeader = getNonRFC2616Date();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("Date", dateHeader);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("filename", "XXX");

		initiate(requestHeaders, headersVariables, HttpStatus.UNAUTHORIZED, "DU01");
	}

	@Test
	@TestDescription(desc = "Attempt to initate download with date header more than 15 minutes in "
			+ "front of the server time and valid authorizaton token.")
	@TestLabel(label = "Reference: 3518")
	public void test_18() {

		String dateHeader = getDateAfter(1);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("filename", "XXX");

		initiate(requestHeaders, headersVariables, HttpStatus.UNAUTHORIZED, "DU01");
	}

	@Test
	@TestDescription(desc = "Attempt to initate download with date header more than 15 minutes behind "
			+ "the server time and valid authorizaton token.")
	@TestLabel(label = "Reference: 3519")
	public void test_19() {

		String dateHeader = getDateBefore(1);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("filename", "XXX");

		initiate(requestHeaders, headersVariables, HttpStatus.UNAUTHORIZED, "DU01");
	}

	@Test
	@TestDescription(desc = "Attempt to initate download with valid date header and no "
			+ "authorization token.")
	@TestLabel(label = "Reference: 3520")
	public void test_20() {

		String dateHeader = getDateHeader();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", "FWS " + unrevokedUserCode + ":"
				+ null);
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("filename", "XXX");

		initiate(requestHeaders, headersVariables, HttpStatus.UNAUTHORIZED, "DU01");
	}

	@Test
	@TestDescription(desc = "Attempt to initate download with valid date header and malformed "
			+ "authorization token.")
	@TestLabel(label = "Reference: 3521")
	public void test_21() {

		String dateHeader = getDateHeader();
		String authToken = getAuthToken(unrevokedPassword, dateHeader);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", "FWS-malformed" + unrevokedUserCode
				+ ":" + authToken);
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("filename", "XXX");

		initiate(requestHeaders, headersVariables, HttpStatus.UNAUTHORIZED, "DU01");
	}

	@Test
	@TestDescription(desc = "Attempt to initate download with valid date header and valid authorization "
			+ "token.<br>Invalid user specified in the authorization header.")
	@TestLabel(label = "Reference: 3522")
	public void test_22() {

		String dateHeader = getDateHeader();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(invalidUserCode, unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("filename", "XXX");

		initiate(requestHeaders, headersVariables, HttpStatus.UNAUTHORIZED, "DU01");
	}

	@Test
	@TestDescription(desc = "Attempt to initate download with valid date header and valid authorization "
			+ "token signed with incorrect password.")
	@TestLabel(label = "Reference: 3523")
	public void test_23() {

		String dateHeader = getDateHeader();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode, invalidPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("filename", "XXX");

		initiate(requestHeaders, headersVariables, HttpStatus.UNAUTHORIZED, "DU01");
	}

	@Test
	@TestDescription(desc = "Initiate download of asset database. Specify a file that does"
			+ " not exist on the file system. Check that FileNotFoundException is returned.")
	@TestLabel(label = "Reference: 3524")
	public void test_24() {

		String dateHeader = getDateHeader();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("filename", workbankDatabaseInvalid);

		InitiateDownloadResponse response = null;
		response = initiate(requestHeaders, headersVariables, HttpStatus.OK, "DU01");

		assertTrue("No response was returned.", response != null);

		assertTrue("Request was sucessful.", !response.isSuccess());

		assertTrue("No error codes returned.", response.getError()
				.getErrorCode() != null);

		assertTrue("Unexpected Exception returned."
				+ "<br/><br/><b>Expected:</b> " + Utils.FILE_NOT_FOUND
				+ "<br><b>Found:</b> " + response.getError().getErrorCode(),
				response.getError().getErrorCode().equals(Utils.FILE_NOT_FOUND));
	}

	@Test
	@TestDescription(desc = "Initiate download of asset database. Specify a file that does "
			+ "exist on the file system but is too large for download. Check that "
			+ "MaximumFileSizeExceededException is returned.")
	@TestLabel(label = "Reference: 3525")
	public void test_25() {

		String dateHeader = getDateHeader();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("filename", workbankDatabaseFileMaxSizeFileName);

		InitiateDownloadResponse response = null;
		response = initiate(requestHeaders, headersVariables, HttpStatus.OK, "DU01");

		assertTrue("No response was returned.", response != null);

		assertTrue("Request was sucessful.", !response.isSuccess());

		assertTrue("No error codes returned.", response.getError()
				.getErrorCode() != null);

		assertTrue(
				"Unexpected Exception returned."
						+ "<br/><br/><b>Expected:</b> "
						+ Utils.MAX_FILESIZE_EXCEEDED + "<br><b>Found:</b> "
						+ response.getError().getErrorCode(),
				response.getError().getErrorCode()
						.equals(Utils.MAX_FILESIZE_EXCEEDED));
	}

	@Test
	@TestDescription(desc = "Initiate download of asset database. Specify file returned by call "
			+ "to get latest asset database details. Verify response message success element "
			+ "is true, an identifier has been generated, total parts and checksum are all returned.")
	@TestLabel(label = "Reference: 3526")
	public void test_26() {

		String dateHeader = getDateHeader();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("filename", databaseName);

		InitiateDownloadResponse response = null;
		response = initiate(requestHeaders, headersVariables, HttpStatus.OK, "DU01");

		assertTrue("No response was returned.", response != null);

		assertTrue("Request was not sucessful.", response.isSuccess());

		assertTrue("Unexpected error code returned."
				+ "<br/><br/><b>Expected:</b> none" + "<br><b>Found:</b> "
				+ response.getError().getErrorCode(), response.getError()
				.getErrorCode() == null);

		assertTrue("Total parts not set", response.getTotalParts() != null);

		assertTrue("Checksum not returned", response.getChecksum() != null);

		assertTrue("Identifier not returned", response.getIdentifier() != null);

		m_initiatedIdentifier = response.getIdentifier();
		m_totalParts = response.getTotalParts().intValue();
		m_checksum = response.getChecksum();
		m_fileName = response.getFileName();
	}

	@Test
	@TestDescription(desc = "Attempt to download part with no date header and valid "
			+ "authorizaton token.")
	@TestLabel(label = "Reference: 3527")
	public void test_27() {

		String dateHeader = getDateHeader();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("filename", databaseName);
		headersVariables.put("identifier", "0");
		headersVariables.put("part", "0");

		retrievePart(requestHeaders, headersVariables, HttpStatus.UNAUTHORIZED);
	}

	@Test
	@TestDescription(desc = "Attempt to download part with invalid header and valid "
			+ "authorizaton token.")
	@TestLabel(label = "Reference: 3528")
	public void test_28() {

		String dateHeader = "invalid date";

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("filename", databaseName);
		headersVariables.put("identifier", "0");
		headersVariables.put("part", "0");

		retrievePart(requestHeaders, headersVariables, HttpStatus.UNAUTHORIZED);
	}

	@Test
	@TestDescription(desc = "Attempt to download part with non RFC 2616 date header and "
			+ "valid authorizaton token.")
	@TestLabel(label = "Reference: 3529")
	public void test_29() {

		String dateHeader = getNonRFC2616Date();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("Date", dateHeader);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("filename", databaseName);
		headersVariables.put("identifier", "0");
		headersVariables.put("part", "0");

		retrievePart(requestHeaders, headersVariables, HttpStatus.UNAUTHORIZED);
	}

	@Test
	@TestDescription(desc = "Attempt to download part with date header more than 15 minutes in "
			+ "front of the server time and valid authorizaton token.")
	@TestLabel(label = "Reference: 3530")
	public void test_30() {

		String dateHeader = getDateAfter(1);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("filename", databaseName);
		headersVariables.put("identifier", "0");
		headersVariables.put("part", "0");

		retrievePart(requestHeaders, headersVariables, HttpStatus.UNAUTHORIZED);
	}

	@Test
	@TestDescription(desc = "Attempt to download part with date header more than 15 minutes behind "
			+ "the server time and valid authorizaton token.")
	@TestLabel(label = "Reference: 3531")
	public void test_31() {

		String dateHeader = getDateBefore(1);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("filename", databaseName);
		headersVariables.put("identifier", "0");
		headersVariables.put("part", "0");

		retrievePart(requestHeaders, headersVariables, HttpStatus.UNAUTHORIZED);
	}

	@Test
	@TestDescription(desc = "Attempt to download part with valid date header and no "
			+ "authorization token.")
	@TestLabel(label = "Reference: 3532")
	public void test_32() {

		String dateHeader = getDateHeader();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", "FWS " + unrevokedUserCode + ":"
				+ null);
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("filename", databaseName);
		headersVariables.put("identifier", "0");
		headersVariables.put("part", "0");

		retrievePart(requestHeaders, headersVariables, HttpStatus.UNAUTHORIZED);
	}

	@Test
	@TestDescription(desc = "Attempt to download part with valid date header and malformed "
			+ "authorization token.")
	@TestLabel(label = "Reference: 3533")
	public void test_33() {

		String dateHeader = getDateHeader();
		String authToken = getAuthToken(unrevokedPassword, dateHeader);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", "FWS-malformed" + unrevokedUserCode
				+ ":" + authToken);
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("filename", databaseName);
		headersVariables.put("identifier", "0");
		headersVariables.put("part", "0");

		retrievePart(requestHeaders, headersVariables, HttpStatus.UNAUTHORIZED);
	}

	@Test
	@TestDescription(desc = "Attempt to download part with valid date header and valid authorization "
			+ "token.<br>Invalid user specified in the authorization header.")
	@TestLabel(label = "Reference: 3534")
	public void test_34() {

		String dateHeader = getDateHeader();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("filename", databaseName);
		headersVariables.put("identifier", "0");
		headersVariables.put("part", "0");

		retrievePart(requestHeaders, headersVariables, HttpStatus.UNAUTHORIZED);
	}

	@Test
	@TestDescription(desc = "Attempt to download part with valid date header and valid authorization "
			+ "token signed with incorrect password.")
	@TestLabel(label = "Reference: 3535")
	public void test_35() {

		String dateHeader = getDateHeader();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("filename", databaseName);
		headersVariables.put("identifier", "0");
		headersVariables.put("part", "0");

		retrievePart(requestHeaders, headersVariables, HttpStatus.UNAUTHORIZED);
	}

	@Test
	@TestDescription(desc = "Attempt to download part. Specify a partnumber of NAN. "
			+ "Verify that HTTP status code 400 Bad Request is returned.")
	@TestLabel(label = "Reference: 3536")
	public void test_36() {

		String dateHeader = getDateHeader();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("filename", databaseName);
		headersVariables.put("identifier", "0");
		headersVariables.put("part", "NAN");

		retrievePart(requestHeaders, headersVariables, HttpStatus.BAD_REQUEST);
	}

	@Test
	@TestDescription(desc = "Attempt to download part. Specify an invalid identifier with valid part "
			+ "number. Verify that HTTP status code 200 is returned. Verify that the success element "
			+ "is set to false and that an 'InvalidDownloadIdentifierException' error code has been "
			+ "returned.")
	@TestLabel(label = "Reference: 3537")
	public void test_37() {

		String dateHeader = getDateHeader();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("filename", databaseName);
		headersVariables.put("identifier", "INVALID");
		headersVariables.put("part", "1");

		DownloadPartResponse objResponse;
		objResponse = retrievePart(requestHeaders, headersVariables,
				HttpStatus.BAD_REQUEST);

		assertTrue("No response was returned.", objResponse != null);

		assertTrue("Request was sucessful.", !objResponse.isSuccess());

		assertTrue("Unexpected error code returned."
				+ "<br/><br/><b>Expected:</b> "
				+ Utils.INVALID_DOWNLOAD_ID_EXCEPTION + "<br><b>Found:</b> "
				+ objResponse.getError().getErrorCode(), objResponse.getError()
				.getErrorCode().equals(Utils.INVALID_DOWNLOAD_ID_EXCEPTION));
	}

	@Test
	@TestDescription(desc = "Attempt to download part. Specify a valid identifier with part "
			+ "number <= 0. Verify that HTTP status code 200 is returned. Verify that the success element "
			+ "is set to false and that a 'PartNumberException' error code has been "
			+ "returned.")
	@TestLabel(label = "Reference: 3538")
	public void test_38() {

		String dateHeader = getDateHeader();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("filename", databaseName);
		headersVariables.put("identifier", m_initiatedIdentifier);
		headersVariables.put("part", "0");

		DownloadPartResponse objResponse;
		objResponse = retrievePart(requestHeaders, headersVariables,
				HttpStatus.OK);

		assertTrue("No response was returned.", objResponse != null);

		assertTrue("Request was sucessful.", !objResponse.isSuccess());

		assertTrue(
				"Unexpected error code returned."
						+ "<br/><br/><b>Expected:</b> "
						+ Utils.PART_NUMBER_EXCEPTION + "<br><b>Found:</b> "
						+ objResponse.getError().getErrorCode(),
				objResponse.getError().getErrorCode()
						.equals(Utils.PART_NUMBER_EXCEPTION));
	}

	@Test
	@TestDescription(desc = "Attempt to download part. Specify a valid identifier with part "
			+ "number larger than total parts for the intialised download. Verify that HTTP status "
			+ "code 200 is returned. Verify that the success element is set to false and that a "
			+ "'PartNumberException' error code has been returned.")
	@TestLabel(label = "Reference: 3539")
	public void test_39() {

		String dateHeader = getDateHeader();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("filename", databaseName);
		headersVariables.put("identifier", m_initiatedIdentifier);
		headersVariables.put("part", Integer.toString((m_totalParts + 1)));

		DownloadPartResponse objResponse;
		objResponse = retrievePart(requestHeaders, headersVariables,
				HttpStatus.OK);

		assertTrue("No response was returned.", objResponse != null);

		assertTrue("Request was sucessful.", !objResponse.isSuccess());

		assertTrue(
				"Unexpected error code returned."
						+ "<br/><br/><b>Expected:</b> "
						+ Utils.PART_NUMBER_EXCEPTION + "<br><b>Found:</b> "
						+ objResponse.getError().getErrorCode(),
				objResponse.getError().getErrorCode()
						.equals(Utils.PART_NUMBER_EXCEPTION));
	}

	@Test
	@TestDescription(desc = "Download all the parts for the download that was previously initialised. For each "
			+ "response check HTTP status code is 200 OK and verify that the success element of the response is true. "
			+ "When all parts are downloaded, assemble the parts and decode them. Generate a checksum and verify it matches "
			+ "the checksum returned when initialising the download.")
	@TestLabel(label = "Reference: 3540")
	public void test_40() {

		String dateHeader = getDateHeader();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("filename", databaseName);
		headersVariables.put("identifier", m_initiatedIdentifier);

		DownloadPartResponse objResponse;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		for (int i = 1; i <= m_totalParts; i++) {

			headersVariables.put("part", Integer.toString(i));

			objResponse = retrievePart(requestHeaders, headersVariables,
					HttpStatus.OK);

			assertTrue("No response was returned.", objResponse != null);

			assertTrue("Request was unsucessful.", objResponse.isSuccess());

			assertTrue("Unexpected error code returned."
					+ "<br/><br/><b>Expected:</b> NONE" + "<br><b>Found:</b> "
					+ objResponse.getError().getErrorCode(), objResponse
					.getError().getErrorCode() == null);

			try {

				outputStream.write(Common.decodeBase64(objResponse
						.getPartData()));
			} catch (IOException e) {

				e.printStackTrace();
				fail(e.getMessage());
			}
		}

		byte[] assembledParts = outputStream.toByteArray();
		String checksum = Common.generateMD5Checksum(assembledParts);

		assertTrue("Checksums do not match." + "<br/><br/><b>Expected:</b> "
				+ m_checksum + "<br><b>Found:</b> " + checksum,
				checksum.equals(m_checksum));

		Common.writeBytesToFile(assembledParts, m_fileName,
				workbankDatabaseDownloadDir);
	}

	// Get details of the latest available asset database.
	public WorkBankDatabaseResponse getDBDetails(
			Map<String, String> headersVariables, HttpHeaders requestHeaders,
			HttpStatus expectedStatusCode, String wbClass) {

		log.debug(">>> getDBDetails ");

		final String wsURL = baseURL + "database/workbank";

		RestOperations restTemplate = null;
		restTemplate = new RestTemplate();

		StringBuffer restCall;
		WorkBankDatabaseResponse requestResponse = null;

		requestHeaders
				.set("x-fws-applicationidentifier", applicationIdentifier);

		try {

			// Build REST URL to call
			restCall = new StringBuffer(wsURL);

			if (!test3514)
				restCall.append("?wbClass=" + wbClass);

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
				xstream.alias("WorkBankDatabaseResponse",
						WorkBankDatabaseResponse.class);
				requestResponse = (WorkBankDatabaseResponse) xstream
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
			Map<String, String> headersVariables, HttpStatus expectedStatusCode, String wbClass) {

		final String wsURL = baseURL
				+ "database/workbank/{filename:.*}/multipart";

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

			restCall.append("?wbClass=" + wbClass);
			
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
			if (he.getStatusCode() == expectedStatusCode) {
				
				log.debug(expectedStatusCode
						+ " status code returned as expected.");
			}
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
				+ "database/workbank/{filename:.*}/multipart/{identifier}/{part}";

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
}
