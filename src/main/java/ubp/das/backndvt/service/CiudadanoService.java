package ubp.das.backndvt.service;

import org.springframework.stereotype.Service;

import ubp.das.backndvt.dto.CiudadanoResponse;
import ubp.das.backndvt.entity.Ciudadano;
import ubp.das.backndvt.exception.RecursoNoEncontradoException;
import ubp.das.backndvt.repository.CiudadanoRepository;

/**
 * Consulta de datos de ciudadanos ya existentes en el sistema.
 * Implementa RF20 (consultar mis mascotas necesita antes poder
 * consultar los datos del propio ciudadano dueño).
 */
@Service
public class CiudadanoService {

    private final CiudadanoRepository ciudadanoRepository;

    public CiudadanoService(CiudadanoRepository ciudadanoRepository) {
        this.ciudadanoRepository = ciudadanoRepository;
    }

    public CiudadanoResponse buscarPorId(Integer idCiudadano) {
        Ciudadano ciudadano = obtenerOFallar(idCiudadano);
        return CiudadanoResponse.from(ciudadano);
    }

    private Ciudadano obtenerOFallar(Integer idCiudadano) {
        return ciudadanoRepository.findById(idCiudadano)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un ciudadano con id " + idCiudadano));
    }
}
