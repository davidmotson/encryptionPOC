package com.davidm.enigma.EnigmaDesktop.impl;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import com.davidm.enigma.EnigmaDesktop.domain.User;
import com.davidm.enigma.EnigmaDesktop.interfaces.AuthTokenProvider;
import com.davidm.enigma.EnigmaDesktop.interfaces.Conversation;
import com.davidm.enigma.EnigmaDesktop.interfaces.ConversationProvider;
import com.davidm.enigma.EnigmaDesktop.interfaces.EncryptionManager;
import com.davidm.enigma.EnigmaDesktop.interfaces.KeyManager;
import com.davidm.enigma.EnigmaDesktop.interfaces.Service;
import com.davidm.enigma.EnigmaDesktop.interfaces.UserDAO;
import com.google.inject.Inject;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;

@Singleton
public class EncryptedSlackConversationProvider implements ConversationProvider, Service{
	private final SlackSession session;
	private final UserDAO userDao;
	private final KeyManager keyManager;
	private final EncryptionManager encryptionManager;
	private final AuthTokenProvider authProvider;
	
	@Inject
	public EncryptedSlackConversationProvider(AuthTokenProvider authProvider, UserDAO userDao, KeyManager keyManager, EncryptionManager encryptionManager) throws IOException {
		this.authProvider = authProvider;
		this.encryptionManager = encryptionManager;
		this.keyManager = keyManager;
		this.userDao = userDao;
		session = SlackSessionFactory.createWebSocketSlackSession(authProvider.getToken());
	}

	@Override
	public Conversation getConversation(User with) {
		return new EncryptedSlackConversation(session, with, encryptionManager, authProvider);
	}

	@Override
	public void addFriend(User user, long seed, boolean even) {
		keyManager.generateKey(user, seed, even);
		userDao.addUser(user);
	}

	@Override
	public List<User> getPossibleFriends() {
		return session.getUsers().stream()
				.filter(user -> !user.isBot())
				.filter(user -> user.getRealName() != null)
				.map(user -> new User(user.getId(), user.getRealName()))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<User> getFriends() {
		return userDao.getFriends().stream().collect(Collectors.toList());
	}

	@Override
	public void init() {
		try {
			session.connect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void shutdown() {
		try {
			session.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
