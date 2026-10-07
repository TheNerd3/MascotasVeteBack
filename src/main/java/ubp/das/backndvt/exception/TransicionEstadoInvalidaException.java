package ubp.das.backndvt.exception;

/**
 * Se lanza cuando se pide una accion (activar/pausar/finalizar) que
 * el estado actual de la publicacion no permite, segun
 * EstadoPublicacion (ej: finalizar algo que ya esta Finalizada).
 * El GlobalExceptionHandler la traduce a un 409.
 */
public class TransicionEstadoInvalidaException extends RuntimeException {

    public TransicionEstadoInvalidaException(String mensaje) {
        super(mensaje);
    }
}
