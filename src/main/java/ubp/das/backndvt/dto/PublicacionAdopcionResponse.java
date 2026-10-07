package ubp.das.backndvt.dto;

import java.time.LocalDate;
import java.util.Set;

import ubp.das.backndvt.entity.EstadoPublicacion;
import ubp.das.backndvt.entity.EstadoPublicacion.Accion;
import ubp.das.backndvt.entity.PublicacionAdopcion;

// RF13/RF18 - Datos publicos de una publicacion de adopcion (sin
// exponer la entidad JPA). accionesDisponibles le dice al frontend
// que botones mostrar segun el estado actual, sin que tenga que
// reimplementar la maquina de estados (ver EstadoPublicacion).
public record PublicacionAdopcionResponse(
        Integer nroPublicacion,
        Integer nroRegMunicipal,
        String nombreMascota,
        String sexoMascota,
        Short anioNacimientoMascota,
        String especieMascota,
        String razaMascota,
        Integer idRefugio,
        LocalDate fechaPublicacion,
        String caracteristicasMascota,
        String condicionAdopcion,
        EstadoPublicacion estadoPublicacion,
        Set<Accion> accionesDisponibles) {

    public static PublicacionAdopcionResponse from(PublicacionAdopcion publicacion, String especie, String raza) {
        return new PublicacionAdopcionResponse(
                publicacion.getNroPublicacion(),
                publicacion.getMascota().getNroRegMunicipal(),
                publicacion.getMascota().getNombre(),
                publicacion.getMascota().getSexo(),
                publicacion.getMascota().getAnioNacimiento(),
                especie,
                raza,
                publicacion.getRefugio().getIdRefugio(),
                publicacion.getFechaPublicacion(),
                publicacion.getCaracteristicasMascota(),
                publicacion.getCondicionAdopcion(),
                publicacion.getEstadoPublicacion(),
                publicacion.getEstadoPublicacion().accionesDisponibles());
    }
}
