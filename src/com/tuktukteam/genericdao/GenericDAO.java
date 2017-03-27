package com.tuktukteam.genericdao;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Transient;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.tuktukteam.genericdao.annotations.ColumnTag;
import com.tuktukteam.genericdao.annotations.FindByValues;
import com.tuktukteam.genericdao.annotations.HashedValue;


@Transactional
public abstract class GenericDAO<T, K>
{
	@PersistenceContext
	protected EntityManager entityManager;
	protected Class<T> type;
	
	public GenericDAO(Class<T> type) { this.type = type; }
	
	private static String getColumnName(Field field) { return field.getName(); }
	protected String getTableName() { return type.getSimpleName(); }
	
	public T findByValues(T _t) throws DAOException
	{
		StringBuilder myQueryString = null;
		List<Field> fieldsToCheck = new ArrayList<>();
		
		for (Class<?> t : getClassAndSuperClasses(_t.getClass()))
		{
			for (Field field : t.getDeclaredFields())
			{
				if (field.getDeclaredAnnotation(FindByValues.class) != null)
					try
					{
						field.setAccessible(true);
						String columnName = getColumnName(field);
						if (field.getDeclaredAnnotation(HashedValue.class) != null)
							fieldsToCheck.add(field);
						else
							if (myQueryString == null)
								myQueryString = new StringBuilder(String.format("FROM %s WHERE %s='%s'", 
										getTableName(),
										columnName,
										field.get(_t).toString()));
							else
								myQueryString.append(String.format(" AND %s='%s'", columnName, field.get(_t).toString()));
					}
					catch (IllegalArgumentException | IllegalAccessException e)
					{
						throw new DAOException("error while getting class infos", e);
					}
			}
		}
		if (myQueryString == null)
			throw new DAOException("no @FindByValues annotation in class");
		
		try
		{
			T returnObject = entityManager.createQuery(myQueryString.toString(), type).getSingleResult();
			boolean bAllChecksPassed = true;
			for (Field field : fieldsToCheck)
			{
				field.setAccessible(true); // TODO remove, useless
				if (!matchesHashedValue(returnObject, field.getName(), (String)field.get(_t)))
				{
					bAllChecksPassed = false;
					break;
				}
			}
			if (bAllChecksPassed)
				return returnObject;
			else
				return null;
		}
		catch (Exception e)
		{
			return null;
		}
	}
	public List<T> getAll()
	{
		return entityManager.createQuery(String.format("FROM %s", getTableName()), type).getResultList();
	}
	
	public T save(T t)
	{
		return entityManager.merge(t);
	}
	
	public T hashFieldsAndSave(T t)
	{
		hashAnnotedFields(t);
		return save(t);
	}
	
	public List<T> saveAll(List<T> listOfT)
	{
		if (listOfT == null)
			return null;
		List<T> listOfManagedT = new ArrayList<>(listOfT.size());
		for (T t : listOfT)
			listOfManagedT.add(save(t));
		return listOfManagedT;
	}
	
	public void deleteAll(List<T> listOfT)
	{
		if (listOfT == null)
			return;
		for (T t : listOfT)
			delete(t);
	}
	
	public void delete(T t)
	{
		if (t != null)
			entityManager.remove(entityManager.merge(t));
	}
	
