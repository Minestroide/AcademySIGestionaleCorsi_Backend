package it.marco.digrigoli.entities.dto;

import java.util.List;

import it.marco.digrigoli.entities.Category.CategoryName;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDTO {
	
	@Id
	@GeneratedValue
	private long id;
	private CategoryName name;
	private List<Long> courseIds;

}
