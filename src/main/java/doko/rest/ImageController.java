package doko.rest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ImageController {
	
	private String IMAGE_PATH = "src/main/resources/images/dokoblatt.png"; //TODO change to target

	public byte[] getImage(String path) {
		try {
			return Files.readAllBytes(Paths.get(path));
		} catch (IOException e) {
			return new byte[0];
		}
	}

	public byte[] getBackgroundImage() {
		return getImage(IMAGE_PATH);
	}
}
