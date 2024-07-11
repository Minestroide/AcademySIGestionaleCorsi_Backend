package it.marco.digrigoli.services;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import it.marco.digrigoli.entities.Contact;
import it.marco.digrigoli.repositories.ContactRepository;
import it.marco.digrigoli.services.interfaces.IContactService;

@Service
public class ContactServiceImpl implements IContactService {

	private ContactRepository repo;
	
	public ContactServiceImpl(ContactRepository contactRepository) {
		this.repo = contactRepository;
	}

	@Override
	public Contact save(Contact contact) {
		return repo.save(contact);
	}

	@Override
	public Iterable<Contact> getAll() {
		return repo.findAll();
	}

	@Override
	public Optional<Contact> getById(Long id) {
		return repo.findById(id);
	}

	@Override
	public void delete(Contact contact) {
		repo.delete(contact);
	}
	
	
	
}
