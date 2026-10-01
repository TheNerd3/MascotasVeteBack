package ubp.das.backndvt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ubp.das.backndvt.dto.LoginRequest;
import ubp.das.backndvt.dto.LoginResponse;
import ubp.das.backndvt.service.AuthService;

/**
 * Recibe el pedido de inicio de sesión de un ciudadano. Implementa
 * RF15. Es público: para poder loguearse todavía no se tiene un token.
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "RF15 - Login", description = "Autenticacion local contra ciudadanos.cuil/clave")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @SecurityRequirements
    @Operation(
            summary = "Iniciar sesion",
            description = "Valida cuil + clave contra ciudadanos y devuelve un JWT. El perfil "
                    + "(CIUDADANO/REFUGIO/VETERINARIA) se resuelve segun si el ciudadano es "
                    + "responsable de un refugio o profesional activo de una veterinaria.")
    @ApiResponse(responseCode = "200", description = "Login exitoso")
    @ApiResponse(responseCode = "401", description = "Cuil/clave incorrectos o ciudadano deshabilitado",
            content = @Content(schema = @Schema(implementation = ubp.das.backndvt.exception.ErrorLoginResponse.class)))
    @ApiResponse(responseCode = "400", description = "Datos de login invalidos (cuil o clave vacios)")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
