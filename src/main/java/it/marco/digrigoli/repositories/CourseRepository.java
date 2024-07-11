package it.marco.digrigoli.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import it.marco.digrigoli.entities.Category;
import it.marco.digrigoli.entities.Course;

public interface CourseRepository extends CrudRepository<Course, Long>, CustomizedCourseRepository {
	
	public List<Course> findByNameAndCategory(String name, Category category);
	
	public List<Course> findByName(String name);
	
}
