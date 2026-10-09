package ubp.das.backndvt.config;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
 * - GET /refugios (RF18, listado publico con sus publicaciones Activas)
 * - GET /carnet/validar (RF11, validacion de carnet para terceros)
 * - GET /catalogos/** (especies y razas, listados de referencia para
 *   formularios, no datos de negocio)
 * - GET /publicaciones/{id}/foto (RF18: la consume un <img src>, que no
 *   manda Authorization; el propio endpoint devuelve 404 si la
 *   publicacion no esta Activa, asi que no expone fotos de pausadas o
 *   finalizadas igual)
 * - GET /atenciones-sanitarias/tipos/cantidad (endpoint de diagnostico
 *   de conexion, no expone datos sensibles)
 * - Swagger UI y la especificacion OpenAPI (documentacion de la API,
 *   no expone datos de negocio)
 *
 * GET/POST/PATCH /publicaciones (RF13, "mis publicaciones") requiere
 * autenticacion y perfil REFUGIO: no es lo mismo que el listado
 * publico de RF18, que vive en GET /refugios. Esto incluye
 * /publicaciones/{id}/foto/propia (RF13, foto sin importar el estado,
 * solo el refugio dueño).
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
        configuracion.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
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
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(this::escribirProblemDetail401)
                        .accessDeniedHandler((request, response, ex) -> escribirProblemDetail403(request, response)))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/veterinarias/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/refugios/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/carnet/validar").permitAll()
                        .requestMatchers(HttpMethod.GET, "/catalogos/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/publicaciones/*/foto").permitAll()
                        .requestMatchers(HttpMethod.GET, "/atenciones-sanitarias/tipos/cantidad").permitAll()
                        .requestMatchers(
                                "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/publicaciones/**").hasRole("REFUGIO")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private void escribirProblemDetail401(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException ex) throws IOException {
        escribirRespuesta(request, response, HttpStatus.UNAUTHORIZED, "Se requiere un token valido (Authorization: Bearer <token>)");
    }

    private void escribirProblemDetail403(HttpServletRequest request, HttpServletResponse response) throws IOException {
        escribirRespuesta(request, response, HttpStatus.FORBIDDEN, "No tiene permisos para acceder a este recurso");
    }

    /**
     * Arma el JSON con el mismo formato RFC 7807 (ProblemDetail) que
     * usa GlobalExceptionHandler para el resto del backend, pero sin
     * depender de un ObjectMapper: estos handlers corren en el
     * filtro de seguridad, antes de que Spring MVC entre en juego, y
     * el cuerpo es siempre el mismo par fijo (detail, path).
     */
    private void escribirRespuesta(HttpServletRequest request, HttpServletResponse response, HttpStatus status, String detalle)
            throws IOException {
        String detalleEscapado = detalle.replace("\"", "\\\"");
        String path = request.getRequestURI().replace("\"", "\\\"");
        String json = """
                {"type":"about:blank","title":"%s","status":%d,"detail":"%s","instance":"%s"}"""
                .formatted(status.getReasonPhrase(), status.value(), detalleEscapado, path);

        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write(json);
    }
}
