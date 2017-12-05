package doko.database.token;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import doko.database.user.User;

@Service
public class TokenService {

	private TokenRepository tokenRepository;

	public List<Token> getAllTokens() {
		return tokenRepository.findAll();
	}

	public boolean isTokenValid(String tokenValue) {
		List<Token> tokens = getAllTokens();
		return tokens.stream().anyMatch(token -> token.getTokenValue().equals(tokenValue));
	}

	public Long getUserIdOfToken(String tokenValue) {
		List<Token> tokens = getAllTokens();
		Token tokenOrNull = tokens.stream().filter(token -> token.getTokenValue().equals(tokenValue)).findFirst()
				.orElse(null);

		return tokenOrNull == null ? null : tokenOrNull.getUserId();
	}

	public Token generateNewToken(User user) {
		return new Token(700L, "sdfsad", new Date());//TODO
	}

	@Autowired
	public void setTokenRepository(TokenRepository tokenRepository) {
		this.tokenRepository = tokenRepository;
	}
}
