package doko.velocity;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.velocity.VelocityContext;

import com.google.gson.Gson;

import doko.DokoConstants;
import doko.database.game.GameService;
import doko.database.game.SortedGame;
import doko.database.player.Player;
import doko.database.player.PlayerService;
import doko.database.round.Round;
import doko.database.round.RoundService;
import doko.database.user.User;
import doko.lineup.NamedLineUp;

public class HtmlProvider {

	private GameService gameService;
	private PlayerService playerService;
	private RoundService roundService;
	private boolean isLoggedIn;

	public HtmlProvider(GameService gameService, PlayerService playerService, RoundService roundService, boolean isLoggedIn) {
		this.gameService = gameService;
		this.playerService = playerService;
		this.roundService = roundService;
		this.isLoggedIn = isLoggedIn;
	}

	private String getPageHtml(String error, String success, String mainHtml) {
		StringBuilder sb = new StringBuilder();
		sb.append(getHeader(error, success));
		sb.append(mainHtml);
		sb.append(getFooter());
		return sb.toString();
	}

	public String getLoginPageHtml(String error, String success) {
		String loginHtml = getLoginHtml();
		return getPageHtml(error, success, loginHtml);
	}

	private String getLoginHtml() {
		VelocityTemplateHandler ve = new VelocityTemplateHandler("templates/login.vm");
		VelocityContext context = new VelocityContext();

		return ve.getFilledTemplate(context);
	}

	public String getReportingPageHtml(String error, String success, List<Player> players) {
		String reportingHtml = getReportingHtml(players);
		return getPageHtml(error, success, reportingHtml);
	}

	private String getReportingHtml(List<Player> players) {
		VelocityTemplateHandler ve = new VelocityTemplateHandler("templates/report.vm");
		VelocityContext context = new VelocityContext();
		context.put("players", players);

		return ve.getFilledTemplate(context);
	}

	public String getPlayerPageHtml(String error, String success, Player player, List<SortedGame> games) {
		String playerHtml = getPlayerHtml(player, games);
		return getPageHtml(error, success, playerHtml);
	}

	private String getPlayerHtml(Player player, List<SortedGame> games) {
		VelocityTemplateHandler ve = new VelocityTemplateHandler("templates/player.vm");
		VelocityContext context = new VelocityContext();
		List<List<Round>> availabeRounds = new ArrayList<>();
		int maxRounds = 0;
		for (SortedGame game : games) {
			List<Round> rounds = roundService.getRoundsByUniqueGameId(game.getUniqueGameId());
			if (!rounds.isEmpty()) {
				availabeRounds.add(rounds);
				if (rounds.size() > maxRounds) {
					maxRounds = rounds.size();
				}
			}
		}
		String averageRoundJSON = getEncodedJSONForPlayerRoundsGraph(player, availabeRounds, maxRounds);
		String ticksJSON = getJSONForTicks(maxRounds); // NOTE the tick count is wrong, but graph still renders well. WONTFIX for now
		context.put("player", player);
		context.put("games", games);
		context.put("roundUrl", DokoConstants.GAME_PAGE_LOCATION);
		context.put("dataForRounds", averageRoundJSON);
		context.put("ticks", ticksJSON);
		context.put(Double.class.getSimpleName(), Double.class);
		context.put("doubleFormatter", new DecimalFormat("#.##"));
		context.put("dateFormatter", new SimpleDateFormat(DokoConstants.OUTPUT_DATE_FORMAT));

		return ve.getFilledTemplate(context);
	}

	public String getDisplayLineUpPageHtml(String error, String success, String lineUpRules,
			NamedLineUp lineUp, List<SortedGame> lineUpGames, boolean isMoneyLineUp) {
		String displayHtml = getDisplayHtml(lineUp, lineUpGames, isMoneyLineUp, lineUpRules);
		return getPageHtml(error, success, displayHtml);
	}

