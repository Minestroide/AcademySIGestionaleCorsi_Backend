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
@Path("/courses")
public class CourseController {

	private ICourseService courseService;
	
	private ICoursePostService coursePostService;

	private ICategoryService categoryService;

	private IUserService userService;

	private ObjectMapper objectMapper;

	private IDBFileService dbFileService;

	public CourseController(ICourseService courseService, ICategoryService categoryService, IUserService userService,
			IDBFileService dbFileService, ObjectMapper objectMapper, ICoursePostService coursePostService) {
		this.courseService = courseService;
		this.categoryService = categoryService;
		this.userService = userService;
		this.objectMapper = objectMapper;
		this.dbFileService = dbFileService;
		this.coursePostService = coursePostService;
	}

	private Logger logger = LogManager.getLogger(this.getClass());

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Iterable<CourseDTO> getAll(@QueryParam("name") String name, @QueryParam("categoryId") Long categoryId) {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		if (categoryId == null && name != null) {
			return courseService.searchByName(name).stream().map((course) -> {
				return modelMapper.map(course, CourseDTO.class);
			}).toList();
		} else if (categoryId != null && name != null) {
			Optional<Category> category = categoryService.getById(categoryId);
			if (category.isEmpty()) {
				return List.of();
			}

			logger.info("Searching by: " + name + " " + categoryId);

			return courseService.searchByNameAndCategoryId(name, category.get()).stream().map((course) -> {
				return modelMapper.map(course, CourseDTO.class);
			}).toList();
		} else {
			return courseService.getAll().stream().map((course) -> {
				return modelMapper.map(course, CourseDTO.class);
			}).toList();
		}
	}

	@DELETE
	@Path("{id}")
	@Secured(role = "ADMIN")
	public Response deleteById(@PathParam("id") String id) {
		Optional<Course> course = courseService.getById(Long.parseLong(id));
		if (course.isEmpty()) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		courseService.delete(course.get());

		return Response.status(Response.Status.OK).build();
	}

