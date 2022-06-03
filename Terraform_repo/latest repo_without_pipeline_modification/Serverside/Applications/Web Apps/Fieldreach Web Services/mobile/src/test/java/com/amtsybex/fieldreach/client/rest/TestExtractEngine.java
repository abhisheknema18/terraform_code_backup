/**
 * Author:  T Murray
 * Date:    13/01/2014
 * Project: FDE022
 * 
 * Copyright AMT-Sybex 2014
 */
package com.amtsybex.fieldreach.client.rest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.extract.core.ExtractEngine;
import com.amtsybex.fieldreach.extract.processor.result.exception.DBAccessException;
import com.amtsybex.fieldreach.extract.processor.result.exception.EventTypeNotFoundException;
import com.amtsybex.fieldreach.extract.processor.result.exception.ResultNotFoundException;
import com.amtsybex.fieldreach.services.test.annotations.TestDescription;
import com.amtsybex.fieldreach.services.test.annotations.TestLabel;
import com.amtsybex.fieldreach.utils.impl.Common;

public class TestExtractEngine extends CommonBase {

	static Logger log = LoggerFactory.getLogger(TestExtractEngine.class.getName());

	ExtractEngine fieldreachExtractEngine = (ExtractEngine) ctx.getBean("fieldreachExtractEngine");

	@Test
	@TestDescription(desc = "Attempt to extract script result. Specify a returnid that does not exist"
			+ "in the ReturnedScripts table. A ResultNotFoundException should be thrown.")
	@TestLabel(label = "Reference: 2701")
	public void test_1() {
		
		String result = null;

		try

		{
			result = fieldreachExtractEngine.extractScriptResult(super.applicationIdentifier,
					extractScriptIdNotExist, 1, "ELLIPSE", false, false);
		} catch (Exception e) {
			assertTrue(
					"Unexepected exception thrown:" + "<br/><br/><b>Expected:</b>ResultNotFoundException"
							+ "<br><b>Found:</b> " + e.getClass().getName(),
					ResultNotFoundException.class.isInstance(e));
		}

		assertTrue("Extraction completed successfully.", result == null);

	}

	/*FDP1353 no longer a valid test as this extracts all
	@Test
	@TestDescription(desc = "Attempt to extract script result. Specify a returnid that does exist"
			+ "in the ReturnedScripts table but has no corresponding configuration in "
			+ "the EventTypeFieldRoles table. A FieldRolesRolesNotFoundException should be thrown.")
	@TestLabel(label = "Reference: 2702")
	public void test_2() {
		
		String result = null;

		try {
			result = fieldreachExtractEngine.extractScriptResult(super.applicationIdentifier,
					extractResultNoScriptRoles, 3, "ELLIPSE", false, false);
			
		} catch (Exception e) {
			
			assertTrue(
					"Unexepected exception thrown:" + "<br/><br/><b>Expected:</b>FieldRolesNotFoundException"
							+ "<br><b>Found:</b> " + e.getClass().getName(),
					FieldRolesNotFoundException.class.isInstance(e));
		}

		assertTrue("Extraction completed successfully.", result == null);

	}*/

	/*FDP1353 no longer a valid test
	@Test
	@TestDescription(desc = "Attempt to extract script result. Specify a returnid that does exist"
			+ "in the ReturnedScripts table. Extract an event type that has FieldRoles defined in the "
			+ "EventTypeFieldRoles table but has no corresponding entries in the FieldRoleTypes table. "
			+ "A FieldRoleTypeNotFoundException should be thrown.")
	@TestLabel(label = "Reference: 2703")
	public void test_3() {
		String result = null;

		try {
			result = fieldreachExtractEngine.extractScriptResult(super.applicationIdentifier,
					extractResultNoScriptRoleTypes, 4, "ELLIPSE", false, true);
		} catch (Exception e) {
			assertTrue(
					"Unexepected exception thrown:" + "<br/><br/><b>Expected:</b>FieldRolesNotFoundException"
							+ "<br><b>Found:</b> " + e.getClass().getName(),
							FieldRolesNotFoundException.class.isInstance(e));
		}

		assertTrue("Extraction completed successfully.", result == null);

	}*/

