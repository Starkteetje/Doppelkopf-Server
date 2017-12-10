package doko.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import doko.DokoConstants;
import doko.database.game.GameService;
import doko.database.game.SortedGame;
import doko.database.player.Player;
import doko.database.player.PlayerService;
import doko.database.rules.Rules;
import doko.database.rules.RulesService;
import doko.database.token.TokenService;
import doko.database.user.User;
import doko.database.user.UserService;
import doko.lineup.LineUp;
import doko.lineup.NamedLineUp;
import doko.lineup.UnnamedLineUp;

public class RequestController {

	@Autowired
	protected GameService gameService;
	@Autowired
	protected PlayerService playerService;
	@Autowired
	protected RulesService rulesService;
	@Autowired
	protected TokenService tokenService;
	@Autowired
	protected UserService userService;

	public boolean isUserLoggedIn(HttpServletRequest request) {
		Object loginStatus = request.getSession().getAttribute(DokoConstants.SESSION_LOGIN_STATUS_ATTRIBUTE_NAME);
		return loginStatus != null && ((String) loginStatus).equals("true");
	}

	public Optional<User> getLoggedInUser(HttpServletRequest request) {
		Object userId = request.getSession().getAttribute(DokoConstants.SESSION_USER_ID_ATTRIBUTE_NAME);
		try {
			return userService.getUser((Long) userId);
		} catch (Exception e) {
			return Optional.ofNullable(null);
		}
	}

	public List<List<Object>> getLineUpGames(LineUp lineUp) {
		NamedLineUp namedLineUp = new NamedLineUp(lineUp, playerService);

		List<Object> names = namedLineUp.getPlayers()
				.stream()
				.map(Player::getName)
				.collect(Collectors.toList());
		List<SortedGame> games = gameService.getGamesForLineUp(namedLineUp);
		List<List<Object>> gamesScores = games.stream()
				.map(SortedGame::getScoresWithDate)
				.collect(Collectors.toList());

		List<List<Object>> namesAndGames = new ArrayList<>();
		namesAndGames.add(names);
		namesAndGames.addAll(gamesScores);

		return namesAndGames;
	}

	public List<List<Object>> getLineUpGames(String lineUpString) {
		LineUp lineUp = new UnnamedLineUp(lineUpString);
		return getLineUpGames(lineUp);
	}

	public String getRules(LineUp lineUp) {
		Optional<Rules> rules = rulesService.getRulesOfLineUp(lineUp);
		if (rules.isPresent()) {
			return rules.get().getRules();
		}
		return "";
	}

	public String getRules(String lineUpString) {
		LineUp lineUp = new UnnamedLineUp(lineUpString);
		return getRules(lineUp);
	}
}
