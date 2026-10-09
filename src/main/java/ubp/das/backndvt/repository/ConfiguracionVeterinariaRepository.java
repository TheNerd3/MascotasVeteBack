package ubp.das.backndvt.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import ubp.das.backndvt.entity.AtributoSistema;
import ubp.das.backndvt.entity.ConfiguracionVeterinaria;
import ubp.das.backndvt.entity.Veterinaria;

/**
 * Nota: el nombre de tabla "configuracion_veterinatarias" (con ese
 * typo) viene tal cual del script de creacion de la base
 * MascotasCordoba. Se respeta a proposito para que el SQL coincida
 * con el esquema real.
 */
@Repository
public class ConfiguracionVeterinariaRepository {

    private static final RowMapper<ConfiguracionVeterinaria> MAPEADOR = (fila, numeroFila) -> {
        Veterinaria veterinaria = new Veterinaria();
        veterinaria.setIdVeterinaria(fila.getInt("id_veterinaria"));

        AtributoSistema atributo = new AtributoSistema();
        atributo.setCodAtributo(fila.getInt("cod_atributo"));
        atributo.setDescAtributo(fila.getString("desc_atributo"));
        atributo.setTipoDato(fila.getString("tipo_dato"));

        ConfiguracionVeterinaria configuracion = new ConfiguracionVeterinaria();
        configuracion.setIdVeterinaria(fila.getInt("id_veterinaria"));
        configuracion.setCodAtributo(fila.getInt("cod_atributo"));
        configuracion.setValor(fila.getString("valor"));
        configuracion.setVeterinaria(veterinaria);
        configuracion.setAtributo(atributo);
        return configuracion;
    };

    private static final String SELECT_BASE = """
            SELECT c.id_veterinaria, c.cod_atributo, c.valor, a.desc_atributo, a.tipo_dato
            FROM configuracion_veterinatarias c
            JOIN atributos_sistema a ON a.cod_atributo = c.cod_atributo
            """;

    private final JdbcTemplate jdbcTemplate;

    public ConfiguracionVeterinariaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ConfiguracionVeterinaria> findByIdVeterinaria(Integer idVeterinaria) {
        return jdbcTemplate.query(SELECT_BASE + " WHERE c.id_veterinaria = ?", MAPEADOR, idVeterinaria);
    }
}
