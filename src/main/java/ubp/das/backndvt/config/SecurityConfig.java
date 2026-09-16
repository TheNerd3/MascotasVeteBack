package ubp.das.backndvt.config;

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

import ubp.das.backndvt.security.JwtAuthenticationFilter;

/**
 * Seguridad stateless basada en JWT (ver security.JwtService /
 * JwtAuthenticationFilter). No hay sesiones de servidor: cada request
 * autenticado manda su Bearer token.
 *
 * Rutas publicas (RNF03/RNF04: todo lo que no este acá requiere
 * autenticacion):
 * - POST /api/auth/login (RF15)
 * - POST /api/ciudadanos (alta de ciudadano)
 * - GET /api/veterinarias (RF17, listado publico de habilitadas)
 * - GET /api/refugios y /api/publicaciones (RF18, listado publico)
 * - GET /api/carnet/validar (RF11, validacion de carnet para terceros)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
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
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/ciudadanos").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/veterinarias/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/refugios/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/publicaciones/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/carnet/validar").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
