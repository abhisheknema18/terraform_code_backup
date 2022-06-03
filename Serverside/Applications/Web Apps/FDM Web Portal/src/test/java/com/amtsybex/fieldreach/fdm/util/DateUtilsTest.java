package com.amtsybex.fieldreach.fdm.util;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

public class DateUtilsTest {

	@Test
	public void testParseTime(){
		
		
		String res = DateUtil.parseTime("1");
		
		assertEquals(res, "00:01");
		
		res = DateUtil.parseTime("01");
		
		assertEquals(res, "00:01");
		
		res = DateUtil.parseTime("001");
		
		assertEquals(res, "00:01");
		
		res = DateUtil.parseTime("0001");
		
		assertEquals(res, "00:01");
		
		res = DateUtil.parseTime("21");
		
		assertEquals(res, "00:21");
		
		res = DateUtil.parseTime("221");
		
		assertEquals(res, "02:21");
		
		res = DateUtil.parseTime("2221");
		
		assertEquals(res, "22:21");
		
		res = DateUtil.parseTime("32221");
		
		assertEquals(res, "03:22:21");
		
		res = DateUtil.parseTime("322:21");
		
		assertEquals(res, "03:22:21");
		
		res = DateUtil.parseTime("132221");
		
		assertEquals(res, "13:22:21");
		
	}

}
