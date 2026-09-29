package ubp.das.backndvt.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ubp.das.backndvt.dto.VeterinariaResponse;
import ubp.das.backndvt.service.VeterinariaService;

/**
 * RF17 - Lista las veterinarias habilitadas. Endpoint publico.
 */
@RestController
@RequestMapping("/veterinarias")
public class VeterinariaController {

    private final VeterinariaService veterinariaService;

    public VeterinariaController(VeterinariaService veterinariaService) {
        this.veterinariaService = veterinariaService;
    }

    @GetMapping
    public ResponseEntity<List<VeterinariaResponse>> listar() {
        return ResponseEntity.ok(veterinariaService.listarHabilitadas());
    }
}