	private String getDisplayHtml(NamedLineUp lineUp, List<SortedGame> lineUpGames, boolean isMoneyLineUp,
			String lineUpRules) {
		VelocityTemplateHandler ve;
		if (lineUpGames.isEmpty()) {
			return "In dieser Besetzung wurde kein Spiel eingetragen. Erzeugung der Übersicht nicht möglich.";
		}
		if (isMoneyLineUp) {
			ve = new VelocityTemplateHandler("templates/displayMoney.vm");
		} else {
			ve = new VelocityTemplateHandler("templates/displayCasual.vm");
		}

		String allSessionsJSON = getEncodedJSONForAllSessionsGraph(lineUp, lineUpGames);
		String perSessionJSON = getEncodedJSONForPerSessionGraph(lineUp, lineUpGames);
		String ticksJSON = getJSONForTicks(lineUpGames);
		VelocityContext context = new VelocityContext();
		context.put("lineUp", lineUp);
		context.put("games", lineUpGames);
		context.put("roundUrl", DokoConstants.GAME_PAGE_LOCATION);
		context.put("playerUrl", DokoConstants.PLAYER_STATS_PAGE_LOCATION);
		context.put("dataForAllSessions", allSessionsJSON);
		context.put("dataPerSession", perSessionJSON);
		context.put("ticks", ticksJSON);
		context.put("rules", lineUpRules);
		context.put(Double.class.getSimpleName(), Double.class);
		context.put("doubleFormatter", new DecimalFormat("#.##"));
		context.put("dateFormatter", new SimpleDateFormat(DokoConstants.OUTPUT_DATE_FORMAT));

		return ve.getFilledTemplate(context);
	}

	public String getGamePageHtml(String error, String success, NamedLineUp lineUp, List<Round> rounds, Date date) {
		String gameHtml = getGameHtml(lineUp, rounds, date);
		return getPageHtml(error, success, gameHtml);
	}

	private String getGameHtml(NamedLineUp lineUp, List<Round> rounds, Date date) {
		if (rounds.isEmpty()) {
			return "Keine Runden für dieses Spiel eingetragen. Erzeugung der Übersicht nicht möglich.";
		}
		// Template assumes that for all rounds the order of players is the same
		VelocityTemplateHandler ve = new VelocityTemplateHandler("templates/displayGame.vm");
		List<Player> players = playerService.getPlayersSortedById(rounds.get(0).getPlayerIds());
		String allRoundsJson = getEncodedJSONForAllRoundsGraph(lineUp, rounds);
		VelocityContext context = new VelocityContext();
		context.put("date", date);
		context.put("players", players);
		context.put("rounds", rounds);
		context.put("playerUrl", DokoConstants.PLAYER_STATS_PAGE_LOCATION);
		context.put("dataForAllRounds", allRoundsJson);
		context.put("ticks", getJSONForTicks(rounds));
		context.put(Double.class.getSimpleName(), Double.class);
		context.put("doubleFormatter", new DecimalFormat("#.##"));
		context.put("dateFormatter", new SimpleDateFormat(DokoConstants.OUTPUT_DATE_FORMAT));

		return ve.getFilledTemplate(context);
	}

	private String getEncodedJSONForAllSessionsGraph(NamedLineUp lineUp, List<SortedGame> lineUpGames) {
		List<List<Object>> graphData = getGraphHeader(lineUp);

		List<Long> previousScores = new ArrayList<>();
		for (int i = 0; i < lineUpGames.size(); i++) {
			List<Object> gameData = new ArrayList<>();
			gameData.add(i + 1);
			List<Long> scores = lineUpGames.get(i).getScores();

			if (i == 0) {
				gameData.addAll(scores);
				previousScores = new ArrayList<>(scores);
			} else {
				for (int j = 0; j < scores.size(); j++) {
					previousScores.set(j, scores.get(j) + previousScores.get(j));
				}
				gameData.addAll(previousScores);
			}
			graphData.add(gameData);
		}
		Gson gson = new Gson();
		return Base64.getEncoder().encodeToString(gson.toJson(graphData).getBytes()); //TODO
	}


	private String getEncodedJSONForPerSessionGraph(NamedLineUp lineUp, List<SortedGame> lineUpGames) {
		List<List<Object>> graphData = getGraphHeader(lineUp);

		for (int i = 0; i < lineUpGames.size(); i++) {
			List<Object> gameData = new ArrayList<>();
			gameData.add(i + 1);
			gameData.addAll(lineUpGames.get(i).getScores());
			graphData.add(gameData);
		}
		Gson gson = new Gson();
		return Base64.getEncoder().encodeToString(gson.toJson(graphData).getBytes()); //TODO
	}

