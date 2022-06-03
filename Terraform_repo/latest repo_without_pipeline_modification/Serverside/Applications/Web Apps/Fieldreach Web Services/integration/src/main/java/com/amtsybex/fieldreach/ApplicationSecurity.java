package com.amtsybex.fieldreach;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class ApplicationSecurity extends WebSecurityConfigurerAdapter {

	@Autowired
	private AccessTokenFilter accessTokenFilter;
	

	private static final String[] AUTH_WHITELIST = {
			"/swagger-resources/**",
			"/swagger-ui/**",
			"/v2/api-docs",
			"/webjars/**",
			"/actuator/**"
	};
	

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http = http.authorizeRequests()
			
				.antMatchers(AUTH_WHITELIST).permitAll()
				.anyRequest()
				.authenticated()
				.and()
				.csrf().disable()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.addFilterBefore(accessTokenFilter, UsernamePasswordAuthenticationFilter.class);
		
	}

}
