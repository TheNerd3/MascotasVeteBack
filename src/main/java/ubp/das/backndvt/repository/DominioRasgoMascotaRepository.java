package ubp.das.backndvt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ubp.das.backndvt.entity.DominioRasgoMascota;
import ubp.das.backndvt.entity.DominioRasgoMascotaId;

public interface DominioRasgoMascotaRepository extends JpaRepository<DominioRasgoMascota, DominioRasgoMascotaId> {

    List<DominioRasgoMascota> findByCodRasgo(Integer codRasgo);
}
