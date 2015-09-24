package com.davidm.enigma.EnigmaDesktop.guice;

import com.davidm.enigma.EnigmaDesktop.impl.DiskBasedKeyManager;
import com.davidm.enigma.EnigmaDesktop.impl.DiskBasedUserDAO;
import com.davidm.enigma.EnigmaDesktop.impl.EncryptedApplication;
import com.davidm.enigma.EnigmaDesktop.impl.EncryptedSlackConversationProvider;
import com.davidm.enigma.EnigmaDesktop.impl.PadEncryptionManager;
import com.davidm.enigma.EnigmaDesktop.impl.UserInputAuthTokenProvider;
import com.davidm.enigma.EnigmaDesktop.interfaces.Application;
import com.davidm.enigma.EnigmaDesktop.interfaces.ConversationProvider;
import com.davidm.enigma.EnigmaDesktop.interfaces.EncryptionManager;
import com.davidm.enigma.EnigmaDesktop.interfaces.KeyManager;
import com.davidm.enigma.EnigmaDesktop.interfaces.AuthTokenProvider;
import com.davidm.enigma.EnigmaDesktop.interfaces.Service;
import com.davidm.enigma.EnigmaDesktop.interfaces.UserDAO;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

public class EncryptedChatModule extends AbstractModule{
	
	public void configure(){
		bind(KeyManager.class).to(DiskBasedKeyManager.class);
		bind(UserDAO.class).to(DiskBasedUserDAO.class);
		bind(EncryptionManager.class).to(PadEncryptionManager.class);
		bind(AuthTokenProvider.class).to(UserInputAuthTokenProvider.class);
		bind(Application.class).to(EncryptedApplication.class);
		bind(ConversationProvider.class).to(EncryptedSlackConversationProvider.class);
		
		Multibinder<Service> allServices = Multibinder.newSetBinder(binder(), Service.class);
		allServices.addBinding().to(DiskBasedKeyManager.class);
		allServices.addBinding().to(DiskBasedUserDAO.class);
		allServices.addBinding().to(EncryptedSlackConversationProvider.class);
	}

}
