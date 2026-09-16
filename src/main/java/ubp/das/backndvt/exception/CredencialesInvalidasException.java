package ubp.das.backndvt.exception;

/**
 * Se lanza cuando el login falla (cuil inexistente, clave incorrecta, o
 * ciudadano deshabilitado). El GlobalExceptionHandler la traduce a un 401.
 * El mensaje es siempre generico a proposito, para no filtrar si el cuil
 * existe o no.
 */
public class CredencialesInvalidasException extends RuntimeException {

    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}
