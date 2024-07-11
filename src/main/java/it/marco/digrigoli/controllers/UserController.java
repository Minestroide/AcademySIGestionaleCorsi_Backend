package it.marco.digrigoli.controllers;

import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.internal.util.Lists;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import it.marco.digrigoli.annotations.JWTTokenNeeded;
import it.marco.digrigoli.annotations.Secured;
import it.marco.digrigoli.entities.User;
import it.marco.digrigoli.entities.dto.UserDTO;
import it.marco.digrigoli.entities.dto.UserRegistrationDTO;
import it.marco.digrigoli.entities.dto.UserUpdateDTO;
import it.marco.digrigoli.entities.dto.UserUpdatePasswordDTO;
import it.marco.digrigoli.services.interfaces.IEmailService;
import it.marco.digrigoli.services.interfaces.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Component
@Path("/users")
public class UserController {

	private IUserService userService;

	private IEmailService emailService;

	private Logger logger = LogManager.getLogger(this.getClass());

	public UserController(IUserService userService, IEmailService emailService) {
		this.userService = userService;
		this.emailService = emailService;
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(role = "ADMIN")
	public Iterable<UserDTO> getUsers() {
		ModelMapper modelMapper = new ModelMapper();

		return Lists.from(userService.getAll().iterator()).stream().map((user) -> {
			return modelMapper.map(user, UserDTO.class);
		}).toList();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void register(@RequestBody @Valid UserRegistrationDTO body) {
		User user = new User();
		user.setEmail(body.getEmail());
		user.setRoles(new ArrayList<>());
		user.setSurname(body.getSurname());
		user.setName(body.getName());
		user.setUsername(body.getUsername());
		
		String newPassword = userService.generateRandomSpecialCharacters(12);
		user.setPassword(newPassword);
		
		userService.register(user);

		this.emailService.sendSimpleMessage(body.getEmail(), "Registrazione effettuata",
				"Ciao, il tuo account è stato registrato correttamente.\n\nQuesta è la password generata automaticamente: "
						+ newPassword);
	}

	@GET
	@Path("/@me")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@JWTTokenNeeded
	public Response getMe(@Context HttpServletRequest request) {
		ModelMapper modelMapper = new ModelMapper();

		if (request.getSession() == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		User authUser = (User) request.getSession().getAttribute("user");

		if (authUser == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}

		return Response.status(Response.Status.OK).entity(modelMapper.map(authUser, UserDTO.class)).build();
	}

	@GET
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@JWTTokenNeeded
	public Response getById(@Context HttpServletRequest request, @PathParam("id") Long id) {
		ModelMapper modelMapper = new ModelMapper();

		if (request.getSession() == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		User authUser = (User) request.getSession().getAttribute("user");

		if (authUser == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}

		Optional<User> user = userService.getById(id);

		if (user.isEmpty()) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		return Response.status(Response.Status.OK).entity(modelMapper.map(user, UserDTO.class)).build();
	}

	@PUT
	@Path("/@me/password")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@JWTTokenNeeded
	public Response putPassword(@Context HttpServletRequest request, @RequestBody @Valid UserUpdatePasswordDTO body) {
		logger.info("Updating user...");
		logger.info(body.toString());
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		if (request.getSession() == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		User authUser = (User) request.getSession().getAttribute("user");

		if (body.getOldPassword() == null || body.getNewPassword() == null || body.getConfirmPassword() == null) {
			logger.info("Invalid DTO.");
			return Response.status(Response.Status.BAD_REQUEST).build();
		}

		String hashedPassword = userService.hashPassword(body.getNewPassword());
		if (hashedPassword.equals(authUser.getPassword())) {
			logger.info("Password doesn't match with the current one.");
			return Response.status(Response.Status.BAD_REQUEST).build();
		}

		if (!body.getNewPassword().equals(body.getConfirmPassword())) {
			logger.info("Passwords doesn't match.");
			return Response.status(Response.Status.BAD_REQUEST).build();
		}

		authUser.setPassword(userService.hashPassword(body.getNewPassword()));

		authUser = userService.save(authUser);

		return Response.status(Response.Status.OK).build();
	}

	@PATCH
	@Path("/@me")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@JWTTokenNeeded
	public Response putMe(@Context HttpServletRequest request, @RequestBody UserUpdateDTO body) {
		logger.info("Updating user...");
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		if (request.getSession() == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		User authUser = (User) request.getSession().getAttribute("user");

		if (authUser == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}

		if (body.getEmail() != null) {

			if (!Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$").matcher(body.getEmail()).matches()) {
				logger.info("Invalid email.");
				return Response.status(Response.Status.BAD_REQUEST).build();
			}

			authUser.setEmail(body.getEmail());
		}

		if (body.getName() != null) {

			if (!Pattern.compile("[a-zA-Z\\\\\\\\èàùìò\\s]{1,50}").matcher(body.getName()).matches()) {
				logger.info("Invalid name.");
				return Response.status(Response.Status.BAD_REQUEST).build();
			}

			authUser.setName(body.getName());
		}

		if (body.getSurname() != null) {

			if (!Pattern.compile("[a-zA-Z\\\\\\\\èàùìò\\s]{1,50}").matcher(body.getSurname()).matches()) {
				logger.info("Invalid surname.");
				return Response.status(Response.Status.BAD_REQUEST).build();
			}

			authUser.setSurname(body.getSurname());
		}

		if (body.getUsername() != null) {

			if (!Pattern.compile("[a-zA-Z0-9]{1,50}").matcher(body.getUsername()).matches()) {
				logger.info("Invalid username.");
				return Response.status(Response.Status.BAD_REQUEST).build();
			}
			authUser.setUsername(body.getUsername());
		}

		authUser = userService.save(authUser);

		return Response.status(Response.Status.OK).entity(modelMapper.map(authUser, UserDTO.class)).build();
	}

}
