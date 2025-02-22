package it.marco.digrigoli.repositories;

import org.springframework.data.repository.CrudRepository;

import it.marco.digrigoli.entities.Contact;

public interface ContactRepository extends CrudRepository<Contact, Long> {

}
