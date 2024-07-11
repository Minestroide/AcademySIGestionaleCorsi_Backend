package it.marco.digrigoli.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import it.marco.digrigoli.entities.CoursePost;
import it.marco.digrigoli.entities.CourseTask;
import it.marco.digrigoli.repositories.CoursePostRepository;
import it.marco.digrigoli.repositories.CourseTaskRepository;
import it.marco.digrigoli.services.interfaces.ICoursePostService;
import it.marco.digrigoli.services.interfaces.ICourseTaskService;

@Service
public class CourseTaskServiceImpl implements ICourseTaskService {

	private CourseTaskRepository repo;
	
	public CourseTaskServiceImpl(CourseTaskRepository repo) {
		this.repo = repo;
	}
	
	@Override
	public CourseTask create(CourseTask task) {
		return repo.save(task);
	}

	@Override
	public void delete(CourseTask task) {
		repo.delete(task);
	}
	
	@Override
	public Optional<CourseTask> getById(Long id) {
		return repo.findById(id);
	}

	@Override
	public Iterable<CourseTask> getAll() {
		return repo.findAll();
	}

}
