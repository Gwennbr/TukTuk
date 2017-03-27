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
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.tuktukteam.autosecurity.AutoFilterForSpringControllers;
import com.tuktukteam.autosecurity.RestrictedAccess;
import com.tuktukteam.autosecurity.RestrictedAccess.AccessType;
import com.tuktukteam.tuktuk.model.Client;
import com.tuktukteam.tuktuk.model.Conducteur;
import com.tuktukteam.tuktuk.restcontroller.ClientRestController;
import com.tuktukteam.tuktuk.restcontroller.ConducteurRestController;

@Controller
public class MainController
{
//	@Autowired
//	private RequestMappingHandlerMapping handlerMapping;
	
	@Autowired
	private ClientRestController clientRest;

	@Autowired
	private ConducteurRestController conducteurRest;

//	@Autowired
//	private AutoWebFilter zzz;
	
	public MainController() { AutoFilterForSpringControllers.addController(getClass(), ""); }
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String showLoginView(Model model) 
	{
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
		return "accueil2"; 
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(@RequestParam String username, @RequestParam String password, Model model, HttpSession session)
	{
		session.setAttribute("client", null);			
		session.setAttribute("conducteur", null);			
	
		ResponseEntity<Conducteur> conducteurEntity = conducteurRest.login(username, password);

		if (conducteurEntity.getStatusCode() == HttpStatus.OK)
		{
			session.setAttribute("conducteur", conducteurEntity.getBody());		
			return "redirect:/";
		}

		ResponseEntity<Client> clientEntity = clientRest.login(username, password);
			
		if (clientEntity.getStatusCode() != HttpStatus.OK) 
		{
			model.addAttribute("errorMsg", "login failed");
			return "login"; 
		}
		
		session.setAttribute("client", clientEntity.getBody());

		return "redirect:/";

		/*
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
		*/
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	@RestrictedAccess(value=AccessType.CLASS_IN_SESSION, authorized={Client.class,Conducteur.class}, onForbidden="redirect:login")
	public String home()
	{
		return "accueil";
	}

}
