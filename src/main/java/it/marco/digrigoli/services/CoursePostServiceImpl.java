package it.marco.digrigoli.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import it.marco.digrigoli.entities.CoursePost;
import it.marco.digrigoli.repositories.CoursePostRepository;
import it.marco.digrigoli.services.interfaces.ICoursePostService;

@Service
public class CoursePostServiceImpl implements ICoursePostService {

	private CoursePostRepository repo;
	
	public CoursePostServiceImpl(CoursePostRepository repo) {
		this.repo = repo;
	}
	
	@Override
	public CoursePost create(CoursePost post) {
		return repo.save(post);
	}

	@Override
	public void delete(CoursePost post) {
		repo.delete(post);
	}
	
	@Override
	public Optional<CoursePost> getById(Long id) {
		return repo.findById(id);
	}

}
