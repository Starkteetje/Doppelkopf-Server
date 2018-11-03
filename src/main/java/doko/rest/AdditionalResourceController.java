package doko.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdditionalResourceController extends DokoController {

	private FileExposer fileExposer = new FileExposer();

	@GetMapping(value = "/images/dokoblatt.png", produces = "image/png")
	public ResponseEntity<byte[]> getBackgroundPicture() {
		return new ResponseEntity<>(fileExposer.getBackgroundImage(), HttpStatus.OK);
	}

	@GetMapping(value = "/images/datepicker-icons.png", produces = "image/png")
	public ResponseEntity<byte[]> getArrowLeft() {
		return new ResponseEntity<>(fileExposer.getDatepickerIcons(), HttpStatus.OK);
	}

	@GetMapping(value = "/images/warning.png", produces = "image/png")
	public ResponseEntity<byte[]> getWarningImage() {
		return new ResponseEntity<>(fileExposer.getWarningIcon(), HttpStatus.OK);
	}

	@GetMapping(value = "/images/task.png", produces = "image/png")
	public ResponseEntity<byte[]> getTaskImage() {
		return new ResponseEntity<>(fileExposer.getTaskIcon(), HttpStatus.OK);
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

	@GetMapping(value = "/notification.js", produces = "application/javascript")
	public ResponseEntity<byte[]> getNotificationJS() {
		return new ResponseEntity<>(fileExposer.getNotificationJS(), HttpStatus.OK);
	}

	@GetMapping(value = "/datepicker1.js", produces = "application/javascript")
	public ResponseEntity<byte[]> getDatepickerJS1() {
		return new ResponseEntity<>(fileExposer.getDatePickerJS1(), HttpStatus.OK);
	}

	@GetMapping(value = "/datepicker2.js", produces = "application/javascript")
	public ResponseEntity<byte[]> getDatePickerJS2() {
		return new ResponseEntity<>(fileExposer.getDatePickerJS2(), HttpStatus.OK);
	}

	@GetMapping(value = "/session.js", produces = "application/javascript")
	public ResponseEntity<byte[]> getSessionGraphJS() {
		return new ResponseEntity<>(fileExposer.getSessionGraphJS(), HttpStatus.OK);
	}

	@GetMapping(value = "/evening.js", produces = "application/javascript")
	public ResponseEntity<byte[]> getEveningGraphJS() {
		return new ResponseEntity<>(fileExposer.getEveningGraphJS(), HttpStatus.OK);
	}
}
