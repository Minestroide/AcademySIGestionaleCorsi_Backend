package it.marco.digrigoli.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.internal.util.Lists;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import it.marco.digrigoli.annotations.JWTTokenNeeded;
import it.marco.digrigoli.annotations.Secured;
import it.marco.digrigoli.entities.Category;
import it.marco.digrigoli.entities.Course;
import it.marco.digrigoli.entities.CoursePost;
import it.marco.digrigoli.entities.CourseTask;
import it.marco.digrigoli.entities.User;
import it.marco.digrigoli.entities.dto.CategoryDTO;
import it.marco.digrigoli.entities.dto.CourseTaskCreateDTO;
import it.marco.digrigoli.entities.dto.CourseTaskDTO;
import it.marco.digrigoli.entities.dto.UserDTO;
import it.marco.digrigoli.services.interfaces.ICategoryService;
import it.marco.digrigoli.services.interfaces.ICourseService;
import it.marco.digrigoli.services.interfaces.ICourseTaskService;
import it.marco.digrigoli.services.interfaces.IDBFileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;

@Component
@Path("/courses/{id}/tasks")
public class CourseTaskController {

	private ICourseService courseService;
	
	private ICourseTaskService taskService;
	
	private IDBFileService fileService;
	
	public CourseTaskController(ICourseTaskService taskService, ICourseService courseService, IDBFileService fileService) {
		this.taskService = taskService;
		this.courseService = courseService;
		this.fileService = fileService;
	}
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@JWTTokenNeeded
	public Response getAll(@PathParam("id") Long courseId, @Context HttpServletRequest request) {
		if (request.getSession() == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		User authUser = (User) request.getSession().getAttribute("user");

		if (authUser == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		
		Optional<Course> course = courseService.getById(courseId);
		
		if(course.isEmpty()) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		
		if(course.get().getUsers().stream().noneMatch((predicate) -> {
			return predicate.getId().equals(authUser.getId());
		})) {
			return Response.status(Response.Status.FORBIDDEN).build();
		}
		
 		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		
		return Response.status(Response.Status.OK).entity(course.get().getTasks().stream().map((courseTask) -> {
			return modelMapper.map(courseTask, CourseTaskDTO.class);
		}).toList()).build();
	}
	
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(role = "ADMIN")
	public Response postTask(@PathParam("id") Long courseId, FormDataMultiPart form, @Context HttpServletRequest request) {
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
		
		Optional<Course> course = courseService.getById(courseId);
		
		if(course.get().getUsers().stream().noneMatch((predicate) -> {
			return predicate.getId().equals(authUser.getId());
		})) {
			return Response.status(Response.Status.FORBIDDEN).build();
		}
		
		if(course.isEmpty()) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}	
		
		CourseTaskCreateDTO createDto = body.getValueAs(CourseTaskCreateDTO.class);

		
		List<String> fileIds = new ArrayList<>();
		
		if(fieldsByName.containsKey("attachments")) {
			for(FormDataBodyPart part : fieldsByName.get("attachments")) {
				InputStream is = part.getEntityAs(InputStream.class);
				ContentDisposition meta = part.getContentDisposition();
				
				fileIds.add(fileService.uploadFile(is, meta));
			}
		}
		
		CourseTask courseTask = new CourseTask();
		courseTask.setTitle(createDto.getTitle());
		courseTask.setExpiration(createDto.getExpiration());
		courseTask.setContent(createDto.getContent());
		courseTask.setAttachmentFileIds(fileIds);
		courseTask.setCreatedAt(Instant.now());
		courseTask.setCourse(course.get());
		courseTask.setUser(authUser);
		
		taskService.create(courseTask);
		
		return Response.status(Response.Status.OK).build();
	}

	@GET
	@Path("/{taskId}/attachments/{attachmentIndex}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCoursePostAttachments(@PathParam(value = "id") String id, @PathParam(value = "taskId") String postId, @PathParam(value = "attachmentIndex") int index, @Context HttpServletRequest request) {
		Optional<CourseTask> courseTask = taskService.getById(Long.parseLong(postId));
		
		if(courseTask.isEmpty()) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		
		GridFsResource resource = fileService.getFile(courseTask.get().getAttachmentFileIds().get(index));
		
		StreamingOutput streamingOutput = new StreamingOutput() {
			
			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				output.write(resource.getContentAsByteArray());
			}
		};
		
		return Response.status(Response.Status.OK).entity(streamingOutput).header("Content-Length", resource.getGridFSFile().getLength()).type("image/png").build();
	}

}