	@GET
	@Path("@me")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@JWTTokenNeeded
	public Response getMine(@Context HttpServletRequest request) {
		if (request.getSession() == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		User authUser = (User) request.getSession().getAttribute("user");

		if (authUser == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		return Response.status(Response.Status.OK).entity(authUser.getCourses().stream().map((course) -> {
			return mapper.map(course, CourseDTO.class);
		}).toList()).build();
	}

	@GET
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getById(@PathParam("id") String id) {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		try {
			Optional<Course> course = courseService.getById(Long.parseLong(id));
			if (course.isEmpty()) {
				return Response.status(Response.Status.NOT_FOUND).build();
			}

			return Response.status(Response.Status.OK).entity(modelMapper.map(course.get(), CourseDTO.class)).build();
		} catch (NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}

	@GET
	@Path("{id}/banner")
	public Response getByIdBanner(@PathParam("id") String id) {
		Optional<Course> course = courseService.getById(Long.parseLong(id));
		if (course.isEmpty()) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		String fileId = course.get().getBannerFileId();
		GridFsResource file = dbFileService.getFile(fileId);

		StreamingOutput output = new StreamingOutput() {

			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				output.write(file.getInputStream().readAllBytes());
			}
		};

		return Response.status(Response.Status.OK).entity(output).type("image/png").header("Content-Length", file.getGridFSFile().getLength())
				.build();
	}

	@POST
	@Path("{id}/subscribe")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@JWTTokenNeeded
	public Response subscribeById(@Context HttpServletRequest request, @PathParam("id") Long id,
			@RequestBody CourseUpdateDTO body) {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		if (request.getSession() == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		User authUser = (User) request.getSession().getAttribute("user");

		if (authUser == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}

		Optional<Course> course = courseService.getById(id);
		if (course.isEmpty()) {
			CourseResponseDTO responseDto = new CourseResponseDTO(id,
					CourseResponseDTO.CourseResponseType.COURSE_NOT_FOUND);
			return Response.status(Response.Status.NOT_FOUND).entity(responseDto).build();
		}

		int previousSize = authUser.getCourses().size();

		boolean alreadySubscribed = authUser.getCourses().stream().filter((currentCourse) -> {
			return currentCourse.getId().equals(id);
		}).findAny().isPresent();

		if (alreadySubscribed) {
			CourseResponseDTO responseDto = new CourseResponseDTO(id,
					CourseResponseDTO.CourseResponseType.USER_IN_COURSE);
			return Response.status(Response.Status.OK).entity(responseDto).build();
		}
		
		int currentUserCount = course.get().getUsers().size();
		
		if(currentUserCount >= course.get().getMaxParticipants()) {
			CourseResponseDTO responseDto = new CourseResponseDTO(id,
					CourseResponseDTO.CourseResponseType.COURSE_FULL);
			return Response.status(Response.Status.OK).entity(responseDto).build();
		}
		
		logger.info("Adding "+authUser.getUsername()+ " to course: "+course.get().getName());

		authUser.getCourses().add(course.get());
		userService.save(authUser);
		CourseResponseDTO responseDto = new CourseResponseDTO(id,
				CourseResponseDTO.CourseResponseType.SUCCESSFULLY_ADDED);
		return Response.status(Response.Status.OK).entity(responseDto).build();
	}

	@POST
	@Path("{id}/unsubscribe")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@JWTTokenNeeded
	public Response unsubscribeById(@Context HttpServletRequest request, @PathParam("id") Long id,
			@RequestBody CourseUpdateDTO body) {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		if (request.getSession() == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		User authUser = (User) request.getSession().getAttribute("user");

		if (authUser == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}

		Optional<Course> course = courseService.getById(id);
		if (course.isEmpty()) {
			CourseResponseDTO responseDto = new CourseResponseDTO(id,
					CourseResponseDTO.CourseResponseType.COURSE_NOT_FOUND);
			return Response.status(Response.Status.NOT_FOUND).entity(responseDto).build();
		}

		logger.info("CourseIds: " + authUser.getCourses().size());

		int previousSize = authUser.getCourses().size();

		authUser.setCourses(authUser.getCourses().stream().filter((currentCourse) -> {
			return !currentCourse.getId().equals(id);
		}).toList());

		if (authUser.getCourses().size() == previousSize) {
			CourseResponseDTO responseDto = new CourseResponseDTO(id,
					CourseResponseDTO.CourseResponseType.USER_NOT_IN_COURSE);
			return Response.status(Response.Status.OK).entity(responseDto).build();
		}

		logger.info("CourseIds after: " + authUser.getCourses().size());
		userService.save(authUser);
		CourseResponseDTO responseDto = new CourseResponseDTO(id,
				CourseResponseDTO.CourseResponseType.SUCCESSFULLY_REMOVED);
		return Response.status(Response.Status.OK).entity(responseDto).build();
	}

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(role = "ADMIN")
	public Response create(@FormDataParam(value = "data") FormDataBodyPart body,
			@FormDataParam(value = "banner") InputStream bannerInputStream,
			@FormDataParam("banner") FormDataContentDisposition bannerDetail) {
		body.setMediaType(MediaType.APPLICATION_JSON_TYPE);
		CourseDTO courseDto = body.getValueAs(CourseDTO.class);
		Optional<Category> category = categoryService.getById(courseDto.getCategoryId());
		if (category.isEmpty()) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		String fileId = dbFileService.uploadFile(bannerInputStream, bannerDetail);

		Course course = new Course();
		course.setBannerFileId(fileId);
		course.setName(courseDto.getName());
		course.setCategory(category.get());
		course.setDescription(courseDto.getDescription());
		course.setMaxParticipants(courseDto.getMaxParticipants());
		courseService.save(course);
		return Response.status(Response.Status.OK).build();
	}
	
	@GET
	@Path("/{id}/posts")
	@Produces(MediaType.APPLICATION_JSON)
	@JWTTokenNeeded
	public Response getCoursePosts(@PathParam(value = "id") String id, @Context HttpServletRequest request) {

		if (request.getSession() == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		User authUser = (User) request.getSession().getAttribute("user");

		if (authUser == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		
		Optional<Course> course = courseService.getById(Long.parseLong(id));
		
		if(course.isEmpty()) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		
		boolean authorized = false;
		
		if(authUser.getRoles().stream().anyMatch((predicate) -> {
			return predicate.getType() == RoleType.ADMIN;
		})) {
			authorized = true;
		}
		
		logger.info("Course users: "+course.get().getUsers().size());
		
		if(course.get().getUsers().stream().anyMatch((predicate) -> {
			return predicate.getId().equals(authUser.getId());
		})) {
			authorized = true;
		}
		
		if(!authorized) {
			return Response.status(Response.Status.FORBIDDEN).build();
		}
		
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		
		return Response.status(Response.Status.OK).entity(course.get().getPosts().stream().map((coursePost) -> {
			return mapper.map(coursePost, CoursePostDTO.class);
		}).toList()).build();
	}

	@POST
	@Path("/{id}/posts")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@JWTTokenNeeded
	public Response newCoursePost(@PathParam(value = "id") String id, FormDataMultiPart form, @Context HttpServletRequest request) {

		Map<String, List<FormDataBodyPart>> fieldsByName = form.getFields();
		FormDataBodyPart body = fieldsByName.get("data").get(0);
		body.setMediaType(MediaType.APPLICATION_JSON_TYPE);
		
		if (request.getSession() == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		User authUser = (User) request.getSession().getAttribute("user");

		if (authUser == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		
		Optional<Course> course = courseService.getById(Long.parseLong(id));
		
		CoursePostCreateDTO createDto = body.getValueAs(CoursePostCreateDTO.class);
		
		if(course.isEmpty()) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		
		if(course.get().getUsers().stream().noneMatch((predicate) -> {
			return predicate.getId().equals(authUser.getId());
		})) {
			return Response.status(Response.Status.FORBIDDEN).build();
		}
		
		List<String> fileIds = new ArrayList<>();
		
		if(fieldsByName.containsKey("attachments")) {
			for(FormDataBodyPart part : fieldsByName.get("attachments")) {
				InputStream is = part.getEntityAs(InputStream.class);
				ContentDisposition meta = part.getContentDisposition();
				
				logger.info("Uploading "+ meta);
				
				fileIds.add(dbFileService.uploadFile(is, meta));
			}
		}
		CoursePost coursePost = new CoursePost();
		coursePost.setCourse(course.get());
		coursePost.setContent(createDto.getContent());
		coursePost.setCreatedAt(Instant.now());
		coursePost.setAttachmentFileIds(new ArrayList<>());
		coursePost.getAttachmentFileIds().addAll(fileIds);
		coursePost.setUser(authUser);
		coursePost = coursePostService.create(coursePost);
		
		return Response.status(Response.Status.OK).build();
	}

	@DELETE
	@Path("/{id}/posts/{postId}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@JWTTokenNeeded
	public Response deleteCoursePost(@PathParam(value = "id") String id, @PathParam(value = "postId") String postId, @Context HttpServletRequest request) {
		if (request.getSession() == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		User authUser = (User) request.getSession().getAttribute("user");

		if (authUser == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		
		Optional<CoursePost> coursePost = coursePostService.getById(Long.parseLong(postId));
		
		boolean authorized = false;
		
		if(authUser.getRoles().stream().anyMatch((predicate) -> {
			return predicate.getType() == RoleType.ADMIN;
		})) {
			authorized = true;
		}
		
		if(authUser.getId().equals(coursePost.get().getUser().getId())) {
			authorized = true;
		}
		
		if(!authorized) {
			return Response.status(Response.Status.FORBIDDEN).build();
		}
		
		if(coursePost.isEmpty()) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		
		coursePostService.delete(coursePost.get());
		
		return Response.status(Response.Status.OK).build();
	}

	@GET
	@Path("/{id}/posts/{postId}/attachments/{attachmentIndex}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCoursePostAttachments(@PathParam(value = "id") String id, @PathParam(value = "postId") String postId, @PathParam(value = "attachmentIndex") int index, @Context HttpServletRequest request) {
		Optional<CoursePost> coursePost = coursePostService.getById(Long.parseLong(postId));
		
		if(coursePost.isEmpty()) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		
		
		logger.info("Loading post attachment for post: "+coursePost.get().getId());
		
		GridFsResource resource = dbFileService.getFile(coursePost.get().getAttachmentFileIds().get(index));
		
		StreamingOutput streamingOutput = new StreamingOutput() {
			
			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				output.write(resource.getContentAsByteArray());
			}
		};
		
		return Response.status(Response.Status.OK).entity(streamingOutput).header("Content-Length", resource.getGridFSFile().getLength()).type("image/png").build();
	}

	@PATCH
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(role = "ADMIN")
	public Response update(@PathParam(value = "id") String id, @RequestBody CourseUpdateDTO body) {
		long parsedId = 0L;

		try {
			parsedId = Long.parseLong(id);
		} catch (NumberFormatException e) {
			// ignore
		}

		if (parsedId <= 0L) {
			return Response.status(Status.BAD_REQUEST).build();
		}

		if (body.getName() == null || body.getName().trim().equals("")) {
			return Response.status(Status.BAD_REQUEST).build();
		}

		Optional<Course> existingCourse = courseService.getById(parsedId);
		if (existingCourse.isEmpty()) {
			return Response.status(Status.NOT_FOUND).build();
		}

		existingCourse.get().setName(body.getName());
		Course updatedCourse = courseService.save(existingCourse.get());

		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		CourseDTO updatedCourseDTO = modelMapper.map(updatedCourse, CourseDTO.class);

		return Response.status(Status.OK).entity(updatedCourseDTO).build();
	}

}
