package com.amtsybex.fieldreach;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;

import com.amtsybex.fieldreach.swagger.model.ValidationTypeList;
import com.amtsybex.fieldreach.swagger.model.ValidationTypeRequest;
import com.fasterxml.classmate.TypeResolver;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Profile("swagger")
public class SwaggerConfig {
	
	@Autowired
	TypeResolver typeResolver;

    public static final String BEARER_KEY = "Bearer";
    
    @Value("${info.app.version?:'DEV'}") 
    private String versionNo;
    
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .useDefaultResponseMessages(false)
                .additionalModels(typeResolver.resolve(ValidationTypeList.class))
                .additionalModels(typeResolver.resolve(ValidationTypeRequest.class))
                .globalResponses(HttpMethod.GET , Collections.singletonList(new ResponseBuilder().code("401").description("Unauthorised - user is not authorised to access the service").build()))
                .globalResponses(HttpMethod.PUT , Collections.singletonList(new ResponseBuilder().code("401").description("Unauthorised - user is not authorised to access the service").build()))
                .globalResponses(HttpMethod.POST , Collections.singletonList(new ResponseBuilder().code("401").description("Unauthorised - user is not authorised to access the service").build()))
                .globalResponses(HttpMethod.DELETE , Collections.singletonList(new ResponseBuilder().code("401").description("Unauthorised - user is not authorised to access the service").build()))
                .tags(new Tag("Info Services", "Services to Supply information about the containers, including time and application version", 0),
						new Tag("Config Services", "Services to allow the download of Application Configuration", 1),
						new Tag("System Event Integration", "Services to allow third party systems to log exceptions in FieldSmart",2),
						new Tag("Work Order Integration", "Services to return lists of available work orders and enable work order download", 3),
						new Tag("Script/Results", "Services to allow the download of scripts and results as well as the upload of results", 4),
                		new Tag("File Upload", "Services to allow the upload of files, including results and transactions", 5))
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.amtsybex.fieldreach.services.endpoint.rest"))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(Arrays.asList(bearerSecurityScheme()))
                .securityContexts(Collections.singletonList(securityContext()));
    }


    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("FieldSmart Web Services")
            .description("The FieldSmart Integration Web Services API")
            .license("FieldSmart Commercial License")
            .licenseUrl("http://www.amt-sybex.com")
            .termsOfServiceUrl("http://www.amt-sybex.com")
            .version(versionNo)
            .contact(new Contact("","", "info@amt-sybex.com"))
            .build();
    }


    private ApiKey bearerSecurityScheme() {
        return new ApiKey(BEARER_KEY, "Authorization", "header");
    }

    private AuthorizationScope[] scopes() {
        return new AuthorizationScope[]{
                new AuthorizationScope("iws", "integration webservices")};
    }
    
    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(
                        Arrays.asList(new SecurityReference(BEARER_KEY, scopes())))
                .build();
    }
}
