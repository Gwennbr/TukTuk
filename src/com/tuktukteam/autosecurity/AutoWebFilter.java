package com.tuktukteam.autosecurity;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.filter.GenericFilterBean;

import lombok.AllArgsConstructor;
import lombok.Getter;


public class AutoWebFilter extends GenericFilterBean
{
//	@Autowired
//	private RequestMappingHandlerMapping handlerMapping;

	private @AllArgsConstructor class SecurityMappingEntry
	{
		@Getter private String url;
		@Getter private SecurityAccess access;
	}
	
	private List<SecurityMappingEntry> securityMappings;
	
	public void addController(Class<?> type)
	{
		for (Field field : type.getDeclaredFields())
		{
			field.setAccessible(true);
			RequestMapping requestMapping = field.getAnnotation(RequestMapping.class);
			SecurityAccess securityAccess = field.getAnnotation(SecurityAccess.class);
			if (requestMapping != null)
			{
				String [] mappings = requestMapping.value();
				if (mappings != null)
					for (String mapping : mappings)
					{
						System.out.print("security mapping : " + mapping + " -> ");
						mapping = mapping.replaceAll("{.*}", "*");
						System.out.println(mapping);
						if (securityAccess == null)
							;
					}
			}
		}
	}
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain)
			throws IOException, ServletException
	{
		// TODO Auto-generated method stub
		HttpServletRequest R = (HttpServletRequest)req;
		String uri = R.getRequestURI().replaceFirst(R.getContextPath() + "/", "");

		System.out.println(uri);
		if (R.getSession().getAttribute("user") != null)
			filterChain.doFilter(req, resp);
		else
		{
			boolean bPassed = false;
			for (String whiteListURI : whiteListURIs)
				if (uri.matches(whiteListURI))
				{
					bPassed = true;
					filterChain.doFilter(req, resp);
				}
			if (!bPassed)
				((HttpServletResponse)resp).sendRedirect(R.getContextPath() + "/login");
		}
	}

	
	private String whiteListURIs[] =
		{
				"^$",
				"^(resources/).*",
				"^(login)$",
				"^(register)$",
				"^(api/toto)$",
				"^(api/).*" //TODO changer pour ne laisser l'accès qu'à login & register
		};
	
	@Override
	protected void initFilterBean() throws ServletException
	{
		super.initFilterBean();
		
		/*
		for (RequestMappingInfo element : handlerMapping.getHandlerMethods().keySet())	
		{
			System.out.println(element.getName() + " " + element.getClass());
		}
		*/		
	}

	@Override
	public void destroy()
	{
		// TODO Auto-generated method stub
		
	}
/*
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain)
			throws IOException, ServletException
	{
		HttpServletRequest R = (HttpServletRequest)req;
		String uri = R.getRequestURI().replaceFirst(R.getContextPath() + "/", "");

		if (R.getSession().getAttribute("user") != null)
			filterChain.doFilter(req, resp);
		else
		{
			boolean bPassed = false;
			for (String whiteListURI : whiteListURIs)
				if (uri.matches(whiteListURI))
				{
					bPassed = true;
					filterChain.doFilter(req, resp);
				}
			if (!bPassed)
				((HttpServletResponse)resp).sendRedirect("./login");
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException
	{
		for (RequestMappingInfo element : handlerMapping.getHandlerMethods().keySet())	
		{
			System.out.println(element.getName() + " " + element.getClass());
		}
		
	}
	*/

}
