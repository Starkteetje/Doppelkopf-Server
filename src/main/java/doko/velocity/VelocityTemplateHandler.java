package doko.velocity;

import java.io.StringWriter;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

public class VelocityTemplateHandler {
	
	private VelocityEngine ve;
	private Template template;
	
	public VelocityTemplateHandler(String template) {
		ve = new VelocityEngine();
	    ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
	    ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
	    ve.init();
	    this.template = ve.getTemplate(template);
	}
	
	public String mergeTemplate(VelocityContext context) {
		StringWriter writer = new StringWriter();
		template.merge(context, writer);
		return writer.toString();   
	}
}
