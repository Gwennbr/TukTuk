package com.tuktukteam.tuktuk.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tuktukteam.genericdao.GenericDAO;
import com.tuktukteam.tuktuk.model.Client;

@Transactional
@Repository
public class ClientDAO extends GenericDAO<Client, Integer> {

	public ClientDAO() {
		super(Client.class);
	}
}
