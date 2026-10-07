package ubp.das.backndvt.exception;

import java.net.URI;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Traduce las excepciones de negocio a respuestas HTTP consistentes
 * con el formato RFC 7807 (ProblemDetail), en vez de dejar que Spring
 * devuelva un 500 generico o una stacktrace. Nunca un 500 por una
 * violacion de constraint esperable: siempre un 4xx con mensaje claro.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final URI TIPO_GENERICO = URI.create("about:blank");

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ProblemDetail> handleNoEncontrado(RecursoNoEncontradoException ex, WebRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(RecursoDuplicadoException.class)
    public ResponseEntity<ProblemDetail> handleDuplicado(RecursoDuplicadoException ex, WebRequest request) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(TransicionEstadoInvalidaException.class)
    public ResponseEntity<ProblemDetail> handleTransicionInvalida(TransicionEstadoInvalidaException ex, WebRequest request) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ErrorLoginResponse> handleCredenciales(CredencialesInvalidasException ex) {
        // El login mantiene su propio formato de error (distinto del resto
        // del backend) por decision explicita: ver ErrorLoginResponse.
        ErrorLoginResponse body = new ErrorLoginResponse(ex.getCodigo(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccesoDenegado(AccessDeniedException ex, WebRequest request) {
        return build(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> handleIntegridad(DataIntegrityViolationException ex, WebRequest request) {
        // Cubre violaciones de UNIQUE/FK que no se hayan validado antes a
        // nivel de negocio (ej: carrera entre dos requests concurrentes).
        return build(HttpStatus.CONFLICT, "El recurso ya existe o viola una restriccion de datos", request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidacion(MethodArgumentNotValidException ex, WebRequest request) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, mensaje, request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleTipoInvalido(MethodArgumentTypeMismatchException ex, WebRequest request) {
        // Ej: ?estado=NoExiste en un query param de tipo enum.
        String mensaje = "Valor invalido para '" + ex.getName() + "': " + ex.getValue();
        return build(HttpStatus.BAD_REQUEST, mensaje, request);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ProblemDetail> handleParametroFaltante(MissingServletRequestParameterException ex, WebRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ProblemDetail> handleMediaTypeNoSoportado(HttpMediaTypeNotSupportedException ex, WebRequest request) {
        return build(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex.getMessage(), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    private ResponseEntity<ProblemDetail> build(HttpStatus status, String message, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(status, message);
        problema.setType(TIPO_GENERICO);
        problema.setTitle(status.getReasonPhrase());
        problema.setInstance(URI.create(path));
        return ResponseEntity.status(status).body(problema);
    }
}
