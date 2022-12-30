package doko.util;

import org.json.JSONObject;

public class RoundStruct {
	private String token;
	private JSONObject json;

	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public JSONObject getJson() {
		return json;
	}
	public void setJson(JSONObject json) {
		this.json = json;
	}
}
