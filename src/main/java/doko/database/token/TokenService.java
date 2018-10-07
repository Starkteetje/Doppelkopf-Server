package doko.database.token;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import doko.database.user.User;
import doko.util.RandomProvider;

@Service
public class TokenService {

	private TokenRepository tokenRepository;
	private RandomProvider random = new RandomProvider();
	private static final int TOKEN_LENGTH = 128;
	private static final int VALIDITY_PERIOD_IN_MS = 1000 * 60 * 60 * 24 * 365; // Tokens will be valid for 1 year

	public List<Token> getAllTokens() {
		return tokenRepository.findAll();
	}

	public boolean isTokenValid(String tokenValue) {
		if (tokenValue == null) {
			return false;
		}
		List<Token> tokens = getAllTokens();
		return tokens.stream().anyMatch(token -> token.getTokenValue().equals(tokenValue));
	}

	public Optional<Token> getTokenByValue(String tokenValue) {
		Token token = tokenRepository.findOneByTokenValue(tokenValue);
		return Optional.ofNullable(token);
	}

	public List<Token> getTokensOfUser(Long id) {
		return tokenRepository.findByUserId(id);
	}

	public boolean deleteTokens(List<Token> tokens) {
		try {
			tokenRepository.delete(tokens);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean deleteToken(Token token) {
		try {
			tokenRepository.delete(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean deleteTokensOfUser(Long id) {
		return deleteTokens(getTokensOfUser(id));
	}

	public Token generateNewToken(User user) {
		Token token = new Token(user.getId(), random.getRandomHexString(TOKEN_LENGTH), new Date(System.currentTimeMillis() + VALIDITY_PERIOD_IN_MS));
		return saveToken(token);
	}

	public Token saveToken(Token token) {//TODO look into why return value
		return tokenRepository.save(token);
	}

	public Optional<Long> getUserIdOfToken(String tokenValue) {
		Optional<Token> token = getTokenByValue(tokenValue);
		if (token.isPresent()) {
			return Optional.ofNullable(token.get().getUserId());
		} else {
			return Optional.empty();
		}
	}

	@Autowired
	public void setTokenRepository(TokenRepository tokenRepository) {
		this.tokenRepository = tokenRepository;
	}

	public boolean deleteTokenByValue(String tokenValue) {
		Optional<Token> token = getTokenByValue(tokenValue);
		if (token.isPresent()) {
			return deleteToken(token.get());
		}
		return true; //TODO define whether no token present -> none deleted returns true/false
	}
}
