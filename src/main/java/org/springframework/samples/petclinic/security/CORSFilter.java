/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author Vitaliy Fedoriv
 *
 */

@Component
public class CORSFilter implements Filter {
	
    public CORSFilter() { }
 
    public void destroy() { }
 
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
    	
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        
        fixHeader(response,"Access-Control-Allow-Origin", "*" );
        fixHeader(response,"Access-Control-Allow-Methods","GET, OPTIONS, HEAD, PUT, POST, DELETE" );
        fixHeader(response,"Access-Control-Allow-Credentials", "true" );
        fixHeader(response,"Access-Control-Max-Age", "10800" );
        fixHeader(response,"Access-Control-Allow-Headers", "content-type, accept, x-requested-with, authorization" );
 
        // answer for CORS handshake
        if (request.getMethod().equals("OPTIONS")) {
        	response.setStatus(HttpServletResponse.SC_ACCEPTED);
            return;
        }
        chain.doFilter(request, response);
    }
    
    private void fixHeader(HttpServletResponse response, String header, String value) {
    	if (response.containsHeader(header)) 
    		response.setHeader(header, value);
    	else response.addHeader(header, value);
    }
    
    public void init(FilterConfig fConfig) throws ServletException { }
    
    @Bean
    public FilterRegistrationBean CORSFilterRegistration(CORSFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(filter);
        registration.setOrder(-100);
        return registration;
    }

}
