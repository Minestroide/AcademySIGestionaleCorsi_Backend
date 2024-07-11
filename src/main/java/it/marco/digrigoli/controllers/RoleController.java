package it.marco.digrigoli.controllers;

import org.glassfish.jersey.internal.guava.Lists;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import it.marco.digrigoli.entities.Role;
import it.marco.digrigoli.entities.dto.RoleDTO;
import it.marco.digrigoli.services.interfaces.IRoleService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Component
@Path("/roles")
public class RoleController {
	
	private IRoleService roleService;
	
	public RoleController(IRoleService roleService) {
		this.roleService = roleService;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Iterable<RoleDTO> getAll() {
		ModelMapper modelMapper = new ModelMapper();
		return Lists.newArrayList(this.roleService.getAll()).stream().map((role) -> {
			return modelMapper.map(role, RoleDTO.class);
		}).toList();
	}

}
