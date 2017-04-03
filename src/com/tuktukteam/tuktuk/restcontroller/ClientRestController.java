package com.tuktukteam.tuktuk.restcontroller;

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
import com.tuktukteam.genericdao.DAOException;
import com.tuktukteam.genericdao.annotations.ColumnTag;
import com.tuktukteam.tuktuk.dao.ClientDAO;
import com.tuktukteam.tuktuk.dao.CourseDAO;
import com.tuktukteam.tuktuk.model.Client;
import com.tuktukteam.tuktuk.model.Conducteur;
import com.tuktukteam.tuktuk.model.Course;

@RestController
@RequestMapping("/customer")
public class ClientRestController {
	@Autowired
	private ClientDAO clientDAO;
	
	@Autowired
	private CourseDAO courseDAO;


	public ClientRestController() {
		AutoFilterForSpringControllers.addController(this.getClass(), "/api");
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	@ResponseBody
	@RestrictedAccess(value = AccessType.PUBLIC)
	public ResponseEntity<Client> login(@RequestParam String username, @RequestParam String password) {
		Client client = new Client();

		client.setUsername(username);
		client.setPassword(password);

		try {
			client = clientDAO.findByValues(client);
		} catch (DAOException e) {
			client = null;
		}

		if (client == null)
			return new ResponseEntity<Client>(HttpStatus.NOT_ACCEPTABLE);
		return AccessTokenSecurity.buildResponseAndCreateAccess(client);
	}

	//récupère les infos du client actuellement connecté et lui renvoie
	@ResponseBody
	@RequestMapping(value = "", method = RequestMethod.GET)
	@RestrictedAccess(value = AccessType.TOKEN, authorized = Client.class)
	public ResponseEntity<Client> getProfile(@RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token) {
		return AccessTokenSecurity.buildResponse(AccessTokenSecurity.getUser(Client.class, token), token, HttpStatus.OK);
	}

	// création d'un nouveau client (register)
	@RequestMapping(value = "", method = RequestMethod.POST)
	@ResponseBody
	@RestrictedAccess(value = AccessType.PUBLIC)
	public ResponseEntity<Client> register(@RequestBody Client client, BindingResult result) 
	{
		if (!result.hasErrors())
		{
			client.setDate_inscription(new Date());
			clientDAO.hashFieldsAndSave(client);
			return new ResponseEntity<Client>(client, HttpStatus.OK);
		}
		return new ResponseEntity<Client>(HttpStatus.BAD_REQUEST);
	}

	//met à jour les infos du client et lui renvoie
	@ResponseBody
	@RequestMapping(value = "", method = RequestMethod.PUT)
	@RestrictedAccess(value = AccessType.TOKEN, authorized = Client.class)
	public ResponseEntity<Client> updateProfile(@RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token,
			@RequestBody Client client, BindingResult result) {

		Client cli = AccessTokenSecurity.getUser(Client.class, token);
		if (!result.hasErrors()) {
			client.setId(cli.getId());
			clientDAO.save(client);
			cli = clientDAO.find(cli.getId());
			return AccessTokenSecurity.buildResponse(cli, token, HttpStatus.OK);
		}
		return AccessTokenSecurity.buildResponse(Client.class, token, HttpStatus.NOT_MODIFIED);
	}

	//récupère l'historique du client actuellement connecté
	@ResponseBody
	@RequestMapping(value = "/history", method = RequestMethod.GET)
	@RestrictedAccess(value = AccessType.TOKEN, authorized = Client.class)
	public ResponseEntity<List<Course>> getRunsHistory(
			@RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token) {
		Client client = AccessTokenSecurity.getUser(Client.class, token);
		client = clientDAO.find(client.getId());
		return AccessTokenSecurity.buildResponse(client.getCourses(), token, HttpStatus.OK);

	}

	
	//calcul la moyenne du client actuellement connecté et lui renvoie
	@ResponseBody
	@RequestMapping(value = "/note", method = RequestMethod.GET)
	@RestrictedAccess(value = AccessType.TOKEN, authorized = {Client.class, Conducteur.class})
	public ResponseEntity<Float> calculAvgNote(@RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token) {

		float moy = 0;
		float somme = 0;
		int i = 0;
		
		if(AccessTokenSecurity.typeOfUser(token)==Client.class)
		{
			Client client = AccessTokenSecurity.getUser(Client.class, token);
			List<Course> courses = clientDAO.find(client.getId()).getCourses();
			for (Course course : courses) {
				if (course.getNoteConducteur() != -1) {
					somme = somme + course.getNoteConducteur();
					i++;
				}
			}
			moy = somme / i;
			
			return AccessTokenSecurity.buildResponse(moy, token, HttpStatus.OK);
		} else {
			Conducteur conducteur = AccessTokenSecurity.getUser(Conducteur.class, token);
			Course cou = courseDAO.getActualDriverRide(conducteur.getId());
			Client client = clientDAO.find(cou.getClient().getId());
			List<Course> courses = client.getCourses();
			for (Course course : courses) {
				if (course.getNoteConducteur() != -1) {
					somme = somme + course.getNoteConducteur();
					i++;
				}
			}
			moy = somme / i;
			return AccessTokenSecurity.buildResponse(moy, token, HttpStatus.OK);
		}
		
	}


	//calcul la moyenne du client demandé et la renvoie au conducteur
	@ResponseBody
	@RequestMapping(value = "/{id}/note", method = RequestMethod.GET)
	@RestrictedAccess(value = AccessType.TOKEN, authorized = Conducteur.class)
	public ResponseEntity<Integer> calculAvgNoteClient(@PathVariable int id, @RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token) {
		Client cli1 = clientDAO.find(id);
		float moy = 0;
		float somme = 0;
		int i = 0;
		List<Course> courses = cli1.getCourses();
		for (Course course : courses) {
			if (course.getNoteConducteur() != -1) {
				somme = somme + course.getNoteConducteur();
				i++;
			}
		}
		moy = somme / i;
		return AccessTokenSecurity.buildResponse(Math.round(moy), token, HttpStatus.OK);
	}

	//récupère les informations utiles du client demandé et les renvoie au conducteur
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	@RestrictedAccess(value = AccessType.TOKEN, authorized = Conducteur.class)
	public ResponseEntity<Client> getInfos(@PathVariable int id, @RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token) {
		return AccessTokenSecurity.buildResponse(clientDAO.getAndFillOnlyFieldsNotTaggedBy(id, ColumnTag.FRONT_RESTRICTED), token, HttpStatus.OK);
	}
	


}
