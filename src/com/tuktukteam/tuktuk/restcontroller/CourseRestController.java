package com.tuktukteam.tuktuk.restcontroller;

import java.io.IOException;
import java.util.Date;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuktukteam.autosecurity.AccessTokenSecurity;
import com.tuktukteam.autosecurity.AutoFilterForSpringControllers;
import com.tuktukteam.autosecurity.RestrictedAccess;
import com.tuktukteam.autosecurity.RestrictedAccess.AccessType;
import com.tuktukteam.tuktuk.dao.ConducteurDAO;
import com.tuktukteam.tuktuk.dao.CourseDAO;
import com.tuktukteam.tuktuk.model.Client;
import com.tuktukteam.tuktuk.model.Conducteur;
import com.tuktukteam.tuktuk.model.Course;

@RestController
public class CourseRestController {


	@Autowired
	private CourseDAO courseDAO;

	@Autowired
	private ConducteurDAO conducteurDAO;

	public CourseRestController() { AutoFilterForSpringControllers.addController(getClass(), "/api"); }
	
	@RequestMapping(value = "/course/prise_en_charge", method = RequestMethod.POST)
	@ResponseBody
	@RestrictedAccess(value = AccessType.TOKEN, authorized = Client.class)
	public ResponseEntity<Course> createRun(@RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token, @RequestParam String adresseDepart) {

		Client c = AccessTokenSecurity.getUser(Client.class, token);
			Course course = new Course();
			course.setAdresseDepart(adresseDepart);
			course.setClient(c);
			course.setNoteClient(-1);
			course.setNoteConducteur(-1);
			course.setValide(false);
			return AccessTokenSecurity.buildResponse(courseDAO.save(course), token, HttpStatus.OK);
	}

	@RequestMapping(value = "/course/{idCourse}/accepter", method = RequestMethod.PUT)
	@ResponseBody
	@RestrictedAccess(value = AccessType.TOKEN, authorized = Conducteur.class)
	public ResponseEntity<Course> acceptRun(@RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token, @PathVariable int idCourse) {

		Course course = courseDAO.find(idCourse);
		Conducteur c = AccessTokenSecurity.getUser(Conducteur.class, token);

		int idConducteur = c.getId();

		if (course.getConducteur() != null) {
			return new ResponseEntity<Course>(HttpStatus.UNAUTHORIZED);
		}
		course.setConducteur(conducteurDAO.find(idConducteur));
		course = courseDAO.save(course);
		return AccessTokenSecurity.buildResponse(course, token, HttpStatus.OK);
	}

	@RequestMapping(value = "/course/{id}/valider", method = RequestMethod.PUT)
	@ResponseBody
	@RestrictedAccess(value=AccessType.TOKEN, authorized=Client.class)
	public ResponseEntity<Course> valideRun(@PathVariable int id, @RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token) {
		Course course = courseDAO.find(id);

		if (AccessTokenSecurity.getUser(Client.class, token).getId()==course.getClient().getId()) {
			course.setValide(true);
			course = courseDAO.save(course);
			return AccessTokenSecurity.buildResponse(course, token, HttpStatus.OK);
		}
		return AccessTokenSecurity.buildResponse(Course.class, token, HttpStatus.FORBIDDEN);
	}

	@RequestMapping(value = "/course/{id}/refuser", method = RequestMethod.PUT)
	@ResponseBody
	@RestrictedAccess(value=AccessType.TOKEN, authorized=Client.class)
	public ResponseEntity<Course> refuseRun(@PathVariable int id, @RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token) {
		Course course = courseDAO.find(id);

		if (AccessTokenSecurity.getUser(Client.class, token).getId()==course.getClient().getId()) {
			course.setConducteur(null);
			course = courseDAO.save(course);
			return AccessTokenSecurity.buildResponse(course, token, HttpStatus.OK);
		}
		return AccessTokenSecurity.buildResponse(Course.class, token, HttpStatus.FORBIDDEN);
	}

	@RequestMapping(value = "/course/{id}", method = RequestMethod.GET)
	@ResponseBody
	@RestrictedAccess(value=AccessType.TOKEN, authorized = {Client.class, Conducteur.class})
	public ResponseEntity<Course> refreshRun(@PathVariable int id, @RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token) {
		Course course = courseDAO.find(id);
		if (AccessTokenSecurity.typeOfUser(token)==Client.class && course.getClient().getId()==AccessTokenSecurity.getUser(Client.class, token).getId()) {
			return new ResponseEntity<Course>(course, HttpStatus.OK);
		}else if(AccessTokenSecurity.typeOfUser(token)==Conducteur.class && course.getConducteur().getId()==AccessTokenSecurity.getUser(Conducteur.class, token).getId())
		{
			return AccessTokenSecurity.buildResponse(course, token, HttpStatus.OK);
		}
		return AccessTokenSecurity.buildResponse(Course.class, token, HttpStatus.FORBIDDEN);
	}

	@RequestMapping(value = "/course/{id}/demarrer", method = RequestMethod.PUT)
	@ResponseBody
	@RestrictedAccess(value=AccessType.TOKEN, authorized = Conducteur.class)
	public ResponseEntity<Course> startRun(@PathVariable int id, @RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token) {
		Conducteur cond = AccessTokenSecurity.getUser(Conducteur.class, token);
		Course course = courseDAO.find(id);

		if (cond.getId() != course.getConducteur().getId() && course.isValide()) {
			course.setDateDebutCourse(new Date());
			course = courseDAO.save(course);
			return AccessTokenSecurity.buildResponse(course, token, HttpStatus.OK);
		}
		return AccessTokenSecurity.buildResponse(Course.class, token, HttpStatus.FORBIDDEN);
	}

