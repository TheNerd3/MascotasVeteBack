package ubp.das.backndvt.repository;

import java.time.LocalDate;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;

/**
 * Punto unico de escritura para informacion_sanitaria. Nunca hacer un
 * INSERT directo (via JpaRepository.save) sobre esa tabla: el correlativo
 * nro_registro se calcula de forma atomica en la base de datos dentro del
 * stored procedure sp_InsertarInformacionSanitaria (contador en
 * mascotas.ultimo_nro_atencion), evitando condiciones de carrera entre
 * transacciones concurrentes.
 */
@Repository
public class InformacionSanitariaProcedureRepository {

    private final EntityManager entityManager;

    public InformacionSanitariaProcedureRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public Integer insertarInformacionSanitaria(
            Integer nroRegMunicipal,
            LocalDate fechaAtencion,
            Integer codTipoAtencion,
            String detalleAtencion,
            LocalDate fechaVencimiento,
            Integer idVeterinaria,
            Integer idProfesional) {

        StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery("sp_InsertarInformacionSanitaria")
                .registerStoredProcedureParameter("nro_reg_municipal", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("fecha_atencion", LocalDate.class, ParameterMode.IN)
                .registerStoredProcedureParameter("cod_tipo_atencion", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("detalle_atencion", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("fecha_vencimiento", LocalDate.class, ParameterMode.IN)
                .registerStoredProcedureParameter("id_veterinaria", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("id_profesional", Integer.class, ParameterMode.IN)
                .setParameter("nro_reg_municipal", nroRegMunicipal)
                .setParameter("fecha_atencion", fechaAtencion)
                .setParameter("cod_tipo_atencion", codTipoAtencion)
                .setParameter("detalle_atencion", detalleAtencion)
                .setParameter("fecha_vencimiento", fechaVencimiento)
                .setParameter("id_veterinaria", idVeterinaria)
                .setParameter("id_profesional", idProfesional);

        // El SP termina con "SELECT @nro_registro AS nro_registro_generado",
        // que llega como una unica fila con un unico valor numerico.
        Number nroRegistroGenerado = (Number) query.getSingleResult();
        return nroRegistroGenerado.intValue();
    }
}
