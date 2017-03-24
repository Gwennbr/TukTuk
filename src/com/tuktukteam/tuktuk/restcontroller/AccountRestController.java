package com.tuktukteam.tuktuk.restcontroller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tuktukteam.tuktuk.dao.ClientDAO;
import com.tuktukteam.tuktuk.dao.ConducteurDAO;
import com.tuktukteam.tuktuk.model.Client;
import com.tuktukteam.tuktuk.model.Conducteur;

@RestController @RequestMapping("/account")
public class AccountRestController
{
	@Autowired private ConducteurDAO conducteurDAO;
	@Autowired private ClientDAO clientDAO;

	
	
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

