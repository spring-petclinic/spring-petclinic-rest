package org.springframework.samples.petclinic.rest.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;

@Configuration
public class ActuatorAcceptHeaderConfig {

    @Bean
    public FilterRegistrationBean<Filter> actuatorAcceptHeaderFilter() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new ActuatorAcceptHeaderFilter());
        registration.addUrlPatterns("/actuator/*");
        registration.setOrder(Integer.MIN_VALUE);
        return registration;
    }

    private static class ActuatorAcceptHeaderFilter implements Filter {
        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            chain.doFilter(new AcceptJsonRequestWrapper(httpRequest), response);
        }
    }

    private static class AcceptJsonRequestWrapper extends HttpServletRequestWrapper {
        AcceptJsonRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getHeader(String name) {
            if ("Accept".equalsIgnoreCase(name)) {
                return "application/json";
            }
            return super.getHeader(name);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            if ("Accept".equalsIgnoreCase(name)) {
                return Collections.enumeration(Collections.singletonList("application/json"));
            }
            return super.getHeaders(name);
        }
    }
}
