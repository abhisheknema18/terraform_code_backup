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
import springfox.documentation.builders.AuthorizationCodeGrantBuilder;
import springfox.documentation.builders.OAuthBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.GrantType;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Profile("swagger")
public class SwaggerConfig {

	@Autowired
	TypeResolver typeResolver;
	
    public static final String BEARER_KEY = "Bearer";
    public static final String OAUTH_KEY = "Fusion-OAuth";
    private static final String CLIENT_ID = "";
    private static final String CLIENT_SECRET = "";
    
    @Value("${info.app.version?:'DEV'}") 
    private String versionNo;
    
    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String AUTH_SERVER;
    
    @Bean

    public Docket api() {

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .additionalModels(typeResolver.resolve(ValidationTypeList.class))
                .additionalModels(typeResolver.resolve(ValidationTypeRequest.class))
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.amtsybex.fieldreach.services.endpoint.rest"))
                .paths(PathSelectors.any())
                .build()
                .useDefaultResponseMessages(false)
                .globalResponses(HttpMethod.GET , Collections.singletonList(new ResponseBuilder().code("401").description("Unauthorised - user is not authorised to access the service").build()))
                .globalResponses(HttpMethod.PUT , Collections.singletonList(new ResponseBuilder().code("401").description("Unauthorised - user is not authorised to access the service").build()))
                .globalResponses(HttpMethod.POST , Collections.singletonList(new ResponseBuilder().code("401").description("Unauthorised - user is not authorised to access the service").build()))
                .globalResponses(HttpMethod.DELETE , Collections.singletonList(new ResponseBuilder().code("401").description("Unauthorised - user is not authorised to access the service").build()))
                .tags(new Tag("Info", "Services to Supply information about the container, including time and application version", 0),
						new Tag("User & Config", "Services to allow the download of Application Configuration and User Info", 1),
						new Tag("Package Sync", "Services to allow the download of packages", 2),
						new Tag("Work Issued", "Services to return lists of available work orders and enable work order download", 3),
						new Tag("Script/Results", "Services to allow the download of scripts and results as well as the upload of results", 4),
						new Tag("Support File Download", "Services to allow the download of application support files", 5),
						new Tag("File Upload", "Services to allow the upload of files, including results and transactions", 6),
						new Tag("Asset Database", "Services to allow the download of Asset Databases", 7),
						new Tag("Result History Database/Delta", "Services to allow the download of result history information", 8),
						new Tag("Work Bank Database", "Services to allow the download of work bank databses", 9))  
                .securitySchemes(Arrays.asList(oauthSecurityScheme(), bearerSecurityScheme()))
                .securityContexts(Collections.singletonList(securityContext()));
    }

    @Bean
    public SecurityConfiguration security() {
        return SecurityConfigurationBuilder.builder()
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .scopeSeparator(" ")
                .useBasicAuthenticationWithAccessCodeGrant(false)
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("FieldSmart Web Services")
            .description("The FieldSmart Mobile Web Services API")
            .license("FieldSmart Commercial License")
            .licenseUrl("http://www.amt-sybex.com")
            .termsOfServiceUrl("http://www.amt-sybex.com")
            .version(versionNo)
            .contact(new Contact("","", "info@amt-sybex.com"))
            .build();
    }

    private SecurityScheme oauthSecurityScheme() {
        GrantType grantType = new AuthorizationCodeGrantBuilder()
                .tokenEndpoint(x -> x.tokenName("oauthtoken").url(AUTH_SERVER + "oauth/token").build())
                .tokenRequestEndpoint(x -> x.url(AUTH_SERVER + "authorize").clientIdName(CLIENT_ID).clientSecretName(CLIENT_SECRET).build())
                .build();

        return new OAuthBuilder().name(OAUTH_KEY)
                .grantTypes(Collections.singletonList(grantType))
                .scopes(Arrays.asList(scopes()))
                .build();
    }

    private ApiKey bearerSecurityScheme() {
        return new ApiKey(BEARER_KEY, "Authorization", "header");
    }

    private AuthorizationScope[] scopes() {
        return new AuthorizationScope[]{
                new AuthorizationScope("openid", "for openid")};
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(
                        Arrays.asList(new SecurityReference(OAUTH_KEY, scopes()), 
                                      new SecurityReference(BEARER_KEY, scopes())))
                .build();
    }
}
