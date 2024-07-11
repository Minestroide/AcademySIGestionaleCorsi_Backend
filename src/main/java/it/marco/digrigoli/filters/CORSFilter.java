package it.marco.digrigoli.filters;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CORSFilter implements ContainerRequestFilter {

	@Context
	private ResourceInfo resourceInfo;
	
	private Logger logger = LogManager.getLogger(this.getClass());
	
	public void filter(ContainerRequestContext requestContext) throws IOException {
		logger.info("Adding CORS "+requestContext.getMethod()+"...");
		
		requestContext.getHeaders().add("Access-Control-Allow-Origin", "*");
		requestContext.getHeaders().add("Access-Control-Allow-Credentials", "*");
		requestContext.getHeaders().add("Access-Control-Allow-Headers", "*, Origin, X-Requested-With, Content-Type, Accept");
		requestContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE, HEAD");
		requestContext.getHeaders().add("Access-Control-Expose-Headers", "Location,Authorization,*");
	}	

}
