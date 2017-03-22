package com.tuktukteam.tuktuk.dao;

import com.tuktukteam.genericdao.GenericDAO;
import com.tuktukteam.tuktuk.model.Client;

public class ClientDAO extends GenericDAO<Client, Integer> {

	public ClientDAO() {
		super(Client.class);
	}
}
