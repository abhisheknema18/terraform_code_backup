package com.amtsybex.fieldreach;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.model.HPCUsers;
import com.amtsybex.fieldreach.backend.service.UserService;
import com.amtsybex.fieldreach.services.endpoint.common.UserController;
import com.amtsybex.fieldreach.services.endpoint.rest.BaseController;
import com.amtsybex.fieldreach.services.exception.UserRevokedException;


@Component
public class UserValidationFilter extends OncePerRequestFilter {
    
    private static Logger log = LoggerFactory.getLogger(UserValidationFilter.class
            .getName());
    
    
    private static String authenticatedURI = "/user/authenticated";
    
    private AntPathMatcher pathMatcher = new AntPathMatcher();
    
    private Set<String> excludeUrls = new HashSet<>(Arrays.asList("/swagger-resources/**",
            "/swagger-ui/**",
            "/v2/api-docs",
            "/webjars/**",
            "/actuator/**"));

    @Autowired
    private UserController userController;
    
    @Value("${server.servlet.context-path}")
    private String context;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {

            Optional<HPCUsers> userDetails = userController.getActiveUserDetails();
            if (userDetails.isPresent()) {

                HPCUsers user = userDetails.get();
                UsernamePasswordAuthenticationToken data = new UsernamePasswordAuthenticationToken(user, null, null);
                SecurityContextHolder.getContext().setAuthentication(data);
                filterChain.doFilter(request, response);
                return;
                
            } else {
                
                log.error(">>> User does not exists ");
                if (request.getRequestURI().contains(authenticatedURI)) {

                    log.debug(">>> New user/authenticated request");
                    filterChain.doFilter(request, response);
                    return;
                    
                } 

            }
        } catch (UserRevokedException e) {
            
            log.error(">>> user is revoked");
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return  excludeUrls.stream().map(p -> context+p).anyMatch(p -> pathMatcher.match(p, request.getRequestURI())); 
    }

}
