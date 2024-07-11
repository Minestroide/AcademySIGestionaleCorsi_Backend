package it.marco.digrigoli.controllers;

import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.exceptions.CodeGenerationException;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import it.marco.digrigoli.entities.User;
import it.marco.digrigoli.entities.dto.LoginFailDTO;
import it.marco.digrigoli.entities.dto.LoginFailDTO.FailType;
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
	
	private Logger logger = LogManager.getLogger(this.getClass());
	
	public AuthController(IUserService userService) {
		this.userService = userService;
	}
	
	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(@RequestBody UserLoginRequestDTO body) throws CodeGenerationException {
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
		
		if(user.get().getTwoFactorSecret() != null && !user.get().getTwoFactorSecret().trim().isEmpty()) {
			
			TimeProvider timeProvider = new SystemTimeProvider();
			CodeGenerator codeGenerator = new DefaultCodeGenerator();
			DefaultCodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
			
			verifier.setAllowedTimePeriodDiscrepancy(2);
			
			if(body.getTwoFactorCode() == null) {
				logger.info("Two factor secret: unauthorized (null).");
				LoginFailDTO dto = new LoginFailDTO();
				dto.setType(FailType.TWO_FACTOR_CODE_NEEDED);
				return Response.status(Response.Status.UNAUTHORIZED).entity(dto).build();
			}
			
			if(!verifier.isValidCode(user.get().getTwoFactorSecret(), body.getTwoFactorCode())) {
				logger.info("Two factor secret: unauthorized.");
				LoginFailDTO dto = new LoginFailDTO();
				dto.setType(FailType.TWO_FACTOR_CODE_NEEDED);
				return Response.status(Response.Status.UNAUTHORIZED).entity(dto).build();
			}
		}
		
		return Response.status(Response.Status.OK).entity(userService.issueToken(body.getEmail())).build();
	}
	
}
