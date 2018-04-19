package com.kimjisub.duchpay;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by kimjisub on 2017. 5. 31..
 */

public class setting {
	
	static class dataSave {
		static void save(Context context, String value) {
			SharedPreferences pref = context.getSharedPreferences("data", MODE_PRIVATE);
			SharedPreferences.Editor editor = pref.edit();
			editor.putString("dataSave", value);
			editor.commit();
		}
		
		static String load(Context context) {
			SharedPreferences pref = context.getSharedPreferences("data", MODE_PRIVATE);
			return pref.getString("dataSave", "");
		}
	}
	
	static class unit {
		static void save(Context context, String value) {
			SharedPreferences pref = context.getSharedPreferences("data", MODE_PRIVATE);
			SharedPreferences.Editor editor = pref.edit();
			editor.putString("unit", value);
			editor.commit();
		}
		
		static String load(Context context) {
			SharedPreferences pref = context.getSharedPreferences("data", MODE_PRIVATE);
			return pref.getString("unit", "원");
		}
	}
}
