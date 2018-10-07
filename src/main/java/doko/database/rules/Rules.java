package doko.database.rules;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "rules")
public class Rules implements Comparable<Rules> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(nullable = false)
	private String lineup;

	@Column(nullable = false)
	private Double version;

	@Column(nullable = false)
	private String text;

	public Rules(String lineup, Double version, String text) {
		this.lineup = lineup;
		this.version = version;
		this.text = text;
	}

	public Rules() {
	}

	public String getLineUpString() {
		return lineup;
	}

	public Double getVersion() {
		return version;
	}

	public String getText() {
		return text;
	}

	@Override
	public int compareTo(Rules otherRules) {
		if (getLineUpString().equals(otherRules.getLineUpString())) {
			return version.compareTo(otherRules.getVersion());
		}
		return getLineUpString().compareTo(otherRules.getLineUpString());
	}

	@Override
	public boolean equals(Object otherRules) {
		if (otherRules instanceof Rules) {
			return this.compareTo((Rules) otherRules) == 0;
		}
		return false;
	}

	@Override
	public int hashCode() {
		String string = getLineUpString() + getVersion().toString();
		return string.hashCode();
	}
}
