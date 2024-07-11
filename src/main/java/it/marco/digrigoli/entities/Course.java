package it.marco.digrigoli.entities;

import java.util.List;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "course")
public class Course {

	@Id
	@GeneratedValue
	private Long id;
	@NotNull
	private String name;
	@NotNull
	private String description;
	@NotNull
	private String bannerFileId;
	private int maxParticipants;
	@ManyToOne(fetch = FetchType.EAGER)
	@NotNull
	private Category category;
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "course")
	@OrderBy("createdAt DESC")
	private List<CoursePost> posts;
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_courses", joinColumns = @JoinColumn(name = "course_id"), inverseJoinColumns = @JoinColumn(name="user_id"))
	private List<User> users;
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "course")
	@OrderBy("createdAt DESC")
	private List<CourseTask> tasks;
	private double price = 0;
	
	public Long getCategoryId() {
		return category.getId();
	}
	
	public List<Long> getPostIds() {
		return posts.stream().map((post) -> {
			return post.getId();
		}).toList();
	}
	
	public List<Long> getUserIds() {
		return users.stream().map((user) -> {
			return user.getId();
		}).toList();
	}
	
}
