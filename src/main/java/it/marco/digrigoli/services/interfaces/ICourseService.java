package it.marco.digrigoli.services.interfaces;

import java.util.List;
import java.util.Optional;

import it.marco.digrigoli.entities.Category;
import it.marco.digrigoli.entities.Course;

public interface ICourseService {
	
	public Course save(Course course);
	
	public Optional<Course> getById(Long id);
	
	public List<Course> getAll();
	
	public List<Course> searchByNameAndCategoryId(String name, Category category);
	
	public List<Course> searchByName(String name);
	
	public void delete(Course course);
	
}
