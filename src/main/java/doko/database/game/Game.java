package doko.database.game;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import doko.LineUp;

@Entity
@Table(name = "scores")
public class Game {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
	
	@Column(nullable = false)
    private Long player1;
	
	@Column(nullable = false)
    private Long score1;
	
	@Column(nullable = false)
    private Long player2;
	
	@Column(nullable = false)
    private Long score2;
	
	@Column(nullable = false)
    private Long player3;
	
	@Column(nullable = false)
    private Long score3;
	
	@Column(nullable = false)
    private Long player4;
	
	@Column(nullable = false)
    private Long score4;
	
	@Column(nullable = false)
    private Long submitted;
	
	@Column(nullable = false)
    private Date date;
	
	public Game(Long player1, Long score1, Long player2, Long score2, Long player3, Long score3, Long player4, Long score4, Long submitted, Date date) {
	    this.player1 = player1;
	    this.score1 = score1;
	    this.player2 = player2;
	    this.score2 = score2;
	    this.player3 = player3;
	    this.score3 = score3;
	    this.player4 = player4;
	    this.submitted = submitted;
	    this.date = date;
	}
	
	public Game() {
	}
	
	public LineUp getLineUp() {
		return new LineUp(player1, player2, player3, player4);
	}
	
	public Long getScoreOf(Long playerId) {
		if (playerId.equals(player1)) return score1;
		if (playerId.equals(player2)) return score2;
		if (playerId.equals(player3)) return score3;
		if (playerId.equals(player4)) return score4;
		return null;
	}
	
	public String prettyPrint() {
		return player1 + "scored " + score1 + "points, " + player2 + "scored " + score2 + "points, " + player3 + "scored " + score3 + "points, " + player4 + "scored " + score4 + "points. Submitted at " + date.toString();
	}
}