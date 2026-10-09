package ubp.das.backndvt.repository;

import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import ubp.das.backndvt.entity.TipoAtencionSanitaria;

@Repository
public class TipoAtencionSanitariaRepository {

    private static final RowMapper<TipoAtencionSanitaria> MAPEADOR = (fila, numeroFila) -> {
        TipoAtencionSanitaria tipo = new TipoAtencionSanitaria();
        tipo.setCodTipoAtencion(fila.getInt("cod_tipo_atencion"));
        tipo.setDescTipoAtencion(fila.getString("desc_tipo_atencion"));
        return tipo;
    };

    private final JdbcTemplate jdbcTemplate;

    public TipoAtencionSanitariaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<TipoAtencionSanitaria> findById(Integer codTipoAtencion) {
        String sql = "SELECT cod_tipo_atencion, desc_tipo_atencion FROM tipos_atencion_sanitaria WHERE cod_tipo_atencion = ?";
        try {
            TipoAtencionSanitaria tipo = jdbcTemplate.queryForObject(sql, MAPEADOR, codTipoAtencion);
            return Optional.ofNullable(tipo);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public long count() {
        Long total = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM tipos_atencion_sanitaria", Long.class);
        return total != null ? total : 0;
    }
}
