package ubp.das.backndvt.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Datos que manda el frontend para iniciar sesión. "usuario" es el
 * identificador del ciudadano (su CUIL), con ese nombre genérico
 * porque a futuro otros perfiles (refugio, veterinaria) también van a
 * loguearse con este mismo endpoint.
 */
public record LoginRequest(

        @NotBlank(message = "el usuario es obligatorio")
        String usuario,

        @NotBlank(message = "la clave es obligatoria")
        String clave) {
}
