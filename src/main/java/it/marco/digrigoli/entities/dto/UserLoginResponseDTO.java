package it.marco.digrigoli.entities.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginResponseDTO {

	private String token;
	private LocalDateTime ttl;
	private LocalDateTime tokenCreationTime;

}
