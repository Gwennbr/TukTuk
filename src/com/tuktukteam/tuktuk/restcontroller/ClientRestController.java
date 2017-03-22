package com.tuktukteam.tuktuk.restcontroller;

import java.util.List;

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
import com.tuktukteam.tuktuk.model.Course;

@RestController
@RequestMapping("/client")
public class ClientRestController {
	
	@Autowired
	private ClientDAO clientDAO;
	
	@RequestMapping(value="/{id}/profil", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Client> getProfile(@PathVariable int id) {
		return new ResponseEntity<Client>(this.clientDAO.find(id), HttpStatus.OK);
	}
	
	@RequestMapping(value="/{id}/courses", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<List<Course>> getCourses(@PathVariable int id) {		
		//TODO TOUT DOUX : si erreur, g�rer list course null
		return new ResponseEntity<List<Course>>(this.clientDAO.find(id).getCourses(), HttpStatus.OK);
	}
	
//	@RequestMapping(value="/{id}/commentaire/{idCourse}", method = RequestMethod.PUT)
//	@ResponseBody
//	public ResponseEntity<Course> setCommAndNote(@PathVariable int id, @PathVariable int idCourse, @RequestParam String commentaire, @RequestParam float note ) {
//				
//		return null;
//	}
	
	@RequestMapping(value="/{id}/infos", method=RequestMethod.GET)
	public ResponseEntity<Client> getInfos(@PathVariable int id) {
		Client c = clientDAO.find(id);
		c.setDateValiditeCB(null);
		c.setNumeroCarteBancaire(null);
		c.setPictogramme(null);		
		c.setMail(null);
		return new ResponseEntity<Client>(c, HttpStatus.OK);
	}	
}