	private String getEncodedJSONForAllRoundsGraph(NamedLineUp lineUp, List<Round> rounds) {
		List<List<Object>> graphData = getGraphHeader(lineUp);

		List<Long> previousScores = new ArrayList<>();
		for (int i = 0; i < rounds.size(); i++) {
			List<Object> gameData = new ArrayList<>();
			gameData.add(i + 1);
			List<Long> scores = rounds.get(i).getScores();

			if (i == 0) {
				gameData.addAll(scores);
				previousScores = new ArrayList<>(scores);
			} else {
				for (int j = 0; j < scores.size(); j++) {
					previousScores.set(j, scores.get(j) + previousScores.get(j));
				}
				gameData.addAll(previousScores);
			}
			graphData.add(gameData);
		}
		Gson gson = new Gson();
		return Base64.getEncoder().encodeToString(gson.toJson(graphData).getBytes()); //TODO
	}

	private <E> String getJSONForTicks(List<E> list) {
		return getJSONForTicks(list.size());
	}

	private String getJSONForTicks(int size) {
		if (size < 1) {
			return getJSONForTicks(1);
		}
		int[] ticks = new int[size - 1];
		for (int i = 0; i < ticks.length; i++) {
			ticks[i] = i + 1;
		}

		Gson gson = new Gson();
		return gson.toJson(ticks);
	}

	private String getEncodedJSONForPlayerRoundsGraph(Player player, List<List<Round>> listOfRounds, int maxRounds) {
		List<List<Object>> graphData = new ArrayList<>();
		Long playerId = player.getId();

		List<Object> legend = new ArrayList<>();
		legend.add("Runde");
		legend.add(player.getName());
		graphData.add(legend);

		List<List<Long>> roundScores = new ArrayList<>();
		for (int i = 0; i < maxRounds; i++) {
			roundScores.add(new ArrayList<>());
		}
		for (int i = 0; i < listOfRounds.size(); i++) {
			List<Round> rounds = listOfRounds.get(i);
			for (int j = 0; j < rounds.size(); j++) {
				Round round = rounds.get(j);
				Long score = getScoreByPlayerId(round, playerId);
				roundScores.get(j).add(score);
			}
		}

		int roundCounter = 1;
		for (List<Long> roundScore : roundScores) {
			if (roundScore.size() < 5) {
				break;
			}
			List<Object> roundData = new ArrayList<>();
			Float averageScore = (float)sumOfLongs(roundScore) / roundScore.size();
			roundData.add(roundCounter);
			roundData.add(averageScore);
			graphData.add(roundData);
			roundCounter++;
		}

		Gson gson = new Gson();
		return Base64.getEncoder().encodeToString(gson.toJson(graphData).getBytes()); //TODO
	}

	private Long getScoreByPlayerId(Round round, Long playerId) {
		int index = round.getPlayerIds().indexOf(playerId);
		return round.getScores().get(index);
	}

	private Long sumOfLongs(List<Long> longs) {
		Long sum = 0l;
		for (Long long1 : longs) {
			sum+= long1;
		}
		return sum;
	}

	private List<List<Object>> getGraphHeader(NamedLineUp lineUp) {
		List<List<Object>> graphData = new ArrayList<>();

		List<Object> legend = new ArrayList<>();
		legend.add("Abend");
		legend.addAll(lineUp.getPlayers()
				.stream()
				.map(Player::getName)
				.collect(Collectors.toList()));
		graphData.add(legend);
		return graphData;
	}

	public String getProfilePageHtml(String error, String success, User user) {
		String profileHtml = getProfileHtml(user);
		return getPageHtml(error, success, profileHtml);
	}

	private String getProfileHtml(User user) {
		VelocityTemplateHandler ve = new VelocityTemplateHandler("templates/profile.vm");
		VelocityContext context = new VelocityContext();
		context.put("username", user.getUsername());
		context.put("mail", user.getEmail());

		return ve.getFilledTemplate(context);
	}

	private String getHeader(String error, String success) {
		VelocityTemplateHandler ve = new VelocityTemplateHandler("templates/header.vm");
		VelocityContext context = new VelocityContext();
		context.put("topLineUps", getTopLineUps());
		context.put("nonTopLineUps", getNonTopLineUps());
		context.put("isLoggedIn", isLoggedIn);
		context.put("error", error);
		context.put("success", success);

		return ve.getFilledTemplate(context);
	}

	private String getFooter() {
		VelocityTemplateHandler ve = new VelocityTemplateHandler("templates/footer.vm");

		return ve.getFilledTemplate(new VelocityContext());
	}

	private NamedLineUp[] getTopLineUps() {
		return playerService.getNamedLineUps(gameService.getTopLineUps());
	}

	private NamedLineUp[] getNonTopLineUps() {
		return playerService.getNamedLineUps(gameService.getNonTopLineUps());
	}
}
