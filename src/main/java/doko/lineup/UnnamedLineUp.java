package doko.lineup;

import java.util.Arrays;
import java.util.Comparator;

public class UnnamedLineUp extends LineUp {

	private Long[] ids;
	private String lineUpString;
	private boolean valid;
	private static int LINEUP_LENGTH = 4;

	public UnnamedLineUp(Long... ids) {
		this.ids = ids;
		Arrays.sort(this.ids, nullComparator);
		valid = isValid(this.ids);
		lineUpString = getLineUpString(this.ids);
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

	public Long[] getIds() {
		return ids;
	}

	public String getLineUpString() {
		return lineUpString;
	}

	protected static String getLineUpString(Long[] ids) {
		Arrays.sort(ids, nullComparator);
		return String.join(",", Arrays.stream(ids).map(id -> id == null ? "-1" : id.toString()).toArray(String[]::new));
	}

	public boolean isValid() {
		return valid;
	}

	private static boolean isValid(Long[] ids) {
		return ids.length == LINEUP_LENGTH
				&& !containsInvalidLong(ids)
				&& Arrays.stream(ids).distinct().toArray().length == LINEUP_LENGTH;
	}

	private static boolean containsInvalidLong(Long[] ids) {
		for (Long id : ids) {
			if (id == null || id < 1) {
				return true;
			}
		}
		return false;
	}

	private static Comparator<Long> nullComparator = new Comparator<Long>() {
		public int compare(Long l1, Long l2) { // null < long
			return l1 == null ? (l2 == null ? 0 : -1) : (l2 == null ? 1 : l1.compareTo(l2));
		}
	};

	public int size() {
		return ids.length;
	}
}
