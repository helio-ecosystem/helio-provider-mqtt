package actions.utils;

import java.util.Map;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


public class Utils {

	public static final Gson GSON = new Gson();
	public static final ObjectMapper JACKSON_MAPPER = new ObjectMapper();
	static{
		JACKSON_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		JACKSON_MAPPER.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
	}

	public static String concatenate(String ... values) {
		StringBuilder str = new StringBuilder();

		for (String value : values) {
			str.append(value);
		}
		return str.toString();
	}

	public static JsonObject toJsonObject(String jsonObject) {
		return Utils.GSON.fromJson(jsonObject, JsonObject.class);
	}

	public static JsonArray toJsonArray(String jsonArray) {
		return Utils.GSON.fromJson(jsonArray, JsonArray.class);
	}

	public static String toJsonString(Map<String,Object> brand) {
		return Utils.GSON.toJson(brand);
	}
	
	
}
