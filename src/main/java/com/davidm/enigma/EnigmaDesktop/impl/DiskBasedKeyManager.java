package com.davidm.enigma.EnigmaDesktop.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.inject.Singleton;

import com.davidm.enigma.EnigmaDesktop.domain.Key;
import com.davidm.enigma.EnigmaDesktop.domain.User;
import com.davidm.enigma.EnigmaDesktop.interfaces.KeyManager;
import com.davidm.enigma.EnigmaDesktop.interfaces.Service;

@Singleton
public class DiskBasedKeyManager implements KeyManager, Service{
	private static final File DB = new File("keys.db");
	private HashMap<User, ArrayList<Key>> keys = new HashMap<User, ArrayList<Key>>();
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void init() {
		if(DB.exists()){
			try {
				ObjectInputStream object = new ObjectInputStream(new FileInputStream(DB));
				keys = (HashMap<User, ArrayList<Key>>) object.readObject();
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
			object.writeObject(keys);
			object.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public Key getKeyFor(User toAddress) {
		Key key = keys.get(toAddress).stream().filter(x -> !x.isUsed()).findFirst().orElseThrow(() -> new RuntimeException("Out of keys for " + toAddress));
		key.setUsed(true);
		return key;
	}

	@Override
	public byte[] rawKeyByRef(int ref, User fromAddress) {
		return keys.get(fromAddress).get(ref).getKey();
	}

	@Override
	public void generateKey(User toAddress, long seed, boolean even) {
		Random generator = new Random(seed);
		ArrayList<Key> keys = new ArrayList<Key>(10_000);
		for(int i = 0; i < 10_000; i++){
			byte[] bytes = new byte[256];
			generator.nextBytes(bytes);
			keys.add(new Key(i, bytes));
		}
		keys.stream().filter(x -> x.getKeyRef() % 2 == (even ? 0 : 1))
				.forEach(x -> x.setUsed(true));
		this.keys.put(toAddress, keys);
	}

}
