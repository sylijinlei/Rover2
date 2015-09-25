package com.wificar.mediaplayer;

public class JNIWificarVideoPlay {
	public static native int playerInit(String path);
    public static native int playerStart();
    public static native int playerPause();
    public static native int playerResume();
    public static native int playerSeek(int time);
    public static native int playerStop();
 
    public static native byte[] videoRender();
    public static native byte[] audioRender(byte[] samplerateArray, int samplerateSize, 
    		                         byte[] channelsArray, int channelsSize,
    		                         byte[] formatArray, int formatchannelsSize);
    
    public static native int getVideoWidth();
    public static native int getVideoHeight();
    public static native int playerIsStop();
    
    public static native int getDuration();
    public static native int getCurrentTime();
    
    public static native byte[] getVideoSnapshot(String filePath);
    
    static{
    	System.loadLibrary("decoder");
    	System.loadLibrary("videodecoder");
    }
}
