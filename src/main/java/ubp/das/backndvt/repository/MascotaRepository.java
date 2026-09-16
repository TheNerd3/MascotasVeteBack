package ubp.das.backndvt.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ubp.das.backndvt.entity.Mascota;

public interface MascotaRepository extends JpaRepository<Mascota, Integer> {

    Optional<Mascota> findByMicrochip(String microchip);

    List<Mascota> findByNombreContainingIgnoreCase(String nombre);

    List<Mascota> findByResponsableIdCiudadano(Integer idResponsable);

    List<Mascota> findByRefugioIdRefugio(Integer idRefugio);
}
