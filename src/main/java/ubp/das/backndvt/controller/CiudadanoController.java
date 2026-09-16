package ubp.das.backndvt.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import ubp.das.backndvt.dto.CiudadanoRequest;
import ubp.das.backndvt.dto.CiudadanoResponse;
import ubp.das.backndvt.security.AuthenticatedUser;
import ubp.das.backndvt.service.CiudadanoService;

@RestController
@RequestMapping("/api/ciudadanos")
public class CiudadanoController {

    private final CiudadanoService ciudadanoService;

    public CiudadanoController(CiudadanoService ciudadanoService) {
        this.ciudadanoService = ciudadanoService;
    }

    /**
     * Alta de ciudadano. Publico (ver SecurityConfig): un ciudadano nuevo
     * todavia no tiene token para autenticarse.
     */
    @PostMapping
    public ResponseEntity<CiudadanoResponse> registrar(@Valid @RequestBody CiudadanoRequest request) {
        CiudadanoResponse creado = ciudadanoService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    /**
     * Consulta de un ciudadano por id. Requiere autenticacion (ver
     * SecurityConfig) y que el usuario autenticado sea el propio
     * ciudadano consultado, para no exponer datos personales de terceros.
     */
    @GetMapping("/{id}")
    @PreAuthorize("#usuario.idCiudadano() == #id")
    public ResponseEntity<CiudadanoResponse> buscarPorId(
            @PathVariable Integer id,
            @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.ok(ciudadanoService.buscarPorId(id));
    }
}
