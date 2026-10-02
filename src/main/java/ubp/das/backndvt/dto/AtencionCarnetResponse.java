package ubp.das.backndvt.dto;

import java.time.LocalDate;

// RF10/RF11 - Una atencion sanitaria dentro del carnet
public record AtencionCarnetResponse(
        Integer nroRegistro,
        LocalDate fechaAtencion,
        String tipoAtencion,
        String detalleAtencion,
        LocalDate fechaVencimiento,
        String veterinaria) {
}
