package ubp.das.backndvt.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ubp.das.backndvt.dto.CiudadanoResponse;
import ubp.das.backndvt.dto.LoginRequest;
import ubp.das.backndvt.dto.LoginResponse;
import ubp.das.backndvt.exception.CredencialesInvalidasException;
import ubp.das.backndvt.repository.UsuarioLogin;
import ubp.das.backndvt.repository.UsuarioRepository;
import ubp.das.backndvt.security.JwtService;

/**
 * Implementacion local de AuthService: valida usuario (cuil) y clave
 * contra la tabla ciudadanos, leida con JdbcTemplate (ver
 * UsuarioRepository). No hay integracion real con CiDi en este
 * entregable, ver CLAUDE.md seccion 2.
 *
 * El perfil devuelto es siempre CIUDADANO en este PR: los perfiles
 * REFUGIO/VETERINARIA/MUNICIPALIDAD se resuelven en los PRs de esas
 * secciones, cuando exista el vinculo correspondiente en la base
 * (responsable de refugio, profesional de veterinaria, etc.) para saber
 * con que rol autenticar a esa persona.
 */
@Service
public class LocalAuthService implements AuthService {

    private static final String PERFIL_CIUDADANO = "CIUDADANO";
    private static final String MENSAJE_CREDENCIALES_INVALIDAS = "Usuario o clave incorrectos";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LocalAuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        UsuarioLogin usuario = usuarioRepository.buscarPorCuil(request.usuario())
                .orElseThrow(() -> new CredencialesInvalidasException(MENSAJE_CREDENCIALES_INVALIDAS));

        // El mensaje es siempre el mismo genérico, tanto si el usuario no
        // existe como si existe pero está deshabilitado o la clave no
        // coincide: así no se le confirma a quien intenta entrar cuál de
        // los dos datos era el incorrecto.
        if (!Boolean.TRUE.equals(usuario.habilitado())) {
            throw new CredencialesInvalidasException(MENSAJE_CREDENCIALES_INVALIDAS);
        }

        if (!passwordEncoder.matches(request.clave(), usuario.clave())) {
            throw new CredencialesInvalidasException(MENSAJE_CREDENCIALES_INVALIDAS);
        }

        String token = jwtService.generarToken(usuario.cuil(), usuario.idCiudadano(), PERFIL_CIUDADANO);
        long expiraEn = jwtService.obtenerExpiracionSegundos();

        return new LoginResponse(token, expiraEn, CiudadanoResponse.from(usuario));
    }
}
