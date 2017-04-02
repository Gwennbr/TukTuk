package com.tuktukteam.tuktuk.controller;



import javax.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.tuktukteam.autosecurity.AccessTokenSecurity;
import com.tuktukteam.autosecurity.AutoFilterForSpringControllers;
import com.tuktukteam.autosecurity.RestrictedAccess;
import com.tuktukteam.autosecurity.RestrictedAccess.AccessType;
import com.tuktukteam.tuktuk.model.Client;
import com.tuktukteam.tuktuk.model.Conducteur;
import com.tuktukteam.tuktuk.restapi.TukTukRestServices;

@Controller
public class MainController
{
	public MainController() { AutoFilterForSpringControllers.addController(getClass(), ""); }
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	@RestrictedAccess(AccessType.PUBLIC)
	public String loginView(Model model) 
	{
		return "login"; 
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@RestrictedAccess(AccessType.PUBLIC)
	public String loginCallback(@RequestParam String username, @RequestParam String password, Model model, HttpSession session)
	{
		session.setAttribute("client", null);			
		session.setAttribute("conducteur", null);			
	
		/*
		ResponseEntity<Conducteur> conducteurEntity = conducteurRest.login(username, password);

		if (conducteurEntity.getStatusCode() == HttpStatus.OK)
		{
			session.setAttribute("conducteur", conducteurEntity.getBody());		
			session.setAttribute("token", conducteurEntity.getHeaders().getFirst(AccessTokenSecurity.TOKEN_HEADER_NAME));
			return "redirect:/";
		}

		ResponseEntity<Client> clientEntity = clientRest.login(username, password);
			
		if (clientEntity.getStatusCode() != HttpStatus.OK) 
		{
			model.addAttribute("errorMsg", "login failed");
			return "login"; 
		}
		
		session.setAttribute("client", clientEntity.getBody());
		session.setAttribute("token", clientEntity.getHeaders().getFirst(AccessTokenSecurity.TOKEN_HEADER_NAME));
		return "redirect:/";
		 */
		
		ResponseEntity<Conducteur> conducteurEntity = TukTukRestServices.loginConducteur(username, password);

		if (conducteurEntity != null)
		{
			session.setAttribute("conducteur", conducteurEntity.getBody());		
			session.setAttribute("token", conducteurEntity.getHeaders().getFirst(AccessTokenSecurity.TOKEN_HEADER_NAME));
			return "redirect:/";
		}

		ResponseEntity<Client> clientEntity = TukTukRestServices.loginClient(username, password);
			
		if (clientEntity == null)
		{
			model.addAttribute("errorMsg", "login failed");
			return "login"; 
		}
		
		session.setAttribute("client", clientEntity.getBody());
		session.setAttribute("token", clientEntity.getHeaders().getFirst(AccessTokenSecurity.TOKEN_HEADER_NAME));

		return "redirect:/";
		
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	@RestrictedAccess(value=AccessType.CLASS_IN_SESSION, authorized={Client.class,Conducteur.class}, onForbidden="redirect:login")
	public String homeView()
	{
		return "accueil";
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	@RestrictedAccess(value=AccessType.CLASS_IN_SESSION, authorized={Client.class,Conducteur.class}, onForbidden="redirect:login")
	public String logout(HttpSession session)
	{
		session.invalidate();
		return "redirect:login";
	}
	
	@RequestMapping(value = "/register", method = RequestMethod.GET)
	@RestrictedAccess(value=AccessType.PUBLIC)
	public String registerView()
	{
		return "register";
	}
	
	@RequestMapping(value = "/registerCustomer", method = RequestMethod.POST)
	@RestrictedAccess(value=AccessType.PUBLIC)
	public String registerCustomer(@ModelAttribute Client client, Model model)
	{
		if (TukTukRestServices.registerClient(client))
			return "redirect:login";
		else
		{
			model.addAttribute("errorMsg", "register failed");
			return "register";
		}
	}

	@RequestMapping(value = "/registerDriver", method = RequestMethod.POST)
	@RestrictedAccess(value=AccessType.PUBLIC)
	public String registerDriver(@ModelAttribute Conducteur conducteur, Model model)
	{
		if (TukTukRestServices.registerConducteur(conducteur))
			return "redirect:login";
		else
		{
			model.addAttribute("errorMsg", "register failed");
			return "register";
		}
	}

}
