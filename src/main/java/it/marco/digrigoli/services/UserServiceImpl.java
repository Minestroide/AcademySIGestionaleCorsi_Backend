package it.marco.digrigoli.services;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.text.RandomStringGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import it.marco.digrigoli.entities.Role;
import it.marco.digrigoli.entities.User;
import it.marco.digrigoli.entities.dto.UserLoginResponseDTO;
import it.marco.digrigoli.repositories.UserRepository;
import it.marco.digrigoli.services.interfaces.IUserService;

@Service
public class UserServiceImpl implements IUserService {

	private UserRepository repo;
	
	private Logger logger = LogManager.getLogger(this.getClass());
	
	@Value("${jwt.secret}")
	private String jwtSecret;

	public UserServiceImpl(UserRepository repo) {
		this.repo = repo;
	}
	
	@Override 
	public String generateRandomSpecialCharacters(int length) {
	    RandomStringGenerator pwdGenerator = new RandomStringGenerator.Builder().withinRange('a', 'z').get();
	    RandomStringGenerator pwdGenerator2 = new RandomStringGenerator.Builder().withinRange('0', '9').get();
	    RandomStringGenerator pwdGenerator3 = new RandomStringGenerator.Builder().withinRange(33, 45).get();
	    return pwdGenerator.generate(length)+pwdGenerator2.generate(3)+pwdGenerator3.generate(3);
	}

	@Override
	public Optional<User> loadUserByName(String username) {
		Optional<User> userOpt = repo.findByUsername(username);

		if (userOpt.isEmpty()) {
			userOpt = repo.findByEmail(username);
		}

		return userOpt;
	}

	@Override
	public Optional<User> findByEmail(String email) {
		// TODO Auto-generated method stub
		return repo.findByEmail(email);
	}

	@Override
	public UserLoginResponseDTO issueToken(String email) {
		byte[] secret = jwtSecret.getBytes();
		Key key = Keys.hmacShaKeyFor(secret);

		Optional<User> userOpt = this.loadUserByName(email);

		if (userOpt.isEmpty()) {
			throw new IllegalStateException("Cannot issue token for null user.");
		}

		User user = userOpt.get();

		Map<String, Object> map = new HashMap<>();

		map.put("user_id", user.getId());
		map.put("name", user.getName());
		map.put("surname", user.getSurname());
		map.put("email", email);

		List<String> roles = new ArrayList<>();

		for (Role role : user.getRoles()) {
			roles.add(role.getType().name());
		}

		map.put("roles", roles);

		LocalDateTime creation = LocalDateTime.now();
		LocalDateTime end = LocalDateTime.now().plusMinutes(15L);
		String jwtToken = Jwts.builder().claims(map).issuer("http://localhost:8080")
				.issuedAt(Date.from(creation.toInstant(ZoneOffset.UTC)))
				.expiration(Date.from(end.toInstant(ZoneOffset.UTC))).signWith(key).compact();

		UserLoginResponseDTO token = new UserLoginResponseDTO();

		token.setToken(jwtToken);
		token.setTtl(end);
		token.setTokenCreationTime(creation);

		return token;
	}

	public String hashPassword(String password) {
		return DigestUtils.sha512Hex(password + "salt");
	}

	@Override
	public User register(User user) {
		if (loadUserByName(user.getEmail()).isPresent() || loadUserByName(user.getUsername()).isPresent()) {
			throw new IllegalStateException("User already exists.");
		}
		user.setPassword(hashPassword(user.getPassword()));
		
		logger.info(user.toString());
		
		return repo.save(user);
	}

	@Override
	public Iterable<User> getAll() {
		return repo.findAll();
	}

	@Override
	public Optional<User> getById(Long id) {
		return repo.findById(id);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		return this.loadUserByName(username).orElse(null);
	}
	
	@Override
	public User save(User user) {
		return repo.save(user);
	}

}
