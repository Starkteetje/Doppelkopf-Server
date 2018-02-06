package doko.rest;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import doko.DokoConstants;
import doko.database.game.Game;
import doko.database.player.Player;
import doko.database.round.Round;
import doko.database.token.Token;
import doko.database.user.User;
import doko.lineup.LineUp;
import doko.lineup.UnnamedLineUp;

@RestController
public class PostController extends RequestController {

	@RequestMapping(value = DokoConstants.LOGIN_PAGE_LOCATION, method = RequestMethod.POST) //TODO does this need protection from CSRF?
	public ResponseEntity<String> loginUser(HttpServletRequest request, HttpServletResponse response, 
			@RequestParam(value = "username") String username, @RequestParam(value = "password") String password,
			@RequestParam(value = "remember_user", defaultValue = "false") String keepLoggedIn) {
		Optional<User> user = userService.getUserByName(username);
		if (!user.isPresent()) {
			return ErrorPageController.getUnauthorizedPage();
		}

		String storedPw = user.get().getPassword();
		Long userId = user.get().getId();
		if (BCrypt.checkpw(password, storedPw)) {
			request.getSession().setAttribute(DokoConstants.SESSION_USER_ID_ATTRIBUTE_NAME, userId);

			if (keepLoggedIn.equals("on")) {
				Token token = tokenService.generateNewToken(user.get());
				Cookie rememberCookie = new Cookie(DokoConstants.LOGIN_COOKIE_NAME, token.getTokenValue());//TODO mark cookie as secure if HTTPS possible
				rememberCookie.setMaxAge(60 * 60 * 24 * 30); // Cookie is stored for 30 days
				response.addCookie(rememberCookie);
			}

			redirectTo(response, DokoConstants.INDEX_PAGE_LOCATION);
			return new ResponseEntity<>(HttpStatus.OK);
		}
		return ErrorPageController.getUnauthorizedPage();
	}

	@RequestMapping(value = DokoConstants.PROFILE_PAGE_LOCATION, method = RequestMethod.POST) //TODO need protection from CSRF
	public ResponseEntity<String> deleteAllLoginTokens(HttpServletRequest request, HttpServletResponse response) {
		Optional<User> user = getLoggedInUser(request);
		if (!user.isPresent()) {
			return ErrorPageController.getUnauthorizedPage();
		}

		Long userId = user.get().getId();
		boolean deleted = tokenService.deleteTokensOfUser(userId);
		if (deleted) {
			setSuccess(request, "Die Gültigkeit aller Login-Tokens wurde widerrufen.");
			redirectTo(response, DokoConstants.PROFILE_PAGE_LOCATION);
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			setError(request, "Fehler beim Löschen der Login-Token.");
			redirectTo(response, DokoConstants.PROFILE_PAGE_LOCATION);
			return new ResponseEntity<>(HttpStatus.OK);
		}
	}

	@RequestMapping(value = DokoConstants.ADD_ROUNDS_PAGE_LOCATION, method = RequestMethod.POST) //doesnt need CSRF prot now, because only send via app
	public ResponseEntity<String> reportGameWithRounds(String someJSONThingy, String token) {
		if (tokenService.isTokenValid(token)) {
			//TODO parse JSON
			List<Round> rounds = parseJSONForRounds(someJSONThingy);
			List<Player> players = parseJSONForPlayers(someJSONThingy);
			Game game = gameService.createGameFromRounds(rounds, players);//TODO saving game gives gameId for Rounds
			boolean roundsAdded = addRounds(rounds);
			boolean gameAdded = addGame(game);
			if (!gameAdded && !roundsAdded) {
				//TODO
			} else if (!gameAdded || !roundsAdded) {
				//TODO
			} else {
				//TODO
			}
			return new ResponseEntity<>(HttpStatus.OK); //TODO
		} else {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
	}

	private List<Player> parseJSONForPlayers(String someJSONThingy) {
		// TODO Auto-generated method stub
		return null;
	}

	private List<Round> parseJSONForRounds(String someJSONThingy) {
		// TODO Auto-generated method stub
		return null;
	}

	@RequestMapping(value = DokoConstants.ADD_GAME_PAGE_LOCATION, method = RequestMethod.POST) //TODO need protection from CSRF
	public ResponseEntity<String> reportGame(HttpServletRequest request,
			HttpServletResponse response, @RequestParam(value = "id1") String id1,
			@RequestParam(value = "id2") String id2, @RequestParam(value = "id3") String id3,
			@RequestParam(value = "id4") String id4, @RequestParam(value = "score1") String score1,
			@RequestParam(value = "score2") String score2, @RequestParam(value = "score3") String score3,
			@RequestParam(value = "score4") String score4, @RequestParam(value = "date") String date) {
		Optional<User> user = getLoggedInUser(request);
		if (user.isPresent()) {
			Long submitterId = user.get().getId();
			SimpleDateFormat sdf = new SimpleDateFormat(DokoConstants.INPUT_DATE_FORMAT, Locale.GERMAN);
			Game game;
			try {
				game = new Game(id1, score1, id2, score2, id3, score3, id4, score4, submitterId, sdf.parse(date));
			} catch (Exception e) {
				setError(request, "Das Spiel konnte nicht gespeichert werden. Versuche es erneut oder kontaktiere den Admin.");
				redirectTo(response, DokoConstants.ADD_GAME_PAGE_LOCATION);
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			boolean wasAdded = addGame(game);
			if (wasAdded) {
				setSuccess(request, "Spiel erfolgreich gespeichert.");
				LineUp lineUp = new UnnamedLineUp(id1, id2, id3, id4);
				redirectTo(response, DokoConstants.LINE_UP_PAGE_LOCATION + "?lineup=" + lineUp.getLineUpString());
			} else {
				setError(request, "Das Spiel konnte nicht gespeichert werden. Versuche es erneut oder kontaktiere den Admin.");
				redirectTo(response, DokoConstants.ADD_GAME_PAGE_LOCATION);
			}
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return ErrorPageController.getUnauthorizedPage();
		}
	}

	private boolean addGame(Game game) {
		game = gameService.addGame(game);
		//Check whether game was added
		Long id = game.getId();
		return id != null;
	}

	private boolean addRounds(Iterable<Round> rounds) {
		rounds = roundService.addRounds(rounds);
		for (Round round : rounds) {
			if (round.getId() == null) {
				// To avoid inconsistencies, delete all rounds, if a single save failed
				roundService.delete(rounds);
				return false;
			}
		}
		return true;
	}

	@RequestMapping(value = DokoConstants.ADD_PLAYER_PAGE_LOCATION, method = RequestMethod.POST) //TODO need protection from CSRF
	public ResponseEntity<String> addPlayer(HttpServletRequest request,
			HttpServletResponse response, @RequestParam(value = "name") String playerName) {
		if (isUserLoggedIn(request)) {
			boolean added = playerService.addPlayer(playerName);
			if (added) {
				setSuccess(request, "Spieler erfolgreich hinzugefügt.");
				redirectTo(response, DokoConstants.ADD_GAME_PAGE_LOCATION);
				return new ResponseEntity<>(HttpStatus.OK);
			} else {
				setError(request, "Fehler beim Hinzufügen des Spielers. Prüfe, ob bereits ein Spieler mit diesem Namen existiert.");
				redirectTo(response, DokoConstants.ADD_GAME_PAGE_LOCATION);
			}
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return ErrorPageController.getUnauthorizedPage();
		}
	}

	private void redirectTo(HttpServletResponse response, String destination) {
		try {
			response.sendRedirect(destination);
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
	}
}