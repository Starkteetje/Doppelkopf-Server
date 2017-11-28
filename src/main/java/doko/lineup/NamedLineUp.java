package doko.lineup;

import java.util.List;
import java.util.stream.Collectors;

public class NamedLineUp extends LineUp {
	
	private Long[] ids;
	private String lineUpString;
	private boolean valid;
	private String lineUpName;
	private static int ABBREVIATED_NAME_LENGTH = 2;
	
	public NamedLineUp(LineUp lineUp, List<String> playerNames) {
		ids = lineUp.getIds();
		lineUpString = lineUp.getLineUpString();
		valid = lineUp.isValid();
		List<String> shortNames = playerNames.stream()
				.map(NamedLineUp::subStringOfN)
				.collect(Collectors.toList());
		lineUpName = String.join("", shortNames) + "-Runde";
	}
	
	public String getLineUpName() {
		return lineUpName;
	}

	public Long[] getIds() {
		return ids;
	}

	public boolean isValid() {
		return valid;
	}

	public String getLineUpString() {
		return lineUpString;
	}

	public int size() {
		return ids.length;
	}
	
	private static String subStringOfN(String string) {
		if (string.length() <= ABBREVIATED_NAME_LENGTH) {
			return string;
		}
		return string.substring(0, ABBREVIATED_NAME_LENGTH);
	}
}
