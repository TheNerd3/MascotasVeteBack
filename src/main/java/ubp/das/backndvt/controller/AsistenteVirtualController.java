package ubp.das.backndvt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ubp.das.backndvt.dto.ConsultaAsistenteRequest;
import ubp.das.backndvt.dto.ConsultaAsistenteResponse;
import ubp.das.backndvt.service.AsistenteVirtualService;

/**
 * RF21 - Asistente virtual de texto libre para usuarios autenticados.
 */
@RestController
@RequestMapping("/asistente")
@Tag(name = "8. RF21 - Asistente virtual", description = "Consulta de texto libre a un asistente virtual")
public class AsistenteVirtualController {

    private final AsistenteVirtualService asistenteVirtualService;

    public AsistenteVirtualController(AsistenteVirtualService asistenteVirtualService) {
        this.asistenteVirtualService = asistenteVirtualService;
    }

    @PostMapping("/consulta")
    @Operation(
            summary = "Consultar al asistente virtual (RF21)",
            description = "Devuelve una respuesta a una consulta de texto libre. La implementacion actual "
                    + "es un mock con respuesta fija, detras de una interfaz reemplazable por un proveedor "
                    + "de IA real sin tocar este endpoint.")
    @ApiResponse(responseCode = "200", description = "Respuesta del asistente")
    @ApiResponse(responseCode = "400", description = "La consulta viene vacia")
    public ResponseEntity<ConsultaAsistenteResponse> consultar(@Valid @RequestBody ConsultaAsistenteRequest request) {
        return ResponseEntity.ok(asistenteVirtualService.responder(request));
    }
}
