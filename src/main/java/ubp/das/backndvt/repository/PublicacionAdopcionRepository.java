package ubp.das.backndvt.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ubp.das.backndvt.entity.PublicacionAdopcion;

public interface PublicacionAdopcionRepository extends JpaRepository<PublicacionAdopcion, Integer> {

    List<PublicacionAdopcion> findByRefugioIdRefugio(Integer idRefugio);

    List<PublicacionAdopcion> findByEstadoPublicacion(String estadoPublicacion);

    Optional<PublicacionAdopcion> findByMascotaNroRegMunicipalAndEstadoPublicacion(
            Integer nroRegMunicipal, String estadoPublicacion);
}
