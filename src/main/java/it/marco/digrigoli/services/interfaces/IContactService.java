package it.marco.digrigoli.services.interfaces;

import java.util.List;
import java.util.Optional;

import it.marco.digrigoli.entities.Contact;

public interface IContactService {
	
	public Contact save(Contact contact);
	
	public Iterable<Contact> getAll();
	
	public Optional<Contact> getById(Long id);
	
	public void delete(Contact contact);

}
