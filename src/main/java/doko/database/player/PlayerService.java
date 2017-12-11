package doko.database.player;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import doko.lineup.LineUp;
import doko.lineup.NamedLineUp;
import doko.lineup.UnnamedLineUp;

@Service
public class PlayerService {

	private PlayerRepository playerRepository;

	public List<Player> getAllPlayers() {
		return playerRepository.findAll();
	}

	public Optional<Player> getPlayer(Long id) {
		return Optional.ofNullable(playerRepository.findOne(id));
	}

	public List<Player> getPlayers(List<Long> ids) {
		return playerRepository.findByIdIn(ids);
	}

	public Optional<Player> getPlayerByName(String playerName) {
		return Optional.ofNullable(playerRepository.findOneByName(playerName));
	}

	public NamedLineUp getNamedLineUp(LineUp lineUp) {
		return new NamedLineUp(lineUp, this);
	}

	public NamedLineUp getNamedLineUp(String lineUpString) {
		return getNamedLineUp(new UnnamedLineUp(lineUpString));
	}

	public NamedLineUp[] getNamedLineUps(LineUp[] lineUps) {
		return Arrays.stream(lineUps)
				.map(this::getNamedLineUp)
				.toArray(NamedLineUp[]::new);
	}

	public boolean addPlayer(String playerName) {
		Optional<Player> existingPlayer = getPlayerByName(playerName);
		if (existingPlayer.isPresent()) {
			return false;
		} else {
			Player player = new Player(playerName);
			playerRepository.save(player);
			return true;
		}
	}

	@Autowired
	public void setPlayerRepository(PlayerRepository playerRepository) {
		this.playerRepository = playerRepository;
	}
}
