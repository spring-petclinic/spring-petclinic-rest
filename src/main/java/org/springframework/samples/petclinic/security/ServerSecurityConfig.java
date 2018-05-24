package org.springframework.samples.petclinic.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
@Import(Encoders.class)

public class ServerSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder userPasswordEncoder;

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(userPasswordEncoder);
    }
    
//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        // web.ignoring().antMatchers("OPTIONS", "/**");
//    	// web.ignoring().antMatchers("OPTIONS", "/login");
//    }
    
//    @Override
//    public void configure(final HttpSecurity http) throws Exception {
//    	http.cors().and().addFilterAfter(new CORSFilter(), SecurityContextPersistenceFilter.class)
//    	.authorizeRequests().antMatchers("/login","/welcome").permitAll().and()
//        .authorizeRequests().antMatchers("/api/**").authenticated();
//    }
    
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(Arrays.asList("*"));
//        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
//        configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
//        configuration.setExposedHeaders(Arrays.asList("x-auth-token"));
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
    
//    @Bean
//    public FilterRegistrationBean corsFilter() {
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true);
//        config.addAllowedOrigin("*");
//        config.addAllowedHeader("*");
//        config.addAllowedMethod("*");
//        source.registerCorsConfiguration("/**", config);
//        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
//        // bean.setOrder(0);
//        bean.setOrder(50);
//        return bean;
//    }
    

     
    
/*    
	@Override
	public void configure(HttpSecurity http) throws Exception {
		// http.csrf().disable();
		
		// http.addFilterBefore(new SimpleCORSFilter(), BasicAuthenticationFilter.class).
		//csrf().disable().httpBasic().and().
		http.
		requestMatchers().and().
		authorizeRequests().antMatchers("/login/**","/logout/**", "/oauth/**", "/oauth/authorize", "/oauth/confirm_access").permitAll();
		
	}
*/
   


}
