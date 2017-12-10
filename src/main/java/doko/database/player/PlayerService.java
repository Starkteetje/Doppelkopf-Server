package doko.database.player;

import java.util.Arrays;
import java.util.List;

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

	public Player getPlayer(Long id) {
		return playerRepository.findOne(id);
	}

	public List<Player> getPlayers(List<Long> ids) {
		return playerRepository.findByIdIn(ids);
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

	@Autowired
	public void setPlayerRepository(PlayerRepository playerRepository) {
		this.playerRepository = playerRepository;
	}
}
