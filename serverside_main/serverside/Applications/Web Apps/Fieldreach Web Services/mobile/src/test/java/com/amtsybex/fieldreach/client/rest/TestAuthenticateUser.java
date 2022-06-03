/**
 * Author:  T Goodwin
 * Date:    23/01/2014
 * Project: FDE022
 * 
 * Copyright AMT-Sybex 2014
 */
package com.amtsybex.fieldreach.client.rest;

import org.slf4j.LoggerFactory; 
import org.slf4j.Logger;  

import org.junit.Test;
import static org.junit.Assert.*;

import org.springframework.http.HttpStatus;
import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.amtsybex.fieldreach.services.test.annotations.TestDescription;
import com.amtsybex.fieldreach.services.test.annotations.TestLabel;
import com.amtsybex.fieldreach.services.utils.Utils;

/**
 * Class to facilitate automated testing of the User Authentication
 * functionality.
 */
public class TestAuthenticateUser extends CommonBase {

	static Logger log = LoggerFactory.getLogger(TestAuthenticateUser.class.getName());

	/* removed authentication but some test cases will have to be reviewed
	@Test
	@TestDescription(desc = "Authenticate with no userCode, userPassword or appCode supplied"
			+ "<br>HTTP status of 400 should be returned."
			+ "<br><br>usercode: null<br>password: null<br>appcode:null")
	@TestLabel(label = "Reference: 1001")
	public void test_1() {
		
		AuthenticateResponse user;

		user = loginUser(null, null, null, HttpStatus.BAD_REQUEST);

		assertTrue("AuthenticateResponse was returned.", user == null);
	}
	
	@Test
	@TestDescription(desc = "Authenticate with userCode but no userPassword or appCode supplied"
			+ "<br>HTTP status of 400 should be returned."
			+ "<br><br>usercode: unrevokedUserCode<br>password: null<br>appcode:null")
	@TestLabel(label = "Reference: 1001")
	public void test_2() {
		
		AuthenticateResponse user;

		user = loginUser(unrevokedUserCode, null, null, HttpStatus.BAD_REQUEST);

		assertTrue("AuthenticateResponse was returned.", user == null);
	}
	
	@Test
	@TestDescription(desc = "Authenticate with userPassword but no userCode or appCode supplied"
			+ "<br>HTTP status of 400 should be returned."
			+ "<br><br>usercode: null<br>password: unrevokedPassword<br>appcode:null")
	@TestLabel(label = "Reference: 1003")
	public void test_3() {
		
		AuthenticateResponse user;

		user = loginUser(null, unrevokedPassword, null, HttpStatus.BAD_REQUEST);

		assertTrue("AuthenticateResponse was returned.", user == null);
	}
	
	@Test
	@TestDescription(desc = "Authenticate with AppCode but no userCode or userPassword supplied"
			+ "<br>HTTP status of 400 should be returned."
			+ "<br><br>usercode: null<br>password: null<br>appcode: appCode")
	@TestLabel(label = "Reference: 1004")
	public void test_4() {
		
		AuthenticateResponse user;

		user = loginUser(null, null, appCode, HttpStatus.BAD_REQUEST);

		assertTrue("AuthenticateResponse was returned.", user == null);
	}
	
	@Test
	@TestDescription(desc = "Authenticate with userCode and userPassword but no appCode supplied"
			+ "<br>HTTP status of 400 should be returned."
			+ "<br><br>usercode: unrevokedUserCode<br>password: null<br>appcode: null")
	@TestLabel(label = "Reference: 1005")
	public void test_5() {
		
		AuthenticateResponse user;

		user = loginUser(unrevokedUserCode, unrevokedPassword, null, HttpStatus.BAD_REQUEST);

		assertTrue("AuthenticateResponse was returned.", user == null);
	}
	
	@Test
	@TestDescription(desc = "Authenticate with appCode and userPassword but no userCode supplied"
			+ "<br>HTTP status of 400 should be returned."
			+ "<br><br>usercode: null<br>password: unrevokedPassword<br>appcode: appCode")
	@TestLabel(label = "Reference: 1006")
	public void test_6() {
		
		AuthenticateResponse user;

		user = loginUser(null, unrevokedPassword, appCode, HttpStatus.BAD_REQUEST);

		assertTrue("AuthenticateResponse was returned.", user == null);
	}
	
	@Test
	@TestDescription(desc = "Authenticate with userCode and appCode but no userPassword supplied"
			+ "<br>HTTP status of 400 should be returned."
			+ "<br><br>usercode: unrevokedUserCode<br>password: null<br>appcode: appCode")
	@TestLabel(label = "Reference: 1007")
	public void test_7() {
		
		AuthenticateResponse user;

		user = loginUser(unrevokedUserCode, null, appCode, HttpStatus.BAD_REQUEST);

		assertTrue("AuthenticateResponse was returned.", user == null);
	}
	
	@Test
	@TestDescription(desc = "Authenticate with wrong case user code parameter name supplied"
			+ "<br>HTTP status of 400 should be returned."
			+ "<br><br>usercode: unrevokedUserCode<br>password: unrevokedPassword<br>appcode: appCode")
	@TestLabel(label = "Reference: 1008")
	public void test_8() {

		AuthenticateResponse user;

		user = loginUser(unrevokedUserCode, unrevokedPassword, appCode,
				HttpStatus.BAD_REQUEST, "usercode", "userPassword", "appCode", null, null);

		assertTrue("Response was returned from the server.", user == null);
	}
	
	@Test
	@TestDescription(desc = "Authenticate with wrong case password parameter name supplied"
			+ "<br>HTTP status of 400 should be returned."
			+ "<br><br>usercode: unrevokedUserCode<br>password: unrevokedPassword<br>appcode: appCode")
	@TestLabel(label = "Reference: 1009")
	public void test_9() {

		AuthenticateResponse user;

		user = loginUser(unrevokedUserCode, unrevokedPassword, appCode,
				HttpStatus.BAD_REQUEST, "userCode", "userpassword", "appCode", null, null);

		assertTrue("Response was returned from the server.", user == null);
	}
	
	@Test
	@TestDescription(desc = "Authenticate with wrong case appcode parameter name supplied"
			+ "<br>HTTP status of 400 should be returned."
			+ "<br><br>usercode: unrevokedUserCode<br>password: unrevokedPassword<br>appcode: appCode")
	@TestLabel(label = "Reference: 1010")
	public void test_10() {

		AuthenticateResponse user;

		user = loginUser(unrevokedUserCode, unrevokedPassword, appCode,
				HttpStatus.BAD_REQUEST, "userCode", "userPassword", "appcode", null, null);

		assertTrue("Response was returned from the server.", user == null);
	}
	
	@Test
	@TestDescription(desc = "Authenticate valid user with invalid password."
			+ "<br><br>usercode: unrevokedUserCode<br>password: xcasfd<br>appcode: appCode")
	@TestLabel(label = "Reference: 1011")
	public void test_11() {

		AuthenticateResponse user;

		user = loginUser(unrevokedUserCode, invalidPassword, appCode, HttpStatus.OK);
		
		assertTrue("No AuthenticateResponse returned.", user != null);
		assertTrue("Login was completed successfully.", !user.isSuccess());
		assertTrue("User information was returned.", user.getUserCode() == null);

		ErrorMessage err = user.getError();
		assertTrue("Exception Expected.", err.getErrorCode() != null);

		log.debug("Exception returned: " + err.getErrorCode());
		
		assertTrue(
				"Unexpected exception returned! <br><br><b>ErrorCode:</b><br>"
						+ user.getError().getErrorCode()
						+ "<br><b>Detail:</b><br>"
						+ user.getError().getErrorDescription(), err
						.getErrorCode().equals(Utils.AUTHENTICATION_EXCEPTION));
	}

	@Test
	@TestDescription(desc = "Authenticate existing user who is revoked."
			+ "<br><br>usercode: revokedUserCode<br>password: revokedPassword<br>appcode: appCode")
	@TestLabel(label = "Reference: 1012")
	public void test_12() {

		AuthenticateResponse user;

		user = loginUser(revokedUserCode, revokedPassword,  appCode, HttpStatus.OK);

		assertTrue("No AuthenticateResponse returned.", user != null);
		assertTrue("Login was completed successfully.", !user.isSuccess());
		assertTrue("User information was returned.", user.getUserCode() == null);

		ErrorMessage err = user.getError();
		assertTrue("Exception Expected.", err.getErrorCode() != null);

		log.debug("Exception returned: " + err.getErrorCode());
		assertTrue(
				"Unexpected exception returned! <br><br><b>ErrorCode:</b><br>"
						+ user.getError().getErrorCode()
						+ "<br><b>Detail:</b><br>"
						+ user.getError().getErrorDescription(), err
						.getErrorCode().equals(Utils.USER_REVOKED));
	}

	@Test
	@TestDescription(desc = "Authenticate existing user whose password has expired."
			+ "<br><br>usercode: userExpiredCode<br>password: userExpiredPassword<br>appcode: appCode")
	@TestLabel(label = "Reference: 1013")
	public void test_13() {
		
		AuthenticateResponse user;

		user = loginUser(userExpiredCode, userExpiredPassword, appCode, HttpStatus.OK);

		assertTrue("No AuthenticateResponse returned.", user != null);
		assertTrue("Login unsuccessful.<br><br><b>ErrorCode:</b><br>"
				+ user.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ user.getError().getErrorDescription(), user.isSuccess());

		log.debug("UserCode returned: " + user.getUserCode());
		
		assertTrue("No user data returned.", user.getUserCode() != null);
		assertTrue("Usercodes do not match.",
				user.getUserCode().equals(userExpiredCode));
	}

	@Test
	@TestDescription(desc = "Authenticate user with appCode that doesnt exist in MobileAppAccess table.."
			+ "<br><br>usercode: unrevokedUserCode<br>password: unrevokedPassword<br>appcode: XXXX")
	@TestLabel(label = "Reference: 1014")
	public void test_14() {
		
		AuthenticateResponse user;

		user = loginUser(unrevokedUserCode, unrevokedPassword, "XXXX", HttpStatus.OK);

		assertTrue("No AuthenticateResponse returned.", user != null);
		
		assertTrue("Login was completed successfully.", !user.isSuccess());
		
		assertTrue("User information was returned.", user.getUserCode() == null);

		ErrorMessage err = user.getError();
		
		assertTrue("Exception Expected.", err.getErrorCode() != null);
		
		assertTrue(
				"Unexpected exception returned! <br><br><b>ErrorCode:</b><br>"
						+ user.getError().getErrorCode()
						+ "<br><b>Detail:</b><br>"
						+ user.getError().getErrorDescription(), err
						.getErrorCode().equals(Utils.APP_ACCESS_EXCEPTION));
	}
	
	@Test
	@TestDescription(desc = "Valid usercode and password combination."
			+ "<br><br>usercode: unrevokedUserCode1<br>password: unrevokedPassword1")
	@TestLabel(label = "Reference: 1015")
	public void test_15() {

		AuthenticateResponse user;

		user = loginUser(unrevokedUserCode, unrevokedPassword, appCode, HttpStatus.OK);
		
		assertTrue("No AuthenticateResponse returned.", user != null);
		assertTrue("Login unsuccessful.<br><br><b>ErrorCode:</b><br>"
				+ user.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ user.getError().getErrorDescription(), user.isSuccess());

		log.debug("UserCode returned: " + user.getUserCode());
		
		assertTrue("No user data returned.", user.getUserCode() != null);
		assertTrue("Usercodes do not match.",
				user.getUserCode().equals(unrevokedUserCode));

	}
	
	@Test
	@TestDescription(desc = "Validate authentication when userCode does not exist in primary provider."
			+ "<br><br>usercode: invalidUserCode<br>password: xcasf")
	@TestLabel(label = "Reference: 1016")
	public void test_16() {

		AuthenticateResponse user;

		user = loginUser(invalidUserCode, invalidPassword, appCode, HttpStatus.OK);

		assertTrue("No AuthenticateResponse returned.", user != null);
		assertTrue("Login was completed successfully.", !user.isSuccess());
		assertTrue("User information was returned.", user.getUserCode() == null);

		ErrorMessage err = user.getError();
		assertTrue("Exception Expected.", err.getErrorCode() != null);

		log.debug("Exception returned: " + err.getErrorCode());
		
		assertTrue(
				"Unexpected exception returned! <br><br><b>ErrorCode:</b><br>"
						+ user.getError().getErrorCode()
						+ "<br><b>Detail:</b><br>"
						+ user.getError().getErrorDescription(), err
						.getErrorCode().equals(Utils.AUTHENTICATION_EXCEPTION));

	}
	
	@Test
	@TestDescription(desc = "Validate authentication when userCode exists in primary provider "
			+ "by not in FR. <br/><br/>**NOTE test only valid for NON-Fieldreach Authentication "
			+ "providers"
			+ "<br><br>usercode: userNotInFieldreachCode<br>password: userNotInFieldreachPassword")
	@TestLabel(label = "Reference: 1017")
	public void test_17() {

		AuthenticateResponse user;

		user = loginUser(userNotInFieldreachCode,
				userNotInFieldreachPassword, appCode, HttpStatus.OK);

		assertTrue("No AuthenticateResponse returned.", user != null);
		assertTrue("Login was completed successfully.", !user.isSuccess());
		assertTrue("User information was returned.", user.getUserCode() == null);

		ErrorMessage err = user.getError();
		assertTrue("Exception Expected.", err.getErrorCode() != null);

		log.debug("Exception returned: " + err.getErrorCode());
		
		assertTrue(
				"Unexpected exception returned! <br><br><b>ErrorCode:</b><br>"
						+ user.getError().getErrorCode()
						+ "<br><b>Detail:</b><br>"
						+ user.getError().getErrorDescription(), err
						.getErrorCode().equals(Utils.AUTHENTICATION_EXCEPTION));
	}

	@Test
	@TestDescription(desc = "Authenticate with valid user/password but encoded in FR algorithm."
			+ "<br><br>usercode: unrevokedUserCode2<br>password: unrevokedPassword2")
	@TestLabel(label = "Reference: 1018")
	public void test_18() {

		AuthenticateResponse user;

		user = loginUser(unrevokedUserCode, unrevokedPassword, appCode,
				HttpStatus.BAD_REQUEST, "userCode", "userPassword", "appCode", baseURL,
				"FieldReach");

		assertTrue("No AuthenticateResponse returned.", user != null);
		assertTrue("Login was completed successfully.", !user.isSuccess());
		assertTrue("User information was returned.", user.getUserCode() == null);

		ErrorMessage err = user.getError();
		assertTrue("Exception Expected.", err.getErrorCode() != null);

		log.debug("Exception returned: " + err.getErrorCode());
		
		assertTrue(
				"Unexpected exception returned! <br><br><b>ErrorCode:</b><br>"
						+ user.getError().getErrorCode()
						+ "<br><b>Detail:</b><br>"
						+ user.getError().getErrorDescription(), err
						.getErrorCode().equals(Utils.AUTHENTICATION_EXCEPTION));
	}
	
	@Test
	@TestDescription(desc = "Authenticate with valid user/password but AES encoded with wrong key."
			+ "<br><br>usercode: unrevokedUserCode<br>password: unrevokedPassword")
	@TestLabel(label = "Reference: 1019")
	public void test_19() {

		AuthenticateResponse user;

		user = loginUser(unrevokedUserCode, unrevokedPassword, appCode,
				HttpStatus.BAD_REQUEST, "userCode", "userPassword", "appCode", baseURL,
				"AES_WRONGKEY");

		assertTrue("No AuthenticateResponse returned.", user != null);
		assertTrue("Login was completed successfully.", !user.isSuccess());
		assertTrue("User information was returned.", user.getUserCode() == null);

		ErrorMessage err = user.getError();
		assertTrue("Exception Expected.", err.getErrorCode() != null);

		log.debug("Exception returned: " + err.getErrorCode());
		
		assertTrue(
				"Unexpected exception returned! <br><br><b>ErrorCode:</b><br>"
						+ user.getError().getErrorCode()
						+ "<br><b>Detail:</b><br>"
						+ user.getError().getErrorDescription(), err
						.getErrorCode().equals(Utils.AUTHENTICATION_EXCEPTION));
	}

	@Test
	@TestDescription(desc = "Valid usercode and password combination for primary "
			+ " where secondary configured"
			+ "<br><br>usercode: unrevokedUserCode1<br>password: unrevokedPassword1")
	@TestLabel(label = "Reference: 1020")
	public void test_20() {

		AuthenticateResponse user;

		user = loginUser(unrevokedUserCode, unrevokedPassword, appCode, HttpStatus.OK);
		
		assertTrue("No AuthenticateResponse returned.", user != null);
		assertTrue("Login unsuccessful.<br><br><b>ErrorCode:</b><br>"
				+ user.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ user.getError().getErrorDescription(), user.isSuccess());

		log.debug("UserCode returned: " + user.getUserCode());
		
		assertTrue("No user data returned.", user.getUserCode() != null);
		assertTrue("Usercodes do not match.",
				user.getUserCode().equals(unrevokedUserCode));
	}

	@Test
	@TestDescription(desc = "Valid usercode and password combination for secondary,"
			+ " where password wrong for primary"
			+ "<br><br>usercode: userInPrimaryAndSecondaryCode<br>password: userInPrimaryAndSecondaryPassword")
	@TestLabel(label = "Reference: 1021")
	public void test_21() {

		AuthenticateResponse user;

		user = loginUser(userInPrimaryAndSecondaryCode,
				userInPrimaryAndSecondaryPassword, appCode, HttpStatus.OK);

		assertTrue("No AuthenticateResponse returned.", user != null);
		assertTrue("Login was completed successfully.", !user.isSuccess());
		assertTrue("User information was returned.", user.getUserCode() == null);

		ErrorMessage err = user.getError();
		assertTrue("Exception Expected.", err.getErrorCode() != null);

		log.debug("Exception returned: " + err.getErrorCode());
		
		assertTrue(
				"Unexpected exception returned! <br><br><b>ErrorCode:</b><br>"
						+ user.getError().getErrorCode()
						+ "<br><b>Detail:</b><br>"
						+ user.getError().getErrorDescription(), err
						.getErrorCode().equals(Utils.AUTHENTICATION_EXCEPTION));
	}

	@Test
	@TestDescription(desc = "Valid usercode and password combination for secondary, where user "
			+ "does not exist in primary"
			+ "<br><br>usercode: userNotInPrimaryCode<br>password: userNotInPrimaryPassword")
	@TestLabel(label = "Reference: 1022")
	public void test_22() {

		AuthenticateResponse user;

		user = loginUser(userNotInPrimaryCode, userNotInPrimaryPassword, appCode,
				HttpStatus.OK);

		assertTrue("No AuthenticateResponse returned.", user != null);
		assertTrue("Login unsuccessful.<br><br><b>ErrorCode:</b><br>"
				+ user.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ user.getError().getErrorDescription(), user.isSuccess());

		log.debug("UserCode returned: " + user.getUserCode());
		
		assertTrue("No user data returned.", user.getUserCode() != null);
		assertTrue("Usercodes do not match.",
				user.getUserCode().equals(userNotInPrimaryCode));
	}
	*/
}
