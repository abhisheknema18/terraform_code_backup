package com.amtsybex.fieldreach;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.model.AccessToken;
import com.amtsybex.fieldreach.backend.model.SystemUsers;
import com.amtsybex.fieldreach.backend.service.AccessTokenService;
import com.amtsybex.fieldreach.backend.service.UserService;
import com.amtsybex.fieldreach.exception.ConfigException;
import com.amtsybex.fieldreach.utils.AccessTokenAuthor;
import com.amtsybex.fieldreach.utils.AccessTokenAuthor.AUTHORITY;
import com.amtsybex.fieldreach.utils.AccessTokenAuthor.EXPIRY;
import com.amtsybex.fieldreach.utils.impl.AccessTokenAuthorImpl;
import com.amtsybex.fieldreach.utils.impl.Common;
import com.nimbusds.jose.JOSEException;

public class BaseTest {

    protected static RSAPrivateKey privateKey;
    protected static RSAPublicKey publicKey;

	@Autowired
	protected WebApplicationContext context;
	
	@Autowired
	protected AccessTokenAuthor tokenAuthor;
    
    @Mock
    protected AccessTokenService accessTokenService;
    
    @Mock
    protected UserService userService;
    
	@InjectMocks
	@Autowired
	protected AccessTokenFilter accessTokenFilter;
	
    @BeforeAll
    public static void init() throws NoSuchAlgorithmException {
    	
		if(privateKey == null) {
			//no public private key so create new ones
			KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
			generator.initialize(2048);
			KeyPair pair = generator.generateKeyPair();
			
			privateKey = (RSAPrivateKey) pair.getPrivate();
			publicKey = (RSAPublicKey) pair.getPublic();

		}
    }
    
    @BeforeEach
    public void setUp() {
		((AccessTokenAuthorImpl)tokenAuthor).setPrivateKey(privateKey);
		((AccessTokenAuthorImpl)tokenAuthor).setPublicKey(publicKey);
    }
    
    protected String arrangeSuccessfulAuthentication() throws FRInstanceException {
    	
    	AccessToken tokenObj = this.getAccessTokenDBObj();
    	String token = this.getAuthorizationToken(tokenObj);
    	tokenObj.setChecksum(Common.generateSHA512Checksum(token.getBytes()));

    	when(userService.getSystemUser(any(), any())).thenReturn(getSystemUser());
    	when(accessTokenService.searchAccessTokenByUuid(null, tokenObj.getId())).thenReturn(tokenObj);
    	
    	return token;
    }
    
    protected AccessToken getAccessTokenDBObj() {
    	
    	Date now = new Date();
		Date expiry = EXPIRY.MONTH_1.generateExpiry();
		
    	AccessToken tokenObj = new AccessToken();
    	
    	tokenObj.setId(UUID.randomUUID().toString());
		tokenObj.setCreateDate(Common.generateFieldreachDBDate(now));
		tokenObj.setCreateTime(Common.generateFieldreachDBTime());

		tokenObj.setCreateUser("FDCSADM");
		tokenObj.setExpiryDate(Common.generateFieldreachDBDate(expiry));
		tokenObj.setLinkedUserCode("FDCSADM");
		tokenObj.setName("TEST TOKEN");
		tokenObj.setRevoked(0);
		tokenObj.setTokenNotes("SOME EXTRA INFO ON TOKEN");
		
		return tokenObj;
    }
    
    protected String getAuthorizationToken(AccessToken tokenObj) {
    	
		String tokenString = null;
		
		try {
			tokenString = tokenAuthor.generateJWT(tokenObj.getId(), tokenObj.getLinkedUserCode(), Common.convertFieldreachDate(tokenObj.getExpiryDate()), Collections.singletonList(AUTHORITY.IWS));
			tokenObj.setChecksum(Common.generateMD5Checksum(tokenString.getBytes()));
		} catch (JOSEException | ConfigException e) {
			e.printStackTrace();
		}

    	return tokenString;
    }
    

    
    protected SystemUsers getSystemUser() {
    	return new SystemUsers("FRADM", "FRADM", null, null, null, 0, null, null, null, null, null, null);
    }
}
