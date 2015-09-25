package com.wificar.component;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.wificar.external.ADPCM;
import com.wificar.external.FSCoder;
import com.wificar.util.ByteUtility;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;

public class AudioComponent {
	private static int[] stepTable = { 7, 8, 9, 10, 11, 12, 13, 14, 16, 17, 19,
			21, 23, 25, 28, 31, 34, 37, 41, 45, 50, 55, 60, 66, 73, 80, 88, 97,
			107, 118, 130, 143, 157, 173, 190, 209, 230, 253, 279, 307, 337,
			371, 408, 449, 494, 544, 598, 658, 724, 796, 876, 963, 1060, 1166,
			1282, 1411, 1552, 1707, 1878, 2066, 2272, 2499, 2749, 3024, 3327,
			3660, 4026, 4428, 4871, 5358, 5894, 6484, 7132, 7845, 8630, 9493,
			10442, 11487, 12635, 13899, 15289, 16818, 18500, 20350, 22385,
			24623, 27086, 29794, 32767 };

	private static int[] indexAdjust = { -1, -1, -1, -1, 2, 4, 6, 8 };
	private int sampleRateInHz = 8000;

	// private static final String LOG_TAG = "AudioRecordTest";
	// private static String mFileName = null;
	private MediaRecorder mRecorder = null;
	private MediaPlayer mPlayer = null;

	private AudioRecord audioRecord;
	private AudioTrack track;
	private int bufferSize = 0;

	private WifiCar wificar = null;

	public AudioComponent(WifiCar wificar) {
		this.wificar = wificar;
		this.initialPlayer(sampleRateInHz);
		// initialRecorder();

	}

	/*
	 * private void onRecord(boolean start) { if (start) { startRecording(); }
	 * else { stopRecording(); } }
	 * 
	 * private void onPlay(boolean start) { if (start) { startPlaying(); } else
	 * { stopPlaying(); } }
	 * 
	 * private void startPlaying() { mPlayer = new MediaPlayer(); try {
	 * mPlayer.setDataSource(mFileName);
	 * 
	 * mPlayer.prepare(); mPlayer.start(); } catch (IOException e) {
	 * Log.e(LOG_TAG, "prepare() failed"); } }
	 * 
	 * private void stopPlaying() { mPlayer.release(); mPlayer = null; }
	 * 
	 * private void startRecording() { mRecorder = new MediaRecorder();
	 * mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
	 * mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
	 * mRecorder.setOutputFile(mFileName);
	 * mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
	 * 
	 * try { mRecorder.prepare(); } catch (IOException e) { Log.e(LOG_TAG,
	 * "prepare() failed"); }
	 * 
	 * mRecorder.start(); }
	 * 
	 * private void stopRecording() {
	 * 
	 * mRecorder.stop(); mRecorder.release(); mRecorder = null; }
	 */
	private boolean isRecording = false;
	private Thread recordThread = null;
	private Thread sendThread = null;
	private ArrayList audioDataList = new ArrayList();
	private final Object mutex = new Object();

