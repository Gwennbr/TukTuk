package com.tuktukteam.tuktuk.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.tuktukteam.tuktuk.model.Personne;
import com.tuktukteam.tuktuk.restapi.TukTukRestServices;

@Controller
public class LoginController
{
//	@Autowired
//	private RequestMappingHandlerMapping handlerMapping;
	
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
	public String login(@RequestParam String username, @RequestParam String password, Model model)
	{
		Personne personne = TukTukRestServices.login(username, password);
		
		if (personne == null)
		{
			model.addAttribute("errorMsg", "login failed");
			return "login"; 
		}

		return "redirect:/";
	}
}
