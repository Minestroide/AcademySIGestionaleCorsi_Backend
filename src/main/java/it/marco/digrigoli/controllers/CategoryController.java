package it.marco.digrigoli.controllers;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.modelmapper.internal.util.Lists;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import it.marco.digrigoli.entities.Category;
import it.marco.digrigoli.entities.dto.CategoryDTO;
import it.marco.digrigoli.entities.dto.UserDTO;
import it.marco.digrigoli.services.interfaces.ICategoryService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Component
@Path("/categories")
public class CategoryController {
	
	private ICategoryService categoryService;
	
	public CategoryController(ICategoryService categoryService) {
		this.categoryService = categoryService;
	}
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Iterable<CategoryDTO> getAll() {
		ModelMapper modelMapper = new ModelMapper();
		
		return Lists.from(categoryService.getAll().iterator()).stream().map((user) -> {
			return modelMapper.map(user, CategoryDTO.class);
		}).toList();
	}
	
	@GET
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getById(@PathParam("id") String id) {
		try {
			ModelMapper modelMapper = new ModelMapper();
			Optional<Category> category = categoryService.getById(Long.parseLong(id));
			if(category.isEmpty()) return Response.status(Response.Status.NOT_FOUND).build();
			
			return Response.status(Response.Status.OK).entity(modelMapper.map(category.get(), CategoryDTO.class)).build();
		} catch(NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void create(@RequestBody CategoryDTO body) {
		ModelMapper modelMapper = new ModelMapper();
		categoryService.save(modelMapper.map(body, Category.class));
	}

}
