package ubp.das.backndvt.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ubp.das.backndvt.dto.RefugioResponse;
import ubp.das.backndvt.entity.Refugio;
import ubp.das.backndvt.repository.PublicacionAdopcionRepository;
import ubp.das.backndvt.repository.RefugioRepository;

/**
 * RF18 - Lista los refugios habilitados (endpoint publico) junto con
 * sus publicaciones de adopcion activas.
 */
@Service
public class RefugioService {

    private final RefugioRepository refugioRepository;
    private final PublicacionAdopcionRepository publicacionAdopcionRepository;

    public RefugioService(RefugioRepository refugioRepository, PublicacionAdopcionRepository publicacionAdopcionRepository) {
        this.refugioRepository = refugioRepository;
        this.publicacionAdopcionRepository = publicacionAdopcionRepository;
    }

    @Transactional(readOnly = true)
    public List<RefugioResponse> listarHabilitados() {
        return refugioRepository.findByHabilitacionMunicipalIsNotNullAndHabilitacionMunicipalNot("").stream()
                .map(this::conPublicacionesActivas)
                .toList();
    }

    private RefugioResponse conPublicacionesActivas(Refugio refugio) {
        return RefugioResponse.from(
                refugio, publicacionAdopcionRepository.findActivasPorRefugioOrdenadoPorFecha(refugio.getIdRefugio()));
    }
}
