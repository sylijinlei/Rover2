package com.wificar.component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Vector;

import android.util.Log;

import com.wificar.util.AVIGenerator;

public class VideoComponent implements Runnable {
	private static Vector<AudioData> audioDatas = new Vector<AudioData>();
	private static Vector<VideoData> videoDatas = new Vector<VideoData>();
	public int debugtemp=0;
	public int debugtemp2=0;
	AVIGenerator aviGenerator = null;
	long lastVideoFrameTimestamp = 0;
	long lastAudioFrameTimestamp = 0;
	long lastVideoFrameCustomTimestamp = 0;
	long lastAudioFrameCustomTimestamp = 0;
	long lastCustomTimestampInterval = 0;
	int state = 0; // recording is 1
	int discard_frame_num = 0;
	WifiCar car = null;
	public VideoComponent(WifiCar car){
		this.car = car;
	}
	public void pushVideoData(VideoData vData, int mark) throws Exception {
		//Log.d("record1",System.currentTimeMillis() + "videoData:"+ vData.getTimestamp() + ":" + mark);
		if (state == 1) {
			 Log.i("record3",System.currentTimeMillis()+"videoData:"+vData.getTimestamp()+":"+mark);
			// aviGenerator.addImage(vData.getData());
				
			long customTimestampInterval = vData.getCustomTimestamp()-getLastVideoFrameCustomTimestamp();
			if(customTimestampInterval>1000){
				customTimestampInterval = 0;
			}
			/*
			if(lastCustomTimestampInterval == 0)
			{
				lastCustomTimestampInterval = customTimestampInterval;
			}
			else
			{
				lastCustomTimestampInterval = (lastCustomTimestampInterval + customTimestampInterval)/2;
				long new_custom_time_stamp = lastVideoFrameCustomTimestamp + lastCustomTimestampInterval;
				vData.setCustomTimestamp(new_custom_time_stamp);
			}
			*/
			//else if(timeInterval<30){
			//	return ;
			//}
			if(car.getAudioFlag()==0){
				//ÀR­µªº¸ê®Æ
				
				//timeInterval°±¤î®É¶¡
				//Log.d("recordvideo", "^^^^^^empty:"+customTimestampInterval+"="+customTimestampInterval*16);
				//AudioData audioData = AudioData.createEmptyPCMData(2560,0, vData.getTimestamp());
				//pushAudioData(audioData,1);
			}
			vData.setCustomDelay((int)customTimestampInterval);
			
			lastVideoFrameTimestamp = vData.getTimestamp();
			lastVideoFrameCustomTimestamp = vData.getCustomTimestamp();
			//Log.d("vipushtime","timestamp"+lastVideoFrameTimestamp+"cutimestamp"+lastVideoFrameCustomTimestamp);
			if( discard_frame_num == 0 )
			{
				debugtemp++;
				videoDatas.add(vData);
				
				int n=videoDatas.size()/30;
				if( n > 0 ) discard_frame_num = n;
			}
			else
			{
				discard_frame_num--;
			}
		}
	}
	public long getLastVideoFrameTimestamp(){
		return lastVideoFrameTimestamp;
	}
	public long getLastAudioFrameTimestamp(){
		return lastAudioFrameTimestamp;
	}
	public long getLastVideoFrameCustomTimestamp(){
		return lastVideoFrameCustomTimestamp;
	}
	public long getLastAudioFrameCustomTimestamp(){
		return lastAudioFrameCustomTimestamp;
	}

	public void pushAudioData(AudioData aData, int mark) throws Exception {
		//Log.d("record2",
		//		System.currentTimeMillis() + "audioData:"
		//				+ aData.getTimestamp() + ":" + mark);
		if (state == 1) {

			byte[] data = aData.getADPCMDataWithSample();
			// aviGenerator.addAudio(data, 0, data.length);
			debugtemp2++;
			//Log.d("pushaudiodata","debugtem2="+debugtemp2);
			audioDatas.add(aData);
			lastAudioFrameTimestamp = aData.getTimestamp();
			lastAudioFrameCustomTimestamp = aData.getCustomTimestamp();
			//Log.d("aupushtime","timestamp"+lastAudioFrameTimestamp+"cutimestamp"+lastAudioFrameCustomTimestamp);
			// lastFrameTime = aData.getTimestamp();
		}
	}

