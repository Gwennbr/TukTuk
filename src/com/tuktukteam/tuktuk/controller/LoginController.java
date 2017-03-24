package com.tuktukteam.tuktuk.controller;


import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.tuktukteam.tuktuk.model.Client;
import com.tuktukteam.tuktuk.model.Conducteur;
import com.tuktukteam.tuktuk.restapi.TukTukRestServices;

@Controller
public class LoginController
{
//	@Autowired
//	private RequestMappingHandlerMapping handlerMapping;
	
//	@Autowired
//	private AccountRestController AccountRestController;

//	@Autowired
//	private AutoWebFilter filter;
	
	public LoginController()
	{
		//filter.addController(LoginController.class);
	}
	
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
		session.setAttribute("client", null);			
		session.setAttribute("conducteur", null);			

		Conducteur conducteur = TukTukRestServices.loginConducteur(username, password);

		if (conducteur != null)
		{
			session.setAttribute("conducteur", conducteur);		
			return "redirect:/";
		}

		Client client = TukTukRestServices.loginClient(username, password);
			
		if (client == null)
		{
			model.addAttribute("errorMsg", "login failed");
			return "login"; 
		}
		
		session.setAttribute("client", client);

		return "redirect:/";
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home()
	{
		return "accueil";
	}
}
