package com.tuktukteam.tuktuk.restcontroller;

import java.io.IOException;
import java.util.Date;
import java.util.List;

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
import com.tuktukteam.tuktuk.dao.ClientDAO;
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
	ClientDAO clientDAO;

	@Autowired
	private ConducteurDAO conducteurDAO;

	public CourseRestController() {
		AutoFilterForSpringControllers.addController(getClass(), "/api");
	}

	@RequestMapping(value = "/ride/request", method = RequestMethod.POST)
	@ResponseBody
	@RestrictedAccess(value = AccessType.TOKEN, authorized = Client.class)
	public ResponseEntity<Course> createRun(@RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token,
			@RequestParam String adresseDepart) {

		Client c = AccessTokenSecurity.getUser(Client.class, token);
		if (courseDAO.getActualCustomerRide(c.getId()) != null) {
			Course course = new Course();
			course.setAdresseDepart(adresseDepart);
			course.setClient(c);
			course.setNoteClient(-1);
			course.setNoteConducteur(-1);
			course.setValide(false);
			return AccessTokenSecurity.buildResponse(courseDAO.save(course), token, HttpStatus.OK);
		}
		
		return AccessTokenSecurity.buildResponse(Course.class, token, HttpStatus.UNAUTHORIZED); 
	}

	@RequestMapping(value = "/ride/{idCourse}/accept", method = RequestMethod.PUT)
	@ResponseBody
	@RestrictedAccess(value = AccessType.TOKEN, authorized = Conducteur.class)
	public ResponseEntity<Course> acceptRun(@RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token,
			@PathVariable int idCourse) {

		Course course = courseDAO.find(idCourse);
		Conducteur c = AccessTokenSecurity.getUser(Conducteur.class, token);

		int idConducteur = c.getId();

		if (course.getConducteur() != null) {
			return AccessTokenSecurity.buildResponse(Course.class, token, HttpStatus.UNAUTHORIZED);
		}
		c = conducteurDAO.find(c.getId());
		c.setAvailable(false);
		conducteurDAO.save(c);
		course.setConducteur(conducteurDAO.find(idConducteur));
		course = courseDAO.save(course);
		return AccessTokenSecurity.buildResponse(course, token, HttpStatus.OK);
	}

	@RequestMapping(value = "/ride/validate", method = RequestMethod.PUT)
	@ResponseBody
	@RestrictedAccess(value = AccessType.TOKEN, authorized = Client.class)
	public ResponseEntity<Course> valideRun(@RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token) {
		Client client = AccessTokenSecurity.getUser(Client.class, token);
		Course course = courseDAO.getActualCustomerRide(client.getId());

		if (client.getId() == course.getClient().getId()) {
			course.setValide(true);
			course = courseDAO.save(course);
			return AccessTokenSecurity.buildResponse(course, token, HttpStatus.OK);
		}
		return AccessTokenSecurity.buildResponse(Course.class, token, HttpStatus.FORBIDDEN);
	}

	@RequestMapping(value = "/ride/decline", method = RequestMethod.PUT)
	@ResponseBody
	@RestrictedAccess(value = AccessType.TOKEN, authorized = Client.class)
	public ResponseEntity<Course> refuseRun(@RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token) {
		Client client = AccessTokenSecurity.getUser(Client.class, token);
		Course course = courseDAO.getActualCustomerRide(client.getId());
		if (client.getId() == course.getClient().getId()) {
			Conducteur conducteur = course.getConducteur();
			conducteur.setAvailable(true);
			conducteurDAO.save(conducteur);
			course.setConducteur(null);
			course = courseDAO.save(course);
			return AccessTokenSecurity.buildResponse(course, token, HttpStatus.OK);
		}
		return AccessTokenSecurity.buildResponse(Course.class, token, HttpStatus.FORBIDDEN);
	}

	@RequestMapping(value = "/ride", method = RequestMethod.GET)
	@ResponseBody
	@RestrictedAccess(value = AccessType.TOKEN, authorized = { Client.class, Conducteur.class })
	public ResponseEntity<Course> refreshRun(@RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token) {
		if (AccessTokenSecurity.typeOfUser(token) == Client.class) {
			Client client = AccessTokenSecurity.getUser(Client.class, token);
			Course course = courseDAO.getActualCustomerRide(client.getId());
			if (course.getClient().getId() == client.getId())
				return new ResponseEntity<Course>(course, HttpStatus.OK);

		} else if (AccessTokenSecurity.typeOfUser(token) == Conducteur.class) {
			Conducteur conducteur = AccessTokenSecurity.getUser(Conducteur.class, token);
			Course course = courseDAO.getActualCustomerRide(conducteur.getId());
			if (course != null && course.getConducteur() != null && course.getConducteur().getId() == conducteur.getId())
				return new ResponseEntity<Course>(course, HttpStatus.OK);
			
			return AccessTokenSecurity.buildResponse(Course.class, token, HttpStatus.I_AM_A_TEAPOT);
		}
		return AccessTokenSecurity.buildResponse(Course.class, token, HttpStatus.FORBIDDEN);
	}

	@RequestMapping(value = "/ride/start", method = RequestMethod.PUT)
	@ResponseBody
	@RestrictedAccess(value = AccessType.TOKEN, authorized = Conducteur.class)
	public ResponseEntity<Course> startRun(@RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token) {
		Conducteur cond = AccessTokenSecurity.getUser(Conducteur.class, token);
		Course course = courseDAO.getActualDriverRide(cond.getId());

		if (cond.getId() == course.getConducteur().getId() && course.isValide()) {
			course.setDateDebutCourse(new Date());
			course = courseDAO.save(course);
			return AccessTokenSecurity.buildResponse(course, token, HttpStatus.OK);
		}
		return AccessTokenSecurity.buildResponse(Course.class, token, HttpStatus.FORBIDDEN);
	}

	@RequestMapping(value = "/ride/complete", method = RequestMethod.PUT)
	@ResponseBody
	@RestrictedAccess(value = AccessType.TOKEN, authorized = Conducteur.class)
	public ResponseEntity<Course> endRun(@RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token) {
		Conducteur cond = AccessTokenSecurity.getUser(Conducteur.class, token);
		Course course = courseDAO.getActualDriverRide(cond.getId());

		if (cond.getId() == course.getConducteur().getId() && course.isValide()
				&& course.getDateDebutCourse() != null) {
			course.setDateFinCourse(new Date());
			course.setDistance(calculDistance(cond, course.getAdresseDepart()));
			course = courseDAO.save(course);
			return AccessTokenSecurity.buildResponse(course, token, HttpStatus.OK);
		}
		return AccessTokenSecurity.buildResponse(Course.class, token, HttpStatus.FORBIDDEN);
	}

	@RequestMapping(value = "/ride/pause", method = RequestMethod.PUT)
	@ResponseBody
	@RestrictedAccess(value = AccessType.TOKEN, authorized = Conducteur.class)
	public ResponseEntity<Boolean> startPause(@RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token) {
		Conducteur cond = AccessTokenSecurity.getUser(Conducteur.class, token);
		Course course = courseDAO.getActualDriverRide(cond.getId());

		if (cond.getDateDebutPause() == 0 && cond.getId() == course.getConducteur().getId() && course.isValide()) {
			cond.setDateDebutPause(System.currentTimeMillis());
			return AccessTokenSecurity.buildResponse(true, token, HttpStatus.OK);
		}

		return AccessTokenSecurity.buildResponse(Boolean.class, token, HttpStatus.FORBIDDEN);
	}

	@RequestMapping(value = "/ride/resume", method = RequestMethod.PUT)
	@ResponseBody
	@RestrictedAccess(value = AccessType.TOKEN, authorized = Conducteur.class)
	private ResponseEntity<Boolean> unPause(@RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token) {

		Conducteur cond = AccessTokenSecurity.getUser(Conducteur.class, token);
		Course course = courseDAO.getActualDriverRide(cond.getId());

		if (cond.getDateDebutPause() > 0 && cond.getId() == course.getConducteur().getId() && course.isValide()) {

			long tempsP = Math.round(((double) (System.currentTimeMillis() - cond.getDateDebutPause()) / 60));
			course.setTempsPause(course.getTempsPause() + tempsP);
			
			return AccessTokenSecurity.buildResponse(true, token, HttpStatus.OK);
		}

		return AccessTokenSecurity.buildResponse(Boolean.class, token, HttpStatus.FORBIDDEN);
	}

	@RequestMapping(value = "/ride/{id}/comment", method = RequestMethod.PUT)
	@ResponseBody
	@RestrictedAccess(value = AccessType.TOKEN, authorized = { Conducteur.class, Client.class })
	public ResponseEntity<Course> comment(@PathVariable int id, @RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token,
			@RequestParam float note, @RequestParam String commentaire) {
		Course course = courseDAO.find(id);
		if (AccessTokenSecurity.typeOfUser(token) == Conducteur.class) {
			if (course.getDateFinCourse() != null && course.getConducteur().getId() == AccessTokenSecurity.getUser(Conducteur.class, token).getId()) {
				course.setNoteConducteur(note);
				course.setComConducteur(commentaire);
				course = courseDAO.save(course);
				return AccessTokenSecurity.buildResponse(course, token, HttpStatus.OK);
			}
			return AccessTokenSecurity.buildResponse(Course.class, token, HttpStatus.FORBIDDEN);
		} else if (AccessTokenSecurity.typeOfUser(token) == Client.class) {
			if (course.getDateFinCourse() != null && course.getClient().getId() == AccessTokenSecurity.getUser(Client.class, token).getId()) {
				course.setNoteConducteur(note);
				course.setComConducteur(commentaire);
				course = courseDAO.save(course);
				return AccessTokenSecurity.buildResponse(course, token, HttpStatus.OK);
			}
			return AccessTokenSecurity.buildResponse(Course.class, token, HttpStatus.FORBIDDEN);
		}
		return AccessTokenSecurity.buildResponse(Course.class, token, HttpStatus.FORBIDDEN);
	}

	@ResponseBody
	@RequestMapping(value = "/ride/customerHistory", method = RequestMethod.GET)
	@RestrictedAccess(value = AccessType.TOKEN, authorized = Conducteur.class)
	public ResponseEntity<List<Course>> getCustomerHistory(
			@RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token) {
		Conducteur cond = AccessTokenSecurity.getUser(Conducteur.class, token);
		Course course = courseDAO.getActualDriverRide(cond.getId());
		Client client = course.getClient();
		client = clientDAO.find(client.getId());
		return AccessTokenSecurity.buildResponse(courseDAO.getCustomerCompletedRides(client.getId()), token,
				HttpStatus.OK);
	}

	// récupère et renvoie l'historique des courses du conducteur demandé au
	// client.
	@RequestMapping(value = "/ride/driverHistory", method = RequestMethod.GET)
	@ResponseBody
	@RestrictedAccess(value = AccessType.TOKEN, authorized = Client.class)
	public ResponseEntity<List<Course>> getDriverHistory(
			@RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token) {
		Course course = courseDAO.getActualCustomerRide(AccessTokenSecurity.getUser(Client.class, token).getId());
		Conducteur conducteur = conducteurDAO.find(course.getConducteur().getId());
		return AccessTokenSecurity.buildResponse(courseDAO.getDriverCompletedRides(conducteur.getId()), token,
				HttpStatus.OK);
	}

	public static int calculDistance(Conducteur cond, String adresse) {

		RestTemplate template = new RestTemplate();
		ResponseEntity<String> jsonEntity = null;
		try {
			jsonEntity = template.getForEntity(
					"https://maps.googleapis.com/maps/api/distancematrix/json?origins={lng},{lat}&destinations={adresse}&mode=bicycling&units=metric",
					String.class, cond.getLongitude(), cond.getLatitude(), adresse.replaceAll(" ", "+"));
		} catch (RestClientException e) {
			e.printStackTrace();
		}

		if (jsonEntity.getStatusCode() == HttpStatus.OK) {
			ObjectMapper objmp = new ObjectMapper();
			try {
				JsonNode rootNode = objmp.readValue(jsonEntity.getBody(), JsonNode.class);
				// return
				// rootNode.get("routes").get(0).get("legs").get(0).get("distance").get("value").asInt();
				try{
					return rootNode.get("rows").get(0).get("elements").get(0).get("distance").get("value").asInt();
				}catch(NullPointerException e){
					return 1000000;  
				}
			} catch (IOException e) {
			}
		}
		return 0;
	}

}
