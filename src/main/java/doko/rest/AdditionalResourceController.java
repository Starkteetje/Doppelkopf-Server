package doko.rest;

import doko.velocity.HtmlProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AdditionalResourceController {

	private HtmlProvider velocity = new HtmlProvider();
	private ImageController imgController = new ImageController();

	@GetMapping(value = "/dokoblatt.png", produces = "image/png")
	public ResponseEntity<byte[]> getBackgroundPicture() {
		return new ResponseEntity<>(imgController.getBackgroundImage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@GetMapping(value = "/style", produces = "text/css")
	public ResponseEntity<String> getCSS() {
		return new ResponseEntity<>(velocity.getCSS(), HttpStatus.OK);
	}
	
	@GetMapping(value = "/datepicker", produces = "text/css")
	public ResponseEntity<String> getDatepickerCSS() {
		return new ResponseEntity<>(velocity.getDatepickerCSS(), HttpStatus.OK);
	}

	@GetMapping(value = "/datepicker-ui", produces = "text/css")
	public ResponseEntity<String> getDatepickerUiCSS() {
		return new ResponseEntity<>(velocity.getDatepickerCSS(), HttpStatus.OK);
	}

	@GetMapping(value = "/datepicker1.js", produces = "application/javascript")
	public ResponseEntity<String> getJS1() {//FIXME broken
		return new ResponseEntity<>(velocity.getJS1(), HttpStatus.OK);
	}

	@GetMapping(value = "/datepicker2.js", produces = "application/javascript")
	public ResponseEntity<String> getJS2() {
		return new ResponseEntity<>(velocity.getJS2(), HttpStatus.OK);
	}
}
