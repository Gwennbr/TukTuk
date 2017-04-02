package com.tuktukteam.tuktuk.security;

import com.tuktukteam.autosecurity.ATSUserComparator;
import com.tuktukteam.tuktuk.model.Personne;

public class UserComparator implements ATSUserComparator<Personne>
{
	public UserComparator(Personne myUser)
	{
		this.myUser = myUser;
	}
	
	@Override
	public boolean sameUser(Personne anotherUser)
	{
		if (!anotherUser.getClass().equals(myUser.getClass()))
			return false;
		return myUser.getUsername().equalsIgnoreCase(anotherUser.getUsername());
	}
	
	private Personne myUser;
}
