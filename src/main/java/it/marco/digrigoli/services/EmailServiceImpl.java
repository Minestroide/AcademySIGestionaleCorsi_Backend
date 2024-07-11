package it.marco.digrigoli.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import it.marco.digrigoli.services.interfaces.IEmailService;

@Component
public class EmailServiceImpl implements IEmailService {
	
	private JavaMailSender sender;
	
	public EmailServiceImpl(JavaMailSender sender) {
		this.sender = sender;
	}
	
	@Value("${spring.mail.username}")
	private String from;

	public void sendSimpleMessage(String to, String subject, String text) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(from);
		message.setTo(to);
		message.setSubject(subject);
		message.setText(text);
		sender.send(message);
	}

}
