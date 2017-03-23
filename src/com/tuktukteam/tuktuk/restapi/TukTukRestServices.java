package com.tuktukteam.tuktuk.restapi;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.tuktukteam.tuktuk.model.Personne;

public class TukTukRestServices
{
	private static final String SERVICE_LOGIN = "/account/login?username={username}&password={password}";
	

	private static RestTemplate restTemplate = new RestTemplate();
	private static String contextPath;
	
	public static void setContextPath(String contextPath) { TukTukRestServices.contextPath = contextPath; }
	
	private static String fullURL(String url)
	{
		return "http://localhost:8080" + contextPath + "/api" + url;
	}
	
	public static Personne login(String username, String password)
	{
		ResponseEntity<Personne> personneEntity;
		
		try
		{
			personneEntity = restTemplate.getForEntity(fullURL(SERVICE_LOGIN), Personne.class, username, password);
		}
		catch(RestClientException e)
		{
			return null;			
		}
		
		if (personneEntity.getStatusCode() == HttpStatus.OK)
			return personneEntity.getBody();
		else
			return null;
	}
	

}
