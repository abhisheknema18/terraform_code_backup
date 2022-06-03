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
public class TestUpdateWork extends CommonBase {

	@Test
	@TestDescription(desc = "Call update work without districtCode parameter and request body. HTTP status"
			+ "400 is returned.")
	@TestLabel(label = "Reference: 2001")
	public void test_1() {
		
		updateWork(null, super.existingWO, "", HttpStatus.BAD_REQUEST);
	}

	@Test
	@TestDescription(desc = "Call update work with the districtCode parameter and without a request body. "
			+ "HTTP status 400 is returned.")
	@TestLabel(label = "Reference: 2002")
	public void test_2() {
		
		updateWork(super.woDC, super.existingWO, "", HttpStatus.BAD_REQUEST);
	}

	@Test
	@TestDescription(desc = "Call update work without the districtCode parameter. Supply a workorder that does not exist"
			+ " in the workissued table in the request body. A " + Utils.WORKORDER_NOT_FOUND_EXCEPTION
			+ " message should be returned.")
	@TestLabel(label = "Reference: 2003")
	public void test_3() {
		
		log.debug(">>> test_3");

		File file = new File(StringUtils.cleanPath(super.uploadWorkOrderDir + "/" + Utils.WORKORDER_FILE_PREFIX
				+ super.updateWODoesNotExist + Utils.WORKORDER_FILE_EXTENSION));

		String woData = null;

		if (file.exists() && file.isFile()) {
			
			try {
				
				byte[] bytes = Common.getBytesFromFile(file);
				woData = new String(bytes);
				
			} catch (FileNotFoundException e) {

				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
				
			} catch (IOException e) {
				
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
			}
			
		} else {
			
			log.error("Error creating request body from work order file");
			fail("Unable to extract work order content to build request body.");
		}

		CallResponse resp = updateWork(null, super.updateWODoesNotExist, woData, HttpStatus.OK);

		// Ensure response was not a success
		assertTrue("Response was a success.", !resp.isSuccess());

		// Check for workorder not found exception
		assertTrue(
				"Unexepected exception returned. Expected: " + Utils.WORKORDER_NOT_FOUND_EXCEPTION + "<br>Returned: "
						+ resp.getError().getErrorCode(),
				resp.getError().getErrorCode().equals(Utils.WORKORDER_NOT_FOUND_EXCEPTION));

		log.debug("<<< test_3");
	}

	@Test
	@TestDescription(desc = "Call update work with the districtCode parameter. Supply a workorder that does not exist"
			+ " in the workissued table in the request body. A " + Utils.WORKORDER_NOT_FOUND_EXCEPTION
			+ " message should be returned.")
	@TestLabel(label = "Reference: 2004")
	public void test_4() {
		
		log.debug(">>> test_4");

		File file = new File(StringUtils.cleanPath(super.uploadWorkOrderDir + "/" + Utils.WORKORDER_FILE_PREFIX
				+ super.updateWODoesNotExist + Utils.WORKORDER_FILE_EXTENSION));

		String woData = null;

		if (file.exists() && file.isFile()) {
			
			try {
				
				byte[] bytes = Common.getBytesFromFile(file);
				woData = new String(bytes);
				
			} catch (FileNotFoundException e) {

				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
				
			} catch (IOException e) {
				
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
			}
			
		} else {
			
			log.error("Error creating request body from work order file");
			fail("Unable to extract work order content to build request body.");
		}

		CallResponse resp = updateWork(super.woDC, super.updateWODoesNotExist, woData, HttpStatus.OK);

		// Ensure response was not a success
		assertTrue("Response was a success.", !resp.isSuccess());

		// Check for workorder not found exception
		assertTrue(
				"Unexepected exception returned. Expected: " + Utils.WORKORDER_NOT_FOUND_EXCEPTION + "<br>Returned: "
						+ resp.getError().getErrorCode(),
				resp.getError().getErrorCode().equals(Utils.WORKORDER_NOT_FOUND_EXCEPTION));

		log.debug("<<< test_4");
	}

