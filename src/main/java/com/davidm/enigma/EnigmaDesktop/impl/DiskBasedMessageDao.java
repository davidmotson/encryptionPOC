package com.davidm.enigma.EnigmaDesktop.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import javax.inject.Singleton;

import com.davidm.enigma.EnigmaDesktop.domain.Message;
import com.davidm.enigma.EnigmaDesktop.domain.User;
import com.davidm.enigma.EnigmaDesktop.interfaces.MessageDAO;
import com.davidm.enigma.EnigmaDesktop.interfaces.Service;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;

@Singleton
public class DiskBasedMessageDao implements Service, MessageDAO {
	private static final User SELF = new User(null, null);
	private static final File DB = new File("messages.db");
	private ListMultimap<User,Message> messages = MultimapBuilder.hashKeys().arrayListValues().build();

	@Override
	public List<Message> getAllMessagesFor(User with) {
		return messages.get(with);
	}

	@Override
	public void addMessage(String message, User with, boolean sent) {
		messages.put(with, new Message(message, sent ? SELF : with));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init() {
		if(DB.exists()){
			try {
				ObjectInputStream object = new ObjectInputStream(new FileInputStream(DB));
				messages = (ListMultimap<User,Message>) object.readObject();
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
			object.writeObject(messages);
			object.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
