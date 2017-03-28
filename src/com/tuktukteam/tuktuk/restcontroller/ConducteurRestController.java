package com.tuktukteam.tuktuk.restcontroller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tuktukteam.autosecurity.AccessTokenSecurity;
import com.tuktukteam.autosecurity.AutoFilterForSpringControllers;
import com.tuktukteam.autosecurity.RestrictedAccess;
import com.tuktukteam.autosecurity.RestrictedAccess.AccessType;
import com.tuktukteam.genericdao.DAOException;
import com.tuktukteam.genericdao.annotations.ColumnTag;
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
	@RestrictedAccess(value = AccessType.PUBLIC)
	public ResponseEntity<Conducteur> login(@RequestParam String username, @RequestParam String password)
	{
		Conducteur conducteur = new Conducteur();

		conducteur.setUsername(username);
		conducteur.setPassword(password);
		
		try
		{
			conducteur = conducteurDAO.findByValues(conducteur);
		}
		catch (DAOException e)
		{
			System.out.println("excep : " + e);
			conducteur = null;
		}
		
		if (conducteur == null)
			return new ResponseEntity<Conducteur>(HttpStatus.NOT_ACCEPTABLE);

		return AccessTokenSecurity.buildResponseAndCreateAccess(conducteur);
	}

	@RequestMapping(value = "", method = RequestMethod.GET)
	@ResponseBody
	@RestrictedAccess(value = AccessType.TOKEN, authorized = Conducteur.class)
	public ResponseEntity<Conducteur> getProfile(@RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token) {		
		return AccessTokenSecurity.buildResponse(AccessTokenSecurity.getUser(Conducteur.class, token), token, HttpStatus.OK);
	}
	
	@RequestMapping(value="", method = RequestMethod.PUT)
	@ResponseBody
	@RestrictedAccess(value = AccessType.TOKEN, authorized = Conducteur.class)
	public ResponseEntity<Conducteur> updateProfile(@RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token, @RequestBody Personne p, BindingResult result) {
		Conducteur cond = AccessTokenSecurity.getUser(Conducteur.class, token) ;		
		if(!result.hasErrors()){
			p.setId(cond.getId());
			personneDAO.save(p);
			cond = conducteurDAO.find(cond.getId());
			return AccessTokenSecurity.buildResponse(cond, token, HttpStatus.OK);
		}
		return AccessTokenSecurity.buildResponse(Conducteur.class, token, HttpStatus.NOT_MODIFIED);
	}
	
	@RequestMapping(value="/historique", method = RequestMethod.GET)
	@ResponseBody
	@RestrictedAccess(value = AccessType.TOKEN, authorized = Conducteur.class)
	public ResponseEntity<List<Course>> getRunsHistory(@RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token) {
		return AccessTokenSecurity.buildResponse(AccessTokenSecurity.getUser(Conducteur.class, token).getCourses(), token, HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}/historique", method = RequestMethod.GET)
	@ResponseBody
	@RestrictedAccess(value = AccessType.TOKEN, authorized = Client.class)
	public ResponseEntity<List<Course>> getDriverHistory(@PathVariable int id, @RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token) {
		return AccessTokenSecurity.buildResponse(this.conducteurDAO.find(id).getCourses(), token, HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}/infos", method = RequestMethod.GET)
	@ResponseBody
	@RestrictedAccess(value=AccessType.TOKEN, authorized = Client.class)
	public ResponseEntity<Conducteur> getInfos(@PathVariable int id, @RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token) {	
		return AccessTokenSecurity.buildResponse(conducteurDAO.getAndFillOnlyFieldsNotTaggedBy(id, ColumnTag.FRONT_RESTRICTED), token, HttpStatus.OK);
	}
	
	@RequestMapping(value="/pause", method = RequestMethod.PUT)
	@ResponseBody
	@RestrictedAccess(value=AccessType.TOKEN, authorized = Conducteur.class)
	public ResponseEntity<Boolean> isNotAvailable(@RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token){
		
		Conducteur cond = AccessTokenSecurity.getUser(Conducteur.class, token);
		cond.setAvailable(false);
		cond = conducteurDAO.save(cond);
		return AccessTokenSecurity.buildResponse(true, token, HttpStatus.OK);

	}
	
	@RequestMapping(value="/disponible", method = RequestMethod.PUT)
	@ResponseBody
	@RestrictedAccess(value=AccessType.TOKEN, authorized = Conducteur.class)
	public ResponseEntity<Boolean> isAvailable(@RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token){
		
		Conducteur cond = AccessTokenSecurity.getUser(Conducteur.class, token);
		cond.setAvailable(true);
		cond = conducteurDAO.save(cond);
		return AccessTokenSecurity.buildResponse(true, token, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/maj", method = RequestMethod.PUT)
	@ResponseBody
	@RestrictedAccess(value=AccessType.TOKEN, authorized = Conducteur.class)
	public ResponseEntity<List<Course>> runsWithoutDriver(@RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token, @RequestParam double longitude, @RequestParam double latitude) {	
		Conducteur c = AccessTokenSecurity.getUser(Conducteur.class, token);
		c.setLongitude(longitude);
		c.setLatitude(latitude);
		c = conducteurDAO.save(c);
		List<Course> courses = courseDAO.getAll();
		//TODO enlever commentaire
		
		for (Course course : courses) {
			if (course.getConducteur() != null) {
				courses.remove(course);
			}
		}
		
		return AccessTokenSecurity.buildResponse(courses, token, HttpStatus.OK);
	}
	
	@RequestMapping(value="/note", method = RequestMethod.GET)
	@ResponseBody
	@RestrictedAccess(value=AccessType.TOKEN, authorized = Conducteur.class)
	public ResponseEntity<Float> calculAvgNote(@RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token)
	{
		Conducteur cond = AccessTokenSecurity.getUser(Conducteur.class, token);
		float moy=0;
		float somme=0;
		int i=0;
		List<Course> courses = cond.getCourses();
		for(Course course : courses)
		{
			if(course.getNoteConducteur()!=-1)
			{
				somme = somme + course.getNoteConducteur(); 
				i++;
			}				
		}
		moy = somme/i;
		return AccessTokenSecurity.buildResponse(moy, token, HttpStatus.OK);
	}
	
	@RequestMapping(value="/{id}/note", method = RequestMethod.GET)
	@ResponseBody
	@RestrictedAccess(value=AccessType.TOKEN, authorized = Client.class)
	public ResponseEntity<Float> calculAvgNoteDriver(@PathVariable int id, @RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token)
	{
		Conducteur cond = conducteurDAO.find(id);
		float moy=0;
		float somme=0;
		int i=0;
		List<Course> courses = cond.getCourses();
		for(Course course : courses)
		{
			if(course.getNoteConducteur()!=-1)
			{
				somme = somme + course.getNoteConducteur(); 
				i++;
			}				
		}
		moy = somme/i;
		return AccessTokenSecurity.buildResponse(moy, token, HttpStatus.OK);
	}
	
}
