package it.marco.digrigoli.entities.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoursePostCreateDTO {

	@NotNull
	private String content;

}
