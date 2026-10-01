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
import ubp.das.backndvt.dto.VeterinariaResponse;
import ubp.das.backndvt.service.VeterinariaService;

/**
 * RF17 - Lista las veterinarias habilitadas. Endpoint publico.
 */
@RestController
@RequestMapping("/veterinarias")
@Tag(name = "4. RF17 - Veterinarias", description = "Listado publico de veterinarias habilitadas")
public class VeterinariaController {

    private final VeterinariaService veterinariaService;

    public VeterinariaController(VeterinariaService veterinariaService) {
        this.veterinariaService = veterinariaService;
    }

    @GetMapping
    @SecurityRequirements
    @Operation(
            summary = "Listar veterinarias habilitadas (RF17)",
            description = "Endpoint publico. Solo devuelve veterinarias con habilitacion_municipal cargada.")
    @ApiResponse(responseCode = "200", description = "Listado de veterinarias habilitadas")
    public ResponseEntity<List<VeterinariaResponse>> listar() {
        return ResponseEntity.ok(veterinariaService.listarHabilitadas());
    }
}