	@Test
	@TestDescription(desc = "Call update work without the districtCode parameter. Supply a workorder that does exist"
			+ " in the workissued table but has a workstatus of complete in the request body. A "
			+ Utils.WORKORDER_UPDATE_EXCEPTION + " message should be returned.")
	@TestLabel(label = "Reference: 2005")
	public void test_5() {
		
		log.debug(">>> test_5");

		File file = new File(StringUtils.cleanPath(super.uploadWorkOrderDir + "/" + Utils.WORKORDER_FILE_PREFIX
				+ super.updateExistingWorkOrderComplete + Utils.WORKORDER_FILE_EXTENSION));

		String woData = null;

		if (file.exists() && file.isFile()) {
			
			try {
				
				byte[] bytes = Common.getBytesFromFile(file);
				woData = new String(bytes);
				
			} catch (FileNotFoundException e) {

				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
				
			} catch (IOException e) {
				
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
			}
			
		} else {
			
			log.error("Error creating request body from work order file");
			fail("Unable to extract work order content to build request body.");
		}

		CallResponse resp = updateWork(null, super.updateExistingWorkOrderComplete, woData, HttpStatus.OK);

		// Ensure response was not a success
		assertTrue("Response was a success.", !resp.isSuccess());

		// Check for workorder not found exception
		assertTrue(
				"Unexepected exception returned. Expected: " + Utils.WORKORDER_UPDATE_EXCEPTION + "<br>Returned: "
						+ resp.getError().getErrorCode(),
				resp.getError().getErrorCode().equals(Utils.WORKORDER_UPDATE_EXCEPTION));

		log.debug("<<< test_5");
	}

	@Test
	@TestDescription(desc = "Call update work with the districtCode parameter. Supply a workorder that does exist"
			+ " in the workissued table but has a workstatus of complete in the request body. A "
			+ Utils.WORKORDER_UPDATE_EXCEPTION + " message should be returned.")
	@TestLabel(label = "Reference: 2006")
	public void test_6() {
		
		log.debug(">>> test_6");

		File file = new File(StringUtils.cleanPath(super.uploadWorkOrderDir + "/" + Utils.WORKORDER_FILE_PREFIX
				+ super.updateExistingWorkOrderComplete + Utils.WORKORDER_FILE_EXTENSION));

		String woData = null;

		if (file.exists() && file.isFile()) {
			
			try {
				
				byte[] bytes = Common.getBytesFromFile(file);
				woData = new String(bytes);
				
			} catch (FileNotFoundException e) {

				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
				
			} catch (IOException e) {
				
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
			}
			
		} else {
			
			log.error("Error creating request body from work order file");
			fail("Unable to extract work order content to build request body.");
		}

		CallResponse resp = updateWork(super.woDC, super.updateExistingWorkOrderComplete, woData, HttpStatus.OK);

		// Ensure response was not a success
		assertTrue("Response was a success.", !resp.isSuccess());

		// Check for workorder not found exception
		assertTrue(
				"Unexepected exception returned. Expected: " + Utils.WORKORDER_UPDATE_EXCEPTION + "<br>Returned: "
						+ resp.getError().getErrorCode(),
				resp.getError().getErrorCode().equals(Utils.WORKORDER_UPDATE_EXCEPTION));

		log.debug("<<< test_6");
	}

