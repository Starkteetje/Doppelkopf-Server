package doko.rest;

import doko.database.player.Player;
import doko.database.player.PlayerService;
import doko.database.rules.RulesService;
import doko.database.token.TokenService;
import doko.database.user.User;
import doko.database.user.UserService;
import doko.lineup.LineUp;
import doko.lineup.NamedLineUp;
import doko.lineup.UnnamedLineUp;
import doko.velocity.HtmlProvider;
import doko.DokoException;
import doko.database.game.Game;
import doko.database.game.GameService;
import doko.database.game.SortedGame;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
	private HtmlProvider velocity = new HtmlProvider();

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
			@RequestHeader(value = "token", defaultValue = "") String token) throws DokoException {
		if (tokenService.isTokenValid(token)) {
			User user = userService.getUser(idString);
			return new ResponseEntity<>(user.asMap(), HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // TODO return error page
	}

	@GetMapping(value = "/login", produces = "text/html")
	public ResponseEntity<String> getLoginPage() {
		NamedLineUp[] topLineUps = playerService.getNamedLineUps(gameService.getTopLineUps());
		NamedLineUp[] nonTopLineUps = playerService.getNamedLineUps(gameService.getNonTopLineUps());
		String errors = ""; // TODO
		String successes = ""; // TODO
		return new ResponseEntity<>(velocity.getLoginPageHtml(topLineUps, nonTopLineUps, errors, successes),
				HttpStatus.OK);
	}

	@GetMapping(value = "/report", produces = "text/html")
	public ResponseEntity<String> getReportingPage() {
		NamedLineUp[] topLineUps = playerService.getNamedLineUps(gameService.getTopLineUps());
		NamedLineUp[] nonTopLineUps = playerService.getNamedLineUps(gameService.getNonTopLineUps());
		String errors = ""; // TODO
		String successes = ""; // TODO
		List<Player> players = playerService.getAllPlayers();
		return new ResponseEntity<>(
				velocity.getReportingPageHtml(topLineUps, nonTopLineUps, errors, successes, players), HttpStatus.OK);
	}

	@RequestMapping(value = "post", method = RequestMethod.POST)
	public ResponseEntity<String> testPost(@RequestParam(value = "id") String id) {

		return new ResponseEntity<>(id, HttpStatus.OK); // TODO return error page
	}

	@RequestMapping(value = "report", method = RequestMethod.POST)
	public ResponseEntity<List<List<String>>> reportNewGame(@RequestParam(value = "id1") String id1,
			@RequestParam(value = "id2") String id2, @RequestParam(value = "id3") String id3,
			@RequestParam(value = "id4") String id4, @RequestParam(value = "score1") String score1,
			@RequestParam(value = "score2") String score2, @RequestParam(value = "score3") String score3,
			@RequestParam(value = "score4") String score4, @RequestParam(value = "token") String token) {
		Long submitterId = tokenService.getUserIdOfToken(token);
		if (submitterId == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // TODO return error page
		} else {
			Calendar calender = Calendar.getInstance();
			if (calender.get(Calendar.HOUR) < 12) {
				calender.add(Calendar.DAY_OF_YEAR, -1);
			}
			Game game = new Game(id1, score1, id2, score2, id3, score3, id4, score4, submitterId, calender.getTime());
			gameService.insertGame(game);
			LineUp lineUp = new UnnamedLineUp(id1, id2, id3, id4);
			return getLineUpGames(lineUp);
		}
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
