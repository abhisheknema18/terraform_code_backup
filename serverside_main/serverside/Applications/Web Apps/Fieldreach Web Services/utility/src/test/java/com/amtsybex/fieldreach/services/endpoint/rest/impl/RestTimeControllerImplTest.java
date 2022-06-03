package com.amtsybex.fieldreach.services.endpoint.rest.impl;

import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.amtsybex.fieldreach.services.endpoint.rest.RestTimeController;

/**
 * Unit Test Class to test RestTimeControllerImpl class.
 * @See RestTimeControllerImpl
 * @author CroninM
 */
public class RestTimeControllerImplTest {

    @Test
    //@DisplayName("Test serverTime, Time returned should be close to current time")
    public void get_serverTime_return_OK_checkIsValid() throws Exception {

    	// [arrange]
    	RestTimeController underTest = new RestTimeControllerImpl();
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(underTest).build();
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
		
		// [act]
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/time"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

		Date returnedDate = dateFormat.parse(result.getResponse().getContentAsString());
		
		// [assert]
		
		//checking if date is within 60 seconds of NOW, this should be ample time so as not to ever get a false negative on this test due to test run time.
		assertTrue(Math.abs(returnedDate.getTime() - (new Date()).getTime()) < 60000);
    }
}
