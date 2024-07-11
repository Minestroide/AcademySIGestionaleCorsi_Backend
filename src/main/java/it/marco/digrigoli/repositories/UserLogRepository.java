package it.marco.digrigoli.repositories;

import org.springframework.data.repository.CrudRepository;

import it.marco.digrigoli.entities.UserLog;

public interface UserLogRepository extends CrudRepository<UserLog, Long> {

}
