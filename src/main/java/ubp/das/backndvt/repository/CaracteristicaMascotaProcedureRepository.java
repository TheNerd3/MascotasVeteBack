package ubp.das.backndvt.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;

/**
 * Punto unico de escritura para caracteristicas_mascotas. Nunca hacer un
 * INSERT directo (via JpaRepository.save) sobre esa tabla: el correlativo
 * nro_caracteristica se calcula de forma atomica en la base de datos
 * dentro del stored procedure sp_InsertarCaracteristicaMascota (tabla de
 * contadores contador_caracteristicas por mascota+rasgo), evitando
 * condiciones de carrera entre transacciones concurrentes.
 */
@Repository
public class CaracteristicaMascotaProcedureRepository {

    private final EntityManager entityManager;

    public CaracteristicaMascotaProcedureRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public Integer insertarCaracteristicaMascota(
            Integer nroRegMunicipal,
            Integer codRasgo,
            Integer nroValorDominio,
            String valorCaracteristica) {

        StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery("sp_InsertarCaracteristicaMascota")
                .registerStoredProcedureParameter("nro_reg_municipal", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("cod_rasgo", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("nro_valor_dominio", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("valor_caracteristica", String.class, ParameterMode.IN)
                .setParameter("nro_reg_municipal", nroRegMunicipal)
                .setParameter("cod_rasgo", codRasgo)
                .setParameter("nro_valor_dominio", nroValorDominio)
                .setParameter("valor_caracteristica", valorCaracteristica);

        // El SP termina con "SELECT @nro_caracteristica AS
        // nro_caracteristica_generado", una unica fila con un unico valor.
        Number nroCaracteristicaGenerado = (Number) query.getSingleResult();
        return nroCaracteristicaGenerado.intValue();
    }
}
