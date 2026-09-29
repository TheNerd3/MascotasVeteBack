package ubp.das.backndvt.repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

// RF10/RF11 - Lee vw_carnet_sanitario tal cual (sin reconstruir el join
// a mano). Devuelve una fila por atencion sanitaria de la mascota, o
// una unica fila con los campos de atencion en null si todavia no
// tiene ninguna.
@Repository
public class CarnetSanitarioRepository {

    private static final RowMapper<CarnetSanitarioFila> MAPEADOR_CARNET = (fila, numeroFila) -> new CarnetSanitarioFila(
            fila.getInt("nro_reg_municipal"),
            fila.getString("nombre_mascota"),
            fila.getString("nombre_responsable"),
            fila.getString("apellido_responsable"),
            fila.getString("cuil"),
            (Integer) fila.getObject("nro_registro"),
            convertirAFecha(fila.getDate("fecha_atencion")),
            fila.getString("desc_tipo_atencion"),
            fila.getString("detalle_atencion"),
            convertirAFecha(fila.getDate("fecha_vencimiento")),
            fila.getString("veterinaria"));

    private final JdbcTemplate jdbcTemplate;

    public CarnetSanitarioRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CarnetSanitarioFila> buscarPorMascota(Integer nroRegMunicipal) {
        String sql = "SELECT * FROM vw_carnet_sanitario WHERE nro_reg_municipal = ?";
        return jdbcTemplate.query(sql, MAPEADOR_CARNET, nroRegMunicipal);
    }

    private static LocalDate convertirAFecha(Date fecha) {
        return fecha != null ? fecha.toLocalDate() : null;
    }
}
