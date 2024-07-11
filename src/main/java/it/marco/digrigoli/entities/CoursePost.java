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
@Table(name = "coursepost")
public class CoursePost {

	@Id
	@GeneratedValue
	private Long id;
	@NotNull
	private String content;
	@NotNull
	private Instant createdAt;
	private List<String> attachmentFileIds;
	@ManyToOne(fetch = FetchType.EAGER)
	@NotNull
	private Course course;
	@ManyToOne(fetch = FetchType.EAGER)
	@NotNull
	private User user;
	
	public Long getUserId() {
		return user.getId();
	}
	
	public Long getCourseId() {
		return course.getId();
	}
	
}
