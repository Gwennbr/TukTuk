package com.tuktukteam.tuktuk.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@javax.servlet.annotation.WebFilter("*")
public class WebFilter implements Filter
{
	private String whiteListURIs[] =
		{
				"^(resources/)",
				"^(login)$",
				"^(register)$"
				
		};
	
	@Override
	public void destroy()
	{
		// TODO Auto-generated method stub
		
	}

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
		// TODO Auto-generated method stub
		
	}

}
