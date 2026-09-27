package ubp.das.backndvt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ubp.das.backndvt.dto.CiudadanoResponse;
import ubp.das.backndvt.security.AuthenticatedUser;
import ubp.das.backndvt.service.CiudadanoService;

/**
 * Expone los datos de un ciudadano ya registrado en el sistema.
 * Implementa RF20: cada ciudadano solo puede consultar sus propios
 * datos, nunca los de otra persona.
 */
@RestController
@RequestMapping("/api/ciudadanos")
public class CiudadanoController {

    private final CiudadanoService ciudadanoService;

    public CiudadanoController(CiudadanoService ciudadanoService) {
        this.ciudadanoService = ciudadanoService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("#usuario.idCiudadano() == #id")
    public ResponseEntity<CiudadanoResponse> buscarPorId(
            @PathVariable Integer id,
            @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.ok(ciudadanoService.buscarPorId(id));
    }
}
