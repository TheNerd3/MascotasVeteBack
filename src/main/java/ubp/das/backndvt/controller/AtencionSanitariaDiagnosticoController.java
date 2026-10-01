package ubp.das.backndvt.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import ubp.das.backndvt.repository.TipoAtencionSanitariaRepository;

/**
 * Endpoint de solo lectura para comprobar que el backend se conecta
 * correctamente a la base MascotasCordoba (útil al probar el
 * despliegue con Docker). No implementa ningún RF del sistema: es
 * únicamente una comprobación técnica de conexión.
 */
@RestController
@RequestMapping("/atenciones-sanitarias/tipos")
@Tag(name = "9. Diagnostico", description = "Endpoint tecnico de verificacion de conexion a la base, no implementa ningun RF")
public class AtencionSanitariaDiagnosticoController {

    private final TipoAtencionSanitariaRepository tipoAtencionSanitariaRepository;

    public AtencionSanitariaDiagnosticoController(
            TipoAtencionSanitariaRepository tipoAtencionSanitariaRepository) {
        this.tipoAtencionSanitariaRepository = tipoAtencionSanitariaRepository;
    }

    @GetMapping("/cantidad")
    @SecurityRequirements
    @Operation(
            summary = "Contar tipos de atencion sanitaria (diagnostico)",
            description = "No implementa ningun RF. Sirve solo para comprobar que el backend esta "
                    + "conectado a la base de datos (por ejemplo, al validar un despliegue nuevo).")
    @ApiResponse(responseCode = "200", description = "Cantidad de filas en tipos_atencion_sanitaria")
    public long contarTiposDeAtencion() {
        return tipoAtencionSanitariaRepository.count();
    }
}
