package ubp.das.backndvt.exception;

/**
 * Se lanza cuando el login falla (usuario inexistente, clave
 * incorrecta, o ciudadano deshabilitado). El GlobalExceptionHandler la
 * traduce a un 401 con formato {codigo, mensaje}. El mensaje es
 * siempre genérico a propósito, para no confirmarle a quien intenta
 * entrar cuál de los dos datos era el incorrecto.
 */
public class CredencialesInvalidasException extends RuntimeException {

    private static final String CODIGO_POR_DEFECTO = "CREDENCIALES_INVALIDAS";

    private final String codigo;

    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
        this.codigo = CODIGO_POR_DEFECTO;
    }

    public String getCodigo() {
        return codigo;
    }
}
