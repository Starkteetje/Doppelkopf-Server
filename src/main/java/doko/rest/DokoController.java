package doko.rest;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.ModelAttribute;

public class DokoController {
	
	@ModelAttribute
	public void setHeaders(HttpServletResponse response) {
		response.setHeader("strict-transport-security", "max-age=31536000"); // HTST header with 1 year validity
		response.setHeader("referrer-policy", "no-referrer"); // Do not send information on which this site to other linked sites
		response.setHeader("content-security-policy", "default-src 'self'; script-src 'self' https://www.gstatic.com; style-src 'self' 'unsafe-inline' https://www.gstatic.com"); // Load assets only from this site and additionally scripts from gstatic//TODO remove unsafe inline
		response.setHeader("x-frame-options", "DENY"); // Do not allow page to be loaded as an iframe
		response.setHeader("x-xss-protection", "1;mode=block"); // Enable XSS protection, block load if attack detected
		response.setHeader("x-content-type-options", "nosniff"); // Tell browser to stick with declared content type
		response.setHeader("feature-policy", "geolocation 'none'; midi 'none'; notifications 'none'; push 'none'; sync-xhr 'none'; microphone 'none'; camera 'none'; magnetometer 'none'; gyroscope 'none'; speaker 'none'; vibrate 'none'; fullscreen 'none'; payment 'none'"); // Tell browser not to allow access to any feature
	}
}
