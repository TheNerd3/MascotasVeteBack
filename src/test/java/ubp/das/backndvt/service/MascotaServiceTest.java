package ubp.das.backndvt.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import ubp.das.backndvt.dto.CaracteristicaMascotaRequest;
import ubp.das.backndvt.dto.DatosPropietarioRequest;
import ubp.das.backndvt.dto.RegistrarMascotaRequest;
import ubp.das.backndvt.dto.RegistrarMascotaResultado;
import ubp.das.backndvt.entity.Ciudadano;
import ubp.das.backndvt.entity.DominioRasgoMascota;
import ubp.das.backndvt.entity.Mascota;
import ubp.das.backndvt.entity.RasgoMascota;
import ubp.das.backndvt.exception.RecursoNoEncontradoException;
import ubp.das.backndvt.repository.CaracteristicaMascotaProcedureRepository;
import ubp.das.backndvt.repository.CiudadanoRepository;
import ubp.das.backndvt.repository.DominioRasgoMascotaRepository;
import ubp.das.backndvt.repository.MascotaExistenteProcedureRepository;
import ubp.das.backndvt.repository.MascotaRepository;
import ubp.das.backndvt.repository.RefugioRepository;

/**
 * Pruebas de MascotaService (RF06) con los repositorios simulados
 * (mock): no necesitan SQL Server para correr.
 */
class MascotaServiceTest {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private MascotaRepository mascotaRepository;
    private MascotaExistenteProcedureRepository mascotaExistenteProcedureRepository;
    private CaracteristicaMascotaProcedureRepository caracteristicaMascotaProcedureRepository;
    private CiudadanoRepository ciudadanoRepository;
    private RefugioRepository refugioRepository;
    private DominioRasgoMascotaRepository dominioRasgoMascotaRepository;
    private MascotaService mascotaService;

    @BeforeEach
    void prepararDependenciasSimuladas() {
        mascotaRepository = mock(MascotaRepository.class);
        mascotaExistenteProcedureRepository = mock(MascotaExistenteProcedureRepository.class);
        caracteristicaMascotaProcedureRepository = mock(CaracteristicaMascotaProcedureRepository.class);
        ciudadanoRepository = mock(CiudadanoRepository.class);
        refugioRepository = mock(RefugioRepository.class);
        dominioRasgoMascotaRepository = mock(DominioRasgoMascotaRepository.class);

        mascotaService = new MascotaService(
                mascotaRepository,
                mascotaExistenteProcedureRepository,
                caracteristicaMascotaProcedureRepository,
                ciudadanoRepository,
                refugioRepository,
                dominioRasgoMascotaRepository,
                passwordEncoder);
    }

    @Test
    void registrarMascotaNuevaConResponsableExistenteDevuelve201() {
        Ciudadano responsable = new Ciudadano();
        responsable.setIdCiudadano(1);

        Mascota guardada = new Mascota();
        guardada.setNroRegMunicipal(10);
        guardada.setNombre("Firulais");
        guardada.setSexo("M");
        guardada.setResponsable(responsable);

        RasgoMascota rasgoEspecie = new RasgoMascota();
        rasgoEspecie.setCodRasgo(1);
        DominioRasgoMascota valorDominio = new DominioRasgoMascota();
        valorDominio.setCodRasgo(1);
        valorDominio.setNroValorDominio(2);
        valorDominio.setRasgo(rasgoEspecie);

        when(ciudadanoRepository.findById(1)).thenReturn(Optional.of(responsable));
        when(mascotaExistenteProcedureRepository.buscarExistente(anyString(), any(), anyInt(), any()))
                .thenReturn(Optional.empty());
        when(dominioRasgoMascotaRepository.findById(1, 2))
                .thenReturn(Optional.of(valorDominio));
        when(mascotaRepository.findByMicrochip(any())).thenReturn(Optional.empty());
        when(mascotaRepository.save(any())).thenReturn(guardada);

        RegistrarMascotaRequest request = new RegistrarMascotaRequest(
                "Firulais", "M", (short) 2020, null, 1, null, null,
                List.of(new CaracteristicaMascotaRequest(1, 2, "Perro")));

        RegistrarMascotaResultado resultado = mascotaService.registrar(request);

        assertThat(resultado.creada()).isTrue();
        assertThat(resultado.mascota().nroRegMunicipal()).isEqualTo(10);
        verify(caracteristicaMascotaProcedureRepository).insertarCaracteristicaMascota(10, 1, 2, "Perro");
    }

    @Test
    void registrarMascotaYaExistenteDevuelve200SinCrearNiInsertarCaracteristicas() {
        Ciudadano responsable = new Ciudadano();
        responsable.setIdCiudadano(1);

        Mascota existente = new Mascota();
        existente.setNroRegMunicipal(5);
        existente.setNombre("Firulais");

        when(ciudadanoRepository.findById(1)).thenReturn(Optional.of(responsable));
        when(mascotaExistenteProcedureRepository.buscarExistente(anyString(), any(), anyInt(), any()))
                .thenReturn(Optional.of(existente));

        RegistrarMascotaRequest request = new RegistrarMascotaRequest(
                "Firulais", "M", (short) 2020, null, 1, null, null, List.of());

        RegistrarMascotaResultado resultado = mascotaService.registrar(request);

        assertThat(resultado.creada()).isFalse();
        assertThat(resultado.mascota().nroRegMunicipal()).isEqualTo(5);
        verify(mascotaRepository, never()).save(any());
        verify(caracteristicaMascotaProcedureRepository, never()).insertarCaracteristicaMascota(any(), any(), any(), any());
    }

    @Test
    void registrarMascotaSinResponsableCreaAlPropietarioNuevo() {
        Ciudadano nuevoCiudadano = new Ciudadano();
        nuevoCiudadano.setIdCiudadano(2);

        Mascota guardada = new Mascota();
        guardada.setNroRegMunicipal(11);
        guardada.setResponsable(nuevoCiudadano);

        DatosPropietarioRequest propietario = new DatosPropietarioRequest(
                "Perez", "Ana", "20123456789", "clave123", null, null, null);

        when(ciudadanoRepository.findByCuil("20123456789")).thenReturn(Optional.empty());
        when(ciudadanoRepository.save(any())).thenReturn(nuevoCiudadano);
        when(mascotaExistenteProcedureRepository.buscarExistente(anyString(), any(), anyInt(), any()))
                .thenReturn(Optional.empty());
        when(mascotaRepository.findByMicrochip(any())).thenReturn(Optional.empty());
        when(mascotaRepository.save(any())).thenReturn(guardada);

        RegistrarMascotaRequest request = new RegistrarMascotaRequest(
                "Michi", "H", (short) 2021, null, null, propietario, null, List.of());

        mascotaService.registrar(request);

        verify(ciudadanoRepository).save(any());
    }

    @Test
    void registrarConRasgoInexistenteRechazaConRecursoNoEncontrado() {
        Ciudadano responsable = new Ciudadano();
        responsable.setIdCiudadano(1);

        when(ciudadanoRepository.findById(1)).thenReturn(Optional.of(responsable));
        when(mascotaExistenteProcedureRepository.buscarExistente(anyString(), any(), anyInt(), any()))
                .thenReturn(Optional.empty());
        when(dominioRasgoMascotaRepository.findById(99, 1)).thenReturn(Optional.empty());

        RegistrarMascotaRequest request = new RegistrarMascotaRequest(
                "Firulais", "M", (short) 2020, null, 1, null, null,
                List.of(new CaracteristicaMascotaRequest(99, 1, "valor")));

        assertThatThrownBy(() -> mascotaService.registrar(request))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

}
