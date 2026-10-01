package ubp.das.backndvt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import ubp.das.backndvt.dto.ValidarCarnetResponse;
import ubp.das.backndvt.service.CarnetSanitarioService;

/**
 * Valida un carnet sanitario digital para terceros autorizados (por
 * ejemplo, una veterinaria que atiende a la mascota por primera vez).
 * Implementa RF11. Es público: no devuelve datos del dueño ni el
 * historial, solo confirma autenticidad y vigencia.
 */
@RestController
@RequestMapping("/carnet")
@Tag(name = "RF11 - Validacion de carnet", description = "Validacion publica del carnet sanitario para terceros")
public class CarnetController {

    private final CarnetSanitarioService carnetSanitarioService;

    public CarnetController(CarnetSanitarioService carnetSanitarioService) {
        this.carnetSanitarioService = carnetSanitarioService;
    }

    @GetMapping("/validar")
    @SecurityRequirements
    @Operation(
            summary = "Validar un token de carnet sanitario",
            description = "Endpoint publico (no requiere token de sesion). Recalcula la firma HMAC-SHA256 y "
                    + "chequea la vigencia. Nunca expone datos del dueño ni el historial, solo si es "
                    + "autentico/vigente y los datos basicos de la mascota.")
    @ApiResponse(responseCode = "200",
            description = "Siempre 200: el campo valido indica si el token es autentico y esta vigente")
    public ResponseEntity<ValidarCarnetResponse> validar(
            @Parameter(description = "Token de verificacion devuelto por GET /mascotas/{nrm}/carnet")
            @RequestParam String token) {
        return ResponseEntity.ok(carnetSanitarioService.validarToken(token));
    }
}
