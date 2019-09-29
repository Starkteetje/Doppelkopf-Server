package doko.rest;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import doko.DokoConstants;
import doko.database.game.Game;
import doko.database.round.Round;
import doko.database.token.Token;
import doko.database.user.User;
import doko.lineup.LineUp;
import doko.lineup.UnnamedLineUp;
import doko.util.JSONHandler;
import doko.util.LoginCredentialsStruct;
import doko.util.PasswordHandler;
import doko.util.RoundStruct;

@Controller
public class PostController extends RequestController {

	@PostMapping(value = "/reportcsp")
	public void reportCSPViolation(HttpServletRequest request, HttpServletResponse response, @RequestBody String body) {
		System.out.println(body);
		//TODO logging
	}

	@PostMapping(value = DokoConstants.LOGIN_PAGE_LOCATION) //TODO needs protection from CSRF
	public ResponseEntity<String> loginUser(HttpServletRequest request, HttpServletResponse response, 
			@RequestParam(value = "username") String username, @RequestParam(value = "password") String password,
			@RequestParam(value = "remember_user", defaultValue = "false") String keepLoggedIn) {
		Optional<User> user = userService.getUserByName(username);
		if (!user.isPresent()) {
			return ErrorPageController.getUnauthorizedPage();
		}

		String storedPassword = user.get().getPassword();
		if (PasswordHandler.checkPassword(password, storedPassword)) {
			request.getSession().setAttribute(DokoConstants.SESSION_USER_ID_ATTRIBUTE_NAME, user.get().getId());

			if (keepLoggedIn.equals("on")) {
				Token token = tokenService.generateNewToken(user.get());
				setRememberCookie(response, token.getTokenValue());
			}

			redirectTo(response, DokoConstants.INDEX_PAGE_LOCATION);
			return new ResponseEntity<>(HttpStatus.OK);
		}
		return ErrorPageController.getUnauthorizedPage();
	}

	@GetMapping(value = DokoConstants.LOGOUT_PAGE_LOCATION)
	public ResponseEntity<String> logoutUser(HttpServletRequest request, HttpServletResponse response) {//TODO needs protection from CSRF
		request.getSession().removeAttribute(DokoConstants.SESSION_USER_ID_ATTRIBUTE_NAME);
		Cookie storedCookie = getRememberCookie(request);
		if (storedCookie != null) {
			tokenService.deleteTokenByValue(storedCookie.getValue());
		}
		deleteRememberCookie(response);

		try {
			response.sendRedirect("/");
		} catch (IOException e) {
			// TODO log
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value = DokoConstants.USER_CHANGE_PASSWORD_LOCATION) //TODO needs protection from CSRF
	public ResponseEntity<String> changePassword(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "oldpw") String oldPassword, @RequestParam(value = "newpw") String newPassword,
			@RequestParam(value = "newpw2") String newPassword2) {
		Optional<User> user = getLoggedInUser(request);
		if (!user.isPresent()) {
			return ErrorPageController.getUnauthorizedPage();
		}

		String storedPassword = user.get().getPassword();
		if (PasswordHandler.checkPassword(oldPassword, storedPassword)) {
			if (newPassword.equals(newPassword2)) {
				boolean changed = userService.changeUserPassword(user.get(), newPassword);
				if (changed) {
					setSuccess(request, "Passwort geändert!");
				} else {
					setError(request, "Fehler beim Ändern des Passwortes.");
				}
				redirectTo(response, DokoConstants.PROFILE_PAGE_LOCATION);
				return new ResponseEntity<>(HttpStatus.OK);
			}
			setError(request, "Die neuen Passwörter müssen übereinstimmen.");
			redirectTo(response, DokoConstants.PROFILE_PAGE_LOCATION);
			return new ResponseEntity<>(HttpStatus.OK);
		}
		setError(request, "Falsches Passwort!");
		redirectTo(response, DokoConstants.PROFILE_PAGE_LOCATION);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value = DokoConstants.API_LOGIN_LOCATION)
	public ResponseEntity<String> getToken(HttpServletRequest request, HttpServletResponse response,
			@RequestBody LoginCredentialsStruct credentials) {
		String username = credentials.getUsername();
		String password = credentials.getPassword();
		Optional<User> user = userService.getUserByName(username);
		if (!user.isPresent()) {
			return ErrorPageController.getUnauthorizedPage();
		}

		String storedPassword = user.get().getPassword();
		if (PasswordHandler.checkPassword(password, storedPassword)) {
			Token token = tokenService.generateNewToken(user.get());
			return new ResponseEntity<>(token.getTokenValue(), HttpStatus.OK);
		}
		return ErrorPageController.getUnauthorizedPage();
	}

	@PostMapping(value = DokoConstants.PROFILE_PAGE_LOCATION) //TODO need protection from CSRF
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

	@PostMapping(value = DokoConstants.API_ADD_GAME_LOCATION)
	public ResponseEntity<String> reportGameWithRounds(HttpServletRequest request, HttpServletResponse response,
			@RequestBody RoundStruct roundStruct) {
		String token = roundStruct.getToken();
		JSONObject gameJSON = roundStruct.getJson();
		if (tokenService.isTokenValid(token)) {
			List<Round> rounds;
			Game game;
			try {
				JSONHandler jsonHandler = new JSONHandler(gameJSON, playerService, tokenService, token);
				rounds = jsonHandler.getRounds();
				game = jsonHandler.getGame();
			} catch (JSONException | ParseException e) {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);//TODO log
			} catch (IOException e) {
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);//TODO log
			}

			// Check whether game with that ID already exists
			if (gameService.getGameByUniqueId(game.getUniqueGameId()).isPresent()) {
				return new ResponseEntity<>(HttpStatus.CONFLICT);//TODO log
			}

			boolean added = addGameAndRounds(game, rounds);
			if (added) {
				return new ResponseEntity<>(HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);//TODO log
		}
	}

	@PostMapping(value = DokoConstants.ADD_GAME_PAGE_LOCATION) //TODO need protection from CSRF
	public ResponseEntity<String> reportGame(HttpServletRequest request,
			HttpServletResponse response, @RequestParam(value = "id1") String id1,
			@RequestParam(value = "id2") String id2, @RequestParam(value = "id3") String id3,
			@RequestParam(value = "id4") String id4, @RequestParam(value = "score1") String score1,
			@RequestParam(value = "score2") String score2, @RequestParam(value = "score3") String score3,
			@RequestParam(value = "score4") String score4, @RequestParam(value = "date") String date) {
		Optional<User> user = getLoggedInUser(request);
		if (user.isPresent()) {
			Long submitterId = user.get().getId();
			SimpleDateFormat sdf = new SimpleDateFormat(DokoConstants.INPUT_DATE_FORMAT_WEBSITE, Locale.GERMAN);
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

	private boolean addGameAndRounds(Game game, Iterable<Round> rounds) {
		boolean gameAdded = addGame(game);
		if (!gameAdded) {
			return false; //TODO log
		} else {
			boolean roundsAdded = addRounds(rounds);
			if (!roundsAdded) {
				gameService.delete(game); // Delete game to ensure consistency
				//TODO log
				return false;
			} else {
				return true;
			}
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

	@PostMapping(value = DokoConstants.ADD_PLAYER_PAGE_LOCATION) //TODO need protection from CSRF
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
			//TODO log
		}
	}
}