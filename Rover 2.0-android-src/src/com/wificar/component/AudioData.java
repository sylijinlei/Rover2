package com.wificar.component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.util.Log;

public class AudioData {
	
	private int serial = 0;
	private int timetick = 0;
	private long timestamp = 0;
	private int audioFormat = 0;
	
	private byte[] data;
	private byte[] encodingData;
	private int sample = 0;
	private int index = 0;
	public AudioData(long timestamp, int serial, int  timetick, int audioFormat, byte[] data, int sample, int index){
		this.data = data;
		this.timestamp = timestamp;
		this.serial = serial;
		this.timetick = timetick; 
		this.audioFormat = audioFormat;
		this.sample = sample;
		this.index = index;
		Log.d("wild2","timestamp:"+timestamp+",timetick:"+timetick);
	}
	public AudioData(int audioFormat, byte[] data, int sample, int index){
		this.data = data;//adpcm
		this.audioFormat = audioFormat;
		this.sample = sample;
		this.index = index;
	}
	public void setPCMData(byte[] pcmData){
		encodingData = pcmData;
	}
	public byte[] getPCMData(){
		return encodingData;
	}
	public byte[] getADPCMData(){
		return data;
	}
	public byte[] getADPCMDataWithSample() throws IOException{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(sample & 0xff);
		baos.write((sample & 0xff) >> 8);
		baos.write(index & 0xff);
		baos.write(0);

		baos.write(getADPCMData());
		return baos.toByteArray();
	}

	public byte[] getPCMFromeADPCM() throws IOException{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();


		byte[] bDecoded = AudioComponent.decodeADPCMToPCM(getADPCMData(),
			160, sample, index);



		baos.write(bDecoded);
		return baos.toByteArray();
	}
	private long customTimestamp = System.currentTimeMillis();
	public long getCustomTimestamp(){
		return customTimestamp;
	}


	public void setParaSample(int paraSample) {
		this.sample = paraSample;
	}



	public int getParaIndex() {
		return index;
	}



	public void setParaIndex(int paraIndex) {
		this.index = paraIndex;
	}



	public long getTimestamp() {
		return timestamp;
	}
	public int getTimeTick() {
		return timetick;
	}


	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public static AudioData createEmptyPCMData(int length, int index,  long timestamp){
		byte[] data = new byte[length];
		
		for (int i = 0; i < length; i++) {
			data[i] = (byte) 0x00;
		}
		//AudioComponent.encodeAdpcm(data, length, sample, index);
		byte[] raw = AudioComponent.encodePCMToADPCM(data, length, 0, 0);
		
		AudioData empty = new AudioData(1, raw, raw[0], index);
		
		//byte[] bDecoded = AudioComponent.decodeAdpcm(data,
		//		length, data[0], index);
		// audio.writeAudioData(bDecoded);
		empty.setPCMData(data);
		
		//empty.setTimestamp(timestamp);
		return empty;
	}
	public int getSerial(){
		return this.serial;
	}


	

}
