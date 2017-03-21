package com.tuktukteam.tuktuk.controller;

import java.io.IOException;
import java.net.URL;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuktukteam.tuktuk.model.Client;

@Controller
public class LoginController
{

	@RequestMapping("/login")
	public String login()
	{
		ObjectMapper mapper = new ObjectMapper();
		
		Client c = null;
		try
		{
			c = mapper.readValue(new URL("http://localhost:8080/TukTuk/api/toto"), Client.class);
		}
		catch (IOException e)
		{ 
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		System.out.println(c.getNuméroCarteBancaire() + " " + c.getPictogramme());
		
		return "login"; 
	}
}
