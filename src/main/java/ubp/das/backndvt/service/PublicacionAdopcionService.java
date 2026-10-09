package ubp.das.backndvt.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ubp.das.backndvt.dto.CambiarEstadoPublicacionRequest;
import ubp.das.backndvt.dto.CrearPublicacionAdopcionRequest;
import ubp.das.backndvt.dto.PublicacionAdopcionResponse;
import ubp.das.backndvt.dto.RegistrarMascotaRequest;
import ubp.das.backndvt.dto.RegistrarMascotaResultado;
import ubp.das.backndvt.entity.CaracteristicaMascota;
import ubp.das.backndvt.entity.EstadoPublicacion;
import ubp.das.backndvt.entity.Mascota;
import ubp.das.backndvt.entity.PublicacionAdopcion;
import ubp.das.backndvt.entity.Refugio;
import ubp.das.backndvt.exception.RecursoDuplicadoException;
import ubp.das.backndvt.exception.RecursoNoEncontradoException;
import ubp.das.backndvt.exception.TransicionEstadoInvalidaException;
import ubp.das.backndvt.repository.CaracteristicaMascotaRepository;
import ubp.das.backndvt.repository.MascotaRepository;
import ubp.das.backndvt.repository.PublicacionAdopcionRepository;
import ubp.das.backndvt.security.AuthenticatedUser;
import ubp.das.backndvt.security.RefugioAutorizacionService;

/**
 * RF13 - Publicaciones de adopcion del refugio autenticado. El refugio
 * sale siempre del JWT (RefugioAutorizacionService), nunca del body ni
 * del path: antes se recibia por path y se validaba contra el
 * responsable despues, lo que es mas fragil.
 *
 * Al crear, si la mascota todavia no esta en el registro se la da de
 * alta en el mismo flujo (reusa MascotaService.registrar), forzando
 * el refugio del JWT. Solo puede haber una publicacion Activa por
 * mascota (se valida antes de insertar, aunque tambien haya un indice
 * unico filtrado en la base).
 */
@Service
public class PublicacionAdopcionService {

    private static final String RASGO_ESPECIE = "Especie";
    private static final String RASGO_RAZA = "Raza";

    private final PublicacionAdopcionRepository publicacionAdopcionRepository;
    private final MascotaRepository mascotaRepository;
    private final MascotaService mascotaService;
    private final RefugioAutorizacionService refugioAutorizacionService;
    private final CaracteristicaMascotaRepository caracteristicaMascotaRepository;

    public PublicacionAdopcionService(
            PublicacionAdopcionRepository publicacionAdopcionRepository,
            MascotaRepository mascotaRepository,
            MascotaService mascotaService,
            RefugioAutorizacionService refugioAutorizacionService,
            CaracteristicaMascotaRepository caracteristicaMascotaRepository) {
        this.publicacionAdopcionRepository = publicacionAdopcionRepository;
        this.mascotaRepository = mascotaRepository;
        this.mascotaService = mascotaService;
        this.refugioAutorizacionService = refugioAutorizacionService;
        this.caracteristicaMascotaRepository = caracteristicaMascotaRepository;
    }

    @Transactional
    public PublicacionAdopcionResponse crear(CrearPublicacionAdopcionRequest request, AuthenticatedUser usuario) {
        Refugio refugio = refugioAutorizacionService.refugioAutenticado(usuario);

        Mascota mascota = resolverMascota(request, refugio);
        refugioAutorizacionService.validarMascotaDelRefugio(mascota, refugio);

        validarSinPublicacionActiva(mascota.getNroRegMunicipal());

        PublicacionAdopcion publicacion = new PublicacionAdopcion();
        publicacion.setMascota(mascota);
        publicacion.setRefugio(refugio);
        publicacion.setFechaPublicacion(LocalDate.now());
        publicacion.setCaracteristicasMascota(request.caracteristicasMascota());
        publicacion.setCondicionAdopcion(request.condicionAdopcion());
        publicacion.setFoto(request.foto());
        publicacion.setEstadoPublicacion(EstadoPublicacion.ACTIVA);

        return toResponse(publicacionAdopcionRepository.save(publicacion));
    }

