package com.tuktukteam.tuktuk.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tuktukteam.genericdao.GenericDAO;
import com.tuktukteam.tuktuk.model.Personne;

@Repository
@Transactional
public class PersonneDAO extends GenericDAO<Personne, Integer>
{
	public PersonneDAO() { super(Personne.class); }
}
