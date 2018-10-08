package doko;

public abstract class DokoConstants {

	private DokoConstants() {}

	public static final String DEFAULT_LINEUP_STRING = "1,2,3,4";
	public static final int NUMBER_OF_TOP_LINEUPS = 5;

	public static final String LOGIN_COOKIE_NAME = "remember_user";
	public static final String SESSION_ERROR_MESSAGE_ATTRIBUTE_NAME = "error";
	public static final String SESSION_SUCCESS_MESSAGE_ATTRIBUTE_NAME = "success";
	public static final String SESSION_USER_ID_ATTRIBUTE_NAME = "user_id";

	public static final String INPUT_DATE_FORMAT_WEBSITE = "E dd.MM.yyyy";
	public static final String INPUT_DATE_FORMAT_API = "yyyy-MM-dd";
	public static final String OUTPUT_DATE_FORMAT = "dd.MM.yy";

	public static final String API_PLAYERS_KEY = "players";
	public static final String API_SCORE_FINAL_KEY = "final";
	public static final String API_SCORE_VALUES_KEY = "points";
	public static final String API_DATE_KEY = "date";
	public static final String API_PLAYER_NAME_KEY = "name";

	public static final String ADD_GAME_PAGE_LOCATION = "/report";
	public static final String ADD_PLAYER_PAGE_LOCATION = "/addplayer";
	public static final String INDEX_PAGE_LOCATION = "/";
	public static final String LINE_UP_PAGE_LOCATION = "/lineup";
	public static final String LOGIN_PAGE_LOCATION = "/login";
	public static final String LOGOUT_PAGE_LOCATION = "/logout";
	public static final String PROFILE_PAGE_LOCATION = "/profile";
	public static final String GAME_PAGE_LOCATION = "/game";

	public static final String API_LOGIN_LOCATION = "/api/login";
	public static final String API_ADD_GAME_LOCATION = "/api/report";
}
