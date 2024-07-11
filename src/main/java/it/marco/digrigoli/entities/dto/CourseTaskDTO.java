package it.marco.digrigoli.entities.dto;

import java.time.Instant;
import java.util.List;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseTaskDTO {

	@Id
	@GeneratedValue
	private Long id;
	private Long userId;
	private Instant createdAt;
	private Instant expiration;
	private String content;
	private String title;
	private List<String> attachmentFileIds;

}
