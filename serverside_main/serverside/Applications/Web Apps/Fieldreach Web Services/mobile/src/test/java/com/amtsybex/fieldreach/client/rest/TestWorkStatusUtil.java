package com.amtsybex.fieldreach.client.rest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.List;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.services.endpoint.common.WorkStatus;
import com.amtsybex.fieldreach.services.endpoint.common.WorkStatus.WORKSTATUSCATEGORY;
import com.amtsybex.fieldreach.services.endpoint.common.WorkStatus.WORKSTATUSDESIGNATION;
import com.amtsybex.fieldreach.services.test.annotations.TestDescription;
import com.amtsybex.fieldreach.services.test.annotations.TestLabel;

public class TestWorkStatusUtil extends CommonBase  {

	static Logger log = LoggerFactory.getLogger(TestWorkStatusUtil.class.getName());

	@Autowired
	private WorkStatus workStatus;
	
	@Test
	@TestDescription(desc="Test work status predispatch has 3 items")
	@TestLabel(label="Reference: 6201")
	public void test_1() 
	{
		
		if (this.workStatus == null)
			this.workStatus = (WorkStatus) ctx.getBean("workStatus");
		
		try {
			assertTrue("Predispatch should have 3 items", workStatus.getPreDispatchStatusList(null).size() == 3 );
		} catch (FRInstanceException e) {
			fail("FRInstanceException " + e.getMessage());
		}
	}
	
	@Test
	@TestDescription(desc="Test work status close is populated and searchable by category and designation")
	@TestLabel(label="Reference: 6202")
	public void test_2() 
	{
		
		if (this.workStatus == null)
			this.workStatus = (WorkStatus) ctx.getBean("workStatus");
		
		try {
			assertTrue("work status close not found", workStatus.getSystemWorkStatusByDesignation(null, WORKSTATUSDESIGNATION.CLOSED).equalsIgnoreCase("COMPLETE") );
			assertTrue("work status close != 1 count", workStatus.getSystemWorkStatusesByDesignation(null, WORKSTATUSDESIGNATION.CLOSED).size() == 1 );
			assertTrue("work status close category count != 1", workStatus.getSystemWorkStatusesByCategory(null, WORKSTATUSCATEGORY.CLOSED).size() == 1 );
			assertTrue("Work status close complete not found", workStatus.getSystemWorkStatusesByCategory(null, WORKSTATUSCATEGORY.CLOSED).get(0).equalsIgnoreCase("COMPLETE") );

		} catch (FRInstanceException e) {
			fail("FRInstanceException " + e.getMessage());
		}
	}
	
	@Test
	@TestDescription(desc="Test work status cancel is populated and searchable by category and designation")
	@TestLabel(label="Reference: 6203")
	public void test_3() 
	{
		
		if (this.workStatus == null)
			this.workStatus = (WorkStatus) ctx.getBean("workStatus");
		
		try {
			assertTrue("work status cancel not found", workStatus.getSystemWorkStatusByDesignation(null, WORKSTATUSDESIGNATION.CANCELLED).equalsIgnoreCase("CANCELLED") );
			assertTrue("work status cancel != 1 count", workStatus.getSystemWorkStatusesByDesignation(null, WORKSTATUSDESIGNATION.CANCELLED).size() == 1 );
			assertTrue("work status cancel category count != 1", workStatus.getSystemWorkStatusesByCategory(null, WORKSTATUSCATEGORY.CANCELLED).size() == 1 );
			assertTrue("Work status cancel complete not found", workStatus.getSystemWorkStatusesByCategory(null, WORKSTATUSCATEGORY.CANCELLED).get(0).equalsIgnoreCase("CANCELLED") );

		} catch (FRInstanceException e) {
			fail("FRInstanceException " + e.getMessage());
		}
	}
	
	@Test
	@TestDescription(desc="Test work status cantdo is populated and searchable by designation")
	@TestLabel(label="Reference: 6204")
	public void test_4() 
	{
		
		if (this.workStatus == null)
			this.workStatus = (WorkStatus) ctx.getBean("workStatus");
		
		try {
			assertTrue("work status cantdo not found", workStatus.getSystemWorkStatusByDesignation(null, WORKSTATUSDESIGNATION.CANTDO).equalsIgnoreCase("REJECTED") );
			assertTrue("work status cantdo != 1 count", workStatus.getSystemWorkStatusesByDesignation(null, WORKSTATUSDESIGNATION.CANTDO).size() == 1 );
			assertTrue("work status cantdo not in predispatch", workStatus.getPreDispatchStatusList(null).contains("REJECTED"));

		} catch (FRInstanceException e) {
			fail("FRInstanceException " + e.getMessage());
		}
	}
	
