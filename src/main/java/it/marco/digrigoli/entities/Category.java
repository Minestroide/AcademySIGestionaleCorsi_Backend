package it.marco.digrigoli.entities;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "category")
public class Category {

	public enum CategoryName {
		FRONTEND, BACKEND, FULLSTACK, CYBERSECURITY
	}

	@Id
	@GeneratedValue
	private Long id;
	@Enumerated(EnumType.STRING)
	@NotNull
	private CategoryName name;
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "category")
	private List<Course> courses;

	public List<Long> getCourseIds() {
		return courses.stream().map((course) -> {
			return course.getId();
		}).toList();
	}

}
