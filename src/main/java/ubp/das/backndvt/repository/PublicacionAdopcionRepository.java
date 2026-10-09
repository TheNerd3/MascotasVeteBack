package ubp.das.backndvt.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import ubp.das.backndvt.entity.EstadoPublicacion;
import ubp.das.backndvt.entity.PublicacionAdopcion;

public interface PublicacionAdopcionRepository extends JpaRepository<PublicacionAdopcion, Integer> {

    // RF13 - "Mis publicaciones": listado paginado del refugio autenticado,
    // con filtro opcional por estado.
    Page<PublicacionAdopcion> findByRefugioIdRefugio(Integer idRefugio, Pageable pageable);

    Page<PublicacionAdopcion> findByRefugioIdRefugioAndEstadoPublicacion(
            Integer idRefugio, EstadoPublicacion estadoPublicacion, Pageable pageable);

    // RF18 - Listado publico: solo publicaciones Activas de un refugio.
    List<PublicacionAdopcion> findByRefugioIdRefugioAndEstadoPublicacionOrderByFechaPublicacionDesc(
            Integer idRefugio, EstadoPublicacion estadoPublicacion);

    Optional<PublicacionAdopcion> findByMascotaNroRegMunicipalAndEstadoPublicacion(
            Integer nroRegMunicipal, EstadoPublicacion estadoPublicacion);
}
