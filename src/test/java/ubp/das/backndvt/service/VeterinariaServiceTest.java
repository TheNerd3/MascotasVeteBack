package ubp.das.backndvt.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ubp.das.backndvt.dto.VeterinariaResponse;
import ubp.das.backndvt.entity.Veterinaria;
import ubp.das.backndvt.repository.VeterinariaRepository;

/**
 * Pruebas de VeterinariaService (RF17) con el repositorio simulado
 * (mock): no necesita SQL Server para correr.
 */
class VeterinariaServiceTest {

    private VeterinariaRepository veterinariaRepository;
    private VeterinariaService veterinariaService;

    @BeforeEach
    void prepararDependenciasSimuladas() {
        veterinariaRepository = mock(VeterinariaRepository.class);
        veterinariaService = new VeterinariaService(veterinariaRepository);
    }

    @Test
    void listarSoloDevuelveVeterinariasHabilitadas() {
        Veterinaria habilitada = new Veterinaria();
        habilitada.setIdVeterinaria(1);
        habilitada.setRazonSocial("Vet Central");
        habilitada.setHabilitacionMunicipal("H-100");

        when(veterinariaRepository.findByHabilitacionMunicipalIsNotNullAndHabilitacionMunicipalNot(""))
                .thenReturn(List.of(habilitada));

        List<VeterinariaResponse> respuesta = veterinariaService.listarHabilitadas();

        assertThat(respuesta).hasSize(1);
        assertThat(respuesta.get(0).razonSocial()).isEqualTo("Vet Central");
    }
}
