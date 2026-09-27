package ubp.das.backndvt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
