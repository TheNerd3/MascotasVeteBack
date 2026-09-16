package ubp.das.backndvt.exception;

import java.time.Instant;

/**
 * Cuerpo estandar de error devuelto por el GlobalExceptionHandler.
 */
public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path) {

    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(Instant.now(), status, error, message, path);
    }
}
