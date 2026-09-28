package ubp.das.backndvt.repository;

import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * Busca los datos de login de un ciudadano en la tabla `ciudadanos`,
 * usando JdbcTemplate con una consulta parametrizada (nunca se arma el
 * SQL concatenando texto, para no quedar expuestos a inyección SQL).
 * Implementa RF15.
 */
@Repository
public class UsuarioRepository {

    private static final RowMapper<UsuarioLogin> MAPEADOR_USUARIO_LOGIN = (fila, numeroFila) -> new UsuarioLogin(
            fila.getInt("id_ciudadano"),
            fila.getString("apellido"),
            fila.getString("nombre"),
            fila.getString("cuil"),
            fila.getString("clave"),
            fila.getString("correo"),
            fila.getString("telefono"),
            fila.getString("domicilio"),
            fila.getBoolean("habilitado"));

    private final JdbcTemplate jdbcTemplate;

    public UsuarioRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<UsuarioLogin> buscarPorCuil(String cuil) {
        String sql = """
                SELECT id_ciudadano, apellido, nombre, cuil, clave,
                       correo, telefono, domicilio, habilitado
                FROM ciudadanos
                WHERE cuil = ?
                """;

        try {
            UsuarioLogin usuario = jdbcTemplate.queryForObject(sql, MAPEADOR_USUARIO_LOGIN, cuil);
            return Optional.ofNullable(usuario);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }
}
