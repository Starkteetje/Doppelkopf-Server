package doko.lineup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UnnamedLineUp extends LineUp {

	private List<Long> ids;
	private String lineUpString;
	private boolean valid;
	private static final int LINEUP_LENGTH = 4;

	public UnnamedLineUp(Long... ids) {
		this.ids = new ArrayList<>();
		for (Long id : ids) {
			this.ids.add(id);
		}
		this.ids.sort(UnnamedLineUp::compare);
		valid = isValid(this.ids);
		lineUpString = getLineUpString(this.ids);
	}

	public UnnamedLineUp(List<Long> ids) {
		this(ids.toArray(new Long[ids.size()]));
	}

	public UnnamedLineUp(String... idStrings) {
		// Long::new does not "sanitize" input, e.g. leading spaces break it
		// Double::new does it however, thus an extra step
		this(Arrays.stream(idStrings).map(idString -> {
			try {
				Double id = new Double(idString);
				if (Double.isNaN(id) || !Double.isFinite(id)) {
					return null;
				}
				return id.longValue();
			} catch (NumberFormatException e) {
				return null;
			}
		}).toArray(Long[]::new));
	}

	public UnnamedLineUp(String lineUpString) {
		this(lineUpString.split(","));
	}

	public List<Long> getIds() {
		return ids;
	}

	public String getLineUpString() {
		return lineUpString;
	}

	protected static String getLineUpString(List<Long> ids) {
		ids.sort(UnnamedLineUp::compare);
		return String.join(",", ids.stream().map(id -> id == null ? "-1" : id.toString()).toArray(String[]::new));
	}

	public boolean isValid() {
		return valid;
	}

	private static boolean isValid(List<Long> ids) {
		return ids.size() == LINEUP_LENGTH
				&& !containsInvalidLong(ids)
				&& ids.stream().distinct().toArray().length == LINEUP_LENGTH;
	}

	private static boolean containsInvalidLong(List<Long> ids) {
		for (Long id : ids) {
			if (id == null || id < 1) {
				return true;
			}
		}
		return false;
	}

	private static int compare(Long l1, Long l2) {
		if (l1 == null) {
			if (l2 == null) {
				return 0; // null == null
			} else {
				return -1; // null < Long
			}
		} else if (l2 == null) {
			return 1; // Long > null
		} else {
			return l1.compareTo(l2);
		}
	}

	public int size() {
		return ids.size();
	}

	public boolean contains(Long playerid) {
		return getIds().contains(playerid);
	}
}
