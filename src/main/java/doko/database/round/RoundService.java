package doko.database.round;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import doko.database.game.Game;

@Service
public class RoundService {

	private RoundRepository roundRepository;

	public List<Round> getRounds(Game game) {
		return getRoundsByUniqueGameId(game.getUniqueId());
	}

	public List<Round> getRoundsByUniqueGameId(String id) {
		return roundRepository.findByUniqueGameId(id);
	}

	public Round addRound(Round round) {
		return roundRepository.save(round);
	}

	public Iterable<Round> addRounds(Iterable<Round> rounds) {
		return roundRepository.save(rounds);
	}

	@Autowired
	public void setRoundRepository(RoundRepository roundRepository) {
		this.roundRepository = roundRepository;
	}

	public void delete(Iterable<Round> rounds) {
		roundRepository.delete(rounds);
	}
}
