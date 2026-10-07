package ubp.das.backndvt.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ubp.das.backndvt.dto.LoginRequest;
import ubp.das.backndvt.dto.LoginResponse;
import ubp.das.backndvt.exception.CredencialesInvalidasException;
import ubp.das.backndvt.repository.UsuarioLogin;
import ubp.das.backndvt.repository.UsuarioRepository;
import ubp.das.backndvt.security.JwtService;

/**
 * Implementacion local de AuthService: valida cuil y clave contra la
 * tabla ciudadanos, leida con JdbcTemplate (ver UsuarioRepository). No
 * hay integracion real con CiDi en este entregable (CLAUDE.md sección
 * 2); esta clase se define detrás de la interfaz AuthService para
 * poder reemplazarla el día que exista esa integración, sin tocar el
 * resto del sistema.
 *
 * RF15 - Autenticar usuarios: el perfil se resuelve según con qué otra
 * tabla está vinculado el ciudadano:
 * - REFUGIO si es responsable de algún refugio.
 * - VETERINARIA si es profesional activo de alguna veterinaria.
 * - CIUDADANO en cualquier otro caso.
 */
@Service
public class LocalAuthService implements AuthService {

    private static final String PERFIL_REFUGIO = "REFUGIO";
    private static final String PERFIL_VETERINARIA = "VETERINARIA";
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
        UsuarioLogin usuario = usuarioRepository.buscarPorCuil(request.cuil())
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

        Integer idRefugio = usuarioRepository.buscarIdRefugioResponsable(usuario.idCiudadano()).orElse(null);
        Integer idVeterinaria = idRefugio == null
                ? usuarioRepository.buscarIdVeterinariaProfesional(usuario.idCiudadano()).orElse(null)
                : null;

        String perfil = resolverPerfil(idRefugio, idVeterinaria);
        String token = jwtService.generarToken(usuario.cuil(), usuario.idCiudadano(), perfil, idRefugio);

        return new LoginResponse(
                token,
                usuario.idCiudadano(),
                usuario.nombre(),
                usuario.apellido(),
                perfil,
                idRefugio,
                idVeterinaria);
    }

    private String resolverPerfil(Integer idRefugio, Integer idVeterinaria) {
        if (idRefugio != null) {
            return PERFIL_REFUGIO;
        }
        if (idVeterinaria != null) {
            return PERFIL_VETERINARIA;
        }
        return PERFIL_CIUDADANO;
    }
}
