package ubp.das.backndvt.dto;

import jakarta.validation.constraints.NotBlank;

// RF15 - Autenticar usuarios
public record LoginRequest(

        @NotBlank(message = "el cuil es obligatorio")
        String cuil,

        @NotBlank(message = "la clave es obligatoria")
        String clave) {
}
