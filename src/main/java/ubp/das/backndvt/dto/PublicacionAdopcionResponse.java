package ubp.das.backndvt.dto;

import java.time.LocalDate;
import java.util.Set;

import ubp.das.backndvt.entity.EstadoPublicacion;
import ubp.das.backndvt.entity.EstadoPublicacion.Accion;

// RF13/RF18 - Datos publicos de una publicacion de adopcion.
// accionesDisponibles le dice al frontend que botones mostrar segun el
// estado actual, sin que tenga que reimplementar la maquina de estados
// (ver EstadoPublicacion). fotoExiste evita mandar el binario completo
// en el listado: la foto se pide aparte, GET /publicaciones/{id}/foto.
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
        Set<Accion> accionesDisponibles,
        boolean fotoExiste) {
}
