package doko.database.game;

import java.util.ArrayList;
import java.util.List;

import doko.LineUp;

public class SortedGame {
	
	private LineUp lineUp;
	private List<String> scores;
	private boolean valid;

	public SortedGame(Game game) {
		lineUp = game.getLineUp();
		valid = lineUp.isValid();
		scores = new ArrayList<String>();
		
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
}
