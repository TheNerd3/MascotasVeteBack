package ubp.das.backndvt.repository;

import java.time.LocalDate;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Punto unico de escritura para informacion_sanitaria. Nunca hacer un
 * INSERT directo sobre esa tabla: el correlativo nro_registro se
 * calcula de forma atomica en la base de datos dentro del stored
 * procedure sp_InsertarInformacionSanitaria (contador en
 * mascotas.ultimo_nro_atencion), evitando condiciones de carrera entre
 * transacciones concurrentes.
 */
@Repository
public class InformacionSanitariaProcedureRepository {

    private final JdbcTemplate jdbcTemplate;

    public InformacionSanitariaProcedureRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public Integer insertarInformacionSanitaria(
            Integer nroRegMunicipal,
            LocalDate fechaAtencion,
            Integer codTipoAtencion,
            String detalleAtencion,
            LocalDate fechaVencimiento,
            Integer idVeterinaria,
            Integer idProfesional) {

        // El SP termina con "SELECT @nro_registro AS nro_registro_generado",
        // que llega como una unica fila con un unico valor numerico.
        return jdbcTemplate.queryForObject(
                "{call sp_InsertarInformacionSanitaria(?, ?, ?, ?, ?, ?, ?)}",
                Integer.class,
                nroRegMunicipal, fechaAtencion, codTipoAtencion, detalleAtencion,
                fechaVencimiento, idVeterinaria, idProfesional);
    }
}
