package ubp.das.backndvt.exception;

/**
 * Se lanza cuando se intenta crear un recurso que viola una restriccion de
 * unicidad de negocio (ej: microchip ya registrado en otra mascota).
 * El GlobalExceptionHandler la traduce a un 409.
 */
public class RecursoDuplicadoException extends RuntimeException {

    public RecursoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
