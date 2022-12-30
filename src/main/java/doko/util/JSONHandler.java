package doko.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;

import doko.DokoConstants;
import doko.database.game.Game;
import doko.database.player.Player;
import doko.database.player.PlayerService;
import doko.database.round.Round;
import doko.database.token.Token;
import doko.database.token.TokenService;

public class JSONHandler {

	private JSONObject json;
	private PlayerService playerService;
	private TokenService tokenService;
	private String token;
	private boolean didAddPlayers = false;
	private String uniqueGameId;

	//TODO more transparency on adding users
	public JSONHandler(JSONObject gameJson, PlayerService playerService, TokenService tokenService, String token) {
		this.json = gameJson;
		this.playerService = playerService;
		this.tokenService = tokenService;
		this.token = token;
		// Generate a unique game id in order to prevent multiple uploads
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update(json.toString().getBytes(StandardCharsets.UTF_8));
			uniqueGameId = toHex(md.digest());
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("SHA-256 no longer supported. What?");
		}
	}

	private void addMissingPlayers() throws IOException {
		for (int i = 0; i < json.getJSONArray(DokoConstants.API_PLAYERS_KEY).length(); i++) {
			JSONObject playerJson = json.getJSONArray(DokoConstants.API_PLAYERS_KEY).getJSONObject(i);
			addPlayerIfNotExists(playerJson.getString(DokoConstants.API_PLAYER_NAME_KEY));
		}
	}

	private void addPlayerIfNotExists(String playerName) throws IOException {
		if (!playerService.existsPlayer(playerName)) {
			playerService.addPlayer(playerName);
		}
		if (!playerService.existsPlayer(playerName)) {
			throw new IOException(String.format("Could not add player %s.", playerName));
		}
	}

	public Game getGame() throws IOException, ParseException {
		if (!didAddPlayers) {
			addMissingPlayers();
			didAddPlayers = true;
		}

		SimpleDateFormat sdf = new SimpleDateFormat(DokoConstants.INPUT_DATE_FORMAT_API, Locale.GERMAN);
		Date date = sdf.parse(json.getString(DokoConstants.API_DATE_KEY));
		boolean isRanked = json.optBoolean(DokoConstants.API_RANKED_KEY, true);

		JSONObject player1Json = json.getJSONArray(DokoConstants.API_PLAYERS_KEY).getJSONObject(0);
		Long player1Id = getPlayerId(player1Json.getString(DokoConstants.API_PLAYER_NAME_KEY));
		Long score1 = player1Json.getLong(DokoConstants.API_SCORE_FINAL_KEY);
		JSONObject player2Json = json.getJSONArray(DokoConstants.API_PLAYERS_KEY).getJSONObject(1);
		Long player2Id = getPlayerId(player2Json.getString(DokoConstants.API_PLAYER_NAME_KEY));
		Long score2 = player2Json.getLong(DokoConstants.API_SCORE_FINAL_KEY);
		JSONObject player3Json = json.getJSONArray(DokoConstants.API_PLAYERS_KEY).getJSONObject(2);
		Long player3Id = getPlayerId(player3Json.getString(DokoConstants.API_PLAYER_NAME_KEY));
		Long score3 = player3Json.getLong(DokoConstants.API_SCORE_FINAL_KEY);
		JSONObject player4Json = json.getJSONArray(DokoConstants.API_PLAYERS_KEY).getJSONObject(3);
		Long player4Id = getPlayerId(player4Json.getString(DokoConstants.API_PLAYER_NAME_KEY));
		Long score4 = player4Json.getLong(DokoConstants.API_SCORE_FINAL_KEY);

		Optional<Long> submitted = tokenService.getUserIdOfToken(token);
		if (submitted.isPresent()) {
			return new Game(uniqueGameId, player1Id, score1, player2Id, score2,
					player3Id, score3, player4Id, score4, submitted.get(), date, isRanked);
		} else {
			throw new IOException("Couldn't determine submitting user.");
		}
	}

	private Long getPlayerId(String playerName) throws IOException {
		Optional<Player> player = playerService.getPlayerByName(playerName);
		if (player.isPresent()) {
			return player.get().getId();
		} else {
			throw new IOException("No such player.");
		}
	}

	public List<Round> getRounds() throws IOException {
		if (!didAddPlayers) {
			addMissingPlayers();
			didAddPlayers = true;
		}

		List<Round> rounds = new ArrayList<>();

		JSONObject player1Json = json.getJSONArray(DokoConstants.API_PLAYERS_KEY).getJSONObject(0);
		Long player1Id = getPlayerId(player1Json.getString(DokoConstants.API_PLAYER_NAME_KEY));
		JSONArray points1 = player1Json.getJSONArray(DokoConstants.API_SCORE_VALUES_KEY);
		JSONObject player2Json = json.getJSONArray(DokoConstants.API_PLAYERS_KEY).getJSONObject(1);
		Long player2Id = getPlayerId(player2Json.getString(DokoConstants.API_PLAYER_NAME_KEY));
		JSONArray points2 = player2Json.getJSONArray(DokoConstants.API_SCORE_VALUES_KEY);
		JSONObject player3Json = json.getJSONArray(DokoConstants.API_PLAYERS_KEY).getJSONObject(2);
		Long player3Id = getPlayerId(player3Json.getString(DokoConstants.API_PLAYER_NAME_KEY));
		JSONArray points3 = player3Json.getJSONArray(DokoConstants.API_SCORE_VALUES_KEY);
		JSONObject player4Json = json.getJSONArray(DokoConstants.API_PLAYERS_KEY).getJSONObject(3);
		Long player4Id = getPlayerId(player4Json.getString(DokoConstants.API_PLAYER_NAME_KEY));
		JSONArray points4 = player4Json.getJSONArray(DokoConstants.API_SCORE_VALUES_KEY);

		for (int i = 0; i < player1Json.getJSONArray(DokoConstants.API_SCORE_VALUES_KEY).length(); i++) {
			Round round = new Round(player1Id, points1.getLong(i),
					player2Id, points2.getLong(i),
					player3Id, points3.getLong(i),
					player4Id, points4.getLong(i),
					uniqueGameId, Long.valueOf(i));
			rounds.add(round);
		}
		return rounds;
	}

	public static String jsonify(Token token) {
		JSONObject json = new JSONObject();
		json.append("value", token.getTokenValue());
		json.append("expiration_date", token.getExpirationDate());
		return json.toString();
	}

	private static String toHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02X", b));
		}
		return sb.toString();
	}
}
