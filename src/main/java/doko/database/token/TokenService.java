package doko.database.token;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

	@Autowired
	public void setTokenRepository(TokenRepository tokenRepository) {
		this.tokenRepository = tokenRepository;
	}
}
