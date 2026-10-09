package ubp.das.backndvt.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import ubp.das.backndvt.entity.Ciudadano;
import ubp.das.backndvt.entity.Refugio;

@Repository
public class RefugioRepository {

    private static final RowMapper<Refugio> MAPEADOR = (fila, numeroFila) -> {
        Ciudadano responsable = new Ciudadano();
        responsable.setIdCiudadano(fila.getInt("id_responsable"));
        responsable.setApellido(fila.getString("responsable_apellido"));
        responsable.setNombre(fila.getString("responsable_nombre"));
        responsable.setCuil(fila.getString("responsable_cuil"));

        Refugio refugio = new Refugio();
        refugio.setIdRefugio(fila.getInt("id_refugio"));
        refugio.setRazonSocial(fila.getString("razon_social"));
        refugio.setCorreo(fila.getString("correo"));
        refugio.setTelefono(fila.getString("telefono"));
        refugio.setDomicilio(fila.getString("domicilio"));
        refugio.setHabilitacionMunicipal(fila.getString("habilitacion_municipal"));
        refugio.setResponsable(responsable);
        return refugio;
    };

    private static final String SELECT_BASE = """
            SELECT r.id_refugio, r.razon_social, r.correo, r.telefono, r.domicilio, r.habilitacion_municipal,
                   c.id_ciudadano AS id_responsable, c.apellido AS responsable_apellido,
                   c.nombre AS responsable_nombre, c.cuil AS responsable_cuil
            FROM refugios r
            JOIN ciudadanos c ON c.id_ciudadano = r.id_responsable
            """;

    private final JdbcTemplate jdbcTemplate;

    public RefugioRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Refugio> findById(Integer idRefugio) {
        try {
            Refugio refugio = jdbcTemplate.queryForObject(SELECT_BASE + " WHERE r.id_refugio = ?", MAPEADOR, idRefugio);
            return Optional.ofNullable(refugio);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public List<Refugio> findByHabilitacionMunicipalIsNotNullAndHabilitacionMunicipalNot(String vacio) {
        String sql = SELECT_BASE + " WHERE r.habilitacion_municipal IS NOT NULL AND r.habilitacion_municipal <> ?";
        return jdbcTemplate.query(sql, MAPEADOR, vacio);
    }
}
