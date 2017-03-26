package com.tuktukteam.tuktuk.restcontroller;

import java.util.Date;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tuktukteam.autosecurity.AutoFilterForSpringControllers;
import com.tuktukteam.tuktuk.dao.ClientDAO;
import com.tuktukteam.tuktuk.dao.ConducteurDAO;
import com.tuktukteam.tuktuk.dao.CourseDAO;
import com.tuktukteam.tuktuk.model.Client;
import com.tuktukteam.tuktuk.model.Conducteur;
import com.tuktukteam.tuktuk.model.Course;

@RestController
public class CourseRestController {

	@Autowired
	private ClientDAO clientDAO;

	@Autowired
	private CourseDAO courseDAO;

	@Autowired
	private ConducteurDAO conducteurDAO;

	public CourseRestController() { AutoFilterForSpringControllers.addController(getClass(), "/api"); }
	
	@RequestMapping(value = "/course/prise_en_charge", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Course> createRun(HttpSession session, @RequestParam String adresseDepart) {

		Client c = (Client) session.getAttribute("client");

		if (c != null) {
			Course course = new Course();
			int idClient = c.getId();
			course.setAdresseDepart(adresseDepart);
			course.setClient(clientDAO.find(idClient));
			course.setNoteClient(-1);
			course.setNoteConducteur(-1);
			course.setValide(false);
			return new ResponseEntity<Course>(courseDAO.save(course), HttpStatus.OK);
		}
		return new ResponseEntity<Course>(HttpStatus.FORBIDDEN);
	}

	@RequestMapping(value = "/course/{idCourse}/accepter", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<Course> acceptRun(HttpSession session, @PathVariable int idCourse) {

		Course course = courseDAO.find(idCourse);
		Conducteur c = (Conducteur) session.getAttribute("conducteur");

		if (c != null) {
			int idConducteur = c.getId();

			if (course.getConducteur() == null) {
				course.setConducteur(conducteurDAO.find(idConducteur));
				course = courseDAO.save(course);
			}
			return new ResponseEntity<Course>(course, HttpStatus.OK);
		}
		return new ResponseEntity<Course>(HttpStatus.FORBIDDEN);
	}

	@RequestMapping(value = "/course/{id}/valider", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<Course> valideRun(@PathVariable int id, HttpSession session) {
		Client client = (Client) session.getAttribute("client");
		Course course = courseDAO.find(id);

		if (client != null && client.getId() == course.getClient().getId()) {
			course.setValide(true);
			course = courseDAO.save(course);
			return new ResponseEntity<Course>(course, HttpStatus.OK);
		}

		return new ResponseEntity<Course>(HttpStatus.FORBIDDEN);
	}

	@RequestMapping(value = "/course/{id}/refuser", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<Course> refuseRun(@PathVariable int id, HttpSession session) {
		Client client = (Client) session.getAttribute("client");
		Course course = courseDAO.find(id);

		if (client != null && client.getId() == course.getClient().getId()) {
			course.setConducteur(null);
			course = courseDAO.save(course);
			return new ResponseEntity<Course>(course, HttpStatus.OK);
		}
		return new ResponseEntity<Course>(HttpStatus.FORBIDDEN);
	}

	@RequestMapping(value = "/course/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Course> refreshRun(@PathVariable int id, HttpSession session) {
		Conducteur cond = (Conducteur) session.getAttribute("conducteur");
		Client client = (Client) session.getAttribute("client");

		if (cond != null || client != null) {
			Course course = courseDAO.find(id);
			return new ResponseEntity<Course>(course, HttpStatus.OK);
		}

		return new ResponseEntity<Course>(HttpStatus.FORBIDDEN);
	}

	@RequestMapping(value = "/course/{id}/demarrer", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<Course> startRun(@PathVariable int id, HttpSession session) {
		Conducteur cond = (Conducteur) session.getAttribute("conducteur");
		Course course = courseDAO.find(id);

		if (cond != course.getConducteur() && course.isValide()) {
			course.setDateDebutCourse(new Date());
			course = courseDAO.save(course);
			return new ResponseEntity<Course>(course, HttpStatus.OK);
		}

		return new ResponseEntity<Course>(HttpStatus.FORBIDDEN);
	}

	@RequestMapping(value = "/course/{id}/terminer", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<Course> endRun(@PathVariable int id, HttpSession session) {
		Conducteur cond = (Conducteur) session.getAttribute("conducteur");
		Course course = courseDAO.find(id);

		if (cond == course.getConducteur() && course.isValide() && course.getDateDebutCourse() != null) {
			course.setDateFinCourse(new Date());
			course = courseDAO.save(course);
			return new ResponseEntity<Course>(course, HttpStatus.OK);
		}
		return new ResponseEntity<Course>(HttpStatus.FORBIDDEN);
	}
	
	

	@RequestMapping(value = "/course/{id}/commenter", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<Course> comment(@PathVariable int id, HttpSession session, @RequestParam float note, @RequestParam String commentaire) {
		Conducteur cond = (Conducteur) session.getAttribute("conducteur");
		Client cli = (Client) session.getAttribute("client");
		Course course = courseDAO.find(id);

		if (course.getDateFinCourse() != null) {
			if (cond != null) {
				course.setNoteConducteur(note);
				course.setComConducteur(commentaire);
				course = courseDAO.save(course);
				return new ResponseEntity<Course>(course, HttpStatus.OK);
			}else if(cli != null){
				course.setNoteClient(note);
				course.setComClient(commentaire);
				course = courseDAO.save(course);
				return new ResponseEntity<Course>(course, HttpStatus.OK);
			}
		}		
		return new ResponseEntity<Course>(HttpStatus.FORBIDDEN);
	}

}
