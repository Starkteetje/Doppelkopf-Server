package doko.rest;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import doko.DokoConstants;
import doko.database.game.Game;
import doko.database.game.SortedGame;
import doko.database.player.Player;
import doko.database.round.Round;
import doko.database.user.User;
import doko.lineup.LineUp;
import doko.lineup.NamedLineUp;
import doko.lineup.UnnamedLineUp;
import doko.velocity.HtmlProvider;

@Controller
public class GetController extends RequestController {

	@GetMapping(value = DokoConstants.INDEX_PAGE_LOCATION, produces = "text/html")
	public ResponseEntity<String> getIndex(HttpServletRequest request) {
		return displayLineUp(request, DokoConstants.DEFAULT_LINEUP_STRING);
	}

	@GetMapping(value = DokoConstants.LINE_UP_PAGE_LOCATION, produces = "text/html")
	public ResponseEntity<String> displayLineUp(HttpServletRequest request,
			@RequestParam(value = "lineup", defaultValue = DokoConstants.DEFAULT_LINEUP_STRING) String lineUpString) {
		boolean isLoggedIn = isUserLoggedIn(request);
		String error = consumeErrorMessage(request);
		String success = consumeSuccessMessage(request);
		String lineUpRules = getRules(lineUpString);
		boolean isMoneyLineUp = lineUpString.equals(DokoConstants.DEFAULT_LINEUP_STRING); //TODO
		NamedLineUp lineUp = playerService.getNamedLineUp(lineUpString);
		List<SortedGame> lineUpGames = gameService.getGamesForLineUp(lineUp);

		HtmlProvider velocity = new HtmlProvider(gameService, playerService, roundService, isLoggedIn);
		return new ResponseEntity<>(
				velocity.getDisplayLineUpPageHtml(error, success, lineUpRules, lineUp, lineUpGames, isMoneyLineUp), HttpStatus.OK);

	}

	@GetMapping(value = DokoConstants.GAME_PAGE_LOCATION, produces = "text/html")
	public ResponseEntity<String> displayGame(HttpServletRequest request,
			@RequestParam(value = "id") String gameId) {
		boolean isLoggedIn = isUserLoggedIn(request);
		String error = consumeErrorMessage(request);
		String success = consumeSuccessMessage(request);
		Optional<Game> game = gameService.getGameByUniqueId(gameId);
		if (!game.isPresent()) {
			return new ResponseEntity<>(
					"Spiel nicht vorhanden", HttpStatus.NOT_FOUND);
		}
		Date date = game.get().getDate();
		List<Round> rounds = roundService.getRoundsByUniqueGameId(gameId);
		NamedLineUp lineUp = playerService.getNamedLineUp(new SortedGame(game.get()).getLineUp());

		HtmlProvider velocity = new HtmlProvider(gameService, playerService, roundService, isLoggedIn);
		return new ResponseEntity<>(
				velocity.getGamePageHtml(error, success, lineUp, rounds, date), HttpStatus.OK);
	}

	@GetMapping(value = DokoConstants.LOGIN_PAGE_LOCATION, produces = "text/html")
	public ResponseEntity<String> getLoginPage(HttpServletRequest request) {
		boolean isLoggedIn = isUserLoggedIn(request);
		String error = consumeErrorMessage(request);
		String success = consumeSuccessMessage(request);

		HtmlProvider velocity = new HtmlProvider(gameService, playerService, roundService, isLoggedIn);
		return new ResponseEntity<>(velocity.getLoginPageHtml(error, success),
				HttpStatus.OK);
	}

	@GetMapping(value = DokoConstants.ADD_GAME_PAGE_LOCATION, produces = "text/html")
	public ResponseEntity<String> getReportingPage(HttpServletRequest request) {
		boolean isLoggedIn = isUserLoggedIn(request);
		String error = consumeErrorMessage(request);
		String success = consumeSuccessMessage(request);
		List<Player> players = playerService.getAllPlayers();

		HtmlProvider velocity = new HtmlProvider(gameService, playerService, roundService, isLoggedIn);
		return new ResponseEntity<>(
				velocity.getReportingPageHtml(error, success, players), HttpStatus.OK);
	}

	@GetMapping(value = DokoConstants.PROFILE_PAGE_LOCATION, produces = "text/html")
	public ResponseEntity<String> getProfile(HttpServletRequest request,
			@RequestParam(value = "lineup", defaultValue = DokoConstants.DEFAULT_LINEUP_STRING) String lineUpString) {
		boolean isLoggedIn = isUserLoggedIn(request);
		String error = consumeErrorMessage(request);
		String success = consumeSuccessMessage(request);
		Optional<User> user = getLoggedInUser(request);

		if (user.isPresent()) {
			HtmlProvider velocity = new HtmlProvider(gameService, playerService, roundService, isLoggedIn);
			return new ResponseEntity<>(
					velocity.getProfilePageHtml(error, success, user.get()), HttpStatus.OK);
		}
		return ErrorPageController.getUnauthorizedPage();
	}

	@GetMapping(value = DokoConstants.PLAYER_STATS_PAGE_LOCATION, produces = "text/html")
	public ResponseEntity<String> getPlayer(HttpServletRequest request, HttpServletResponse response, @RequestParam(value ="id") String playerIdString) {
		boolean isLoggedIn = isUserLoggedIn(request);
		String error = consumeErrorMessage(request);
		String success = consumeSuccessMessage(request);
		Long playerId;
		try {
			playerId = Long.parseLong(playerIdString);
		} catch (NumberFormatException e) {
			return ErrorPageController.getBadRequestPage();
		}
		Optional<Player> player = playerService.getPlayer(playerId);
		if (player.isPresent()) {
			List<SortedGame> games = gameService.getGamesForPlayer(player.get());
			HtmlProvider velocity = new HtmlProvider(gameService, playerService, roundService, isLoggedIn);
			return new ResponseEntity<>(
					velocity.getPlayerPageHtml(error, success, player.get(), games), HttpStatus.OK);
		} else {
			return ErrorPageController.getPageNotFoundPage();
		}
	}

	@GetMapping(value = "/games", produces = "application/json")
	public ResponseEntity<List<SortedGame>> getGames() {
		return new ResponseEntity<>(gameService.getValidGamesOrdered(), HttpStatus.OK);
	}

	@GetMapping(value = "/players", produces = "application/json")
	public ResponseEntity<List<Player>> getPlayers() {
		return new ResponseEntity<>(playerService.getAllPlayers(), HttpStatus.OK);
	}

	@GetMapping(value = "/lineupgames", produces = "application/json")
	public ResponseEntity<List<List<Object>>> getLineUpGamesAsJSON(
			@RequestParam(value = "lineup", defaultValue = DokoConstants.DEFAULT_LINEUP_STRING) String lineUpString) {
		LineUp lineUp = new UnnamedLineUp(lineUpString);
		return new ResponseEntity<>(getLineUpGames(lineUp), HttpStatus.OK);
	}
}
