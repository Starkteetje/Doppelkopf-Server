package doko.database.season;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import doko.database.game.SortedGame;

@Entity
@Table(name = "seasons")
public class Season {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(nullable = false)
	private String lineUpString;

	@Column(nullable = false)
	private Date fromDate;

	@Column(nullable = true)
	private Date toDate;

	public Season(String lineUpString, Date fromDate, Date toDate) {
		this.lineUpString = lineUpString;
		this.fromDate = fromDate;
		this.toDate = toDate;
	}

	public Season() {
	}

	public Long getId() {
		return id;
	}

	public String getLineUpString() {
		return lineUpString;
	}

	public Date getFrom() {
		return fromDate;
	}

	public Date getTo() {
		return toDate;
	}

	public boolean contains(SortedGame game) {
		return fromDate.before(game.getDate()) && (toDate == null || toDate.after(game.getDate()));
	}
}
