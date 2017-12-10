package doko.lineup;

import java.util.List;
import java.util.stream.Collectors;

import doko.database.player.Player;
import doko.database.player.PlayerService;

public class NamedLineUp extends LineUp {

	private List<Long> ids;
	private List<Player> players;
	private String lineUpString;
	private boolean valid;
	private String lineUpName;
	private static final int ABBREVIATED_NAME_LENGTH = 2;

	public NamedLineUp(LineUp lineUp, PlayerService playerService) {
		ids = lineUp.getIds();
		lineUpString = lineUp.getLineUpString();
		valid = lineUp.isValid();
		players = playerService.getPlayers(ids);
		players.sort(null); // Sorting players in their defined order is necessary to assure that players are in the same order as the ids
		List<String> shortNames = players.stream()
				.map(Player::getName)
				.map(NamedLineUp::subStringOfN)
				.collect(Collectors.toList());
		lineUpName = String.join("", shortNames) + "-Runde";
	}

	public List<Long> getIds() {
		return ids;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public boolean isValid() {
		return valid;
	}

	public int size() {
		return ids.size();
	}

	public String getLineUpString() {
		return lineUpString;
	}

	public String getLineUpName() {
		return lineUpName;
	}

	private static String subStringOfN(String string) {
		if (string.length() <= ABBREVIATED_NAME_LENGTH) {
			return string;
		}
		return string.substring(0, ABBREVIATED_NAME_LENGTH);
	}
}
