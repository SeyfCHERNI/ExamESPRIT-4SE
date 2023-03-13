package tn.esprit.controllers;


import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import tn.esprit.entities.Clinique;
import tn.esprit.entities.Medecin;
import tn.esprit.entities.Patient;
import tn.esprit.entities.RendezVous;
import tn.esprit.entities.Specialite;
import tn.esprit.repositories.CliniqueRepository;
import tn.esprit.repositories.MedecinRepository;
import tn.esprit.repositories.PatientRepository;
import tn.esprit.repositories.RendezVousRepository;

@Slf4j
@RestController
@RequestMapping("rest")
public class ExamRestController {

	@Autowired
	RendezVousRepository rdvRepository;

	@Autowired
	MedecinRepository medecinRepository;

	@Autowired
	PatientRepository patientRepository;

	@Autowired
	CliniqueRepository cliniqueRepository;

	//http://localhost:9090/rest/add-clinique
	@PostMapping("/add-clinique")
	public Clinique addClinique (@RequestBody Clinique clinique) {
		return cliniqueRepository.save(clinique);
	}

	//http://localhost:9090/rest/add-medecin/{cliniqueId}
	@PostMapping("/add-medecin/{cliniqueId}")
	public Medecin addMedecinAndAssignToClinique(@RequestBody Medecin medecin,@PathVariable Long cliniqueId) {

		Clinique clinique = cliniqueRepository.findById(cliniqueId).orElse(null);

		// la clinique est le parent donc pour faire l'affectation d'une manière simple 
		// ajouter le médecin à la liste des médecins de la clinique 
		// => avant de proceder vers l'affection verifier avant qui est le parent et qui est le fils 

		clinique.getMedecins().add(medecin);
		return medecinRepository.save(medecin);
	}

	//http://localhost:9090/rest/add-patient
	@PostMapping("/add-patient")
	public void addPatient(@RequestBody Patient patient) {
		patientRepository.save(patient);
	}

	//http://localhost:9090/rest/addRDVAndAssignMedAndPatient/{idMedecin}/{idPatient}
	@PostMapping("/addRDVAndAssignMedAndPatient/{idMedecin}/{idPatient}")
	public void addRDVAndAssignMedAndPatient(@RequestBody RendezVous rdv, @PathVariable Long idMedecin,@PathVariable Long idPatient) {
		Medecin medecin = medecinRepository.findById(idMedecin).orElse(null);
		Patient patient = patientRepository.findById(idPatient).orElse(null);

		// RDV est le parent => on fait l'affectation coté Rdv

		rdv.setMedecin(medecin);
		rdv.setPatient(patient);
		rdvRepository.save(rdv);
	}

	//http://localhost:9090/rest/getRendezVousByCliniqueAndSpecialite/{idClinique}/{specialite}
	@GetMapping("/getRendezVousByCliniqueAndSpecialite/{idClinique}/{specialite}")
	public List<RendezVous> getRendezVousByCliniqueAndSpecialite(@PathVariable Long idClinique,@PathVariable Specialite specialite) {
		return rdvRepository.findByMedecinCliniquesIdCliniqueAndMedecinSpecialite(idClinique, specialite);
	}



	//http://localhost:9090/rest/getNbrRendezVousMedecin/{idMedecin}
	@GetMapping("/getNbrRendezVousMedecin/{idMedecin}")
	public int getNbrRendezVousMedecin(@PathVariable Long idMedecin) {

		// KeyWord
		return rdvRepository.countByMedecinIdMedecin(idMedecin);
	}




	//http://localhost:9090/rest/getRevenuMedecin/{idMedecin}/{startDate}/{endDate}
	@GetMapping("/getRevenuMedecin/{idMedecin}/{startDate}/{endDate}")
	public int getRevenuMedecin(@PathVariable Long idMedecin,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {


		//		int somme=0;
		//		Medecin medecin = medecinRepository.findById(idMedecin).orElse(null);
		//		for (RendezVous rdv : medecin.getRdvs()) {
		//			if (rdv.getDateRdv().after(startDate) && rdv.getDateRdv().before(endDate) ) {
		//				somme+= rdv.getMedecin().getPrixConsultation();
		//			}
		//		}
		//      return somme;


		//		//or stream
		//		Medecin medecin = medecinRepository.findById(idMedecin).orElse(null);
		//		return  medecin.getRdvs().stream()
		//				.filter(rdv -> rdv.getDateRdv().after(startDate) && rdv.getDateRdv().before(endDate)) 
		//				.count() * medecin.getPrixConsultation();
		//


		//JPQL
		//		return rdvRepository.getRevenuMedecin(idMedecin,startDate,endDate);

		// Keyword
		Medecin medecin = medecinRepository.findById(idMedecin).orElse(null);	
		return rdvRepository.countByMedecinIdMedecinAndDateRdvBetween(idMedecin,startDate,endDate) *  medecin.getPrixConsultation();


	}


	//N'oubliez pas d'ajouter l'annotation @EnableScheduling au niveau du classe main.
		@Scheduled(cron = "*/30 * * * * *")
	public void retriveRend_vous() {
		List<RendezVous> rdvs = rdvRepository.findAll();
		for (RendezVous RendezVous : rdvs) {
			if (RendezVous.getDateRdv().after(new Date()))
				log.info("La liste des RendezVous : " + RendezVous.getDateRdv() + " : Medecin :"
						+ RendezVous.getMedecin().getNomMedecin() + " : Patient :"
						+ RendezVous.getPatient().getNomPatient());
		}
	}



}
