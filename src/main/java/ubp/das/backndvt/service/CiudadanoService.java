package ubp.das.backndvt.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ubp.das.backndvt.dto.CiudadanoResponse;
import ubp.das.backndvt.dto.MascotaResponse;
import ubp.das.backndvt.entity.Ciudadano;
import ubp.das.backndvt.exception.RecursoNoEncontradoException;
import ubp.das.backndvt.repository.CiudadanoRepository;
import ubp.das.backndvt.repository.MascotaRepository;

/**
 * Consulta de datos de ciudadanos ya existentes en el sistema.
 * Implementa RF20: consultar los propios datos y las propias
 * mascotas (la autorizacion de "solo el propio usuario" se valida en
 * el controller con @PreAuthorize).
 */
@Service
public class CiudadanoService {

    private final CiudadanoRepository ciudadanoRepository;
    private final MascotaRepository mascotaRepository;

    public CiudadanoService(CiudadanoRepository ciudadanoRepository, MascotaRepository mascotaRepository) {
        this.ciudadanoRepository = ciudadanoRepository;
        this.mascotaRepository = mascotaRepository;
    }

    public CiudadanoResponse buscarPorId(Integer idCiudadano) {
        Ciudadano ciudadano = obtenerOFallar(idCiudadano);
        return CiudadanoResponse.from(ciudadano);
    }

    public List<MascotaResponse> listarMascotas(Integer idCiudadano) {
        obtenerOFallar(idCiudadano);
        return mascotaRepository.findByResponsableIdCiudadano(idCiudadano).stream()
                .map(MascotaResponse::from)
                .toList();
    }

    private Ciudadano obtenerOFallar(Integer idCiudadano) {
        return ciudadanoRepository.findById(idCiudadano)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un ciudadano con id " + idCiudadano));
    }
}
