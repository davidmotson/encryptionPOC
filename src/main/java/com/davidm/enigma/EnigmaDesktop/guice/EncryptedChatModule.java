package com.davidm.enigma.EnigmaDesktop.guice;

import com.davidm.enigma.EnigmaDesktop.impl.DiskBasedKeyManager;
import com.davidm.enigma.EnigmaDesktop.impl.DiskBasedMessageDao;
import com.davidm.enigma.EnigmaDesktop.impl.DiskBasedUserDAO;
import com.davidm.enigma.EnigmaDesktop.impl.EncryptedApplication;
import com.davidm.enigma.EnigmaDesktop.impl.GTalkConversationProvider;
import com.davidm.enigma.EnigmaDesktop.impl.PadEncryptionManager;
import com.davidm.enigma.EnigmaDesktop.impl.UserInputLoginProvider;
import com.davidm.enigma.EnigmaDesktop.interfaces.Application;
import com.davidm.enigma.EnigmaDesktop.interfaces.ConversationProvider;
import com.davidm.enigma.EnigmaDesktop.interfaces.EncryptionManager;
import com.davidm.enigma.EnigmaDesktop.interfaces.KeyManager;
import com.davidm.enigma.EnigmaDesktop.interfaces.LoginProvider;
import com.davidm.enigma.EnigmaDesktop.interfaces.MessageDAO;
import com.davidm.enigma.EnigmaDesktop.interfaces.Service;
import com.davidm.enigma.EnigmaDesktop.interfaces.UserDAO;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

public class EncryptedChatModule extends AbstractModule{
	
	public void configure(){
		bind(KeyManager.class).to(DiskBasedKeyManager.class);
		bind(MessageDAO.class).to(DiskBasedMessageDao.class);
		bind(UserDAO.class).to(DiskBasedUserDAO.class);
		bind(ConversationProvider.class).to(GTalkConversationProvider.class);
		bind(EncryptionManager.class).to(PadEncryptionManager.class);
		bind(LoginProvider.class).to(UserInputLoginProvider.class);
		bind(Application.class).to(EncryptedApplication.class);
		
		Multibinder<Service> allServices = Multibinder.newSetBinder(binder(), Service.class);
		allServices.addBinding().to(DiskBasedKeyManager.class);
		allServices.addBinding().to(DiskBasedMessageDao.class);
		allServices.addBinding().to(DiskBasedUserDAO.class);
	}

}
