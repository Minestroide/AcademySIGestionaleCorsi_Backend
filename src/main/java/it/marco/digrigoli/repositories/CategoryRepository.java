package it.marco.digrigoli.repositories;

import org.springframework.data.repository.CrudRepository;

import it.marco.digrigoli.entities.Category;

public interface CategoryRepository extends CrudRepository<Category, Long> {
	
}
