package com.tuktukteam.autosecurity;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;


public class ResponseWrapper extends HttpServletResponseWrapper
{

	public ResponseWrapper(HttpServletResponse response, HttpServletRequest request, RestrictedAccess accessInfos) 
	{ 
		super(response);
		output = new CharArrayWriter();
		this.request = request;
		this.accessInfos = accessInfos;
	}

	private CharArrayWriter output;
	private boolean bHeaderAdded = false;
	private HttpServletRequest request;
	protected RestrictedAccess accessInfos;
	
//	private boolean usingWriter = false;
	
	public String toString() { return output.toString(); }

	//public PrintWriter getWriter() { return new PrintWriter(output); }
	
	/*
	 @Override
     public ServletOutputStream getOutputStream() throws IOException
     {
         // will error out, if in use
         if (usingWriter) {
             super.getOutputStream();
         }
         usingWriter = true;
         return output.getStream();
     }
	 */
	 
     @Override
     public PrintWriter getWriter() throws IOException
     {
         return new PrintWriter(output);
     }

	@Override
	public void setStatus(int sc)
	{
		super.setStatus(sc);
 		if (!bHeaderAdded && sc >= 200 && sc <= 299)
 		{
 			String token = (String) request.getAttribute(AccessTokenSecurity.TOKEN_HEADER_NAME);
 			if (token != null)
 				;
 			System.out.println("set status");
 			//super.addHeader(headerName, headerValue);
 			bHeaderAdded = true;
 		}	}
     
}
