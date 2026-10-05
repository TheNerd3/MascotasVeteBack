package ubp.das.backndvt.dto;

import java.time.LocalDate;

import ubp.das.backndvt.entity.PublicacionAdopcion;

// RF13/RF18 - Datos publicos de una publicacion de adopcion (sin
// exponer la entidad JPA)
public record PublicacionAdopcionResponse(
        Integer nroPublicacion,
        Integer nroRegMunicipal,
        String nombreMascota,
        String sexoMascota,
        Short anioNacimientoMascota,
        Integer idRefugio,
        LocalDate fechaPublicacion,
        String caracteristicasMascota,
        String condicionAdopcion,
        String estadoPublicacion) {

    public static PublicacionAdopcionResponse from(PublicacionAdopcion publicacion) {
        return new PublicacionAdopcionResponse(
                publicacion.getNroPublicacion(),
                publicacion.getMascota().getNroRegMunicipal(),
                publicacion.getMascota().getNombre(),
                publicacion.getMascota().getSexo(),
                publicacion.getMascota().getAnioNacimiento(),
                publicacion.getRefugio().getIdRefugio(),
                publicacion.getFechaPublicacion(),
                publicacion.getCaracteristicasMascota(),
                publicacion.getCondicionAdopcion(),
                publicacion.getEstadoPublicacion());
    }
}
