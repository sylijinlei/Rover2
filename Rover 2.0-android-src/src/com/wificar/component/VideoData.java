package com.wificar.component;

import android.util.Log;

public class VideoData {
	private long timestamp = 0;
	private int time = 0;

	
	private byte[] data;

	public VideoData(long timestamp, int time, byte[] data){
		this.data = data;
		this.timestamp = timestamp;
		
		this.time = time; 
		//Log.d("wild3","timestamp:"+timestamp+",timetick:"+time);
		
	}
	public byte[] getData(){
		return data;
	}
	public long getTimestamp() {
		// TODO Auto-generated method stub
		return timestamp;
	}
	private long customTimestamp = System.currentTimeMillis();
	public long getCustomTimestamp(){
		return customTimestamp;
	}
	public void setCustomTimestamp( long timestamp ){
		customTimestamp = timestamp;
		return;
	}
	int delay = 0;
	public void setDelay(int timeInterval) {
		// TODO Auto-generated method stub
		this.delay = timeInterval;
	}
	public int getDelay() {
		// TODO Auto-generated method stub
		return delay;
	}
	
	int customDelay = 0;
	public void setCustomDelay(int timeInterval) {
		// TODO Auto-generated method stub
		this.customDelay = timeInterval;
	}
	public int getCustomDelay() {
		// TODO Auto-generated method stub
		return customDelay;
	}
}