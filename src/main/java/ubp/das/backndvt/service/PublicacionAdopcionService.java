package ubp.das.backndvt.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ubp.das.backndvt.dto.ActualizarPublicacionAdopcionRequest;
import ubp.das.backndvt.dto.CrearPublicacionAdopcionRequest;
import ubp.das.backndvt.dto.PublicacionAdopcionResponse;
import ubp.das.backndvt.dto.RegistrarMascotaResultado;
import ubp.das.backndvt.entity.Mascota;
import ubp.das.backndvt.entity.PublicacionAdopcion;
import ubp.das.backndvt.entity.Refugio;
import ubp.das.backndvt.exception.RecursoDuplicadoException;
import ubp.das.backndvt.exception.RecursoNoEncontradoException;
import ubp.das.backndvt.repository.MascotaRepository;
import ubp.das.backndvt.repository.PublicacionAdopcionRepository;
import ubp.das.backndvt.repository.RefugioRepository;

/**
 * RF13 - Publicaciones de adopcion. Al crear, si la mascota todavia no
 * esta en el registro se la da de alta en el mismo flujo (reusa
 * MascotaService.registrar). Solo puede haber una publicacion Activa
 * por mascota (se valida antes de insertar, aunque tambien haya un
 * indice unico filtrado en la base).
 */
@Service
public class PublicacionAdopcionService {

    private final PublicacionAdopcionRepository publicacionAdopcionRepository;
    private final MascotaRepository mascotaRepository;
    private final RefugioRepository refugioRepository;
    private final MascotaService mascotaService;

    public PublicacionAdopcionService(
            PublicacionAdopcionRepository publicacionAdopcionRepository,
            MascotaRepository mascotaRepository,
            RefugioRepository refugioRepository,
            MascotaService mascotaService) {
        this.publicacionAdopcionRepository = publicacionAdopcionRepository;
        this.mascotaRepository = mascotaRepository;
        this.refugioRepository = refugioRepository;
        this.mascotaService = mascotaService;
    }

    @Transactional
    public PublicacionAdopcionResponse crear(Integer idRefugio, CrearPublicacionAdopcionRequest request, Integer idCiudadanoAutenticado) {
        Refugio refugio = refugioRepository.findById(idRefugio)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un refugio con id " + idRefugio));

        validarResponsableDelRefugio(refugio, idCiudadanoAutenticado);

        Mascota mascota = resolverMascota(request, idRefugio);

        publicacionAdopcionRepository.findByMascotaNroRegMunicipalAndEstadoPublicacion(
                mascota.getNroRegMunicipal(), PublicacionAdopcion.ESTADO_ACTIVA).ifPresent(existente -> {
                    throw new RecursoDuplicadoException(
                            "La mascota " + mascota.getNroRegMunicipal() + " ya tiene una publicacion de adopcion activa");
                });

        PublicacionAdopcion publicacion = new PublicacionAdopcion();
        publicacion.setMascota(mascota);
        publicacion.setRefugio(refugio);
        publicacion.setFechaPublicacion(LocalDate.now());
        publicacion.setCaracteristicasMascota(request.caracteristicasMascota());
        publicacion.setCondicionAdopcion(request.condicionAdopcion());
        publicacion.setFoto(request.foto());
        publicacion.setEstadoPublicacion(PublicacionAdopcion.ESTADO_ACTIVA);

        return PublicacionAdopcionResponse.from(publicacionAdopcionRepository.save(publicacion));
    }

    @Transactional
    public PublicacionAdopcionResponse actualizarEstado(
            Integer nroPublicacion, ActualizarPublicacionAdopcionRequest request, Integer idCiudadanoAutenticado) {
        PublicacionAdopcion publicacion = publicacionAdopcionRepository.findById(nroPublicacion)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe una publicacion con nro " + nroPublicacion));

        validarResponsableDelRefugio(publicacion.getRefugio(), idCiudadanoAutenticado);

        if (PublicacionAdopcion.ESTADO_ACTIVA.equals(request.estadoPublicacion())
                && !PublicacionAdopcion.ESTADO_ACTIVA.equals(publicacion.getEstadoPublicacion())) {
            publicacionAdopcionRepository.findByMascotaNroRegMunicipalAndEstadoPublicacion(
                    publicacion.getMascota().getNroRegMunicipal(), PublicacionAdopcion.ESTADO_ACTIVA).ifPresent(otra -> {
                        throw new RecursoDuplicadoException(
                                "La mascota " + publicacion.getMascota().getNroRegMunicipal()
                                        + " ya tiene otra publicacion de adopcion activa");
                    });
        }

        publicacion.setEstadoPublicacion(request.estadoPublicacion());
        return PublicacionAdopcionResponse.from(publicacionAdopcionRepository.save(publicacion));
    }

    public List<PublicacionAdopcionResponse> listarPorEstado(String estado) {
        List<PublicacionAdopcion> publicaciones = estado != null
                ? publicacionAdopcionRepository.findByEstadoPublicacion(estado)
                : publicacionAdopcionRepository.findAll();
        return publicaciones.stream().map(PublicacionAdopcionResponse::from).toList();
    }

    private Mascota resolverMascota(CrearPublicacionAdopcionRequest request, Integer idRefugio) {
        if (!request.requiereRegistrarMascota()) {
            return mascotaRepository.findById(request.nroRegMunicipal())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "No existe una mascota con nro_reg_municipal " + request.nroRegMunicipal()));
        }

        if (request.mascota() == null) {
            throw new IllegalArgumentException("Debe indicar nroRegMunicipal o los datos de la mascota a registrar");
        }

        RegistrarMascotaResultado resultado = mascotaService.registrar(request.mascota());
        return mascotaRepository.findById(resultado.mascota().nroRegMunicipal()).orElseThrow();
    }

    private void validarResponsableDelRefugio(Refugio refugio, Integer idCiudadanoAutenticado) {
        if (!refugio.getResponsable().getIdCiudadano().equals(idCiudadanoAutenticado)) {
            throw new AccessDeniedException("Solo el responsable del refugio puede gestionar sus publicaciones");
        }
    }
}
