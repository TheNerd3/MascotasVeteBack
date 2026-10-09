package ubp.das.backndvt.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ubp.das.backndvt.dto.CambiarEstadoPublicacionRequest;
import ubp.das.backndvt.dto.CrearPublicacionAdopcionRequest;
import ubp.das.backndvt.dto.PublicacionAdopcionResponse;
import ubp.das.backndvt.entity.EstadoPublicacion;
import ubp.das.backndvt.security.AuthenticatedUser;
import ubp.das.backndvt.service.PublicacionAdopcionService;

/**
 * RF13 - "Mis publicaciones" de adopcion: todo este controller es del
 * refugio autenticado (ownership resuelto siempre desde el JWT via
 * PublicacionAdopcionService + RefugioAutorizacionService). El
 * listado publico de publicaciones Activas por refugio es RF18, ver
 * RefugioController.
 */
@RestController
@RequestMapping("/publicaciones")
@Tag(name = "3. RF13 - Mis publicaciones", description = "Alta y gestion de publicaciones del refugio autenticado")
public class PublicacionAdopcionController {

    private final PublicacionAdopcionService publicacionAdopcionService;

    public PublicacionAdopcionController(PublicacionAdopcionService publicacionAdopcionService) {
        this.publicacionAdopcionService = publicacionAdopcionService;
    }

    @PostMapping
    @Operation(
            summary = "Crear publicacion de adopcion (RF13)",
            description = "El refugio sale del usuario autenticado, nunca del body. Si no viene nroRegMunicipal, "
                    + "registra la mascota en el mismo flujo (mismo comportamiento que POST /mascotas), asociada "
                    + "siempre al refugio autenticado. Valida que la mascota pertenezca a ese refugio y que no "
                    + "tenga ya otra publicacion Activa.")
    @ApiResponse(responseCode = "201", description = "Publicacion creada en estado Activa")
    @ApiResponse(responseCode = "403",
            description = "El usuario autenticado no es responsable de ningun refugio, o la mascota no le pertenece")
    @ApiResponse(responseCode = "404", description = "La mascota indicada no existe")
    @ApiResponse(responseCode = "409", description = "La mascota ya tiene una publicacion Activa")
    public ResponseEntity<PublicacionAdopcionResponse> crear(
            @Valid @RequestBody CrearPublicacionAdopcionRequest request,
            @AuthenticationPrincipal AuthenticatedUser usuario) {
        PublicacionAdopcionResponse respuesta = publicacionAdopcionService.crear(request, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @PatchMapping("/{nroPublicacion}/estado")
    @Operation(
            summary = "Cambiar el estado de una publicacion (RF13)",
            description = "Recibe una accion (ACTIVAR, PAUSAR o FINALIZAR), no un estado destino: el estado "
                    + "destino de cada accion lo decide la maquina de estados del backend. Activa <-> Pausada, "
                    + "cualquiera de las dos puede Finalizar, y Finalizada es terminal.")
    @ApiResponse(responseCode = "200", description = "Estado actualizado")
    @ApiResponse(responseCode = "403", description = "El usuario autenticado no es el refugio dueño de la publicacion")
    @ApiResponse(responseCode = "404", description = "No existe una publicacion con ese numero")
    @ApiResponse(responseCode = "409",
            description = "La accion no es valida para el estado actual, o ya existe otra publicacion Activa de la misma mascota")
    public ResponseEntity<PublicacionAdopcionResponse> cambiarEstado(
            @PathVariable Integer nroPublicacion,
            @Valid @RequestBody CambiarEstadoPublicacionRequest request,
            @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.ok(publicacionAdopcionService.cambiarEstado(nroPublicacion, request, usuario));
    }

    @GetMapping
    @Operation(
            summary = "Listar mis publicaciones (RF13)",
            description = "Publicaciones del refugio autenticado, paginado y con filtro opcional por estado. "
                    + "Para el listado publico de publicaciones Activas por refugio, ver GET /refugios (RF18).")
    @ApiResponse(responseCode = "200", description = "Pagina de publicaciones del refugio autenticado")
    @ApiResponse(responseCode = "403", description = "El usuario autenticado no es responsable de ningun refugio")
    public ResponseEntity<Page<PublicacionAdopcionResponse>> listarMisPublicaciones(
            @Parameter(description = "Filtrar por estado: Activa, Pausada o Finalizada")
            @RequestParam(required = false) EstadoPublicacion estado,
            @AuthenticationPrincipal AuthenticatedUser usuario,
            Pageable pageable) {
        return ResponseEntity.ok(publicacionAdopcionService.listarMisPublicaciones(estado, usuario, pageable));
    }

    @GetMapping(value = "/{nroPublicacion}/foto", produces = MediaType.IMAGE_JPEG_VALUE)
    @SecurityRequirements
    @Operation(
            summary = "Foto de una publicacion (RF18, endpoint publico)",
            description = "Sin token: lo consume un <img src>, que no manda el header Authorization. Solo "
                    + "devuelve la foto si la publicacion esta Activa (404 en cualquier otro caso), para no "
                    + "exponer fotos de publicaciones Pausadas o Finalizadas a quien adivine el id.")
    @ApiResponse(responseCode = "200", description = "Foto de la publicacion")
    @ApiResponse(responseCode = "404", description = "No existe la publicacion, no esta Activa, o no tiene foto")
    public ResponseEntity<byte[]> obtenerFotoPublica(@PathVariable Integer nroPublicacion) {
        return ResponseEntity.ok(publicacionAdopcionService.obtenerFotoPublica(nroPublicacion));
    }

    @GetMapping(value = "/{nroPublicacion}/foto/propia", produces = MediaType.IMAGE_JPEG_VALUE)
    @Operation(
            summary = "Foto de una publicacion propia (RF13)",
            description = "Requiere autenticacion: a diferencia del endpoint publico, devuelve la foto sin "
                    + "importar el estado de la publicacion (Activa, Pausada o Finalizada), siempre que el "
                    + "refugio autenticado sea el dueño.")
    @ApiResponse(responseCode = "200", description = "Foto de la publicacion")
    @ApiResponse(responseCode = "403", description = "El usuario autenticado no es el refugio dueño de la publicacion")
    @ApiResponse(responseCode = "404", description = "No existe la publicacion, o no tiene foto")
    public ResponseEntity<byte[]> obtenerFotoPropia(
            @PathVariable Integer nroPublicacion, @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.ok(publicacionAdopcionService.obtenerFotoPropia(nroPublicacion, usuario));
    }
}
