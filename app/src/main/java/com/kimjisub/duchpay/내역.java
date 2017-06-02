package com.kimjisub.duchpay;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by jisub on 2015-07-16.
 */
public class 내역 {
	String 제목 = null;
	String[] 사용인원 = null;
	boolean 소비지불 = false;
	boolean 개인단체 = false;
	long 돈 = 0;
	long 시간 = 0;

	
	public 내역() {
	}
	public 내역(JSONObject JsonObject) {
		this(JsonObject.toString());
	}
	public 내역(String JsonString) {
		try {
			JSONObject JsonObject = new JSONObject(JsonString);
			제목 = JsonObject.getString("title");
			
			JSONArray json배열 = JsonObject.getJSONArray("people");
			사용인원 = new String[json배열.length()];
			for (int i = 0; i < json배열.length(); i++)
				사용인원[i] = json배열.getString(i);
			
			소비지불 = JsonObject.getBoolean("o1");
			개인단체 = JsonObject.getBoolean("o2");
			돈 = JsonObject.getLong("money");
			시간 = JsonObject.getLong("time");
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public JSONObject toJSONObject() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("title", 제목);
			
			JSONArray jsonArray = new JSONArray();
			for (int i = 0; i < 사용인원.length; i++)
				jsonArray.put(사용인원[i]);
			jsonObject.put("people", jsonArray);
			
			jsonObject.put("o1", 소비지불);
			jsonObject.put("o2", 개인단체);
			jsonObject.put("money", 돈);
			jsonObject.put("time", 시간);
			
			return jsonObject;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static JSONArray listToJSONArray(ArrayList<내역> data){
		JSONArray JsonArray = new JSONArray();
		for(int i=0;i<data.size();i++){
			JsonArray.put(data.get(i).toJSONObject());
		}
		return JsonArray;
	}
	
	public static ArrayList<내역> JSONArrayToList(JSONArray JsonArray){
		ArrayList<내역> ret = new ArrayList<>();
		
		try {
			for(int i=0;i<JsonArray.length();i++){
				ret.add(new 내역(JsonArray.getJSONObject(i)));
			}
			return ret;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
}
