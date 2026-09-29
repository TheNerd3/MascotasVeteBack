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
    public ResponseEntity<MascotaResponse> registrar(@Valid @RequestBody RegistrarMascotaRequest request) {
        RegistrarMascotaResultado resultado = mascotaService.registrar(request);
        HttpStatus status = resultado.creada() ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status).body(resultado.mascota());
    }

    @PostMapping("/{nrm}/atenciones")
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
    public ResponseEntity<CarnetSanitarioResponse> obtenerCarnet(
            @PathVariable("nrm") Integer nroRegMunicipal,
            @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.ok(carnetSanitarioService.obtenerCarnet(nroRegMunicipal, usuario.idCiudadano()));
    }
}
