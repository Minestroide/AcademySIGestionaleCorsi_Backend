package it.marco.digrigoli.entities.dto;

import java.util.List;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
	
	@Id
	@GeneratedValue
	private long id;
	@Pattern(regexp = "[a-zA-Z0-9]{1,50}", message = "username invalid")
	@NotNull
	private String username;
	@Email
	private String email;
	@Pattern(regexp = "[a-zA-Z\\\\èàùìò\s]{1,50}", message = "name invalid")
	private String name;
	@Pattern(regexp = "[a-zA-Z\\\\èàùìò\s]{1,50}", message = "surname invalid")
	private String surname;
	private List<String> roleIds;
	private List<String> courseIds;
	private boolean twoFactorEnabled;

}
