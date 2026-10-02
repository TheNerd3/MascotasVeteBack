package ubp.das.backndvt.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ubp.das.backndvt.dto.AtencionSanitariaResponse;
import ubp.das.backndvt.dto.CarnetSanitarioResponse;
import ubp.das.backndvt.dto.MascotaResponse;
import ubp.das.backndvt.dto.RegistrarAtencionSanitariaRequest;
import ubp.das.backndvt.dto.RegistrarMascotaRequest;
import ubp.das.backndvt.dto.RegistrarMascotaResultado;
import ubp.das.backndvt.security.AuthenticatedUser;
import ubp.das.backndvt.service.AtencionSanitariaService;
import ubp.das.backndvt.service.CarnetSanitarioService;
import ubp.das.backndvt.service.MascotaService;

/**
 * Recibe el alta de mascotas (RF06), su información sanitaria (RF09) y
 * el carnet sanitario digital (RF10/RF11) en el Registro Único
 * Municipal.
 */
@RestController
@RequestMapping("/mascotas")
@Tag(name = "2. RF06/RF09/RF10/RF11 - Mascotas", description = "Alta, atenciones sanitarias y carnet sanitario digital")
public class MascotaController {

    private final MascotaService mascotaService;
    private final AtencionSanitariaService atencionSanitariaService;
    private final CarnetSanitarioService carnetSanitarioService;

    public MascotaController(
            MascotaService mascotaService,
            AtencionSanitariaService atencionSanitariaService,
            CarnetSanitarioService carnetSanitarioService) {
        this.mascotaService = mascotaService;
        this.atencionSanitariaService = atencionSanitariaService;
        this.carnetSanitarioService = carnetSanitarioService;
    }

    @PostMapping
    @Operation(
            summary = "Registrar mascota (RF06)",
            description = "Busca duplicados por microchip (prioritario) o por nombre+año_nacimiento+responsable "
                    + "(sp_BuscarMascotaExistente). Si el propietario no existe todavia en ciudadanos, se crea. "
                    + "Devuelve 200 con la mascota existente si ya estaba registrada, o 201 con la nueva si se creo.")
    @ApiResponse(responseCode = "200", description = "La mascota ya existia, se devuelve la existente")
    @ApiResponse(responseCode = "201", description = "Mascota creada")
    @ApiResponse(responseCode = "400", description = "Datos invalidos (sexo, caracteristicas, etc.)")
    @ApiResponse(responseCode = "404", description = "idResponsable, idRefugio o un rasgo/valor de dominio no existen")
    @ApiResponse(responseCode = "409", description = "El microchip ya esta registrado en otra mascota")
    public ResponseEntity<MascotaResponse> registrar(@Valid @RequestBody RegistrarMascotaRequest request) {
        RegistrarMascotaResultado resultado = mascotaService.registrar(request);
        HttpStatus status = resultado.creada() ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status).body(resultado.mascota());
    }

    @PostMapping("/{nrm}/atenciones")
    @Operation(
            summary = "Registrar atencion sanitaria (RF09)",
            description = "Valida en orden: la mascota existe, el tipo de atencion existe, la veterinaria esta "
                    + "habilitada, y el profesional esta vinculado a esa veterinaria sin baja. Inserta via "
                    + "sp_InsertarInformacionSanitaria (nunca INSERT directo).")
    @ApiResponse(responseCode = "201", description = "Atencion registrada, devuelve el nroRegistro generado")
    @ApiResponse(responseCode = "400", description = "Datos invalidos (fecha, tipo de atencion, etc.)")
    @ApiResponse(responseCode = "404",
            description = "Mascota, tipo de atencion, veterinaria o profesional no existen, "
                    + "veterinaria no habilitada, o profesional dado de baja")
    public ResponseEntity<AtencionSanitariaResponse> registrarAtencion(
            @PathVariable("nrm") Integer nroRegMunicipal,
            @Valid @RequestBody RegistrarAtencionSanitariaRequest request) {
        AtencionSanitariaResponse respuesta = atencionSanitariaService.registrar(nroRegMunicipal, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    // RF10/RF11 - Solo el ciudadano responsable de la mascota puede ver su carnet
    // (la autorizacion se valida en el service, porque necesita ir a buscar
    // primero el id_responsable de la mascota antes de poder compararlo).
    @GetMapping("/{nrm}/carnet")
    @Operation(
            summary = "Obtener carnet sanitario digital (RF10)",
            description = "Arma el carnet desde vw_carnet_sanitario con el historial de atenciones y un token "
                    + "de verificacion HMAC-SHA256 (distinto del JWT de sesion) para validarlo luego con "
                    + "GET /carnet/validar. Solo el responsable de la mascota puede verlo.")
    @ApiResponse(responseCode = "200", description = "Carnet obtenido")
    @ApiResponse(responseCode = "403", description = "El usuario autenticado no es el responsable de la mascota")
    @ApiResponse(responseCode = "404", description = "No existe una mascota con ese nro_reg_municipal")
    public ResponseEntity<CarnetSanitarioResponse> obtenerCarnet(
            @PathVariable("nrm") Integer nroRegMunicipal,
            @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.ok(carnetSanitarioService.obtenerCarnet(nroRegMunicipal, usuario.idCiudadano()));
    }
}
