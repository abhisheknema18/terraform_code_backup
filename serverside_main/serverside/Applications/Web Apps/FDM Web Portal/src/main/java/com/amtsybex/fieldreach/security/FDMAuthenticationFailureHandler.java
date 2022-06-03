package com.amtsybex.fieldreach.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class FDMAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		
		if (exception instanceof OAuth2AuthenticationException && ((OAuth2AuthenticationException) exception).getError()
				.getErrorCode().equalsIgnoreCase("authorization_request_not_found")) {

			//Fix if session timeout occurs on the login page
			this.getRedirectStrategy().sendRedirect(request, response, "/fdm/home.xhtml");
		} else {
			super.onAuthenticationFailure(request, response, exception);
		}

	}

}
