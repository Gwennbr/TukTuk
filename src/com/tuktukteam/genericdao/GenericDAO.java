package com.tuktukteam.genericdao;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

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

	public T findByValues(T t) throws DAOException
	{
		StringBuilder myQueryString = null;
		List<Field> fieldsToCheck = new ArrayList<>();
		
		for (Field field : t.getClass().getDeclaredFields())
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
									field.get(t).toString()));
						else
							myQueryString.append(String.format(" AND %s='%s'", columnName, field.get(t).toString()));
				}
				catch (IllegalArgumentException | IllegalAccessException e)
				{
					throw new DAOException("error while getting class infos", e);
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
				if (!matchesHashedValue(returnObject, field.getName(), (String)field.get(t)))
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
		String myQueryString = String.format("FROM %s", getTableName());
		
		return entityManager.createQuery(myQueryString, type).getResultList();
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
		return;
	}
	
	public void delete(T t)
	{
		if (t != null)
			entityManager.remove(entityManager.merge(t));
	}
	
	public T find(K id)
	{
		return entityManager.find(type, id);
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
	
	private T hashAnnotedFields(T t)
	{
		for (Field field : t.getClass().getDeclaredFields())
		{
			HashedValue hv = field.getDeclaredAnnotation(HashedValue.class);
			if (hv != null)
			{
				field.setAccessible(true);
				try
				{
					field.set(t, encodeValue(t, field, hv.strength()));
				}
				catch (IllegalArgumentException | IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
		}
		return t;
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