	@Test
	@TestDescription(desc = "Call update work without the districtCode parameter. Supply an invalid workorder that"
			+ " already exists in the workissued table in the request body. " + "A "
			+ Utils.WORKORDER_VALIDATION_EXCEPTION + " message should be returned.")
	@TestLabel(label = "Reference: 2007")
	public void test_7() {
		
		log.debug(">>> test_7");

		File file = new File(StringUtils.cleanPath(super.uploadWorkOrderDir + "/" + Utils.WORKORDER_FILE_PREFIX
				+ super.updateExistingWOInvalid + Utils.WORKORDER_FILE_EXTENSION));

		String woData = null;

		if (file.exists() && file.isFile()) {
			
			try {
				
				byte[] bytes = Common.getBytesFromFile(file);
				woData = new String(bytes);
				
			} catch (FileNotFoundException e) {

				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
				
			} catch (IOException e) {
				
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
			}
			
		} else {
			
			log.error("Error creating request body from work order file");
			fail("Unable to extract work order content to build request body.");
		}

		CallResponse resp = updateWork(null, super.updateExistingWOInvalid, woData, HttpStatus.OK);

		// Ensure response was not a success
		assertTrue("Response was a success.", !resp.isSuccess());

		// Ensure workorder validation was not passed
		assertTrue("Workorder file passsed validation.",
				resp.getError().getErrorCode().equals(Utils.WORKORDER_VALIDATION_EXCEPTION));

		log.debug("<<< test_7");
	}

	@Test
	@TestDescription(desc = "Call update work without the districtCode parameter. Supply an invalid workorder that"
			+ " already exists in the workissued table in the request body. " + "A "
			+ Utils.WORKORDER_VALIDATION_EXCEPTION + " message should be returned.")
	@TestLabel(label = "Reference: 2008")
	public void test_8() {
		
		log.debug(">>> test_8");

		File file = new File(StringUtils.cleanPath(super.uploadWorkOrderDir + "/" + Utils.WORKORDER_FILE_PREFIX
				+ super.updateExistingWOInvalid + Utils.WORKORDER_FILE_EXTENSION));

		String woData = null;

		if (file.exists() && file.isFile()) {
			
			try {
				
				byte[] bytes = Common.getBytesFromFile(file);
				woData = new String(bytes);
				
			} catch (FileNotFoundException e) {
				
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
				
			} catch (IOException e) {
				
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
			}
			
		} else {
			
			log.error("Error creating request body from work order file");
			fail("Unable to extract work order content to build request body.");
		}

		CallResponse resp = updateWork(super.woDC, super.updateExistingWOInvalid, woData, HttpStatus.OK);

		// Ensure response was not a success
		assertTrue("Response was a success.", !resp.isSuccess());

		// Ensure workorder validation was not passed
		assertTrue("Workorder file passsed validation.",
				resp.getError().getErrorCode().equals(Utils.WORKORDER_VALIDATION_EXCEPTION));

		log.debug("<<< test_8");
	}

	@Test
	@TestDescription(desc = "Call update work without the districtCode parameter. Supply a valid workorder that"
			+ " does already exist in the workissued table in the request body. The workorder status should not match the configured "
			+ "can’t do or “cancelled” status values. Ensure the file contains different workgroup information. No exceptions will be "
			+ "returned, the older workorder will be deleted, a new one created, and the Fieldreach database updated.")
	@TestLabel(label = "Reference: 2009")
	public void test_9() {
		
		log.debug(">>> test_9");

		File file = new File(StringUtils.cleanPath(super.uploadWorkOrderDir + "/" + Utils.WORKORDER_FILE_PREFIX
				+ super.updateExistingWOValidReallocate + Utils.WORKORDER_FILE_EXTENSION));

		String woData = null;

		if (file.exists() && file.isFile()) {
			
			try {
				
				byte[] bytes = Common.getBytesFromFile(file);
				woData = new String(bytes);
				
			} catch (FileNotFoundException e) {
				
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
				
			} catch (IOException e) {
				
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
			}
			
		} else {
			
			log.error("Error creating request body from work order file");
			fail("Unable to extract work order content to build request body.");
		}

		CallResponse resp = updateWork(null, super.updateExistingWOValidReallocate, woData, HttpStatus.OK);

		// Ensure response was a success
		assertTrue("Response was not a success. Error Code: " + resp.getError().getErrorCode(), resp.isSuccess());

		// Check no exceptions are returned
		assertTrue("Unexpected Exception occured: " + resp.getError().getErrorCode(),
				resp.getError().getErrorCode() == null);

		log.debug("<<< test_9");
	}

