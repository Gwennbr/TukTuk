package com.tuktukteam.autosecurity;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(METHOD)
public @interface RestrictedAccess 
{
	AccessType value() default AccessType.CLASS_IN_SESSION;
	Class<?> [] authorized() default {};
	String [] attributesNames() default {};
	String onForbidden() default "status:403";
	AccessOptions [] options() default {};
	
	enum AccessType { NONE, PUBLIC, CLASS_IN_SESSION, TOKEN };	
	enum AccessOptions { ASSIGN_TOKEN_ON_SUCCESS };
}
