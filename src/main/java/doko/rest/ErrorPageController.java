package doko.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ErrorPageController {

	private ErrorPageController() {}

	public static <T> ResponseEntity<T> getUnauthorizedPage() {
		return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // TODO return error page
	}

	public static <T> ResponseEntity<T> getBadRequestPage() {
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // TODO return error page
	}

	public static <T> ResponseEntity<T> getServerErrorPage() {
		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // TODO return error page
	}
}
