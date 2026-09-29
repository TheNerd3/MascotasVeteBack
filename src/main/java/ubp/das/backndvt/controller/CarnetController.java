package ubp.das.backndvt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
public class CarnetController {

    private final CarnetSanitarioService carnetSanitarioService;

    public CarnetController(CarnetSanitarioService carnetSanitarioService) {
        this.carnetSanitarioService = carnetSanitarioService;
    }

    @GetMapping("/validar")
    public ResponseEntity<ValidarCarnetResponse> validar(@RequestParam String token) {
        return ResponseEntity.ok(carnetSanitarioService.validarToken(token));
    }
}
