package it.marco.digrigoli.services.interfaces;

import java.util.List;
import java.util.Optional;

import it.marco.digrigoli.entities.CoursePost;
import it.marco.digrigoli.entities.CourseTask;

public interface ICourseTaskService {
	
	public CourseTask create(CourseTask task);
	
	public void delete(CourseTask task);
	
	public Optional<CourseTask> getById(Long id);
	
	public Iterable<CourseTask> getAll();

}