	@Test
	@TestDescription(desc = "Attempt to extract script result. Specify a returnid that does exist"
			+ "in the ReturnedScripts table. Ensure there is corresponding configuration in "
			+ "the FieldRoles table. FieldRole configuration should reference event type "
			+ "that do not exist in the EventTypes table. An EventTypeNotFoundException " + "should be thrown.")
	@TestLabel(label = "Reference: 2704")
	public void test_4() {
		String result = null;

		try {
			result = fieldreachExtractEngine.extractScriptResult(super.applicationIdentifier, extractResultNoEventType,
					99999999, "ELLIPSE", false, false);
		} catch (Exception e) {
			assertTrue(
					"Unexepected exception thrown:" + "<br/><br/><b>Expected:</b>EventTypeNotFoundException"
							+ "<br><b>Found:</b> " + e.getClass().getName(),
					EventTypeNotFoundException.class.isInstance(e));
		}

		assertTrue("Extraction completed successfully.", result == null);

	}

	@Test
	@TestDescription(desc = "Configure a script for extraction by creating the appropriate records in the "
			+ "EventType, FieldRoleTypes and EventTypeFieldRoles tables in the Fieldreach database. Attempt to extract a "
			+ "result for this script that exists in the ReturnedScripts table but does not have the associated "
			+ "record in the ScriptVersions table. A DBAccessException should be thrown.")
	@TestLabel(label = "Reference: 2705")
	public void test_5() {
		String result = null;

		try {
			result = fieldreachExtractEngine.extractScriptResult(super.applicationIdentifier,
					extractResultNoScriptVersion, 1, "ELLIPSE", false, false);
		} catch (Exception e) {
			assertTrue("Unexepected exception thrown:" + "<br/><br/><b>Expected:</b>DBAccessException"
					+ "<br><b>Found:</b> " + e.getClass().getName(), DBAccessException.class.isInstance(e));
		}

		assertTrue("Extraction completed successfully.", result == null);

	}

	
	@Test
	@TestDescription(desc = "Configure a script for extraction by creating the appropriate records in the "
			+ "EventType, FieldRoleTypes and EventTypeFieldRoles tables in the Fieldreach database. Attempt to extract a "
			+ "result for this script that exists in the ReturnedScripts table but does not have the associated "
			+ "record in the Script table. A DBAccessException should be thrown.")
	@TestLabel(label = "Reference: 2706")
	public void test_6() {
		String result = null;

		try {
			result = fieldreachExtractEngine.extractScriptResult(super.applicationIdentifier, extractResultNoScript, 1,
					"ELLIPSE", false, false);
		} catch (Exception e) {
			assertTrue("Unexepected exception thrown:" + "<br/><br/><b>Expected:</b>DBAccessException"
					+ "<br><b>Found:</b> " + e.getClass().getName(), FRInstanceException.class.isInstance(e));
		}

		assertTrue("Extraction completed successfully.", result == null);

	}


	@Test
	@TestDescription(desc = "Configure a script for extraction by creating the appropriate records in the "
			+ "EventType, FieldRoleTypes and EventTypeFieldRoles tables in the Fieldreach database. Attempt to extract a "
			+ "result for this script that exists in the ReturnedScripts table but does not have the associated "
			+ "record in the Item table. A DBAccessException should be thrown.")
	@TestLabel(label = "Reference: 2707")
	public void test_7() {
		String result = null;

		try {
			result = fieldreachExtractEngine.extractScriptResult(super.applicationIdentifier, extractResultNoItem, 2,
					"ELLIPSE", false, false);
			
		} catch (Exception e) {
			assertTrue("Unexepected exception thrown:" + "<br/><br/><b>Expected:</b>DBAccessException"
					+ "<br><b>Found:</b> " + e.getClass().getName(), DBAccessException.class.isInstance(e));
		}

		assertTrue("Extraction completed successfully.", result == null);

	}

	@Test
	@TestDescription(desc = "Configure a script for extraction by creating the appropriate records in the "
			+ "EventType, FieldRoleTypes and EventTypeFieldRoles tables in the Fieldreach database. Attempt to extract a "
			+ "result for this script that exists in the ReturnedScripts table but does not have the associated "
			+ "record in the HPCUsers table. A DBAccessException should be thrown.")
	@TestLabel(label = "Reference: 2708")
	public void test_8() {
		String result = null;

		try {
			result = fieldreachExtractEngine.extractScriptResult(super.applicationIdentifier, extractResultNoUser, 2,
					"ELLIPSE", false, false);
		} catch (Exception e) {
			assertTrue("Unexepected exception thrown:" + "<br/><br/><b>Expected:</b>DBAccessException"
					+ "<br><b>Found:</b> " + e.getClass().getName(), DBAccessException.class.isInstance(e));
		}

		assertTrue("Extraction completed successfully.", result == null);

	}

