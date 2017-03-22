package com.tuktukteam.tuktuk.restcontroller;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tuktukteam.tuktuk.model.Client;

@RestController
public class TestsREST
{
	@RequestMapping(value = "/toto", method = RequestMethod.GET)
	public ResponseEntity<Client> toto()
	{
		Client c = new Client();
		c.setDateValiditeCB(new Date());
		c.setNumeroCarteBancaire("1234 1234 1234 1234");
		c.setPictogramme("123");
		
		return new ResponseEntity<>(c, HttpStatus.OK);
	}
	
}
