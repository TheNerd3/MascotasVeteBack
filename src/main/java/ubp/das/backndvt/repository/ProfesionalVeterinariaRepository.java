package ubp.das.backndvt.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import ubp.das.backndvt.entity.ProfesionalVeterinaria;
import ubp.das.backndvt.entity.Veterinaria;

@Repository
public class ProfesionalVeterinariaRepository {

    private static final RowMapper<ProfesionalVeterinaria> MAPEADOR = (fila, numeroFila) -> {
        Veterinaria veterinaria = new Veterinaria();
        veterinaria.setIdVeterinaria(fila.getInt("id_veterinaria"));

        ProfesionalVeterinaria profesional = new ProfesionalVeterinaria();
        profesional.setIdVeterinaria(fila.getInt("id_veterinaria"));
        profesional.setIdProfesional(fila.getInt("id_profesional"));
        profesional.setBaja(fila.getBoolean("baja"));
        profesional.setVeterinaria(veterinaria);
        return profesional;
    };

    private static final String SELECT_BASE =
            "SELECT id_veterinaria, id_profesional, baja FROM profesionales_veterinarias";

    private final JdbcTemplate jdbcTemplate;

    public ProfesionalVeterinariaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<ProfesionalVeterinaria> findById(Integer idVeterinaria, Integer idProfesional) {
        try {
            ProfesionalVeterinaria profesional = jdbcTemplate.queryForObject(
                    SELECT_BASE + " WHERE id_veterinaria = ? AND id_profesional = ?",
                    MAPEADOR, idVeterinaria, idProfesional);
            return Optional.ofNullable(profesional);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public List<ProfesionalVeterinaria> findByIdVeterinaria(Integer idVeterinaria) {
        return jdbcTemplate.query(SELECT_BASE + " WHERE id_veterinaria = ?", MAPEADOR, idVeterinaria);
    }
}