	public void startRecord() {
		if(isRecording) return;
		synchronized (mutex) {
			isRecording = true;
			//Log.d("mic", "start recorder");
			final int customBufferSize = 640;

			recordThread = new Thread(new Runnable() {
				public void run() {

					//Log.d("mic", "initial recorder");
					initialRecorder();

					// initialPlayer(8000);

					//Log.d("mic", "state:(" + audioRecord.getState() + ")");
					if (audioRecord.getState() == audioRecord.STATE_UNINITIALIZED) {

						return;
					}

					audioRecord.startRecording();

					int serial = 0;
					int index = 0;
					int ticktime = 0;
					int timestamp = 0;
					int sample = 0;

					while (isRecording) {
						byte[] buffer = new byte[customBufferSize];
						int readState = audioRecord.read(buffer, 0,
								customBufferSize);

						// byte[] audioData = buffer.clone();
						if (readState == audioRecord.ERROR) {
							//Log.d("mic", "state:(ERROR)");
						} else if (readState == audioRecord.ERROR_BAD_VALUE) {
							//Log.d("mic", "state:(ERROR_BAD_VALUE)");
						} else if (readState == audioRecord.ERROR_INVALID_OPERATION) {
							//Log.d("mic", "state:(ERROR_INVALID_OPERATION)");
						} else {
							//Log.d("mic", "state:(S)");
						}
						// mPlayer.
						// audioRecord.get
						// if(audioDataList.size()>=2){
						// byte[] adpcmData = ADPCM.compress(audioData,
						// audioRecord.getChannelCount(),
						// audioRecord., 160);
						//Log.d("mic", "state:(" + readState + ")");
						// Log.d("mic","("+isRecording+")recorder length:"+customBufferSize+",index:"+index+",sample:"+sample);
						// Log.d("mic",ByteUtility.bytesToHex(audioData));
						TalkData data = encodeAdpcm(buffer, buffer.length,
								sample, index);
						data.setSerial(serial);
						data.setTicktime(ticktime);
						data.setTimestamp(timestamp);

						sample = data.getParaSample();
						index = data.getParaIndex();

						// CommandEncoder.Protocol cmd =
						// CommandEncoder.createTalkData(data);

						// trackPlayer.write(buffer, 0, buffer.length);
						// trackPlayer.flush();

						try {
							wificar.sendTalkData(data, 0);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						//Log.d("mic", "data:" + data.getData().length);
						// wificar.sendAudio(audioData);
						// Log.d("wild0","send audio record:"+System.currentTimeMillis());
						// }
						// audioDataList.add(audioData);
						ticktime = ticktime + 40;
						serial++;
						timestamp = (int) (System.currentTimeMillis() / 1000);

						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}

					//Log.d("mic", "state:(" + audioRecord.getState() + ")stop");
					//if (audioRecord != null) {
						
						audioRecord.stop();
						audioRecord.release();
						audioRecord = null;
						
					//}
			
					// record(out);
				}
			});
			recordThread.setName("Recording Thread");
			//android.os.Process
			//		.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
			recordThread.start();
		}
	}

	/*
	 * public void record1() { isRecording = true; final int customBufferSize =
	 * 640; final int maxCustomBufferSize = 4096; initialRecorder();
	 * Log.d("recordplay","record"); //ByteArrayOutputStream baos = new
	 * ByteArrayOutputStream(); //baos.
	 * 
	 * 
	 * 
	 * recordThread = new Thread(new Runnable() { public void run() { ByteBuffer
	 * bb = ByteBuffer.allocate(maxCustomBufferSize*5);
	 * audioRecord.startRecording();
	 * 
	 * int serial=0; int index= 0; int ticktime= 0; int timestamp = 0; int
	 * sample =0;
	 * 
	 * int remaind = 0;
	 * 
	 * //int position = 0; //audioRecord.
	 * 
	 * 
	 * while(isRecording){ byte[] buffer = new byte[maxCustomBufferSize]; int
	 * readState = 0;
	 * 
	 * readState = audioRecord.read(buffer,0, maxCustomBufferSize);
	 * if(readState==audioRecord.ERROR){
	 * 
	 * } else if(readState==audioRecord.ERROR_BAD_VALUE){
	 * 
	 * } else if(readState==audioRecord.ERROR_INVALID_OPERATION){
	 * 
	 * } else{
	 * 
	 * }
	 * 
	 * 
	 * //audioRecord.read(audioBuffer, sizeInBytes)
	 * 
	 * 
	 * bb.put(buffer); bb.position(readState+remaind ); bb.flip();
	 * 
	 * Log.d("recordplay","offset:"+bb.position()+"limit:"+bb.limit()+"state:"+
	 * readState); byte[] partBuffer; while(bb.position()<bb.limit()){
	 * if((bb.limit()-bb.position())>customBufferSize){ partBuffer = new
	 * byte[customBufferSize]; ByteBuffer pb = bb.get(partBuffer);
	 * 
	 * //position = position+customBufferSize;
	 * 
	 * //Log.d("recordplay","position:"+bb.position()+",limit:"+bb.limit());
	 * Log.
	 * d("recordplay","("+isRecording+")remainder:"+bb.remaining()+",position:"
	 * +bb
	 * .position()+"recorder length:"+customBufferSize+",index:"+index+",sample:"
	 * +sample);
	 * 
	 * TalkData data = encodeAdpcm(partBuffer, partBuffer.length, sample,
	 * index); data.setSerial(serial); data.setTicktime(ticktime);
	 * data.setTimestamp(timestamp);
	 * 
	 * 
	 * try{ //Thread.sleep(40); wificar.sendTalkData(data); } catch(Exception
	 * e){ e.printStackTrace(); }
	 * 
	 * sample = data.getParaSample(); index = data.getParaIndex();
	 * 
	 * ticktime = ticktime+40; serial++; timestamp = (int)
	 * (System.currentTimeMillis()/1000);
	 * 
	 * } else if((bb.limit()-bb.position())==customBufferSize){ partBuffer = new
	 * byte[customBufferSize]; ByteBuffer pb = bb.get(partBuffer);
	 * 
	 * //position = position+customBufferSize;
	 * 
	 * //Log.d("recordplay","position:"+bb.position()+",limit:"+bb.limit());
	 * Log.
	 * d("recordplay","(Finish)remainder:"+bb.remaining()+",position:"+bb.position
	 * (
	 * )+"recorder length:"+customBufferSize+",index:"+index+",sample:"+sample);
	 * 
	 * TalkData data = encodeAdpcm(partBuffer, partBuffer.length, sample,
	 * index); data.setSerial(serial); data.setTicktime(ticktime);
	 * data.setTimestamp(timestamp);
	 * 
	 * 
	 * try{ //Thread.sleep(40); wificar.sendTalkData(data); } catch(Exception
	 * e){ e.printStackTrace(); }
	 * 
	 * sample = data.getParaSample(); index = data.getParaIndex();
	 * 
	 * ticktime = ticktime+40; serial++; timestamp = (int)
	 * (System.currentTimeMillis()/1000);
	 * 
	 * remaind = 0; bb.clear();
	 * 
	 * Log.d("recordplay","(Finish)remainder:"+bb.remaining()+",position:"+bb.
	 * position
	 * ()+"recorder length:"+customBufferSize+",index:"+index+",sample:"+
	 * sample); break; } else{
	 * //Log.d("recordplay","remaind position:"+position+",limit:"+bb.limit());
	 * remaind = (bb.limit()-bb.position()); byte[] temp = new byte[remaind];
	 * 
	 * bb.get(temp); bb.clear(); //position = 0;
	 * Log.d("recordplay","remaind position:"
	 * +bb.position()+",limit:"+bb.limit()); bb.put(temp);
	 * Log.d("recordplay","remaind position:"
	 * +bb.position()+",limit:"+bb.limit());
	 * //Log.d("recordplay","remaind[temp:"
	 * +remaind+"] position 2:"+bb.position()+",limit:"+bb.limit()); break; }
	 * System.gc(); } //sample = 0; //index = 0; //if(serial>5){ //break; //}
	 * 
	 * }
	 * 
	 * audioRecord.stop(); audioRecord.release();
	 * 
	 * //record(out); } }); recordThread.start(); }
	 */

	public int initialRecorder() {
		// ByteArrayOutputStream out = new ByteArrayOutputStream();
		// out.writeTo(stream);
		// out¤ú¤‘¬°ÀÉ®×
		/*
		 * File pcmFile = new File(myDataPath.getAbsolutePath() +
		 * "/record.pcm"); // Delete any previous recording. if
		 * (pcmFile.exists()) pcmFile.delete(); // Create the new file. try {
		 * pcmFile.createNewFile(); } catch (IOException e) { throw new
		 * IllegalStateException("Failed to create " + pcmFile.toString()); }
		 */
		// Start record pcm data
		//Log.d("wild0", "audio recorder initial");
		try {
			// Create a DataOuputStream to write the audio data into the saved
			// file.
			// OutputStream os = new FileOutputStream(pcmFile);
			// BufferedOutputStream bos = new BufferedOutputStream(os);
			// DataOutputStream dos = new DataOutputStream(bos);
			// Create a new AudioRecord object to record the audio.
			bufferSize = AudioRecord
					.getMinBufferSize(sampleRateInHz,
							AudioFormat.CHANNEL_IN_MONO,
							AudioFormat.ENCODING_PCM_16BIT);
			//Log.d("recordplay", "record buffer size:" + bufferSize);

			audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
					sampleRateInHz, AudioFormat.CHANNEL_IN_MONO,
					AudioFormat.ENCODING_PCM_16BIT, bufferSize);
			// byte[] buffer = new byte[bufferSize];

			/*
			 * audioRecord.startRecording(); while (isRecording) { int
			 * bufferReadResult = audioRecord.read(buffer, 0, bufferSize); for
			 * (int i = 0; i < bufferReadResult; i++) out.write(buffer[i]); } //
			 * filler, data size need can %8 = 0
			 * 
			 * if (out.size() % 8 != 0) { int filler = 0; filler = 8 -
			 * (out.size() % 8); for (int i = 0; i < filler; i++) {
			 * out.write(0); } }
			 * 
			 * // stop record and close the file. audioRecord.stop();
			 * out.close();
			 */

		} catch (Exception e) {
			e.printStackTrace();
		}
		return audioRecord.getState();
		// stop thread, this method may be not great, we can use the
		// "thread.join( )" method
		// recordThread.stop();
	}

