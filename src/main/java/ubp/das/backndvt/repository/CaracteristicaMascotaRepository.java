package ubp.das.backndvt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ubp.das.backndvt.entity.CaracteristicaMascota;
import ubp.das.backndvt.entity.CaracteristicaMascotaId;

public interface CaracteristicaMascotaRepository extends JpaRepository<CaracteristicaMascota, CaracteristicaMascotaId> {

    List<CaracteristicaMascota> findByNroRegMunicipal(Integer nroRegMunicipal);
}
