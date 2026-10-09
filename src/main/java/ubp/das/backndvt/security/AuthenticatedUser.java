package ubp.das.backndvt.security;

/**
 * Datos del usuario autenticado extraidos del JWT, disponibles en el
 * SecurityContext para que los controllers/services validen ownership
 * (ej: RF20 "solo el propio usuario puede consultar sus mascotas",
 * RF13 "solo el refugio dueño gestiona sus publicaciones"). idRefugio
 * es null si el perfil no es REFUGIO.
 */
public record AuthenticatedUser(Integer idCiudadano, String cuil, String perfil, Integer idRefugio) {
}
