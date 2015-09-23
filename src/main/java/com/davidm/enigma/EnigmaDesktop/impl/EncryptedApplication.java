package com.davidm.enigma.EnigmaDesktop.impl;

import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import com.davidm.enigma.EnigmaDesktop.domain.User;
import com.davidm.enigma.EnigmaDesktop.interfaces.Application;
import com.davidm.enigma.EnigmaDesktop.interfaces.Conversation;
import com.davidm.enigma.EnigmaDesktop.interfaces.ConversationProvider;
import com.davidm.enigma.EnigmaDesktop.interfaces.Service;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

@Singleton
public class EncryptedApplication implements Application {
	private static final Set<String> AFFIRMATIVES = ImmutableSet.of("yes", "y",
			"ya");
	private final Set<Service> services;
	private final ConversationProvider conversationProvider;

	@Inject
	public EncryptedApplication(Set<Service> services,
			ConversationProvider conversationProvider) {
		this.services = services;
		this.conversationProvider = conversationProvider;
	}

	@Override
	public void run() {
		services.forEach(x -> x.init());
		Scanner keyboard = new Scanner(System.in);
		System.out.println("Loading complete, welcome to encrypted chat");
		System.out.println("Type 'help' to get started");
		boolean running = true;
		while (running) {
			switch (keyboard.nextLine().trim()) {
			case "help":
				System.out.println("Type 'exit' to exit\nType 'add' to add a friend\nType 'talk' to talk to a friend");
				break;
			case "exit":
				running = false;
				break;
			case "add":
				System.out.println("Enter the friend's gmail address");
				String address = keyboard.nextLine();
				System.out.println("Enter a name");
				String alias = keyboard.nextLine();
				User newFriend = new User(address, alias);
				System.out.println("Enter a seed (numeric)");
				long seed = keyboard.nextLong();
				keyboard.nextLine();
				System.out.println("Even? (Y/N)");
				boolean even = AFFIRMATIVES.contains(keyboard.nextLine()
						.toLowerCase(Locale.US));
				conversationProvider.addFriend(newFriend, seed, even);
				System.out.println("Friend added, type 'talk' to talk to them");
				break;
			case "talk":
				System.out.println("Who would you like to talk to? Type a number.");
				List<User> friends = conversationProvider.getFriends().stream().collect(Collectors.toList());
				for (int i = 0; i < friends.size(); i++) {
					User friend = friends.get(i);
					System.out.println(i + ". " + friend.getAlias() + " (" + friend.getAddress() + ")");
				}
				User friend = friends.get(keyboard.nextInt());
				keyboard.nextLine();
				Conversation conversation = conversationProvider.getConversation(friend);
				conversation.getPastMessages().stream().map(x -> x.getFrom().getAlias() + ": " + x.getMessage()).forEach(System.out::println);
				conversation.addMessageListener(message -> System.out.println(friend.getAlias() + ": " + message));
				System.out.println("Now talking to " + friend.getAlias() + ". Type exit to stop, or a message to send");
				boolean talking = true;
				while(talking){
					String input = keyboard.nextLine();
					if(input.trim().equalsIgnoreCase("exit")){
						talking = false;
						conversation.clearMessageListeners();
						continue;
					}
					conversation.sendMessage(input);
				}
				break;
			}
		}

		keyboard.close();
		services.forEach(x -> x.shutdown());
	}
}
