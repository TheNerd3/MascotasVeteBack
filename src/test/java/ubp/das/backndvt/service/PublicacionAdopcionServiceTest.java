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
import org.springframework.security.access.AccessDeniedException;

import ubp.das.backndvt.dto.ActualizarPublicacionAdopcionRequest;
import ubp.das.backndvt.dto.CrearPublicacionAdopcionRequest;
import ubp.das.backndvt.dto.PublicacionAdopcionResponse;
import ubp.das.backndvt.entity.Ciudadano;
import ubp.das.backndvt.entity.Mascota;
import ubp.das.backndvt.entity.PublicacionAdopcion;
import ubp.das.backndvt.entity.Refugio;
import ubp.das.backndvt.exception.RecursoDuplicadoException;
import ubp.das.backndvt.exception.RecursoNoEncontradoException;
import ubp.das.backndvt.repository.MascotaRepository;
import ubp.das.backndvt.repository.PublicacionAdopcionRepository;
import ubp.das.backndvt.repository.RefugioRepository;

/**
 * Pruebas de PublicacionAdopcionService (RF13) con los repositorios
 * simulados (mock): no necesitan SQL Server para correr.
 */
class PublicacionAdopcionServiceTest {

    private static final Integer ID_REFUGIO = 5;
    private static final Integer ID_RESPONSABLE_REFUGIO = 1;
    private static final Integer NRM = 10;

    private PublicacionAdopcionRepository publicacionAdopcionRepository;
    private MascotaRepository mascotaRepository;
    private RefugioRepository refugioRepository;
    private MascotaService mascotaService;
    private PublicacionAdopcionService publicacionAdopcionService;

    @BeforeEach
    void prepararDependenciasSimuladas() {
        publicacionAdopcionRepository = mock(PublicacionAdopcionRepository.class);
        mascotaRepository = mock(MascotaRepository.class);
        refugioRepository = mock(RefugioRepository.class);
        mascotaService = mock(MascotaService.class);

        publicacionAdopcionService = new PublicacionAdopcionService(
                publicacionAdopcionRepository, mascotaRepository, refugioRepository, mascotaService);
    }

    private Refugio crearRefugio() {
        Ciudadano responsable = new Ciudadano();
        responsable.setIdCiudadano(ID_RESPONSABLE_REFUGIO);

        Refugio refugio = new Refugio();
        refugio.setIdRefugio(ID_REFUGIO);
        refugio.setResponsable(responsable);
        return refugio;
    }

    private Mascota crearMascota() {
        Mascota mascota = new Mascota();
        mascota.setNroRegMunicipal(NRM);
        mascota.setNombre("Firulais");
        return mascota;
    }

    @Test
    void crearUnaPublicacionParaUnaMascotaExistenteSinDuplicados() {
        when(refugioRepository.findById(ID_REFUGIO)).thenReturn(Optional.of(crearRefugio()));
        when(mascotaRepository.findById(NRM)).thenReturn(Optional.of(crearMascota()));
        when(publicacionAdopcionRepository.findByMascotaNroRegMunicipalAndEstadoPublicacion(NRM, PublicacionAdopcion.ESTADO_ACTIVA))
                .thenReturn(Optional.empty());
        when(publicacionAdopcionRepository.save(any())).thenAnswer(invocacion -> {
            PublicacionAdopcion publicacion = invocacion.getArgument(0);
            publicacion.setNroPublicacion(100);
            return publicacion;
        });

        CrearPublicacionAdopcionRequest request = new CrearPublicacionAdopcionRequest(
                NRM, null, "Mestizo, 2 años", "Con patio", null);

        PublicacionAdopcionResponse respuesta = publicacionAdopcionService.crear(ID_REFUGIO, request, ID_RESPONSABLE_REFUGIO);

        assertThat(respuesta.nroPublicacion()).isEqualTo(100);
        assertThat(respuesta.estadoPublicacion()).isEqualTo(PublicacionAdopcion.ESTADO_ACTIVA);
        verify(mascotaService, never()).registrar(any());
    }

