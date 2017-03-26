package com.tuktukteam.autosecurity;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class ResponseWrapper extends HttpServletResponseWrapper
{

	public ResponseWrapper(HttpServletResponse response) { super(response);	output = new CharArrayWriter(); usingWriter = false;}

	private CharArrayWriter output;
	private boolean usingWriter;
	
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
         // will error out, if in use
         if (usingWriter) {
             super.getWriter();
         }
         usingWriter = true;
         return new PrintWriter(output);
     }
}
