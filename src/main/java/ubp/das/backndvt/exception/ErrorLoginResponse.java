package ubp.das.backndvt.exception;

/**
 * Cuerpo de error específico del login (RF15), con el formato pedido
 * para ese endpoint: un código identificable por el frontend y un
 * mensaje en español simple, sin los demás datos técnicos que trae
 * el ProblemDetail (RFC 7807) usado en el resto del backend.
 */
public record ErrorLoginResponse(String codigo, String mensaje) {
}
