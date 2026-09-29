package ubp.das.backndvt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

// RF13 - Cambia el estado de una publicacion de adopcion existente
// (Activa/Pausada/Finalizada).
public record ActualizarPublicacionAdopcionRequest(

        @NotBlank(message = "el estado es obligatorio")
        @Pattern(regexp = "Activa|Pausada|Finalizada", message = "el estado debe ser Activa, Pausada o Finalizada")
        String estadoPublicacion) {
}
