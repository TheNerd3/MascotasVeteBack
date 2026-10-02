package ubp.das.backndvt.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

// RF09 - Registrar informacion sanitaria
public record RegistrarAtencionSanitariaRequest(

        @NotNull(message = "la fechaAtencion es obligatoria")
        LocalDate fechaAtencion,

        @NotNull(message = "el codTipoAtencion es obligatorio")
        Integer codTipoAtencion,

        String detalleAtencion,

        LocalDate fechaVencimiento,

        @NotNull(message = "el idVeterinaria es obligatorio")
        Integer idVeterinaria,

        @NotNull(message = "el idProfesional es obligatorio")
        Integer idProfesional) {
}
