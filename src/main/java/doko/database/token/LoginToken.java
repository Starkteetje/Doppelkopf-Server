package doko.database.token;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "login_tokens")
public class LoginToken {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
	
	@Column(nullable = false)
    private Integer user_id;
	
	@Column(nullable = false)
    private String token_value;
	
	@Column(nullable = false)
    private Date expiration_date;
	
	public LoginToken(Integer userId, String tokenValue, Date expirationDate) {
		this.user_id = userId;
		this.token_value = tokenValue;
		this.expiration_date = expirationDate;
	}
	
	public LoginToken() {
	}
	
	public Integer getUserId() {
		return user_id;
	}
	
	public String getTokenValue() {
		return token_value;
	}
	
	public Date getExpirationDate() {
		return expiration_date;
	}
}
