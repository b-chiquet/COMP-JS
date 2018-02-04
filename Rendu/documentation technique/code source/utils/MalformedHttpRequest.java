package org.xtext.example.utils;

@SuppressWarnings("serial")
public class MalformedHttpRequest extends Exception {
	public MalformedHttpRequest(String message){
		super(message);
	}
}
