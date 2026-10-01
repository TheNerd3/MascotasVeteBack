package ubp.das.backndvt.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import ubp.das.backndvt.dto.RefugioResponse;
import ubp.das.backndvt.service.RefugioService;

/**
 * RF18 - Lista los refugios habilitados con sus publicaciones de
 * adopcion activas. Endpoint publico.
 */
@RestController
@RequestMapping("/refugios")
@Tag(name = "RF18 - Refugios", description = "Listado publico de refugios habilitados y sus publicaciones activas")
public class RefugioController {

    private final RefugioService refugioService;

    public RefugioController(RefugioService refugioService) {
        this.refugioService = refugioService;
    }

    @GetMapping
    @SecurityRequirements
    @Operation(
            summary = "Listar refugios habilitados (RF18)",
            description = "Endpoint publico. Solo devuelve refugios con habilitacion_municipal cargada, "
                    + "junto con sus publicaciones de adopcion en estado Activa.")
    @ApiResponse(responseCode = "200", description = "Listado de refugios habilitados")
    public ResponseEntity<List<RefugioResponse>> listar() {
        return ResponseEntity.ok(refugioService.listarHabilitados());
    }
}
