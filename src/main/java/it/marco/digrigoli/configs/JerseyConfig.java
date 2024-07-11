package it.marco.digrigoli.configs;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import it.marco.digrigoli.annotations.JWTTokenNeeded;
import it.marco.digrigoli.filters.CORSFilter;
import it.marco.digrigoli.filters.JWTTokenNeededFilter;
import jakarta.ws.rs.ApplicationPath;

@Component
@ApplicationPath("/api")
public class JerseyConfig extends ResourceConfig {

	public JerseyConfig() {
		packages("it.marco.digrigoli");
	}
	
}
