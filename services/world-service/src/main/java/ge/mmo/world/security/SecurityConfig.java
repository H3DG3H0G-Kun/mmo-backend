package ge.mmo.world.security;

import ge.mmo.common.security.JwtService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig {

    @Bean
    JwtService jwtService(JwtProperties props) {
        // TTL is irrelevant for a verify-only service; supply a placeholder.
        Duration ttl = props.accessTtl() != null ? props.accessTtl() : Duration.ofHours(1);
        return new JwtService(props.secret(), props.issuer(), ttl);
    }

    @Bean
    AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, ex) -> response.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized");
    }

    @Bean
    AccessDeniedHandler accessDeniedHandler() {
        return (request, response, ex) -> response.sendError(HttpStatus.FORBIDDEN.value(), "Forbidden");
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http,
                                    JwtService jwtService,
                                    AuthenticationEntryPoint entryPoint,
                                    AccessDeniedHandler accessDeniedHandler) throws Exception {
        http
                .cors(org.springframework.security.config.Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Only health/info/prometheus/metrics are exposed (see application.yml);
                        // permit them so Prometheus can scrape. Lock down per-environment later.
                        .requestMatchers("/actuator/**").permitAll()
                        // The Content Studio static page (served from /static/studio).
                        .requestMatchers("/", "/studio/**").permitAll()
                        // The WebSocket handshake authenticates via its own first message, not this filter.
                        .requestMatchers("/ws/**").permitAll()
                        // Content authoring is admin-only.
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .exceptionHandling(e -> e.authenticationEntryPoint(entryPoint).accessDeniedHandler(accessDeniedHandler))
                .addFilterBefore(new JwtAuthenticationFilter(jwtService),
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