	@Test
	@TestDescription(desc = "Call update work with the districtCode parameter. Supply a valid workorder that"
			+ " already exists in the workissued table in the request body. The workorder status should not match"
			+ " the configured can’t do or cancelled status values. Ensure the file contains different workgroup"
			+ " information. No exceptions will be returned, the older workorder will be deleted, a new one created,"
			+ " and the Fieldreach database updated.")
	@TestLabel(label = "Reference: 2010")
	public void test_10() {
		
		log.debug(">>> test_10");

		File file = new File(StringUtils.cleanPath(super.uploadWorkOrderDir + "/" + Utils.WORKORDER_FILE_PREFIX
				+ super.updateExistingWOValidReallocate + Utils.WORKORDER_FILE_EXTENSION));

		String woData = null;

		if (file.exists() && file.isFile()) {
			
			try {
				
				byte[] bytes = Common.getBytesFromFile(file);
				woData = new String(bytes);
				
			} catch (FileNotFoundException e) {
				
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
				
			} catch (IOException e) {
				
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
			}
			
		} else {
			
			log.error("Error creating request body from work order file");
			fail("Unable to extract work order content to build request body.");
		}

		CallResponse resp = updateWork(super.woDC, super.updateExistingWOValidReallocate, woData, HttpStatus.OK);

		// Ensure response was a success
		assertTrue("Response was not a success. Error Code: " + resp.getError().getErrorCode(), resp.isSuccess());

		// Check no exceptions are returned
		assertTrue("Unexpected Exception occured: " + resp.getError().getErrorCode(),
				resp.getError().getErrorCode() == null);

		log.debug("<<< test_10");
	}

	@Test
	@TestDescription(desc = "Call update work without the districtCode parameter. Supply a valid workorder that"
			+ " does already exist in the workissued table in the request body. The workorder status should not"
			+ " match the configured can’t do or cancelled status values.  Ensure the file contains"
			+ " different information. No exceptions will be returned, the older workorder will be deleted,"
			+ " a new one created, and the Fieldreach database updated.")
	@TestLabel(label = "Reference: 2011")
	public void test_11() {
		
		log.debug(">>> test_11");

		File file = new File(StringUtils.cleanPath(super.uploadWorkOrderDir + "/" + Utils.WORKORDER_FILE_PREFIX
				+ super.updateExistingWOValid + Utils.WORKORDER_FILE_EXTENSION));

		String woData = null;

		if (file.exists() && file.isFile()) {
			
			try {
				
				byte[] bytes = Common.getBytesFromFile(file);
				woData = new String(bytes);
				
			} catch (FileNotFoundException e) {
				
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
				
			} catch (IOException e) {
				
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
			}
			
		} else {
			
			log.error("Error creating request body from work order file");
			fail("Unable to extract work order content to build request body.");
		}

		CallResponse resp = updateWork(null, super.updateExistingWOValid, woData, HttpStatus.OK);

		// Ensure response was a success
		assertTrue("Response was not a success. Error Code: " + resp.getError().getErrorCode(), resp.isSuccess());

		// Check no exceptions are returned
		assertTrue("Unexpected Exception occured: " + resp.getError().getErrorCode(),
				resp.getError().getErrorCode() == null);

		log.debug("<<< test_11");
	}

