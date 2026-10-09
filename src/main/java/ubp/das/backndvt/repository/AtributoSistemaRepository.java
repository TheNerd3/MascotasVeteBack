package ubp.das.backndvt.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import ubp.das.backndvt.entity.AtributoSistema;

@Repository
public class AtributoSistemaRepository {

    private static final RowMapper<AtributoSistema> MAPEADOR = (fila, numeroFila) -> {
        AtributoSistema atributo = new AtributoSistema();
        atributo.setCodAtributo(fila.getInt("cod_atributo"));
        atributo.setDescAtributo(fila.getString("desc_atributo"));
        atributo.setTipoDato(fila.getString("tipo_dato"));
        atributo.setObservAtributo(fila.getString("observ_atributo"));
        return atributo;
    };

    private static final String SELECT_BASE =
            "SELECT cod_atributo, desc_atributo, tipo_dato, observ_atributo FROM atributos_sistema";

    private final JdbcTemplate jdbcTemplate;

    public AtributoSistemaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<AtributoSistema> findById(Integer codAtributo) {
        try {
            AtributoSistema atributo = jdbcTemplate.queryForObject(
                    SELECT_BASE + " WHERE cod_atributo = ?", MAPEADOR, codAtributo);
            return Optional.ofNullable(atributo);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public List<AtributoSistema> findAll() {
        return jdbcTemplate.query(SELECT_BASE, MAPEADOR);
    }
}
