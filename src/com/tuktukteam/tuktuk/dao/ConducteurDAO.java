package com.tuktukteam.tuktuk.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tuktukteam.genericdao.GenericDAO;
import com.tuktukteam.tuktuk.model.Conducteur;

@Transactional
@Repository
public class ConducteurDAO extends GenericDAO<Conducteur, Integer> {

	public ConducteurDAO() {
		super(Conducteur.class);
	}

}
