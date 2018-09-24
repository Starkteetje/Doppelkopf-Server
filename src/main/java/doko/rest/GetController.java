package doko.rest;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

@RestController
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

		HtmlProvider velocity = new HtmlProvider(gameService, playerService, isLoggedIn);
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

		HtmlProvider velocity = new HtmlProvider(gameService, playerService, isLoggedIn);
		return new ResponseEntity<>(
				velocity.getGamePageHtml(error, success, lineUp, rounds, date), HttpStatus.OK);
	}

	@GetMapping(value = DokoConstants.LOGIN_PAGE_LOCATION, produces = "text/html")
	public ResponseEntity<String> getLoginPage(HttpServletRequest request) {
		boolean isLoggedIn = isUserLoggedIn(request);
		String error = consumeErrorMessage(request);
		String success = consumeSuccessMessage(request);

		HtmlProvider velocity = new HtmlProvider(gameService, playerService, isLoggedIn);
		return new ResponseEntity<>(velocity.getLoginPageHtml(error, success),
				HttpStatus.OK);
	}

	@GetMapping(value = DokoConstants.ADD_GAME_PAGE_LOCATION, produces = "text/html")
	public ResponseEntity<String> getReportingPage(HttpServletRequest request) {
		boolean isLoggedIn = isUserLoggedIn(request);
		String error = consumeErrorMessage(request);
		String success = consumeSuccessMessage(request);
		List<Player> players = playerService.getAllPlayers();

		HtmlProvider velocity = new HtmlProvider(gameService, playerService, isLoggedIn);
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
			HtmlProvider velocity = new HtmlProvider(gameService, playerService, isLoggedIn);
			return new ResponseEntity<>(
					velocity.getProfilePageHtml(error, success, user.get()), HttpStatus.OK);
		}
		return ErrorPageController.getUnauthorizedPage();
	}

	@RequestMapping(value = DokoConstants.LOGOUT_PAGE_LOCATION, method = RequestMethod.GET)
	public ResponseEntity<String> logoutUser(HttpServletRequest request, HttpServletResponse response) {//changes server stuff, so move to post. also CSRF
		request.getSession().removeAttribute(DokoConstants.SESSION_USER_ID_ATTRIBUTE_NAME);
		Cookie storedCookie = getRememberCookie(request);
		if (storedCookie != null) {
			tokenService.deleteTokenByValue(storedCookie.getValue());
		}
		Cookie deleteCookie = new Cookie(DokoConstants.LOGIN_COOKIE_NAME, "");
		deleteCookie.setMaxAge(0); // Delete Cookie
		response.addCookie(deleteCookie);

		try {
			response.sendRedirect("/");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping(value = "/games")
	public ResponseEntity<List<SortedGame>> getGames() {
		return new ResponseEntity<>(gameService.getValidGames(), HttpStatus.OK);
	}

	@GetMapping(value = "/players")
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
