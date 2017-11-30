package doko.velocity;

import java.util.List;

import org.apache.velocity.VelocityContext;

import doko.database.player.Player;
import doko.lineup.NamedLineUp;

public class HtmlProvider {

	public String getCSS() {
		VelocityTemplateHandler ve = new VelocityTemplateHandler("css/style.css");

		return ve.mergeTemplate(new VelocityContext());
	}

	public String getLoginPageHtml(NamedLineUp[] topLineUps, NamedLineUp[] nonTopLineUps, String errors,
			String successes) {
		String loginHtml = getLoginHtml(errors, successes);
		return getPageHtml(topLineUps, nonTopLineUps, loginHtml);
	}

	private String getLoginHtml(String errors, String successes) {
		VelocityTemplateHandler ve = new VelocityTemplateHandler("templates/login.vm");
		VelocityContext context = new VelocityContext();
		context.put("errors", errors);
		context.put("successes", successes);

		return ve.mergeTemplate(context);
	}

	private String getHeader() {
		VelocityTemplateHandler ve = new VelocityTemplateHandler("templates/header.vm");

		return ve.mergeTemplate(new VelocityContext());
	}

	private String getNavigation(NamedLineUp[] topLineUps, NamedLineUp[] nonTopLineUps) {
		VelocityTemplateHandler ve = new VelocityTemplateHandler("templates/navigation.vm");
		VelocityContext context = new VelocityContext();
		context.put("topLineUps", topLineUps);
		context.put("nonTopLineUps", nonTopLineUps);

		return ve.mergeTemplate(context);
	}

	private String getFooter() {
		VelocityTemplateHandler ve = new VelocityTemplateHandler("templates/footer.vm");

		return ve.mergeTemplate(new VelocityContext());
	}

	public String getDatepickerCSS() {
		VelocityTemplateHandler ve = new VelocityTemplateHandler("css/datepicker-ui.css");

		return ve.mergeTemplate(new VelocityContext());
	}

	public String getJS1() {
		VelocityTemplateHandler ve = new VelocityTemplateHandler("js/datepicker1.js");

		return ve.mergeTemplate(new VelocityContext());
	}

	public String getJS2() {
		VelocityTemplateHandler ve = new VelocityTemplateHandler("js/datepicker2.js");

		return ve.mergeTemplate(new VelocityContext());
	}

	public String getReportingPageHtml(NamedLineUp[] topLineUps, NamedLineUp[] nonTopLineUps, String errors,
			String successes, List<Player> players) {
		String reportingHtml = getReportingHtml(players, errors, successes);
		return getPageHtml(topLineUps, nonTopLineUps, reportingHtml);
	}

	private String getReportingHtml(List<Player> players, String errors, String successes) {
		VelocityTemplateHandler ve = new VelocityTemplateHandler("templates/report.vm");
		VelocityContext context = new VelocityContext();
		context.put("players", players);
		context.put("errors", errors);
		context.put("successes", successes);

		return ve.mergeTemplate(context);
	}

	private String getPageHtml(NamedLineUp[] topLineUps, NamedLineUp[] nonTopLineUps, String mainHtml) {
		StringBuilder sb = new StringBuilder();
		sb.append(getHeader());
		sb.append(getNavigation(topLineUps, nonTopLineUps));
		sb.append(mainHtml);
		sb.append(getFooter());
		return sb.toString();
	}
}
