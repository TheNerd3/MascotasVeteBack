package ubp.das.backndvt.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import ubp.das.backndvt.dto.CambiarEstadoPublicacionRequest;
import ubp.das.backndvt.dto.CrearPublicacionAdopcionRequest;
import ubp.das.backndvt.dto.DatosPropietarioRequest;
import ubp.das.backndvt.dto.MascotaResponse;
import ubp.das.backndvt.dto.PublicacionAdopcionResponse;
import ubp.das.backndvt.dto.RegistrarMascotaRequest;
import ubp.das.backndvt.dto.RegistrarMascotaResultado;
import ubp.das.backndvt.entity.Ciudadano;
import ubp.das.backndvt.entity.EstadoPublicacion;
import ubp.das.backndvt.entity.EstadoPublicacion.Accion;
import ubp.das.backndvt.entity.Mascota;
import ubp.das.backndvt.entity.PublicacionAdopcion;
import ubp.das.backndvt.entity.Refugio;
import ubp.das.backndvt.exception.RecursoDuplicadoException;
import ubp.das.backndvt.exception.RecursoNoEncontradoException;
import ubp.das.backndvt.exception.TransicionEstadoInvalidaException;
import ubp.das.backndvt.repository.MascotaRepository;
import ubp.das.backndvt.repository.PublicacionAdopcionRepository;
import ubp.das.backndvt.repository.RefugioRepository;
import ubp.das.backndvt.security.AuthenticatedUser;
import ubp.das.backndvt.security.RefugioAutorizacionService;

/**
 * Pruebas de PublicacionAdopcionService (RF13) con los repositorios
 * simulados (mock): no necesitan SQL Server para correr. El refugio
 * del usuario autenticado se resuelve siempre a traves de
 * RefugioAutorizacionService (no se mockea aca con un mock generico:
 * se usa la implementacion real contra un RefugioRepository mockeado,
 * para probar la integracion real entre ambos).
 */
class PublicacionAdopcionServiceTest {

    private static final Integer ID_REFUGIO = 5;
    private static final Integer ID_RESPONSABLE_REFUGIO = 1;
    private static final Integer NRM = 10;

    private PublicacionAdopcionRepository publicacionAdopcionRepository;
    private MascotaRepository mascotaRepository;
    private RefugioRepository refugioRepository;
    private MascotaService mascotaService;
    private RefugioAutorizacionService refugioAutorizacionService;
    private PublicacionAdopcionService publicacionAdopcionService;

    @BeforeEach
    void prepararDependenciasSimuladas() {
        publicacionAdopcionRepository = mock(PublicacionAdopcionRepository.class);
        mascotaRepository = mock(MascotaRepository.class);
        refugioRepository = mock(RefugioRepository.class);
        mascotaService = mock(MascotaService.class);
        refugioAutorizacionService = new RefugioAutorizacionService(refugioRepository);

        publicacionAdopcionService = new PublicacionAdopcionService(
                publicacionAdopcionRepository, mascotaRepository, mascotaService, refugioAutorizacionService);
    }

    private Refugio crearRefugio() {
        Ciudadano responsable = new Ciudadano();
        responsable.setIdCiudadano(ID_RESPONSABLE_REFUGIO);

        Refugio refugio = new Refugio();
        refugio.setIdRefugio(ID_REFUGIO);
        refugio.setResponsable(responsable);
        return refugio;
    }

    private Mascota crearMascota(Refugio refugio) {
        Mascota mascota = new Mascota();
        mascota.setNroRegMunicipal(NRM);
        mascota.setNombre("Firulais");
        mascota.setRefugio(refugio);
        return mascota;
    }

    private AuthenticatedUser usuarioRefugio() {
        return new AuthenticatedUser(ID_RESPONSABLE_REFUGIO, "20123456789", "REFUGIO", ID_REFUGIO);
    }

    private PublicacionAdopcionResponse respuestaDe(Integer nroPublicacion, EstadoPublicacion estado) {
        return new PublicacionAdopcionResponse(
                nroPublicacion, NRM, "Firulais", "M", (short) 2024, null, null, ID_REFUGIO,
                java.time.LocalDate.now(), null, null, estado, estado.accionesDisponibles(), false);
    }

