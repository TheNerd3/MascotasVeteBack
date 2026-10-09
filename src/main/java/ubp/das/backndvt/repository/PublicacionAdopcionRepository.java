package ubp.das.backndvt.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import ubp.das.backndvt.dto.PublicacionAdopcionResponse;
import ubp.das.backndvt.entity.EstadoPublicacion;
import ubp.das.backndvt.entity.EstadoPublicacion.Accion;
import ubp.das.backndvt.entity.PublicacionAdopcion;

/**
 * RF13/RF18 - Publicaciones de adopcion. El listado resuelve
 * especie/raza con 2 LEFT JOIN directos contra caracteristicas_mascotas
 * (uno por el rasgo Especie, otro por Raza) en la misma consulta, en
 * vez de una query aparte por publicacion (evita el N+1 que tenia la
 * version JPA). El estado se guarda y se lee como texto plano
 * ("Activa"/"Pausada"/"Finalizada", igual que el CHECK de la base),
 * nunca como el name() del enum Java.
 */
@Repository
public class PublicacionAdopcionRepository {

    private static final String RASGO_ESPECIE = "Especie";
    private static final String RASGO_RAZA = "Raza";

    private static final RowMapper<PublicacionAdopcionResponse> MAPEADOR_RESPONSE = (fila, numeroFila) -> {
        EstadoPublicacion estado = EstadoPublicacion.desdeNombreEnBase(fila.getString("estado_publicacion"));

        return new PublicacionAdopcionResponse(
                fila.getInt("nro_publicacion"),
                fila.getInt("nro_reg_municipal"),
                fila.getString("nombre"),
                fila.getString("sexo"),
                fila.getObject("año_nacimiento") != null ? fila.getShort("año_nacimiento") : null,
                fila.getString("especie"),
                fila.getString("raza"),
                fila.getInt("id_refugio"),
                fila.getObject("fecha_publicacion", LocalDate.class),
                fila.getString("caracteristicas_mascota"),
                fila.getString("condicion_adopcion"),
                estado,
                estado.accionesDisponibles(),
                fila.getBytes("foto") != null);
    };

    private static final RowMapper<PublicacionAdopcion> MAPEADOR_ENTIDAD = (fila, numeroFila) -> {
        PublicacionAdopcion publicacion = new PublicacionAdopcion();
        publicacion.setNroPublicacion(fila.getInt("nro_publicacion"));
        publicacion.setNroRegMunicipal(fila.getInt("nro_reg_municipal"));
        publicacion.setIdRefugio(fila.getInt("id_refugio"));
        publicacion.setFechaPublicacion(fila.getObject("fecha_publicacion", LocalDate.class));
        publicacion.setCaracteristicasMascota(fila.getString("caracteristicas_mascota"));
        publicacion.setCondicionAdopcion(fila.getString("condicion_adopcion"));
        publicacion.setFoto(fila.getBytes("foto"));
        publicacion.setEstadoPublicacion(EstadoPublicacion.desdeNombreEnBase(fila.getString("estado_publicacion")));
        return publicacion;
    };

    private static final String SELECT_RESPONSE_BASE = """
            SELECT p.nro_publicacion, p.nro_reg_municipal, p.id_refugio, p.fecha_publicacion,
                   p.caracteristicas_mascota, p.condicion_adopcion, p.foto, p.estado_publicacion,
                   m.nombre, m.sexo, m.año_nacimiento,
                   especie.nom_valor_dominio AS especie, raza.nom_valor_dominio AS raza
            FROM publicaciones_adopcion p
            JOIN mascotas m ON m.nro_reg_municipal = p.nro_reg_municipal
            LEFT JOIN caracteristicas_mascotas ce ON ce.nro_reg_municipal = p.nro_reg_municipal
                AND ce.cod_rasgo = (SELECT cod_rasgo FROM rasgos_mascotas WHERE nom_rasgo = 'Especie')
            LEFT JOIN dominio_rasgos_mascotas especie ON especie.cod_rasgo = ce.cod_rasgo
                AND especie.nro_valor_dominio = ce.nro_valor_dominio
            LEFT JOIN caracteristicas_mascotas cr ON cr.nro_reg_municipal = p.nro_reg_municipal
                AND cr.cod_rasgo = (SELECT cod_rasgo FROM rasgos_mascotas WHERE nom_rasgo = 'Raza')
            LEFT JOIN dominio_rasgos_mascotas raza ON raza.cod_rasgo = cr.cod_rasgo
                AND raza.nro_valor_dominio = cr.nro_valor_dominio
            """;

    private final JdbcTemplate jdbcTemplate;