	/*
	 * class RecordButton extends Button { boolean mStartRecording = true;
	 * 
	 * OnClickListener clicker = new OnClickListener() { public void
	 * onClick(View v) { onRecord(mStartRecording); if (mStartRecording) {
	 * setText("Stop recording"); } else { setText("Start recording"); }
	 * mStartRecording = !mStartRecording; } };
	 * 
	 * public RecordButton(Context ctx) { super(ctx);
	 * setText("Start recording"); setOnClickListener(clicker); } }
	 * 
	 * class PlayButton extends Button { boolean mStartPlaying = true;
	 * 
	 * OnClickListener clicker = new OnClickListener() { public void
	 * onClick(View v) { onPlay(mStartPlaying); if (mStartPlaying) {
	 * setText("Stop playing"); } else { setText("Start playing"); }
	 * mStartPlaying = !mStartPlaying; } };
	 * 
	 * public PlayButton(Context ctx) { super(ctx); setText("Start playing");
	 * setOnClickListener(clicker); } }
	 */
	// public AudioRecordTest() {
	// mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
	// mFileName += "/audiorecordtest.3gp";
	// }

	/*
	 * @Override public void onPause() { super.onPause(); if (mRecorder != null)
	 * { mRecorder.release(); mRecorder = null; }
	 * 
	 * if (mPlayer != null) { mPlayer.release(); mPlayer = null; } }
	 */
	int audioTrackBufferSize = 0;
	AudioTrack trackPlayer = null;
	Thread playThread = null;
	private boolean isPlaying = false;

