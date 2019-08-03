package com.emerfcouto.cursomc.services;

import org.springframework.mail.SimpleMailMessage;

import com.emerfcouto.cursomc.domain.Pedido;

public interface EmailService {

	void sendOrderConfirmationEmail(Pedido obj);
	
	void sendEmail(SimpleMailMessage msg);
}
