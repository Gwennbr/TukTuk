package com.tuktukteam.tuktuk.dao;

import com.tuktukteam.genericdao.GenericDAO;
import com.tuktukteam.tuktuk.model.Conducteur;

public class ConducteurDAO extends GenericDAO<Conducteur, Integer> {

	public ConducteurDAO() {
		super(Conducteur.class);
	}

}
