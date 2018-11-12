package doko.rest;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class FileExposer {

	private static final String CSS_PATH = "BOOT-INF/classes/css/";
	private static final String IMAGE_PATH = "BOOT-INF/classes/images/";
	private static final String JS_PATH = "BOOT-INF/classes/js/";

	private byte[] getFile(String path) {
		try {
			InputStream is = this.getClass().getClassLoader().getResourceAsStream(path);
			return IOUtils.toByteArray(is);
		} catch (IOException e) {
			System.out.println("Could not read file " + path); //TODO log properly
			return new byte[0];
		}
	}

	private byte[] getCSS(String cssName) {
		return getFile(CSS_PATH + cssName);
	}

	private byte[] getImage(String imgName) {
		return getFile(IMAGE_PATH + imgName);
	}

	private byte[] getJS(String jsName) {
		return getFile(JS_PATH + jsName);
	}

	public byte[] getBackgroundImage() {
		return getImage("dokoblatt.png");
	}

	public byte[] getWarningIcon() {
		return getImage("warning.png");
	}

	public byte[] getTaskIcon() {
		return getImage("task.png");
	}

	public byte[] getNotificationJS() {
		return getJS("notification.js");
	}

	public byte[] getDatePickerConfigJS() {
		return getJS("datepickerconfig.js");
	}

	public byte[] getDatePickerJS1() {
		return getJS("datepicker1.js");
	}

	public byte[] getDatePickerJS2() {
		return getJS("datepicker2.js");
	}

	public byte[] getEveningGraphJS() {
		return getJS("evening.js");
	}

	public byte[] getSessionGraphJS() {
		return getJS("session.js");
	}

	public byte[] getMainCSS() {
		return getCSS("style.css");
	}

	public byte[] getDatepickerCSS() {
		return getCSS("datepicker.css");
	}

	public byte[] getDatepickerUiCSS() {
		return getCSS("datepicker-ui.css");
	}

	public byte[] getDatepickerIcons() {
		return getImage("datepicker-icons.png");
	}
}
