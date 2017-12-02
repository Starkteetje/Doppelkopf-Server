package doko.rest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileExposer {

	private String CSS_PATH = "src/main/resources/css/"; // TODO change to target
	private String IMAGE_PATH = "src/main/resources/images/"; // TODO change to target
	private String JS_PATH = "src/main/resources/js/"; // TODO change to target

	private byte[] getFile(String path) {
		try {
			//TODO log all file system access
			return Files.readAllBytes(Paths.get(path));
		} catch (IOException e) {
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

	public byte[] getJS1() {
		return getJS("datepicker1.js");
	}

	public byte[] getJS2() {
		return getJS("datepicker2.js");
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
