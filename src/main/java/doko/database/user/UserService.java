package doko.database.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import doko.DokoException;

@Service
public class UserService {

	private UserRepository userRepository;

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public User getUser(String idString) throws DokoException {
		try {
			Long id = new Long(idString);
			return getUser(id);
		} catch (Exception e) {
			throw new DokoException("Malformed ID or no such user exists.");
		}
	}

	public User getUser(Long id) throws DokoException {
		if (id == null) {
			throw new DokoException("Malformed ID.");
		} else {
			return userRepository.findOne(id);
		}
	}

	@Autowired
	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
}
