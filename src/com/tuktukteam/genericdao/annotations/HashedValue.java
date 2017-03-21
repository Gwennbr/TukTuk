package com.tuktukteam.genericdao.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
public @interface HashedValue 
{
	public int strength() default 10;
//	public int saltSize() default 32;
//	public int iterations() default 10;
}
