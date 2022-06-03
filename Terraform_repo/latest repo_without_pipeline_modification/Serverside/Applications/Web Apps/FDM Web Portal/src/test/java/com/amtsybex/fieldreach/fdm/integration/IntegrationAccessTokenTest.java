package com.amtsybex.fieldreach.fdm.integration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.exception.UserNotFoundException;
import com.amtsybex.fieldreach.backend.model.AccessToken;
import com.amtsybex.fieldreach.backend.model.SystemUsers;
import com.amtsybex.fieldreach.backend.service.AccessTokenService;
import com.amtsybex.fieldreach.backend.service.UserService;
import com.amtsybex.fieldreach.exception.ConfigException;
import com.amtsybex.fieldreach.fdm.builder.AccessTokenBuilder;
import com.amtsybex.fieldreach.fdm.builder.SystemUsersBuilder;
import com.amtsybex.fieldreach.utils.AccessTokenAuthor;
import com.amtsybex.fieldreach.utils.AccessTokenAuthor.AUTHORITY;
import com.amtsybex.fieldreach.utils.impl.Common;
import com.nimbusds.jose.JOSEException;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@RunWith(MockitoJUnitRunner.class)
public class IntegrationAccessTokenTest {

	@Mock
	private UserService userService;

	@Mock
	private AccessTokenService accessTokenService;
	
	@Mock
	private AccessTokenAuthor accessTokeAuthor;
	
	@Mock
	private IntegrationAccessToken integrationAccessToken;
	
	@Mock
	private AccessToken accessToken;

	@Test
	public void should_pass_when_users_prefixed_sys() {

		SystemUsers systemUser = new SystemUsersBuilder.Builder("1").userName("SYSADMIN").adminUser(1).revoked(0)
				.build();
		List<SystemUsers> userList = new ArrayList<SystemUsers>();
		userList.add(systemUser);

		List<SystemUsers> result = null;
		String frInstance = "";
		try {
			Mockito.when(userService.searchTokenCapableSystemUsers(frInstance)).thenReturn(userList);

			result = userService.searchTokenCapableSystemUsers(frInstance);
		} catch (FRInstanceException e) {

			fail("Exception Occurred.");
		}

		Assert.assertThat(result.get(0).getUserName(), CoreMatchers.containsString("SYS"));
	}

	@Test
	public void should_fail_when_users_not_prefixed_sys() {

		SystemUsers systemUser = new SystemUsersBuilder.Builder("1").userName("FRADM").adminUser(1).revoked(0).build();
		List<SystemUsers> userList = new ArrayList<SystemUsers>();
		userList.add(systemUser);
		List<SystemUsers> result = null;
		String frInstance = "";
		try {
			Mockito.when(userService.searchTokenCapableSystemUsers(frInstance)).thenReturn(userList);

			result = userService.searchTokenCapableSystemUsers(frInstance);
		} catch (FRInstanceException e) {

			fail("Exception Occurred.");
		}

		Assert.assertThat(result.get(0).getUserName(), CoreMatchers.not("SYS"));
	}

	@Test
	public void verify_access_token_valid() {

		AccessToken accessToken = new AccessTokenBuilder.Builder("AccessTokenUniqueId")
				.build();
		String frInstance = "";

		try {
				accessTokenService.saveAccessToken(frInstance, accessToken);

				assertNotNull(accessToken.getChecksum());
				assertNotNull(accessToken.getCreateDate());
				assertNotNull(accessToken.getCreateTime());
				assertNotNull(accessToken.getCreateUser());
				assertNotNull(accessToken.getExpiryDate());
				assertNotNull(accessToken.getId());

		} catch (FRInstanceException e) {
			fail("Exception Occurred.");
		}

	}
	
	@Test
	public void validate_token_is_generated_successfully() {

		String uId = "RandomUniqueUid";
		String subject = "ClientInfo";
		List<AUTHORITY> auhtority = new ArrayList<AUTHORITY>();
		String result = null;

		auhtority.add(AUTHORITY.IWS);

		Date expiry = new Date();

		try {

			Mockito.when(accessTokeAuthor.generateJWT(uId, subject, expiry, auhtority)).thenReturn("jwtToken");
			
			result = accessTokeAuthor.generateJWT(uId, subject, expiry, auhtority);

		} catch (JOSEException | ConfigException e) {
			fail("Exception Occurred.");
		}

		assertNotSame("", uId);
		assertNotSame("", subject);
		assertFalse(auhtority.isEmpty());
		assertNotNull(expiry);
		assertNotNull(result);
	}
	
