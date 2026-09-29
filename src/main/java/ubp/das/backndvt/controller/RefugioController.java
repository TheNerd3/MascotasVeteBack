package ubp.das.backndvt.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ubp.das.backndvt.dto.RefugioResponse;
import ubp.das.backndvt.service.RefugioService;

/**
 * RF18 - Lista los refugios habilitados con sus publicaciones de
 * adopcion activas. Endpoint publico.
 */
@RestController
@RequestMapping("/refugios")
public class RefugioController {

    private final RefugioService refugioService;

    public RefugioController(RefugioService refugioService) {
        this.refugioService = refugioService;
    }

    @GetMapping
    public ResponseEntity<List<RefugioResponse>> listar() {
        return ResponseEntity.ok(refugioService.listarHabilitados());
    }
}
