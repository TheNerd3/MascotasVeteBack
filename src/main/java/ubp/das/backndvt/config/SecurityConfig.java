package ubp.das.backndvt.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import ubp.das.backndvt.security.JwtAuthenticationFilter;

/**
 * Seguridad stateless basada en JWT (ver security.JwtService /
 * JwtAuthenticationFilter). No hay sesiones de servidor: cada request
 * autenticado manda su Bearer token.
 *
 * Las rutas de esta clase son relativas al backend (sin "/api"): el
 * prefijo "/api/v1" lo agrega el context path con el que se despliega
 * el WAR en Tomcat, no el codigo (ver Dockerfile).
 *
 * Rutas publicas (RNF03/RNF04: todo lo que no este acá requiere
 * autenticacion):
 * - POST /auth/login (RF15)
 * - GET /veterinarias (RF17, listado publico de habilitadas)
 * - GET /refugios y /publicaciones (RF18, listado publico)
 * - GET /carnet/validar (RF11, validacion de carnet para terceros)
 * - GET /atenciones-sanitarias/tipos/cantidad (endpoint de diagnostico
 *   de conexion, no expone datos sensibles)
 * - Swagger UI y la especificacion OpenAPI (documentacion de la API,
 *   no expone datos de negocio)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final String origenPermitido;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            @Value("${cors.origen-permitido}") String origenPermitido) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.origenPermitido = origenPermitido;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Permite que el frontend Angular, que corre en otro origen (otro
     * puerto), pueda llamar a este backend desde el navegador. El
     * origen permitido se configura con la variable de entorno
     * CORS_ORIGEN_PERMITIDO, nunca queda fijo en el código.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuracion = new CorsConfiguration();
        configuracion.setAllowedOrigins(List.of(origenPermitido));
        configuracion.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuracion.setAllowedHeaders(List.of("*"));
        configuracion.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource fuenteConfiguracion = new UrlBasedCorsConfigurationSource();
        fuenteConfiguracion.registerCorsConfiguration("/**", configuracion);
        return fuenteConfiguracion;
    }

    /**
     * Toda la autenticacion pasa por JwtAuthenticationFilter, no por el
     * UserDetailsService de Spring Security. Se define este bean vacio
     * unicamente para que Spring Boot no auto-genere un usuario en
     * memoria con password aleatoria (ver
     * UserDetailsServiceAutoConfiguration), que no se usa para nada aca
     * y solo ensucia los logs de arranque.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/veterinarias/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/refugios/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/publicaciones/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/carnet/validar").permitAll()
                        .requestMatchers(HttpMethod.GET, "/atenciones-sanitarias/tipos/cantidad").permitAll()
                        .requestMatchers(
                                "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