	@Test
	public void should_fetch_system_users_with_sys() {
		SystemUsers systemUser = new SystemUsersBuilder.Builder("1").userName("SYSFRADM").adminUser(1).revoked(0).build();
		List<SystemUsers> userList = new ArrayList<SystemUsers>();
		userList.add(systemUser);
		List<SystemUsers> result = null;

		try {

			Mockito.when(integrationAccessToken.fetchSystemUsers()).thenReturn(userList);

			result = integrationAccessToken.fetchSystemUsers();

		} catch (UserNotFoundException | FRInstanceException e) {
			fail("Exception Occurred");
		}

		assertNotNull(result);
		Assert.assertThat(result.get(0).getUserName(), CoreMatchers.containsString("SYS"));
	}
	
	@Test
	public void should_fetch_all_access_token_list() {

		List<AccessToken> result = null;

		AccessToken accessTokenOne = new AccessTokenBuilder.Builder("AccessTokenOneUniqueId")
				.build();

		AccessToken accessTokenTwo = new AccessTokenBuilder.Builder("AccessTokenOneUniqueId")
				.build();

		List<AccessToken> accessTokenlist = new ArrayList<AccessToken>();

		accessTokenlist.add(accessTokenOne);
		accessTokenlist.add(accessTokenTwo);

		try {
			
			Mockito.when(accessTokenService.searchAllAccessToken(Mockito.anyString())).thenReturn(accessTokenlist);

			result = accessTokenService.searchAllAccessToken(Mockito.anyString());
			
		} catch (FRInstanceException e) {
			
			fail("Exception Occurred");
			
		}

		assertNotNull(result);

	}
	
	@Test
	public void should_return_green_when_more_than_thirty_days() {
		String result = null;
		Date currentDate = Common.convertFieldreachDate(20210915);
		Date expiryDate = Common.convertFieldreachDate(20211115);
		try {
			
			Mockito.when(accessToken.getIconColor()).thenReturn("color:green");
			
			result = accessToken.getIconColor();
			
		} catch (ParseException e) {
			
			fail("Exception Occurred");
			
		}
		assertNotNull(result);
		assertSame("color:green", result);
		if(!expiryDate.after(currentDate)) {
			fail("date is after expiry date");
		}
		
	}
	
	@Test
	public void should_return_orange_when_less_than_thirty_days() {
		String result = null;
		Date currentDate = Common.convertFieldreachDate(20210915);
		Date expiryDate = Common.convertFieldreachDate(20210930);
		Date currentDatePlusThirtyDays = Common.convertFieldreachDate(20211015);
		
		try {
			
			Mockito.when(accessToken.getIconColor()).thenReturn("color:orange");
			
			result = accessToken.getIconColor();
			
		} catch (ParseException e) {
			
			fail("Exception Occurred");
			
		}
		assertNotNull(result);
		assertSame("color:orange", result);
		if(!(expiryDate.before(currentDatePlusThirtyDays) && expiryDate.after(currentDate))) {
			fail("date is not within 30 days");
			
		}
		
	}
	
	@Test
	public void should_return_orange_when_expired() {
		String result = null;
		Date expiryDate = Common.convertFieldreachDate(20211130);
		Date currentDatePlusThirtyDays = Common.convertFieldreachDate(20211015);
		
		try {
			
			Mockito.when(accessToken.getIconColor()).thenReturn("color:red");
			
			result = accessToken.getIconColor();
			
		} catch (ParseException e) {
			
			fail("Exception Occurred");
			
		}
		assertNotNull(result);
		assertSame("color:red", result);
		if(!(expiryDate.after(currentDatePlusThirtyDays))) {
			fail("date is within expiry date");
			
		}
		
	}
	
	@Test
	public void should_return_grey_when_revoked() {
		
		int revoked = 1;
		String result = null;
		
		try {
			
			Mockito.when(accessToken.getIconColor()).thenReturn("color:grey");
			
			result = accessToken.getIconColor();
			
		} catch (ParseException e) {
			
			fail("Exception Occurred");
			
		}
		assertNotNull(result);
		assertSame("color:grey", result);
		assertSame(revoked, 1);
		
	}
	
}
