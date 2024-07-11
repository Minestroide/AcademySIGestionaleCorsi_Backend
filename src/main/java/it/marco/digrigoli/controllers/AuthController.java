package it.marco.digrigoli.controllers;

import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import it.marco.digrigoli.entities.User;
import it.marco.digrigoli.entities.dto.UserLoginRequestDTO;
import it.marco.digrigoli.services.interfaces.IUserService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Component
@Path("/auth")
public class AuthController {
	
	private IUserService userService;
	
	public AuthController(IUserService userService) {
		this.userService = userService;
	}
	
	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(@RequestBody UserLoginRequestDTO body) {
		if(body == null) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		
		String hashedPassword = DigestUtils.sha512Hex(body.getPassword()+"salt");
		Optional<User> user = userService.loadUserByName(body.getEmail());
		
		if(user.isEmpty()) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		
		String currentHashedPassword = user.get().getPassword();
		
		if(!currentHashedPassword.equals(hashedPassword)) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		
		return Response.status(Response.Status.OK).entity(userService.issueToken(body.getEmail())).build();
	}
	
}
