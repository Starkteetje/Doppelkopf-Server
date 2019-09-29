package doko.rest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import doko.DokoConstants;
import doko.database.game.GameService;
import doko.database.game.SortedGame;
import doko.database.player.Player;
import doko.database.player.PlayerService;
import doko.database.round.RoundService;
import doko.database.rules.Rules;
import doko.database.rules.RulesService;
import doko.database.token.Token;
import doko.database.token.TokenService;
import doko.database.user.User;
import doko.database.user.UserService;
import doko.lineup.LineUp;
import doko.lineup.NamedLineUp;
import doko.lineup.UnnamedLineUp;

public class RequestController extends DokoController {

	@Autowired
	protected GameService gameService;
	@Autowired
	protected PlayerService playerService;
	@Autowired
	protected RoundService roundService;
	@Autowired
	protected RulesService rulesService;
	@Autowired
	protected TokenService tokenService;
	@Autowired
	protected UserService userService;

	public Optional<User> getLoggedInUser(HttpServletRequest request) {
		Optional<Long> userId = getLoggedInUserId(request);
		if (userId.isPresent()) {
			return userService.getUser(userId.get());
		}
		return Optional.ofNullable(null);
	}

	public boolean isUserLoggedIn(HttpServletRequest request) {
		Optional<User> loggedInUser = getLoggedInUser(request);
		return loggedInUser.isPresent();
	}

	private Optional<Long> getLoggedInUserId(HttpServletRequest request) {
		Object userId = request.getSession().getAttribute(DokoConstants.SESSION_USER_ID_ATTRIBUTE_NAME);
		if (userId == null) {
			return loginUserByLoginToken(request);
		}
		return Optional.of((Long) userId);
	}

	private Optional<Long> loginUserByLoginToken(HttpServletRequest request) {
		Cookie rememberCookie = getRememberCookie(request);
		if (rememberCookie != null) {
			String tokenValue = rememberCookie.getValue();
			Optional<Token> token = tokenService.getTokenByValue(tokenValue);
			if (token.isPresent()) {
				Long userId = token.get().getUserId();
				request.getSession().setAttribute(DokoConstants.SESSION_USER_ID_ATTRIBUTE_NAME, userId);
				return Optional.of(userId);
			}
		}
		return Optional.ofNullable(null);
	}

	protected Cookie getRememberCookie(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(DokoConstants.LOGIN_COOKIE_NAME)) {
					return cookie;
				}
			}
		}
		return null;
	}

	protected void setRememberCookie(HttpServletResponse response, String cookieValue) {
		// SameSite attribute is not supported by Java cookie, thus via header
		//remember_user=TOKENVALUE; Max-Age=31536000; Expires=Mon, 28-Sep-2020 08:36:04 GMT; Secure; HttpOnly
		int maxAge = 60 * 60 * 24 * 365;
		Date expiryDate= new Date();
		expiryDate.setTime (expiryDate.getTime() + maxAge);
		DateFormat df = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss zzz", Locale.US);
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		response.setHeader("Set-Cookie", String.format("%s=%s; Max-Age=%d; Expires=%s; SameSite=strict; Secure; HttpOnly;", DokoConstants.LOGIN_COOKIE_NAME, cookieValue, maxAge, df.format(expiryDate)));
	}

	protected void deleteRememberCookie(HttpServletResponse response) {
		Cookie deleteCookie = new Cookie(DokoConstants.LOGIN_COOKIE_NAME, "");
		deleteCookie.setMaxAge(0); // Delete Cookie
		response.addCookie(deleteCookie);
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
			return rules.get().getText();
		}
		return "";
	}

	public String getRules(String lineUpString) {
		LineUp lineUp = new UnnamedLineUp(lineUpString);
		return getRules(lineUp);
	}

	protected void setError(HttpServletRequest request, String error) {
		request.getSession().setAttribute(DokoConstants.SESSION_ERROR_MESSAGE_ATTRIBUTE_NAME, error);
	}

	protected void setSuccess(HttpServletRequest request, String success) {
		request.getSession().setAttribute(DokoConstants.SESSION_SUCCESS_MESSAGE_ATTRIBUTE_NAME, success);
	}

	protected String consumeErrorMessage(HttpServletRequest request) {
		String errorString;
		Object error = request.getSession().getAttribute(DokoConstants.SESSION_ERROR_MESSAGE_ATTRIBUTE_NAME);
		if (error == null) {
			errorString = "";
		} else {
			errorString = (String) error;
		}
		request.getSession().removeAttribute(DokoConstants.SESSION_ERROR_MESSAGE_ATTRIBUTE_NAME);
		return errorString;
	}

	protected String consumeSuccessMessage(HttpServletRequest request) {
		String successString;
		Object success = request.getSession().getAttribute(DokoConstants.SESSION_SUCCESS_MESSAGE_ATTRIBUTE_NAME);
		if (success == null) {
			successString = "";
		} else {
			successString = (String) success;
		}
		request.getSession().removeAttribute(DokoConstants.SESSION_SUCCESS_MESSAGE_ATTRIBUTE_NAME);
		return successString;
	}
}
