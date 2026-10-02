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
 * Implementa RF15, incluyendo la resolución del perfil (ciudadano,
 * refugio o veterinaria) según con qué otras tablas está vinculado.
 */
@Repository
public class UsuarioRepository {

    private static final RowMapper<UsuarioLogin> MAPEADOR_USUARIO_LOGIN = (fila, numeroFila) -> new UsuarioLogin(
            fila.getInt("id_ciudadano"),
            fila.getString("apellido"),
            fila.getString("nombre"),
            fila.getString("cuil"),
            fila.getString("clave"),
            fila.getBoolean("habilitado"));

    private final JdbcTemplate jdbcTemplate;

    public UsuarioRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<UsuarioLogin> buscarPorCuil(String cuil) {
        String sql = """
                SELECT id_ciudadano, apellido, nombre, cuil, clave, habilitado
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

    // RF15 - perfil REFUGIO: el ciudadano es responsable de algun refugio
    public Optional<Integer> buscarIdRefugioResponsable(Integer idCiudadano) {
        String sql = "SELECT id_refugio FROM refugios WHERE id_responsable = ?";
        return jdbcTemplate.query(sql, rs -> rs.next() ? Optional.of(rs.getInt("id_refugio")) : Optional.empty(),
                idCiudadano);
    }

    // RF15 - perfil VETERINARIA: el ciudadano es profesional activo (sin baja)
    // de alguna veterinaria
    public Optional<Integer> buscarIdVeterinariaProfesional(Integer idCiudadano) {
        String sql = """
                SELECT id_veterinaria
                FROM profesionales_veterinarias
                WHERE id_profesional = ? AND baja = 0
                """;
        return jdbcTemplate.query(sql,
                rs -> rs.next() ? Optional.of(rs.getInt("id_veterinaria")) : Optional.empty(),
                idCiudadano);
    }
}
