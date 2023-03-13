	package tn.esprit.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tn.esprit.entities.Clinique;

@Repository
public interface CliniqueRepository extends JpaRepository<Clinique, Long> {

}
