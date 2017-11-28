package doko.database.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import doko.database.game.SortedGame;
import doko.lineup.LineUp;
import doko.lineup.NamedLineUp;

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
		players.sort(Player::compareTo);
		
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
	
	public NamedLineUp[] getNamedLineUps(LineUp[] lineUps) {
		return Arrays.stream(lineUps)
				.map(lineUp -> new NamedLineUp(lineUp, getPlayerNames(lineUp)))
				.toArray(NamedLineUp[]::new);
	}
	
	@Autowired
	public void setPlayerRepository(PlayerRepository playerRepository) {
		this.playerRepository = playerRepository;
	}
}
