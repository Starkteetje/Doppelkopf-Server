package doko.rest;

import doko.database.player.Player;
import doko.database.player.PlayerService;
import doko.database.rules.Rules;
import doko.database.rules.RulesService;
import doko.database.token.TokenService;
import doko.database.user.User;
import doko.database.user.UserService;
import doko.lineup.LineUp;
import doko.lineup.UnnamedLineUp;
import doko.velocity.HtmlProvider;
import doko.DokoConstants;
import doko.database.game.GameService;
import doko.database.game.SortedGame;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class DokoRestController {

	private GameService gameService;
	private PlayerService playerService;
	private RulesService rulesService;
	private TokenService tokenService;
	private UserService userService;

	public DokoRestController() {
	}

	public DokoRestController(GameService gameService, PlayerService playerService, RulesService rulesService,
			TokenService tokenService, UserService userService) {
		this.gameService = gameService;
		this.playerService = playerService;
		this.rulesService = rulesService;
		this.tokenService = tokenService;
		this.userService = userService;
	}

	@GetMapping(value = "/games")
	public ResponseEntity<List<SortedGame>> getGames() {
		return new ResponseEntity<>(gameService.getValidGames(), HttpStatus.OK);
	}

	@GetMapping(value = "/players")
	public ResponseEntity<List<Player>> getPlayers() {
		return new ResponseEntity<>(playerService.getAllPlayers(), HttpStatus.OK);
	}

	public ResponseEntity<List<List<String>>> getLineUpGames(LineUp lineUp) {
		List<SortedGame> games = gameService.getGamesForLineUp(lineUp);
		List<List<String>> gamesScores = games.stream()
				.map(SortedGame::getScoresWithDate)
				.collect(Collectors.toList());
		List<String> names = playerService.getPlayerNames(lineUp);

		List<List<String>> namesAndGames = new ArrayList<>();
		namesAndGames.add(names);
		namesAndGames.addAll(gamesScores);

		return new ResponseEntity<>(namesAndGames, HttpStatus.OK);
	}

	@GetMapping(value = "/", produces = "application/json")
	public ResponseEntity<List<List<String>>> getIndex() {
		LineUp lineUp = new UnnamedLineUp("1,2,3,4");
		return getLineUpGames(lineUp);
	}

	@GetMapping(value = "/lineupgames", produces = "application/json")
	public ResponseEntity<List<List<String>>> getLineUpGames(
			@RequestParam(value = "lineup", defaultValue = "1,2,3,4") String lineUpString) {
		LineUp lineUp = new UnnamedLineUp(lineUpString);
		return getLineUpGames(lineUp);
	}

	@GetMapping(value = "/user", produces = "application/json")
	public ResponseEntity<Map<String, String>> getUser(@RequestParam(value = "id") String idString,
			@RequestHeader(value = "token", defaultValue = "") String token) {
		if (tokenService.isTokenValid(token)) {
			Optional<User> user = userService.getUser(idString);
			if (user.isPresent()) {
				return new ResponseEntity<>(user.get().asMap(), HttpStatus.OK);
			}
		}
		return ErrorPageController.getUnauthorizedPage();
	}

	@GetMapping(value = "/login", produces = "text/html")
	public ResponseEntity<String> getLoginPage(HttpServletRequest request) {
		boolean isLoggedIn = isUserLoggedIn(request);
		String errors = ""; // TODO
		String successes = ""; // TODO

		HtmlProvider velocity = new HtmlProvider(gameService, playerService, tokenService);
		return new ResponseEntity<>(velocity.getLoginPageHtml(isLoggedIn, errors, successes),
				HttpStatus.OK);
	}

	@GetMapping(value = "/report", produces = "text/html")
	public ResponseEntity<String> getReportingPage(HttpServletRequest request) {
		boolean isLoggedIn = isUserLoggedIn(request);
		String errors = ""; // TODO
		String successes = ""; // TODO
		List<Player> players = playerService.getAllPlayers();

		HtmlProvider velocity = new HtmlProvider(gameService, playerService, tokenService);
		return new ResponseEntity<>(
				velocity.getReportingPageHtml(isLoggedIn, errors, successes, players), HttpStatus.OK);
	}

	public ResponseEntity<String> getRules(LineUp lineUp) {
		Optional<Rules> rules = rulesService.getRulesOfLineUp(lineUp);
		if (rules.isPresent()) {
			return new ResponseEntity<>(rules.get().getRules(), HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	private boolean isUserLoggedIn(HttpServletRequest request) {
		Object loginStatus = request.getSession().getAttribute(DokoConstants.SESSION_LOGIN_STATUS_ATTRIBUTE_NAME);
		return loginStatus != null && ((String) loginStatus).equals("true");
	}

	@Autowired
	public void setGameService(GameService gameService) {
		this.gameService = gameService;
	}

	@Autowired
	public void setPlayerService(PlayerService playerService) {
		this.playerService = playerService;
	}

	@Autowired
	public void setRulesService(RulesService rulesService) {
		this.rulesService = rulesService;
	}

	@Autowired
	public void setTokenService(TokenService tokenService) {
		this.tokenService = tokenService;
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}
