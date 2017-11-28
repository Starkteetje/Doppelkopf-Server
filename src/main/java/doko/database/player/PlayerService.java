package doko.database.player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import doko.LineUp;
import doko.database.game.SortedGame;

@Service
public class PlayerService {

	private PlayerRepository playerRepository;
	
	public List<Player> getAllPlayers() {
		return playerRepository.findAll();
	}
	
	public List<Player> getPlayers(List<Long> ids) {
		return playerRepository.findByIdIn(ids);
	}
	
	public List<String> getPlayerNames(List<Long> ids) {
		List<Player> players = getPlayers(ids);
		return players.stream()
				.map(Player::getName)
				.collect(Collectors.toList());
	}
	
	public List<String> getPlayerNames(LineUp lineUp) {
		return getPlayerNames(Arrays.asList(lineUp.getIds()));
	}
	
	public List<String> getPlayerNames(SortedGame game) {
		return getPlayerNames(game.getLineUp());
	}
	
	@Autowired
	public void setPlayerRepository(PlayerRepository playerRepository) {
		this.playerRepository = playerRepository;
	}
}