	public void initialPlayer(int sampleRate) {
		//Log.d("audio", "initial audio:" + sampleRate);
		/*
		 * audioTrackBufferSize = AudioTrack.getMinBufferSize(sampleRateInHz,
		 * AudioFormat.CHANNEL_CONFIGURATION_MONO,
		 * AudioFormat.ENCODING_PCM_16BIT); trackPlayer = new
		 * AudioTrack(AudioManager.STREAM_MUSIC, sampleRateInHz,
		 * AudioFormat.CHANNEL_CONFIGURATION_MONO,
		 * AudioFormat.ENCODING_PCM_16BIT, audioTrackBufferSize,
		 * AudioTrack.MODE_STREAM);//
		 */

		audioTrackBufferSize = android.media.AudioTrack.getMinBufferSize(
				sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT);
		trackPlayer = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, audioTrackBufferSize,
				AudioTrack.MODE_STREAM);

	}

	public void play() {
		this.initialPlayer(sampleRateInHz);
		trackPlayer.play();
		isPlaying = true;

		// playThread = new Thread() {
		// public void run() {
		// byte[] buf = new byte[audioTrackBufferSize];
		// trackPlayer.play();
		// isPlaying = true;
		// while (isPlaying) {
		// trackPlayer.write(aData.getPCMData(), 0, aData.getPCMData().length);
		// trackPlayer.flush();
		// Log.d("audio", "playing");
		/*
		 * try { stream.read(buf); byte[] packet = buf.clone(); // bytes_pkg =
		 * m_out_bytes.clone() ; trackPlayer.write(packet, 0, packet.length); }
		 * catch (Exception e) { e.printStackTrace(); }
		 */

		// try {
		// Thread.sleep(50);
		// } catch (InterruptedException e) {
		// TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// trackPlayer.stop();
		// trackPlayer.release();

		// }
		// };
		// playThread.start();
		//Log.d("audio", "play audio:" + audioTrackBufferSize);

	}

	public void stopPlayer() {
		isPlaying = false;
		if (trackPlayer != null) {
			trackPlayer.stop();
			trackPlayer.release();
			trackPlayer = null;
		}
		
		//Log.d("audio", "stop audio");
	}
	

	public void stopRecord() {
		isRecording = false;
	//	Log.d("audio", "stop audio");
	}

	public void writeAudioData(AudioData aData) {
		// Log.d("audio","write audio data:"+packet.length);
		if (isPlaying) {
			trackPlayer.write(aData.getPCMData(), 0, aData.getPCMData().length);
			trackPlayer.flush();
		}

		// Log.d("audio",
		// "playing track:"+ByteUtility.convertByteArrayToString(packet));
	}

	// public void closePlayer() {
	// isPlaying = false;

	// }
	public static byte[] encodePCMToADPCM(byte[] raw, int len, int sample,
			int index) {
		short[] pcm = ByteUtility.bytesToShorts(raw);
		byte[] adpcm = new byte[(len / 4)];
		int cur_sample;
		int i;
		int delta;
		int sb;
		int code;
		// Log.d("wild3", "len:"+len+",pcmlen:"+pcm.length);
		len >>= 1;

		// int pre_sample = refsample.getValue();
		// int index = refindex.getValue();

		for (i = 0; i < len; i++) {
			cur_sample = pcm[i]; //
			// Log.d("wild3", "cur_sample:"+cur_sample);
			delta = cur_sample - sample; //
			if (delta < 0) {
				delta = -delta;
				sb = 8; //
			} else {
				sb = 0;
			} //
			code = 4 * delta / stepTable[index]; //
			if (code > 7)
				code = 7; //

			delta = (stepTable[index] * code) / 4 + stepTable[index] / 8; //
			if (sb > 0)
				delta = -delta;
			sample += delta; //
			if (sample > 32767)
				sample = 32767;
			else if (sample < -32768)
				sample = -32768;

			index += indexAdjust[code]; //
			if (index < 0)
				index = 0; //
			else if (index > 88)
				index = 88;

			if ((i & 0x01) == 0x01)
				adpcm[(i >> 1)] |= code | sb;
			else
				adpcm[(i >> 1)] = (byte) ((code | sb) << 4); //
		}
		return adpcm;
	}

	public static TalkData encodeAdpcm(byte[] raw, int len, int sample,
			int index) {

		// short[] pcm = LibcMisc.get_short_array(raw,0);
		short[] pcm = ByteUtility.bytesToShorts(raw);
		byte[] adpcm = new byte[(len / 4)];
		int cur_sample;
		int i;
		int delta;
		int sb;
		int code;
		// Log.d("wild3", "len:"+len+",pcmlen:"+pcm.length);
		len >>= 1;

		// int pre_sample = refsample.getValue();
		// int index = refindex.getValue();

		for (i = 0; i < len; i++) {
			cur_sample = pcm[i]; //
			// Log.d("wild3", "cur_sample:"+cur_sample);
			delta = cur_sample - sample; //
			if (delta < 0) {
				delta = -delta;
				sb = 8; //
			} else {
				sb = 0;
			} //
			code = 4 * delta / stepTable[index]; //
			if (code > 7)
				code = 7; //

			delta = (stepTable[index] * code) / 4 + stepTable[index] / 8; //
			if (sb > 0)
				delta = -delta;
			sample += delta; //
			if (sample > 32767)
				sample = 32767;
			else if (sample < -32768)
				sample = -32768;

			index += indexAdjust[code]; //
			if (index < 0)
				index = 0; //
			else if (index > 88)
				index = 88;

			if ((i & 0x01) == 0x01)
				adpcm[(i >> 1)] |= code | sb;
			else
				adpcm[(i >> 1)] = (byte) ((code | sb) << 4); //
		}
		// Log.d("wild2", "adpcm data size:"+adpcm.length);
		// TalkData data = new TalkData(WifiCar.data, sample, index);
		TalkData data = new TalkData(adpcm, sample, index);
		return data;
		// refsample.setValue(pre_sample);
		// refindex.setValue(index);
	}

	public static byte[] decodeADPCMToPCM(byte[] raw, int len, int sample,
			int index) {
		ByteBuffer bDecoded = ByteBuffer.allocate(len * 4);

		int i;
		int code;
		int sb;
		int delta;
		// short[] pcm = new short[len * 2];
		len <<= 1;

		for (i = 0; i < len; i++) {
			if ((i & 0x01) != 0)
				code = raw[i >> 1] & 0x0f;
			else
				code = raw[i >> 1] >> 4;
			if ((code & 8) != 0)
				sb = 1;
			else
				sb = 0;
			code &= 7;

			delta = (stepTable[index] * code) / 4 + stepTable[index] / 8;
			if (sb != 0)
				delta = -delta;
			sample += delta;
			if (sample > 32767)
				sample = 32767;
			else if (sample < -32768)
				sample = -32768;
			// pcm[i] = (short)pre_sample;
			bDecoded.put(CommandEncoder.int16ToByteArray(sample));
			index += indexAdjust[code];
			if (index < 0)
				index = 0;
			if (index > 88)
				index = 88;
		}

		return bDecoded.array();
	}
	/*
	 * public static byte[] decodeAdpcm(byte[] raw, int len, int sample, int
	 * index) { ByteBuffer bDecoded = ByteBuffer.allocate(len * 4);
	 * 
	 * int i; int code; int sb; int delta; // short[] pcm = new short[len * 2];
	 * len <<= 1;
	 * 
	 * for (i = 0; i < len; i++) { if ((i & 0x01) != 0) code = raw[i >> 1] &
	 * 0x0f; else code = raw[i >> 1] >> 4; if ((code & 8) != 0) sb = 1; else sb
	 * = 0; code &= 7;
	 * 
	 * delta = (stepTable[index] * code) / 4 + stepTable[index] / 8; if (sb !=
	 * 0) delta = -delta; sample += delta; if (sample > 32767) sample = 32767;
	 * else if (sample < -32768) sample = -32768; // pcm[i] = (short)pre_sample;
	 * bDecoded.put(CommandEncoder.int16ToByteArray(sample)); index +=
	 * indexAdjust[code]; if (index < 0) index = 0; if (index > 88) index = 88;
	 * }
	 * 
	 * return bDecoded.array(); }
	 */
}