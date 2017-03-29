package com.tuktukteam.autosecurity;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.filter.GenericFilterBean;

import com.tuktukteam.autosecurity.RestrictedAccess.AccessOptions;
import com.tuktukteam.autosecurity.RestrictedAccess.AccessType;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Controller
public class AutoFilterForSpringControllers extends GenericFilterBean
{
//	@Autowired
//	private RequestMappingHandlerMapping handlerMapping;


	private static @AllArgsConstructor class SecurityMappingEntry
	{
		@Getter private String uri;
		@Getter AccessType accessType;
		@Getter Class<?> authorized[];
		@Getter String attributesNames[];
		@Getter String onForbidden;
		@Getter AccessOptions options[];		
		@Getter private RequestMethod [] methods;	
	}
	
	private static List<SecurityMappingEntry> securityMappings = new ArrayList<>();
	private static List<Class<?>> controllersAdded = new ArrayList<>();
	
	private static String convertToRegex(String uri)
	{
		return '^' + Matcher.quoteReplacement(uri).replaceAll("\\{.*\\}", "[^/.]*") + '$';
	}
	
	private static List<String> buildMappingsList(String prefix, String classMappings[], String methodMappings[])
	{
		List<String> mappings = new ArrayList<>();
		
		if (classMappings.length == 0)
		{
			for (String url : methodMappings)
				mappings.add(convertToRegex(prefix + url));
		}
		else
			for (String classUrl : classMappings)
				if (methodMappings.length == 0)
					mappings.add(convertToRegex(prefix + classUrl));
				else
					for (String methodUrl : methodMappings)
						mappings.add(convertToRegex(prefix + classUrl + methodUrl));
		return mappings;
	}
	
	private static RequestMethod [] getRequestMethods(RequestMethod classMethods[], RequestMethod methodMethods[])
	{
		List<RequestMethod> methods = new ArrayList<>();
		
		if (classMethods.length == 0)
		{
			if (methodMethods.length == 0)
				for (RequestMethod method : RequestMethod.values())
					methods.add(method);
			else
				for (RequestMethod method : methodMethods)
					methods.add(method);
		}
		else
			if (methodMethods.length == 0)
				for (RequestMethod method : classMethods)
					methods.add(method);
			else
				for (RequestMethod mMethod : methodMethods)
					for (RequestMethod cMethod : classMethods)
						if (cMethod == mMethod && !methods.contains(mMethod))
						{
							methods.add(mMethod);
							break;
						}
		
		RequestMethod [] methodsArray = new RequestMethod[methods.size()];
		
		return methods.toArray(methodsArray);
	}
	
	public static void addAllowedUri(String regex, RequestMethod requestMethods[])
	{
		securityMappings.add(new SecurityMappingEntry(regex, 
							AccessType.PUBLIC, 
							null,
							null,
							null,
							null,
							requestMethods));
	}
	
	public static void addController(Class<?> type, String prefix)
	{
		if (controllersAdded.contains(type))
		{
			System.out.println(String.format("filter : controller %s already added", type.getSimpleName()));
			return;
		}
		controllersAdded.add(type);
		
		if (prefix == null)
			prefix = "";
		
		String classMappings[] = {};

		RequestMapping classRequestMapping = type.getDeclaredAnnotation(RequestMapping.class);
		if (classRequestMapping != null)
			classMappings = classRequestMapping.value();
		
		for (Method method : type.getDeclaredMethods())
		{
			RequestMapping methodRequestMapping = method.getDeclaredAnnotation(RequestMapping.class);
			if (methodRequestMapping != null)
			{
				RestrictedAccess methodSecurityAccess = method.getDeclaredAnnotation(RestrictedAccess.class);

				String [] methodMappings = methodRequestMapping.value();

				for (String uri : buildMappingsList(prefix, classMappings, methodMappings))
				{
					RequestMethod classMethods[] = {};
					if (classRequestMapping != null)
						classMethods = classRequestMapping.method();
					
					RequestMethod methods[] = getRequestMethods(classMethods, methodRequestMapping.method());
					
					if (methods.length == 0)
						System.out.println("filter add : " +  uri + " (" + "BLOCKED : NO HTTP METHODS LEFT !" + ")");
					else
					{
						StringBuilder sb = new StringBuilder(methods.length * 6);
						for (RequestMethod met : methods)
							if (sb.length() == 0)
								sb.append(met.toString());
							else
								sb.append(String.format(", %s", met.toString()));
						System.out.println(String.format("filter add : %s (%s)", uri, sb.toString()));
					
						if (methodSecurityAccess != null)
							securityMappings.add(new SecurityMappingEntry(
									uri, 
									methodSecurityAccess.value(),
									methodSecurityAccess.authorized().clone(),
									methodSecurityAccess.attributesNames().clone(),
									methodSecurityAccess.onForbidden(),
									methodSecurityAccess.options().clone(),
									methods));
						else
						{
							RestrictedAccess classSecurityAccess = type.getDeclaredAnnotation(RestrictedAccess.class);
							if (classSecurityAccess == null)
								securityMappings.add(new SecurityMappingEntry(
										uri, 
										AccessType.PUBLIC,
										null,
										null,
										null,
										null,
										methods));
							else
								securityMappings.add(new SecurityMappingEntry(
										uri, 
										classSecurityAccess.value(),
										classSecurityAccess.authorized().clone(),
										classSecurityAccess.attributesNames().clone(),
										classSecurityAccess.onForbidden(),
										classSecurityAccess.options().clone(),
										methods));
								
							
						}
					}
				}
			}
		}
	}
	
