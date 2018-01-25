package doko;

public abstract class DokoConstants {

	private DokoConstants() {}

	public static final String DEFAULT_LINEUP_STRING = "1,2,3,4";
	public static final int NUMBER_OF_TOP_LINEUPS = 5;

	public static final String LOGIN_COOKIE_NAME = "remember_user";
	public static final String SESSION_ERROR_MESSAGE_ATTRIBUTE_NAME = "error";
	public static final String SESSION_SUCCESS_MESSAGE_ATTRIBUTE_NAME = "success";
	public static final String SESSION_USER_ID_ATTRIBUTE_NAME = "user_id";

	public static final String INPUT_DATE_FORMAT = "E dd.MM.yyyy";
	public static final String OUTPUT_DATE_FORMAT = "dd.MM.yy";

	public static final String ADD_GAME_PAGE_LOCATION = "/report";
	public static final String ADD_PLAYER_PAGE_LOCATION = "/addplayer";
	public static final String ADD_ROUNDS_PAGE_LOCATION = "/addrounds";
	public static final String INDEX_PAGE_LOCATION = "/";
	public static final String LINE_UP_PAGE_LOCATION = "/lineup";
	public static final String LOGIN_PAGE_LOCATION = "/login";
	public static final String LOGOUT_PAGE_LOCATION = "/logout";
	public static final String PROFILE_PAGE_LOCATION = "/profile";
}
