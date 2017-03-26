package com.tuktukteam.tools;

import java.security.SecureRandom;

public class Random
{
	private static final int LETTERS_COUNT = 26;
	private static final int DIGITS_COUNT = 10;
	private static final int TOTAL_VALUES = LETTERS_COUNT * 2 + DIGITS_COUNT;
	
	public static String nextString(int size)
	{
		char str[] = new char[size];
		SecureRandom rand = new SecureRandom();
		
		for (int i = 0 ; i < size ; ++i)
		{
			int val = rand.nextInt(TOTAL_VALUES); 
			if (val < LETTERS_COUNT)
				str[i] = (char)('a' + val);
			else
			{
				val -= LETTERS_COUNT;
				if (val < LETTERS_COUNT)
					str[i] = (char)('A' + val);
				else
				{
					val -= LETTERS_COUNT;
					str[i] = (char)('0' + val);
				}
			}
		}
		
		return new String(str);
	}
	
	
}
