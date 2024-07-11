package it.marco.digrigoli.entities.dto;

import it.marco.digrigoli.entities.Role.RoleType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleCreateRequestDTO {

	private RoleType type;
	
}
