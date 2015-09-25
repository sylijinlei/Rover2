package com.wificar.util;

public class VideoUtility {
	public static String getVideoFile(String fileName){
		if(!fileName.endsWith(".avi")){
			fileName = fileName+".avi";
		}
		return fileName;
	}
}
