package it.marco.digrigoli.services;

import org.springframework.stereotype.Service;

import it.marco.digrigoli.entities.Role;
import it.marco.digrigoli.repositories.RoleRepository;
import it.marco.digrigoli.services.interfaces.IRoleService;

@Service
public class RoleServiceImpl implements IRoleService {

	private RoleRepository repo;
	
	public RoleServiceImpl(RoleRepository repo) {
		this.repo = repo;
	}
	
	public Iterable<Role> getAll() {
		return this.repo.findAll();
	}
	
	public Role save(Role role) {
		return this.repo.save(role);
	}
	
}
