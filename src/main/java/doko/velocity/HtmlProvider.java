package doko.velocity;

import org.apache.velocity.VelocityContext;

import doko.lineup.NamedLineUp;

public class HtmlProvider {
	
	public String getCSS() {
		VelocityTemplateHandler ve = new VelocityTemplateHandler("css/style.css");

	    return ve.mergeTemplate(new VelocityContext());
	}
	
	public String getLoginPageHtml(NamedLineUp[] topLineUps, NamedLineUp[] nonTopLineUps, String errors) {
		StringBuilder sb = new StringBuilder();
		sb.append(getHeader());
		sb.append(getNavigation(topLineUps, nonTopLineUps));
		sb.append(getLoginHtml(errors));
		sb.append(getFooter());
		return sb.toString();
	}
	
	private String getLoginHtml(String errors) {
		VelocityTemplateHandler ve = new VelocityTemplateHandler("templates/login.vm");
	    VelocityContext context = new VelocityContext();
	    context.put("errors", errors);

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
}
