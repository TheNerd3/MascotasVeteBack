package ubp.das.backndvt.dto;

import ubp.das.backndvt.entity.Ciudadano;

/**
 * DTO de salida de ciudadano: nunca incluye "clave" (ni siquiera el hash).
 */
public record CiudadanoResponse(
        Integer idCiudadano,
        String apellido,
        String nombre,
        String cuil,
        String correo,
        String telefono,
        String domicilio,
        Boolean habilitado) {

    public static CiudadanoResponse from(Ciudadano ciudadano) {
        return new CiudadanoResponse(
                ciudadano.getIdCiudadano(),
                ciudadano.getApellido(),
                ciudadano.getNombre(),
                ciudadano.getCuil(),
                ciudadano.getCorreo(),
                ciudadano.getTelefono(),
                ciudadano.getDomicilio(),
                ciudadano.getHabilitado());
    }
}
