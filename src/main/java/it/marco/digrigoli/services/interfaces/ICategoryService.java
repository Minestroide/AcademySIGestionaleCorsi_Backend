package it.marco.digrigoli.services.interfaces;
import java.util.Optional;

import it.marco.digrigoli.entities.Category;
import it.marco.digrigoli.entities.dto.CategoryDTO;

public interface ICategoryService {
	
	public void save(Category category);
	
	public Optional<Category> getById(Long id);
	
	public Iterable<Category> getAll();
	
}