	public void start(String path, String fileName, int width, int height)
			throws Exception {
		Log.e("file", "save as:"+path+","+fileName);
		File avi = new File(path, fileName);
		aviGenerator = new AVIGenerator(avi);
		aviGenerator.addVideoStream(height, width);
		aviGenerator.addAudioStream();
		aviGenerator.startAVI();
		
		state = 1;
		//lastVideoFrameTime = 0;
		Thread t = new Thread(this);
		t.setName("FLIM Thread");
		t.start();
	}

	public void stop() throws Exception {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		state = 0;
		//aviGenerator.finishAVI();
	}

	public int check() {
		if (state == 0) {
			//¨S¦³¿ý»s
			if (videoDatas.size() == 0 && audioDatas.size() == 0) {
				//¨â­Ódata¬Ò¬°0
				return 1;
			} else if (videoDatas.size() == 0 || audioDatas.size() == 0) {
				
				//¨ä¤¤£‰­Ó¬°0
				return -1;
			} else {

			}
		} else if (state == 1)  {
			// ¶}©l¿ý»s state=1
			
		}
		return 0;
		// ¤‘£ž¤£·|µo¥Í¨S¸ê®Æ£

		// AudioData fad = audioDatas.get(0);
		// VideoData fvd = videoDatas.get(0);

		// long fadTime = fad.getTimestamp();
		// long fvdTime = fvd.getTimestamp();

		// if(state==0 && videoDatas.size()==0){
		// Log.d("handler","finish record");
		// return 1;//µ²§ô¿ý¼v
		// }
		// else{

		// }

		/*
		 * if(Math.abs(fadTime-fvdTime)>50 & fadTime>fvdTime){
		 * //videoDatas.remove(0); //return 1; } else
		 * if(Math.abs(fadTime-fvdTime)>50 & fvdTime>fadTime){
		 * //audioDatas.remove(0);
		 * 
		 * }
		 */
		/*
		 * if(audioDatas.size()==0){ return -1; } if(videoDatas.size()==0){
		 * return -1; } AudioData fadN = audioDatas.get(0); VideoData fvdN =
		 * videoDatas.get(0);
		 */
		// long fadNTime = fadN.getTimestamp();
		// long fvdNTime = fvdN.getTimestamp();

		/*
		 * if(Math.abs(fadTime-fvdTime)<50){ return 0; } else{ return -1; }
		 * 
		 * /* if(fad.getTimestamp()>fvd.getTimestamp()){ videoDatas.remove(0);
		 * return 1; } else if(fad.getTimestamp()<fvd.getTimestamp()){
		 * audioDatas.remove(0); return -1; } return 0;
		 */
	}

