package it.marco.digrigoli.entities.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserTwoFactorEnableDTO {
	
	private String secret;
	private String code;

}
