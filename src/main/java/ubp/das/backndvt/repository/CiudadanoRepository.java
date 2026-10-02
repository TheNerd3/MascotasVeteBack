package ubp.das.backndvt.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ubp.das.backndvt.entity.Ciudadano;

public interface CiudadanoRepository extends JpaRepository<Ciudadano, Integer> {

    Optional<Ciudadano> findByCuil(String cuil);
}