	public void preProcess() {
		//§¹¦¨³oµ{§Ç,video ¸òaudioªº»~®t­n«Ü¦e(50¤‘¤º)
		if (videoDatas.size() != 0 && audioDatas.size() != 0) {
			//¨â­Ó¬Ò¤£¬°0
			AudioData fad = audioDatas.get(0);
			VideoData fvd = videoDatas.get(0);

			long fadTime = fad.getTimestamp();
			long fvdTime = fvd.getTimestamp();

			//Log.d("handler", "video size:" + videoDatas.size());
			//Log.d("handler", "audio size:" + audioDatas.size());

			while (Math.abs(fadTime - fvdTime) > 5 && fadTime > fvdTime) {
				videoDatas.remove(0);
				//Log.d("handler", "remove video timestamp:" + fvdTime);
				if(videoDatas.size()==0){
					break;
				}
				else{
					fvd = videoDatas.get(0);
					fvdTime = fvd.getTimestamp();
				}
				// return 1;
			}
			while (Math.abs(fadTime - fvdTime) > 5 && fvdTime > fadTime) {
				audioDatas.remove(0);
				//Log.d("handler", "remove audio timestamp:" + fvdTime);
				if(audioDatas.size()==0){
					break;
				}
				else{
					fad = audioDatas.get(0);
					fadTime = fad.getTimestamp();
				}
			}
			//Log.d("handler", "end");
		}
	}

	
	public void run() {
		// TODO Auto-generated method stub
		//§I°±£‰¬í¥s¶}©l
		int videonum=0;
		int audionum=0;
		int frame_numb=0;
		long firsttimestamp=0;
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		preProcess();
		long lastCustomTimestamp = 0;
		long lastTimestamp = 0;
		long currentTimestamp = System.currentTimeMillis();
		//int result = check();//1:·|°±¤î¿ý»s
		//Log.d("recordvideo","===================start record at");
		//Log.d("recordvideo","video frame timestamp:"+videoDatas.get(0).getTimestamp());
		//Log.d("recordvideo","audio frame timestamp:"+audioDatas.get(0).getTimestamp());
		//Log.d("preprocess","debugtemp="+debugtemp+"debugtemp2="+debugtemp2+"currenttimestamp="+currentTimestamp);
		
		videonum=videoDatas.size();
		audionum=audioDatas.size();
		while (state==1) {
			//try {
			//	while(videoDatas.size()==0){
					
			//	}
			//} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
			//	e1.printStackTrace();
			//}
			// Log.d("handler","process:"+result);
			// if(audioDatas.size()==0 || videoDatas.size()==0) continue;
			//if (result == 0) {
			
			
				
				
				if (videoDatas.size() > 0) {
					VideoData fvd = videoDatas.get(0);
					//0725 ÐÞ¸ÄÏÂÃæµÄ´úÂë£¬ÔÚifÅÐ¶ÏºóÃæ¼ÓÉÏ´óÀ¨ºÅ£¬µ±VideoDataÎª¿ÕÊ±£¬¼ÌÐøÍùÏÂÖ´ÐÐ
					if( fvd == null ) {
						Log.e("recordvideo","****fvd is null!!!***");
						continue ;
					}
					/*
					try {
						Thread.sleep(fvd.getDelay());
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					*/
					
					lastCustomTimestamp = fvd.getCustomTimestamp();
					lastTimestamp = fvd.getTimestamp();
					//Log.d("vigettime","timestamp"+lastTimestamp+"cutimestamp"+lastCustomTimestamp);
					if(frame_numb==0){
						 frame_numb=1;
						 firsttimestamp=lastCustomTimestamp;
						}
				
						
					try {
						long t = System.currentTimeMillis()- currentTimestamp;
						if(t<fvd.getCustomDelay()){
							//Log.d("virun","sleep"+(fvd.getCustomDelay()-t));
							Thread.sleep(fvd.getCustomDelay()-t);
						}
						
						if(!aviGenerator.addImage(fvd.getData())){
							break;
						}
						videoDatas.remove(0);
						currentTimestamp = System.currentTimeMillis();
						//Log.d("recordvideo","*********video frame timestamp:"+fvd.getTimestamp()+" at "+System.currentTimeMillis()+"["+fvd.getCustomDelay()+"]");
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
				else{
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				
				try {
					long lastAudioTimestamp = 0;
					//if (audioDatas.size() > 0) {
					while(audioDatas.size() > 0 && audioDatas.get(0).getTimestamp() <= lastTimestamp )
					{
						AudioData fad = audioDatas.get(0);
						lastAudioTimestamp = fad.getTimestamp();
					byte[] data;
					
				//		data = fad.getADPCMDataWithSample();
				         data = fad.getPCMFromeADPCM();
						aviGenerator.addAudio(data, 0, data.length);
						audioDatas.remove(0);
						//Log.d("recordvideo","---------audio frame timestamp:"+fad.getTimestamp());
					
					
					}
					
					//Log.d("test full","videodatasize="+videoDatas.size()+"audiodatasize="+audioDatas.size()+"adtime="+lastAudioTimestamp+"lsttstamp="+lastTimestamp);
				
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//Log.d("while data start li","videoDatas.size()="+videonum+"audioDatas.size()="+audionum);
				//Log.d("while data li","videoDatas.size()="+videoDatas.size()+"audioDatas.size()="+audioDatas.size());
			//}
			//result = check();
			//Log.d("recordvideo","result:"+result);
		}
		//Log.d("while data start","videoDatas.size()="+videonum+"audioDatas.size()="+audionum);
		//Log.d("while data","videoDatas.size()="+videoDatas.size()+"audioDatas.size()="+audioDatas.size());
		try {
			aviGenerator.finishAVI(lastCustomTimestamp-firsttimestamp);
			audioDatas.clear();
			videoDatas.clear();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Log.d("recordvideo","end recording");
	}

}
