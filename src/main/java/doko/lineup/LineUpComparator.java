package doko.lineup;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import doko.database.game.GameService;
import doko.database.game.SortedGame;

public class LineUpComparator implements Comparator<LineUp> {

	private GameService gameService;

	public LineUpComparator(GameService gameService) {
		this.gameService = gameService;
	}

	// Sort lineups by number of games, or if equal, latest play date (both
	// descending)
	public int compare(LineUp l1, LineUp l2) {
		return -compareAscending(l1, l2);
	}

	public int compareAscending(LineUp l1, LineUp l2) {
		List<SortedGame> games1 = gameService.getGamesForLineUp(l1);
		List<SortedGame> games2 = gameService.getGamesForLineUp(l2);
		int numberOfGames1 = games1.size();
		int numberOfGames2 = games2.size();

		if (numberOfGames1 < numberOfGames2) {
			return -1;
		}
		if (numberOfGames1 > numberOfGames2) {
			return 1;
		}

		return latestPlayDate(games1).compareTo(latestPlayDate(games2));
	}

	private Date latestPlayDate(List<SortedGame> games) {
		return games.stream()
				.map(SortedGame::getDate)
				.sorted()
				.limit(1)
				.collect(Collectors.toList())
				.get(0);
	}

}
