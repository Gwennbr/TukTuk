package com.tuktukteam.genericdao.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
public @interface ColumnTag 
{
	String[] value();
	
	public static final String FRONT_RESTRICTED = "Front_Restricted";
	public static final String FRONT_PUBLIC = "Front_Public";
}
