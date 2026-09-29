package ubp.das.backndvt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import ubp.das.backndvt.dto.ConsultaAsistenteRequest;
import ubp.das.backndvt.dto.ConsultaAsistenteResponse;
import ubp.das.backndvt.service.AsistenteVirtualService;

/**
 * RF21 - Asistente virtual de texto libre para usuarios autenticados.
 */
@RestController
@RequestMapping("/asistente")
public class AsistenteVirtualController {

    private final AsistenteVirtualService asistenteVirtualService;

    public AsistenteVirtualController(AsistenteVirtualService asistenteVirtualService) {
        this.asistenteVirtualService = asistenteVirtualService;
    }

    @PostMapping("/consulta")
    public ResponseEntity<ConsultaAsistenteResponse> consultar(@Valid @RequestBody ConsultaAsistenteRequest request) {
        return ResponseEntity.ok(asistenteVirtualService.responder(request));
    }
}