	@Test
	@TestDescription(desc="Test work status released is populated and searchable by designation")
	@TestLabel(label="Reference: 6205")
	public void test_5() 
	{
		
		if (this.workStatus == null)
			this.workStatus = (WorkStatus) ctx.getBean("workStatus");
		
		try {
			assertTrue("work status released not found", workStatus.getSystemWorkStatusByDesignation(null, WORKSTATUSDESIGNATION.RELEASED).equalsIgnoreCase("RELEASED") );
			assertTrue("work status released != 1 count", workStatus.getSystemWorkStatusesByDesignation(null, WORKSTATUSDESIGNATION.RELEASED).size() == 1 );
			assertTrue("work status released not in predispatch", workStatus.getPreDispatchStatusList(null).contains("RELEASED"));

		} catch (FRInstanceException e) {
			fail("FRInstanceException " + e.getMessage());
		}
	}
	
	@Test
	@TestDescription(desc="Test work status recall is populated and searchable by designation")
	@TestLabel(label="Reference: 6206")
	public void test_6() 
	{
		
		if (this.workStatus == null)
			this.workStatus = (WorkStatus) ctx.getBean("workStatus");
		
		try {
			assertTrue("work status recall not found", workStatus.getSystemWorkStatusByDesignation(null, WORKSTATUSDESIGNATION.RECALLED).equalsIgnoreCase("RECALL") );
			assertTrue("work status recall != 1 count", workStatus.getSystemWorkStatusesByDesignation(null, WORKSTATUSDESIGNATION.RECALLED).size() == 1 );
			assertTrue("work status recall not in predispatch", workStatus.getPreDispatchStatusList(null).contains("RECALL"));

		} catch (FRInstanceException e) {
			fail("FRInstanceException " + e.getMessage());
		}
	}
	
	@Test
	@TestDescription(desc="Test work status PRECLOSEAPPROVAL is populated and searchable by designation")
	@TestLabel(label="Reference: 6207")
	public void test_7() 
	{
		
		if (this.workStatus == null)
			this.workStatus = (WorkStatus) ctx.getBean("workStatus");
		
		try {
			assertTrue("work status PRECLOSEAPPROVAL not found", workStatus.getSystemWorkStatusByDesignation(null, WORKSTATUSDESIGNATION.PRECLOSEAPPROVAL).equalsIgnoreCase("FIELD_COMP") );
			assertTrue("work status PRECLOSEAPPROVAL != 1 count", workStatus.getSystemWorkStatusesByDesignation(null, WORKSTATUSDESIGNATION.PRECLOSEAPPROVAL).size() == 1 );
			assertTrue("work status PRECLOSEAPPROVAL category count != 1", workStatus.getSystemWorkStatusesByCategory(null, WORKSTATUSCATEGORY.PRECLOSE).size() == 1 );
			assertTrue("Work status PRECLOSEAPPROVAL complete not found", workStatus.getSystemWorkStatusesByCategory(null, WORKSTATUSCATEGORY.PRECLOSE).get(0).equalsIgnoreCase("FIELD_COMP") );


		} catch (FRInstanceException e) {
			fail("FRInstanceException " + e.getMessage());
		}
	}
	
	@Test
	@TestDescription(desc="Test work status ISSUED is populated and searchable by designation")
	@TestLabel(label="Reference: 6208")
	public void test_8() 
	{
		
		if (this.workStatus == null)
			this.workStatus = (WorkStatus) ctx.getBean("workStatus");
		
		try {
			assertTrue("work status ISSUED not found", workStatus.getSystemWorkStatusByDesignation(null, WORKSTATUSDESIGNATION.ISSUED).equalsIgnoreCase("ISSUED") );
			assertTrue("work status ISSUED != 1 count", workStatus.getSystemWorkStatusesByDesignation(null, WORKSTATUSDESIGNATION.ISSUED).size() == 1 );
		} catch (FRInstanceException e) {
			fail("FRInstanceException " + e.getMessage());
		}
	}
	
	@Test
	@TestDescription(desc="Test work status REISSUED is populated and searchable by designation")
	@TestLabel(label="Reference: 6209")
	public void test_9() 
	{
		
		if (this.workStatus == null)
			this.workStatus = (WorkStatus) ctx.getBean("workStatus");
		
		try {
			assertTrue("work status REISSUED not found", workStatus.getSystemWorkStatusByDesignation(null, WORKSTATUSDESIGNATION.REISSUED).equalsIgnoreCase("REISSUED") );
			assertTrue("work status REISSUED != 1 count", workStatus.getSystemWorkStatusesByDesignation(null, WORKSTATUSDESIGNATION.REISSUED).size() == 1 );
		} catch (FRInstanceException e) {
			fail("FRInstanceException " + e.getMessage());
		}
	}
	
	
	@Test
	@TestDescription(desc="Test work status INFIELD")
	@TestLabel(label="Reference: 6210")
	public void test_10() 
	{
		
		if (this.workStatus == null)
			this.workStatus = (WorkStatus) ctx.getBean("workStatus");
		
		try {
			List <String> woStatuses = this.workStatus.getSystemWorkStatusesByCategory(null, WORKSTATUSCATEGORY.INFIELD);
			
			assertTrue("work status INFIELD != 4 count", woStatuses.size() == 4);
			
		} catch (FRInstanceException e) {
			fail("FRInstanceException " + e.getMessage());
		}
	}
	
	
}
