package it.marco.digrigoli.controllers;

import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import it.marco.digrigoli.annotations.Secured;
import it.marco.digrigoli.entities.Contact;
import it.marco.digrigoli.entities.User;
import it.marco.digrigoli.entities.dto.ContactCreateDTO;
import it.marco.digrigoli.entities.dto.UserLoginRequestDTO;
import it.marco.digrigoli.services.interfaces.IContactService;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Component
@Path("/contacts")
public class ContactController {
	
	private IContactService contactService;
	
	public ContactController(IContactService contactService) {
		this.contactService = contactService;
	}
	
	private Logger logger = LogManager.getLogger(this.getClass());
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postContact(@RequestBody ContactCreateDTO body) {
		if(body == null) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		
		ModelMapper modelMapper = new ModelMapper();
		contactService.save(modelMapper.map(body, Contact.class));
		
		return Response.status(Response.Status.OK).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(role = "ADMIN")
	public Iterable<Contact> getAll() {
		return contactService.getAll();
	}
	
	@DELETE
	@Path("{id}")
	@Secured(role = "ADMIN")
	public Response deleteContact(@PathParam("id") Long id) {
		Optional<Contact> contact = contactService.getById(id);
		
		if(contact.isEmpty()) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		
		contactService.delete(contact.get());
		
		return Response.status(Response.Status.OK).build();
	}
	
}
