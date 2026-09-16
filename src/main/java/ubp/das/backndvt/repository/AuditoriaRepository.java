package ubp.das.backndvt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ubp.das.backndvt.entity.Auditoria;

/**
 * Solo lectura: la tabla auditoria la llenan los triggers TR_*_Audit,
 * nunca se debe escribir en ella desde la aplicacion.
 */
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    List<Auditoria> findByNombreTablaOrderByFechaOperacionDesc(String nombreTabla);
}
