package ubp.das.backndvt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ubp.das.backndvt.entity.InformacionSanitaria;
import ubp.das.backndvt.entity.InformacionSanitariaId;

public interface InformacionSanitariaRepository extends JpaRepository<InformacionSanitaria, InformacionSanitariaId> {

    List<InformacionSanitaria> findByNroRegMunicipal(Integer nroRegMunicipal);
}
