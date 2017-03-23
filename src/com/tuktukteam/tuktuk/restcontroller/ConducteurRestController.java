package com.tuktukteam.tuktuk.restcontroller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tuktukteam.tuktuk.dao.ConducteurDAO;
import com.tuktukteam.tuktuk.model.Client;
import com.tuktukteam.tuktuk.model.Conducteur;
import com.tuktukteam.tuktuk.model.Course;

@RestController
@RequestMapping(value = "/conducteur")
public class ConducteurRestController {

	@Autowired
	private ConducteurDAO conducteurDAO;

	@RequestMapping(value = "/profil", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Conducteur> getProfile(HttpSession session) {

		Conducteur c = (Conducteur) session.getAttribute("conducteur");

		if (c != null) {
			int idC = c.getId();
			return new ResponseEntity<Conducteur>(this.conducteurDAO.find(idC), HttpStatus.OK);
		}
		return new ResponseEntity<Conducteur>(HttpStatus.FORBIDDEN);
	}

	@RequestMapping(value = "/{id}/courses", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<List<Course>> getCourses(@PathVariable int id, HttpSession session) {
		// TODO TOUT DOUX : si erreur, gérer list course null
		Conducteur conducteur = (Conducteur) session.getAttribute("conducteur");
		Client client = (Client) session.getAttribute("client");

		if (client != null || (conducteur != null && conducteur.getId() == id)) {
			return new ResponseEntity<List<Course>>(this.conducteurDAO.find(id).getCourses(), HttpStatus.OK);
		} else {
			return new ResponseEntity<List<Course>>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/{id}/infos", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Conducteur> getInfos(@PathVariable int id, HttpSession session) {

		Client client = (Client) session.getAttribute("client");

		if (client != null) {			
			Conducteur c = conducteurDAO.find(id);
			c.setBic(null);
			c.setIban(null);
			c.setMail(null);
			return new ResponseEntity<Conducteur>(c, HttpStatus.OK);
		}		
		return new ResponseEntity<Conducteur>(HttpStatus.FORBIDDEN);
	}

}
