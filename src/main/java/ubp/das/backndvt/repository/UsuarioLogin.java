package ubp.das.backndvt.repository;

/**
 * Datos de un ciudadano leídos directamente de la tabla `ciudadanos`
 * con JdbcTemplate, para el login (RF15). Incluye la clave hasheada
 * porque hace falta para compararla; nunca se debe convertir esto en
 * una respuesta HTTP tal cual.
 */
public record UsuarioLogin(
        Integer idCiudadano,
        String apellido,
        String nombre,
        String cuil,
        String clave,
        String correo,
        String telefono,
        String domicilio,
        Boolean habilitado) {
}
