package br.com.rrstecnologia.pharmastock.repository;

import br.com.rrstecnologia.pharmastock.entity.Pharma;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PharmaRepository extends JpaRepository<Pharma, Long> {

    Optional<Pharma> findByName(String name);

}
