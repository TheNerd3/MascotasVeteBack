package ubp.das.backndvt.dto;

import java.util.List;

// RF10/RF11 - Carnet sanitario digital completo de una mascota
public record CarnetSanitarioResponse(
        Integer nroRegMunicipal,
        String nombreMascota,
        String nombreResponsable,
        String apellidoResponsable,
        String cuil,
        List<AtencionCarnetResponse> atenciones,
        String tokenVerificacion) {
}
