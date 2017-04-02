package com.tuktukteam.tuktuk.restapi;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.tuktukteam.tuktuk.model.Client;
import com.tuktukteam.tuktuk.model.Conducteur;

public class TukTukRestServices
{
	private static final String SERVICE_LOGIN_CUSTOMER = "/customer/login?username={username}&password={password}";
	private static final String SERVICE_LOGIN_DRIVER = "/driver/login?username={username}&password={password}";
	private static final String SERVICE_REGISTER_CUSTOMER = "/customer";
	private static final String SERVICE_REGISTER_DRIVER = "/driver";

	private static RestTemplate restTemplate = new RestTemplate();
	private static String contextPath;
	
	public static void setContextPath(String contextPath) { TukTukRestServices.contextPath = contextPath; }
	
	private static String fullURL(String url)
	{
		return "http://localhost:8080" + contextPath + "/api" + url;
	}
	
	public static ResponseEntity<Client> loginClient(String username, String password)
	{
		ResponseEntity<Client> clientEntity;
		
		try
		{
			clientEntity = restTemplate.getForEntity(fullURL(SERVICE_LOGIN_CUSTOMER), Client.class, username, password);
		}
		catch(RestClientException e)
		{
			return null;			
		}
		
		
		if (clientEntity.getStatusCode() == HttpStatus.OK)
			return clientEntity;
		else
			return null;
	}
	
	public static ResponseEntity<Conducteur> loginConducteur(String username, String password)
	{
		ResponseEntity<Conducteur> conducteurEntity;
		
		try
		{
			conducteurEntity = restTemplate.getForEntity(fullURL(SERVICE_LOGIN_DRIVER), Conducteur.class, username, password);
		}
		catch(RestClientException e)
		{
			return null;			
		}
		
		if (conducteurEntity.getStatusCode() == HttpStatus.OK)
			return conducteurEntity;
		else
			return null;
	}

	public static boolean registerClient(Client client)
	{
		try
		{
			ResponseEntity<Client> clientEntity = restTemplate.postForEntity(fullURL(SERVICE_REGISTER_CUSTOMER), client, Client.class);
			if (clientEntity != null && clientEntity.getStatusCode().is2xxSuccessful())
				return true;
		}
		catch (RestClientException e)
		{
		}
		return false;
	}
	
	public static boolean registerConducteur(Conducteur conducteur)
	{
		try
		{
			ResponseEntity<Conducteur> conducteurEntity = restTemplate.postForEntity(fullURL(SERVICE_REGISTER_DRIVER), conducteur, Conducteur.class);
			if (conducteurEntity != null && conducteurEntity.getStatusCode().is2xxSuccessful())
				return true;
		}
		catch (RestClientException e)
		{
		}
		return false;
	}

}
