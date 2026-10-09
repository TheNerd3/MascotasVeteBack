package ubp.das.backndvt.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Punto unico de escritura para caracteristicas_mascotas. Nunca hacer un
 * INSERT directo sobre esa tabla: el correlativo nro_caracteristica se
 * calcula de forma atomica en la base de datos dentro del stored
 * procedure sp_InsertarCaracteristicaMascota (tabla de contadores
 * contador_caracteristicas por mascota+rasgo), evitando condiciones de
 * carrera entre transacciones concurrentes.
 */
@Repository
public class CaracteristicaMascotaProcedureRepository {

    private final JdbcTemplate jdbcTemplate;

    public CaracteristicaMascotaProcedureRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public Integer insertarCaracteristicaMascota(
            Integer nroRegMunicipal,
            Integer codRasgo,
            Integer nroValorDominio,
            String valorCaracteristica) {

        // El SP termina con "SELECT @nro_caracteristica AS
        // nro_caracteristica_generado", una unica fila con un unico valor.
        return jdbcTemplate.queryForObject(
                "{call sp_InsertarCaracteristicaMascota(?, ?, ?, ?)}",
                Integer.class,
                nroRegMunicipal, codRasgo, nroValorDominio, valorCaracteristica);
    }
}
