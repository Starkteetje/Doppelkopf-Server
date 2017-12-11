package doko.random;

import java.security.SecureRandom;

public class RandomProvider {

	private static final String HEX_CHARS = "0123456789ABCDEF";

	private SecureRandom random = new SecureRandom();

	public String getRandomHexString(int length) {
		StringBuilder sb = new StringBuilder();
		while (sb.length() < length) {
			sb.append(HEX_CHARS.charAt(random.nextInt(HEX_CHARS.length())));
		}
		return sb.toString();
	}
}
