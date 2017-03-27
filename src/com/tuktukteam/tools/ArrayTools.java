package com.tuktukteam.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArrayTools
{
	
	public static <T> List<T> intersectAndExclude(T sourceArray[], T includeValues[], T excludeValues[])
	{
		List<T> resultList = new ArrayList<>();
		
		if (sourceArray == null || sourceArray.length == 0)
			return resultList;
		
		if (includeValues != null)
		{
			if (includeValues.length == 0)
				return resultList;
			else
				for (T t : sourceArray)
					for (T t2 : includeValues)
						if (t.equals(t2))
							resultList.add(t);
		}
		else
			for (T t : sourceArray)
				resultList.add(t);

		if (excludeValues == null || excludeValues.length == 0)
			return resultList;
		
		resultList.removeAll(Arrays.asList(excludeValues));
		
		return resultList;					
		
	}
	
	public static <T> List<T> intersectAndExclude(List<T> sourceList, T includeValues[], T excludeValues[])
	{
		List<T> resultList = new ArrayList<>();
		
		if (sourceList == null || sourceList.size() == 0)
			return resultList;
		
		if (includeValues != null)
		{
			if (includeValues.length == 0)
				return resultList;
			else
				for (T t : sourceList)
					for (T t2 : includeValues)
						if (t.equals(t2))
							resultList.add(t);
		}
		else
			for (T t : sourceList)
				resultList.add(t);

		if (excludeValues == null || excludeValues.length == 0)
			return resultList;
		
		resultList.removeAll(Arrays.asList(excludeValues));
		
		return resultList;					
		
	}
}
