package ubp.das.backndvt.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import ubp.das.backndvt.entity.Veterinaria;

@Repository
public class VeterinariaRepository {

    private static final RowMapper<Veterinaria> MAPEADOR = (fila, numeroFila) -> {
        Veterinaria veterinaria = new Veterinaria();
        veterinaria.setIdVeterinaria(fila.getInt("id_veterinaria"));
        veterinaria.setRazonSocial(fila.getString("razon_social"));
        veterinaria.setCorreo(fila.getString("correo"));
        veterinaria.setTelefono(fila.getString("telefono"));
        veterinaria.setDomicilio(fila.getString("domicilio"));
        veterinaria.setHabilitacionMunicipal(fila.getString("habilitacion_municipal"));
        return veterinaria;
    };

    private static final String SELECT_BASE =
            "SELECT id_veterinaria, razon_social, correo, telefono, domicilio, habilitacion_municipal FROM veterinarias";

    private final JdbcTemplate jdbcTemplate;

    public VeterinariaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Veterinaria> findById(Integer idVeterinaria) {
        try {
            Veterinaria veterinaria = jdbcTemplate.queryForObject(
                    SELECT_BASE + " WHERE id_veterinaria = ?", MAPEADOR, idVeterinaria);
            return Optional.ofNullable(veterinaria);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public List<Veterinaria> findByHabilitacionMunicipalIsNotNullAndHabilitacionMunicipalNot(String vacio) {
        String sql = SELECT_BASE + " WHERE habilitacion_municipal IS NOT NULL AND habilitacion_municipal <> ?";
        return jdbcTemplate.query(sql, MAPEADOR, vacio);
    }
}