	public boolean userInSessionHasValidAccess(HttpSession session, Class<?> authorizedUsers[], String attributeNames[])
	{
		if (authorizedUsers == null || authorizedUsers.length == 0)
			return false;
		if (attributeNames != null && attributeNames.length != 0 && attributeNames.length != authorizedUsers.length)
			return false;
		for (int i = 0 ; i < authorizedUsers.length ; ++i)
			if (attributeNames == null || attributeNames.length == 0)
			{
				String name = authorizedUsers[i].getSimpleName();
				Object user = session.getAttribute(String.format("%c%s", Character.toLowerCase(name.charAt(0)), name.substring(1)));
				if (user != null && user.getClass().equals(authorizedUsers[i]))
					return true;
			}
			else
			{
				Object user = session.getAttribute(attributeNames[i]);
				if (user != null && user.getClass().equals(authorizedUsers[i]))
					return true;
			}
		return false;
	}
	
	public void onForbidden(HttpServletResponse response, String command) throws IOException
	{
		String commandStatus = "status";
		String commandRedirect = "redirect";
		String commandLowerCase = command.toLowerCase();

		if (commandLowerCase.startsWith(commandStatus))
		{
			int status;
			try
			{
				status = Integer.parseInt(command.substring(commandStatus.length()+1));
			}
			catch (NumberFormatException e)
			{
				status = 500;
			}
			response.setStatus(status);
		}
		else
			if (commandLowerCase.startsWith(commandRedirect))
				/*
				if (command.charAt(commandRedirect.length()+1) == '/')
					response.sendRedirect(command.substring(commandRedirect.length()+2));
				else
				*/
					response.sendRedirect(command.substring(commandRedirect.length()+1));
	}
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain)
			throws IOException, ServletException
	{
		// TODO Auto-generated method stub
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)resp;
		String uri = request.getRequestURI().replaceFirst(request.getContextPath(), "");

		for (SecurityMappingEntry sec : securityMappings)
			if (uri.matches(sec.getUri()))
			{
				//ResponseWrapper responseWrapper = new ResponseWrapper(response, request, access); //AccessTokenSecurity.TOKEN_HEADER_NAME, token);
				switch (sec.accessType)
				{
					case PUBLIC:
						filterChain.doFilter(request, response);								
						break;
					
					case CLASS_IN_SESSION:
						if (userInSessionHasValidAccess(request.getSession(), sec.authorized, sec.attributesNames))
							filterChain.doFilter(request, response);
						else 
							onForbidden(response, sec.onForbidden);
						break;
							
					case TOKEN:
						if (AccessTokenSecurity.accessIsAuthorized(request, sec.authorized))
						{
							//String token = request.getHeader(AccessTokenSecurity.TOKEN_HEADER_NAME);
							//token = AccessTokenSecurity.updateToken(token);
							//PrintWriter out = response.getWriter();
							//AccessTokenSecurity.addUpdatedTokenInResponseHeaders(response, request.getHeader(AccessTokenSecurity.TOKEN_HEADER_NAME));
							filterChain.doFilter(request, response); //responseWrapper);
							//out.write(responseWrapper.toString());
							//out.close();
						}
						else
							onForbidden(response, sec.onForbidden);
						break;
							
					case NONE:
					default:
						onForbidden(response, sec.onForbidden);
						break;
				}
				//responseWrapper.getWriter().write(responseWrapper.toString());
				return;
			}
		filterChain.doFilter(request, response);

	}

/*	
	private String whiteListURIs[] =
		{
				"^$",
				"^(resources/).*",
				"^(login)$",
				"^(register)$",
				"^(api/toto)$",
				"^(api/).*" //TODO changer pour ne laisser l'accès qu'à login & register
		};
	*/
	@Override
	protected void initFilterBean() throws ServletException
	{
		super.initFilterBean();
	}

	@Override
	public void destroy()
	{
		// TODO Auto-generated method stub
		
	}
}
