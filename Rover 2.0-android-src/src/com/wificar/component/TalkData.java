package com.wificar.component;

public class TalkData {
	private int timestamp = 0;
	private int serial = 0;
	private int ticktime = 0;
	private int audioFormat = 0;
	
	private byte[] data;
	//private byte[] encodingData;
	private int sample = 0;
	private int index = 0;
	public TalkData(byte[] data, int sample, int index){
		this.data = data;

		this.sample = sample;
		this.index = index;
	}
	public TalkData(int ticktime, int serial, int timestamp, int audioFormat, byte[] data, int sample, int index){
		this.data = data;
		this.timestamp = timestamp;
		this.serial = serial;
		this.ticktime = ticktime; 
		this.audioFormat = audioFormat;
		this.sample = sample;
		this.index = index;
	}
	public void setSerial(int serial){
		this.serial = serial;
		
	}
	public int getSerial(){
		return this.serial;
	}
	
	public byte[] getData(){
		return data;
	}



	public void setParaSample(int paraSample) {
		this.sample = paraSample;
	}



	public int getParaIndex() {
		return index;
	}
	public int getParaSample() {
		return sample;
	}


	public void setParaIndex(int paraIndex) {
		this.index = paraIndex;
	}
	public int getTicktime(){
		return this.ticktime;
	}
	public void setTicktime(int ticktime){
		this.ticktime = ticktime;
	}


	public int getTimestamp() {
		return timestamp;
	}



	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}
}
