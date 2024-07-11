package it.marco.digrigoli.services;


import java.util.Optional;

import org.springframework.stereotype.Service;

import it.marco.digrigoli.entities.Category;
import it.marco.digrigoli.repositories.CategoryRepository;
import it.marco.digrigoli.services.interfaces.ICategoryService;

@Service
public class CategoryServiceImpl implements ICategoryService {

	private CategoryRepository repo;
	
	public CategoryServiceImpl(CategoryRepository repo) {
		this.repo = repo;
	}
	
	@Override
	public void save(Category category) {
		this.repo.save(category);
	}
	
	@Override
	public Optional<Category> getById(Long id) {
		return repo.findById(id);
	}
	
	@Override
	public Iterable<Category> getAll() {
		return repo.findAll();
	}

}
