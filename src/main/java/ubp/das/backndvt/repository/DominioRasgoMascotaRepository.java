package ubp.das.backndvt.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import ubp.das.backndvt.entity.DominioRasgoMascota;
import ubp.das.backndvt.entity.RasgoMascota;

@Repository
public class DominioRasgoMascotaRepository {

    private static final RowMapper<DominioRasgoMascota> MAPEADOR = (fila, numeroFila) -> {
        RasgoMascota rasgo = new RasgoMascota();
        rasgo.setCodRasgo(fila.getInt("cod_rasgo"));
        rasgo.setNomRasgo(fila.getString("nom_rasgo"));

        DominioRasgoMascota dominio = new DominioRasgoMascota();
        dominio.setCodRasgo(fila.getInt("cod_rasgo"));
        dominio.setNroValorDominio(fila.getInt("nro_valor_dominio"));
        dominio.setNomValorDominio(fila.getString("nom_valor_dominio"));
        dominio.setRasgo(rasgo);
        return dominio;
    };

    private static final String SELECT_BASE = """
            SELECT d.cod_rasgo, d.nro_valor_dominio, d.nom_valor_dominio, r.nom_rasgo
            FROM dominio_rasgos_mascotas d
            JOIN rasgos_mascotas r ON r.cod_rasgo = d.cod_rasgo
            """;

    private final JdbcTemplate jdbcTemplate;

    public DominioRasgoMascotaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<DominioRasgoMascota> findByCodRasgo(Integer codRasgo) {
        return jdbcTemplate.query(SELECT_BASE + " WHERE d.cod_rasgo = ?", MAPEADOR, codRasgo);
    }

    public Optional<DominioRasgoMascota> findById(Integer codRasgo, Integer nroValorDominio) {
        try {
            DominioRasgoMascota dominio = jdbcTemplate.queryForObject(
                    SELECT_BASE + " WHERE d.cod_rasgo = ? AND d.nro_valor_dominio = ?",
                    MAPEADOR, codRasgo, nroValorDominio);
            return Optional.ofNullable(dominio);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }
}
