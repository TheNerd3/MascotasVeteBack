package ubp.das.backndvt.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import ubp.das.backndvt.entity.RasgoMascota;

@Repository
public class RasgoMascotaRepository {

    private static final RowMapper<RasgoMascota> MAPEADOR = (fila, numeroFila) -> {
        RasgoMascota rasgo = new RasgoMascota();
        rasgo.setCodRasgo(fila.getInt("cod_rasgo"));
        rasgo.setNomRasgo(fila.getString("nom_rasgo"));
        return rasgo;
    };

    private final JdbcTemplate jdbcTemplate;

    public RasgoMascotaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<RasgoMascota> findAll() {
        return jdbcTemplate.query("SELECT cod_rasgo, nom_rasgo FROM rasgos_mascotas", MAPEADOR);
    }
}
