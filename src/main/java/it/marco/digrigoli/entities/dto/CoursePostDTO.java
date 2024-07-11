package it.marco.digrigoli.entities.dto;

import java.time.Instant;
import java.util.List;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoursePostDTO {

	@Id
	@GeneratedValue
	private Long id;
	private Long userId;
	private Instant createdAt;
	private String content;
	private List<String> attachmentFileIds;

}
