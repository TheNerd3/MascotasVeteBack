package ubp.das.backndvt.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ubp.das.backndvt.dto.CiudadanoResponse;
import ubp.das.backndvt.dto.MascotaResponse;
import ubp.das.backndvt.entity.Ciudadano;
import ubp.das.backndvt.entity.Mascota;
import ubp.das.backndvt.exception.RecursoNoEncontradoException;
import ubp.das.backndvt.repository.CiudadanoRepository;
import ubp.das.backndvt.repository.MascotaRepository;

/**
 * Pruebas de CiudadanoService (RF20) con los repositorios simulados
 * (mock): no necesitan SQL Server para correr.
 */
class CiudadanoServiceTest {

    private static final Integer ID_CIUDADANO = 1;

    private CiudadanoRepository ciudadanoRepository;
    private MascotaRepository mascotaRepository;
    private CiudadanoService ciudadanoService;

    @BeforeEach
    void prepararDependenciasSimuladas() {
        ciudadanoRepository = mock(CiudadanoRepository.class);
        mascotaRepository = mock(MascotaRepository.class);
        ciudadanoService = new CiudadanoService(ciudadanoRepository, mascotaRepository);
    }

    private Ciudadano crearCiudadano() {
        Ciudadano ciudadano = new Ciudadano();
        ciudadano.setIdCiudadano(ID_CIUDADANO);
        ciudadano.setApellido("Perez");
        ciudadano.setNombre("Ana");
        ciudadano.setCuil("20123456789");
        ciudadano.setHabilitado(true);
        return ciudadano;
    }

    @Test
    void buscarUnCiudadanoExistentePorIdFunciona() {
        when(ciudadanoRepository.findById(ID_CIUDADANO)).thenReturn(Optional.of(crearCiudadano()));

        CiudadanoResponse respuesta = ciudadanoService.buscarPorId(ID_CIUDADANO);

        assertThat(respuesta.idCiudadano()).isEqualTo(ID_CIUDADANO);
    }

    @Test
    void buscarUnCiudadanoInexistenteRechaza() {
        when(ciudadanoRepository.findById(ID_CIUDADANO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ciudadanoService.buscarPorId(ID_CIUDADANO))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void listarLasMascotasDeUnCiudadanoExistenteFunciona() {
        when(ciudadanoRepository.findById(ID_CIUDADANO)).thenReturn(Optional.of(crearCiudadano()));

        Mascota mascota = new Mascota();
        mascota.setNroRegMunicipal(10);
        mascota.setNombre("Firulais");
        when(mascotaRepository.findByResponsableIdCiudadano(ID_CIUDADANO)).thenReturn(List.of(mascota));

        List<MascotaResponse> respuesta = ciudadanoService.listarMascotas(ID_CIUDADANO);

        assertThat(respuesta).hasSize(1);
        assertThat(respuesta.get(0).nombre()).isEqualTo("Firulais");
    }

    @Test
    void listarLasMascotasDeUnCiudadanoInexistenteRechaza() {
        when(ciudadanoRepository.findById(ID_CIUDADANO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ciudadanoService.listarMascotas(ID_CIUDADANO))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