    @Transactional
    public PublicacionAdopcionResponse cambiarEstado(
            Integer nroPublicacion, CambiarEstadoPublicacionRequest request, AuthenticatedUser usuario) {
        Refugio refugio = refugioAutorizacionService.refugioAutenticado(usuario);

        PublicacionAdopcion publicacion = publicacionAdopcionRepository.findById(nroPublicacion)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe una publicacion con nro " + nroPublicacion));

        if (!publicacion.getRefugio().getIdRefugio().equals(refugio.getIdRefugio())) {
            throw new AccessDeniedException("Solo el refugio que publico puede cambiar su estado");
        }

        EstadoPublicacion.Accion accion = request.accion();
        EstadoPublicacion estadoActual = publicacion.getEstadoPublicacion();

        if (!estadoActual.permite(accion)) {
            throw new TransicionEstadoInvalidaException(
                    "No se puede aplicar " + accion + " a una publicacion en estado " + estadoActual.getNombreEnBase());
        }

        EstadoPublicacion estadoDestino = accion.getEstadoDestino();
        if (estadoDestino == EstadoPublicacion.ACTIVA) {
            validarSinPublicacionActiva(publicacion.getMascota().getNroRegMunicipal());
        }

        publicacion.setEstadoPublicacion(estadoDestino);
        return toResponse(publicacionAdopcionRepository.save(publicacion));
    }

    @Transactional(readOnly = true)
    public Page<PublicacionAdopcionResponse> listarMisPublicaciones(
            EstadoPublicacion estado, AuthenticatedUser usuario, Pageable pageable) {
        Refugio refugio = refugioAutorizacionService.refugioAutenticado(usuario);

        Page<PublicacionAdopcion> publicaciones = estado != null
                ? publicacionAdopcionRepository.findByRefugioIdRefugioAndEstadoPublicacion(refugio.getIdRefugio(), estado, pageable)
                : publicacionAdopcionRepository.findByRefugioIdRefugio(refugio.getIdRefugio(), pageable);

        return publicaciones.map(this::toResponse);
    }

    /**
     * Arma el DTO de una publicacion resolviendo especie/raza de la
     * mascota desde caracteristicas_mascotas. Publico porque
     * RefugioService (RF18, listado publico) tambien arma este DTO.
     */
    public PublicacionAdopcionResponse toResponse(PublicacionAdopcion publicacion) {
        List<CaracteristicaMascota> caracteristicas =
                caracteristicaMascotaRepository.findByNroRegMunicipal(publicacion.getMascota().getNroRegMunicipal());

        String especie = valorDeRasgo(caracteristicas, RASGO_ESPECIE);
        String raza = valorDeRasgo(caracteristicas, RASGO_RAZA);

        return PublicacionAdopcionResponse.from(publicacion, especie, raza);
    }

    private String valorDeRasgo(List<CaracteristicaMascota> caracteristicas, String nombreRasgo) {
        return caracteristicas.stream()
                .filter(caracteristica -> nombreRasgo.equals(caracteristica.getDominioRasgo().getRasgo().getNomRasgo()))
                .map(caracteristica -> caracteristica.getDominioRasgo().getNomValorDominio())
                .findFirst()
                .orElse(null);
    }

    private Mascota resolverMascota(CrearPublicacionAdopcionRequest request, Refugio refugio) {
        if (!request.requiereRegistrarMascota()) {
            return mascotaRepository.findById(request.nroRegMunicipal())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "No existe una mascota con nro_reg_municipal " + request.nroRegMunicipal()));
        }

        if (request.mascota() == null) {
            throw new IllegalArgumentException("Debe indicar nroRegMunicipal o los datos de la mascota a registrar");
        }

        // El refugio del body (si vino) se ignora: el dueño de la mascota
        // que se da de alta en este flujo es siempre el refugio autenticado.
        RegistrarMascotaRequest conRefugioForzado = forzarRefugio(request.mascota(), refugio);
        RegistrarMascotaResultado resultado = mascotaService.registrar(conRefugioForzado);
        return mascotaRepository.findById(resultado.mascota().nroRegMunicipal()).orElseThrow();
    }

    /**
     * Si el formulario de alta (RF13) no manda idResponsable ni
     * propietario, la mascota del refugio queda a cargo del mismo
     * responsable legal del refugio autenticado: no tiene sentido
     * pedirle al refugio los datos de un "propietario" para una
     * mascota que es suya.
     */
    private RegistrarMascotaRequest forzarRefugio(RegistrarMascotaRequest original, Refugio refugio) {
        boolean sinResponsable = original.idResponsable() == null && original.propietario() == null;
        Integer idResponsable = sinResponsable ? refugio.getResponsable().getIdCiudadano() : original.idResponsable();

        return new RegistrarMascotaRequest(
                original.nombre(),
                original.sexo(),
                original.anioNacimiento(),
                original.microchip(),
                idResponsable,
                original.propietario(),
                refugio.getIdRefugio(),
                original.caracteristicas());
    }

    private void validarSinPublicacionActiva(Integer nroRegMunicipal) {
        publicacionAdopcionRepository
                .findByMascotaNroRegMunicipalAndEstadoPublicacion(nroRegMunicipal, EstadoPublicacion.ACTIVA)
                .ifPresent(existente -> {
                    throw new RecursoDuplicadoException(
                            "La mascota " + nroRegMunicipal + " ya tiene una publicacion de adopcion activa");
                });
    }
}
