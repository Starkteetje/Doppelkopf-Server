package doko.rest;

import doko.database.player.Player;
import doko.database.player.PlayerService;
import doko.database.rules.RulesService;
import doko.database.token.TokenService;
import doko.database.user.User;
import doko.database.user.UserService;
import doko.lineup.LineUp;
import doko.lineup.UnnamedLineUp;
import doko.velocity.HtmlProvider;
import doko.DokoException;
import doko.database.game.GameService;
import doko.database.game.SortedGame;

import java.util.ArrayList;
import java.util.Arrays;
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
	private ImageController imgController = new ImageController();
	
	@GetMapping(value = "/doko")
    public ResponseEntity<List<String>> getLineUp(@RequestParam(value = "lineup", defaultValue = "1,2,3,4") String lineUp) {

        String[] playerIds = lineUp.split(",");
        if (playerIds.length != 4) {
        	return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(Arrays.asList(playerIds), HttpStatus.OK);
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
	public ResponseEntity<List<List<String>>> getLineUpGames(@RequestParam(value = "lineup", defaultValue = "1,2,3,4") String lineUpString) {
		LineUp lineUp = new UnnamedLineUp(lineUpString);
		return getLineUpGames(lineUp);
	}
	
	@GetMapping(value = "/user", produces = "application/json")
	public ResponseEntity<Map<String, String>> getUser(@RequestParam(value = "id") String idString, @RequestHeader(value = "token", defaultValue = "") String token) throws DokoException {
		if (tokenService.isTokenValid(token)) {
			User user = userService.getUser(idString);
			return new ResponseEntity<>(user.asMap(), HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); //TODO return error page
	}
	
	@GetMapping(value = "/login", produces = "text/html")
	public ResponseEntity<String> getLoginPage() {
		LineUp[] topLineUps = gameService.getTopLineUps();
		LineUp[] nonTopLineUps = gameService.getNonTopLineUps();
		String error = ""; //TODO
		return new ResponseEntity<>(velocity.getLoginPageHtml(playerService.getNamedLineUps(topLineUps), playerService.getNamedLineUps(nonTopLineUps), error), HttpStatus.OK);
	}
	
	@GetMapping(value = "/style", produces = "text/css")
	public ResponseEntity<String> getCSS() {
		return new ResponseEntity<>(velocity.getCSS(), HttpStatus.OK);
	}
	
	@GetMapping(value = "/dokoblatt.png", produces = "image/png")
	public ResponseEntity<byte[]> getBackgroundPicture() {
		return new ResponseEntity<>(imgController.getBackgroundImage(), HttpStatus.INTERNAL_SERVER_ERROR);
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
