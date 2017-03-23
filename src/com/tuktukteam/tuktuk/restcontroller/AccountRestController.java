package com.tuktukteam.tuktuk.restcontroller;


import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tuktukteam.genericdao.DAOException;
import com.tuktukteam.tuktuk.dao.PersonneDAO;
import com.tuktukteam.tuktuk.model.Client;
import com.tuktukteam.tuktuk.model.Conducteur;
import com.tuktukteam.tuktuk.model.Personne;

@RestController @RequestMapping("/account")
public class AccountRestController
{
	@Autowired
	private PersonneDAO personneDAO;

	
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
	
}
