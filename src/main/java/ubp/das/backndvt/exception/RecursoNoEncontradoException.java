package ubp.das.backndvt.exception;

/**
 * Se lanza cuando se busca una entidad por id/clave y no existe.
 * El GlobalExceptionHandler la traduce a un 404.
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
