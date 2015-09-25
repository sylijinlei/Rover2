package com.wificar.util;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class WificarUtility {
	private static String appName = "";
	private final static String IR_PROPERTY="_IR_PROPERTY";
	private final static String SOUND_PROPERTY="_SOUND_PROPERTY";
	
	public final static String ACCOUNT_ID="_ACCOUNT_ID";
	public final static String ACCOUNT_PASSWORD="_ACCOUNT_PASSWORD";
	public final static String ACCOUNT_PORT="_ACCOUNT_PORT";
	public final static String ACCOUNT_HOST="_ACCOUNT_HOST";
	public final static String CONTROLLER_TYPE="_CONTROLLER_TYPE";
	public final static String WIFICAR_IR="_WIFICAR_IR";
	public final static String WIFICAR_MIC="_WIFICAR_MIC";
	public final static String VIDEO_FOLDER="_VIDEO_FOLDER";
	
	public static int[] getRandamNumber(){
		int[] val = new int[4];
		/*
		val[0] = (int)(System.currentTimeMillis()%1000);
		val[1] = (int)(System.currentTimeMillis()%1000);
		val[2] = (int)(System.currentTimeMillis()%1000);
		val[3] = (int)(System.currentTimeMillis()%1000);
		*/
		val[0] = 0xE8030000;
		val[1] = 0xD0070000;
		val[2] = 3000;
		val[3] = 4000;
		
		return val;
	}

	public static String getAppName(){
		return appName;
	}
	public static  void putStringVariable(Context context, String variableName, String value){
		int mode = Activity.MODE_PRIVATE;
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				WificarUtility.getAppName(), mode);

		// Save to preference
		SharedPreferences.Editor editor = sharedPreferences
				.edit();
		
		editor.putString(getAppName()+variableName, value);
		editor.commit();
	}
	public static String getStringVariable(Context context, String variableName, String defaultValue){
		int mode = Activity.MODE_PRIVATE;
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				getAppName(), mode);
		//Log.d("wild0","put shared:"+CosaUtility.getAppName()+variableName+","+value);
		return sharedPreferences.getString(getAppName()+variableName, defaultValue);
		// Save to preference
		
	}
	public static int getTimeFrom1970(){
		return (int)(System.currentTimeMillis()/1000);
	}
	
	public static  void putIntVariable(Context context, String variableName, int value){
		int mode = Activity.MODE_PRIVATE;
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				WificarUtility.getAppName(), mode);

		// Save to preference
		SharedPreferences.Editor editor = sharedPreferences
				.edit();
		
		editor.putInt(getAppName()+variableName, value);
		editor.commit();
	}
	public static int getIntVariable(Context context, String variableName, int defaultValue){
		int mode = Activity.MODE_PRIVATE;
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				getAppName(), mode);
		//Log.d("wild0","put shared:"+CosaUtility.getAppName()+variableName+","+value);
		return sharedPreferences.getInt(getAppName()+variableName, defaultValue);
		// Save to preference
		
	}
}
