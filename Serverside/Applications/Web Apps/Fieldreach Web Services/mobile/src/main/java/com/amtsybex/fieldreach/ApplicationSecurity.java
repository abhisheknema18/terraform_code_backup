package com.amtsybex.fieldreach;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Opaquetoken;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import com.microsoft.sqlserver.jdbc.StringUtils;

@EnableWebSecurity
@Configuration
public class ApplicationSecurity extends WebSecurityConfigurerAdapter {

	@Value("${auth.audience}")
	private String audience;

	@Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
	private String issuer;
	
	private boolean isOpaque;

	private static final String[] AUTH_WHITELIST = {
			"/swagger-resources/**",
			"/swagger-ui/**",
			"/v2/api-docs",
			"/webjars/**",
			"/actuator/**"
	};
	
	public ApplicationSecurity(OAuth2ResourceServerProperties resourceServerProps) {

		if(!StringUtils.isEmpty(resourceServerProps.getJwt().getIssuerUri())) {
			this.issuer = resourceServerProps.getJwt().getIssuerUri();
		}else {
			this.issuer = resourceServerProps.getJwt().getJwkSetUri();
		}

		Opaquetoken opaqueTokenProps = resourceServerProps.getOpaquetoken();

		if(!StringUtils.isEmpty(opaqueTokenProps.getIntrospectionUri())) {
			this.isOpaque = true;
		}else {
			this.isOpaque = false;
		}
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		OAuth2ResourceServerConfigurer<HttpSecurity> config = http.authorizeRequests()
				.antMatchers(AUTH_WHITELIST).permitAll()
				.anyRequest()
				.authenticated()
				.and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.oauth2ResourceServer();

		if(this.isOpaque) {
			config.opaqueToken();
		}else{
			config.jwt().decoder(jwtDecoder());
		}

	}
	
	public JwtDecoder jwtDecoder() {

		OAuth2TokenValidator<Jwt> withAudience = new ApplicationAudienceValidator(audience);
		OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuer);
		OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(withAudience, withIssuer);

		NimbusJwtDecoder jwtDecoder = (NimbusJwtDecoder) JwtDecoders.fromOidcIssuerLocation(issuer);
		jwtDecoder.setJwtValidator(validator);
		return jwtDecoder;
	}

}
