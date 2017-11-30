package doko.database.game;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import doko.lineup.LineUp;

public class SortedGame {

	private LineUp lineUp;
	private List<String> scores;
	private boolean valid;
	private Date date;

	public SortedGame(Game game) {
		lineUp = game.getLineUp();
		valid = lineUp.isValid();
		date = game.getDate();
		scores = new ArrayList<>();

		for (int i = 0; i < lineUp.size(); i++) {
			Long score = game.getScoreOf(lineUp.getIds()[i]);
			if (score == null) {
				valid = false;
				scores.add("");
			} else {
				scores.add(score.toString());
			}
		}
	}

	public LineUp getLineUp() {
		return lineUp;
	}

	public List<String> getScores() {
		return scores;
	}

	public boolean isValid() {
		return valid;
	}

	public Date getDate() {
		return date;
	}

	public List<String> getScoresWithDate() {
		List<String> scoresWithDate = new ArrayList<>();
		scoresWithDate.addAll(scores);
		scoresWithDate.add(date.toString());
		return scoresWithDate;
	}
}
