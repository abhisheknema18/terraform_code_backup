package com.amtsybex.fieldreach;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.jboss.weld.environment.servlet.EnhancedListener;
import org.springframework.boot.web.servlet.ServletContextInitializer;

public class WeldServletContextInitializer implements ServletContextInitializer {

	private ServletContainerInitializer servletContainerInitializer;

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException
	{
		ServletContainerInitializer servletContainerInitializer = getServletContainerInitializer();
		servletContainerInitializer.onStartup(null, servletContext);
	}

	private ServletContainerInitializer getServletContainerInitializer()
	{
		if (servletContainerInitializer == null)
			servletContainerInitializer = (ServletContainerInitializer) new EnhancedListener();

		return servletContainerInitializer;
	}
}
