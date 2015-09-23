package com.davidm.enigma.EnigmaDesktop.interfaces;

import java.util.Set;

import com.davidm.enigma.EnigmaDesktop.domain.User;

public interface UserDAO {
	public Set<User> getFriends();
	public void addUser(User in);
}
