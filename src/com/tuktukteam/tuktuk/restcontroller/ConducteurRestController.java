package com.tuktukteam.tuktuk.restcontroller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tuktukteam.autosecurity.AutoFilterForSpringControllers;
import com.tuktukteam.genericdao.DAOException;
import com.tuktukteam.tuktuk.dao.ConducteurDAO;
import com.tuktukteam.tuktuk.dao.CourseDAO;
import com.tuktukteam.tuktuk.dao.PersonneDAO;
import com.tuktukteam.tuktuk.model.Client;
import com.tuktukteam.tuktuk.model.Conducteur;
import com.tuktukteam.tuktuk.model.Course;
import com.tuktukteam.tuktuk.model.Personne;

@RestController
@RequestMapping(value = "/conducteur")
public class ConducteurRestController {

	@Autowired private ConducteurDAO conducteurDAO;
	@Autowired private CourseDAO courseDAO;
	@Autowired private PersonneDAO personneDAO;
	
	public ConducteurRestController() { AutoFilterForSpringControllers.addController(getClass(), "/api"); }
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Conducteur> login(@RequestParam String username, @RequestParam String password)
	{
		System.out.println("REST /conducteur/login : " + this);
		
		Conducteur conducteur = new Conducteur();

		conducteur.setUsername(username);
		conducteur.setPassword(password);
		
		try
		{
			conducteur = conducteurDAO.findByValues(conducteur);
		}
		catch (DAOException e)
		{
			conducteur = null;
		}
		
		if (conducteur == null)
			return new ResponseEntity<Conducteur>(HttpStatus.NOT_FOUND);

		return new ResponseEntity<Conducteur>(conducteur, HttpStatus.OK);
	}

	@RequestMapping(value = "", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Conducteur> getProfile(HttpSession session) {

		Conducteur c = (Conducteur) session.getAttribute("conducteur");

		if (c != null) {
			int idC = c.getId();
			return new ResponseEntity<Conducteur>(this.conducteurDAO.find(idC), HttpStatus.OK);
		}
		return new ResponseEntity<Conducteur>(HttpStatus.FORBIDDEN);
	}
	
	@RequestMapping(value="", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<Conducteur> updateProfile(HttpSession session, @RequestBody Personne p, BindingResult result) {
		Conducteur cond = (Conducteur) session.getAttribute("conducteur");
		
		if(cond!= null){
			if(!result.hasErrors()){
				p.setId(cond.getId());
				personneDAO.save(p);
				cond = conducteurDAO.find(cond.getId());
				return new ResponseEntity<Conducteur>(cond, HttpStatus.OK);
			}
			return new ResponseEntity<Conducteur>(HttpStatus.NOT_MODIFIED);
		}		
		return new ResponseEntity<Conducteur>(HttpStatus.FORBIDDEN);
	}
	
	@RequestMapping(value="/historique", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Conducteur> getRunsHistory(HttpSession session) {
		Conducteur cond = (Conducteur) session.getAttribute("conducteur");
		if (cond != null) {
			cond = conducteurDAO.find(cond.getId());
			return new ResponseEntity<Conducteur>(cond, HttpStatus.OK);
		}

		return new ResponseEntity<Conducteur>(HttpStatus.FORBIDDEN);
	}

	@RequestMapping(value = "/{id}/historique", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<List<Course>> getDriverHistory(@PathVariable int id, HttpSession session) {
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
			c.setPassword(null);
			return new ResponseEntity<Conducteur>(c, HttpStatus.OK);
		}		
		return new ResponseEntity<Conducteur>(HttpStatus.FORBIDDEN);
	}
	
	@RequestMapping(value="/pause", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<Boolean> isNotAvailable(HttpSession session){
		
		Conducteur cond = (Conducteur) session.getAttribute("conducteur");
		
		if(cond != null)
		{
			cond.setAvailable(false);
			cond = conducteurDAO.save(cond);
			return new ResponseEntity<Boolean>(true, HttpStatus.OK);
		}		
		return new ResponseEntity<Boolean>(HttpStatus.FORBIDDEN);
	}
	
	@RequestMapping(value="/disponible", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<Boolean> isAvailable(HttpSession session){
		
		Conducteur cond = (Conducteur) session.getAttribute("conducteur");
		
		if(cond != null)
		{
			cond.setAvailable(true);
			cond = conducteurDAO.save(cond);
			return new ResponseEntity<Boolean>(true, HttpStatus.OK);
		}		
		return new ResponseEntity<Boolean>(HttpStatus.FORBIDDEN);
	}
	
	@RequestMapping(value = "/maj", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<List<Course>> runsWithoutDriver(HttpSession session, @RequestParam double longitude, @RequestParam double latitude) {	
		Conducteur c = (Conducteur) session.getAttribute("conducteur");
		if (c != null) {
			c.setLongitude(longitude);
			c.setLatitude(latitude);
			c = conducteurDAO.save(c);
			List<Course> courses = courseDAO.getAll();
			for (Course course : courses) {
				if (course.getConducteur() != null) {
					courses.remove(course);
				}
			}
			return new ResponseEntity<List<Course>>(courses, HttpStatus.OK);
		}
		return new ResponseEntity<List<Course>>(HttpStatus.FORBIDDEN);
	}
	
	@RequestMapping(value="/{id}/note", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Float> calculAvgNote(@PathVariable int id, HttpSession session)
	{
		Conducteur cond2 = (Conducteur) session.getAttribute("conducteur");
		Conducteur cond1 = conducteurDAO.find(id);
		Client cli = (Client) session.getAttribute("client");
		
		if(cli!= null || (cond2 != null && cond2.getId()==cond1.getId())){
			float moy=0;
			float somme=0;
			int i=0;
			List<Course> courses = cond1.getCourses();
			for(Course course : courses)
			{
				if(course.getNoteConducteur()!=-1)
				{
					somme = somme + course.getNoteConducteur(); 
					i++;
				}				
			}
			moy = somme/i;
			return new ResponseEntity<Float>(moy, HttpStatus.OK);
		}		
		return new ResponseEntity<Float>(HttpStatus.FORBIDDEN);
	}
	
}
