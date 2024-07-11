package it.marco.digrigoli.repositories;

import java.util.List;

import it.marco.digrigoli.entities.Category;
import it.marco.digrigoli.entities.Course;

public interface CustomizedCourseRepository {
	
	public Iterable<Course> findByNameAndCategoryId(String name, Category category);

}
