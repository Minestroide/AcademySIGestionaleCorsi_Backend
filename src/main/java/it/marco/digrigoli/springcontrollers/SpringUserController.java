package it.marco.digrigoli.springcontrollers;

import java.security.Principal;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.util.Lists;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.marco.digrigoli.entities.dto.UserDTO;
import it.marco.digrigoli.services.interfaces.IUserService;

@RestController
@RequestMapping("/spring/api/users")
public class SpringUserController {
	
	private IUserService userService;
	
	private Logger logger = LogManager.getLogger(this.getClass());
	
	public SpringUserController(IUserService userService) {
		this.userService = userService;
	}

	@GetMapping
	@Secured("ROLE_ADMIN")
	public Iterable<UserDTO> getUsers(Principal principal) {
		logger.debug(principal.getName());
		ModelMapper modelMapper = new ModelMapper();
		
		return Lists.from(userService.getAll().iterator()).stream().map((user) -> {
			return modelMapper.map(user, UserDTO.class);
		}).toList();
	}
	
}
