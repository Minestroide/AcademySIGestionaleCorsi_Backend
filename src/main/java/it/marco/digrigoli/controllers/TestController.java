package it.marco.digrigoli.controllers;

import org.springframework.stereotype.Component;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Component
@Path("/test")
public class TestController {

	@GET
	public String test() {
		return "Test";
	}
	
}
