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
 * Genera y valida el token que prueba que un ciudadano inició sesión
 * (RF15). El token guarda el cuil, el id del ciudadano y su perfil,
 * para no tener que consultar la base en cada pedido al servidor.
 *
 * La clave con la que se firma el token viene de la variable de
 * entorno JWT_SECRET (nunca va escrita en el código), y debe tener al
 * menos 32 caracteres porque el algoritmo de firma lo exige. La
 * duración del token viene de JWT_EXPIRACION (en segundos).
 */
@Service
public class JwtService {

    private final SecretKey clave;
    private final long expiracionSegundos;

    public JwtService(
            @Value("${jwt.secret}") String secreto,
            @Value("${jwt.expiracion-segundos}") long expiracionSegundos) {
        this.clave = Keys.hmacShaKeyFor(secreto.getBytes());
        this.expiracionSegundos = expiracionSegundos;
    }

    public String generarToken(String cuil, Integer idCiudadano, String perfil) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + expiracionSegundos * 1000);

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

    public long obtenerExpiracionSegundos() {
        return expiracionSegundos;
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
