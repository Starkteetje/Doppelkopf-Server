package doko;

import java.io.StringWriter;
import java.nio.file.Paths;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

public class Velocity {

	public static String getHtml() {
		System.out.println(Paths.get(".").toAbsolutePath().normalize().toString());
		/*  first, get and initialize an engine  */
	    VelocityEngine ve = new VelocityEngine();
	    ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
	    ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
	    ve.init();
	    /*  next, get the Template  */
	    Template t = ve.getTemplate("templates/test.vm" );
	    /*  create a context and add data */
	    VelocityContext context = new VelocityContext();
	    context.put("username_placeholder", "World");
	    /* now render the template into a StringWriter */
	    StringWriter writer = new StringWriter();
	    t.merge( context, writer );
	    /* show the World */
	    return writer.toString();   
	}
}
