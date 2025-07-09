package ch.kbw.sl.config;

import ch.kbw.sl.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity // Still useful for enabling Spring Security features
@EnableMethodSecurity // Enables method-level security annotations like @PreAuthorize
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//    private final AuthenticationProvider authenticationProvider;

    public SecurityConfig( JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
//        this.authenticationProvider = authenticationProvider;
    }




    // Configure the Security Filter Chain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Example: Disable CSRF for stateless APIs
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/public/**", "/auth/**").permitAll() // Allow public access
                        .anyRequest().authenticated() // Require authentication for all other requests
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS) // Stateless session management
                )
//                .authenticationProvider(authenticationProvider) // Set the authentication provider
                // Configure how authentication happens, e.g., formLogin, httpBasic, oauth2Login, etc.
                // .formLogin(withDefaults()) // Example: Enable default form login
                // .httpBasic(withDefaults()); // Example: Enable default HTTP Basic

                // Explicitly set the AuthenticationManager if you defined it as a bean
                .authenticationManager(authenticationManager);
        // Add the JWT filter to the security chain
        http.addFilterBefore(jwtAuthenticationFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }

    // ... other security configurations (e.g., JWT filters, etc.)
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // Allow requests from http://localhost:3000
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        // Allow all common methods (GET, POST, PUT, DELETE, etc.)
        config.addAllowedMethod("*");
        // Allow all headers
        config.addAllowedHeader("*");
        // If you need to send cookies or authorization headers, you might need:
        // config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Apply this CORS configuration to all paths ("/**")
        source.registerCorsConfiguration("/**", config);

        return source;
    }

}
