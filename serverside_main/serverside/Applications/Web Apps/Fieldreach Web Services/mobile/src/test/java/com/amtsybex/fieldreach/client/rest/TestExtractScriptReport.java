/**
 * Author:  Clive Moore
 * Date:    18/03/2016
 * Project: FDP1183
 * 
 * Copyright AMT-Sybex 2013
 */
package com.amtsybex.fieldreach.client.rest;

import org.slf4j.LoggerFactory; 
import org.slf4j.Logger;  

import com.amtsybex.fieldreach.backend.exception.ScriptDefinitionNotFoundException;
import com.amtsybex.fieldreach.extract.core.ExtractEngine;
import com.amtsybex.fieldreach.services.test.annotations.TestDescription;
import com.amtsybex.fieldreach.services.test.annotations.TestLabel;
import com.amtsybex.fieldreach.utils.impl.Common;

import org.junit.*;

import static org.junit.Assert.*;

public class TestExtractScriptReport extends CommonBase {

	static Logger log = LoggerFactory.getLogger(TestExtractScriptReport.class.getName());

	ExtractEngine fieldreachExtractEngine = (ExtractEngine) ctx.getBean("fieldreachExtractEngine");

	@Test
	@TestDescription(desc = "Attempt to extract script result. Specify a scriptid that does not exist "
			+ "in FieldReach. A ScriptDefinitionNotFoundException should be thrown.")
	@TestLabel(label = "Reference: 4201")
	public void test_1() {
		
		byte[] result = null;

		try

		{
			result = fieldreachExtractEngine.getScriptReport(super.applicationIdentifier,
					extractResultReturnIdMissing);
		} catch (Exception e) {
			assertTrue(
					"Unexepected exception thrown:" + "<br/><br/><b>Expected:</b>ScriptDefinitionNotFoundException"
							+ "<br><b>Found:</b> " + e.getClass().getName(),
						ScriptDefinitionNotFoundException.class.isInstance(e));
		}

		assertTrue("Extraction completed successfully.", result == null);

	}

	
	@Test
	@TestDescription(desc = "Attempt to extract script result. Specify a scriptid that does exist "
			+ "in FieldReach. ")
	@TestLabel(label = "Reference: 4202")
	public void test_2() {
		
		byte[] result = null;

		try {
			result = fieldreachExtractEngine.getScriptReport(super.applicationIdentifier, extractScriptIdExists);
		} catch (Exception e) {
			fail("Unexepected exception thrown: <br><b>Found:</b> " + e.getClass().getName());
		}

		assertTrue("Extraction not completed successfully.", result != null);

		Common.writeBytesToFile(result, "4202.xlsx", retrieveScriptResultDir);

	}

	
}