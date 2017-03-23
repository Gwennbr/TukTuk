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

import com.tuktukteam.tuktuk.dao.ClientDAO;
import com.tuktukteam.tuktuk.model.Client;
import com.tuktukteam.tuktuk.model.Conducteur;
import com.tuktukteam.tuktuk.model.Course;

@RestController
@RequestMapping("/client")
public class ClientRestController {

	@Autowired
	private ClientDAO clientDAO;

	@RequestMapping(value = "/profil", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Client> getProfile(HttpSession session) {

		Client c = (Client) session.getAttribute("client");

		if (c != null) {
			int idC = c.getId();
			return new ResponseEntity<Client>(this.clientDAO.find(idC),	HttpStatus.OK);
		}
		
		return new ResponseEntity<Client>(HttpStatus.FORBIDDEN);
	}

	@RequestMapping(value = "/{id}/courses", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<List<Course>> getCourses(@PathVariable int id, HttpSession session) {
		// TODO TOUT DOUX : si erreur, gérer list course null
		Client client = (Client) session.getAttribute("client");
		Conducteur conducteur = (Conducteur) session.getAttribute("conducteur");

		if (conducteur != null || (client != null && client.getId() == id)) {
			return new ResponseEntity<List<Course>>(this.clientDAO.find(id).getCourses(), HttpStatus.OK);
		} else {
			return new ResponseEntity<List<Course>>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/{id}/infos", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Client> getInfos(@PathVariable int id, HttpSession session) {

		Client client = (Client) session.getAttribute("client");
		Conducteur conducteur = (Conducteur) session.getAttribute("conducteur");

		if (conducteur != null) {
			Client c = clientDAO.find(id);
			c.setDateValiditeCB(null);
			c.setNumeroCarteBancaire(null);
			c.setPictogramme(null);
			c.setMail(null);
			return new ResponseEntity<Client>(c, HttpStatus.OK);
		} else {
			return new ResponseEntity<Client>(HttpStatus.FORBIDDEN);
		}
	}
}
