package ubp.das.backndvt.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import ubp.das.backndvt.dto.ActualizarPublicacionAdopcionRequest;
import ubp.das.backndvt.dto.CrearPublicacionAdopcionRequest;
import ubp.das.backndvt.dto.PublicacionAdopcionResponse;
import ubp.das.backndvt.security.AuthenticatedUser;
import ubp.das.backndvt.service.PublicacionAdopcionService;

/**
 * RF13 - Publicaciones de adopcion. Crear/actualizar requieren ser el
 * responsable del refugio; el listado publico (RF18) no requiere
 * autenticacion.
 */
@RestController
public class PublicacionAdopcionController {

    private final PublicacionAdopcionService publicacionAdopcionService;

    public PublicacionAdopcionController(PublicacionAdopcionService publicacionAdopcionService) {
        this.publicacionAdopcionService = publicacionAdopcionService;
    }

    @PostMapping("/refugios/{idRefugio}/publicaciones")
    public ResponseEntity<PublicacionAdopcionResponse> crear(
            @PathVariable Integer idRefugio,
            @Valid @RequestBody CrearPublicacionAdopcionRequest request,
            @AuthenticationPrincipal AuthenticatedUser usuario) {
        PublicacionAdopcionResponse respuesta = publicacionAdopcionService.crear(idRefugio, request, usuario.idCiudadano());
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @PutMapping("/publicaciones/{nroPublicacion}")
    public ResponseEntity<PublicacionAdopcionResponse> actualizarEstado(
            @PathVariable Integer nroPublicacion,
            @Valid @RequestBody ActualizarPublicacionAdopcionRequest request,
            @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.ok(
                publicacionAdopcionService.actualizarEstado(nroPublicacion, request, usuario.idCiudadano()));
    }

    @GetMapping("/publicaciones")
    public ResponseEntity<List<PublicacionAdopcionResponse>> listar(
            @RequestParam(required = false) String estado) {
        return ResponseEntity.ok(publicacionAdopcionService.listarPorEstado(estado));
    }
}
