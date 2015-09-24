package com.davidm.enigma.EnigmaDesktop.impl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import javax.inject.Singleton;

import com.davidm.enigma.EnigmaDesktop.interfaces.AuthTokenProvider;
import com.google.common.io.Files;
import com.google.inject.Inject;

@Singleton
public class UserInputAuthTokenProvider implements AuthTokenProvider {
	private static final File DB = new File("key.txt");
	private String token;
	
	@Inject
	public UserInputAuthTokenProvider() {
		if(DB.exists()){
			try {
				token = Files.readFirstLine(DB, StandardCharsets.UTF_8);
				return;
			} catch (IOException e) {
				
			}
		}
		Scanner keyboard = new Scanner(System.in);
		System.out.println("Please enter a Slack Token. To generate one, go to https://api.slack.com/web?sudo=1");
		token = keyboard.nextLine();
		try {
			Files.write(token, DB, StandardCharsets.UTF_8);
		} catch (IOException e) {
		}
	}

	@Override
	public String getToken(){
		return token;
	}
}
