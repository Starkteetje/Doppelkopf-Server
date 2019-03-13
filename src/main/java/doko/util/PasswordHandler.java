package doko.util;

import org.springframework.security.crypto.bcrypt.BCrypt;

public abstract class PasswordHandler {

	private PasswordHandler() {}

	public static final int COST_FACTOR = 10;

	public static boolean checkPassword(String password, String storedValue) {
		return BCrypt.checkpw(password, storedValue);
	}

	public static String getPasswordDerivation(String password, String salt) {
		return BCrypt.hashpw(password, salt);
	}

	public static String getPasswordDerivation(String password) {
		return getPasswordDerivation(password, BCrypt.gensalt(COST_FACTOR));
	}

}
