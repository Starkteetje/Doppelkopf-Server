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
import doko.database.token.Token;
import doko.database.user.User;
import doko.lineup.LineUp;
import doko.lineup.UnnamedLineUp;

@RestController
public class PostController extends RequestController {

	@RequestMapping(value = "login", method = RequestMethod.POST) //TODO does this need protection from CSRF?
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
			Token token = tokenService.generateNewToken(user.get());

			//TODO mark cookie as secure if HTTPS possible
			if (keepLoggedIn.equals("on")) {
				Cookie rememberCookie = new Cookie(DokoConstants.LOGIN_COOKIE_NAME, token.getTokenValue());
				rememberCookie.setMaxAge(60 * 60 * 24 * 30); // Cookie is stored for 30 days
				response.addCookie(rememberCookie);
			}
			request.getSession().setAttribute(DokoConstants.SESSION_LOGIN_STATUS_ATTRIBUTE_NAME, "true");
			request.getSession().setAttribute(DokoConstants.SESSION_USER_ID_ATTRIBUTE_NAME, userId);

			try {
				response.sendRedirect("/");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new ResponseEntity<>(HttpStatus.OK);
		}
		return ErrorPageController.getUnauthorizedPage();
	}

	@RequestMapping(value = "profile", method = RequestMethod.POST) //TODO need protection from CSRF
	public ResponseEntity<String> deleteAllLoginTokens(HttpServletRequest request, HttpServletResponse response) {
		Optional<User> user = getLoggedInUser(request);
		if (!user.isPresent()) {
			return ErrorPageController.getUnauthorizedPage();
		}

		Long userId = user.get().getId();
		boolean deleted = tokenService.deleteTokensOfUser(userId);
		if (deleted) {
			String success = "Die Gültigkeit aller Login-Tokens wurde widerrufen.";
			return new ResponseEntity<>(HttpStatus.OK); //TODO
		} else {
			String error = "Fehler beim Löschen der Login-Token.";
			return ErrorPageController.getServerErrorPage(); //TODO
		}
	}

	@RequestMapping(value = "report", method = RequestMethod.POST) //TODO need protection from CSRF
	public ResponseEntity<List<List<Object>>> reportNewGame(@RequestParam(value = "id1") String id1,
			@RequestParam(value = "id2") String id2, @RequestParam(value = "id3") String id3,
			@RequestParam(value = "id4") String id4, @RequestParam(value = "score1") String score1,
			@RequestParam(value = "score2") String score2, @RequestParam(value = "score3") String score3,
			@RequestParam(value = "score4") String score4, @RequestParam(value = "date") String date,
			@RequestParam(value = "token") String token) {
		Long submitterId = tokenService.getUserIdOfToken(token);
		if (submitterId == null) {
			return ErrorPageController.getUnauthorizedPage();
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("E dd.MM.yyyy", Locale.GERMAN);
			Game game;
			try {
				game = new Game(id1, score1, id2, score2, id3, score3, id4, score4, submitterId, sdf.parse(date));
			} catch (Exception e) {
				// TODO wrong date format, id or score <- log
				e.printStackTrace();
				return ErrorPageController.getBadRequestPage();
			}
			gameService.insertGame(game);
			LineUp lineUp = new UnnamedLineUp(id1, id2, id3, id4);
			return new ResponseEntity<>(getLineUpGames(lineUp), HttpStatus.OK);
		}
	}
}