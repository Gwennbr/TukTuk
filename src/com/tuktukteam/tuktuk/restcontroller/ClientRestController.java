package com.tuktukteam.tuktuk.restcontroller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
import com.tuktukteam.tuktuk.model.Client;
import com.tuktukteam.tuktuk.model.Conducteur;
import com.tuktukteam.tuktuk.model.Course;
import com.tuktukteam.tuktuk.model.Personne;

@RestController
@RequestMapping("/client")
public class ClientRestController
{
	@Autowired private ClientDAO clientDAO;
	
	public ClientRestController() { AutoFilterForSpringControllers.addController(this.getClass(), "/api"); }
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	@ResponseBody
	@RestrictedAccess(value=AccessType.PUBLIC)
	public ResponseEntity<Client> login(@RequestParam String username, @RequestParam String password)
	{
		Client client = new Client();

		client.setUsername(username);
		client.setPassword(password);
		
		try
		{
			client = clientDAO.findByValues(client);
		}
		catch (DAOException e)
		{
			client = null;
		}
		
		if (client == null)
			return new ResponseEntity<Client>(HttpStatus.NOT_ACCEPTABLE);

		HttpHeaders headers = new HttpHeaders();
		AccessTokenSecurity.addNewAccessInHeaders(headers, client);
		return new ResponseEntity<Client>(client, headers, HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "/profil_old", method = RequestMethod.GET)
	public ResponseEntity<Client> getProfile_old(HttpSession session) {

		Client c = (Client) session.getAttribute("client");

		if (c != null) {
			int idC = c.getId();
			return new ResponseEntity<Client>(this.clientDAO.find(idC), HttpStatus.OK);
		}
		return new ResponseEntity<Client>(HttpStatus.FORBIDDEN);
	}

	@ResponseBody
	@RequestMapping(value = "/profil", method = RequestMethod.GET)
	@RestrictedAccess(value=AccessType.TOKEN, authorized=Client.class)
	public ResponseEntity<Client> getProfile(@RequestHeader(AccessTokenSecurity.TOKEN_HEADER_NAME) String token)
	{
		return new ResponseEntity<Client>(AccessTokenSecurity.getUser(Client.class, token), HttpStatus.OK);
	}
	
	@RequestMapping(value="", method = RequestMethod.PUT)
	public ResponseEntity<Client> updateProfile(HttpSession session, @RequestBody Personne p, BindingResult result) {
		Client cli = (Client) session.getAttribute("client");
		
		if(cli!= null){
			if(!result.hasErrors()){
				p.setId(cli.getId());
				//personneDAO.save(p);
				cli = clientDAO.find(cli.getId());
				return new ResponseEntity<Client>(cli, HttpStatus.OK);
			}
			return new ResponseEntity<Client>(HttpStatus.NOT_MODIFIED);
		}		
		return new ResponseEntity<Client>(HttpStatus.FORBIDDEN);
	}
	

	@RequestMapping(value="/historique", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Client> getRunsHistory(HttpSession session) {
		Client client = (Client) session.getAttribute("client");
		if (client != null) {
			client = clientDAO.find(client.getId());
			return new ResponseEntity<Client>(client, HttpStatus.OK);
		}

		return new ResponseEntity<Client>(HttpStatus.FORBIDDEN);
	}

	@RequestMapping(value = "/{id}/historique", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<List<Course>> getCustomerHistory(@PathVariable int id, HttpSession session) {
		Conducteur conducteur = (Conducteur) session.getAttribute("conducteur");

		if (conducteur != null) {
			Client client = clientDAO.find(id);
			return new ResponseEntity<List<Course>>(client.getCourses(), HttpStatus.OK);
		}
		return new ResponseEntity<List<Course>>(HttpStatus.FORBIDDEN);
	}
	
	
	
	@RequestMapping(value="/{id}/note", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Float> calculAvgNote(@PathVariable int id, HttpSession session)
	{
		Conducteur cond = (Conducteur) session.getAttribute("conducteur");
		Client cli1 = clientDAO.find(id);
		Client cli2 = (Client) session.getAttribute("client");
		
		if(cond!= null || (cli2 != null && cli2.getId()==cli1.getId())){
			float moy=0;
			float somme=0;
			int i=0;
			List<Course> courses = cli1.getCourses();
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

	@RequestMapping(value = "/{id}/infos", method = RequestMethod.GET)
	@ResponseBody
	@RestrictedAccess(value=AccessType.TOKEN, authorized=Conducteur.class)
	public ResponseEntity<Client> getInfos(@PathVariable int id, HttpSession session) {

		return new ResponseEntity<Client>(clientDAO.getAndFillOnlyFieldsNotTaggedBy(id, ColumnTag.FRONT_RESTRICTED), HttpStatus.OK);
	}

}
