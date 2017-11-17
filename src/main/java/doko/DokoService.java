package doko;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import doko.database.game.Game;
import doko.database.game.GameRepository;
import doko.database.game.SortedGame;
import doko.database.player.Player;
import doko.database.player.PlayerRepository;
import doko.database.rules.Rules;
import doko.database.rules.RulesRepository;
import doko.database.token.LoginToken;
import doko.database.token.TokenRepository;
import doko.database.user.User;
import doko.database.user.UserRepository;

@Service
public class DokoService {
	
	private GameRepository gameRepository;
	private PlayerRepository playerRepository;
	private RulesRepository rulesRepository;
	private TokenRepository tokenRepository;
	private UserRepository userRepository;
	
	public List<SortedGame> getValidGames() {
		List<Game> games = gameRepository.findAll();
		return games.stream()
				.map(SortedGame::new)
				.filter(SortedGame::isValid)
				.collect(Collectors.toList());
	}
	
	public List<SortedGame> getGamesForLineUp(LineUp lineUp) {
		List<SortedGame> games = getValidGames();
		return games.stream()
				.filter(game -> game.getLineUp().equals(lineUp))
				.collect(Collectors.toList());
	}
	
	public List<Player> getAllPlayers() {
		return playerRepository.findAll();
	}
	
	public List<Player> getPlayers(List<Long> ids) {
		return playerRepository.findByIdIn(ids);
	}
	
	public List<String> getPlayerNames(List<Long> ids) {
		List<Player> players = getPlayers(ids);
		return players.stream()
				.map(player -> player.getName())
				.collect(Collectors.toList());
	}
	
	public List<String> getPlayerNames(LineUp lineUp) {
		return getPlayerNames(Arrays.asList(lineUp.getIds()));
	}
	
	public List<String> getPlayerNames(SortedGame game) {
		return getPlayerNames(game.getLineUp());
	}
	
	public List<Rules> getAllRules() {
		return rulesRepository.findAll();
	}
	
	public List<LoginToken> getAllTokens() {
		return tokenRepository.findAll();
	}
	
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	@Autowired
	public void setGameRepository(GameRepository gameRepository) {
	    this.gameRepository = gameRepository;
	}

	@Autowired
	public void setPlayerRepository(PlayerRepository playerRepository) {
		this.playerRepository = playerRepository;
	}

	@Autowired
	public void setRulesRepository(RulesRepository rulesRepository) {
		this.rulesRepository = rulesRepository;
	}

	@Autowired
	public void setTokenRepository(TokenRepository tokenRepository) {
		this.tokenRepository = tokenRepository;
	}

	@Autowired
	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
}