	@Test
	@TestDescription(desc = "Call update work with the districtCode parameter. Supply a valid workorder that"
			+ " already exists in the workissued table in the request body. The workorder status should not"
			+ " match the configured can’t do or cancelled status values. Ensure the file contains different"
			+ " information. No exceptions will be returned, the older worokorder will be deleted, a new one"
			+ " created, and the Fieldreach database updated.")
	@TestLabel(label = "Reference: 2012")
	public void test_12() {
		
		log.debug(">>> test_12");

		File file = new File(StringUtils.cleanPath(super.uploadWorkOrderDir + "/" + Utils.WORKORDER_FILE_PREFIX
				+ super.updateExistingWOValid + Utils.WORKORDER_FILE_EXTENSION));

		String woData = null;

		if (file.exists() && file.isFile()) {
			
			try {
				
				byte[] bytes = Common.getBytesFromFile(file);
				woData = new String(bytes);
				
			} catch (FileNotFoundException e) {
				
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
				
			} catch (IOException e) {
				
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
			}
			
		} else {
			
			log.error("Error creating request body from work order file");
			fail("Unable to extract work order content to build request body.");
		}

		CallResponse resp = updateWork(super.woDC, super.updateExistingWOValid, woData, HttpStatus.OK);

		// Ensure response was a success
		assertTrue("Response was not a success. Error Code: " + resp.getError().getErrorCode(), resp.isSuccess());

		// Check no exceptions are returned
		assertTrue("Unexpected Exception occured: " + resp.getError().getErrorCode(),
				resp.getError().getErrorCode() == null);

		log.debug("<<< test_12");
	}

	@Test
	@TestDescription(desc = "Call the updatework web service specifying a work order that exists in "
			+ "the WorkIssued table that has a district code other than NA in the request"
			+ "body. Ensure the work order is valid and contains no district code element. "
			+ "Do not specify the optional districtCode parameter. A " + Utils.WORKORDER_NOT_FOUND_EXCEPTION
			+ " exception message should be returned. ")
	@TestLabel(label = "Reference: 2013")
	public void test_13() {
		
		log.debug(">>> test_13");

		File file = new File(StringUtils.cleanPath(super.uploadWorkOrderDir + "/" + Utils.WORKORDER_FILE_PREFIX
				+ super.updateDistrictCodeMissing + Utils.WORKORDER_FILE_EXTENSION));

		String woData = null;

		if (file.exists() && file.isFile()) {
			
			try {
				
				byte[] bytes = Common.getBytesFromFile(file);
				woData = new String(bytes);
				
			} catch (FileNotFoundException e) {
				
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
				
			} catch (IOException e) {
				
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
			}
			
		} else {
			
			log.error("Error creating request body from work order file");
			fail("Unable to extract work order content to build request body.");
		}

		CallResponse resp = updateWork(null, super.updateDistrictCodeMissing, woData, HttpStatus.OK);

		// Ensure response was a success
		assertTrue("Response was a success.", !resp.isSuccess());

		// Check no exceptions are returned
		assertTrue("Unexpected Exception occured: " + resp.getError().getErrorCode(),
				resp.getError().getErrorCode().equals(Utils.WORKORDER_NOT_FOUND_EXCEPTION));

		log.debug("<<< test_13");
	}

	@Test
	@TestDescription(desc = "Call update work without the districtCode parameter. Supply a valid workorder that"
			+ " does already exist in the workissued table in the request body. The workorder status should  match the configured "
			+ "can’t do status value. Ensure the file contains different workgroup information. No exceptions will be "
			+ "returned, the older workorder will be deleted, a new one created, and the Fieldreach database updated.")
	@TestLabel(label = "Reference: 2014")
	public void test_14() {
		
		log.debug(">>> test_14");

		File file = new File(StringUtils.cleanPath(super.uploadWorkOrderDir + "/" + Utils.WORKORDER_FILE_PREFIX
				+ super.updateReissueCantDo + Utils.WORKORDER_FILE_EXTENSION));

		String woData = null;

		if (file.exists() && file.isFile()) {
			
			try {
				
				byte[] bytes = Common.getBytesFromFile(file);
				woData = new String(bytes);
				
			} catch (FileNotFoundException e) {
				
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
				
			} catch (IOException e) {
				
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
			}
			
		} else {
			
			log.error("Error creating request body from work order file");
			fail("Unable to extract work order content to build request body.");
		}

		CallResponse resp = updateWork(null, super.updateReissueCantDo, woData, HttpStatus.OK);

		// Ensure response was a success
		assertTrue("Response was not a success. Error Code: " + resp.getError().getErrorCode(), resp.isSuccess());

		// Check no exceptions are returned
		assertTrue("Unexpected Exception occured: " + resp.getError().getErrorCode(),
				resp.getError().getErrorCode() == null);

		log.debug("<<< test_14");
	}

