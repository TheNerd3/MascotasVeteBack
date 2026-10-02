package ubp.das.backndvt.dto;

import java.util.List;

import ubp.das.backndvt.entity.Refugio;

// RF18 - Datos publicos de un refugio habilitado, con sus
// publicaciones de adopcion activas
public record RefugioResponse(
        Integer idRefugio,
        String razonSocial,
        String correo,
        String telefono,
        String domicilio,
        List<PublicacionAdopcionResponse> publicacionesActivas) {

    public static RefugioResponse from(Refugio refugio, List<PublicacionAdopcionResponse> publicacionesActivas) {
        return new RefugioResponse(
                refugio.getIdRefugio(),
                refugio.getRazonSocial(),
                refugio.getCorreo(),
                refugio.getTelefono(),
                refugio.getDomicilio(),
                publicacionesActivas);
    }
}
