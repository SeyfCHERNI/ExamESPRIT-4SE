package tn.esprit.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tn.esprit.entities.Medecin;

@Repository
public interface MedecinRepository extends JpaRepository<Medecin, Long> {

}
