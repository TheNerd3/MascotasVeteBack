package ubp.das.backndvt.security;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Emision y validacion de JWT propios (sin integracion real con CiDi, ver
 * brief seccion 6). El "subject" del token es el cuil del ciudadano; se
 * agrega el claim "perfil" (CIUDADANO/REFUGIO/VETERINARIA/MUNICIPALIDAD)
 * para autorizacion basica en los controllers.
 *
 * La clave de firma se toma de la variable de entorno JWT_SECRET. Debe
 * tener al menos 32 caracteres (HMAC-SHA256 lo exige).
 */
@Service
public class JwtService {

    private static final long EXPIRACION_MS = 1000L * 60 * 60 * 8; // 8 horas

    private final SecretKey clave;

    public JwtService(@Value("${jwt.secret}") String secreto) {
        this.clave = Keys.hmacShaKeyFor(secreto.getBytes());
    }

    public String generarToken(String cuil, Integer idCiudadano, String perfil) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + EXPIRACION_MS);

        return Jwts.builder()
                .subject(cuil)
                .claims(Map.of(
                        "idCiudadano", idCiudadano,
                        "perfil", perfil))
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(clave)
                .compact();
    }

    public String extraerCuil(String token) {
        return extraerClaims(token).getSubject();
    }

    public String extraerPerfil(String token) {
        return extraerClaim(token, claims -> claims.get("perfil", String.class));
    }

    public Integer extraerIdCiudadano(String token) {
        return extraerClaim(token, claims -> claims.get("idCiudadano", Integer.class));
    }

    public boolean esValido(String token) {
        try {
            Claims claims = extraerClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    private <T> T extraerClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extraerClaims(token));
    }

    private Claims extraerClaims(String token) {
        return Jwts.parser()
                .verifyWith(clave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
