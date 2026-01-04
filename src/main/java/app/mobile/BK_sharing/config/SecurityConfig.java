package app.mobile.BK_sharing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                // 1. Disable CSRF (for API testing)
//                .csrf(csrf -> csrf.disable())
//
//                // 2. Configure CORS to allow everything
//                .cors(cors -> cors.configurationSource(request -> {
//                    org.springframework.web.cors.CorsConfiguration config =
//                            new org.springframework.web.cors.CorsConfiguration();
//                    config.setAllowedOrigins(java.util.List.of("*"));
//                    config.setAllowedMethods(java.util.List.of("*"));
//                    config.setAllowedHeaders(java.util.List.of("*"));
//                    config.setAllowCredentials(false);  // Must be false with wildcard
//                    return config;
//                }))
//
//                // 3. Configure authorization - ALLOW EVERYTHING
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/**").permitAll()  // Allow ALL paths
//                        .anyRequest().permitAll()            // Double confirmation
//                )
//
//                // 4. Disable form login and basic auth
//                .formLogin(form -> form.disable())
//                .httpBasic(basic -> basic.disable())
//
//                // 5. Disable session creation (stateless)
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS)
//                );
//
//        return http.build();
//    }
}