	public T find(K id)
	{
		try
		{
			return entityManager.find(type, id);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public T getAndFillOnlyFieldsWithTags(K id, List<String> includeTags, List<String> excludeTags)
	{
		T persistenceInstance = find(id);
		if (persistenceInstance == null)
			return null;
		
		List<Field> fieldsToReturn = new ArrayList<>();
		
		for (Class<?> typ : getClassAndSuperClasses(type))
			for (Field field : typ.getDeclaredFields())
				if (!field.isAnnotationPresent(Transient.class) && !Modifier.isStatic(field.getModifiers()))
				{
					ColumnTag tags = field.getDeclaredAnnotation(ColumnTag.class);
					if (excludeTags != null && tags != null)
					{
						boolean bContinueToNextField = false;
						for (String tag : tags.value())
							if (excludeTags != null && excludeTags.contains(tag))
							{
								bContinueToNextField = true;
								break;
							}	
						if (bContinueToNextField)
							continue;
					}
					
					if (includeTags == null || includeTags.size() == 0)
						fieldsToReturn.add(field);
					else
						if (tags != null)
							for (String tag : tags.value())
								if (includeTags.contains(tag))
								{
									fieldsToReturn.add(field);
									break;
								}
				}
	
		T returnInstance;
		try
		{
			returnInstance = type.newInstance();
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			return null;
		}
		
		for (Field field : fieldsToReturn)
		{
			field.setAccessible(true);
			try
			{
				field.set(returnInstance, field.get(persistenceInstance));
			}
			catch (IllegalArgumentException | IllegalAccessException e)
			{
				return null;
			}
		}
		return returnInstance;
		
	}

	public T getAndFillOnlyFieldsWithTags(K id, String includeTags[], String excludeTags[])
	{
		return getAndFillOnlyFieldsWithTags(id, 
				includeTags == null ? null : Arrays.asList(includeTags), 
				excludeTags == null ? null : Arrays.asList(excludeTags));
	}
	
	public T getAndFillOnlyFieldsTaggedBy(K id, String...includeTags)
	{
		return getAndFillOnlyFieldsWithTags(id, Arrays.asList(includeTags), null);
	}
	
	public T getAndFillOnlyFieldsNotTaggedBy(K id, String...excludeTags)
	{
		return getAndFillOnlyFieldsWithTags(id, null, Arrays.asList(excludeTags));
	}
	
	public List<Class<?>> getClassAndSuperClasses(Class<?> type)
	{
		List<Class<?>> classes = new ArrayList<>();
		while (type != null)
		{
			classes.add(type);
			type = type.getSuperclass();
		}
		return classes;
	}
	
	public Field getDeclaredFieldTroughAllInheritance(T t, String fieldName)
	{
		Class<?> type = t.getClass();
		while (type != null)
			try
			{
				return type.getDeclaredField(fieldName);
			}
			catch (NoSuchFieldException | SecurityException e)
			{
				type = type.getSuperclass();
			} 
		return null;
	}
	
	public boolean matchesHashedValue(T t, String fieldName, String rawValue) throws NoSuchFieldException
	{
		Field field = getDeclaredFieldTroughAllInheritance(t, fieldName);
		field.setAccessible(true);
		
		HashedValue hv = field.getDeclaredAnnotation(HashedValue.class);
		if (hv == null)
			throw new NoSuchFieldException("no HashedValue annotation on field");
		
		BCryptPasswordEncoder enc = new BCryptPasswordEncoder(hv.strength());
		try
		{
			return enc.matches(rawValue, (String) field.get(t));
		}
		catch (IllegalArgumentException | IllegalAccessException e)
		{
			e.printStackTrace();
			return false;
		}
		
	}
/*
	private Object getColumnValue(T t, Field field)
	{
		field.setAccessible(true);
		HashedValue hv = field.getDeclaredAnnotation(HashedValue.class);
		if (hv == null)
			try
			{
				return field.get(t);
			}
			catch (IllegalArgumentException | IllegalAccessException e)
			{
				e.printStackTrace();
				return null;
			}
		else
			return encodeValue(t, field, hv.strength());
	}
	*/
	private String encodeValue(T t, Field field, int strength)
	{
		try
		{
			return new BCryptPasswordEncoder(strength).encode((String)field.get(t));
		}
		catch (IllegalArgumentException | IllegalAccessException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	private T hashAnnotedFields(T _t)
	{
		for (Class<?> t : getClassAndSuperClasses(_t.getClass()))
		{
			for (Field field : t.getDeclaredFields())
			{
				HashedValue hv = field.getDeclaredAnnotation(HashedValue.class);
				if (hv != null)
				{
					field.setAccessible(true);
					try
					{
						field.set(_t, encodeValue(_t, field, hv.strength()));
					}
					catch (IllegalArgumentException | IllegalAccessException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		return _t;
	}
	
}

/*
public T saveWithHashedValues(T t, HashMap<String, String> fieldsToHash) throws NoSuchFieldException
{
	for (Entry<String, String> entry : fieldsToHash.entrySet())
		setHashedValueOnEntity(t, entry.getKey(), entry.getValue());

	return save(t);
}

public T saveWithHashedValue(T t, String fieldName, String valueToHash) throws NoSuchFieldException
{
	return setHashedValueOnEntity(t, fieldName, valueToHash);
}
*/



/*
private T setHashedValueOnEntity(T t, String fieldName, String valueToHash) throws NoSuchFieldException
{
	return setHashedValueOnEntity(t, t.getClass().getDeclaredField(fieldName), valueToHash);
}
*/
