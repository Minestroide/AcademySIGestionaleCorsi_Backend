package it.marco.digrigoli.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.internal.util.Lists;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.marco.digrigoli.annotations.JWTTokenNeeded;
import it.marco.digrigoli.annotations.Secured;
import it.marco.digrigoli.entities.Category;
import it.marco.digrigoli.entities.Course;
import it.marco.digrigoli.entities.CoursePost;
import it.marco.digrigoli.entities.User;
import it.marco.digrigoli.entities.Role.RoleType;
import it.marco.digrigoli.entities.dto.CategoryDTO;
import it.marco.digrigoli.entities.dto.CourseDTO;
import it.marco.digrigoli.entities.dto.CoursePostCreateDTO;
import it.marco.digrigoli.entities.dto.CoursePostDTO;
import it.marco.digrigoli.entities.dto.CourseResponseDTO;
import it.marco.digrigoli.entities.dto.CourseUpdateDTO;
import it.marco.digrigoli.services.interfaces.ICategoryService;
import it.marco.digrigoli.services.interfaces.ICoursePostService;
import it.marco.digrigoli.services.interfaces.ICourseService;
import it.marco.digrigoli.services.interfaces.IDBFileService;
import it.marco.digrigoli.services.interfaces.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.StreamingOutput;
import jakarta.ws.rs.core.UriInfo;

@Component
@Path("/courses/{courseId}/users")
public class CourseUserController {

	private ICourseService courseService;
	
	private ICoursePostService coursePostService;

	private ICategoryService categoryService;

	private IUserService userService;

	private ObjectMapper objectMapper;

	private IDBFileService dbFileService;

	public CourseUserController(ICourseService courseService, ICategoryService categoryService, IUserService userService,
			IDBFileService dbFileService, ObjectMapper objectMapper, ICoursePostService coursePostService) {
		this.courseService = courseService;
		this.categoryService = categoryService;
		this.userService = userService;
		this.objectMapper = objectMapper;
		this.dbFileService = dbFileService;
		this.coursePostService = coursePostService;
	}

	private Logger logger = LogManager.getLogger(this.getClass());

	@DELETE
	@Path("/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(role = "ADMIN")
	public Response kickUser(@PathParam("courseId") Long courseId, @PathParam("userId") Long userId) {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		Optional<Course> course = courseService.getById(courseId);
		
		if(course.isEmpty()) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		
		if(!course.get().getUserIds().contains(userId)) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		
		course.get().setUsers(course.get().getUsers().stream().filter((predicate) -> {
			return !predicate.getId().equals(userId);
		}).toList());
		
		courseService.save(course.get());
		
		return Response.status(Response.Status.OK).build();
	}

}
