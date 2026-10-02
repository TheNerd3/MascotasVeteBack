package ubp.das.backndvt.security;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HexFormat;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * RF10/RF11 - Genera y valida el token de verificación del carnet
 * sanitario digital: un hash HMAC-SHA256 del número de registro
 * municipal más la fecha de emisión, firmado con una clave propia
 * (distinta de la del login). No es un JWT: es un token simple
 * pensado para que cualquiera con el link pueda comprobar que el
 * carnet es auténtico y sigue vigente, sin exponer más datos.
 */
@Service
public class CarnetTokenService {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final String ALGORITMO = "HmacSHA256";
    private static final String SEPARADOR = "|";

    private final String clave;
    private final long vigenciaDias;

    public CarnetTokenService(
            @Value("${carnet.token-secret}") String clave,
            @Value("${carnet.vigencia-dias}") long vigenciaDias) {
        this.clave = clave;
        this.vigenciaDias = vigenciaDias;
    }

    public String generarToken(Integer nroRegMunicipal, LocalDate fechaEmision) {
        String contenido = contenidoAFirmar(nroRegMunicipal, fechaEmision);
        String firma = firmar(contenido);
        String payload = nroRegMunicipal + SEPARADOR + fechaEmision.format(FORMATO_FECHA) + SEPARADOR + firma;
        return Base64.getUrlEncoder().withoutPadding().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
    }

    public ResultadoValidacion validar(String token) {
        try {
            String payload = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
            String[] partes = payload.split("\\" + SEPARADOR);
            if (partes.length != 3) {
                return ResultadoValidacion.invalido();
            }

            Integer nroRegMunicipal = Integer.valueOf(partes[0]);
            LocalDate fechaEmision = LocalDate.parse(partes[1], FORMATO_FECHA);
            String firmaRecibida = partes[2];

            String firmaEsperada = firmar(contenidoAFirmar(nroRegMunicipal, fechaEmision));
            if (!firmaEsperada.equals(firmaRecibida)) {
                return ResultadoValidacion.invalido();
            }

            boolean vigente = !fechaEmision.plusDays(vigenciaDias).isBefore(LocalDate.now());
            return new ResultadoValidacion(true, vigente, nroRegMunicipal, fechaEmision);
        } catch (RuntimeException ex) {
            return ResultadoValidacion.invalido();
        }
    }

    private String contenidoAFirmar(Integer nroRegMunicipal, LocalDate fechaEmision) {
        return nroRegMunicipal + SEPARADOR + fechaEmision.format(FORMATO_FECHA);
    }

    private String firmar(String contenido) {
        try {
            Mac mac = Mac.getInstance(ALGORITMO);
            mac.init(new SecretKeySpec(clave.getBytes(StandardCharsets.UTF_8), ALGORITMO));
            byte[] firma = mac.doFinal(contenido.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(firma);
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            throw new IllegalStateException("No se pudo firmar el token del carnet sanitario", ex);
        }
    }

    /** RF11 - Resultado de validar un token de carnet ya emitido. */
    public record ResultadoValidacion(boolean firmaValida, boolean vigente, Integer nroRegMunicipal, LocalDate fechaEmision) {

        public static ResultadoValidacion invalido() {
            return new ResultadoValidacion(false, false, null, null);
        }

        public boolean esValido() {
            return firmaValida && vigente;
        }
    }
}