	@RequestMapping(value = "/course/{id}/terminer", method = RequestMethod.PUT)
	@ResponseBody
	@RestrictedAccess(value = AccessType.TOKEN, authorized=Conducteur.class)
	public ResponseEntity<Course> endRun(@PathVariable int id, @RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token) {
		Conducteur cond = AccessTokenSecurity.getUser(Conducteur.class, token);
		Course course = courseDAO.find(id);
		if (cond.getId() == course.getConducteur().getId() && course.isValide() && course.getDateDebutCourse() != null) {
			course.setDateFinCourse(new Date());
			course = courseDAO.save(course);
			return AccessTokenSecurity.buildResponse(course, token, HttpStatus.OK);
		}
		return AccessTokenSecurity.buildResponse(Course.class, token, HttpStatus.FORBIDDEN);
	}
	
	@RequestMapping(value="/course/{id}/pause", method = RequestMethod.PUT)
	@ResponseBody
	@RestrictedAccess(value=AccessType.TOKEN, authorized=Conducteur.class)
	public ResponseEntity<Boolean> startPause(@PathVariable int id, @RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token) {
		Conducteur cond = AccessTokenSecurity.getUser(Conducteur.class, token);
		Course course = courseDAO.find(id);
		if(cond.getDateDebutPause()==0 && cond.getId() == course.getConducteur().getId() && course.isValide())
		{
			cond.setDateDebutPause(System.currentTimeMillis());
			return AccessTokenSecurity.buildResponse(true, token, HttpStatus.OK);
		}
		
		return AccessTokenSecurity.buildResponse(Boolean.class, token, HttpStatus.FORBIDDEN);
	}
	
	@RequestMapping(value="/course/{id}/reprise", method = RequestMethod.PUT)
	@ResponseBody
	@RestrictedAccess(value=AccessType.TOKEN, authorized = Conducteur.class)
	private ResponseEntity<Boolean> unPause(@PathVariable int id, @RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token) {
		
		Conducteur cond = AccessTokenSecurity.getUser(Conducteur.class, token);
		Course course = courseDAO.find(id);
		
		if(cond.getDateDebutPause()> 0 && cond.getId() == course.getConducteur().getId() && course.isValide()) {
			
			long tempsP = Math.round(((double)(System.currentTimeMillis() - cond.getDateDebutPause()) / 60));
			course.setTempsPause(course.getTempsPause()+tempsP);	
			return AccessTokenSecurity.buildResponse(true, token, HttpStatus.OK);
		}
		
		return AccessTokenSecurity.buildResponse(Boolean.class, token, HttpStatus.FORBIDDEN);
	}
	
	

	@RequestMapping(value = "/course/{id}/commenter", method = RequestMethod.PUT)
	@ResponseBody
	@RestrictedAccess(value = AccessType.TOKEN, authorized ={Conducteur.class, Client.class})
	public ResponseEntity<Course> comment(@PathVariable int id, @RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token, @RequestParam float note, @RequestParam String commentaire) {
		Course course = courseDAO.find(id);

		if (course.getDateFinCourse() != null) {
			if (AccessTokenSecurity.typeOfUser(token)==Conducteur.class) {
				course.setNoteConducteur(note);
				course.setComConducteur(commentaire);
				course = courseDAO.save(course);
				return AccessTokenSecurity.buildResponse(course, token, HttpStatus.OK);
			}else if(AccessTokenSecurity.typeOfUser(token)==Client.class){
				course.setNoteClient(note);
				course.setComClient(commentaire);
				course = courseDAO.save(course);
				return AccessTokenSecurity.buildResponse(course, token, HttpStatus.OK);
			}
		}		
		return AccessTokenSecurity.buildResponse(Course.class, token, HttpStatus.FORBIDDEN);
	}
	
	@RequestMapping("/toto")
	@ResponseBody
	@RestrictedAccess(value = AccessType.TOKEN, authorized=Conducteur.class)
	public ResponseEntity<Integer> toto(@RequestParam String adr, @RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token)
	{
		Conducteur c = AccessTokenSecurity.getUser(Conducteur.class, token);
		
		return AccessTokenSecurity.buildResponse(calculDistance(c, adr), token, HttpStatus.OK);
	}
	
	
	
	public static int calculDistance(Conducteur cond, String adresse) {
		
		RestTemplate template = new RestTemplate();
		ResponseEntity<String> jsonEntity = null;
		try{
			jsonEntity = template.getForEntity("https://maps.googleapis.com/maps/api/distancematrix/json?origins={lng},{lat}&destinations={adresse}&mode=bicycling&units=metric", String.class,cond.getLongitude(), cond.getLatitude(), adresse.replaceAll(" ", "+"));
		} catch (RestClientException e){
			e.printStackTrace();
		}
		
		if(jsonEntity.getStatusCode()==HttpStatus.OK){
			ObjectMapper objmp = new ObjectMapper();
			try {
				JsonNode rootNode = objmp.readValue(jsonEntity.getBody(), JsonNode.class);
				//return rootNode.get("routes").get(0).get("legs").get(0).get("distance").get("value").asInt();
				return rootNode.get("rows").get(0).get("elements").get(0).get("distance").get("value").asInt();
			} catch (IOException e) 
			{
			}
		}		
		return 0;
	}

}
