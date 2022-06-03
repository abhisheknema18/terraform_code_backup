package com.amtsybex.fieldreach.fdm.property;

import java.io.Serializable;

import org.apache.deltaspike.core.api.exclude.Exclude;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Exclude // exclude from deltaspike scan!!
@Component
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
/**
 * This is just a test class for externalizing properties that will replace the
 * PropertyUtil class
 * 
 * to use in a jsf class there is a producer and we can just use the @ Inject
 * annotation
 * 
 * @author P10245651
 *
 */
public class PortalPropertyUtil implements Serializable {

	private static final long serialVersionUID = -5980759472442673779L;

	@Bean
	@ConfigurationProperties(prefix = "portal")
	public PortalProperties props() {
		return new PortalProperties();
	}
	
	@Bean
	@ConfigurationProperties(prefix = "server")
	public ServerProperties server() {
		return new ServerProperties();
	}

}
