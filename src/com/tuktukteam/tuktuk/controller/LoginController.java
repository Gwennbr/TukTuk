package com.tuktukteam.tuktuk.controller;

import java.io.IOException;
import java.net.URL;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuktukteam.tuktuk.model.Client;
import com.tuktukteam.tuktuk.model.Personne;

@Controller
public class LoginController
{

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String showLoginView(Model model)
	{
		/*
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
		
		System.out.println(c.getNumeroCarteBancaire() + " " + c.getPictogramme());
		
		model.addAttribute("personne", c);
		*/
		return "login"; 
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(@ModelAttribute Personne personne, Model model, HttpSession session)
	{
		ObjectMapper mapper = new ObjectMapper();
		
		
		return "home";
	}
}
