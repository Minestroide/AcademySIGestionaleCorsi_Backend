package it.marco.digrigoli.entities.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginRequestDTO {

	private String email;
	private String password;
	private String twoFactorCode;
	
}
