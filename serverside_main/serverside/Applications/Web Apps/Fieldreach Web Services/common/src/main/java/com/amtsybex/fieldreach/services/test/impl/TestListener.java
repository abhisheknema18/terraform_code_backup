/**
 * Author:  T Murray
 * Date:    04/05/2016
 * Project: FDP1170
 * 
 * Copyright AMT-Sybex 2016
 */
package com.amtsybex.fieldreach.services.test.impl;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import com.amtsybex.fieldreach.services.test.annotations.TestDescription;
import com.amtsybex.fieldreach.services.test.annotations.TestLabel;

public class TestListener extends RunListener {

	StringBuffer buffer;
	
	boolean failed = false;
	
	public TestListener(StringBuffer buffer) {
		
		this.buffer = buffer;
	}

	public void testFailure(Failure failure) {	
		
		failed = true;
		
		String desc = failure.getDescription().getAnnotation(TestDescription.class).desc();
		String name = failure.getDescription().getAnnotation(TestLabel.class).label();
		
		String failReason = failure.getMessage();
		
		createResultTable(name, desc, failReason, "red");
	}

	public void testFinished(Description description) {	
		
		if (failed == true) {
			
			failed = false;
		}
		else {
			
			String desc = description.getAnnotation(TestDescription.class).desc();
			String name = description.getAnnotation(TestLabel.class).label();
			
			createResultTable(name, desc, null, "green");
		}
	}

	
	private void createResultTable(String name, String description, 
								   String failReason, String col) {
		
		buffer.append("" +
				  "<tr>" +
				  "		<td colspan='2' style='border-bottom: 1px dashed #000000;" +
				  "         	               font-weight:bold;" +
				  "				               background-color:#FFFFFF;'>" +
				  			name +
				  "		</td>" +
				  "</tr>"+
				  "<tr>" +
				  "		<td style='width:100px; " +
				  "				   font-weight:bold;" +
				  "				   border-top:1px solid #000000;" +
				  "				   border-left: 1px solid #000000;" +
				  "         	   border-right:1px solid #000000;" +
				  "				   background-color:#F5F5F5;" +
				  "				   border-bottom:1px solid #000000;'>" +
				  "			Description: " + 
				  "		</td>" +
				  "		<td style='border-top:1px solid #000000;" +
				  "         	   border-right:1px solid #000000;" +
				  "				   background-color:#FFFFFF;" +
				  "				   border-bottom:1px solid #000000;'>" +
				  			description +
				  "		</td>" +
				  "</tr>"+
				  "<tr>" +
				  "		<td style='width:100px; " +
				  "				   font-weight:bold;" +
				  "				   background-color:#F5F5F5;" +
				  "				   border-left: 1px solid #000000;" +
				  "         	   border-right:1px solid #000000;" +
				  "				   border-bottom: 1px solid #000000;'>" +
				  "			Result:" + 
				  "		</td>" +
				  "		<td style='border-right: 1px solid #000000;" +
				  "				   background-color:"+col+";'>" +
				  "			&nbsp;" +
				  "		</td>" +
				  "</tr>");
		
			if (failReason != null) {
				
				buffer.append("<tr>" +
						  "		<td style='width:100px; " +
						  "				   font-weight:bold;" +
						  "				   border-top:1px solid #000000;" +
						  "				   border-left: 1px solid #000000;" +
						  "         	   border-right:1px solid #000000;" +
						  "				   background-color:#F5F5F5;" +
						  "				   border-bottom:1px solid #000000;'>" +
						  "			Fail Reason: " + 
						  "		</td>" +
						  "		<td style='border-top:1px solid #000000;" +
						  "         	   border-right:1px solid #000000;" +
						  "				   background-color:#FFFFFF;" +
						  "				   border-bottom:1px solid #000000;'>" +
						  						failReason +
						  "		</td>" +
						  "</tr>");
			}
			
			buffer.append("" +
					  "<tr>" +
					  "		<td colspan='2' style='height:10px'>" +
					  "			&nbsp;"	+
					  "		</td>" +
					  "</tr>");
	}
	
}
