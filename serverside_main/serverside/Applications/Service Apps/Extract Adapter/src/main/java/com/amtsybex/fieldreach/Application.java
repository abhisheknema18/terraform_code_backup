package com.amtsybex.fieldreach;

import com.amtsybex.fieldreach.exception.InitialisationException;
import com.amtsybex.fieldreach.exception.InstanceStartedException;
import com.amtsybex.fieldreach.extract.core.impl.ExtractAdapterCoreImpl;
import com.amtsybex.fieldreach.interfaces.ServiceApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;

import java.util.Arrays;

@SpringBootApplication
@ImportResource({"applicationContext-fea.xml"})
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class.getName());

    @Autowired()
    private ServiceApplication extractAdapterService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        startExtractAdapter(ctx);
        return args -> {

            log.debug("Let's inspect the beans provided by Spring Boot:");

            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                log.debug(beanName);
            }

            log.info("Extract Adapter - Spring Boot App initialized");
        };
    }

    private void startExtractAdapter(ApplicationContext ctx) {
        try {
            extractAdapterService.setContext(ctx);
            extractAdapterService.start();
        } catch (InitialisationException | InstanceStartedException e) {
            log.error("Unable to auto start Extract Adapter: " + e.getMessage());
        }
    }
}
