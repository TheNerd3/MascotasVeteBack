package ubp.das.backndvt.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ubp.das.backndvt.dto.ValorCatalogoResponse;
import ubp.das.backndvt.entity.RasgoMascota;
import ubp.das.backndvt.exception.RecursoNoEncontradoException;
import ubp.das.backndvt.repository.DominioRasgoMascotaRepository;
import ubp.das.backndvt.repository.RasgoMascotaRepository;

/**
 * RF06/RF13 - Catalogos de especie y raza para los combos del
 * formulario de alta de mascota/publicacion. Especie y raza son
 * rasgos independientes en el modelo (rasgos_mascotas +
 * dominio_rasgos_mascotas): no hay relacion especie-raza en la base,
 * asi que el catalogo de razas devuelve todo el dominio sin filtrar.
 */
@Service
public class CatalogoService {

    private static final String RASGO_ESPECIE = "Especie";
    private static final String RASGO_RAZA = "Raza";

    private final RasgoMascotaRepository rasgoMascotaRepository;
    private final DominioRasgoMascotaRepository dominioRasgoMascotaRepository;

    public CatalogoService(
            RasgoMascotaRepository rasgoMascotaRepository, DominioRasgoMascotaRepository dominioRasgoMascotaRepository) {
        this.rasgoMascotaRepository = rasgoMascotaRepository;
        this.dominioRasgoMascotaRepository = dominioRasgoMascotaRepository;
    }

    @Transactional(readOnly = true)
    public List<ValorCatalogoResponse> listarEspecies() {
        return listarValoresDelRasgo(RASGO_ESPECIE);
    }

    @Transactional(readOnly = true)
    public List<ValorCatalogoResponse> listarRazas() {
        return listarValoresDelRasgo(RASGO_RAZA);
    }

    private List<ValorCatalogoResponse> listarValoresDelRasgo(String nombreRasgo) {
        Integer codRasgo = rasgoMascotaRepository.findAll().stream()
                .filter(rasgo -> nombreRasgo.equals(rasgo.getNomRasgo()))
                .map(RasgoMascota::getCodRasgo)
                .findFirst()
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el rasgo " + nombreRasgo));

        return dominioRasgoMascotaRepository.findByCodRasgo(codRasgo).stream()
                .map(ValorCatalogoResponse::from)
                .toList();
    }
}
