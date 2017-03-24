package com.tuktukteam.tuktuk.controller;


import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.tuktukteam.tuktuk.model.Personne;
import com.tuktukteam.tuktuk.restapi.TukTukRestServices;
import com.tuktukteam.tuktuk.restcontroller.AccountRestController;

@Controller
public class LoginController
{
//	@Autowired
//	private RequestMappingHandlerMapping handlerMapping;
	
//	@Autowired
//	private AccountRestController AccountRestController;
	
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
	public String login(@RequestParam String username, @RequestParam String password, Model model, HttpSession session)
	{
		Personne personne = TukTukRestServices.login(username, password);
		
		if (personne == null)
		{
			model.addAttribute("errorMsg", "login failed");
			return "login"; 
		}

		return "redirect:/";
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home()
	{
		return "accueil";
	}
}
