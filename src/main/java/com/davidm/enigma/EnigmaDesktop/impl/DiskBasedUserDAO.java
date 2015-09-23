package com.davidm.enigma.EnigmaDesktop.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Singleton;

import com.davidm.enigma.EnigmaDesktop.domain.User;
import com.davidm.enigma.EnigmaDesktop.interfaces.Service;
import com.davidm.enigma.EnigmaDesktop.interfaces.UserDAO;

@Singleton
public class DiskBasedUserDAO implements UserDAO, Service {
	private static final File DB = new File("users.db");
	private Set<User> users = new HashSet<User>();

	@SuppressWarnings("unchecked")
	@Override
	public void init() {
		if(DB.exists()){
			try {
				ObjectInputStream object = new ObjectInputStream(new FileInputStream(DB));
				users = (HashSet<User>) object.readObject();
				object.close();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
			
		}
	}

	@Override
	public void shutdown() {
		DB.delete();
		try {
			ObjectOutputStream object = new ObjectOutputStream(new FileOutputStream(DB));
			object.writeObject(users);
			object.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public Set<User> getFriends() {
		return users;
	}

	@Override
	public void addUser(User in) {
		users.add(in);
	}

}