    @Test
    void crearUnaPublicacionParaUnaMascotaExistenteSinDuplicados() {
        Refugio refugio = crearRefugio();
        when(refugioRepository.findById(ID_REFUGIO)).thenReturn(Optional.of(refugio));
        when(mascotaRepository.findById(NRM)).thenReturn(Optional.of(crearMascota(refugio)));
        when(publicacionAdopcionRepository.findActivaPorMascota(NRM)).thenReturn(Optional.empty());
        when(publicacionAdopcionRepository.save(any())).thenAnswer(invocacion -> {
            PublicacionAdopcion publicacion = invocacion.getArgument(0);
            publicacion.setNroPublicacion(100);
            return publicacion;
        });
        when(publicacionAdopcionRepository.findResponseById(100))
                .thenReturn(Optional.of(respuestaDe(100, EstadoPublicacion.ACTIVA)));

        CrearPublicacionAdopcionRequest request = new CrearPublicacionAdopcionRequest(
                NRM, null, "Mestizo, 2 años", "Con patio", null);

        PublicacionAdopcionResponse respuesta = publicacionAdopcionService.crear(request, usuarioRefugio());

        assertThat(respuesta.nroPublicacion()).isEqualTo(100);
        assertThat(respuesta.estadoPublicacion()).isEqualTo(EstadoPublicacion.ACTIVA);
        assertThat(respuesta.accionesDisponibles()).containsExactlyInAnyOrder(Accion.PAUSAR, Accion.FINALIZAR);
        verify(mascotaService, never()).registrar(any());
    }

    @Test
    void crearUnaPublicacionRegistrandoLaMascotaEnElMismoFlujo() {
        Refugio refugio = crearRefugio();
        when(refugioRepository.findById(ID_REFUGIO)).thenReturn(Optional.of(refugio));
        when(mascotaService.registrar(any())).thenReturn(
                new RegistrarMascotaResultado(
                        new MascotaResponse(NRM, "Firulais", "M", (short) 2024, null, true, 1, ID_REFUGIO),
                        true));
        when(mascotaRepository.findById(NRM)).thenReturn(Optional.of(crearMascota(refugio)));
        when(publicacionAdopcionRepository.findActivaPorMascota(NRM)).thenReturn(Optional.empty());
        when(publicacionAdopcionRepository.save(any())).thenAnswer(invocacion -> {
            PublicacionAdopcion publicacion = invocacion.getArgument(0);
            publicacion.setNroPublicacion(100);
            return publicacion;
        });
        when(publicacionAdopcionRepository.findResponseById(100))
                .thenReturn(Optional.of(respuestaDe(100, EstadoPublicacion.ACTIVA)));

        CrearPublicacionAdopcionRequest request = new CrearPublicacionAdopcionRequest(
                null,
                new RegistrarMascotaRequest(
                        "Firulais", "M", (short) 2024, null, null,
                        new DatosPropietarioRequest("Perez", "Ana", "20123456789", "clave123", null, null, null),
                        999, // idRefugio que manda el cliente: debe ser ignorado y forzado al del JWT
                        List.of()),
                "Mestizo",
                "Con patio",
                null);

        PublicacionAdopcionResponse respuesta = publicacionAdopcionService.crear(request, usuarioRefugio());

        assertThat(respuesta.nroRegMunicipal()).isEqualTo(NRM);
        verify(mascotaService).registrar(
                org.mockito.ArgumentMatchers.argThat(mascotaRequest -> mascotaRequest.idRefugio().equals(ID_REFUGIO)));
    }

