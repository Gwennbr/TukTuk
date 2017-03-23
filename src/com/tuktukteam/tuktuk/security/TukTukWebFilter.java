package com.tuktukteam.tuktuk.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.GenericFilterBean;


public class TukTukWebFilter extends GenericFilterBean
{
//	@Autowired
//	private RequestMappingHandlerMapping handlerMapping;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain)
			throws IOException, ServletException
	{
		// TODO Auto-generated method stub
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
				((HttpServletResponse)resp).sendRedirect(R.getContextPath() + "/login");
		}
	}

	
	private String whiteListURIs[] =
		{
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
