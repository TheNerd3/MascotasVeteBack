package ubp.das.backndvt.repository;

import java.time.LocalDate;

// RF10/RF11 - Una fila de vw_carnet_sanitario. nroRegistro y el resto
// de los campos de atencion vienen null cuando la mascota todavia no
// tiene ninguna atencion registrada (LEFT JOIN en la vista).
public record CarnetSanitarioFila(
        Integer nroRegMunicipal,
        String nombreMascota,
        String nombreResponsable,
        String apellidoResponsable,
        String cuil,
        Integer nroRegistro,
        LocalDate fechaAtencion,
        String descTipoAtencion,
        String detalleAtencion,
        LocalDate fechaVencimiento,
        String veterinaria) {
}
