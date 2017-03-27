package com.tuktukteam.autosecurity;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tuktukteam.tools.Random;
import com.tuktukteam.tuktuk.model.Client;

import lombok.Getter;
import lombok.Setter;

public class AccessTokenSecurity
{
	public static final String TOKEN_HEADER_NAME = "TokenAuth-Token";
	public static final String TOKEN_EXPIRES_HEADER_NAME = "TokenAuth-Expires";
	
	private static final int DEFAULT_TOKEN_LENGTH = 30;
	private static final long DEFAULT_TOKEN_TIMEOUT = 5 * 60 * 1000; // 5 minutes

	public static void setTokenLength(int _tokenLength) { tokenLength = _tokenLength; }
	
	public static <T> T getUser(Class<T> userClass, String token)
	{
		AccessEntry accessEntry = accesses.get(token);
		
		if (accessEntry == null || !accessEntry.getUser().getClass().equals(userClass))
			return null;
		
		return userClass.cast(accessEntry.user);
	}
	
	public static  boolean tokenIsAssociatedWithUserType(String token, Class<?> userClass)
	{
		AccessEntry accessEntry = accesses.get(token);
		
		if (accessEntry == null || !accessEntry.getUser().getClass().equals(userClass))
			return false;
		
		return true;
	}
	
	public static Class<?> typeOfUser(String token)
	{
		AccessEntry accessEntry = accesses.get(token);
		
		if (accessEntry == null)
			return null;
		
		return accessEntry.getUser().getClass();
	}
	
	public <T> ResponseEntity<T> buildResponse(T entity, String oldToken, HttpStatus status)
	{
		HttpHeaders headers = new HttpHeaders();
		
		AccessEntry accessEntry = accesses.get(oldToken);
		
		if (accessEntry == null)
			return null;
		
		headers.add(TOKEN_HEADER_NAME, updateToken(oldToken));
		headers.add(TOKEN_EXPIRES_HEADER_NAME, String.valueOf(accessEntry.getTimeout()));

		return new ResponseEntity<T>(entity, headers, status);
	}
	
	public <T> ResponseEntity<T> buildResponse(String token, HttpStatus status)
	{
		HttpHeaders headers = new HttpHeaders();
		
		AccessEntry accessEntry = accesses.get(token);
		
		if (accessEntry == null) 
			return null;
		
		headers.add(TOKEN_HEADER_NAME, token);
		headers.add(TOKEN_EXPIRES_HEADER_NAME, String.valueOf(accessEntry.getTimeout()));

		return new ResponseEntity<T>(headers, status);
	}
	
	public static String newAccessToken(Object user, long timeout)
	{
		if (!accesses.isEmpty())
			for (AccessEntry accessEntry : accesses.values())
				if (accessEntry.getUser().equals(user))
					return accessEntry.getToken();
		
		String token = Random.nextString(tokenLength);
		accesses.put(token, new AccessEntry(user, token, timeout));
		return token;
	}
	
	public static String newAccessToken(Object user) { return newAccessToken(user, DEFAULT_TOKEN_TIMEOUT); }
	
	public static void addNewAccessInResponseHeaders(HttpHeaders headers, Object user, long timeout)
	{
		String token = newAccessToken(user, timeout);
		
		headers.add(TOKEN_HEADER_NAME, token);
		headers.add(TOKEN_EXPIRES_HEADER_NAME, String.valueOf(timeout));
	}

	public static void addNewAccessInHeaders(HttpHeaders headers, Object user)
	{
		addNewAccessInResponseHeaders(headers, user, DEFAULT_TOKEN_TIMEOUT);
	}
	
	public static String updateToken(String token) //TODO change method access to private
	{
		AccessEntry accessEntry = accesses.get(token);

		if (accessEntry == null)
			return null;
		
		String newToken = Random.nextString(tokenLength);
		accessEntry.setToken(newToken);
		accessEntry.updateInvalidateTime();
		accesses.remove(token);
		accesses.put(newToken, accessEntry);
		return newToken;
	}
	
	public static boolean accessIsAuthorized(HttpServletRequest request, Class<?> authorizedClasses[])
	{
		AccessEntry accessEntry = accesses.get(request.getHeader(TOKEN_HEADER_NAME));

		if (accessEntry == null)
			return false;
		
		if (authorizedClasses == null) // Si aucune classe, l'accès se fait par token sans spécification sur le type d'utilisateur
			return true;
		
		for (Class<?> userClass : authorizedClasses)
			if (userClass.equals(accessEntry.getUser().getClass()))
				return true;
		
		return false;
	}
	
	public static void addUpdatedTokenInResponseHeaders(HttpServletResponse response, String oldToken)
	{
		AccessEntry accessEntry = accesses.get(oldToken);
		
		if (accessEntry == null)
			return;
		
		response.addHeader(TOKEN_HEADER_NAME, updateToken(oldToken));
		response.addHeader(TOKEN_EXPIRES_HEADER_NAME, String.valueOf(accessEntry.getTimeout()));
	}
	
	public static void updateUserForToken(String token, Object user)
	{
		AccessEntry accessEntry = accesses.get(token);
		
		if (accessEntry == null)
			return;
		
		accessEntry.setUser(user);
	}
	
	private static int tokenLength = DEFAULT_TOKEN_LENGTH;
	
	private static Map<String, AccessEntry> accesses = new HashMap<>();;
	
	private static class AccessEntry
	{
		@Getter @Setter private Object user;
		@Getter @Setter private String token;
		@Getter @Setter private long timeout;
		@Getter         private long invalidateTime;
		
		public AccessEntry(Object user, String token, long timeout) 
		{
			this.user = user;
			this.token = token;
			this.timeout = timeout;
			updateInvalidateTime();
		}
		
		public void updateInvalidateTime() { invalidateTime = System.currentTimeMillis() + timeout; }
	}
}
