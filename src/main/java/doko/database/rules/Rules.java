package doko.database.rules;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "rules")
public class Rules {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(nullable = false)
	private String lineup;

	@Column(nullable = false)
	private Double version;

	@Column(nullable = false)
	private String rules;

	public Rules(String lineup, Double version, String rules) {
		this.lineup = lineup;
		this.version = version;
		this.rules = rules;
	}

	public Rules() {
	}

	public String getLineUpString() {
		return lineup;
	}

	public Double getVersion() {
		return version;
	}

	public String getRules() {
		return rules;
	}
}
