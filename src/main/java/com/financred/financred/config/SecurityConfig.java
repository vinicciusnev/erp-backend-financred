package com.financred.financred.config;

import com.financred.financred.infra.security.SecurityFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // === ROTAS PÚBLICAS ===
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll()

                        // === ROTAS DO CLIENTE (logado com ROLE_CLIENTE) ===
                        .requestMatchers(HttpMethod.GET, "/api/v1/clientes/me").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/clientes/me").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/emprestimos/me").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/emprestimos/me/status").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.POST, "/api/v1/emprestimos/quitar").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.POST, "/api/v1/emprestimos/parcelas/quitar").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/historico-pagamentos/me").hasRole("CLIENTE")

                        // === ROTAS ACESSÍVEIS TANTO POR ADMIN QUANTO CLIENTE ===
                        .requestMatchers(HttpMethod.GET, "/api/v1/emprestimos/*/parcelas").hasAnyRole("CLIENTE", "ADMIN")

                        // === ROTAS DE ADMIN ===
                        .requestMatchers(HttpMethod.GET, "/api/v1/clientes").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/clientes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/emprestimos/admin").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/historico-pagamentos/admin").hasRole("ADMIN")

                        // === ROTAS GERAIS (autenticado, qualquer perfil) ===
                        .requestMatchers(HttpMethod.GET, "/api/v1/dashboard/stats").authenticated()

                        // === ROTA WEBSOCKET (autenticado, qualquer perfil) ===
                        .requestMatchers("/api/v1/ws/**").permitAll()

                        // === QUALQUER OUTRA ROTA ===
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler())
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:443", "http://localhost:5173", "http://localhost:8081"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Usuario nao autenticado\"}");
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Acesso negado\"}");
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}