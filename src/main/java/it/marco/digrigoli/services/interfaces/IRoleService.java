package it.marco.digrigoli.services.interfaces;

import it.marco.digrigoli.entities.Role;

public interface IRoleService {
	
	public Role save(Role role);
	
	public Iterable<Role> getAll();

}