    @Test
    void crearUnaPublicacionRegistrandoLaMascotaEnElMismoFlujo() {
        when(refugioRepository.findById(ID_REFUGIO)).thenReturn(Optional.of(crearRefugio()));
        when(mascotaService.registrar(any())).thenReturn(
                new ubp.das.backndvt.dto.RegistrarMascotaResultado(
                        new ubp.das.backndvt.dto.MascotaResponse(NRM, "Firulais", "M", (short) 2024, null, true, 1, ID_REFUGIO),
                        true));
        when(mascotaRepository.findById(NRM)).thenReturn(Optional.of(crearMascota()));
        when(publicacionAdopcionRepository.findByMascotaNroRegMunicipalAndEstadoPublicacion(NRM, PublicacionAdopcion.ESTADO_ACTIVA))
                .thenReturn(Optional.empty());
        when(publicacionAdopcionRepository.save(any())).thenAnswer(invocacion -> invocacion.getArgument(0));

        CrearPublicacionAdopcionRequest request = new CrearPublicacionAdopcionRequest(
                null,
                new ubp.das.backndvt.dto.RegistrarMascotaRequest(
                        "Firulais", "M", (short) 2024, null, null,
                        new ubp.das.backndvt.dto.DatosPropietarioRequest(
                                "Perez", "Ana", "20123456789", "clave123", null, null, null),
                        ID_REFUGIO, List.of()),
                "Mestizo",
                "Con patio",
                null);

        PublicacionAdopcionResponse respuesta = publicacionAdopcionService.crear(ID_REFUGIO, request, ID_RESPONSABLE_REFUGIO);

        assertThat(respuesta.nroRegMunicipal()).isEqualTo(NRM);
        verify(mascotaService).registrar(any());
    }

    @Test
    void crearUnaPublicacionParaUnaMascotaConPublicacionActivaRechaza() {
        when(refugioRepository.findById(ID_REFUGIO)).thenReturn(Optional.of(crearRefugio()));
        when(mascotaRepository.findById(NRM)).thenReturn(Optional.of(crearMascota()));
        when(publicacionAdopcionRepository.findByMascotaNroRegMunicipalAndEstadoPublicacion(NRM, PublicacionAdopcion.ESTADO_ACTIVA))
                .thenReturn(Optional.of(new PublicacionAdopcion()));

        CrearPublicacionAdopcionRequest request = new CrearPublicacionAdopcionRequest(NRM, null, null, null, null);

        assertThatThrownBy(() -> publicacionAdopcionService.crear(ID_REFUGIO, request, ID_RESPONSABLE_REFUGIO))
                .isInstanceOf(RecursoDuplicadoException.class);
    }

