package com.michelin.service.user;

public interface EmailService {

	void sendPasswordResetEmail(String toEmail, String token);
	
}
