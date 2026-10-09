package ubp.das.backndvt.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import ubp.das.backndvt.entity.InformacionSanitaria;
import ubp.das.backndvt.entity.ProfesionalVeterinaria;
import ubp.das.backndvt.entity.TipoAtencionSanitaria;
import ubp.das.backndvt.entity.Veterinaria;

@Repository
public class InformacionSanitariaRepository {

    private static final RowMapper<InformacionSanitaria> MAPEADOR = (fila, numeroFila) -> {
        TipoAtencionSanitaria tipoAtencion = new TipoAtencionSanitaria();
        tipoAtencion.setCodTipoAtencion(fila.getInt("cod_tipo_atencion"));
        tipoAtencion.setDescTipoAtencion(fila.getString("desc_tipo_atencion"));

        Veterinaria veterinaria = new Veterinaria();
        veterinaria.setIdVeterinaria(fila.getInt("id_veterinaria"));

        ProfesionalVeterinaria profesionalVeterinaria = new ProfesionalVeterinaria();
        profesionalVeterinaria.setIdVeterinaria(fila.getInt("id_veterinaria"));
        profesionalVeterinaria.setIdProfesional(fila.getInt("id_profesional"));
        profesionalVeterinaria.setVeterinaria(veterinaria);

        InformacionSanitaria informacion = new InformacionSanitaria();
        informacion.setNroRegMunicipal(fila.getInt("nro_reg_municipal"));
        informacion.setNroRegistro(fila.getInt("nro_registro"));
        informacion.setFechaAtencion(fila.getObject("fecha_atencion", LocalDate.class));
        informacion.setCodTipoAtencion(fila.getInt("cod_tipo_atencion"));
        informacion.setDetalleAtencion(fila.getString("detalle_atencion"));
        informacion.setFechaVencimiento(fila.getObject("fecha_vencimiento", LocalDate.class));
        informacion.setIdVeterinaria(fila.getInt("id_veterinaria"));
        informacion.setIdProfesional(fila.getInt("id_profesional"));
        informacion.setTipoAtencion(tipoAtencion);
        informacion.setProfesionalVeterinaria(profesionalVeterinaria);
        return informacion;
    };

    private static final String SELECT_BASE = """
            SELECT i.nro_reg_municipal, i.nro_registro, i.fecha_atencion, i.cod_tipo_atencion,
                   i.detalle_atencion, i.fecha_vencimiento, i.id_veterinaria, i.id_profesional,
                   t.desc_tipo_atencion
            FROM informacion_sanitaria i
            JOIN tipos_atencion_sanitaria t ON t.cod_tipo_atencion = i.cod_tipo_atencion
            """;

    private final JdbcTemplate jdbcTemplate;

    public InformacionSanitariaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<InformacionSanitaria> findByNroRegMunicipal(Integer nroRegMunicipal) {
        return jdbcTemplate.query(SELECT_BASE + " WHERE i.nro_reg_municipal = ?", MAPEADOR, nroRegMunicipal);
    }
}
