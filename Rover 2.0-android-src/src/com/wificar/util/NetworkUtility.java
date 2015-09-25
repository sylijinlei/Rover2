package com.wificar.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import com.wificar.WificarActivity;

import android.util.Log;

public class NetworkUtility {
	public static String getURLContent(String url) {
		URLConnection conn;
		// DataInputStream dis = null;

		StringBuffer sb = new StringBuffer();
		try {
			
			if(WificarActivity.getInstance().getWifiCar().isConnected()==0){
				return "";
			}
			Log.d("network", url);
			URL updateURL = new URL(url);
			conn = updateURL.openConnection();

			BufferedReader rd = new BufferedReader(new InputStreamReader(conn
					.getInputStream(), "UTF8"));

			String s = "";
			while ((s = rd.readLine()) != null) {
				sb.append(s);
			}
		} catch (Exception e) {
			Log.d("network", "error");
			e.printStackTrace();
		}
		return sb.toString();
	}
}
