package doko.database.game;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import doko.DokoConstants;
import doko.lineup.LineUp;
import doko.lineup.LineUpComparator;

@Service
public class GameService {

	private GameRepository gameRepository;

	public List<SortedGame> getValidGamesOrdered() {
		List<Game> games = gameRepository.findAll();
		return games.stream()
				.map(SortedGame::new)
				.filter(SortedGame::isValid) // TODO log invalid games -> DB inconsistency
				.sorted((a, b) -> a.getDate().compareTo(b.getDate())) // Order games by their date
				.collect(Collectors.toList());
	}

	public List<SortedGame> getGamesForLineUp(LineUp lineUp) {
		List<SortedGame> games = getValidGamesOrdered();
		return games.stream()
				.filter(game -> game.getLineUp().equals(lineUp))
				.collect(Collectors.toList());
	}

	public Optional<Game> getGameByUniqueId(String uniqueGameId) {
		return Optional.ofNullable(gameRepository.findOneByUniqueGameId(uniqueGameId));
	}

	public Game addGame(Game game) {
		return gameRepository.save(game);
	}

	public List<LineUp> getAllLineUps() {
		List<SortedGame> games = getValidGamesOrdered();
		return games.stream()
				.map(SortedGame::getLineUp)
				.distinct()
				.collect(Collectors.toList());
	}

	public LineUp[] getTopLineUps() {
		List<LineUp> lineUps = getAllLineUps();
		return lineUps.stream()
				.sorted(new LineUpComparator(this))
				.limit(DokoConstants.NUMBER_OF_TOP_LINEUPS)
				.toArray(LineUp[]::new);
	}

	public LineUp[] getNonTopLineUps() {
		List<LineUp> nonTopLineUps = getAllLineUps();
		nonTopLineUps.removeAll(Arrays.asList(getTopLineUps()));
		return nonTopLineUps.stream()
				.sorted(new LineUpComparator(this))
				.toArray(LineUp[]::new);
	}

	@Autowired
	public void setGameRepository(GameRepository gameRepository) {
		this.gameRepository = gameRepository;
	}
}
