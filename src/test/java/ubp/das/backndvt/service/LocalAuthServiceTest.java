package ubp.das.backndvt.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import ubp.das.backndvt.dto.LoginRequest;
import ubp.das.backndvt.dto.LoginResponse;
import ubp.das.backndvt.exception.CredencialesInvalidasException;
import ubp.das.backndvt.repository.UsuarioLogin;
import ubp.das.backndvt.repository.UsuarioRepository;
import ubp.das.backndvt.security.JwtService;

/**
 * Pruebas de LocalAuthService (RF15) con UsuarioRepository simulado
 * (mock): no necesitan SQL Server para correr, así que la corre la CI
 * en cada push.
 */
class LocalAuthServiceTest {

    private static final String CUIL_EXISTENTE = "20123456789";
    private static final String CLAVE_CORRECTA = "claveSegura123";

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private UsuarioRepository usuarioRepository;
    private JwtService jwtService;
    private LocalAuthService authService;

    @BeforeEach
    void prepararDependenciasSimuladas() {
        usuarioRepository = mock(UsuarioRepository.class);
        jwtService = mock(JwtService.class);
        authService = new LocalAuthService(usuarioRepository, passwordEncoder, jwtService);
    }

    @Test
    void loginConCredencialesCorrectasDevuelveTokenYDatosDelUsuario() {
        UsuarioLogin usuario = new UsuarioLogin(
                1, "Perez", "Ana", CUIL_EXISTENTE, passwordEncoder.encode(CLAVE_CORRECTA),
                "ana@correo.com", "3511234567", "Calle Falsa 123", true);

        when(usuarioRepository.buscarPorCuil(CUIL_EXISTENTE)).thenReturn(Optional.of(usuario));
        when(jwtService.generarToken(CUIL_EXISTENTE, 1, "CIUDADANO")).thenReturn("token-simulado");
        when(jwtService.obtenerExpiracionSegundos()).thenReturn(28800L);

        LoginResponse respuesta = authService.login(new LoginRequest(CUIL_EXISTENTE, CLAVE_CORRECTA));

        assertThat(respuesta.token()).isEqualTo("token-simulado");
        assertThat(respuesta.expiraEn()).isEqualTo(28800L);
        assertThat(respuesta.usuario().cuil()).isEqualTo(CUIL_EXISTENTE);
        assertThat(respuesta.usuario().nombre()).isEqualTo("Ana");
    }

    @Test
    void loginConClaveIncorrectaRechazaConMensajeGenerico() {
        UsuarioLogin usuario = new UsuarioLogin(
                1, "Perez", "Ana", CUIL_EXISTENTE, passwordEncoder.encode(CLAVE_CORRECTA),
                null, null, null, true);

        when(usuarioRepository.buscarPorCuil(CUIL_EXISTENTE)).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> authService.login(new LoginRequest(CUIL_EXISTENTE, "claveIncorrecta")))
                .isInstanceOf(CredencialesInvalidasException.class)
                .hasMessage("Usuario o clave incorrectos");
    }

    @Test
    void loginConUsuarioInexistenteRechazaConElMismoMensajeGenerico() {
        when(usuarioRepository.buscarPorCuil("99999999999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginRequest("99999999999", CLAVE_CORRECTA)))
                .isInstanceOf(CredencialesInvalidasException.class)
                .hasMessage("Usuario o clave incorrectos");
    }

    @Test
    void loginConUsuarioDeshabilitadoRechazaAunqueLaClaveSeaCorrecta() {
        UsuarioLogin usuarioDeshabilitado = new UsuarioLogin(
                1, "Perez", "Ana", CUIL_EXISTENTE, passwordEncoder.encode(CLAVE_CORRECTA),
                null, null, null, false);

        when(usuarioRepository.buscarPorCuil(CUIL_EXISTENTE)).thenReturn(Optional.of(usuarioDeshabilitado));

        assertThatThrownBy(() -> authService.login(new LoginRequest(CUIL_EXISTENTE, CLAVE_CORRECTA)))
                .isInstanceOf(CredencialesInvalidasException.class)
                .hasMessage("Usuario o clave incorrectos");
    }
}