	@Test
	@TestDescription(desc = "Configure a script for extraction by creating the appropriate records in the "
			+ "EventType, FieldRoleTypes and EventTypeFieldRoles tables in the Fieldreach database. Attempt to extract a "
			+ "result for this script that exists in the ReturnedScripts table but does not have the associated "
			+ "record in the HPCWorkgroups table. A DBAccessException should be thrown.")
	@TestLabel(label = "Reference: 2709")
	public void test_9() {
		String result = null;

		try {
			result = fieldreachExtractEngine.extractScriptResult(super.applicationIdentifier, extractResultNoWorkgroup,
					2, "ELLIPSE", false, false);
		} catch (Exception e) {
			assertTrue("Unexepected exception thrown:" + "<br/><br/><b>Expected:</b>DBAccessException"
					+ "<br><b>Found:</b> " + e.getClass().getName(), DBAccessException.class.isInstance(e));
		}

		assertTrue("Extraction completed successfully.", result == null);

	}

	@Test
	@TestDescription(desc = "Configure a script for extraction by creating the appropriate records in the "
			+ "EventType, FieldRoleTypes and EventTypeFieldRoles tables in the Fieldreach database. Attempt to extract a "
			+ "result for this script that exists in the ReturnedScripts table but does not have the associated "
			+ "record in the ScriptResults table. A DBAccessException should be thrown.")
	@TestLabel(label = "Reference: 2710")
	public void test_10() {
		String result = null;

		try {
			result = fieldreachExtractEngine.extractScriptResult(super.applicationIdentifier,
					extractResultNoScriptResults, 2, "ELLIPSE", false, false);
		} catch (Exception e) {
			assertTrue("Unexepected exception thrown:" + "<br/><br/><b>Expected:</b>DBAccessException"
					+ "<br><b>Found:</b> " + e.getClass().getName(), DBAccessException.class.isInstance(e));
		}

		assertTrue("Extraction completed successfully.", result == null);

	}

	@Test
	@TestDescription(desc = "Configure a script for extraction by creating the appropriate records in the "
			+ "EventType, FieldRoleTypes and EventTypeFieldRoles tables in the Fieldreach database. Ensure all "
			+ "necessary information exists in the database to enable extraction. Verify that the result "
			+ "has been extracted by checking for the existence of the file 2711.xml.")
	@TestLabel(label = "Reference: 2711")
	public void test_11() {
		String result = null;

		try {
			result = fieldreachExtractEngine.extractScriptResult(super.applicationIdentifier, extractResult, 2,
					"ELLIPSE", false, false);
		} catch (Exception e) {
			fail("Unexepected exception thrown: <br><b>Found:</b> " + e.getClass().getName());
		}

		assertTrue("Extraction not completed successfully.", result != null);

		Common.writeBytesToFile(result.getBytes(), "2711.xml", retrieveScriptResultDir);
	}

	@Test
	@TestDescription(desc = "Extract the script result extracted for test 2711. Extract ALL script result data. "
			+ "Do not extract binary response data. Verify that the result has been extracted by checking for "
			+ "the existence of the file 2712.xml")
	@TestLabel(label = "Reference: 2712")
	public void test_12() {
		String result = null;

		try {
			result = fieldreachExtractEngine.extractScriptResultAll(super.applicationIdentifier, extractResult, "TEST",
					false, false, false, false);
		} catch (Exception e) {
			fail("Unexepected exception thrown: <br><b>Found:</b> " + e.getClass().getName());
		}

		assertTrue("Extraction not completed successfully.", result != null);

		Common.writeBytesToFile(result.getBytes(), "2712.xml", retrieveScriptResultDir);

	}

	@Test
	@TestDescription(desc = "Extract the script result extracted for test 2711. Extract ALL script result data. "
			+ "Also extract binary response data. Verify that the result has been extracted by checking for "
			+ "the existence of the file 2713.xml")
	@TestLabel(label = "Reference: 2713")
	public void test_16() {
		String result = null;

		try {
			result = fieldreachExtractEngine.extractScriptResultAll(super.applicationIdentifier, extractResult, "TEST",
					true, false, false, false);
		} catch (Exception e) {
			fail("Unexepected exception thrown: <br><b>Found:</b> " + e.getClass().getName());
		}

		assertTrue("Extraction not completed successfully.", result != null);

		Common.writeBytesToFile(result.getBytes(), "2713.xml", retrieveScriptResultDir);

	}
	
}
