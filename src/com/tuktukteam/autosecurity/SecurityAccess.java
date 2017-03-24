package com.tuktukteam.autosecurity;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(METHOD)
public @interface SecurityAccess 
{
	public AccessType value() default AccessType.PUBLIC;
	public Class<?> [] attributeClass();
	public String [] attributeNames();

	public enum AccessType { PUBLIC, CLASS_IN_SESSION };	
}
