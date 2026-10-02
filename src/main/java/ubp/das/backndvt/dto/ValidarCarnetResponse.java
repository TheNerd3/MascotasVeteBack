package ubp.das.backndvt.dto;

import java.time.LocalDate;

// RF11 - Respuesta publica de validacion de carnet: solo confirma
// autenticidad/vigencia, nunca datos del dueño ni el historial.
public record ValidarCarnetResponse(
        boolean valido,
        Integer nroRegMunicipal,
        String nombreMascota,
        LocalDate fechaEmision) {
}
