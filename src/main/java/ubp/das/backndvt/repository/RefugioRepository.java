package ubp.das.backndvt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ubp.das.backndvt.entity.Refugio;

public interface RefugioRepository extends JpaRepository<Refugio, Integer> {

    List<Refugio> findByHabilitacionMunicipalIsNotNullAndHabilitacionMunicipalNot(String vacio);
}
