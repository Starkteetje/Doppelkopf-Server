package doko.database.user;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
	
	@Column(nullable = false)
    private String username;
	
	@Column(nullable = false)
    private String password;
	
	@Column(nullable = true)
    private String email;
	
	public User(String username, String password, String email) {
		this.username = username;
		this.password = password;
		this.email = email;
	}
	
	public User() {
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getEmail() {
		return email;
	}
	
	public Map<String, String> asMap() {
		Map<String, String> map = new HashMap<>();
		map.put("username", username);
		map.put("password", password);
		map.put("email", email);
		return map;
	}
}
