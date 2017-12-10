package doko.velocity;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.velocity.VelocityContext;

import com.google.gson.Gson;

import doko.DokoConstants;
import doko.database.game.GameService;
import doko.database.game.SortedGame;
import doko.database.player.Player;
import doko.database.player.PlayerService;
import doko.database.token.TokenService;
import doko.database.user.User;
import doko.lineup.NamedLineUp;

public class HtmlProvider {

	private GameService gameService;
	private PlayerService playerService;
	private TokenService tokenService;
	private boolean isLoggedIn;

	public HtmlProvider(GameService gameService, PlayerService playerService, TokenService tokenService, boolean isLoggedIn) {
		this.gameService = gameService;
		this.playerService = playerService;
		this.tokenService = tokenService;
		this.isLoggedIn = isLoggedIn;
	}

	private String getPageHtml(String mainHtml) {
		StringBuilder sb = new StringBuilder();
		sb.append(getHeader());
		sb.append(getNavigation());
		sb.append(mainHtml);
		sb.append(getFooter());
		return sb.toString();
	}

	public String getLoginPageHtml(String errors, String successes) {
		String loginHtml = getLoginHtml(errors, successes);
		return getPageHtml(loginHtml);
	}

	private String getLoginHtml(String errors, String successes) {
		VelocityTemplateHandler ve = new VelocityTemplateHandler("templates/login.vm");
		VelocityContext context = new VelocityContext();
		context.put("errors", errors);
		context.put("successes", successes);

		return ve.getFilledTemplate(context);
	}

	public String getReportingPageHtml(String errors, String successes, List<Player> players) {
		String reportingHtml = getReportingHtml(players, errors, successes);
		return getPageHtml(reportingHtml);
	}

	private String getReportingHtml(List<Player> players, String errors, String successes) {
		VelocityTemplateHandler ve = new VelocityTemplateHandler("templates/report.vm");
		VelocityContext context = new VelocityContext();
		context.put("players", players);
		context.put("errors", errors);
		context.put("successes", successes);

		return ve.getFilledTemplate(context);
	}

	public String getDisplayLineUpPageHtml(String errors, String successes, String lineUpRules, NamedLineUp lineUp, List<SortedGame> lineUpGames, boolean isMoneyLineUp) {
		String displayHtml = getDisplayHtml(lineUp, lineUpGames, isMoneyLineUp, lineUpRules, errors, successes);
		return getPageHtml(displayHtml);
	}

	private String getDisplayHtml(NamedLineUp lineUp, List<SortedGame> lineUpGames, boolean isMoneyLineUp, String lineUpRules, String errors, String successes) {
		VelocityTemplateHandler ve;
		if (isMoneyLineUp) {
			ve = new VelocityTemplateHandler("templates/displayMoney.vm");
		} else {
			ve = new VelocityTemplateHandler("templates/displayCasual.vm");
		}

		String allSessionsJSON = getJSONForAllSessionsGraph(lineUp, lineUpGames);
		String perSessionJSON = getJSONForPerSessionGraph(lineUp, lineUpGames);
		String ticksJSON = getJSONForTicks(lineUpGames);
		VelocityContext context = new VelocityContext();
		context.put("lineUp", lineUp);
		context.put("games", lineUpGames);
		context.put("isMoney", isMoneyLineUp);
		context.put("dataForAllSessions", allSessionsJSON);
		context.put("dataPerSession", perSessionJSON);
		context.put("ticks", ticksJSON);
		context.put("rules", lineUpRules);
		context.put("errors", errors);
		context.put("successes", successes);
		context.put(Double.class.getSimpleName(), Double.class);
		context.put("doubleFormatter", new DecimalFormat("#.##"));
		context.put("dateFormatter", new SimpleDateFormat(DokoConstants.OUTPUT_DATE_FORMAT));

		return ve.getFilledTemplate(context);
	}

	private String getJSONForAllSessionsGraph(NamedLineUp lineUp, List<SortedGame> lineUpGames) {
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
		return gson.toJson(graphData);
	}


	private String getJSONForPerSessionGraph(NamedLineUp lineUp, List<SortedGame> lineUpGames) {
		List<List<Object>> graphData = getGraphHeader(lineUp);

		for (int i = 0; i < lineUpGames.size(); i++) {
			List<Object> gameData = new ArrayList<>();
			gameData.add(i + 1);
			gameData.addAll(lineUpGames.get(i).getScores());
			graphData.add(gameData);
		}
		Gson gson = new Gson();
		return gson.toJson(graphData);
	}

	private String getJSONForTicks(List<SortedGame> lineUpGames) {
		int[] ticks = new int[lineUpGames.size() - 1];
		for (int i = 0; i < ticks.length; i++) {
			ticks[i] = i + 1;
		}

		Gson gson = new Gson();
		return gson.toJson(ticks);
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

	public String getProfilePageHtml(String errors, String successes, User user) {
		String profileHtml = getProfileHtml(user, errors, successes);
		return getPageHtml(profileHtml);
	}

	private String getProfileHtml(User user, String errors, String successes) {
		VelocityTemplateHandler ve = new VelocityTemplateHandler("templates/profile.vm");
		VelocityContext context = new VelocityContext();
		context.put("username", user.getUsername());
		context.put("mail", user.getEmail());
		context.put("errors", errors);
		context.put("successes", successes);

		return ve.getFilledTemplate(context);
	}

	private String getHeader() {
		VelocityTemplateHandler ve = new VelocityTemplateHandler("templates/header.vm");

		return ve.getFilledTemplate(new VelocityContext());
	}

	private String getFooter() {
		VelocityTemplateHandler ve = new VelocityTemplateHandler("templates/footer.vm");

		return ve.getFilledTemplate(new VelocityContext());
	}

	private String getNavigation() {
		VelocityTemplateHandler ve = new VelocityTemplateHandler("templates/navigation.vm");
		VelocityContext context = new VelocityContext();
		context.put("topLineUps", getTopLineUps());
		context.put("nonTopLineUps", getNonTopLineUps());
		context.put("isLoggedIn", isLoggedIn);

		return ve.getFilledTemplate(context);
	}

	private NamedLineUp[] getTopLineUps() {
		return playerService.getNamedLineUps(gameService.getTopLineUps());
	}

	private NamedLineUp[] getNonTopLineUps() {
		return playerService.getNamedLineUps(gameService.getNonTopLineUps());
	}
}
