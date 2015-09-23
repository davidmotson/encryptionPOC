package com.davidm.enigma.EnigmaDesktop.domain;

import java.io.Serializable;
import java.util.Optional;

import lombok.Value;

@Value
public class User implements Serializable{
	private static final long serialVersionUID = 9010705367566533396L;
	
	
	String address;
	String alias;
	
	public String getAlias(){
		return Optional.ofNullable(alias).orElse("YOU");
	}
}
