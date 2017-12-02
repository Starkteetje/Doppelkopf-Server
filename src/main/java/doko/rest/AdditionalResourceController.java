package doko.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AdditionalResourceController {

	private FileExposer fileExposer = new FileExposer();

	@GetMapping(value = "/images/dokoblatt.png", produces = "image/png")
	public ResponseEntity<byte[]> getBackgroundPicture() {
		return new ResponseEntity<>(fileExposer.getBackgroundImage(), HttpStatus.OK);
	}

	@GetMapping(value = "/images/datepicker-icons.png", produces = "image/png")
	public ResponseEntity<byte[]> getArrowLeft() {
		return new ResponseEntity<>(fileExposer.getDatepickerIcons(), HttpStatus.OK);
	}

	@GetMapping(value = "/style", produces = "text/css")
	public ResponseEntity<byte[]> getCSS() {
		return new ResponseEntity<>(fileExposer.getMainCSS(), HttpStatus.OK);
	}

	@GetMapping(value = "/datepicker", produces = "text/css")
	public ResponseEntity<byte[]> getDatepickerCSS() {
		return new ResponseEntity<>(fileExposer.getDatepickerCSS(), HttpStatus.OK);
	}

	@GetMapping(value = "/datepicker-ui", produces = "text/css")
	public ResponseEntity<byte[]> getDatepickerUiCSS() {
		return new ResponseEntity<>(fileExposer.getDatepickerUiCSS(), HttpStatus.OK);
	}

	@GetMapping(value = "/datepicker1.js", produces = "application/javascript")
	public ResponseEntity<byte[]> getJS1() {
		return new ResponseEntity<>(fileExposer.getJS1(), HttpStatus.OK);
	}

	@GetMapping(value = "/datepicker2.js", produces = "application/javascript")
	public ResponseEntity<byte[]> getJS2() {
		return new ResponseEntity<>(fileExposer.getJS2(), HttpStatus.OK);
	}
}
