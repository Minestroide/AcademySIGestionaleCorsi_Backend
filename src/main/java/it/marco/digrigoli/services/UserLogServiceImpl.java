package it.marco.digrigoli.services;

import org.springframework.stereotype.Service;

import it.marco.digrigoli.entities.UserLog;
import it.marco.digrigoli.repositories.UserLogRepository;
import it.marco.digrigoli.services.interfaces.IUserLogService;

@Service
public class UserLogServiceImpl implements IUserLogService {

	private UserLogRepository repo;
	
	public UserLogServiceImpl(UserLogRepository repo) {
		this.repo = repo;
	}
	
	@Override
	public UserLog save(UserLog userLog) {
		return repo.save(userLog);
	}
	
}
