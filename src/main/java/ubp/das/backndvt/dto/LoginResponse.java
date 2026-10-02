package ubp.das.backndvt.dto;

/**
 * Respuesta de POST /api/auth/login. "perfil" es el rol que se le asigna
 * al usuario dentro del token (por ahora se resuelve como CIUDADANO por
 * default; los perfiles REFUGIO/VETERINARIA/MUNICIPALIDAD se agregan en
 * los PRs de esas secciones, cuando exista el vinculo correspondiente en
 * la base con esa persona).
 */
public record LoginResponse(
        String token,
        String tipo,
        CiudadanoResponse ciudadano,
        String perfil) {

    public static LoginResponse of(String token, CiudadanoResponse ciudadano, String perfil) {
        return new LoginResponse(token, "Bearer", ciudadano, perfil);
    }
}
