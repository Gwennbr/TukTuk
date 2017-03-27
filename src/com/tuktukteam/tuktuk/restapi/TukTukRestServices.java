package com.tuktukteam.tuktuk.restapi;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.tuktukteam.tuktuk.model.Client;
import com.tuktukteam.tuktuk.model.Conducteur;

public class TukTukRestServices
{
	private static final String SERVICE_LOGIN_CLIENT = "/client/login?username={username}&password={password}";
	private static final String SERVICE_LOGIN_CONDUCTEUR = "/conducteur/login?username={username}&password={password}";
	

	private static RestTemplate restTemplate = new RestTemplate();
	private static String contextPath;
	
	public static void setContextPath(String contextPath) { TukTukRestServices.contextPath = contextPath; }
	
	private static String fullURL(String url)
	{
		return "http://localhost:8080" + contextPath + "/api" + url;
	}
	
	public static Client loginClient(String username, String password)
	{
		ResponseEntity<Client> clientEntity;
		
		try
		{
			clientEntity = restTemplate.getForEntity(fullURL(SERVICE_LOGIN_CLIENT), Client.class, username, password);
		}
		catch(RestClientException e)
		{
			return null;			
		}
		
		
		if (clientEntity.getStatusCode() == HttpStatus.OK)
			return clientEntity.getBody();
		else
			return null;
	}
	
	public static Conducteur loginConducteur(String username, String password)
	{
		ResponseEntity<Conducteur> conducteurEntity;
		
		try
		{
			conducteurEntity = restTemplate.getForEntity(fullURL(SERVICE_LOGIN_CONDUCTEUR), Conducteur.class, username, password);
		}
		catch(RestClientException e)
		{
			return null;			
		}
		
		if (conducteurEntity.getStatusCode() == HttpStatus.OK)
			return conducteurEntity.getBody();
		else
			return null;
	}

}
