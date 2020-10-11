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
import com.google.gson.GsonBuilder;

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
		String placementJson = getEncodedJSONForPlayerPlacementsGraph(player, games);
		String signJson = getEncodedJSONForPlayerSignGraph(player, games);
		context.put("player", player);
		context.put("games", games);
		context.put("roundUrl", DokoConstants.GAME_PAGE_LOCATION);
		context.put("dataForRounds", averageRoundJSON);
		context.put("ticks", ticksJSON);
		context.put("dataForPlacements", placementJson);
		context.put("dataForSign", signJson);
		context.put(Double.class.getSimpleName(), Double.class);
		context.put("doubleFormatter", new DecimalFormat("#.##"));
		context.put("dateFormatter", new SimpleDateFormat(DokoConstants.OUTPUT_DATE_FORMAT));

		return ve.getFilledTemplate(context);
	}

	public String getDisplayLineUpPageHtml(String error, String success, String lineUpRules,
			NamedLineUp lineUp, List<SortedGame> lineUpGames, List<Round> rounds, boolean isMoneyLineUp) {
		String displayHtml = getDisplayHtml(lineUp, lineUpGames, rounds, isMoneyLineUp, lineUpRules);
		return getPageHtml(error, success, displayHtml);
	}

	private String getDisplayHtml(NamedLineUp lineUp, List<SortedGame> lineUpGames, List<Round> rounds, boolean isMoneyLineUp,
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

		List<String> placementJsons = new ArrayList<>();
		List<String> signJsons = new ArrayList<>();
		for (Player player : lineUp.getPlayers()) {
			String placementJson = getEncodedJSONForPlayerPlacementsGraph(player, lineUpGames);
			placementJsons.add(placementJson);
			String signJson = getEncodedJSONForPlayerSignGraph(player, lineUpGames);
			signJsons.add(signJson);
		}
		List<List<String>> playedWithJsons = getEncodedJSONsForPlayWithTable(lineUp.getPlayers(), rounds);
		Gson gson = new GsonBuilder().disableHtmlEscaping().create(); // Disable HTML escaping due to '=' in b64. b64 does not contain '<'
		String playedWithJsonsJson = gson.toJson(playedWithJsons);

		VelocityContext context = new VelocityContext();
		context.put("lineUp", lineUp);
		context.put("games", lineUpGames);
		context.put("roundUrl", DokoConstants.GAME_PAGE_LOCATION);
		context.put("playerUrl", DokoConstants.PLAYER_STATS_PAGE_LOCATION);
		context.put("dataForAllSessions", allSessionsJSON);
		context.put("dataPerSession", perSessionJSON);
		context.put("dataForPlacements", placementJsons);
		context.put("dataForSigns", signJsons);
		context.put("playedWith", playedWithJsonsJson);
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

	// Assumes 4 players
	private String getEncodedJSONForPlayerPlacementsGraph(Player player, List<SortedGame> games) {
		List<List<Object>> graphData = new ArrayList<>();

		List<Object> legend = new ArrayList<>();
		legend.add("Platzierung");
		legend.add("Anzahl der Platzierungen");
		graphData.add(legend);

		List<Integer> placements = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			placements.add(0);
		}
		for (SortedGame game : games) {
			int placement = getPlacementByPlayerId(game, player.getId());
			placements.set(placement, placements.get(placement).intValue() + 1);
		}

		for (int i = 0; i < 4; i++) {
			List<Object> ithPlacement = new ArrayList<>();
			ithPlacement.add(new Integer(i + 1).toString() + ". Platz");
			ithPlacement.add(placements.get(i));
			graphData.add(ithPlacement);
		}

		Gson gson = new Gson();
		return Base64.getEncoder().encodeToString(gson.toJson(graphData).getBytes()); //TODO
	}

	private String getEncodedJSONForPlayerSignGraph(Player player, List<SortedGame> games) {
		List<List<Object>> graphData = new ArrayList<>();

		List<Object> legend = new ArrayList<>();
		legend.add("Vorzeichen");
		legend.add("Anzahl der Endergebnisse");
		graphData.add(legend);

		List<Integer> signs = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			signs.add(0);
		}
		for (SortedGame game : games) {
			Long score = getScoreByPlayerId(game, player.getId());
			if (score > 0) {
				signs.set(0, signs.get(0).intValue() + 1);
			} else if (score == 0) {
				signs.set(1, signs.get(1).intValue() + 1);
			} else {
				signs.set(2, signs.get(2).intValue() + 1);
			}
		}

		List<Object> positiveArray = new ArrayList<>();
		positiveArray.add("Positiv");
		positiveArray.add(signs.get(0));
		graphData.add(positiveArray);

		List<Object> neutralArray = new ArrayList<>();
		neutralArray.add("Neutral");
		neutralArray.add(signs.get(1));
		graphData.add(neutralArray);

		List<Object> negativeArray = new ArrayList<>();
		negativeArray.add("Negativ");
		negativeArray.add(signs.get(2));
		graphData.add(negativeArray);

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

	// Returns from 0 to 3 for easier index ops
	private int getPlacementByPlayerId(SortedGame game, Long playerId) {
		Long playerScore = getScoreByPlayerId(game, playerId);
		List<Long> scores = game.getScores();
		int placement = 0;

		for (Long score : scores) {
			if (score > playerScore) {
				placement++;
			}
		}
		return placement;
	}

	private Long getScoreByPlayerId(SortedGame game, Long playerId) {
		int index = game.getLineUp().getIds().indexOf(playerId);
		return game.getScores().get(index);
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

	/*
	 * Caveats: Ignores 0-score games. Will ignore the non-solo parties of a solo. Somewhat assumes 4 players
	 */
	private List<List<String>> getEncodedJSONsForPlayWithTable(List<Player> players, List<Round> rounds) {
		// Return null if no rounds
		if (rounds.isEmpty()) {
			return null;
		}

		List<Long> playerIds = rounds.get(0).getPlayerIds(); // PlayerIds are always sorted, thus in the same order
		int numberOfPlayers = playerIds.size();
		// Create matrix of scores
		List<List<Long>> results = new ArrayList<>();
		for (int i = 0; i < numberOfPlayers * numberOfPlayers; i++) {
			results.add(new ArrayList<>());
		}
		for (Round round : rounds) {
			updateResultList(results, round);
		}

		// Match result list to sorted players in case Round and LineUp do not have the same order
		List<List<String>> tableJsons = new ArrayList<>();
		for (Player player1 : players) {
			int player1Index = rounds.get(0).getPlayerIds().indexOf(player1.getId());
			List<String> playerPlayedWithJsons = new ArrayList<>();	
			for (Player player2 : players) {
				int player2Index = rounds.get(0).getPlayerIds().indexOf(player2.getId());
				String playedWithJson = getPlayedWithEncodedJsonFromResult(results.get(numberOfPlayers * player1Index + player2Index));
				playerPlayedWithJsons.add(playedWithJson);
			}
			tableJsons.add(playerPlayedWithJsons);
		}
		return tableJsons;
	}

	private void updateResultList(List<List<Long>> results, Round round) {
		List<Long> scores = round.getScores();

		// Score is 0, no idea who played together
		if (scores.get(0) == 0) {
			return;
		}

		List<Integer> postiveScoreIndices = new ArrayList<>();
		List<Integer> negativeScoreIndices = new ArrayList<>();

		for (int i = 0; i < scores.size(); i++) {
			if (scores.get(i) > 0) {
				postiveScoreIndices.add(i);
			} else {
				negativeScoreIndices.add(i);
			}
		}

		// Won solo
		if (negativeScoreIndices.size() > postiveScoreIndices.size()) {
			int positiveIndex = postiveScoreIndices.get(0);
			updateResultsScore(results, scores.size(), positiveIndex, positiveIndex, scores.get(positiveIndex));
			// Lost solo
		} else if (postiveScoreIndices.size() > negativeScoreIndices.size()) {
			int negativeIndex = negativeScoreIndices.get(0);
			updateResultsScore(results, scores.size(), negativeIndex, negativeIndex, scores.get(negativeIndex));
			// Regular game
		} else {
			updateResultsScore(results, scores.size(), postiveScoreIndices.get(0), postiveScoreIndices.get(1), scores.get(postiveScoreIndices.get(0)));
			updateResultsScore(results, scores.size(), negativeScoreIndices.get(0), negativeScoreIndices.get(1), scores.get(negativeScoreIndices.get(0)));
		}
	}

	private void updateResultsScore(List<List<Long>> results, int numberOfPlayers, int player1Index, int player2Index, Long score) {
		if (player1Index == player2Index) {
			int changeIndex = numberOfPlayers * player1Index + player1Index;
			results.get(changeIndex).add(score);
		} else {
			int changeIndex1 = numberOfPlayers * player1Index + player2Index;
			int changeIndex2 = numberOfPlayers * player2Index + player1Index;
			results.get(changeIndex1).add(score);
			results.get(changeIndex2).add(score);
		}
	}

	private String getPlayedWithEncodedJsonFromResult(List<Long> list) {
		List<List<Object>> graphData = new ArrayList<>();

		List<Object> legend = new ArrayList<>();
		legend.add("Vorzeichen mit Mitspieler");
		legend.add("Anzahl der Partien");
		graphData.add(legend);

		List<Object> positive = new ArrayList<>();
		List<Object> negative = new ArrayList<>();
		int numPos = 0;
		int numNeg = 0;
		for (Long score : list) {
			if (score > 0) {
				numPos++;
			} else {
				numNeg++;
			}
		}

		positive.add("Gewonnene Partien");
		positive.add(numPos);
		graphData.add(positive);

		negative.add("Verlorene Partien");
		negative.add(numNeg);
		graphData.add(negative);

		Gson gson = new Gson();
		return Base64.getEncoder().encodeToString(gson.toJson(graphData).getBytes());
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
