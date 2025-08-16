package com.shequ.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                // Allow access to error pages and static resources
                .antMatchers("/error", "/favicon.ico", "/static/**", "/public/**").permitAll()
                // Authentication - related interfaces allow anonymous access
                .antMatchers("/api/auth/**").permitAll()
                // Public API interfaces (including home page data and activity details viewing)
                .antMatchers("/api/public/**").permitAll()
                // The activity registration interface requires login authentication.
                .antMatchers("/api/public/activities/*/register").authenticated()
                .antMatchers("/api/public/activities/*/cancel").authenticated()
                .antMatchers("/api/public/activities/*/mark-attended/*").hasRole("ADMIN")
                // Get activity categories (public interface)
                .antMatchers("/api/categories/enabled").permitAll()
                // Get the list of public activities
                .antMatchers("/api/activities/public").permitAll()
                // Public API interfaces
                .antMatchers("/api/public/**").permitAll()
                // Get activity categories (public interface)
                .antMatchers("/api/categories/enabled").permitAll()
                // Get the list of public activities
                .antMatchers("/api/activities/public").permitAll()
                // The administrator interface requires administrator privileges.
                .antMatchers("/api/admin/**").hasRole("ADMIN")
                // Other API interfaces require authentication
                .antMatchers("/api/**").authenticated()
                // Other requests allow anonymous access (for front - end routing)
                .anyRequest().permitAll()
                .and()
                // Add JWT filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // Handle exceptions
                .exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> {
                    // Check if the request is an API request
                    if (request.getRequestURI().startsWith("/api/")) {
                        response.setContentType("application/json;charset=UTF-8");
                        response.setStatus(401);
                        response.getWriter().write("{\"code\":401,\"message\":\"Unauthorized, please log in first\",\"data\":null}");
                    } else {
                        // For non - API requests, return an error page.
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                    }
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    // Check if the request is an API request
                    if (request.getRequestURI().startsWith("/api/")) {
                        response.setContentType("application/json;charset=UTF-8");
                        response.setStatus(403);
                        response.getWriter().write("{\"code\":403,\"message\":\"Insufficient permissions\",\"data\":null}");
                    } else {
                        // For non - API requests, return an error page.
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
                    }
                });
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
