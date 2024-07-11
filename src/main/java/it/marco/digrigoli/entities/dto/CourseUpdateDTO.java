package it.marco.digrigoli.entities.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseUpdateDTO {
	
	private String name;
	private String description;
	private int maxParticipants;

}
