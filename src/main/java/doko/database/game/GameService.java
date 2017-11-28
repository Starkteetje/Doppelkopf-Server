package doko.database.game;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import doko.LineUp;

@Service
public class GameService {
	
	private GameRepository gameRepository;
	
	public List<SortedGame> getValidGames() {
		List<Game> games = gameRepository.findAll();
		return games.stream()
				.map(SortedGame::new)
				.filter(SortedGame::isValid) //TODO log invalid games -> DB inconsistency
				.collect(Collectors.toList());
	}
	
	public List<SortedGame> getGamesForLineUp(LineUp lineUp) {
		List<SortedGame> games = getValidGames();
		return games.stream()
				.filter(game -> game.getLineUp().equals(lineUp))
				.collect(Collectors.toList());
	}
	
	public boolean insertGame(SortedGame game) {
		//TODO
		return false;
	}
	
	public void makeZeroSumGame(SortedGame game) {
		//TODO
	}

	@Autowired
	public void setGameRepository(GameRepository gameRepository) {
	    this.gameRepository = gameRepository;
	}
}
