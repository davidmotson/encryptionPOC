package com.davidm.enigma.EnigmaDesktop.domain;

import java.io.Serializable;

import lombok.Data;

@Data
public class Key implements Serializable{
	private static final long serialVersionUID = 2524803476013130964L;
	
	
	boolean used;
	final int keyRef;
	final byte[] key;
}
