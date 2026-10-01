package ubp.das.backndvt.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import ubp.das.backndvt.dto.CiudadanoResponse;
import ubp.das.backndvt.dto.MascotaResponse;
import ubp.das.backndvt.security.AuthenticatedUser;
import ubp.das.backndvt.service.CiudadanoService;

/**
 * Expone los datos de un ciudadano ya registrado en el sistema.
 * Implementa RF20: cada ciudadano solo puede consultar sus propios
 * datos, nunca los de otra persona.
 */
@RestController
@RequestMapping("/ciudadanos")
@Tag(name = "RF20 - Ciudadanos", description = "Datos propios del ciudadano autenticado y sus mascotas")
public class CiudadanoController {

    private final CiudadanoService ciudadanoService;

    public CiudadanoController(CiudadanoService ciudadanoService) {
        this.ciudadanoService = ciudadanoService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("#usuario.idCiudadano() == #id")
    @Operation(
            summary = "Obtener los datos de un ciudadano",
            description = "Solo el propio ciudadano autenticado puede consultar sus datos (nunca se expone la clave).")
    @ApiResponse(responseCode = "200", description = "Datos del ciudadano")
    @ApiResponse(responseCode = "403", description = "El id del path no coincide con el usuario autenticado")
    @ApiResponse(responseCode = "404", description = "No existe un ciudadano con ese id")
    public ResponseEntity<CiudadanoResponse> buscarPorId(
            @PathVariable Integer id,
            @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.ok(ciudadanoService.buscarPorId(id));
    }

    @GetMapping("/{id}/mascotas")
    @PreAuthorize("#usuario.idCiudadano() == #id")
    @Operation(
            summary = "Listar las mascotas de un ciudadano (RF20)",
            description = "Solo el propio ciudadano autenticado puede ver sus mascotas registradas.")
    @ApiResponse(responseCode = "200", description = "Listado de mascotas (puede ser vacio)")
    @ApiResponse(responseCode = "403", description = "El id del path no coincide con el usuario autenticado")
    @ApiResponse(responseCode = "404", description = "No existe un ciudadano con ese id")
    public ResponseEntity<List<MascotaResponse>> listarMascotas(
            @PathVariable Integer id,
            @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.ok(ciudadanoService.listarMascotas(id));
    }
}
