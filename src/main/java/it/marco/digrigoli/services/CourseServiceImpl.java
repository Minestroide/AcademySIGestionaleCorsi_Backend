package it.marco.digrigoli.services;

import java.util.List;
import java.util.Optional;

import org.glassfish.jersey.internal.guava.Lists;
import org.springframework.stereotype.Service;

import it.marco.digrigoli.entities.Category;
import it.marco.digrigoli.entities.Course;
import it.marco.digrigoli.repositories.CourseRepository;
import it.marco.digrigoli.services.interfaces.ICourseService;
import it.marco.digrigoli.services.interfaces.IDBFileService;

@Service
public class CourseServiceImpl implements ICourseService {

	private CourseRepository repo;
	private IDBFileService fileService;
	
	public CourseServiceImpl(CourseRepository repo, IDBFileService fileService) {
		this.repo = repo;
		this.fileService = fileService;
	}
	
	@Override
	public Course save(Course course) {
		return this.repo.save(course);
	}
	
	@Override
	public Optional<Course> getById(Long id) {
		return repo.findById(id);
	}

	@Override
	public List<Course> getAll() {
		return Lists.newArrayList(repo.findAll());
	}
	
	@Override
	public List<Course> searchByName(String name) {
		// TODO Auto-generated method stub
		return repo.findByName(name);
	}
	
	@Override
	public List<Course> searchByNameAndCategoryId(String name, Category category) {
		// TODO Auto-generated method stub
		return Lists.newArrayList(repo.findByNameAndCategoryId(name, category));
	}
	
	@Override
	public void delete(Course course) {
		if(course.getBannerFileId() != null) {
			fileService.deleteFile(course.getBannerFileId());
		}
		repo.delete(course);
	}
	
}
