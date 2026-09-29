package ubp.das.backndvt.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import ubp.das.backndvt.dto.AtencionSanitariaResponse;
import ubp.das.backndvt.dto.MascotaResponse;
import ubp.das.backndvt.dto.RegistrarAtencionSanitariaRequest;
import ubp.das.backndvt.dto.RegistrarMascotaRequest;
import ubp.das.backndvt.dto.RegistrarMascotaResultado;
import ubp.das.backndvt.service.AtencionSanitariaService;
import ubp.das.backndvt.service.MascotaService;

/**
 * Recibe el alta de mascotas (RF06) y de su información sanitaria
 * (RF09) en el Registro Único Municipal.
 */
@RestController
@RequestMapping("/mascotas")
public class MascotaController {

    private final MascotaService mascotaService;
    private final AtencionSanitariaService atencionSanitariaService;

    public MascotaController(MascotaService mascotaService, AtencionSanitariaService atencionSanitariaService) {
        this.mascotaService = mascotaService;
        this.atencionSanitariaService = atencionSanitariaService;
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
}