    public PublicacionAdopcionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<PublicacionAdopcion> findById(Integer nroPublicacion) {
        String sql = """
                SELECT nro_publicacion, nro_reg_municipal, id_refugio, fecha_publicacion,
                       caracteristicas_mascota, condicion_adopcion, foto, estado_publicacion
                FROM publicaciones_adopcion
                WHERE nro_publicacion = ?
                """;
        try {
            PublicacionAdopcion publicacion = jdbcTemplate.queryForObject(sql, MAPEADOR_ENTIDAD, nroPublicacion);
            return Optional.ofNullable(publicacion);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public Optional<PublicacionAdopcionResponse> findResponseById(Integer nroPublicacion) {
        try {
            PublicacionAdopcionResponse respuesta = jdbcTemplate.queryForObject(
                    SELECT_RESPONSE_BASE + " WHERE p.nro_publicacion = ?", MAPEADOR_RESPONSE, nroPublicacion);
            return Optional.ofNullable(respuesta);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public Optional<byte[]> findFoto(Integer nroPublicacion) {
        String sql = "SELECT foto FROM publicaciones_adopcion WHERE nro_publicacion = ?";
        try {
            byte[] foto = jdbcTemplate.queryForObject(sql, (fila, numeroFila) -> fila.getBytes("foto"), nroPublicacion);
            return Optional.ofNullable(foto);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public Optional<String> findEstado(Integer nroPublicacion) {
        String sql = "SELECT estado_publicacion FROM publicaciones_adopcion WHERE nro_publicacion = ?";
        try {
            String estado = jdbcTemplate.queryForObject(sql, (fila, numeroFila) -> fila.getString("estado_publicacion"),
                    nroPublicacion);
            return Optional.ofNullable(estado);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public Optional<Integer> findIdRefugioById(Integer nroPublicacion) {
        String sql = "SELECT id_refugio FROM publicaciones_adopcion WHERE nro_publicacion = ?";
        try {
            Integer idRefugio = jdbcTemplate.queryForObject(sql, Integer.class, nroPublicacion);
            return Optional.ofNullable(idRefugio);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public Page<PublicacionAdopcionResponse> findByRefugio(Integer idRefugio, EstadoPublicacion estado, Pageable pageable) {
        StringBuilder where = new StringBuilder(" WHERE p.id_refugio = ?");
        List<Object> parametros = new java.util.ArrayList<>(List.of(idRefugio));
        if (estado != null) {
            where.append(" AND p.estado_publicacion = ?");
            parametros.add(estado.getNombreEnBase());
        }

        String conteo = "SELECT COUNT(*) FROM publicaciones_adopcion p" + where;
        Integer total = jdbcTemplate.queryForObject(conteo, Integer.class, parametros.toArray());

        String orden = " ORDER BY p.fecha_publicacion DESC"
                + " OFFSET " + pageable.getOffset() + " ROWS FETCH NEXT " + pageable.getPageSize() + " ROWS ONLY";
        String sql = SELECT_RESPONSE_BASE + where + orden;

        List<PublicacionAdopcionResponse> contenido = jdbcTemplate.query(sql, MAPEADOR_RESPONSE, parametros.toArray());
        return new PageImpl<>(contenido, pageable, total != null ? total : 0);
    }

    public List<PublicacionAdopcionResponse> findActivasPorRefugioOrdenadoPorFecha(Integer idRefugio) {
        String sql = SELECT_RESPONSE_BASE + " WHERE p.id_refugio = ? AND p.estado_publicacion = ?"
                + " ORDER BY p.fecha_publicacion DESC";
        return jdbcTemplate.query(sql, MAPEADOR_RESPONSE, idRefugio, EstadoPublicacion.ACTIVA.getNombreEnBase());
    }

    public Optional<PublicacionAdopcion> findActivaPorMascota(Integer nroRegMunicipal) {
        String sql = """
                SELECT nro_publicacion, nro_reg_municipal, id_refugio, fecha_publicacion,
                       caracteristicas_mascota, condicion_adopcion, foto, estado_publicacion
                FROM publicaciones_adopcion
                WHERE nro_reg_municipal = ? AND estado_publicacion = ?
                """;
        try {
            PublicacionAdopcion publicacion = jdbcTemplate.queryForObject(
                    sql, MAPEADOR_ENTIDAD, nroRegMunicipal, EstadoPublicacion.ACTIVA.getNombreEnBase());
            return Optional.ofNullable(publicacion);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public PublicacionAdopcion save(PublicacionAdopcion publicacion) {
        if (publicacion.getNroPublicacion() == null) {
            return insertar(publicacion);
        }
        return actualizarEstado(publicacion);
    }

    private PublicacionAdopcion insertar(PublicacionAdopcion publicacion) {
        String sql = """
                INSERT INTO publicaciones_adopcion
                    (nro_reg_municipal, id_refugio, fecha_publicacion, caracteristicas_mascota,
                     condicion_adopcion, foto, estado_publicacion)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            var statement = connection.prepareStatement(sql, new String[] { "nro_publicacion" });
            statement.setInt(1, publicacion.getNroRegMunicipal());
            statement.setInt(2, publicacion.getIdRefugio());
            statement.setObject(3, publicacion.getFechaPublicacion());
            statement.setString(4, publicacion.getCaracteristicasMascota());
            statement.setString(5, publicacion.getCondicionAdopcion());
            if (publicacion.getFoto() != null) {
                statement.setBytes(6, publicacion.getFoto());
            } else {
                statement.setNull(6, Types.VARBINARY);
            }
            statement.setString(7, publicacion.getEstadoPublicacion().getNombreEnBase());
            return statement;
        }, keyHolder);

        publicacion.setNroPublicacion(keyHolder.getKey().intValue());
        return publicacion;
    }

    private PublicacionAdopcion actualizarEstado(PublicacionAdopcion publicacion) {
        String sql = "UPDATE publicaciones_adopcion SET estado_publicacion = ? WHERE nro_publicacion = ?";
        jdbcTemplate.update(sql, publicacion.getEstadoPublicacion().getNombreEnBase(), publicacion.getNroPublicacion());
        return publicacion;
    }
}
