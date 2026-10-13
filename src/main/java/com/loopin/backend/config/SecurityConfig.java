package com.loopin.backend.config;

import com.loopin.backend.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final List<String> allowedOrigins;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          @Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:5173}") String origins) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.allowedOrigins = Arrays.stream(origins.split(",")).map(String::trim).filter(s -> !s.isBlank()).toList();
    }

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Bootstrap-Token"));
        config.setExposedHeaders(List.of("Location"));
        config.setAllowCredentials(false);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, exception) -> {
                            response.setStatus(401);
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            response.getWriter().write("{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Autenticação necessária ou token inválido\"}");
                        })
                        .accessDeniedHandler((request, response, exception) -> {
                            response.setStatus(403);
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            response.getWriter().write("{\"status\":403,\"error\":\"Forbidden\",\"message\":\"Você não tem permissão para esta operação\"}");
                        }))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/health").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/login", "/auth/bootstrap-lider").permitAll()
                        .requestMatchers(HttpMethod.POST, "/usuarios").permitAll()
                        .requestMatchers(HttpMethod.GET, "/usuarios/me").authenticated()
                        .requestMatchers("/usuarios", "/usuarios/**").hasRole("LIDER")

                        .requestMatchers(HttpMethod.GET, "/estrategias", "/estrategias/**")
                        .hasAnyRole("OPERADOR", "GESTOR", "LIDER")
                        .requestMatchers(HttpMethod.POST, "/estrategias").hasRole("LIDER")
                        .requestMatchers(HttpMethod.PUT, "/estrategias/**").hasRole("LIDER")
                        .requestMatchers(HttpMethod.DELETE, "/estrategias/**").hasRole("LIDER")

                        .requestMatchers(HttpMethod.GET, "/ideias/minhas").hasRole("OPERADOR")
                        .requestMatchers(HttpMethod.GET, "/ideias").hasAnyRole("GESTOR", "LIDER")
                        .requestMatchers(HttpMethod.GET, "/ideias/**").hasAnyRole("OPERADOR", "GESTOR", "LIDER")
                        .requestMatchers(HttpMethod.POST, "/ideias").hasRole("OPERADOR")
                        .requestMatchers(HttpMethod.PATCH, "/ideias/*/avaliacao").hasRole("GESTOR")
                        .requestMatchers(HttpMethod.PUT, "/ideias/*").hasRole("OPERADOR")
                        .requestMatchers(HttpMethod.DELETE, "/ideias/*").hasRole("OPERADOR")

                        .requestMatchers(HttpMethod.GET, "/projetos", "/projetos/**").hasAnyRole("GESTOR", "LIDER")
                        .requestMatchers(HttpMethod.POST, "/projetos").hasRole("GESTOR")
                        .requestMatchers(HttpMethod.PUT, "/projetos/**").hasRole("GESTOR")
                        .requestMatchers(HttpMethod.DELETE, "/projetos/**").hasRole("GESTOR")
                        .requestMatchers(HttpMethod.POST, "/projetos/*/resultados").hasRole("GESTOR")

                        .requestMatchers(HttpMethod.GET, "/relatorios/**").hasRole("LIDER")
                        .requestMatchers(HttpMethod.POST, "/ia/**").hasRole("GESTOR")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
