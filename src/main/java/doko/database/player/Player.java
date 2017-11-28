package doko.database.player;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "players")
public class Player implements Comparable<Player> {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
	
	@Column(nullable = false)
    private String name;
	
	public Player(String name) {
		this.name = name;
	}
	
	public Player() {
	}
	
	public String getName() {
		return name;
	}

	@Override
	public int compareTo(Player otherPlayer) {
		return Long.compare(id, otherPlayer.id);
	}
	
	@Override
	public boolean equals(Object otherPlayer) {
		if (otherPlayer instanceof Player) {
			return id == ((Player)otherPlayer).id;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return (int) id;
	}
}
