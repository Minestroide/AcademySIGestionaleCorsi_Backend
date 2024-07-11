package it.marco.digrigoli.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CourseResponseDTO {

	public enum CourseResponseType {
		COURSE_NOT_FOUND,
		SUCCESSFULLY_REMOVED,
		SUCCESSFULLY_ADDED,
		USER_NOT_IN_COURSE,
		USER_IN_COURSE,
		COURSE_FULL
	}
	
	private Long courseId;
	private CourseResponseType type;
	
}
