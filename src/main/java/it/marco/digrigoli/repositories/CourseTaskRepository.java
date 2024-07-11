package it.marco.digrigoli.repositories;

import org.springframework.data.repository.CrudRepository;

import it.marco.digrigoli.entities.Course;
import it.marco.digrigoli.entities.CoursePost;
import it.marco.digrigoli.entities.CourseTask;

public interface CourseTaskRepository extends CrudRepository<CourseTask, Long> {

}
