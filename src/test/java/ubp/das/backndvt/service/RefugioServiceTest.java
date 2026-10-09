package ubp.das.backndvt.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ubp.das.backndvt.dto.RefugioResponse;
import ubp.das.backndvt.entity.EstadoPublicacion;
import ubp.das.backndvt.entity.Mascota;
import ubp.das.backndvt.entity.PublicacionAdopcion;
import ubp.das.backndvt.entity.Refugio;
import ubp.das.backndvt.repository.PublicacionAdopcionRepository;
import ubp.das.backndvt.repository.RefugioRepository;

/**
 * Pruebas de RefugioService (RF18) con los repositorios simulados
 * (mock): no necesitan SQL Server para correr.
 */
class RefugioServiceTest {

    private RefugioRepository refugioRepository;
    private PublicacionAdopcionRepository publicacionAdopcionRepository;
    private PublicacionAdopcionService publicacionAdopcionService;
    private RefugioService refugioService;

    @BeforeEach
    void prepararDependenciasSimuladas() {
        refugioRepository = mock(RefugioRepository.class);
        publicacionAdopcionRepository = mock(PublicacionAdopcionRepository.class);
        publicacionAdopcionService = mock(PublicacionAdopcionService.class);
        when(publicacionAdopcionService.toResponse(any())).thenAnswer(invocacion -> {
            PublicacionAdopcion publicacion = invocacion.getArgument(0);
            return new ubp.das.backndvt.dto.PublicacionAdopcionResponse(
                    publicacion.getNroPublicacion(),
                    publicacion.getMascota().getNroRegMunicipal(),
                    publicacion.getMascota().getNombre(),
                    publicacion.getMascota().getSexo(),
                    publicacion.getMascota().getAnioNacimiento(),
                    null,
                    null,
                    publicacion.getRefugio().getIdRefugio(),
                    publicacion.getFechaPublicacion(),
                    publicacion.getCaracteristicasMascota(),
                    publicacion.getCondicionAdopcion(),
                    publicacion.getEstadoPublicacion(),
                    publicacion.getEstadoPublicacion().accionesDisponibles());
        });
        refugioService = new RefugioService(refugioRepository, publicacionAdopcionRepository, publicacionAdopcionService);
    }

    private Mascota crearMascota() {
        Mascota mascota = new Mascota();
        mascota.setNroRegMunicipal(10);
        mascota.setNombre("Firulais");
        return mascota;
    }

    private PublicacionAdopcion crearPublicacion(EstadoPublicacion estado, Refugio refugio) {
        PublicacionAdopcion publicacion = new PublicacionAdopcion();
        publicacion.setNroPublicacion(100);
        publicacion.setMascota(crearMascota());
        publicacion.setRefugio(refugio);
        publicacion.setEstadoPublicacion(estado);
        return publicacion;
    }

    @Test
    void listarSoloDevuelveRefugiosHabilitadosConSusPublicacionesActivas() {
        Refugio refugio = new Refugio();
        refugio.setIdRefugio(5);
        refugio.setRazonSocial("Refugio Esperanza");
        refugio.setHabilitacionMunicipal("H-200");

        when(refugioRepository.findByHabilitacionMunicipalIsNotNullAndHabilitacionMunicipalNot(""))
                .thenReturn(List.of(refugio));
        when(publicacionAdopcionRepository.findByRefugioIdRefugioAndEstadoPublicacionOrderByFechaPublicacionDesc(
                5, EstadoPublicacion.ACTIVA))
                .thenReturn(List.of(crearPublicacion(EstadoPublicacion.ACTIVA, refugio)));

        List<RefugioResponse> respuesta = refugioService.listarHabilitados();

        assertThat(respuesta).hasSize(1);
        assertThat(respuesta.get(0).publicacionesActivas()).hasSize(1);
        assertThat(respuesta.get(0).publicacionesActivas().get(0).estadoPublicacion())
                .isEqualTo(EstadoPublicacion.ACTIVA);
    }
}