    @Test
    void crearUnaPublicacionSiendoOtroCiudadanoDistintoAlResponsableRechaza() {
        when(refugioRepository.findById(ID_REFUGIO)).thenReturn(Optional.of(crearRefugio()));

        CrearPublicacionAdopcionRequest request = new CrearPublicacionAdopcionRequest(NRM, null, null, null, null);

        assertThatThrownBy(() -> publicacionAdopcionService.crear(ID_REFUGIO, request, 99))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void crearUnaPublicacionParaUnRefugioInexistenteRechaza() {
        when(refugioRepository.findById(ID_REFUGIO)).thenReturn(Optional.empty());

        CrearPublicacionAdopcionRequest request = new CrearPublicacionAdopcionRequest(NRM, null, null, null, null);

        assertThatThrownBy(() -> publicacionAdopcionService.crear(ID_REFUGIO, request, ID_RESPONSABLE_REFUGIO))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void actualizarElEstadoDeUnaPublicacionExistenteFunciona() {
        PublicacionAdopcion publicacion = new PublicacionAdopcion();
        publicacion.setNroPublicacion(100);
        publicacion.setMascota(crearMascota());
        publicacion.setRefugio(crearRefugio());
        publicacion.setEstadoPublicacion(PublicacionAdopcion.ESTADO_ACTIVA);

        when(publicacionAdopcionRepository.findById(100)).thenReturn(Optional.of(publicacion));
        when(publicacionAdopcionRepository.save(any())).thenAnswer(invocacion -> invocacion.getArgument(0));

        ActualizarPublicacionAdopcionRequest request = new ActualizarPublicacionAdopcionRequest(PublicacionAdopcion.ESTADO_FINALIZADA);

        PublicacionAdopcionResponse respuesta =
                publicacionAdopcionService.actualizarEstado(100, request, ID_RESPONSABLE_REFUGIO);

        assertThat(respuesta.estadoPublicacion()).isEqualTo(PublicacionAdopcion.ESTADO_FINALIZADA);
    }

    @Test
    void reactivarUnaPublicacionCuandoYaHayOtraActivaParaLaMismaMascotaRechaza() {
        PublicacionAdopcion publicacion = new PublicacionAdopcion();
        publicacion.setNroPublicacion(100);
        publicacion.setMascota(crearMascota());
        publicacion.setRefugio(crearRefugio());
        publicacion.setEstadoPublicacion(PublicacionAdopcion.ESTADO_PAUSADA);

        when(publicacionAdopcionRepository.findById(100)).thenReturn(Optional.of(publicacion));
        when(publicacionAdopcionRepository.findByMascotaNroRegMunicipalAndEstadoPublicacion(NRM, PublicacionAdopcion.ESTADO_ACTIVA))
                .thenReturn(Optional.of(new PublicacionAdopcion()));

        ActualizarPublicacionAdopcionRequest request = new ActualizarPublicacionAdopcionRequest(PublicacionAdopcion.ESTADO_ACTIVA);

        assertThatThrownBy(() -> publicacionAdopcionService.actualizarEstado(100, request, ID_RESPONSABLE_REFUGIO))
                .isInstanceOf(RecursoDuplicadoException.class);
    }

    @Test
    void actualizarUnaPublicacionSiendoOtroCiudadanoDistintoAlResponsableRechaza() {
        PublicacionAdopcion publicacion = new PublicacionAdopcion();
        publicacion.setNroPublicacion(100);
        publicacion.setMascota(crearMascota());
        publicacion.setRefugio(crearRefugio());
        publicacion.setEstadoPublicacion(PublicacionAdopcion.ESTADO_ACTIVA);

        when(publicacionAdopcionRepository.findById(100)).thenReturn(Optional.of(publicacion));

        ActualizarPublicacionAdopcionRequest request = new ActualizarPublicacionAdopcionRequest(PublicacionAdopcion.ESTADO_FINALIZADA);

        assertThatThrownBy(() -> publicacionAdopcionService.actualizarEstado(100, request, 99))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void listarPublicacionesFiltrandoPorEstadoActiva() {
        PublicacionAdopcion publicacion = new PublicacionAdopcion();
        publicacion.setNroPublicacion(100);
        publicacion.setMascota(crearMascota());
        publicacion.setRefugio(crearRefugio());
        publicacion.setEstadoPublicacion(PublicacionAdopcion.ESTADO_ACTIVA);

        when(publicacionAdopcionRepository.findByEstadoPublicacion(PublicacionAdopcion.ESTADO_ACTIVA))
                .thenReturn(List.of(publicacion));

        List<PublicacionAdopcionResponse> respuesta = publicacionAdopcionService.listarPorEstado(PublicacionAdopcion.ESTADO_ACTIVA);

        assertThat(respuesta).hasSize(1);
        assertThat(respuesta.get(0).estadoPublicacion()).isEqualTo(PublicacionAdopcion.ESTADO_ACTIVA);
    }
}
