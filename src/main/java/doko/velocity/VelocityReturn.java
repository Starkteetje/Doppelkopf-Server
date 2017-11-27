package doko.velocity;

import org.apache.velocity.VelocityContext;

public class VelocityReturn {

	public String getTestHtml() {
		VelocityTemplateHandler ve = new VelocityTemplateHandler("templates/test.vm");
	    VelocityContext context = new VelocityContext();
	    context.put("username_placeholder", "World");

	    return ve.mergeTemplate(context);
	}
}
