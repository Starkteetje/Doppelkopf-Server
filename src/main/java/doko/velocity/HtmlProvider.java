package doko.velocity;

import java.util.List;

import org.apache.velocity.VelocityContext;

import doko.database.game.GameService;
import doko.database.player.Player;
import doko.database.player.PlayerService;
import doko.database.token.TokenService;
import doko.lineup.NamedLineUp;

public class HtmlProvider {

	private GameService gameService;
	private PlayerService playerService;
	private TokenService tokenService;

	public HtmlProvider(GameService gameService, PlayerService playerService, TokenService tokenService) {
		this.gameService = gameService;
		this.playerService = playerService;
		this.tokenService = tokenService;
	}

	private String getPageHtml(boolean isLoggedIn, String mainHtml) {
		StringBuilder sb = new StringBuilder();
		sb.append(getHeader());
		sb.append(getNavigation(isLoggedIn));
		sb.append(mainHtml);
		sb.append(getFooter());
		return sb.toString();
	}

	public String getLoginPageHtml(boolean isLoggedIn, String errors, String successes) {
		String loginHtml = getLoginHtml(errors, successes);
		return getPageHtml(isLoggedIn, loginHtml);
	}

	private String getLoginHtml(String errors, String successes) {
		VelocityTemplateHandler ve = new VelocityTemplateHandler("templates/login.vm");
		VelocityContext context = new VelocityContext();
		context.put("errors", errors);
		context.put("successes", successes);

		return ve.getFilledTemplate(context);
	}

	public String getReportingPageHtml(boolean isLoggedIn, String errors, String successes, List<Player> players) {
		String reportingHtml = getReportingHtml(players, errors, successes);
		return getPageHtml(isLoggedIn, reportingHtml);
	}

	private String getReportingHtml(List<Player> players, String errors, String successes) {
		VelocityTemplateHandler ve = new VelocityTemplateHandler("templates/report.vm");
		VelocityContext context = new VelocityContext();
		context.put("players", players);
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

	private String getNavigation(boolean isLoggedIn) {
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
