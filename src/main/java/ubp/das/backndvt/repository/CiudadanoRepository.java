package ubp.das.backndvt.repository;

import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import ubp.das.backndvt.entity.Ciudadano;

@Repository
public class CiudadanoRepository {

    private static final RowMapper<Ciudadano> MAPEADOR = (fila, numeroFila) -> {
        Ciudadano ciudadano = new Ciudadano();
        ciudadano.setIdCiudadano(fila.getInt("id_ciudadano"));
        ciudadano.setApellido(fila.getString("apellido"));
        ciudadano.setNombre(fila.getString("nombre"));
        ciudadano.setCuil(fila.getString("cuil"));
        ciudadano.setClave(fila.getString("clave"));
        ciudadano.setCorreo(fila.getString("correo"));
        ciudadano.setTelefono(fila.getString("telefono"));
        ciudadano.setDomicilio(fila.getString("domicilio"));
        ciudadano.setHabilitado(fila.getBoolean("habilitado"));
        return ciudadano;
    };

    private static final String SELECT_BASE =
            "SELECT id_ciudadano, apellido, nombre, cuil, clave, correo, telefono, domicilio, habilitado FROM ciudadanos";

    private final JdbcTemplate jdbcTemplate;

    public CiudadanoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Ciudadano> findById(Integer idCiudadano) {
        try {
            Ciudadano ciudadano = jdbcTemplate.queryForObject(
                    SELECT_BASE + " WHERE id_ciudadano = ?", MAPEADOR, idCiudadano);
            return Optional.ofNullable(ciudadano);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public Optional<Ciudadano> findByCuil(String cuil) {
        try {
            Ciudadano ciudadano = jdbcTemplate.queryForObject(SELECT_BASE + " WHERE cuil = ?", MAPEADOR, cuil);
            return Optional.ofNullable(ciudadano);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public Ciudadano save(Ciudadano ciudadano) {
        String sql = """
                INSERT INTO ciudadanos (apellido, nombre, cuil, clave, correo, telefono, domicilio, habilitado)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            var statement = connection.prepareStatement(sql, new String[] { "id_ciudadano" });
            statement.setString(1, ciudadano.getApellido());
            statement.setString(2, ciudadano.getNombre());
            statement.setString(3, ciudadano.getCuil());
            statement.setString(4, ciudadano.getClave());
            statement.setString(5, ciudadano.getCorreo());
            statement.setString(6, ciudadano.getTelefono());
            statement.setString(7, ciudadano.getDomicilio());
            statement.setBoolean(8, Boolean.TRUE.equals(ciudadano.getHabilitado()));
            return statement;
        }, keyHolder);

        ciudadano.setIdCiudadano(keyHolder.getKey().intValue());
        return ciudadano;
    }
}
