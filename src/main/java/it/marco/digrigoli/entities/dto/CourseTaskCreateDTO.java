package it.marco.digrigoli.entities.dto;

import java.time.Instant;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseTaskCreateDTO {
	
	private String content;
	private Instant expiration;
	@NotNull
	private String title;

}
