package it.marco.digrigoli.entities.dto;

import java.util.List;

import it.marco.digrigoli.entities.Role.RoleType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleDTO {
	
	@Id
	@GeneratedValue
	private long id;
	private List<String> userIds;
	private RoleType type;
	
}
