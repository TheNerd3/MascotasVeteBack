package ubp.das.backndvt.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import ubp.das.backndvt.dto.CarnetSanitarioResponse;
import ubp.das.backndvt.dto.ValidarCarnetResponse;
import ubp.das.backndvt.entity.Ciudadano;
import ubp.das.backndvt.entity.Mascota;
import ubp.das.backndvt.exception.RecursoNoEncontradoException;
import ubp.das.backndvt.repository.CarnetSanitarioFila;
import ubp.das.backndvt.repository.CarnetSanitarioRepository;
import ubp.das.backndvt.repository.MascotaRepository;
import ubp.das.backndvt.security.CarnetTokenService;

/**
 * Pruebas de CarnetSanitarioService (RF10/RF11) con los repositorios
 * simulados (mock): no necesitan SQL Server para correr.
 */
class CarnetSanitarioServiceTest {

    private static final Integer NRM = 10;
    private static final Integer ID_RESPONSABLE = 1;

    private MascotaRepository mascotaRepository;
    private CarnetSanitarioRepository carnetSanitarioRepository;
    private CarnetTokenService carnetTokenService;
    private CarnetSanitarioService carnetSanitarioService;

    @BeforeEach
    void prepararDependenciasSimuladas() {
        mascotaRepository = mock(MascotaRepository.class);
        carnetSanitarioRepository = mock(CarnetSanitarioRepository.class);
        carnetTokenService = new CarnetTokenService("clave-de-prueba-de-al-menos-32-caracteres", 365);

        carnetSanitarioService = new CarnetSanitarioService(mascotaRepository, carnetSanitarioRepository, carnetTokenService);
    }

    private Mascota crearMascotaConResponsable(Integer idResponsable) {
        Ciudadano responsable = new Ciudadano();
        responsable.setIdCiudadano(idResponsable);

        Mascota mascota = new Mascota();
        mascota.setNroRegMunicipal(NRM);
        mascota.setNombre("Firulais");
        mascota.setResponsable(responsable);
        return mascota;
    }

    @Test
    void elResponsableDeLaMascotaPuedeVerSuCarnetConAtenciones() {
        when(mascotaRepository.findById(NRM)).thenReturn(Optional.of(crearMascotaConResponsable(ID_RESPONSABLE)));

        CarnetSanitarioFila filaConAtencion = new CarnetSanitarioFila(
                NRM, "Firulais", "Ana", "Perez", "20123456789",
                1, LocalDate.of(2026, 1, 10), "Vacunación", "Antirrábica", null, "Vet Central");
        when(carnetSanitarioRepository.buscarPorMascota(NRM)).thenReturn(List.of(filaConAtencion));

        CarnetSanitarioResponse carnet = carnetSanitarioService.obtenerCarnet(NRM, ID_RESPONSABLE);

        assertThat(carnet.nroRegMunicipal()).isEqualTo(NRM);
        assertThat(carnet.nombreMascota()).isEqualTo("Firulais");
        assertThat(carnet.atenciones()).hasSize(1);
        assertThat(carnet.tokenVerificacion()).isNotBlank();
    }

    @Test
    void unaMascotaSinAtencionesDevuelveElCarnetConListaVacia() {
        when(mascotaRepository.findById(NRM)).thenReturn(Optional.of(crearMascotaConResponsable(ID_RESPONSABLE)));

        CarnetSanitarioFila filaSinAtencion = new CarnetSanitarioFila(
                NRM, "Firulais", "Ana", "Perez", "20123456789",
                null, null, null, null, null, null);
        when(carnetSanitarioRepository.buscarPorMascota(NRM)).thenReturn(List.of(filaSinAtencion));

        CarnetSanitarioResponse carnet = carnetSanitarioService.obtenerCarnet(NRM, ID_RESPONSABLE);

        assertThat(carnet.atenciones()).isEmpty();
    }

    @Test
    void unUsuarioQueNoEsElResponsableNoPuedeVerElCarnet() {
        when(mascotaRepository.findById(NRM)).thenReturn(Optional.of(crearMascotaConResponsable(ID_RESPONSABLE)));

        assertThatThrownBy(() -> carnetSanitarioService.obtenerCarnet(NRM, 99))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void consultarElCarnetDeUnaMascotaInexistenteRechaza() {
        when(mascotaRepository.findById(NRM)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carnetSanitarioService.obtenerCarnet(NRM, ID_RESPONSABLE))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void validarUnTokenDeUnaMascotaExistenteDevuelveDatosPublicos() {
        String token = carnetTokenService.generarToken(NRM, LocalDate.now());

        Mascota mascota = crearMascotaConResponsable(ID_RESPONSABLE);
        when(mascotaRepository.findById(NRM)).thenReturn(Optional.of(mascota));

        ValidarCarnetResponse resultado = carnetSanitarioService.validarToken(token);

        assertThat(resultado.valido()).isTrue();
        assertThat(resultado.nroRegMunicipal()).isEqualTo(NRM);
        assertThat(resultado.nombreMascota()).isEqualTo("Firulais");
    }

    @Test
    void validarUnTokenInvalidoDevuelveSoloValidoFalse() {
        ValidarCarnetResponse resultado = carnetSanitarioService.validarToken("token-truchado");

        assertThat(resultado.valido()).isFalse();
        assertThat(resultado.nroRegMunicipal()).isNull();
        assertThat(resultado.nombreMascota()).isNull();
    }
}
