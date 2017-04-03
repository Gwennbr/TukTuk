package com.tuktukteam.tuktuk.restcontroller;

import java.util.ArrayList;
import java.util.Date;
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
import com.tuktukteam.comparator.ComparatorCourse;
import com.tuktukteam.genericdao.DAOException;
import com.tuktukteam.genericdao.annotations.ColumnTag;
import com.tuktukteam.tuktuk.dao.ConducteurDAO;
import com.tuktukteam.tuktuk.dao.CourseDAO;
import com.tuktukteam.tuktuk.model.Client;
import com.tuktukteam.tuktuk.model.Conducteur;
import com.tuktukteam.tuktuk.model.Course;

@RestController
@RequestMapping(value = "/driver")
public class ConducteurRestController {

	@Autowired
	private ConducteurDAO conducteurDAO;
	@Autowired
	private CourseDAO courseDAO;

	public ConducteurRestController() {
		AutoFilterForSpringControllers.addController(getClass(), "/api");
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	@ResponseBody
	@RestrictedAccess(value = AccessType.PUBLIC)
	public ResponseEntity<Conducteur> login(@RequestParam String username, @RequestParam String password) {
		Conducteur conducteur = new Conducteur();

		conducteur.setUsername(username);
		conducteur.setPassword(password);

		try {
			conducteur = conducteurDAO.findByValues(conducteur);
		} catch (DAOException e) {
			System.out.println("excep : " + e);
			conducteur = null;
		}

		if (conducteur == null)
			return new ResponseEntity<Conducteur>(HttpStatus.NOT_ACCEPTABLE);

		return AccessTokenSecurity.buildResponseAndCreateAccess(conducteur);
	}

	// récupère et renvoie toutes les données du conducteur actuellement
	// connecté.
	@RequestMapping(value = "", method = RequestMethod.GET)
	@ResponseBody
	@RestrictedAccess(value = AccessType.TOKEN, authorized = Conducteur.class)
	public ResponseEntity<Conducteur> getProfile(@RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token) {
		return AccessTokenSecurity.buildResponse(AccessTokenSecurity.getUser(Conducteur.class, token), token,
				HttpStatus.OK);
	}

	// création d'un nouveau conducteur (register)
	@RequestMapping(value = "", method = RequestMethod.POST)
	@ResponseBody
	@RestrictedAccess(value = AccessType.PUBLIC)
	public ResponseEntity<Conducteur> register(@RequestBody Conducteur conducteur, BindingResult result) 
	{
		if (!result.hasErrors())
		{
			conducteur.setDate_inscription(new Date());
			conducteurDAO.hashFieldsAndSave(conducteur);
			return new ResponseEntity<Conducteur>(conducteur, HttpStatus.OK);
		}
		return new ResponseEntity<Conducteur>(HttpStatus.BAD_REQUEST);
	}

	// met à jour les informations du conducteur
	@RequestMapping(value = "", method = RequestMethod.PUT)
	@ResponseBody
	@RestrictedAccess(value = AccessType.TOKEN, authorized = Conducteur.class)
	public ResponseEntity<Conducteur> updateProfile(@RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token,
			@RequestBody Conducteur conducteur, BindingResult result) {
		Conducteur cond = AccessTokenSecurity.getUser(Conducteur.class, token);
		if (!result.hasErrors()) {
			conducteur.setId(cond.getId());
			conducteurDAO.save(conducteur);
			cond = conducteurDAO.find(cond.getId());
			return AccessTokenSecurity.buildResponse(cond, token, HttpStatus.OK);
		}
		return AccessTokenSecurity.buildResponse(Conducteur.class, token, HttpStatus.NOT_MODIFIED);
	}

	// récupère et renvoie l'historique des courses du conducteur actuemllement
	// connecté
	@RequestMapping(value = "/history", method = RequestMethod.GET)
	@ResponseBody
	@RestrictedAccess(value = AccessType.TOKEN, authorized = Conducteur.class)
	public ResponseEntity<List<Course>> getRunsHistory(
			@RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token) {
		Conducteur conducteur = AccessTokenSecurity.getUser(Conducteur.class, token);
		conducteur = conducteurDAO.find(conducteur.getId());
		return AccessTokenSecurity.buildResponse(conducteur.getCourses(), token, HttpStatus.OK);
	}

	// récupère et renvoie les informations utiles du conducteur demandé au
	// client
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	@RestrictedAccess(value = AccessType.TOKEN, authorized = Client.class)
	public ResponseEntity<Conducteur> getInfos(@PathVariable int id,
			@RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token) {
		return AccessTokenSecurity.buildResponse(
				conducteurDAO.getAndFillOnlyFieldsNotTaggedBy(id, ColumnTag.FRONT_RESTRICTED), token, HttpStatus.OK);
	}

	// rend le conducteur actuellement connecté non disponible.
	@RequestMapping(value = "/unavailable", method = RequestMethod.PUT)
	@ResponseBody
	@RestrictedAccess(value = AccessType.TOKEN, authorized = Conducteur.class)
	public ResponseEntity<Boolean> isNotAvailable(@RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token) {

		Conducteur cond = AccessTokenSecurity.getUser(Conducteur.class, token);
		cond.setAvailable(false);
		cond = conducteurDAO.save(cond);
		return AccessTokenSecurity.buildResponse(true, token, HttpStatus.OK);

	}

	// rend le conducteur actuellement connecté disponible.
	@RequestMapping(value = "/available", method = RequestMethod.PUT)
	@ResponseBody
	@RestrictedAccess(value = AccessType.TOKEN, authorized = Conducteur.class)
	public ResponseEntity<Boolean> isAvailable(@RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token) {

		Conducteur cond = AccessTokenSecurity.getUser(Conducteur.class, token);
		cond.setAvailable(true);
		cond = conducteurDAO.save(cond);
		return AccessTokenSecurity.buildResponse(true, token, HttpStatus.OK);
	}

	// met à jour la position du conducteur dans la base de donnée et récupère
	// en même temps toutes les courses sans conducteur.
	@RequestMapping(value = "/refreshPos", method = RequestMethod.PUT)
	@ResponseBody
	@RestrictedAccess(value = AccessType.TOKEN, authorized = Conducteur.class)
	public ResponseEntity<List<Course>> runsWithoutDriver(
			@RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token, @RequestParam double longitude,
			@RequestParam double latitude) {
		Conducteur c = AccessTokenSecurity.getUser(Conducteur.class, token);
		c.setLongitude(longitude);
		c.setLatitude(latitude);
		c = conducteurDAO.save(c);
		

		if (courseDAO.getActualDriverRide(c.getId()) == null) {
			List<Course> courses = courseDAO.getRidesWithoutDriver();
			List<Course> cou2 = new ArrayList<>();
			
			for (Course course : courses) {
				if (CourseRestController.calculDistance(c, course.getAdresseDepart()) <= 10000) {
					cou2.add(course);
				}
			}

			ComparatorCourse comp = new ComparatorCourse(c);
			cou2.sort(comp);

			return AccessTokenSecurity.buildResponse(cou2, token, HttpStatus.OK);
		}
		//TODO vérif retour
		return AccessTokenSecurity.buildResponse(null, token, HttpStatus.OK);
	}

	// calcul la moyenne des notes du conducteur actuellement connecté et lui
	// renvoie
	@RequestMapping(value = "/note", method = RequestMethod.GET)
	@ResponseBody
	@RestrictedAccess(value = AccessType.TOKEN, authorized = {Conducteur.class, Client.class})
	public ResponseEntity<Integer> calculAvgNote(@RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token) {		
		float moy = 0;
		float somme = 0;
		int i = 0;

		if(AccessTokenSecurity.typeOfUser(token)==Conducteur.class){
			Conducteur cond = AccessTokenSecurity.getUser(Conducteur.class, token);
			List<Course> courses = conducteurDAO.find(cond.getId()).getCourses();
			for (Course course : courses) {
				if (course.getNoteClient() != -1) {
					somme = somme + course.getNoteClient();
					i++;
				}
			}
			moy = somme / i;
			return AccessTokenSecurity.buildResponse(Math.round(moy), token, HttpStatus.OK);
		} else {
			Client client = AccessTokenSecurity.getUser(Client.class, token);
			Course course = courseDAO.getActualCustomerRide(client.getId());
			Conducteur cond = conducteurDAO.find(course.getConducteur().getId());
			List<Course> courses = cond.getCourses();
			for (Course cou : courses) {
				if (cou.getNoteClient() != -1) {
					somme = somme + cou.getNoteClient();
					i++;
				}
			}
			moy = somme / i;
			return AccessTokenSecurity.buildResponse(Math.round(moy), token, HttpStatus.OK);
		}
	}

	// calcul la moyenne des notes du conducteur demandé et la renvoie au client
	@RequestMapping(value = "/{id}/note", method = RequestMethod.GET)
	@ResponseBody
	@RestrictedAccess(value = AccessType.TOKEN, authorized = Client.class)
	public ResponseEntity<Integer> calculAvgNoteDriver(@PathVariable int id, @RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token) {
		Conducteur cond = conducteurDAO.find(id);
		float moy = 0;
		float somme = 0;
		int i = 0;
		List<Course> courses = cond.getCourses();
		for (Course course : courses) {
			if (course.getNoteConducteur() != -1) {
				somme = somme + course.getNoteConducteur();
				i++;
			}
		}
		moy = somme / i;
		return AccessTokenSecurity.buildResponse(Math.round(moy), token, HttpStatus.OK);
	}

	// récupère tout les conducteurs à +/- 5 km et les renvoie au client
	@RequestMapping(value = "s", method = RequestMethod.GET)
	@ResponseBody
	@RestrictedAccess()
	public ResponseEntity<List<Conducteur>> getAllNearDrivers(
			@RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token, @RequestParam double latitude,
			@RequestParam double longitude) {
		//List<Coordonnee> coordonnees = new ArrayList<>();
		List<Conducteur> returnList = new ArrayList<>();
		List<Conducteur> conducteurs = conducteurDAO.getAllAndFillOnlyFieldsTaggedBy("coordonnees");
		for (Conducteur cond : conducteurs) {
			if (cond.getLatitude() <= latitude + 0.07 && cond.getLatitude() >= latitude - 0.07
					&& cond.getLongitude() <= longitude + 0.07 && cond.getLongitude() <= longitude + 0.07) {
				returnList.add(cond);
				//coordonnees.add(new Coordonnee(cond.getLatitude(), cond.getLongitude()));
			}
		}

		return AccessTokenSecurity.buildResponse(returnList, token, HttpStatus.OK);
	}

}
