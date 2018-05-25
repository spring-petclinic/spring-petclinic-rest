package org.springframework.samples.petclinic.security;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.header.HeaderWriterFilter;

@Configuration
@EnableResourceServer
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER-6)
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
	
	private static final String RESOURCE_ID = "resource-server-rest-api";
	private static final String SECURED_READ_SCOPE = "#oauth2.hasScope('read')";
	private static final String SECURED_WRITE_SCOPE = "#oauth2.hasScope('write')";
	private static final String SECURED_PATTERN = "/api/**";

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) {
		resources.resourceId(RESOURCE_ID);
	}
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.cors().and()
//		.csrf().csrfTokenRepository(csrfTokenRepository())
//	    .and().
		.requestMatchers()
        .antMatchers(SECURED_PATTERN).and().authorizeRequests()
        .antMatchers(HttpMethod.POST, SECURED_PATTERN).access(SECURED_WRITE_SCOPE)
        .antMatchers("/login/**","/logout/**").permitAll()
        .anyRequest().access(SECURED_READ_SCOPE)
        .and().
   //     addFilterAfter(csrfHeaderFilter(), CsrfFilter.class).
        addFilterBefore(new CORSFilter(), HeaderWriterFilter.class);
	}
	
//    private Filter csrfHeaderFilter() {
//        return new OncePerRequestFilter() {
//            @Override
//            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
//                                            FilterChain filterChain) throws ServletException, IOException {
//                CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
//                if (csrf != null) {
//                    Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
//                    String token = csrf.getToken();
//                    if (cookie == null || token != null && !token.equals(cookie.getValue())) {
//                        cookie = new Cookie("XSRF-TOKEN", token);
//                        cookie.setPath("/");
//                        response.addCookie(cookie);
//                    }
//                }
//                filterChain.doFilter(request, response);
//            }
//        };
//    }
//
//    private CsrfTokenRepository csrfTokenRepository() {
//        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
//        repository.setHeaderName("X-XSRF-TOKEN");
//        return repository;
//    }

}
