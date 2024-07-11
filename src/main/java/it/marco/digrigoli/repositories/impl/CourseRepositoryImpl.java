package it.marco.digrigoli.repositories.impl;

import java.util.List;
import java.util.Optional;

import it.marco.digrigoli.entities.Category;
import it.marco.digrigoli.entities.Course;
import it.marco.digrigoli.repositories.CourseRepository;
import it.marco.digrigoli.repositories.CustomizedCourseRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class CourseRepositoryImpl implements CustomizedCourseRepository {

	@PersistenceContext
	private EntityManager entityManager;
	
	public List<Course> findByNameAndCategoryId(String name, Category category) {
		String sql = "SELECT c FROM Course c WHERE c.name LIKE :name AND c.category = :category";
		return (List<Course>) entityManager.createQuery(sql)
				.setParameter("name", name)
				.setParameter("category", category).getResultList();
	}

}
