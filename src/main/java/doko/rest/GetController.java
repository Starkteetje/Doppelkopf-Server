package doko.rest;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import doko.DokoConstants;
import doko.database.game.SortedGame;
import doko.database.player.Player;
import doko.database.user.User;
import doko.lineup.LineUp;
import doko.lineup.NamedLineUp;
import doko.lineup.UnnamedLineUp;
import doko.velocity.HtmlProvider;

@RestController
public class GetController extends RequestController {

	@GetMapping(value = "/games")
	public ResponseEntity<List<SortedGame>> getGames() {
		return new ResponseEntity<>(gameService.getValidGames(), HttpStatus.OK);
	}

	@GetMapping(value = "/players")
	public ResponseEntity<List<Player>> getPlayers() {
		return new ResponseEntity<>(playerService.getAllPlayers(), HttpStatus.OK);
	}

	@GetMapping(value = "/", produces = "text/html")
	public ResponseEntity<String> getIndex(HttpServletRequest request) {
		return displayLineUp(request, DokoConstants.DEFAULT_LINEUP_STRING);
	}

	@GetMapping(value = "/lineupgames", produces = "application/json")
	public ResponseEntity<List<List<Object>>> getLineUpGamesAsJSON(
			@RequestParam(value = "lineup", defaultValue = DokoConstants.DEFAULT_LINEUP_STRING) String lineUpString) {
		LineUp lineUp = new UnnamedLineUp(lineUpString);
		return new ResponseEntity<>(getLineUpGames(lineUp), HttpStatus.OK);
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

		HtmlProvider velocity = new HtmlProvider(gameService, playerService, tokenService, isLoggedIn);
		return new ResponseEntity<>(velocity.getLoginPageHtml(errors, successes),
				HttpStatus.OK);
	}

	@GetMapping(value = "/report", produces = "text/html")
	public ResponseEntity<String> getReportingPage(HttpServletRequest request) {
		boolean isLoggedIn = isUserLoggedIn(request);
		String errors = ""; // TODO
		String successes = ""; // TODO
		List<Player> players = playerService.getAllPlayers();

		HtmlProvider velocity = new HtmlProvider(gameService, playerService, tokenService, isLoggedIn);
		return new ResponseEntity<>(
				velocity.getReportingPageHtml(errors, successes, players), HttpStatus.OK);
	}

	@GetMapping(value = "/lineup", produces = "text/html")
	public ResponseEntity<String> displayLineUp(HttpServletRequest request,
			@RequestParam(value = "lineup", defaultValue = DokoConstants.DEFAULT_LINEUP_STRING) String lineUpString) {
		boolean isLoggedIn = isUserLoggedIn(request);
		String errors = ""; // TODO
		String successes = ""; // TODO
		String lineUpRules = getRules(lineUpString);
		boolean isMoneyLineUp = lineUpString.equals(DokoConstants.DEFAULT_LINEUP_STRING); //TODO
		NamedLineUp lineUp = playerService.getNamedLineUp(lineUpString);
		List<SortedGame> lineUpGames = gameService.getGamesForLineUp(lineUp);

		HtmlProvider velocity = new HtmlProvider(gameService, playerService, tokenService, isLoggedIn);
		return new ResponseEntity<>(
				velocity.getDisplayLineUpPageHtml(errors, successes, lineUpRules, lineUp, lineUpGames, isMoneyLineUp), HttpStatus.OK);

	}

	@GetMapping(value = "/profile", produces = "text/html")
	public ResponseEntity<String> getProfile(HttpServletRequest request,
			@RequestParam(value = "lineup", defaultValue = DokoConstants.DEFAULT_LINEUP_STRING) String lineUpString) {
		boolean isLoggedIn = isUserLoggedIn(request);
		String errors = ""; // TODO
		String successes = ""; // TODO
		Optional<User> user = getLoggedInUser(request);

		if (user.isPresent()) {
			HtmlProvider velocity = new HtmlProvider(gameService, playerService, tokenService, isLoggedIn);
			return new ResponseEntity<>(
					velocity.getProfilePageHtml(errors, successes, user.get()), HttpStatus.OK);
		}
		return ErrorPageController.getUnauthorizedPage();
	}

	@RequestMapping(value = "logout", method = RequestMethod.GET)
	public ResponseEntity<String> logoutUser(HttpServletRequest request, HttpServletResponse response) {
		request.getSession().removeAttribute(DokoConstants.SESSION_LOGIN_STATUS_ATTRIBUTE_NAME);
		Cookie rememberCookie = new Cookie(DokoConstants.LOGIN_COOKIE_NAME, "");
		rememberCookie.setMaxAge(0); // Delete Cookie
		response.addCookie(rememberCookie);

		try {
			response.sendRedirect("/");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
