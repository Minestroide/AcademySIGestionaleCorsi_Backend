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
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name="role")
@ToString
public class Role {

	public enum RoleType {
		STUDENT,
		TEACHER,
		ADMIN
	}
	
	@Id
	@GeneratedValue
	private long id;
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name="role_id"))
	private List<User> users;
	@Enumerated(EnumType.STRING)
	private RoleType type;
	
	public List<Long> getUserIds() {
		return users.stream().map((user) -> {
			return user.getId();
		}).toList();
	}
	
}
