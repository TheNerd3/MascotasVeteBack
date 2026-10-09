package ubp.das.backndvt.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import ubp.das.backndvt.entity.Ciudadano;
import ubp.das.backndvt.entity.Mascota;
import ubp.das.backndvt.entity.Refugio;

@Repository
public class MascotaRepository {

    private static final RowMapper<Mascota> MAPEADOR = (fila, numeroFila) -> {
        Ciudadano responsable = new Ciudadano();
        responsable.setIdCiudadano(fila.getInt("id_responsable"));

        Mascota mascota = new Mascota();
        mascota.setNroRegMunicipal(fila.getInt("nro_reg_municipal"));
        mascota.setNombre(fila.getString("nombre"));
        mascota.setSexo(fila.getString("sexo"));
        mascota.setAnioNacimiento(fila.getObject("año_nacimiento") != null ? fila.getShort("año_nacimiento") : null);
        mascota.setMicrochip(fila.getString("microchip"));
        mascota.setVive(fila.getBoolean("vive"));
        mascota.setResponsable(responsable);
        mascota.setUltimoNroAtencion(fila.getInt("ultimo_nro_atencion"));

        Integer idRefugio = fila.getObject("id_refugio") != null ? fila.getInt("id_refugio") : null;
        if (idRefugio != null) {
            Refugio refugio = new Refugio();
            refugio.setIdRefugio(idRefugio);
            mascota.setRefugio(refugio);
        }
        return mascota;
    };

    private static final String SELECT_BASE = """
            SELECT nro_reg_municipal, nombre, sexo, año_nacimiento, microchip, vive,
                   id_responsable, id_refugio, ultimo_nro_atencion
            FROM mascotas
            """;

    private final JdbcTemplate jdbcTemplate;

    public MascotaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Mascota> findById(Integer nroRegMunicipal) {
        try {
            Mascota mascota = jdbcTemplate.queryForObject(
                    SELECT_BASE + " WHERE nro_reg_municipal = ?", MAPEADOR, nroRegMunicipal);
            return Optional.ofNullable(mascota);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public Optional<Mascota> findByMicrochip(String microchip) {
        try {
            Mascota mascota = jdbcTemplate.queryForObject(SELECT_BASE + " WHERE microchip = ?", MAPEADOR, microchip);
            return Optional.ofNullable(mascota);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public List<Mascota> findByNombreContainingIgnoreCase(String nombre) {
        return jdbcTemplate.query(SELECT_BASE + " WHERE LOWER(nombre) LIKE LOWER(?)", MAPEADOR, "%" + nombre + "%");
    }

    public List<Mascota> findByResponsableIdCiudadano(Integer idResponsable) {
        return jdbcTemplate.query(SELECT_BASE + " WHERE id_responsable = ?", MAPEADOR, idResponsable);
    }

    public List<Mascota> findByRefugioIdRefugio(Integer idRefugio) {
        return jdbcTemplate.query(SELECT_BASE + " WHERE id_refugio = ?", MAPEADOR, idRefugio);
    }

    public Mascota save(Mascota mascota) {
        String sql = """
                INSERT INTO mascotas (nombre, sexo, año_nacimiento, microchip, vive, id_responsable, id_refugio, ultimo_nro_atencion)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            var statement = connection.prepareStatement(sql, new String[] { "nro_reg_municipal" });
            statement.setString(1, mascota.getNombre());
            statement.setString(2, mascota.getSexo());
            if (mascota.getAnioNacimiento() != null) {
                statement.setShort(3, mascota.getAnioNacimiento());
            } else {
                statement.setNull(3, java.sql.Types.SMALLINT);
            }
            statement.setString(4, mascota.getMicrochip());
            statement.setBoolean(5, Boolean.TRUE.equals(mascota.getVive()));
            statement.setInt(6, mascota.getResponsable().getIdCiudadano());
            if (mascota.getRefugio() != null) {
                statement.setInt(7, mascota.getRefugio().getIdRefugio());
            } else {
                statement.setNull(7, java.sql.Types.INTEGER);
            }
            statement.setInt(8, mascota.getUltimoNroAtencion() != null ? mascota.getUltimoNroAtencion() : 0);
            return statement;
        }, keyHolder);

        mascota.setNroRegMunicipal(keyHolder.getKey().intValue());
        return mascota;
    }
}
