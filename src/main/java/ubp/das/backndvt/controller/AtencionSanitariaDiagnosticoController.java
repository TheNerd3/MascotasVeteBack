package ubp.das.backndvt.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ubp.das.backndvt.repository.TipoAtencionSanitariaRepository;

/**
 * Endpoint de solo lectura para comprobar que el backend se conecta
 * correctamente a la base MascotasCordoba (útil al probar el
 * despliegue con Docker). No implementa ningún RF del sistema: es
 * únicamente una comprobación técnica de conexión.
 */
@RestController
@RequestMapping("/atenciones-sanitarias/tipos")
public class AtencionSanitariaDiagnosticoController {

    private final TipoAtencionSanitariaRepository tipoAtencionSanitariaRepository;

    public AtencionSanitariaDiagnosticoController(
            TipoAtencionSanitariaRepository tipoAtencionSanitariaRepository) {
        this.tipoAtencionSanitariaRepository = tipoAtencionSanitariaRepository;
    }

    @GetMapping("/cantidad")
    public long contarTiposDeAtencion() {
        return tipoAtencionSanitariaRepository.count();
    }
}
