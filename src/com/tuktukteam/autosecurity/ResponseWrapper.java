package com.tuktukteam.autosecurity;

import java.io.CharArrayWriter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;


public class ResponseWrapper extends HttpServletResponseWrapper
{

	public ResponseWrapper(HttpServletResponse response, String headerName, String headerValue) 
	{ 
		super(response);
		output = new CharArrayWriter();
		this.headerName = headerName;
		this.headerValue = headerValue;
	}

	private CharArrayWriter output;
	private boolean bHeaderAdded = false;
	private String headerName;
	private String headerValue;
	
//	private boolean usingWriter = false;
	
	public String toString() { return output.toString(); }

	@Override
	public void addHeader(String name, String value)
	{
		System.out.println(String.format("Header: %s=%s", name, value));
		super.addHeader(name, value);
		if (!bHeaderAdded)
		{
			super.addHeader(headerName, headerValue);
			bHeaderAdded = true;
		}
	}
	
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
	 /*
     @Override
     public PrintWriter getWriter() throws IOException
     {
         // will error out, if in use
         if (usingWriter) {
             super.getWriter();
         }
         usingWriter = true;
         return new PrintWriter(output);
     }
     */
}
