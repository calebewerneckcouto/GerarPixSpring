package com.cwcdev.pix.exception;

public class AccessDeniedException extends RuntimeException {

	
	private static final long serialVersionUID = 1L;
	
	
	public AccessDeniedException(String msg) {
	super(msg);
	}

}
