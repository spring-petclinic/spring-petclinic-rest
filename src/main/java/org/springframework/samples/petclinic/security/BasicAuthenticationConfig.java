package org.springframework.samples.petclinic.security;

// @Configuration
// @EnableGlobalMethodSecurity(prePostEnabled = true) // Enable @PreAuthorize method-level security
// @ConditionalOnProperty(name = "petclinic.security.enable", havingValue = "true")
public class BasicAuthenticationConfig  {

    // @Autowired
    // private DataSource dataSource;

    // @Bean
    // public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    //     // @formatter:off
    //     http
    //         .authorizeHttpRequests((authz) -> authz
    //             .anyRequest().authenticated()
    //             )
    //             .httpBasic()
    //                 .and()
    //             .csrf()
    //                 .disable();
    //     // @formatter:on
    //     return http.build();
    // }

    // @Autowired
    // public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    //     // @formatter:off
    //     auth
    //         .jdbcAuthentication()
    //             .dataSource(dataSource)
    //             .usersByUsernameQuery("select username,password,enabled from users where username=?")
    //             .authoritiesByUsernameQuery("select username,role from roles where username=?");
    //     // @formatter:on
    // }
}
