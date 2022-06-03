package com.amtsybex.fieldreach;

import java.util.Set;

import javax.enterprise.inject.spi.CDI;

import org.jboss.weld.bean.builtin.BeanManagerProxy;
import org.jboss.weld.manager.BeanManagerImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication

@ComponentScan(basePackages = {"com.amtsybex.fieldreach.backend", "com.amtsybex.fieldreach.security","com.amtsybex.fieldreach.fdm.property","com.amtsybex.fieldreach.utils.impl", "com.amtsybex.fieldreach.fdm.util"}, 
excludeFilters={ 
		@ComponentScan.Filter(type=FilterType.ANNOTATION, value=javax.inject.Named.class), 
		@ComponentScan.Filter(type=FilterType.ANNOTATION, value=javax.inject.Inject.class), 
		@ComponentScan.Filter(type=FilterType.ANNOTATION, value=org.apache.deltaspike.core.api.scope.WindowScoped.class)
		})

@EnableAutoConfiguration(exclude = { HibernateJpaAutoConfiguration.class }) 

@ImportResource({ "classpath:applicationContext-db.xml"
	,"classpath:applicationContext-fdmPortal.xml"
	,"classpath:applicationContext-extractEngine.xml"})
public class FdmwebportalApplication {
	
	
	public static void main(String[] args) {
		SpringApplication.run(FdmwebportalApplication.class, args);   
	}
	
	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {

			//System.out.println("Let's inspect the beans provided by Spring Boot:");		
			
			BeanManagerImpl b = BeanManagerProxy.unwrap(CDI.current().getBeanManager());
			Set<javax.enterprise.inject.spi.Bean<?>> bs = b.getAccessibleBeans();
		
		/*	for(javax.enterprise.inject.spi.Bean bn: bs) {
				
				if(bn.getBeanClass().toString().contains("fieldreach")) {
					System.out.println(bn.getScope());
					System.out.println(bn.getBeanClass());
					System.out.println(bn.getClass());
					System.out.println("----");
				}

			}

			System.out.println("Approval status : "+propertyTest.getResultApprovalStatuses());*/

		};
	}
	
	@Bean
	public ServletContextInitializer weldServletContextInitializer()
	{
		return new WeldServletContextInitializer();
	}

	
}
