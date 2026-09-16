package ubp.das.backndvt.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ubp.das.backndvt.dto.CiudadanoRequest;
import ubp.das.backndvt.dto.CiudadanoResponse;
import ubp.das.backndvt.entity.Ciudadano;
import ubp.das.backndvt.exception.RecursoDuplicadoException;
import ubp.das.backndvt.exception.RecursoNoEncontradoException;
import ubp.das.backndvt.repository.CiudadanoRepository;

@Service
public class CiudadanoService {

    private final CiudadanoRepository ciudadanoRepository;
    private final PasswordEncoder passwordEncoder;

    public CiudadanoService(CiudadanoRepository ciudadanoRepository, PasswordEncoder passwordEncoder) {
        this.ciudadanoRepository = ciudadanoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public CiudadanoResponse registrar(CiudadanoRequest request) {
        ciudadanoRepository.findByCuil(request.cuil()).ifPresent(existente -> {
            throw new RecursoDuplicadoException("Ya existe un ciudadano registrado con el cuil " + request.cuil());
        });

        Ciudadano ciudadano = new Ciudadano();
        ciudadano.setApellido(request.apellido());
        ciudadano.setNombre(request.nombre());
        ciudadano.setCuil(request.cuil());
        ciudadano.setClave(passwordEncoder.encode(request.clave()));
        ciudadano.setCorreo(request.correo());
        ciudadano.setTelefono(request.telefono());
        ciudadano.setDomicilio(request.domicilio());
        ciudadano.setHabilitado(true);

        Ciudadano guardado = ciudadanoRepository.save(ciudadano);
        return CiudadanoResponse.from(guardado);
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
