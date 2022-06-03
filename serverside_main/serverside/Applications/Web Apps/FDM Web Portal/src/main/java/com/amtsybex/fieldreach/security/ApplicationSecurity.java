package com.amtsybex.fieldreach.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity(debug = false)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurity extends WebSecurityConfigurerAdapter implements WebMvcConfigurer{
	
	@Autowired
	FDMAuthenticationFailureHandler fdmAuthenticationFailureHandler;

	private final LogoutHandler logoutHandler;
	private final ApplicationAuthenticationSuccessHandler applicationAuthenticationSuccessHandler;

	public ApplicationSecurity(LogoutHandler logoutHandler, ApplicationAuthenticationSuccessHandler applicationAuthenticationSuccessHandler) {
		this.logoutHandler = logoutHandler;
		this.applicationAuthenticationSuccessHandler = applicationAuthenticationSuccessHandler;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.cors().disable().csrf().disable().authorizeRequests()
		
			.antMatchers(HttpMethod.OPTIONS, "**").permitAll()
			.antMatchers("/stylesheet/**").permitAll()
			.antMatchers("/images/**").permitAll()
			.antMatchers("/scripts/**").permitAll()
			.antMatchers("/highcharts/**").permitAll()	
			.antMatchers("javax.faces.resource**").permitAll()
			.antMatchers("/actuator/**").permitAll()
			.antMatchers("/actuator/**").permitAll()
			.antMatchers("/fdm/home.xhtml").hasRole("USER")
            .antMatchers("/fdm/search.xhtml").hasRole("USER")
            .antMatchers("/fdm/details.xhtml").hasRole("USER")
            .antMatchers("/fdm/detailsMultiView.xhtml").hasRole("USER")
            .antMatchers("/fdm/dashboard.xhtml").hasRole("DASHBOARD_VIEW")
            .antMatchers("/fdm/usermap.xhtml").hasRole("USER_OFF")
            .antMatchers("/fdm/worklist.xhtml").hasRole("WORK_VIEW")
            .antMatchers("/fdm/workdetail.xhtml").hasRole("WORK_VIEW")
            .antMatchers("/fdm/userlist.xhtml").hasRole("ADMIN")
            .antMatchers("/fdm/assetlist.xhtml").hasRole("USER_OFF")
            .antMatchers("/fdm/resultAnalysis.xhtml").hasRole("USER")
            .antMatchers("/fdm/userMonitor.xhtml").hasRole("ADMIN")
            .antMatchers("/fdm/userAdminSearchList.xhtml").hasRole("USER")
            .antMatchers("/fdm/mobileWorkGroupSearchList.xhtml").hasRole("USER")
            .antMatchers("/fdm/systemUserAdminSearchList.xhtml").hasRole("ADMIN")
            .antMatchers("/fdm/alertManagerList.xhtml").hasRole("ADMIN")
            .antMatchers("/fdm/accessTokenList.xhtml").hasRole("ACCESS_TOKEN_VIEW")
			.anyRequest().authenticated()
	
			.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
			.and().oauth2Login().failureHandler(fdmAuthenticationFailureHandler).successHandler(applicationAuthenticationSuccessHandler)
			.and().logout().logoutUrl("/logout.xhtml").logoutSuccessUrl("/fdm/logout.xhtml").invalidateHttpSession(true);
			//.and().logout().addLogoutHandler(logoutHandler).logoutUrl("/logout.xhtml").logoutSuccessUrl("/fdm/home.xhtml").invalidateHttpSession(true);

	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedMethods("*");
	}

}
