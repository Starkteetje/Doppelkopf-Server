package doko.rest;

import doko.database.player.Player;
import doko.database.player.PlayerService;
import doko.database.rules.RulesService;
import doko.database.token.TokenService;
import doko.database.user.User;
import doko.database.user.UserService;
import doko.velocity.VelocityReturn;
import doko.velocity.VelocityTemplateHandler;
import doko.DokoException;
import doko.LineUp;
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
	private VelocityReturn velocity = new VelocityReturn();
	
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
	
	@GetMapping(value = "/gameplayers")
	public ResponseEntity<List<String>> getGamePlayers() {
		List<SortedGame> games = gameService.getValidGames();
		List<List<String>> names = games.stream().map(playerService::getPlayerNames).collect(Collectors.toList());
		List<String> argh = names.stream().map(List::toString).collect(Collectors.toList());
		
		return new ResponseEntity<>(argh, HttpStatus.OK);
	}
	
	public ResponseEntity<List<List<String>>> getLineUpGames(LineUp lineUp) {
		List<SortedGame> games = gameService.getGamesForLineUp(lineUp);
		List<List<String>> gamesScores = games.stream()
				.map(SortedGame::getScores)
				.collect(Collectors.toList());
		List<String> names = playerService.getPlayerNames(lineUp);
		
		List<List<String>> namesAndGames = new ArrayList<>();
		namesAndGames.add(names);
		namesAndGames.addAll(gamesScores);
		
		return new ResponseEntity<>(namesAndGames, HttpStatus.OK);
	}
	
	@GetMapping(value = "/lineupgames", produces = "application/json")
	public ResponseEntity<List<List<String>>> getLineUpGames(@RequestParam(value = "lineup", defaultValue = "1,2,3,4") String lineUpString) {
		LineUp lineUp = new LineUp(lineUpString);
		return getLineUpGames(lineUp);
	}
	
	@GetMapping(value = "/user", produces = "application/json")
	public ResponseEntity<Map<String, String>> getUser(@RequestParam(value = "id") String idString, @RequestHeader(value = "token") String token) throws DokoException {
		if (tokenService.isTokenValid(token)) {
			User user = userService.getUser(idString);
			return new ResponseEntity<>(user.asMap(), HttpStatus.OK);
		}
		return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED); //TODO test and proper response
	}
	
	@GetMapping(value = "/velocity")
	public ResponseEntity<String> getHtml() {
		return new ResponseEntity<>(velocity.getTestHtml(), HttpStatus.OK);
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
	
	/*
	@PostMapping(value = "/companies", consumes = "application/json")
    public ResponseEntity<String> prepareMailsForUser(@RequestHeader(value = "name", defaultValue = "") String name,
                                                      @RequestHeader(value = "email", defaultValue = "") String email,
                                                      @RequestHeader(value = "password", defaultValue = "") String password,
                                                      @RequestHeader(value = "smtpUrl", defaultValue = "") String smtpURL,
                                                      @RequestBody List<Long> companyIds) {

        List<Company> companies = companyService.getCompanysByID(companyIds);

        if (!StringUtils.isEmpty(password)) {
            //send mails, if password was given
            try {
                mailService.sendMail(name, email, password, smtpURL, companies);
            } catch (MailException e) {
                log.error("could not send mail", e);
                return new ResponseEntity<>("unable to create or send mails", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        User user = userService.saveUser(name, email, companies);
        return new ResponseEntity(user.getToken(), HttpStatus.OK);
    }
	
	@RequestMapping(method = RequestMethod.GET, value = "/user")
    public ResponseEntity<User> revisit(@RequestParam(value = "token") String token) {
        User user = userRepository.findByToken(token);
        if(user == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity(user, HttpStatus.OK);
        }
    }
    */
}
