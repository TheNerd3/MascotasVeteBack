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
        UsuarioLogin usuario = new UsuarioLogin(1, "Perez", "Ana", CUIL_EXISTENTE, passwordEncoder.encode(CLAVE_CORRECTA), true);

        when(usuarioRepository.buscarPorCuil(CUIL_EXISTENTE)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.buscarIdRefugioResponsable(1)).thenReturn(Optional.empty());
        when(usuarioRepository.buscarIdVeterinariaProfesional(1)).thenReturn(Optional.empty());
        when(jwtService.generarToken(CUIL_EXISTENTE, 1, "CIUDADANO", null)).thenReturn("token-simulado");

        LoginResponse respuesta = authService.login(new LoginRequest(CUIL_EXISTENTE, CLAVE_CORRECTA));

        assertThat(respuesta.token()).isEqualTo("token-simulado");
        assertThat(respuesta.idCiudadano()).isEqualTo(1);
        assertThat(respuesta.nombre()).isEqualTo("Ana");
        assertThat(respuesta.perfil()).isEqualTo("CIUDADANO");
        assertThat(respuesta.idRefugio()).isNull();
        assertThat(respuesta.idVeterinaria()).isNull();
    }

    @Test
    void loginDeResponsableDeRefugioDevuelvePerfilRefugio() {
        UsuarioLogin usuario = new UsuarioLogin(2, "Gomez", "Luis", CUIL_EXISTENTE, passwordEncoder.encode(CLAVE_CORRECTA), true);

        when(usuarioRepository.buscarPorCuil(CUIL_EXISTENTE)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.buscarIdRefugioResponsable(2)).thenReturn(Optional.of(5));
        when(jwtService.generarToken(CUIL_EXISTENTE, 2, "REFUGIO", 5)).thenReturn("token-simulado");

        LoginResponse respuesta = authService.login(new LoginRequest(CUIL_EXISTENTE, CLAVE_CORRECTA));

        assertThat(respuesta.perfil()).isEqualTo("REFUGIO");
        assertThat(respuesta.idRefugio()).isEqualTo(5);
        assertThat(respuesta.idVeterinaria()).isNull();
    }

    @Test
    void loginDeProfesionalVeterinarioDevuelvePerfilVeterinaria() {
        UsuarioLogin usuario = new UsuarioLogin(3, "Diaz", "Marta", CUIL_EXISTENTE, passwordEncoder.encode(CLAVE_CORRECTA), true);

        when(usuarioRepository.buscarPorCuil(CUIL_EXISTENTE)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.buscarIdRefugioResponsable(3)).thenReturn(Optional.empty());
        when(usuarioRepository.buscarIdVeterinariaProfesional(3)).thenReturn(Optional.of(7));
        when(jwtService.generarToken(CUIL_EXISTENTE, 3, "VETERINARIA", null)).thenReturn("token-simulado");

        LoginResponse respuesta = authService.login(new LoginRequest(CUIL_EXISTENTE, CLAVE_CORRECTA));

        assertThat(respuesta.perfil()).isEqualTo("VETERINARIA");
        assertThat(respuesta.idVeterinaria()).isEqualTo(7);
        assertThat(respuesta.idRefugio()).isNull();
    }

    @Test
    void loginConClaveIncorrectaRechazaConMensajeGenerico() {
        UsuarioLogin usuario = new UsuarioLogin(1, "Perez", "Ana", CUIL_EXISTENTE, passwordEncoder.encode(CLAVE_CORRECTA), true);

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
                1, "Perez", "Ana", CUIL_EXISTENTE, passwordEncoder.encode(CLAVE_CORRECTA), false);

        when(usuarioRepository.buscarPorCuil(CUIL_EXISTENTE)).thenReturn(Optional.of(usuarioDeshabilitado));

        assertThatThrownBy(() -> authService.login(new LoginRequest(CUIL_EXISTENTE, CLAVE_CORRECTA)))
                .isInstanceOf(CredencialesInvalidasException.class)
                .hasMessage("Usuario o clave incorrectos");
    }
}
