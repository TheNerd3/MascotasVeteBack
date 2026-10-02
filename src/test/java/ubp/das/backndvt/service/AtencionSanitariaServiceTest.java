package ubp.das.backndvt.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ubp.das.backndvt.dto.AtencionSanitariaResponse;
import ubp.das.backndvt.dto.RegistrarAtencionSanitariaRequest;
import ubp.das.backndvt.entity.Mascota;
import ubp.das.backndvt.entity.ProfesionalVeterinaria;
import ubp.das.backndvt.entity.ProfesionalVeterinariaId;
import ubp.das.backndvt.entity.TipoAtencionSanitaria;
import ubp.das.backndvt.entity.Veterinaria;
import ubp.das.backndvt.exception.RecursoNoEncontradoException;
import ubp.das.backndvt.repository.InformacionSanitariaProcedureRepository;
import ubp.das.backndvt.repository.MascotaRepository;
import ubp.das.backndvt.repository.ProfesionalVeterinariaRepository;
import ubp.das.backndvt.repository.TipoAtencionSanitariaRepository;
import ubp.das.backndvt.repository.VeterinariaRepository;

/**
 * Pruebas de AtencionSanitariaService (RF09) con los repositorios
 * simulados (mock): no necesitan SQL Server para correr.
 */
class AtencionSanitariaServiceTest {

    private static final Integer NRM = 10;
    private static final Integer ID_VETERINARIA = 1;
    private static final Integer ID_PROFESIONAL = 2;

    private MascotaRepository mascotaRepository;
    private TipoAtencionSanitariaRepository tipoAtencionSanitariaRepository;
    private VeterinariaRepository veterinariaRepository;
    private ProfesionalVeterinariaRepository profesionalVeterinariaRepository;
    private InformacionSanitariaProcedureRepository informacionSanitariaProcedureRepository;
    private AtencionSanitariaService atencionSanitariaService;

    @BeforeEach
    void prepararDependenciasSimuladas() {
        mascotaRepository = mock(MascotaRepository.class);
        tipoAtencionSanitariaRepository = mock(TipoAtencionSanitariaRepository.class);
        veterinariaRepository = mock(VeterinariaRepository.class);
        profesionalVeterinariaRepository = mock(ProfesionalVeterinariaRepository.class);
        informacionSanitariaProcedureRepository = mock(InformacionSanitariaProcedureRepository.class);

        atencionSanitariaService = new AtencionSanitariaService(
                mascotaRepository,
                tipoAtencionSanitariaRepository,
                veterinariaRepository,
                profesionalVeterinariaRepository,
                informacionSanitariaProcedureRepository);
    }

    private RegistrarAtencionSanitariaRequest requestValido() {
        return new RegistrarAtencionSanitariaRequest(
                LocalDate.now(), 1, "Vacuna antirrábica", null, ID_VETERINARIA, ID_PROFESIONAL);
    }

    private void simularMascotaExistente() {
        Mascota mascota = new Mascota();
        mascota.setNroRegMunicipal(NRM);
        when(mascotaRepository.findById(NRM)).thenReturn(Optional.of(mascota));
    }

    private void simularTipoAtencionExistente() {
        when(tipoAtencionSanitariaRepository.findById(1)).thenReturn(Optional.of(new TipoAtencionSanitaria()));
    }

    private void simularVeterinariaHabilitada() {
        Veterinaria veterinaria = new Veterinaria();
        veterinaria.setIdVeterinaria(ID_VETERINARIA);
        veterinaria.setHabilitacionMunicipal("HAB-123");
        when(veterinariaRepository.findById(ID_VETERINARIA)).thenReturn(Optional.of(veterinaria));
    }

    private void simularProfesionalVinculadoSinBaja() {
        ProfesionalVeterinaria profesional = new ProfesionalVeterinaria();
        profesional.setIdVeterinaria(ID_VETERINARIA);
        profesional.setIdProfesional(ID_PROFESIONAL);
        profesional.setBaja(false);
        when(profesionalVeterinariaRepository.findById(new ProfesionalVeterinariaId(ID_VETERINARIA, ID_PROFESIONAL)))
                .thenReturn(Optional.of(profesional));
    }

    @Test
    void registrarConTodoValidoLlamaAlStoredProcedureYDevuelveElNumeroGenerado() {
        simularMascotaExistente();
        simularTipoAtencionExistente();
        simularVeterinariaHabilitada();
        simularProfesionalVinculadoSinBaja();

        when(informacionSanitariaProcedureRepository.insertarInformacionSanitaria(
                any(), any(), any(), any(), any(), any(), any())).thenReturn(3);

        AtencionSanitariaResponse respuesta = atencionSanitariaService.registrar(NRM, requestValido());

        assertThat(respuesta.nroRegMunicipal()).isEqualTo(NRM);
        assertThat(respuesta.nroRegistro()).isEqualTo(3);
    }

    @Test
    void registrarConMascotaInexistenteRechaza() {
        when(mascotaRepository.findById(NRM)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> atencionSanitariaService.registrar(NRM, requestValido()))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void registrarConTipoDeAtencionInexistenteRechaza() {
        simularMascotaExistente();
        when(tipoAtencionSanitariaRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> atencionSanitariaService.registrar(NRM, requestValido()))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void registrarConVeterinariaSinHabilitacionRechaza() {
        simularMascotaExistente();
        simularTipoAtencionExistente();

        Veterinaria veterinariaSinHabilitacion = new Veterinaria();
        veterinariaSinHabilitacion.setIdVeterinaria(ID_VETERINARIA);
        veterinariaSinHabilitacion.setHabilitacionMunicipal(null);
        when(veterinariaRepository.findById(ID_VETERINARIA)).thenReturn(Optional.of(veterinariaSinHabilitacion));

        assertThatThrownBy(() -> atencionSanitariaService.registrar(NRM, requestValido()))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void registrarConProfesionalDadoDeBajaRechaza() {
        simularMascotaExistente();
        simularTipoAtencionExistente();
        simularVeterinariaHabilitada();

        ProfesionalVeterinaria profesionalDeBaja = new ProfesionalVeterinaria();
        profesionalDeBaja.setBaja(true);
        when(profesionalVeterinariaRepository.findById(new ProfesionalVeterinariaId(ID_VETERINARIA, ID_PROFESIONAL)))
                .thenReturn(Optional.of(profesionalDeBaja));

        assertThatThrownBy(() -> atencionSanitariaService.registrar(NRM, requestValido()))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
