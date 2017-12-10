package doko.database.game;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import doko.lineup.LineUp;
import doko.lineup.UnnamedLineUp;

public class SortedGame {

	private LineUp lineUp;
	private List<Long> scores;
	private boolean valid;
	private Date date;

	public SortedGame(Game game) {
		lineUp = new UnnamedLineUp(game.getPlayerIds());
		scores = game.getScores();
		valid = lineUp.isValid();
		date = game.getDate();

		for (int i = 0; i < lineUp.size(); i++) {
			Long score = scores.get(i);
			if (score == null) {
				valid = false;
				scores.set(i, 0L);
			}
		}
	}

	public LineUp getLineUp() {
		return lineUp;
	}

	public List<Long> getScores() {
		return scores;
	}

	public boolean isValid() {
		return valid;
	}

	public Date getDate() {
		return date;
	}

	public List<Object> getScoresWithDate() {
		List<Object> scoresWithDate = new ArrayList<>();
		scoresWithDate.addAll(scores);
		scoresWithDate.add(date.toString());
		return scoresWithDate;
	}
}
