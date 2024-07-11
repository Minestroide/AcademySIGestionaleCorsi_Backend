package it.marco.digrigoli.entities.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ContactCreateDTO {

	@Email
	@NotNull
	private String email;
	@NotNull
	private String content;
	
}
