package it.marco.digrigoli.repositories;

import org.springframework.data.repository.CrudRepository;

import it.marco.digrigoli.entities.Course;
import it.marco.digrigoli.entities.CoursePost;

public interface CoursePostRepository extends CrudRepository<CoursePost, Long> {

}
