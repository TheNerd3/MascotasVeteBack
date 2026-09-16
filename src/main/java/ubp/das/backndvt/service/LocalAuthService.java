package ubp.das.backndvt.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ubp.das.backndvt.dto.CiudadanoResponse;
import ubp.das.backndvt.dto.LoginRequest;
import ubp.das.backndvt.dto.LoginResponse;
import ubp.das.backndvt.entity.Ciudadano;
import ubp.das.backndvt.exception.CredencialesInvalidasException;
import ubp.das.backndvt.repository.CiudadanoRepository;
import ubp.das.backndvt.security.JwtService;

/**
 * Implementacion local de AuthService: valida cuil+clave contra la tabla
 * ciudadanos (sin integracion real con CiDi, ver brief seccion 6).
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

    private final CiudadanoRepository ciudadanoRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LocalAuthService(
            CiudadanoRepository ciudadanoRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.ciudadanoRepository = ciudadanoRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Ciudadano ciudadano = ciudadanoRepository.findByCuil(request.cuil())
                .orElseThrow(() -> new CredencialesInvalidasException("Cuil o clave incorrectos"));

        if (!Boolean.TRUE.equals(ciudadano.getHabilitado())) {
            throw new CredencialesInvalidasException("El ciudadano se encuentra deshabilitado");
        }

        if (!passwordEncoder.matches(request.clave(), ciudadano.getClave())) {
            throw new CredencialesInvalidasException("Cuil o clave incorrectos");
        }

        String token = jwtService.generarToken(ciudadano.getCuil(), ciudadano.getIdCiudadano(), PERFIL_CIUDADANO);
        return LoginResponse.of(token, CiudadanoResponse.from(ciudadano), PERFIL_CIUDADANO);
    }
}
