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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "RF13/RF18 - Publicaciones de adopcion", description = "Alta y gestion de publicaciones, listado publico")
public class PublicacionAdopcionController {

    private final PublicacionAdopcionService publicacionAdopcionService;

    public PublicacionAdopcionController(PublicacionAdopcionService publicacionAdopcionService) {
        this.publicacionAdopcionService = publicacionAdopcionService;
    }

    @PostMapping("/refugios/{idRefugio}/publicaciones")
    @Operation(
            summary = "Crear publicacion de adopcion (RF13)",
            description = "Si no viene nroRegMunicipal, registra la mascota en el mismo flujo (mismo "
                    + "comportamiento que POST /mascotas). Solo el responsable del refugio puede publicar en "
                    + "su nombre. Valida que la mascota no tenga ya otra publicacion Activa.")
    @ApiResponse(responseCode = "201", description = "Publicacion creada en estado Activa")
    @ApiResponse(responseCode = "403", description = "El usuario autenticado no es el responsable de ese refugio")
    @ApiResponse(responseCode = "404", description = "El refugio o la mascota indicada no existen")
    @ApiResponse(responseCode = "409", description = "La mascota ya tiene una publicacion Activa")
    public ResponseEntity<PublicacionAdopcionResponse> crear(
            @PathVariable Integer idRefugio,
            @Valid @RequestBody CrearPublicacionAdopcionRequest request,
            @AuthenticationPrincipal AuthenticatedUser usuario) {
        PublicacionAdopcionResponse respuesta = publicacionAdopcionService.crear(idRefugio, request, usuario.idCiudadano());
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @PutMapping("/publicaciones/{nroPublicacion}")
    @Operation(
            summary = "Cambiar el estado de una publicacion (RF13)",
            description = "Activa, Pausada o Finalizada. Al reactivar, revalida que no exista ya otra "
                    + "publicacion Activa para la misma mascota.")
    @ApiResponse(responseCode = "200", description = "Estado actualizado")
    @ApiResponse(responseCode = "403", description = "El usuario autenticado no es el responsable del refugio dueño de la publicacion")
    @ApiResponse(responseCode = "404", description = "No existe una publicacion con ese numero")
    @ApiResponse(responseCode = "409", description = "Ya existe otra publicacion Activa para la misma mascota")
    public ResponseEntity<PublicacionAdopcionResponse> actualizarEstado(
            @PathVariable Integer nroPublicacion,
            @Valid @RequestBody ActualizarPublicacionAdopcionRequest request,
            @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.ok(
                publicacionAdopcionService.actualizarEstado(nroPublicacion, request, usuario.idCiudadano()));
    }

    @GetMapping("/publicaciones")
    @SecurityRequirements
    @Operation(
            summary = "Listar publicaciones de adopcion (RF18)",
            description = "Endpoint publico. Si no se indica estado, devuelve todas las publicaciones.")
    @ApiResponse(responseCode = "200", description = "Listado de publicaciones")
    public ResponseEntity<List<PublicacionAdopcionResponse>> listar(
            @Parameter(description = "Filtrar por estado: Activa, Pausada o Finalizada")
            @RequestParam(required = false) String estado) {
        return ResponseEntity.ok(publicacionAdopcionService.listarPorEstado(estado));
    }
}
