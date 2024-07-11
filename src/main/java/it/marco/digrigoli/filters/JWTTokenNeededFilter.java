package it.marco.digrigoli.filters;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import it.marco.digrigoli.annotations.JWTTokenNeeded;
import it.marco.digrigoli.annotations.Secured;
import it.marco.digrigoli.entities.User;
import it.marco.digrigoli.services.interfaces.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Component
@Provider
public class JWTTokenNeededFilter implements ContainerRequestFilter {

	private IUserService userService;

	@Value("${jwt.secret}")
	private String jwtSecret;
	
	public JWTTokenNeededFilter(IUserService userService) {
		this.userService = userService;
	}
	
	@Context
	private ResourceInfo resourceInfo;
	
	@Context
	private HttpServletRequest request;
	
	private Logger logger = LogManager.getLogger(this.getClass());
	
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		logger.info("JWT Token filter");
		
		JWTTokenNeeded jwtAnnotation = resourceInfo.getResourceMethod().getAnnotation(JWTTokenNeeded.class);
		Secured annotation = resourceInfo.getResourceMethod().getAnnotation(Secured.class);
		
		if(annotation == null) {
			annotation = resourceInfo.getResourceClass().getAnnotation(Secured.class);
		}
		
		if(jwtAnnotation == null) {
			jwtAnnotation = resourceInfo.getResourceClass().getAnnotation(JWTTokenNeeded.class);
		}
		
		if(annotation == null && jwtAnnotation == null) {
			logger.info("No valid annotations found.");
			return;
		}
		
		String roleNeeded = annotation == null ? "" : annotation.role();
		
		String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
		
		if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			logger.info("No valid authorization header found.");
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
			return;
		}
		
		String token = authorizationHeader.substring("Bearer".length()).trim();
		
		try {
			byte[] secret = jwtSecret.getBytes();
			
			SecretKey secretKey = Keys.hmacShaKeyFor(secret);
			
			Jws<Claims> claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
			Claims body = claims.getPayload();
			
			Long userId = body.get("user_id", Long.class);
			Optional<User> user = userService.getById(userId);
			
			if(user.isEmpty()) {
				logger.info("User not found.");
				requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
				return;
			}
			
			request.getSession().setAttribute("user", user.get());
			
			List<String> rolesToToken = body.get("roles", List.class);
			
			boolean hasRole = false;
			logger.info(rolesToToken);
			for(String role : rolesToToken) {
				if(role.equals(roleNeeded)) {
					hasRole = true;
					break;
				}
			}
			
			if(!hasRole && !roleNeeded.equals("")) {
				logger.info("User does not have role ("+roleNeeded+")");
				requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
			} else {
				logger.info("Auth successful.");
			}
		} catch(Exception e) {
			logger.error("An error occurred: ", e);
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
		}
	}	

}
