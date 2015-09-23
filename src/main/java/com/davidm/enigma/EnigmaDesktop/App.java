package com.davidm.enigma.EnigmaDesktop;

import com.davidm.enigma.EnigmaDesktop.guice.EncryptedChatModule;
import com.davidm.enigma.EnigmaDesktop.interfaces.Application;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class App {
	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new EncryptedChatModule());
		injector.getInstance(Application.class).run();
	}
}
