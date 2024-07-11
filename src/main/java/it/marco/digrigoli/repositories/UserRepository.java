package it.marco.digrigoli.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import it.marco.digrigoli.entities.User;

public interface UserRepository extends CrudRepository<User, Long> {

	public Optional<User> findByUsername(String username);
	
	public Optional<User> findByEmail(String email);
	
}
