package com.davidm.enigma.EnigmaDesktop.impl;

import java.io.IOException;
import java.util.Set;

import javax.inject.Singleton;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import com.davidm.enigma.EnigmaDesktop.domain.User;
import com.davidm.enigma.EnigmaDesktop.interfaces.Conversation;
import com.davidm.enigma.EnigmaDesktop.interfaces.ConversationProvider;
import com.davidm.enigma.EnigmaDesktop.interfaces.EncryptionManager;
import com.davidm.enigma.EnigmaDesktop.interfaces.KeyManager;
import com.davidm.enigma.EnigmaDesktop.interfaces.LoginProvider;
import com.davidm.enigma.EnigmaDesktop.interfaces.MessageDAO;
import com.davidm.enigma.EnigmaDesktop.interfaces.UserDAO;
import com.google.inject.Inject;

@Singleton
public class GTalkConversationProvider implements ConversationProvider {
	private final XMPPTCPConnection gTalk;
	private final ChatManager chatManager;
	private final UserDAO userDao;
	private final EncryptionManager encryptionManager;
	private final KeyManager keyManager;
	private final MessageDAO messageDao;

	@Inject
	public GTalkConversationProvider(LoginProvider loginProvider, UserDAO userDao, EncryptionManager encryptionManager, MessageDAO messageDao, KeyManager keyManager) throws XMPPException, SmackException, IOException {
		gTalk = new XMPPTCPConnection(XMPPTCPConnectionConfiguration.builder()
				.setHost("talk.google.com").setPort(5222)
				.setServiceName("gmail.com").build());
		chatManager = ChatManager.getInstanceFor(gTalk);
		this.userDao = userDao;
		this.messageDao = messageDao;
		this.encryptionManager = encryptionManager;
		this.keyManager = keyManager;
		gTalk.connect();
		gTalk.login(loginProvider.getUsername(), loginProvider.getPassword());

	}


	@Override
	public Conversation getConversation(User with) {
		return new EncryptedConversation(with, chatManager.createChat(with.getAddress()), encryptionManager, messageDao, chatManager);
		
	}

	@Override
	public void addFriend(User user, long seed, boolean even) {
		keyManager.generateKey(user, seed, even);
		userDao.addUser(user);
	}

	@Override
	public Set<User> getFriends() {
		return userDao.getFriends();
	}

}
