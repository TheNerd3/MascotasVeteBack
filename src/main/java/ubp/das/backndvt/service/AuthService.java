package ubp.das.backndvt.service;

import ubp.das.backndvt.dto.LoginRequest;
import ubp.das.backndvt.dto.LoginResponse;

/**
 * Autenticacion de usuarios (RF15). Se define como interfaz a proposito:
 * la implementacion actual (LocalAuthService) valida contra
 * ciudadanos.cuil + ciudadanos.clave porque todavia no hay integracion
 * real con CiDi (ver brief seccion 6). El dia que exista esa integracion,
 * se agrega una implementacion nueva (ej: CidiAuthService) sin tocar los
 * controllers ni el resto del sistema que dependen de esta interfaz.
 */
public interface AuthService {

    LoginResponse login(LoginRequest request);
}
