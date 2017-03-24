package com.tuktukteam.tuktuk.restcontroller;


import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tuktukteam.genericdao.DAOException;
import com.tuktukteam.tuktuk.dao.ClientDAO;
import com.tuktukteam.tuktuk.dao.ConducteurDAO;
import com.tuktukteam.tuktuk.dao.PersonneDAO;
import com.tuktukteam.tuktuk.model.Client;
import com.tuktukteam.tuktuk.model.Conducteur;
import com.tuktukteam.tuktuk.model.Personne;

@RestController @RequestMapping("/account")
public class AccountRestController
{
	@Autowired private PersonneDAO personneDAO;
	@Autowired private ConducteurDAO conducteurDAO;
	@Autowired private ClientDAO clientDAO;

	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Personne> login(@RequestParam String username, @RequestParam String password, HttpSession session)
	{

		Personne personne = new Personne();
		personne.setUsername(username);
		personne.setPassword(password);
		
		try
		{
			personne = personneDAO.findByValues(personne);
		}
		catch (DAOException e)
		{
			personne = null;
		}
		
		session.setAttribute("client", null);			
		session.setAttribute("conducteur", null);			

		if (personne == null)
			return new ResponseEntity<Personne>(HttpStatus.NOT_FOUND);

		if (personne instanceof Client)
			session.setAttribute("client", personne);
		else
			if (personne instanceof Conducteur)
			session.setAttribute("conducteur", personne);		

		return new ResponseEntity<Personne>(personne, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/registerDriver", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<Conducteur> registerDriver(@RequestBody Conducteur conducteur, BindingResult bindingResult)
	{
		if (bindingResult.hasErrors())
			return new ResponseEntity<Conducteur>(HttpStatus.BAD_REQUEST);
		
		return new ResponseEntity<Conducteur>(conducteurDAO.save(conducteur), HttpStatus.OK);
	}

	@RequestMapping(value = "/registerCustomer", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<Client> registerCustomer(@RequestBody Client client, BindingResult bindingResult)
	{
		if (bindingResult.hasErrors())
			return new ResponseEntity<Client>(HttpStatus.BAD_REQUEST);
		
		return new ResponseEntity<Client>(clientDAO.save(client), HttpStatus.OK);
	}
}

