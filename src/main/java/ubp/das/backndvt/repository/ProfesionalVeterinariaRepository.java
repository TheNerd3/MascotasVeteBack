package ubp.das.backndvt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ubp.das.backndvt.entity.ProfesionalVeterinaria;
import ubp.das.backndvt.entity.ProfesionalVeterinariaId;

public interface ProfesionalVeterinariaRepository extends JpaRepository<ProfesionalVeterinaria, ProfesionalVeterinariaId> {

    List<ProfesionalVeterinaria> findByIdVeterinaria(Integer idVeterinaria);
}
