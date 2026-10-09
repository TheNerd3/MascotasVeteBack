package ubp.das.backndvt.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import ubp.das.backndvt.entity.Auditoria;

/**
 * Solo lectura: la tabla auditoria la llenan los triggers TR_*_Audit,
 * nunca se debe escribir en ella desde la aplicacion.
 */
@Repository
public class AuditoriaRepository {

    private static final RowMapper<Auditoria> MAPEADOR = (fila, numeroFila) -> {
        Auditoria auditoria = new Auditoria();
        auditoria.setIdAuditoria(fila.getLong("id_auditoria"));
        auditoria.setNombreTabla(fila.getString("nombre_tabla"));
        auditoria.setOperacion(fila.getString("operacion"));
        auditoria.setFechaOperacion(fila.getObject("fecha_operacion", LocalDateTime.class));
        auditoria.setUsuarioBd(fila.getString("usuario_bd"));
        auditoria.setDatosAnteriores(fila.getString("datos_anteriores"));
        auditoria.setDatosNuevos(fila.getString("datos_nuevos"));
        return auditoria;
    };

    private static final String SELECT_BASE = """
            SELECT id_auditoria, nombre_tabla, operacion, fecha_operacion, usuario_bd,
                   datos_anteriores, datos_nuevos
            FROM auditoria
            """;

    private final JdbcTemplate jdbcTemplate;

    public AuditoriaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Auditoria> findByNombreTablaOrderByFechaOperacionDesc(String nombreTabla) {
        return jdbcTemplate.query(
                SELECT_BASE + " WHERE nombre_tabla = ? ORDER BY fecha_operacion DESC", MAPEADOR, nombreTabla);
    }
}