	@Test
	@TestDescription(desc = "Call update work with the districtCode parameter. Supply a valid workorder that"
			+ " already exists in the workissued table in the request body. The workorder status should  match"
			+ " the configured can’t do status value. Ensure the file contains different workgroup"
			+ " information. No exceptions will be returned, the older workorder will be deleted, a new one created,"
			+ " and the Fieldreach database updated.")
	@TestLabel(label = "Reference: 2015")
	public void test_15() {
		
		log.debug(">>> test_15");

		File file = new File(StringUtils.cleanPath(super.uploadWorkOrderDir + "/" + Utils.WORKORDER_FILE_PREFIX
				+ super.updateReissueCantDo + Utils.WORKORDER_FILE_EXTENSION));

		String woData = null;

		if (file.exists() && file.isFile()) {
			
			try {
				
				byte[] bytes = Common.getBytesFromFile(file);
				woData = new String(bytes);
				
			} catch (FileNotFoundException e) {
				
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
				
			} catch (IOException e) {
				
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
			}
			
		} else {
			
			log.error("Error creating request body from work order file");
			fail("Unable to extract work order content to build request body.");
		}

		CallResponse resp = updateWork(super.woDC, super.updateReissueCantDo, woData, HttpStatus.OK);

		// Ensure response was a success
		assertTrue("Response was not a success. Error Code: " + resp.getError().getErrorCode(), resp.isSuccess());

		// Check no exceptions are returned
		assertTrue("Unexpected Exception occured: " + resp.getError().getErrorCode(),
				resp.getError().getErrorCode() == null);

		log.debug("<<< test_15");
	}

	@Test
	@TestDescription(desc = "Call update work without the districtCode parameter. Supply a valid workorder that"
			+ " does already exist in the workissued table in the request body. The workorder status should  match the configured "
			+ "cancelled status value. Ensure the file contains different workgroup information. No exceptions will be "
			+ "returned, the older workorder will be deleted, a new one created, and the Fieldreach database updated.")
	@TestLabel(label = "Reference: 2016")
	public void test_16() {
		
		log.debug(">>> test_16");

		File file = new File(StringUtils.cleanPath(super.uploadWorkOrderDir + "/" + Utils.WORKORDER_FILE_PREFIX
				+ super.updateReissueCancelled + Utils.WORKORDER_FILE_EXTENSION));

		String woData = null;

		if (file.exists() && file.isFile()) {
			
			try {
				
				byte[] bytes = Common.getBytesFromFile(file);
				woData = new String(bytes);
				
			} catch (FileNotFoundException e) {
				
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
				
			} catch (IOException e) {
				
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
			}
			
		} else {
			
			log.error("Error creating request body from work order file");
			fail("Unable to extract work order content to build request body.");
		}

		CallResponse resp = updateWork(null, super.updateReissueCancelled, woData, HttpStatus.OK);

		// Ensure response was a success
		assertTrue("Response was not a success. Error Code: " + resp.getError().getErrorCode(), resp.isSuccess());

		// Check no exceptions are returned
		assertTrue("Unexpected Exception occured: " + resp.getError().getErrorCode(),
				resp.getError().getErrorCode() == null);

		
		log.debug("<<< test_16");
	}

