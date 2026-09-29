package ubp.das.backndvt.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import ubp.das.backndvt.entity.Mascota;

// RF06 - Registrar mascotas: busca duplicados usando sp_BuscarMascotaExistente
// (microchip prioritario; si no viene, cae a nombre + año_nacimiento + responsable).
@Repository
public class MascotaExistenteProcedureRepository {

    private final EntityManager entityManager;

    public MascotaExistenteProcedureRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Optional<Mascota> buscarExistente(
            String nombre,
            Short anioNacimiento,
            Integer idResponsable,
            String microchip) {

        StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery("sp_BuscarMascotaExistente", Mascota.class)
                .registerStoredProcedureParameter("nombre", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("anio_nacimiento", Short.class, ParameterMode.IN)
                .registerStoredProcedureParameter("id_responsable", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("microchip", String.class, ParameterMode.IN)
                .setParameter("nombre", nombre)
                .setParameter("anio_nacimiento", anioNacimiento)
                .setParameter("id_responsable", idResponsable)
                .setParameter("microchip", microchip);

        try {
            @SuppressWarnings("unchecked")
            Mascota mascota = (Mascota) query.getSingleResult();
            return Optional.ofNullable(mascota);
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }
}
