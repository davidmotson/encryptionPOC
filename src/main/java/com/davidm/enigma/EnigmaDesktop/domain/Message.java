package com.davidm.enigma.EnigmaDesktop.domain;

import java.io.Serializable;

import lombok.Value;

@Value
public class Message implements Serializable{
	private static final long serialVersionUID = 2418076037264581208L;
	
	
	String message;
	User from;
}
