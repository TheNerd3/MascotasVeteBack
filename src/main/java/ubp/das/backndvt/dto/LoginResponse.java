package ubp.das.backndvt.dto;

/**
 * Respuesta de un login exitoso (RF15). "expiraEn" son los segundos
 * que faltan para que el token venza, así el frontend sabe cuándo
 * pedir que el ciudadano vuelva a iniciar sesión.
 */
public record LoginResponse(
        String token,
        long expiraEn,
        CiudadanoResponse usuario) {
}
