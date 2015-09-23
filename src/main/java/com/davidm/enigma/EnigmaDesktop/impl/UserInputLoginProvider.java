package com.davidm.enigma.EnigmaDesktop.impl;

import java.util.Scanner;

import javax.inject.Singleton;

import com.davidm.enigma.EnigmaDesktop.interfaces.LoginProvider;
import com.google.inject.Inject;

@Singleton
public class UserInputLoginProvider implements LoginProvider {
	private String username;
	private String password;
	
	@Inject
	public UserInputLoginProvider() {
		@SuppressWarnings("resource")
		Scanner keyboard = new Scanner(System.in);
		System.out.print("Please enter your username: ");
		username = keyboard.nextLine();
		System.out.print("Please enter your password: ");
		password = keyboard.nextLine();
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String getPassword() {
		return password;
	}

}
