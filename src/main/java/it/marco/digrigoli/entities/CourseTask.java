package it.marco.digrigoli.entities;

import java.time.Instant;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "coursetask")
public class CourseTask {

	@Id
	@GeneratedValue
	private Long id;
	@NotNull
	private String title;
	@NotNull
	private String content;
	@NotNull
	private Instant createdAt;
	private Instant expiration;
	private List<String> attachmentFileIds;
	@ManyToOne(fetch = FetchType.EAGER)
	@NotNull
	private Course course;
	@ManyToOne(fetch = FetchType.EAGER)
	@NotNull
	private User user;
	
	public Long getCourseId() {
		return course.getId();
	}
	
	public Long getUserId() {
		return user.getId();
	}
	
}
