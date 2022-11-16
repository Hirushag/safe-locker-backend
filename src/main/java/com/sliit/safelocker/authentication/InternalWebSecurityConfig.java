package com.sliit.safelocker.authentication;

import com.sliit.safelocker.filter.AuthenticationFilter;
import com.sliit.safelocker.filter.JwtRequestFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Slf4j
@Configuration
@EnableWebSecurity
public class InternalWebSecurityConfig extends WebSecurityConfigurerAdapter {


    private final String URLS[] = {"/authenticate", "/register"};
    private final String SWAGGERURLS[] = {"/webjars/**", "/swagger-ui.html", "/swagger-resources/**", "/v2/api-docs", "/v3/api-docs/"};

    @Autowired
    private JwtRequestFilter jwtRequestFilter;


    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        // super.configure(http);
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authProvider());
        authenticationFilter.setFilterProcessesUrl("/authenticate");
        httpSecurity.csrf().disable()
                .cors().and()
                // dont authenticate this particular request
                .authorizeRequests().antMatchers().permitAll()
//                .antMatchers("/api/v1/hello2").hasAnyAuthority("USER", "ADMIN")
                // all other requests need to be authenticated
                .anyRequest().permitAll().and()
                // make sure we use stateless session; session won't be used to
                // store user's state.
                //.exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        httpSecurity.addFilter(authenticationFilter);
        /// Add a filter to validate the tokens with every request
        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider());

    }




    @Override
    @Bean
    protected AuthenticationManager authenticationManager() throws Exception {
        log.info("authenticationManager in WebSecurityConfig run");
        return super.authenticationManager();
    }


    @Bean
    public SafeLockerAuthenticationProvider authProvider() {
        SafeLockerAuthenticationProvider sltAuthenticationProvider = new SafeLockerAuthenticationProvider();
        return sltAuthenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
        corsConfiguration.addAllowedMethod(HttpMethod.PUT.name());
        corsConfiguration.addAllowedMethod(HttpMethod.DELETE.name());
        corsConfiguration.addAllowedMethod(HttpMethod.GET.name());
        corsConfiguration.addAllowedMethod(HttpMethod.POST.name());


        source.registerCorsConfiguration("/**", corsConfiguration);


        return source;
    }



}
