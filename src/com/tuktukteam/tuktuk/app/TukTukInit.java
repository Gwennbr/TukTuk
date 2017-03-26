package com.tuktukteam.tuktuk.app;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.springframework.web.bind.annotation.RequestMethod;

import com.tuktukteam.autosecurity.AutoFilterForSpringControllers;
import com.tuktukteam.tuktuk.restapi.TukTukRestServices;

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
		AutoFilterForSpringControllers.addAllowedUri("/resources/.*", RequestMethod.values());
		TukTukRestServices.setContextPath(context.getServletContext().getContextPath());
	}

}
