package ubp.das.backndvt.dto;

import jakarta.validation.constraints.NotBlank;

// RF21 - Consulta de texto libre al asistente virtual
public record ConsultaAsistenteRequest(

        @NotBlank(message = "la consulta es obligatoria")
        String consulta) {
}