	@Test
	@TestDescription(desc = "Call update work with the districtCode parameter. Supply a valid workorder that"
			+ " already exists in the workissued table in the request body. The workorder status should  match"
			+ " the configured cancelled status value. Ensure the file contains different workgroup"
			+ " information. No exceptions will be returned, the older workorder will be deleted, a new one created,"
			+ " and the Fieldreach database updated.")
	@TestLabel(label = "Reference: 2017")
	public void test_17() {
		
		log.debug(">>> test_17");

		File file = new File(StringUtils.cleanPath(super.uploadWorkOrderDir + "/" + Utils.WORKORDER_FILE_PREFIX
				+ super.updateReissueCancelled + Utils.WORKORDER_FILE_EXTENSION));

		String woData = null;

		if (file.exists() && file.isFile()) {
			
			try {
				
				byte[] bytes = Common.getBytesFromFile(file);
				woData = new String(bytes);
				
			} catch (FileNotFoundException e) {
				
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
				
			} catch (IOException e) {
				
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
			}
			
		} else {
			
			log.error("Error creating request body from work order file");
			fail("Unable to extract work order content to build request body.");
		}

		CallResponse resp = updateWork(super.woDC, super.updateReissueCancelled, woData, HttpStatus.OK);

		// Ensure response was a success
		assertTrue("Response was not a success. Error Code: " + resp.getError().getErrorCode(), resp.isSuccess());

		// Check no exceptions are returned
		assertTrue("Unexpected Exception occured: " + resp.getError().getErrorCode(),
				resp.getError().getErrorCode() == null);

		log.debug("<<< test_17");
	}
	
	
	@Test
	@TestDescription(desc = "Call update work with the districtCode parameter. Supply a valid workorder. Supply a FWS authorization header, an UnauthorizedException should be returned.")
	@TestLabel(label = "Reference: 2018")
	public void test_18() {
		
		log.debug(">>> test_18");

		File file = new File(StringUtils.cleanPath(super.uploadWorkOrderDir + "/" + Utils.WORKORDER_FILE_PREFIX
				+ super.updateReissueCancelled + Utils.WORKORDER_FILE_EXTENSION));

		String woData = null;

		if (file.exists() && file.isFile()) {
			
			try {
				
				byte[] bytes = Common.getBytesFromFile(file);
				woData = new String(bytes);
				
			} catch (FileNotFoundException e) {
				
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
				
			} catch (IOException e) {
				
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
			}
			
		} else {
			
			log.error("Error creating request body from work order file");
			fail("Unable to extract work order content to build request body.");
		}

		updateWork(super.woDC, super.updateReissueCancelled, woData, HttpStatus.UNAUTHORIZED, false);


		log.debug("<<< test_18");
	}

	
	protected CallResponse updateWork(String districtCode, String woNo, String body, HttpStatus expectedStatusCode) {
		return this.updateWork(districtCode, woNo, body, expectedStatusCode, true);
	}
	
	/**
	 * Method to call the update work web service
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
	protected CallResponse updateWork(String districtCode, String woNo, String body, HttpStatus expectedStatusCode, boolean iwsType) {

		log.debug(">>> updateWork districtCode= " + districtCode + " body=xxxx");

		final String wsURL = baseURL + "updatework/" + woNo;

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
			
			requestHeaders.set("x-fws-applicationidentifier", applicationIdentifier);

			// Build request Body
			String requestBody = body;

			HttpEntity<String> requestEntity = new HttpEntity<String>(requestBody, requestHeaders);

			// Make REST call and retrieve the results
			ResponseEntity<String> resultsExchange = restTemplate.exchange(restCall.toString(), HttpMethod.PUT,
					requestEntity, String.class);

			String results = resultsExchange.getBody();

			log.debug("update work results : " + results);
			log.debug("HTTP Status Code: " + resultsExchange.getStatusCode());

			assertTrue("Result is null", results != null);
			assertTrue("Unexpected HTTP Status Code. Expected: " + expectedStatusCode.value() + "<br>Recieved: ",
					resultsExchange.getStatusCode().value() == expectedStatusCode.value());

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
				
				log.debug(expectedStatusCode + " status code returned as expected.");
				
			} else {
				
				log.error("Client Exception :" + he.getMessage());
				fail("Client Exception :" + he.getMessage());
			}
			
		} catch (Exception e) {
			
			log.error("Client Exception :" + e.getMessage());
			fail("Client Exception :" + e.getMessage());
		}

		log.debug("<<< updateWork");

		return resp;
	}
}
