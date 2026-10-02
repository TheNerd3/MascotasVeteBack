package ubp.das.backndvt.security;

/**
 * Datos del usuario autenticado extraidos del JWT, disponibles en el
 * SecurityContext para que los controllers/services validen ownership
 * (ej: RF20 "solo el propio usuario puede consultar sus mascotas").
 */
public record AuthenticatedUser(Integer idCiudadano, String cuil, String perfil) {
}
