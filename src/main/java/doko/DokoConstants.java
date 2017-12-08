package doko;

public abstract class DokoConstants {

	private DokoConstants() {}

	public static final String LOGIN_COOKIE_NAME = "remember_user";

	public static final int NUMBER_OF_TOP_LINEUPS = 5;

	public static final String SESSION_LOGIN_STATUS_ATTRIBUTE_NAME = "login_status";

	public static final String SESSION_USER_ID_ATTRIBUTE_NAME = "user_id";

	public static final String DEFAULT_LINEUP_STRING = "1,2,3,4";

	public static final String DATABASE_DATE_FORMAT = "yyyy-MM-dd hh:mm:ss.S";

	public static final String OUTPUT_DATE_FORMAT = "dd.MM.yy";
}
