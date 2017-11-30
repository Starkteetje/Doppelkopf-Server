package doko.database.game;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import doko.lineup.LineUp;
import doko.lineup.LineUpComparator;

@Service
public class GameService {

	private GameRepository gameRepository;
	private static int NUMBER_OF_TOP_LINEUPS = 5;

	public List<SortedGame> getValidGames() {
		List<Game> games = gameRepository.findAll();
		return games.stream()
				.map(SortedGame::new)
				.filter(SortedGame::isValid) // TODO log invalid games -> DB inconsistency
				.collect(Collectors.toList());
	}

	public List<SortedGame> getGamesForLineUp(LineUp lineUp) {
		List<SortedGame> games = getValidGames();
		return games.stream()
				.filter(game -> game.getLineUp().equals(lineUp))
				.collect(Collectors.toList());
	}

	public void insertGame(Game game) {
		try {
			gameRepository.save(game);
		} catch (Exception e) {
			System.out.println("Game couldn't be saved"); //TODO Log properly, send error to user
		}
	}

	public void makeZeroSumGame(SortedGame game) {
		// TODO
	}

	public Set<LineUp> getAllLineUps() {
		List<SortedGame> games = getValidGames();
		return games.stream()
				.map(SortedGame::getLineUp)
				.collect(Collectors.toSet());
	}

	public LineUp[] getTopLineUps() {
		Set<LineUp> lineUps = getAllLineUps();
		return lineUps.stream()
				.sorted(new LineUpComparator(this))
				.limit(NUMBER_OF_TOP_LINEUPS)
				.toArray(LineUp[]::new);
	}

	public LineUp[] getNonTopLineUps() {
		Set<LineUp> nonTopLineUps = getAllLineUps();
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
