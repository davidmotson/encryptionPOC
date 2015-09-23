package com.davidm.enigma.EnigmaDesktop.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message.Type;

import com.davidm.enigma.EnigmaDesktop.domain.Message;
import com.davidm.enigma.EnigmaDesktop.domain.User;
import com.davidm.enigma.EnigmaDesktop.interfaces.Conversation;
import com.davidm.enigma.EnigmaDesktop.interfaces.EncryptionManager;
import com.davidm.enigma.EnigmaDesktop.interfaces.MessageDAO;
import com.google.common.collect.ImmutableSet;

public class EncryptedConversation implements Conversation {
	private final Chat chat;
	private final User user;
	private final EncryptionManager encryptionManager;
	private final MessageDAO messageDao;
	private final List<Consumer<String>> messageConsumers;
	
	public EncryptedConversation(User user, Chat chat, EncryptionManager encryptionManager, MessageDAO messageDao, ChatManager chatManager) {
		this.messageDao = messageDao;
		this.messageConsumers = new LinkedList<Consumer<String>>();
		this.user = user;
		this.chat = chat;
		this.encryptionManager = encryptionManager;
		messageConsumers.add(message -> messageDao.addMessage(message, user, false));
		ChatMessageListener listener = new ChatMessageListener() {
			
			@Override
			public void processMessage(Chat chat, org.jivesoftware.smack.packet.Message message) {
				if(message.getType() == Type.chat && message.getBody() != null){
					String decrypted = encryptionManager.decrypt(message.getBody(), user);
					messageConsumers.forEach(x -> x.accept(decrypted));
				}
			}
		};
		chat.addMessageListener(listener);
		chatManager.addChatListener(new ChatManagerListener() {
			
			@Override
			public void chatCreated(Chat chat, boolean createdLocally) {
				chat.addMessageListener(listener);
			}
		});
	}

	@Override
	public User getUser() {
		return user;
	}

	@Override
	public void addMessageListener(Consumer<String> listener) {
		messageConsumers.add(listener);
	}
	
	@Override
	public void clearMessageListeners() {
		messageConsumers.retainAll(ImmutableSet.of(messageConsumers.get(0)));
	}

	@Override
	public void sendMessage(String message) {
		try {
			chat.sendMessage(encryptionManager.encrypt(message, user));
			messageDao.addMessage(message, user, true);
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Message> getPastMessages() {
		return messageDao.getAllMessagesFor(user);
	}
}
