package doko;

import doko.database.player.Player;
import doko.database.user.User;
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
public class Doko {
	
	DokoService dokoService;
	
	@Autowired
	public void setDokoService(DokoService dokoService) {
		this.dokoService = dokoService;
	}
	
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
		return new ResponseEntity<>(dokoService.getValidGames(), HttpStatus.OK);
	}
	
	@GetMapping(value = "/players")
	public ResponseEntity<List<Player>> getPlayers() {
		return new ResponseEntity<>(dokoService.getAllPlayers(), HttpStatus.OK);
	}
	
	@GetMapping(value = "/gameplayers")
	public ResponseEntity<List<String>> getGamePlayers() {
		List<SortedGame> games = dokoService.getValidGames();
		List<List<String>> names = games.stream().map(dokoService::getPlayerNames).collect(Collectors.toList());
		List<String> argh = names.stream().map(List::toString).collect(Collectors.toList());
		
		return new ResponseEntity<>(argh, HttpStatus.OK);
	}
	
	public ResponseEntity<List<List<String>>> getLineUpGames(LineUp lineUp) {
		List<SortedGame> games = dokoService.getGamesForLineUp(lineUp);
		List<List<String>> gamesScores = games.stream()
				.map(SortedGame::getScores)
				.collect(Collectors.toList());
		List<String> names = dokoService.getPlayerNames(lineUp);
		
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
	
	@GetMapping(value = "/userinfo", produces = "application/json")
	public ResponseEntity<Map<String, String>> getUserInfo(@RequestParam(value = "id") String idString, @RequestHeader(value = "token") String token) throws DokoException {
		//TODO validate token
		User user = dokoService.getUser(idString);
		return new ResponseEntity<>(user.asMap(), HttpStatus.OK);
	}
	
	@GetMapping(value = "/velocity")
	public ResponseEntity<String> getHtml() {
		return new ResponseEntity<>(Velocity.getHtml(), HttpStatus.OK);
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
