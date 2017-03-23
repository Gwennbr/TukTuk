package com.tuktukteam.tuktuk.app;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.tuktukteam.tuktuk.controller.TukTukRestServices;

@WebListener
public class TukTukInit implements ServletContextListener
{

	@Override
	public void contextDestroyed(ServletContextEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contextInitialized(ServletContextEvent context)
	{
		TukTukRestServices.setContextPath(context.getServletContext().getContextPath());
	}

}
