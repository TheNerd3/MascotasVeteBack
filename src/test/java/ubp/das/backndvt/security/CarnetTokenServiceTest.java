package ubp.das.backndvt.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import ubp.das.backndvt.security.CarnetTokenService.ResultadoValidacion;

/**
 * Pruebas de CarnetTokenService (RF10/RF11): no necesitan SQL Server
 * para correr.
 */
class CarnetTokenServiceTest {

    private static final String CLAVE = "clave-de-prueba-para-el-carnet-de-al-menos-32-caracteres";

    @Test
    void unTokenRecienGeneradoEsValidoYTraeElMismoNrmYFecha() {
        CarnetTokenService servicio = new CarnetTokenService(CLAVE, 365);
        LocalDate fechaEmision = LocalDate.of(2026, 1, 15);

        String token = servicio.generarToken(10, fechaEmision);
        ResultadoValidacion resultado = servicio.validar(token);

        assertThat(resultado.esValido()).isTrue();
        assertThat(resultado.nroRegMunicipal()).isEqualTo(10);
        assertThat(resultado.fechaEmision()).isEqualTo(fechaEmision);
    }

    @Test
    void unTokenVencidoNoEsValidoAunqueLaFirmaSeaCorrecta() {
        CarnetTokenService servicio = new CarnetTokenService(CLAVE, 30);
        LocalDate fechaEmisionVieja = LocalDate.now().minusDays(31);

        String token = servicio.generarToken(10, fechaEmisionVieja);
        ResultadoValidacion resultado = servicio.validar(token);

        assertThat(resultado.firmaValida()).isTrue();
        assertThat(resultado.vigente()).isFalse();
        assertThat(resultado.esValido()).isFalse();
    }

    @Test
    void unTokenAlteradoNoPasaLaValidacion() {
        CarnetTokenService servicio = new CarnetTokenService(CLAVE, 365);
        String token = servicio.generarToken(10, LocalDate.now());

        String tokenAlterado = token.substring(0, token.length() - 2) + "xx";

        ResultadoValidacion resultado = servicio.validar(tokenAlterado);

        assertThat(resultado.esValido()).isFalse();
    }

    @Test
    void unTokenFirmadoConOtraClaveNoEsValido() {
        CarnetTokenService servicioEmisor = new CarnetTokenService(CLAVE, 365);
        CarnetTokenService servicioVerificador = new CarnetTokenService("otra-clave-distinta-de-al-menos-32-caracteres", 365);

        String token = servicioEmisor.generarToken(10, LocalDate.now());
        ResultadoValidacion resultado = servicioVerificador.validar(token);

        assertThat(resultado.esValido()).isFalse();
    }

    @Test
    void unTokenConTextoInvalidoNoRompeSinoQueDevuelveInvalido() {
        CarnetTokenService servicio = new CarnetTokenService(CLAVE, 365);

        ResultadoValidacion resultado = servicio.validar("esto-no-es-un-token-valido");

        assertThat(resultado.esValido()).isFalse();
    }
}
