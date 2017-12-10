package doko.lineup;

import java.util.List;

public abstract class LineUp {

	public abstract List<Long> getIds();

	public abstract boolean isValid();

	public abstract String getLineUpString();

	public abstract int size();

	@Override
	public boolean equals(Object lineUp) {
		if (lineUp instanceof LineUp) {
			return getLineUpString().equals(((LineUp) lineUp).getLineUpString());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getLineUpString().hashCode();
	}
}
