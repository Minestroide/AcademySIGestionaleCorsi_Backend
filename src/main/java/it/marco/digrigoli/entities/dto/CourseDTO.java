package it.marco.digrigoli.entities.dto;

import java.util.List;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseDTO {

	@Id
	@GeneratedValue
	private long id;
	private String name;
	private Long categoryId;
	private int maxParticipants;
	private String description;
	private List<Long> userIds;

}
