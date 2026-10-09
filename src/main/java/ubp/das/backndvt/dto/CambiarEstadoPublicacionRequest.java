package ubp.das.backndvt.dto;

import jakarta.validation.constraints.NotNull;
import ubp.das.backndvt.entity.EstadoPublicacion.Accion;

// RF13 - Pide una transicion de estado sobre una publicacion existente
// (PATCH /publicaciones/{id}/estado). La accion, no el estado destino,
// es lo que llega del cliente: el estado destino de cada accion lo
// decide EstadoPublicacion (unico lugar con esa regla).
public record CambiarEstadoPublicacionRequest(

        @NotNull(message = "la accion es obligatoria (ACTIVAR, PAUSAR o FINALIZAR)")
        Accion accion) {
}
