package it.marco.digrigoli.services.interfaces;

import java.util.Optional;

import it.marco.digrigoli.entities.CoursePost;

public interface ICoursePostService {
	
	public CoursePost create(CoursePost post);
	
	public void delete(CoursePost post);
	
	public Optional<CoursePost> getById(Long id);

}
