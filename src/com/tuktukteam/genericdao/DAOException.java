package com.tuktukteam.genericdao;


public class DAOException extends Exception
{
	private static final long serialVersionUID = 1L;

	public DAOException(String mess) 
	{ 
		super(Thread.currentThread().getStackTrace()[2].toString() + ": " + mess); 
	}

	public DAOException(String mess, Exception e) 
	{ 
		super(String.format("%s : %s (%s)", 
				Thread.currentThread().getStackTrace()[1].toString(),
				mess,
				e.getMessage())); 
	}
}
