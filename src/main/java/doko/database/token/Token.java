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
public class Token {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
	
	@Column(name="user_id", nullable = false)
    private Integer userId;
	
	@Column(name="token_value", nullable = false)
    private String tokenValue;
	
	@Column(name="expiration_date", nullable = false)
    private Date expirationDate;
	
	public Token(Integer userId, String tokenValue, Date expirationDate) {
		this.userId = userId;
		this.tokenValue = tokenValue;
		this.expirationDate = expirationDate;
	}
	
	public Token() {
	}
	
	public Integer getUserId() {
		return userId;
	}
	
	public String getTokenValue() {
		return tokenValue;
	}
	
	public Date getExpirationDate() {
		return expirationDate;
	}
}
