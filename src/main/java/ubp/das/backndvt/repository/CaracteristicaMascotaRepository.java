package ubp.das.backndvt.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import ubp.das.backndvt.entity.CaracteristicaMascota;
import ubp.das.backndvt.entity.DominioRasgoMascota;
import ubp.das.backndvt.entity.Mascota;
import ubp.das.backndvt.entity.RasgoMascota;

@Repository
public class CaracteristicaMascotaRepository {

    private static final RowMapper<CaracteristicaMascota> MAPEADOR = (fila, numeroFila) -> {
        Mascota mascota = new Mascota();
        mascota.setNroRegMunicipal(fila.getInt("nro_reg_municipal"));

        RasgoMascota rasgo = new RasgoMascota();
        rasgo.setCodRasgo(fila.getInt("cod_rasgo"));
        rasgo.setNomRasgo(fila.getString("nom_rasgo"));

        DominioRasgoMascota dominioRasgo = new DominioRasgoMascota();
        dominioRasgo.setCodRasgo(fila.getInt("cod_rasgo"));
        dominioRasgo.setNroValorDominio(fila.getInt("nro_valor_dominio"));
        dominioRasgo.setNomValorDominio(fila.getString("nom_valor_dominio"));
        dominioRasgo.setRasgo(rasgo);

        CaracteristicaMascota caracteristica = new CaracteristicaMascota();
        caracteristica.setNroRegMunicipal(fila.getInt("nro_reg_municipal"));
        caracteristica.setCodRasgo(fila.getInt("cod_rasgo"));
        caracteristica.setNroCaracteristica(fila.getInt("nro_caracteristica"));
        caracteristica.setNroValorDominio(fila.getInt("nro_valor_dominio"));
        caracteristica.setValorCaracteristica(fila.getString("valor_caracteristica"));
        caracteristica.setMascota(mascota);
        caracteristica.setDominioRasgo(dominioRasgo);
        return caracteristica;
    };

    private static final String SELECT_BASE = """
            SELECT cm.nro_reg_municipal, cm.cod_rasgo, cm.nro_caracteristica, cm.nro_valor_dominio,
                   cm.valor_caracteristica, d.nom_valor_dominio, r.nom_rasgo
            FROM caracteristicas_mascotas cm
            JOIN dominio_rasgos_mascotas d ON d.cod_rasgo = cm.cod_rasgo AND d.nro_valor_dominio = cm.nro_valor_dominio
            JOIN rasgos_mascotas r ON r.cod_rasgo = cm.cod_rasgo
            """;

    private final JdbcTemplate jdbcTemplate;

    public CaracteristicaMascotaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CaracteristicaMascota> findByNroRegMunicipal(Integer nroRegMunicipal) {
        return jdbcTemplate.query(SELECT_BASE + " WHERE cm.nro_reg_municipal = ?", MAPEADOR, nroRegMunicipal);
    }
}
