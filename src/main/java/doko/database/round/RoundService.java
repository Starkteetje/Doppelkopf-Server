package doko.database.round;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import doko.database.game.Game;

@Service
public class RoundService {

	private RoundRepository roundRepository;

	public List<Round> getRounds(Game game) {
		return getRoundsByUniqueGameId(game.getUniqueGameId());
	}

	public List<Round> getRoundsByUniqueGameId(String id) {
		return roundRepository.findAll().stream()
				.filter(r -> r.getUniqueGameId().equals(id))
				.sorted()
				.collect(Collectors.toList());
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
