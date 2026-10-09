package ubp.das.backndvt.service;

import java.time.LocalDate;

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
import ubp.das.backndvt.entity.EstadoPublicacion;
import ubp.das.backndvt.entity.Mascota;
import ubp.das.backndvt.entity.PublicacionAdopcion;
import ubp.das.backndvt.entity.Refugio;
import ubp.das.backndvt.exception.RecursoDuplicadoException;
import ubp.das.backndvt.exception.RecursoNoEncontradoException;
import ubp.das.backndvt.exception.TransicionEstadoInvalidaException;
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

    private final PublicacionAdopcionRepository publicacionAdopcionRepository;
    private final MascotaRepository mascotaRepository;
    private final MascotaService mascotaService;
    private final RefugioAutorizacionService refugioAutorizacionService;

    public PublicacionAdopcionService(
            PublicacionAdopcionRepository publicacionAdopcionRepository,
            MascotaRepository mascotaRepository,
            MascotaService mascotaService,
            RefugioAutorizacionService refugioAutorizacionService) {
        this.publicacionAdopcionRepository = publicacionAdopcionRepository;
        this.mascotaRepository = mascotaRepository;
        this.mascotaService = mascotaService;
        this.refugioAutorizacionService = refugioAutorizacionService;
    }

    @Transactional
    public PublicacionAdopcionResponse crear(CrearPublicacionAdopcionRequest request, AuthenticatedUser usuario) {
        Refugio refugio = refugioAutorizacionService.refugioAutenticado(usuario);

        Mascota mascota = resolverMascota(request, refugio);
        refugioAutorizacionService.validarMascotaDelRefugio(mascota, refugio);

        validarSinPublicacionActiva(mascota.getNroRegMunicipal());

        PublicacionAdopcion publicacion = new PublicacionAdopcion();
        publicacion.setNroRegMunicipal(mascota.getNroRegMunicipal());
        publicacion.setIdRefugio(refugio.getIdRefugio());
        publicacion.setFechaPublicacion(LocalDate.now());
        publicacion.setCaracteristicasMascota(request.caracteristicasMascota());
        publicacion.setCondicionAdopcion(request.condicionAdopcion());
        publicacion.setFoto(request.foto());
        publicacion.setEstadoPublicacion(EstadoPublicacion.ACTIVA);

        PublicacionAdopcion guardada = publicacionAdopcionRepository.save(publicacion);
        return publicacionAdopcionRepository.findResponseById(guardada.getNroPublicacion()).orElseThrow();
    }

    @Transactional
    public PublicacionAdopcionResponse cambiarEstado(
            Integer nroPublicacion, CambiarEstadoPublicacionRequest request, AuthenticatedUser usuario) {
        Refugio refugio = refugioAutorizacionService.refugioAutenticado(usuario);

        PublicacionAdopcion publicacion = publicacionAdopcionRepository.findById(nroPublicacion)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe una publicacion con nro " + nroPublicacion));

        if (!publicacion.getIdRefugio().equals(refugio.getIdRefugio())) {
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
            validarSinPublicacionActiva(publicacion.getNroRegMunicipal());
        }

        publicacion.setEstadoPublicacion(estadoDestino);
        publicacionAdopcionRepository.save(publicacion);
        return publicacionAdopcionRepository.findResponseById(nroPublicacion).orElseThrow();
    }

    @Transactional(readOnly = true)
    public Page<PublicacionAdopcionResponse> listarMisPublicaciones(
            EstadoPublicacion estado, AuthenticatedUser usuario, Pageable pageable) {
        Refugio refugio = refugioAutorizacionService.refugioAutenticado(usuario);
        return publicacionAdopcionRepository.findByRefugio(refugio.getIdRefugio(), estado, pageable);
    }

    /**
     * RF18 - Foto de una publicacion, endpoint publico (sin token: lo
     * consume un <img src>, que no manda Authorization). Solo expone la
     * foto si la publicacion esta Activa, para no mostrar fotos de
     * publicaciones Pausadas/Finalizadas a cualquiera que adivine el id.
     */
    @Transactional(readOnly = true)
    public byte[] obtenerFotoPublica(Integer nroPublicacion) {
        String estado = publicacionAdopcionRepository.findEstado(nroPublicacion)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe una publicacion con nro " + nroPublicacion));

        if (!EstadoPublicacion.ACTIVA.getNombreEnBase().equals(estado)) {
            throw new RecursoNoEncontradoException("La publicacion no esta activa");
        }

        return publicacionAdopcionRepository.findFoto(nroPublicacion)
                .orElseThrow(() -> new RecursoNoEncontradoException("La publicacion no tiene foto"));
    }

    /**
     * RF13 - Foto de una publicacion, para el refugio dueño: a
     * diferencia del endpoint publico (RF18), no filtra por estado
     * Activa, porque el refugio tiene que poder ver la foto de sus
     * publicaciones Pausadas y Finalizadas tambien.
     */
    @Transactional(readOnly = true)
    public byte[] obtenerFotoPropia(Integer nroPublicacion, AuthenticatedUser usuario) {
        Refugio refugio = refugioAutorizacionService.refugioAutenticado(usuario);
        Integer idRefugioDeLaPublicacion = publicacionAdopcionRepository.findIdRefugioById(nroPublicacion)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe una publicacion con nro " + nroPublicacion));

        if (!idRefugioDeLaPublicacion.equals(refugio.getIdRefugio())) {
            throw new AccessDeniedException("Solo el refugio que publico puede ver esta foto");
        }

        return publicacionAdopcionRepository.findFoto(nroPublicacion)
                .orElseThrow(() -> new RecursoNoEncontradoException("La publicacion no tiene foto"));
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
        publicacionAdopcionRepository.findActivaPorMascota(nroRegMunicipal)
                .ifPresent(existente -> {
                    throw new RecursoDuplicadoException(
                            "La mascota " + nroRegMunicipal + " ya tiene una publicacion de adopcion activa");
                });
    }
}
