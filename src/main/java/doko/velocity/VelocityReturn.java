package doko.velocity;

import org.apache.velocity.VelocityContext;

public class VelocityReturn {//TODO bad name

	public String getTestHtml() {
		VelocityTemplateHandler ve = new VelocityTemplateHandler("templates/test.vm");
	    VelocityContext context = new VelocityContext();
	    context.put("username_placeholder", "World");

	    return ve.mergeTemplate(context);
	}
	
	public String getCSS() {
		VelocityTemplateHandler ve = new VelocityTemplateHandler("css/style.css");

	    return ve.mergeTemplate(new VelocityContext());
	}
	
	public String getLoginPageHtml() {
		StringBuilder sb = new StringBuilder();
		sb.append(getHeader());
		sb.append(getLoginHtml());
		sb.append(getFooter());
		return sb.toString();
	}
	
	private String getLoginHtml() {
		VelocityTemplateHandler ve = new VelocityTemplateHandler("templates/login.vm");
	    VelocityContext context = new VelocityContext();
	    context.put("errors", "");

	    return ve.mergeTemplate(context);
	}
	
	private String getHeader() {
		VelocityTemplateHandler ve = new VelocityTemplateHandler("templates/header.vm");

	    return ve.mergeTemplate(new VelocityContext());
	}
	
	private String getFooter() {
		VelocityTemplateHandler ve = new VelocityTemplateHandler("templates/footer.vm");

	    return ve.mergeTemplate(new VelocityContext());
	}
}