    @Test
    void crearUnaPublicacionParaUnaMascotaDeOtroRefugioRechaza() {
        Refugio refugio = crearRefugio();
        Refugio otroRefugio = new Refugio();
        otroRefugio.setIdRefugio(999);

        when(refugioRepository.findById(ID_REFUGIO)).thenReturn(Optional.of(refugio));
        when(mascotaRepository.findById(NRM)).thenReturn(Optional.of(crearMascota(otroRefugio)));

        CrearPublicacionAdopcionRequest request = new CrearPublicacionAdopcionRequest(NRM, null, null, null, null);

        assertThatThrownBy(() -> publicacionAdopcionService.crear(request, usuarioRefugio()))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void crearUnaPublicacionParaUnaMascotaConPublicacionActivaRechaza() {
        Refugio refugio = crearRefugio();
        when(refugioRepository.findById(ID_REFUGIO)).thenReturn(Optional.of(refugio));
        when(mascotaRepository.findById(NRM)).thenReturn(Optional.of(crearMascota(refugio)));
        when(publicacionAdopcionRepository.findActivaPorMascota(NRM))
                .thenReturn(Optional.of(new PublicacionAdopcion()));

        CrearPublicacionAdopcionRequest request = new CrearPublicacionAdopcionRequest(NRM, null, null, null, null);

        assertThatThrownBy(() -> publicacionAdopcionService.crear(request, usuarioRefugio()))
                .isInstanceOf(RecursoDuplicadoException.class);
    }

    @Test
    void crearUnaPublicacionSinSerResponsableDeNingunRefugioRechaza() {
        AuthenticatedUser usuarioSinRefugio = new AuthenticatedUser(99, "20999999999", "CIUDADANO", null);
        CrearPublicacionAdopcionRequest request = new CrearPublicacionAdopcionRequest(NRM, null, null, null, null);

        assertThatThrownBy(() -> publicacionAdopcionService.crear(request, usuarioSinRefugio))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void crearUnaPublicacionParaUnRefugioInexistenteRechaza() {
        when(refugioRepository.findById(ID_REFUGIO)).thenReturn(Optional.empty());

        CrearPublicacionAdopcionRequest request = new CrearPublicacionAdopcionRequest(NRM, null, null, null, null);

        assertThatThrownBy(() -> publicacionAdopcionService.crear(request, usuarioRefugio()))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    private PublicacionAdopcion publicacionConEstado(EstadoPublicacion estado) {
        PublicacionAdopcion publicacion = new PublicacionAdopcion();
        publicacion.setNroPublicacion(100);
        publicacion.setNroRegMunicipal(NRM);
        publicacion.setIdRefugio(ID_REFUGIO);
        publicacion.setEstadoPublicacion(estado);
        return publicacion;
    }

    @Test
    void finalizarUnaPublicacionActivaFunciona() {
        Refugio refugio = crearRefugio();
        PublicacionAdopcion publicacion = publicacionConEstado(EstadoPublicacion.ACTIVA);

        when(refugioRepository.findById(ID_REFUGIO)).thenReturn(Optional.of(refugio));
        when(publicacionAdopcionRepository.findById(100)).thenReturn(Optional.of(publicacion));
        when(publicacionAdopcionRepository.findResponseById(100))
                .thenReturn(Optional.of(respuestaDe(100, EstadoPublicacion.FINALIZADA)));

        CambiarEstadoPublicacionRequest request = new CambiarEstadoPublicacionRequest(Accion.FINALIZAR);

        PublicacionAdopcionResponse respuesta = publicacionAdopcionService.cambiarEstado(100, request, usuarioRefugio());

        assertThat(respuesta.estadoPublicacion()).isEqualTo(EstadoPublicacion.FINALIZADA);
        assertThat(respuesta.accionesDisponibles()).isEmpty();
    }

    @Test
    void pausarUnaPublicacionActivaFunciona() {
        Refugio refugio = crearRefugio();
        PublicacionAdopcion publicacion = publicacionConEstado(EstadoPublicacion.ACTIVA);

        when(refugioRepository.findById(ID_REFUGIO)).thenReturn(Optional.of(refugio));
        when(publicacionAdopcionRepository.findById(100)).thenReturn(Optional.of(publicacion));
        when(publicacionAdopcionRepository.findResponseById(100))
                .thenReturn(Optional.of(respuestaDe(100, EstadoPublicacion.PAUSADA)));

        CambiarEstadoPublicacionRequest request = new CambiarEstadoPublicacionRequest(Accion.PAUSAR);

        PublicacionAdopcionResponse respuesta = publicacionAdopcionService.cambiarEstado(100, request, usuarioRefugio());

        assertThat(respuesta.estadoPublicacion()).isEqualTo(EstadoPublicacion.PAUSADA);
        assertThat(respuesta.accionesDisponibles()).containsExactlyInAnyOrder(Accion.ACTIVAR, Accion.FINALIZAR);
    }

    @Test
    void reactivarUnaPublicacionPausadaSinOtraActivaFunciona() {
        Refugio refugio = crearRefugio();
        PublicacionAdopcion publicacion = publicacionConEstado(EstadoPublicacion.PAUSADA);

        when(refugioRepository.findById(ID_REFUGIO)).thenReturn(Optional.of(refugio));
        when(publicacionAdopcionRepository.findById(100)).thenReturn(Optional.of(publicacion));
        when(publicacionAdopcionRepository.findActivaPorMascota(NRM)).thenReturn(Optional.empty());
        when(publicacionAdopcionRepository.findResponseById(100))
                .thenReturn(Optional.of(respuestaDe(100, EstadoPublicacion.ACTIVA)));

        CambiarEstadoPublicacionRequest request = new CambiarEstadoPublicacionRequest(Accion.ACTIVAR);

        PublicacionAdopcionResponse respuesta = publicacionAdopcionService.cambiarEstado(100, request, usuarioRefugio());

        assertThat(respuesta.estadoPublicacion()).isEqualTo(EstadoPublicacion.ACTIVA);
        assertThat(respuesta.accionesDisponibles()).containsExactlyInAnyOrder(Accion.PAUSAR, Accion.FINALIZAR);
    }

    @Test
    void pausarUnaPublicacionYaPausadaRechazaPorTransicionInvalida() {
        Refugio refugio = crearRefugio();
        PublicacionAdopcion publicacion = publicacionConEstado(EstadoPublicacion.PAUSADA);

        when(refugioRepository.findById(ID_REFUGIO)).thenReturn(Optional.of(refugio));
        when(publicacionAdopcionRepository.findById(100)).thenReturn(Optional.of(publicacion));

        CambiarEstadoPublicacionRequest request = new CambiarEstadoPublicacionRequest(Accion.PAUSAR);

        assertThatThrownBy(() -> publicacionAdopcionService.cambiarEstado(100, request, usuarioRefugio()))
                .isInstanceOf(TransicionEstadoInvalidaException.class);
    }

    @Test
    void activarUnaPublicacionYaActivaRechazaPorTransicionInvalida() {
        Refugio refugio = crearRefugio();
        PublicacionAdopcion publicacion = publicacionConEstado(EstadoPublicacion.ACTIVA);

        when(refugioRepository.findById(ID_REFUGIO)).thenReturn(Optional.of(refugio));
        when(publicacionAdopcionRepository.findById(100)).thenReturn(Optional.of(publicacion));

        CambiarEstadoPublicacionRequest request = new CambiarEstadoPublicacionRequest(Accion.ACTIVAR);

        assertThatThrownBy(() -> publicacionAdopcionService.cambiarEstado(100, request, usuarioRefugio()))
                .isInstanceOf(TransicionEstadoInvalidaException.class);
    }

    @Test
    void activarUnaPublicacionFinalizadaRechazaPorTransicionInvalida() {
        Refugio refugio = crearRefugio();
        PublicacionAdopcion publicacion = publicacionConEstado(EstadoPublicacion.FINALIZADA);

        when(refugioRepository.findById(ID_REFUGIO)).thenReturn(Optional.of(refugio));
        when(publicacionAdopcionRepository.findById(100)).thenReturn(Optional.of(publicacion));

        CambiarEstadoPublicacionRequest request = new CambiarEstadoPublicacionRequest(Accion.ACTIVAR);

        assertThatThrownBy(() -> publicacionAdopcionService.cambiarEstado(100, request, usuarioRefugio()))
                .isInstanceOf(TransicionEstadoInvalidaException.class);
    }

    @Test
    void finalizarUnaPublicacionFinalizadaRechazaPorTransicionInvalida() {
        Refugio refugio = crearRefugio();
        PublicacionAdopcion publicacion = publicacionConEstado(EstadoPublicacion.FINALIZADA);

        when(refugioRepository.findById(ID_REFUGIO)).thenReturn(Optional.of(refugio));
        when(publicacionAdopcionRepository.findById(100)).thenReturn(Optional.of(publicacion));

        CambiarEstadoPublicacionRequest request = new CambiarEstadoPublicacionRequest(Accion.FINALIZAR);

        assertThatThrownBy(() -> publicacionAdopcionService.cambiarEstado(100, request, usuarioRefugio()))
                .isInstanceOf(TransicionEstadoInvalidaException.class);
    }

    @Test
    void reactivarUnaPublicacionCuandoYaHayOtraActivaParaLaMismaMascotaRechaza() {
        Refugio refugio = crearRefugio();
        PublicacionAdopcion publicacion = publicacionConEstado(EstadoPublicacion.PAUSADA);

        when(refugioRepository.findById(ID_REFUGIO)).thenReturn(Optional.of(refugio));
        when(publicacionAdopcionRepository.findById(100)).thenReturn(Optional.of(publicacion));
        when(publicacionAdopcionRepository.findActivaPorMascota(NRM))
                .thenReturn(Optional.of(new PublicacionAdopcion()));

        CambiarEstadoPublicacionRequest request = new CambiarEstadoPublicacionRequest(Accion.ACTIVAR);

        assertThatThrownBy(() -> publicacionAdopcionService.cambiarEstado(100, request, usuarioRefugio()))
                .isInstanceOf(RecursoDuplicadoException.class);
    }

    @Test
    void cambiarEstadoSiendoOtroRefugioDistintoAlDuenioDeLaPublicacionRechaza() {
        Refugio otroRefugio = new Refugio();
        otroRefugio.setIdRefugio(777);
        Ciudadano otroResponsable = new Ciudadano();
        otroResponsable.setIdCiudadano(55);
        otroRefugio.setResponsable(otroResponsable);

        PublicacionAdopcion publicacion = publicacionConEstado(EstadoPublicacion.ACTIVA);

        AuthenticatedUser usuarioOtroRefugio = new AuthenticatedUser(55, "20555555555", "REFUGIO", 777);
        when(refugioRepository.findById(777)).thenReturn(Optional.of(otroRefugio));
        when(publicacionAdopcionRepository.findById(100)).thenReturn(Optional.of(publicacion));

        CambiarEstadoPublicacionRequest request = new CambiarEstadoPublicacionRequest(Accion.FINALIZAR);

        assertThatThrownBy(() -> publicacionAdopcionService.cambiarEstado(100, request, usuarioOtroRefugio))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void listarMisPublicacionesFiltrandoPorEstadoActivaYPaginado() {
        Refugio refugio = crearRefugio();
        Pageable pageable = PageRequest.of(0, 10);
        Page<PublicacionAdopcionResponse> pagina =
                new PageImpl<>(List.of(respuestaDe(100, EstadoPublicacion.ACTIVA)), pageable, 1);

        when(refugioRepository.findById(ID_REFUGIO)).thenReturn(Optional.of(refugio));
        when(publicacionAdopcionRepository.findByRefugio(ID_REFUGIO, EstadoPublicacion.ACTIVA, pageable))
                .thenReturn(pagina);

        Page<PublicacionAdopcionResponse> respuesta =
                publicacionAdopcionService.listarMisPublicaciones(EstadoPublicacion.ACTIVA, usuarioRefugio(), pageable);

        assertThat(respuesta.getTotalElements()).isEqualTo(1);
        assertThat(respuesta.getContent().get(0).estadoPublicacion()).isEqualTo(EstadoPublicacion.ACTIVA);
    }

    @Test
    void listarMisPublicacionesSinFiltroDeEstado() {
        Refugio refugio = crearRefugio();
        Pageable pageable = PageRequest.of(0, 10);
        Page<PublicacionAdopcionResponse> pagina = new PageImpl<>(List.of(), pageable, 0);

        when(refugioRepository.findById(ID_REFUGIO)).thenReturn(Optional.of(refugio));
        when(publicacionAdopcionRepository.findByRefugio(ID_REFUGIO, null, pageable)).thenReturn(pagina);

        Page<PublicacionAdopcionResponse> respuesta =
                publicacionAdopcionService.listarMisPublicaciones(null, usuarioRefugio(), pageable);

        assertThat(respuesta.getTotalElements()).isZero();
    }

    @Test
    void obtenerFotoPropiaSiendoElRefugioDuenioFunciona() {
        Refugio refugio = crearRefugio();
        when(refugioRepository.findById(ID_REFUGIO)).thenReturn(Optional.of(refugio));
        when(publicacionAdopcionRepository.findIdRefugioById(100)).thenReturn(Optional.of(ID_REFUGIO));
        when(publicacionAdopcionRepository.findFoto(100)).thenReturn(Optional.of(new byte[] { 1, 2, 3 }));

        byte[] foto = publicacionAdopcionService.obtenerFotoPropia(100, usuarioRefugio());

        assertThat(foto).containsExactly(1, 2, 3);
    }

    @Test
    void obtenerFotoPropiaSiendoOtroRefugioRechaza() {
        Refugio refugio = crearRefugio();
        when(refugioRepository.findById(ID_REFUGIO)).thenReturn(Optional.of(refugio));
        when(publicacionAdopcionRepository.findIdRefugioById(100)).thenReturn(Optional.of(999));

        assertThatThrownBy(() -> publicacionAdopcionService.obtenerFotoPropia(100, usuarioRefugio()))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void obtenerFotoPublicaDeUnaPublicacionNoActivaRechazaConRecursoNoEncontrado() {
        when(publicacionAdopcionRepository.findEstado(100))
                .thenReturn(Optional.of(EstadoPublicacion.PAUSADA.getNombreEnBase()));

        assertThatThrownBy(() -> publicacionAdopcionService.obtenerFotoPublica(100))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void obtenerFotoPublicaDeUnaPublicacionActivaFunciona() {
        when(publicacionAdopcionRepository.findEstado(100))
                .thenReturn(Optional.of(EstadoPublicacion.ACTIVA.getNombreEnBase()));
        when(publicacionAdopcionRepository.findFoto(100)).thenReturn(Optional.of(new byte[] { 9 }));

        byte[] foto = publicacionAdopcionService.obtenerFotoPublica(100);

        assertThat(foto).containsExactly(9);
    }
}
