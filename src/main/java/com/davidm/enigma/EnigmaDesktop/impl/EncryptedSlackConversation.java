package com.davidm.enigma.EnigmaDesktop.impl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.davidm.enigma.EnigmaDesktop.domain.Message;
import com.davidm.enigma.EnigmaDesktop.domain.User;
import com.davidm.enigma.EnigmaDesktop.interfaces.AuthTokenProvider;
import com.davidm.enigma.EnigmaDesktop.interfaces.Conversation;
import com.davidm.enigma.EnigmaDesktop.interfaces.EncryptionManager;
import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;

public class EncryptedSlackConversation implements Conversation {
	private static final User YOU = new User(null, null);
	private final User user;
	private final SlackChannel userChannel;
	private final SlackSession session;
	private final EncryptionManager encryptionManager;
	private final List<Consumer<String>> messageConsumers;
	private final AuthTokenProvider tokenProvider;
	
	public EncryptedSlackConversation(SlackSession session, User user, EncryptionManager encryptionManager, AuthTokenProvider tokenProvider) {
		messageConsumers = new LinkedList<Consumer<String>>();
		this.tokenProvider = tokenProvider;
		this.encryptionManager = encryptionManager;
		this.user = user;
		this.userChannel = session.findChannelById(user.getAddress());
		this.session = session;
		session.addMessagePostedListener((event, eventSession) -> {
			if(event.getChannel().getId().equals(userChannel.getId())){
				if (event.getSender().getId().equals(user.getAddress())) {
					String message = encryptionManager.decrypt(event.getMessageContent(), user);
					messageConsumers.forEach(x -> x.accept(message));
				}
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
		messageConsumers.clear();
	}

	@Override
	public void sendMessage(String message) {
		session.sendMessage(userChannel, encryptionManager.encrypt(message, user), null);
	}

	@Override
	public List<Message> getPastMessages() {
		try {
			String response = executePost("https://slack.com/api/im.history", "token=" + tokenProvider.getToken()+"&channel="+userChannel.getId());
			JSONObject json = (JSONObject) new JSONParser().parse(response);
			if((Boolean) json.get("ok")){
				LinkedList<Message> output = new LinkedList<Message>();
				JSONArray messages = ((JSONArray) json.get("messages"));
				for(Object obj : messages){
					JSONObject message = (JSONObject) obj;
					if("message".equals(message.get("type"))){
						User from = user.getAddress().equals(message.get("user")) ? user : YOU;
						String text = encryptionManager.decrypt(((String)message.get("text")), user);
						output.addFirst(new Message(text, from));
					}
				}
				return output;
			}else{
				return Collections.emptyList();
			}
		} catch (Exception e) {
			return Collections.emptyList();
		}
		
	}
	
	private static String executePost(String targetURL, String urlParameters) {
		  HttpURLConnection connection = null;  
		  try {
		    //Create connection
		    URL url = new URL(targetURL);
		    connection = (HttpURLConnection)url.openConnection();
		    connection.setRequestMethod("POST");
		    connection.setRequestProperty("Content-Type", 
		        "application/x-www-form-urlencoded");

		    connection.setRequestProperty("Content-Length", 
		        Integer.toString(urlParameters.getBytes().length));
		    connection.setRequestProperty("Content-Language", "en-US");  

		    connection.setUseCaches(false);
		    connection.setDoOutput(true);

		    //Send request
		    DataOutputStream wr = new DataOutputStream (
		        connection.getOutputStream());
		    wr.writeBytes(urlParameters);
		    wr.close();

		    //Get Response  
		    InputStream is = connection.getInputStream();
		    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		    StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+ 
		    String line;
		    while((line = rd.readLine()) != null) {
		      response.append(line);
		      response.append('\r');
		    }
		    rd.close();
		    return response.toString();
		  } catch (Exception e) {
		    e.printStackTrace();
		    return null;
		  } finally {
		    if(connection != null) {
		      connection.disconnect(); 
		    }
		  }
		}

}
