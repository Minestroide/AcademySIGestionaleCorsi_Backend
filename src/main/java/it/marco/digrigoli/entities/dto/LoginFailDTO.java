package it.marco.digrigoli.entities.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginFailDTO {
	
	public enum FailType {
		TWO_FACTOR_CODE_NEEDED
	}
	
	private FailType type;

}
