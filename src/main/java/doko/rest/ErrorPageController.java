package doko.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.autoconfigure.web.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ErrorPageController extends AbstractErrorController implements ErrorController {

	public ErrorPageController(ErrorAttributes errorAttributes) {
		super(errorAttributes);
	}

	public static final String ERROR_PATH = "error";

	public static <T> ResponseEntity<T> getUnauthorizedPage() {
		return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // TODO return error page
	}

	public static <T> ResponseEntity<T> getBadRequestPage() {
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // TODO return error page
	}

	public static <T> ResponseEntity<T> getServerErrorPage() {
		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // TODO return error page
	}

	@RequestMapping
	@ResponseBody
	public ResponseEntity<String> error(HttpServletRequest request) {
		HttpStatus errorCode = getErrorCode(request);
		return new ResponseEntity<>(errorCode.toString(), errorCode); // TODO
	}

	@Override
	public String getErrorPath() {
		return ERROR_PATH;
	}

	private HttpStatus getErrorCode(HttpServletRequest request) {
		return getStatus(request);
	}
}
