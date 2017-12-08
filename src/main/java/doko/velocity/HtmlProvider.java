package doko.velocity;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.velocity.VelocityContext;

import doko.DokoConstants;
import doko.database.game.GameService;
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

	public String getDisplayLineUpPageHtml(String errors, String successes, String lineUpRules, List<List<String>> lineUpGames, boolean isMoneyLineUp) {
		String displayHtml = getDisplayHtml(lineUpGames, isMoneyLineUp, lineUpRules, errors, successes);
		return getPageHtml(displayHtml);
	}

	private String getDisplayHtml(List<List<String>> lineUpGames, boolean isMoneyLineUp, String lineUpRules, String errors, String successes) {
		VelocityTemplateHandler ve;
		if (isMoneyLineUp) {
			ve = new VelocityTemplateHandler("templates/displayMoney.vm");
		} else {
			ve = new VelocityTemplateHandler("templates/displayCasual.vm");
		}
		VelocityContext context = new VelocityContext();
		context.put("games", lineUpGames);
		context.put("isMoney", isMoneyLineUp);
		context.put("rules", lineUpRules);
		context.put("errors", errors);
		context.put("successes", successes);
		context.put(Double.class.getSimpleName(), Double.class);
		context.put("integerFormatter", new DecimalFormat("#"));
		context.put("doubleFormatter", new DecimalFormat("#.##"));
		context.put("dateParser", new SimpleDateFormat(DokoConstants.DATABASE_DATE_FORMAT));
		context.put("dateFormatter", new SimpleDateFormat(DokoConstants.OUTPUT_DATE_FORMAT));

		return ve.getFilledTemplate(context);
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
