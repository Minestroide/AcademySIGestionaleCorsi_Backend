package it.marco.digrigoli.entities.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationDTO {
	
	private String username;
	private String email;
	private String name;
	private String surname;
	private String password;

}
