package ubp.das.backndvt.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ubp.das.backndvt.dto.VeterinariaResponse;
import ubp.das.backndvt.repository.VeterinariaRepository;

/**
 * RF17 - Lista las veterinarias habilitadas (endpoint publico).
 */
@Service
public class VeterinariaService {

    private final VeterinariaRepository veterinariaRepository;

    public VeterinariaService(VeterinariaRepository veterinariaRepository) {
        this.veterinariaRepository = veterinariaRepository;
    }

    public List<VeterinariaResponse> listarHabilitadas() {
        return veterinariaRepository.findByHabilitacionMunicipalIsNotNullAndHabilitacionMunicipalNot("").stream()
                .map(VeterinariaResponse::from)
                .toList();
    }
}
