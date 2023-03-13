package tn.esprit.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tn.esprit.entities.RendezVous;
import tn.esprit.entities.Specialite;

@Repository
public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {


	//Question 5 : KeyWord
	List<RendezVous> findByMedecinCliniquesIdCliniqueAndMedecinSpecialite(Long idClinique, Specialite specialite);

	//Question 5 : JPQL
	@Query("SELECT r FROM RendezVous r JOIN r.medecin.cliniques c WHERE c.idClinique = :idClinique AND r.medecin.specialite = :specialite") 
	List<RendezVous> getRendezVousByCliniqueAndSpecialiteJPQL(@Param("idClinique") Long idClinique,@Param("specialite") Specialite specialite);

	
	
	// Question 6 : KeyWord
	int countByMedecinIdMedecin(Long idMedecin);
	// Question 6 : JPQL
	
	@Query("SELECT Count(r) FROM RendezVous r WHERE r.medecin.idMedecin = :idMedecin") 
	int countByMedecinIdMedecinJPQL(@Param("idMedecin") Long idMedecin);

	
	//Question 8 : JPQL
	@Query("SELECT Sum(r.medecin.prixConsultation) "
			+"FROM RendezVous r "
			+"where r.medecin.idMedecin=:idMedecin "
			+ "and r.dateRdv BETWEEN :date1 and :date2") 
	int getRevenuMedecin(@Param("idMedecin") Long idMedecin, @Param("date1") Date startDate, @Param("date2")Date endDate);


	//	Question 8 :  Keyword
	int countByMedecinIdMedecinAndDateRdvBetween(Long idMedecin,Date startDate, Date endDate);
}
