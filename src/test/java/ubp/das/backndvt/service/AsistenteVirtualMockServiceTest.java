package ubp.das.backndvt.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import ubp.das.backndvt.dto.ConsultaAsistenteRequest;
import ubp.das.backndvt.dto.ConsultaAsistenteResponse;

/**
 * Pruebas de AsistenteVirtualMockService (RF21).
 */
class AsistenteVirtualMockServiceTest {

    @Test
    void responderDevuelveSiempreUnaRespuestaNoVacia() {
        AsistenteVirtualService servicio = new AsistenteVirtualMockService();

        ConsultaAsistenteResponse respuesta = servicio.responder(new ConsultaAsistenteRequest("¿Como adopto una mascota?"));

        assertThat(respuesta.respuesta()).isNotBlank();
    }
}
