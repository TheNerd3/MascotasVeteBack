package ubp.das.backndvt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ubp.das.backndvt.entity.ConfiguracionVeterinaria;
import ubp.das.backndvt.entity.ConfiguracionVeterinariaId;

public interface ConfiguracionVeterinariaRepository extends JpaRepository<ConfiguracionVeterinaria, ConfiguracionVeterinariaId> {

    List<ConfiguracionVeterinaria> findByIdVeterinaria(Integer idVeterinaria);
}
