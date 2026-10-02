package ubp.das.backndvt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ubp.das.backndvt.entity.Veterinaria;

public interface VeterinariaRepository extends JpaRepository<Veterinaria, Integer> {

    List<Veterinaria> findByHabilitacionMunicipalIsNotNullAndHabilitacionMunicipalNot(String vacio);
}
