package com.emerfcouto.cursomc.services;

import org.springframework.security.core.context.SecurityContextHolder;

import com.emerfcouto.cursomc.secutiry.UserSS;

public class UserService {

	public static UserSS authenticated() {
		
		try {
			
			return (UserSS) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			
		} catch (Exception e) {
			
			return null;
		}
	}
}
