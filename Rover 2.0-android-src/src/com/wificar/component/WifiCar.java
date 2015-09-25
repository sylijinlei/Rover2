package com.wificar.component;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.KeyStore.Entry;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import javax.net.SocketFactory;
import org.apache.http.util.ByteArrayBuffer;
import com.wificar.WificarActivity;
import com.wificar.external.ADPCM;
import com.wificar.surface.CameraSurfaceView;
import com.wificar.util.ByteUtility;
import com.wificar.util.ImageUtility;
import com.wificar.util.MessageUtility;
import com.wificar.util.NetworkUtility;
import com.wificar.util.TimeUtility;
import com.wificar.util.WificarUtility;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

public class WifiCar {
	private int [] PowerCount = new int[]{0,0,0,0,0,0,0,0,0,0};
	private int PowerTimes = 0;
	private int PTimes =0;
	private boolean First = true;
	private boolean First3 = false;
	private boolean First4 = false;
	private boolean First5 = false;
	private boolean First6 = false;
	private boolean First7 = false;
	private long startRecordTime;
	private long endRecordTime;
	private int number1Count = 0;
	private Timer showP = new Timer(true);
	
	private float battery = 0.0f;
	private float batteryStop = 0.0f;
	private int battery_value = 0;
	private int battery_valueStop = 0;
	private int batteryCount = 0;
	private int batteryCountStop = 0;
	private String photofilename;
	public static int videoWidth = 320;
	public static int videoHeight = 240;
	private boolean bSocketState = false;
	//public static byte[] data = null;
	public void takePicture(Activity activity) throws ParseException{
		
		BitmapFactory.Options opt = new BitmapFactory.Options();
		
		byte[] bArrayImage = vData.getData();
		Bitmap currentBitmap = BitmapFactory.decodeByteArray(bArrayImage, 0,
				bArrayImage.length, opt);
		
		//////////////////////05.31/////////////////////////////////
		currentBitmap = WificarActivity.getInstance().createBitMap(currentBitmap);//加水印
		photofilename = TimeUtility.getCurrentTimeStr();
		String photopath =WificarActivity.getInstance().ReadSDPath() + "/CAR 2.0/Pictures";
		File file = new File(photopath);
		if (!file.exists()) {
		file.mkdirs();  
		}
		File f = new File(WificarActivity.getInstance().ReadSDPath() + "/CAR 2.0/Pictures/" +  photofilename + ".jpg");
		try {
		f.createNewFile();
		} catch (IOException e) {
		// TODO Auto-generated catch block
		//DebugMessage.put("在保存图片时出错："+e.toString());
		}
		FileOutputStream fOut = null;
		try {
		fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
		e.printStackTrace();
		}
		currentBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
		try {
		fOut.flush();
		
		} catch (IOException e) {
		e.printStackTrace();
		}
		try {
		fOut.close();
		String szUrl =WificarActivity.getInstance().ReadSDPath() + "/CAR 2.0/Pictures/" + photofilename + ".jpg" ;
		//Log.i("zhahg", "szUrl :" + szUrl);
		try {
			Uri uri = Uri.parse(szUrl);
			String img_path = WificarActivity.getInstance().ReadSDPath() + "/CAR 2.0/Pictures/" +  photofilename + ".jpg";
			uri = Uri.parse("file://" + img_path);
			//Log.i("zhang", "uri :" + uri);
			//Log.i("zhang", "img_path :" + img_path);
			// Scan the file to update media store index
			activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
			if(WificarActivity.getInstance().isplay_pictrue !=1){
			Toast pictureOK = Toast.makeText(activity,
					MessageUtility.MESSAGE_TAKE_PHOTO_SUCCESSFULLY,
					Toast.LENGTH_SHORT);
				pictureOK.show();
			}
			
		} catch (Exception e) {
			
			Toast pictureFAIL = Toast.makeText(activity,
					MessageUtility.MESSAGE_TAKE_PHOTO_FAIL, Toast.LENGTH_SHORT);
			pictureFAIL.show();

			e.printStackTrace();
		}
		} catch (IOException e) {
			e.printStackTrace();
			}
		
		if(carStateMode==1){
			//recording
			Log.d("recordaction","stopAudio:"+System.currentTimeMillis());
			//this.addRecordAction(System.currentTimeMillis(), RecordAction.ACTION_TAKE_PICTURE);
		}

	}
	
	Timer batteryTimer = new Timer("battery timer");
	public void startBatteryTask() {
		
		//t.schedule(new DrawingTask(), 0, timeOut);
		Log.d("wild1","startBatteryTask");
		batteryTimer.schedule(batteryTask, 1000, 5000);
		//handler.postDelayed(BatteryTask, 10000);
	}
	private TimerTask batteryTask = new TimerTask() {
		@Override
		public void run() {
			
			byte[] data;
			try {
				data = CommandEncoder.cmdFetchBatteryPowerReq();
				dataOutputStream.write(data);
				dataOutputStream.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	};
	Timer keepAliveTimer = new Timer("keep alive");
	public void startKeepAliveTask() {

		//t.schedule(new DrawingTask(), 0, timeOut);
		Log.d("keep","startKeepAliveTask");
		keepAliveTimer.schedule(KeepAliveTask, 1000, 30000);
		//handler.postDelayed(KeepAliveTask, 30000);
	}
	private TimerTask KeepAliveTask = new TimerTask() {
		@Override
		public void run() {
			byte[] data;
			try {
				Log.d("keep","startKeepAliveTask:"+System.currentTimeMillis());
				data = CommandEncoder.cmdKeepAlive();
				dataOutputStream.write(data);
				dataOutputStream.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				
				e.printStackTrace();
			}
			
		}
	};
	
	public static final int LEFT_WHEEL = 0;
	public static final int RIGHT_WHEEL = 1;
	
	
	private WifiCar instance = null;
	private AudioComponent audio = null;
	private VideoComponent flim = null;

	//private int connected = 0;
	private boolean changeFlag = true;
	private int lastAudioDataTime = 0;
	private int audioIndex = 0;
	private byte[] initialAudioData;
	private int initialParaSample;
	private int initialParaIndex;
	private int moveFlag = 0;
	private int moveFlagCount = 0;
	private int moveFlagMaxCount = 50;
	private int audioFlag = 1;
	private boolean move = false;
	
	private VideoData vData = null;
	private AudioData aData = null;
	
	public int getMoveFlagCount(){
		return moveFlagCount;
	}
	public void decreaseMoveFlagCount(){
		Log.d("move", "decreaseMoveFlagCount:"+moveFlagCount);
		moveFlagCount--;
	}
	public void enableMoveFlag() {
		move = true;
		moveFlagCount=moveFlagMaxCount;
		moveFlag = 1;
		disableAudioFlag();
		
	}

	public void disableAudioFlag() {
		audioFlag = 0;
	}
	public void enableAudioFlag() {
		audioFlag = 1;
		
	}
	public int getAudioFlag(){
		return audioFlag;
	}

	public void disableMoveFlag() {
		//Log.i("zhang", "从移动到静止时的 重新计数");
		move = false;
		batteryStop = 0;
		battery_valueStop =0;
		batteryCountStop = 0;
		
		moveFlag = 0;
	}
	public boolean isRecording(){
		
		return (carStateMode==1?true:false);
	}
	public boolean isPlaying(){
		
		return (carStateMode==2?true:false);
	}
	public boolean isChange() {
		return changeFlag;
	}

	public void change() {
		changeFlag = true;
	}

	public void updatedChange() {
		changeFlag = false;
	}

	public boolean setConnect() {
		try {
			connectCommand();
			bConnected = true;
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	public void setDisconnect() {
		if (bConnected) {
			disconnect();
			bConnected = false;
		}

	}
	public void checkConnection(){
		if (bConnected) {
			try {
				this.connectCommand();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public boolean disconnect(){
		try {
			Log.d("wild0","disconnect");
			bSocketState = false;
			socket.close();
			receiverMediaSocket.close();
			//senderMediaSocket.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
	}

	public int isConnected() {
		try{
			if(socket==null){
				//Log.i("isConnected", "socket = null");
				return 0;
			}
		if (socket.isConnected()) {
			//Τ硈絬
			//Log.i("isConnected", "socket.isConnected()");
			return 1;
		} else {
			//Log.i("isConnected", "else socket.isConnected()");
			return 0;
		}
		}
		catch(Exception e){
			e.printStackTrace();
			return 0;
		}
	}
	// variable
	private long lastMoveCurrentTime = System.currentTimeMillis();
	private long soundDelayTime = 1500;
	
	// Constant
	public static final int MAX_SPEED = 10;
	private static final int iHeaderLen = 23;
	private static final int SAMPLE_RATE = 8000;

	public final static int MESSAGE_DISCONNECTED = 8904;
	private static final String TAG = "WifiCar";

	private static int[] step_table = { 7, 8, 9, 10, 11, 12, 13, 14, 16, 17,
			19, 21, 23, 25, 28, 31, 34, 37, 41, 45, 50, 55, 60, 66, 73, 80, 88,
			97, 107, 118, 130, 143, 157, 173, 190, 209, 230, 253, 279, 307,
			337, 371, 408, 449, 494, 544, 598, 658, 724, 796, 876, 963, 1060,
			1166, 1282, 1411, 1552, 1707, 1878, 2066, 2272, 2499, 2749, 3024,
			3327, 3660, 4026, 4428, 4871, 5358, 5894, 6484, 7132, 7845, 8630,
			9493, 10442, 11487, 12635, 13899, 15289, 16818, 18500, 20350,
			22385, 24623, 27086, 29794, 32767 };

	private static int[] index_adjust = { -1, -1, -1, -1, 2, 4, 6, 8 };
	/* ===================== Global variables ===================== */
	String targetHost = "192.168.1.100"; // Target IP
	int targetPort = 80; // Target port
	String targetId = "AC13"; // ID
	String targetPassword = "AC13"; // PW

	String targetCameraVer = ""; // FW Version
	String targetDevID = ""; // Device ID
	private boolean bConnected = false; // Connection status
	int iTimeout = 5000; // Timeout
	Handler handlerGUI = null; // handle to GUI
	CameraSurfaceView sfhGUI = null; // surface holder to GUI
	byte[] bBufCamera = null; // Byte array for preserve the last image for
								// camera
	//int iStatus = STATUS_CONNECTION.STATE_DISCONNECTED; // Status
	public boolean bIr = false; // IR status
	private Activity mainUI = null; // UI Activity
	private long lLastCmdTimeStamp = 0; // Time stamp for record the time that
										// last command is sent. If more than
										// 60s, need to send Keep_Alive

	// Continue speed is not used anymore
	/*
	 * // Continue move speed int iSpeedR = 0; int iSpeedL = 0;
	 */

	// Socket for command
	Socket socket = null; // socket
	DataOutputStream dataOutputStream = null; // output data stream
	DataInputStream dataInputStream = null; // input data stream
	
	Socket receiverMediaSocket = null; // socket for AV stream
	Socket senderMediaSocket = null; // socket for AV stream
	DataOutputStream mediaReceiverOutputStream = null; // output AV data stream
	DataInputStream mediaReceiverInputStream = null; // input AV data stream
	DataOutputStream mediaSenderOutputStream = null; // output AV data stream
	DataInputStream mediaSenderInputStream = null; // input AV data stream

	/* ===================== Global variables ===================== */

	int v1 = 0; // self uncoded number
	int v2 = 0;
	int v3 = 0;
	int v4 = 0;
	/*
	 * byte[] v1 = new byte[4]; byte[] v2 = new byte[4]; byte[] v3 = new
	 * byte[4]; byte[] v4 = new byte[4];
	 */
	int nc1 = 0; // new changlleng
	int nc2 = 0;
	int nc3 = 0;
	int nc4 = 0;

	String cameraId = "";
	String deviceId = "";

	public int getChallenge(int i) {
		if (i == 0) {
			return v1;
		} else if (i == 1) {
			return v2;
		} else if (i == 2) {
			return v3;
		} else if (i == 3) {
			return v4;
		}
		return 0;
	}

	public void setChallengeReverse(int i, int value) {
		if (i == 0) {
			this.nc1 = value;
		} else if (i == 1) {
			this.nc2 = value;
		} else if (i == 2) {
			this.nc3 = value;
		} else if (i == 3) {
			this.nc4 = value;
		}

	}

	public void setCameraId(String cameraId) {
		this.cameraId = cameraId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getFilewareVersion() {
		return deviceId;
	}

	public String getDeviceId() {
		return deviceId;
	}
	
	
	public int getMoveFlag() {
		return moveFlag;
	}

	public String getCameraId() {
		return cameraId;
	}

	public String getKey() {
		// return targetPassword+":"+cameraId+":"+targetId;
		return targetId + ":" + cameraId + "-save-connect:" + targetPassword;  //-save-private:  -hello-lock&lock:   -teabottle-470ml:
	}

	public WifiCar(Activity inActivity) {
		this(inActivity, -1, -1, -1, -1);
	}

	public WifiCar(Activity inActivity, int v1, int v2, int v3, int v4) {
		instance = this;
		audio = new AudioComponent(this);
		flim = new VideoComponent(this);
		mainUI = inActivity;

		this.v1 = v1;
		this.v2 = v2;
		this.v3 = v3;
		this.v4 = v4;

		// Get preference
		targetHost = WificarUtility.getStringVariable(inActivity,
				WificarUtility.ACCOUNT_HOST, this.targetHost);
		targetPort = WificarUtility.getIntVariable(inActivity,
				WificarUtility.ACCOUNT_PORT, this.targetPort);
		targetId = WificarUtility.getStringVariable(inActivity,
				WificarUtility.ACCOUNT_ID, this.targetId);
		targetPassword = WificarUtility.getStringVariable(inActivity,
				WificarUtility.ACCOUNT_PASSWORD, this.targetPassword);

		/*
		 * targetHost = savedIP; targetPort = savedPort; targetID = savedID;
		 * targetPW = savedPW;
		 */
		// int mode = Activity.MODE_PRIVATE;
		// SharedPreferences carSharedPreferences =
		// mainUI.getSharedPreferences(WIFICAR_PREFS, mode);

		// Get saved values

		// String savedIP = carSharedPreferences.getString(PREF_IP, targetIP);
		// int savedPort = carSharedPreferences.getInt(PREF_PORT, targetPort);
		// String savedID = carSharedPreferences.getString(PREF_ID, targetID);
		// String savedPW = carSharedPreferences.getString(PREF_PW, targetPW);
		Log.d("wificar", "new WifiCar");
	}

	// Continue speed is not used anymore
	/*
	 * public void setContinueSpeed(int inR, int inL) { iSpeedR = inR; iSpeedL =
	 * inL; }
	 */

	public void setGUIHandler(Handler inHandler) {
		handlerGUI = inHandler;
	}

	// public void setSurfaceHolder(SurfaceHolder inHolder)
	// {
	// sfhGUI = inHolder;
	// }

	public void setSurfaceView(CameraSurfaceView surfaceView) {
		sfhGUI = surfaceView;
	}
	public void setVideoBitmapBytes(VideoData vData){
		sfhGUI.setCameraBytes(vData);
		//this.bBufCamera = bArrayImage;
	}


	public static void setVideoFolder(Context context, String path) {
		File f = new File(path);
		if (!f.exists())
			f.mkdirs();
		WificarUtility.putStringVariable(context, WificarUtility.VIDEO_FOLDER,
				path);
		/*
		 * int mode = Activity.MODE_PRIVATE; SharedPreferences
		 * carSharedPreferences = context.getSharedPreferences(WIFICAR_PREFS,
		 * mode);
		 * 
		 * // Save to preference SharedPreferences.Editor carEditor =
		 * carSharedPreferences.edit(); carEditor.putString(PREF_VIDEO_FOLDER,
		 * path); carEditor.commit();
		 */
	}

	public static String getVideoFolder(Context context) {

		return WificarUtility.getStringVariable(context,
				WificarUtility.VIDEO_FOLDER, Environment
						.getExternalStorageDirectory().getAbsolutePath());

		/*
		 * int mode = Activity.MODE_PRIVATE; SharedPreferences
		 * carSharedPreferences = context.getSharedPreferences(WIFICAR_PREFS,
		 * mode); return carSharedPreferences.getString(PREF_VIDEO_FOLDER,
		 * Environment.getExternalStorageDirectory().getAbsolutePath());
		 */
	}

	public Boolean setHost(String host) {
		targetHost = host;
		WificarUtility.putStringVariable(mainUI, WificarUtility.ACCOUNT_HOST,
				host);
		// Get preference
		/*
		 * int mode = Activity.MODE_PRIVATE; SharedPreferences
		 * carSharedPreferences = mainUI.getSharedPreferences(WIFICAR_PREFS,
		 * mode);
		 * 
		 * // Save to preference SharedPreferences.Editor carEditor =
		 * carSharedPreferences.edit(); carEditor.putString(PREF_IP, targetIP);
		 * carEditor.commit();
		 */
		return true;
	}

	public String getHost() {
		return WificarUtility.getStringVariable(mainUI,
				WificarUtility.ACCOUNT_HOST, targetHost);

	}

	public Boolean setPort(int port) {
		targetPort = port;
		WificarUtility
				.putIntVariable(mainUI, WificarUtility.ACCOUNT_PORT, port);
		// Get preference
		/*
		 * int mode = Activity.MODE_PRIVATE; SharedPreferences
		 * carSharedPreferences = mainUI.getSharedPreferences(WIFICAR_PREFS,
		 * mode);
		 * 
		 * // Save to preference SharedPreferences.Editor carEditor =
		 * carSharedPreferences.edit(); carEditor.putInt(PREF_PORT, targetPort);
		 * carEditor.commit();
		 */
		return true;
	}

	public int getPort() {
		return WificarUtility.getIntVariable(mainUI,
				WificarUtility.ACCOUNT_PORT, targetPort);
	}

	public Boolean setId(String id) {
		targetId = id;
		WificarUtility.putStringVariable(mainUI, WificarUtility.ACCOUNT_ID, id);

		// Get preference
		/*
		 * int mode = Activity.MODE_PRIVATE; SharedPreferences
		 * carSharedPreferences = mainUI.getSharedPreferences(WIFICAR_PREFS,
		 * mode);
		 * 
		 * // Save to preference SharedPreferences.Editor carEditor =
		 * carSharedPreferences.edit(); carEditor.putString(PREF_ID, targetID);
		 * carEditor.commit();
		 */
		return true;
	}

	public String getId() {
		return WificarUtility.getStringVariable(mainUI,
				WificarUtility.ACCOUNT_ID, targetId);
	}

	public Boolean setPassword(String pw) {
		targetPassword = pw;
		WificarUtility.putStringVariable(mainUI,
				WificarUtility.ACCOUNT_PASSWORD, pw);
		// Get preference
		/*
		 * int mode = Activity.MODE_PRIVATE; SharedPreferences
		 * carSharedPreferences = mainUI.getSharedPreferences(WIFICAR_PREFS,
		 * mode);
		 * 
		 * // Save to preference SharedPreferences.Editor carEditor =
		 * carSharedPreferences.edit(); carEditor.putString(PREF_PW, targetPW);
		 * carEditor.commit();
		 */
		return true;
	}

	public String getPassword() {
		return WificarUtility.getStringVariable(mainUI,
				WificarUtility.ACCOUNT_PASSWORD, targetPassword);
		// return targetPW;
	}

	public static String getVersion(Context context) {
		PackageManager packageManager = context.getPackageManager();
		String packageName = context.getPackageName();

		try {
			PackageInfo info = packageManager.getPackageInfo(packageName, 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			return "";
		}
	}

	public String getDevID() {
		return targetDevID;
	}

	public String getSSID() throws Exception {

		String url = "http://" + this.targetHost + ":" + this.targetPort
				+ "/get_params.cgi?user=" + this.targetId + "&pwd="
				+ this.targetPassword;
		//Log.d("wificar", "get ssid:" + url);
		String content = NetworkUtility.getURLContent(url);
		int start = content.indexOf("adhoc_ssid");
		int first = content.indexOf('\'', start);
		int second = content.indexOf('\'', first + 1);
		String ssid = content.substring(first + 1, second);
		//Log.d("wificar", "ssid:" + ssid);
		return ssid;
		// return "KH_"+cameraId;
	}

	public byte[] getBufCamera() {
		return bBufCamera;
	}

	private void updateData() {
		// targetHost = WificarUtility.getStringVariable(instance,
		// WificarUtility.ACCOUNT_HOST, "");
	}
	public synchronized void connectMediaReceiver(int linkId) throws IOException {
		Socket s = createMediaReceiverSocket(targetHost, targetPort);
		
		mediaReceiverOutputStream = new DataOutputStream(s.getOutputStream());
		mediaReceiverInputStream = new DataInputStream(s.getInputStream());
		
		byte[] login = CommandEncoder.cmdMediaLoginReq(linkId);
		mediaReceiverOutputStream.write(login);

		Runnable thread = new Runnable() {
			

			public void run() {
				ByteArrayBuffer bufInput = new ByteArrayBuffer(1024 * 1024);
				bufInput.clear();
				try {
					while (bSocketState) {

						//int iLimit = mediaInputStream.available();
						int iLimit = 4096*2;
						
						 //Log.d("media","limit:"+iLimit);
						if (iLimit > 0) {
							byte[] b = new byte[iLimit];
							int iReadLen = mediaReceiverInputStream.read(b, 0, iLimit);
							//Log.d("limit","limit:"+mediaReceiverInputStream.available());

								bufInput.append(b, 0, iReadLen);
								bufInput = CommandEncoder.parseMediaCommand(instance, bufInput, mediaReceiverInputStream.available());

						}
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						//Log.e("Media Thread", "media Thread");
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//Log.i("zhang", "media Thread is stopping");//wifi断开14s后调用这里
					WificarActivity.getInstance().reStartConnect();
					//WificarActivity.getInstance().sendMessage(WificarActivity.getInstance().MESSAGE_CONNECT_TO_CAR_FAIL);
					e.printStackTrace();
				}
			}
		};
		Thread rec = new Thread(thread);
		rec.setName("Media Thread");
		rec.start();
	}
	
	public synchronized void connectMediaSender(int linkId) throws IOException {
		Socket s = createMediaSenderSocket(targetHost, targetPort);
		
		mediaSenderOutputStream = new DataOutputStream(s.getOutputStream());
		mediaSenderInputStream = new DataInputStream(s.getInputStream());
		
		byte[] login = CommandEncoder.cmdMediaLoginReq(linkId);
		mediaSenderOutputStream.write(login);

		Runnable thread = new Runnable() {
			

			public void run() {
				ByteArrayBuffer bufInput = new ByteArrayBuffer(1024 * 1024);
				bufInput.clear();
				try {
					while (bSocketState) {

						//int iLimit = mediaInputStream.available();
						int iLimit = 4096*2;
						
						 //Log.d("media","limit:"+iLimit);
						if (iLimit > 0) {
							byte[] b = new byte[iLimit];
							int iReadLen = mediaSenderInputStream.read(b, 0, iLimit);
							//Log.d("limit","limit:"+mediaSenderInputStream.available());

								bufInput.append(b, 0, iReadLen);

								bufInput = CommandEncoder.parseMediaCommand(instance, bufInput, mediaSenderInputStream.available());

						}
						try {
							Thread.sleep(5);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				} catch (IOException e) {
					//Log.e("Media Send Thread", "Media Send Thread is stopping");
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		Thread send = new Thread(thread);
		send.setName("Media Send Thread");
		send.start();
	}
	
	
		/////////////06.04//////////////////
		public synchronized void startRecordTrack() throws IOException {
			byte[] cmd = CommandEncoder.cmdDeviceControlReq(6, 1);
			dataOutputStream.write(cmd);
			dataOutputStream.flush();
		}
		public synchronized void stopRecordTrack() throws IOException {
			byte[] cmd = CommandEncoder.cmdDeviceControlReq(6, 0);
			dataOutputStream.write(cmd);
			dataOutputStream.flush();
		}
		public synchronized void startPlayTrack() throws IOException {
			byte[] cmd = CommandEncoder.cmdDeviceControlReq(7, 1);
			dataOutputStream.write(cmd);
			dataOutputStream.flush();
		}
		public synchronized void stopPlayTrack() throws IOException {
			byte[] cmd = CommandEncoder.cmdDeviceControlReq(7, 0);
			dataOutputStream.write(cmd);
			dataOutputStream.flush();
		}
		/////////////06.04//////////////////
	
	public synchronized void verifyCommand() throws IOException {
		byte[] cmd = CommandEncoder.cmdVerifyReq(getKey(), nc1, nc2, nc3, nc4);
		dataOutputStream.write(cmd);
		dataOutputStream.flush();
		startBatteryTask();
		startKeepAliveTask();
	}
	
	private int carStateMode = 0;//0⊿ㄆ,1record,2play
	Timer recordTimer = null;
	Timer playTimer = null;
	long startRecordTimeStamp = 0;
	long recordTimeLength = 0;
	
	public synchronized void connectCommand() throws IOException {
//		WificarActivity.getInstance().sendMessage(WificarActivity.MESSAGE_CONNECT_TO_CAR);
		Log.e(TAG, "connectCommand=------");
		Socket s = createCommandSocket(targetHost, targetPort);
		if(!s.isConnected()){
			throw new IOException();
		}
		Log.e(TAG, "isConnected..........");
		dataOutputStream = new DataOutputStream(s.getOutputStream());
		dataInputStream = new DataInputStream(s.getInputStream());
		
		byte[] login = CommandEncoder.cmdLoginReq(v1,v2,v3,v4);
		dataOutputStream.write(login);
		dataOutputStream.flush();

		Runnable thread = new Runnable() {
			public void run() {
				ByteArrayBuffer bufInput = new ByteArrayBuffer(1024 * 1024);
				// while (bConnected) {
				// Continue speed is not used anymore
				/*
				 * // Send move event if has speed value if(iSpeedR != 0)
				 * moveCommand(GO_DIRECTION.Right, iSpeedR); if(iSpeedL != 0)
				 * moveCommand(GO_DIRECTION.Left, iSpeedL);
				 */

				// Check if it does not send any command for 60s. If yes, send a
				// keep alive command.
				//if ((System.currentTimeMillis() - lLastCmdTimeStamp) > 60000)
				//	sendCommand(CMD_OP_CODE.Keep_Alive);

				// Receive packet

				try {
					while (bSocketState) {

						int iLimit = dataInputStream.available();
						// Log.d("wild0","limit:"+iLimit);
						if (iLimit > 0) {
							byte[] b = new byte[iLimit];
							int iReadLen = dataInputStream.read(b, 0, iLimit);

							bufInput.append(b, 0, iReadLen);

							// Log.d("wild0","limit:"+iLimit);
							bufInput = CommandEncoder.parseCommand(instance, bufInput);
							Log.e(TAG, "bufInput--------------");
						}

					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					if(bConnected){
						try {
							instance.connectCommand();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}
		};
		Thread rec = new Thread(thread);
		rec.setName("Command Thread");
		rec.start();

	}

	private void enableCommandInputStream(DataInputStream in) {
		Runnable thread = new Runnable() {
			public void run() {
				ByteArrayBuffer bufInput = new ByteArrayBuffer(1024 * 1024);
				// while (bConnected) {
				// Continue speed is not used anymore
				/*
				 * // Send move event if has speed value if(iSpeedR != 0)
				 * moveCommand(GO_DIRECTION.Right, iSpeedR); if(iSpeedL != 0)
				 * moveCommand(GO_DIRECTION.Left, iSpeedL);
				 */

				// Check if it does not send any command for 60s. If yes, send a
				// keep alive command.
				if ((System.currentTimeMillis() - lLastCmdTimeStamp) > 60000){
					byte[] keepAlive;
					try {
						keepAlive = CommandEncoder.cmdKeepAlive();
						dataOutputStream.write(keepAlive);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					//sendCommand(CMD_OP_CODE.Keep_Alive);
				}
				// Receive packet

				try {
					while (true) {

						int iLimit = dataInputStream.available();
						// Log.d("wild0","limit:"+iLimit);
						if (iLimit > 0) {
							byte[] b = new byte[iLimit];
							int iReadLen = dataInputStream.read(b, 0, iLimit);

							bufInput.append(b, 0, iReadLen);

							// Log.d("wild0","limit:"+iLimit);
							CommandEncoder.parseCommand(instance, bufInput);

						}

					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

	}
	
	private Socket createCommandSocket(String targetHost, int targetPort) throws IOException{
	
			socket = SocketFactory.getDefault().createSocket();
			SocketAddress remoteAddr = new InetSocketAddress(targetHost,
					targetPort);
			socket.connect(remoteAddr, 5000);
			bSocketState = true;
			return socket;
		
	}

	private Socket createMediaReceiverSocket(String targetHost, int targetPort) throws IOException {

		receiverMediaSocket = SocketFactory.getDefault().createSocket();
			SocketAddress remoteAddr = new InetSocketAddress(targetHost,
					targetPort);
			receiverMediaSocket.connect(remoteAddr, 5000);
			bSocketState = true;
			return receiverMediaSocket;
		
	}
	private Socket createMediaSenderSocket(String targetHost, int targetPort) throws IOException {

		senderMediaSocket = SocketFactory.getDefault().createSocket();
		SocketAddress remoteAddr = new InetSocketAddress(targetHost,
				targetPort);
		//Log.i("zhang stop sockect", "socket 取消");
		senderMediaSocket.connect(remoteAddr, 5000);
		bSocketState = true;
		return senderMediaSocket;
}

	public boolean enableVideo() throws IOException {
		WificarActivity.getInstance().sendMessage(WificarActivity.MESSAGE_CONNECT_TO_CAR_SUCCESS);
		
		//if (!bConnected)
		//	return false;
		
		//Log.d("wild0","enableVideo");
		byte[] cmd = CommandEncoder.cmdVideoStartReq();
		dataOutputStream.write(cmd);
		dataOutputStream.flush();
		
		return true;
		
	}

	public boolean disableVideo() throws IOException {
		if (!bConnected)
			return false;
		
		byte[] cmd = CommandEncoder.cmdVideoEnd();
		dataOutputStream.write(cmd);
		return true;
		
	}
	
	public synchronized void led_onTrack() throws IOException {
		byte[] cmd = CommandEncoder.cmdDeviceControlReq(8, 0);
		dataOutputStream.write(cmd);
		dataOutputStream.flush();
	}
	public synchronized void led_offTrack() throws IOException {
		byte[] cmd = CommandEncoder.cmdDeviceControlReq(9, 0);
		dataOutputStream.write(cmd);
		dataOutputStream.flush();
	}
	
	public boolean cameraup() throws IOException{
		if (!bConnected)
			return false;
		this.moveFlag = 1;
		byte[] cmd = CommandEncoder.cmdDecoderControlReq(0);
		dataOutputStream.write(cmd);
		dataOutputStream.flush();
		return true;
	}
	public boolean cameradown() throws IOException{
		if (!bConnected)
			return false;
		this.moveFlag = 1;
		byte[] cmd = CommandEncoder.cmdDecoderControlReq(2);
		dataOutputStream.write(cmd);
		dataOutputStream.flush();
		return true;
	}
	public boolean camerastop() throws IOException{
		if (!bConnected)
			return false;
		this.moveFlag = 0;
		byte[] cmd = CommandEncoder.cmdDecoderControlReq(1);
		dataOutputStream.write(cmd);
		dataOutputStream.flush();
		return true;
	}
	public boolean enableRecordAudio(int time) throws IOException {
		Log.d("audio", "audioEnable");
		audio.startRecord();
		if (!bConnected)
			return false;
		
		//this.playAudio();
		byte[] cmd = CommandEncoder.cmdTalkStartReq(time);
		dataOutputStream.write(cmd);
		dataOutputStream.flush();
		
		//audio.record();
		//audio.record();
		return true;
	}
	public boolean enableAudio() throws IOException {
		Log.d("wild0", "audioEnable");
		if (!bConnected)
			return false;
		
		//this.playAudio();
		byte[] cmd = CommandEncoder.cmdAudioStartReq();
		dataOutputStream.write(cmd);
		dataOutputStream.flush();
		
		
		return true;

	}

	
	public boolean disableRecordAudio() throws IOException {
		this.stopRecordAudio();
		//audio.stopRecord();
		if (!bConnected)
			return false;
		//this.stopRecordAudio();
		byte[] cmd = CommandEncoder.cmdTalkEnd();
		dataOutputStream.write(cmd);
		dataOutputStream.flush();
		
		
		return true;
	}
	public boolean disableAudio() throws IOException {
		if (!bConnected)
			return false;
		
		/*
		byte[] cmd = CommandEncoder.cmdAudioEnd();
		dataOutputStream.write(cmd);
		dataOutputStream.flush();
		*/
		this.stopAudio();
		
		return true;
		
		/*
		Boolean bRet = false;
		Log.d("wificar", "AudoDisable");
		bRet = sendCommand(CMD_OP_CODE.Audio_End);

		if (bRet) {
		}

		this.iAudioLinkID = 0;

		if (this.aAudio != null) {
			this.aAudio.stop();
			this.aAudio.release();
			this.aAudio = null;
		}

		if (tAudio != null) {
			tAudio.cancel();
			tAudio = null;
		}

		if (vAudio != null)
			vAudio.clear();

		return bRet;
		*/
	}
	public boolean enableIR() throws IOException{
		if (!bConnected)
			return false;
		
		byte[] cmd = CommandEncoder.cmdDecoderControlReq(94);
		dataOutputStream.write(cmd);
		dataOutputStream.flush();
		return true;
	}
	public boolean disableIR() throws IOException{
		if (!bConnected)
			return false;
		
		byte[] cmd = CommandEncoder.cmdDecoderControlReq(95);
		dataOutputStream.write(cmd);
		dataOutputStream.flush();
		return true;
	}
		
	public boolean g_move(int cmd , int speeds) throws IOException{
		if (!bConnected)
			return false;
		
		byte[] cmdg = null;
		if(speeds > 0){
			this.moveFlag = 1;
			//enableMoveFlag();
			cmdg = CommandEncoder.cmdDeviceControlReq(11, 255);
		}
		if(speeds < 0){
			this.moveFlag = 1;
			//enableMoveFlag();
			cmdg = CommandEncoder.cmdDeviceControlReq(12, 255);
		}
		
		dataOutputStream.write(cmdg);
		dataOutputStream.flush();
		return true;
	}
	
	public boolean move(int dir, int speed) throws IOException{
		if (!bConnected)
			return false;
		
		Log.d("move",
				 "connected:"+dir);
		byte[] cmd = null;
		if(dir==this.LEFT_WHEEL){
			if(speed>0){
				enableMoveFlag();
				this.moveFlag = 1;
				cmd = CommandEncoder.cmdDeviceControlReq(4, speed);
				Log.e("move","cmdDeviceControlReq(1):"+speed);
			}
			else if(speed==0){
				disableMoveFlag();
				this.moveFlag = 0;
				cmd = CommandEncoder.cmdDeviceControlReq(3, 0);
				Log.e("move","cmdDeviceControlReq(2):"+speed);
			}
			else if(speed<0){
				enableMoveFlag();
				this.moveFlag = 1;
				cmd = CommandEncoder.cmdDeviceControlReq(5, Math.abs(speed));
				Log.e("move","cmdDeviceControlReq(3):"+speed);
			}
		}
		else if(dir==this.RIGHT_WHEEL){
			if(speed>0){
				enableMoveFlag();
				this.moveFlag = 1;
				cmd = CommandEncoder.cmdDeviceControlReq(1, speed);
			}
			else if(speed==0){
				disableMoveFlag();
				this.moveFlag = 0;
				cmd = CommandEncoder.cmdDeviceControlReq(0, speed);
			}
			else if(speed<0){
				enableMoveFlag();
				this.moveFlag = 1;
				cmd = CommandEncoder.cmdDeviceControlReq(2, Math.abs(speed));
			}
		}
		
		dataOutputStream.write(cmd);
		dataOutputStream.flush();
		return true;
	}
	public void sendTalkData(TalkData data, int type) throws IOException {
		if(type==0){
			CommandEncoder.Protocol cmd = CommandEncoder.createTalkData(data);
			Log.d("mic","send talk("+receiverMediaSocket.isConnected()+","+mediaReceiverOutputStream.size()+"):"+data.getTicktime()+","+data.getSerial()+","+data.getTimestamp());
			Log.d("mic","hex:"+ByteUtility.bytesToHex(cmd.output()));
			mediaReceiverOutputStream.write(cmd.output());
			mediaReceiverOutputStream.flush();
			}
		if(type==1){
		CommandEncoder.Protocol cmd = CommandEncoder.createTalkData(data);
		Log.d("mic","send talk("+senderMediaSocket.isConnected()+","+mediaSenderOutputStream.size()+"):"+data.getTicktime()+","+data.getSerial()+","+data.getTimestamp());
		Log.d("mic","hex:"+ByteUtility.bytesToHex(cmd.output()));
		mediaSenderOutputStream.write(cmd.output());
		mediaSenderOutputStream.flush();
		}
		
		//dataOutputStream.write(cmd.output());
		//dataOutputStream.flush();
	}
	
	public static byte[] int32ToByteArray(int value) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			int offset = i * 8;
			b[i] = (byte) ((value >>> offset) & 0xFF);
		}
		return b;
	}

	private static byte[] int16ToByteArray(int value) {
		byte[] b = new byte[2];
		for (int i = 0; i < 2; i++) {
			int offset = i * 8;
			b[i] = (byte) ((value >>> offset) & 0xFF);
		}
		return b;
	}

	private static byte[] int8ToByteArray(int value) {
		byte[] b = new byte[1];
		for (int i = 0; i < 1; i++) {
			int offset = i * 8;
			b[i] = (byte) ((value >>> offset) & 0xFF);
		}
		return b;
	}

	private static int byteArrayToInt(byte[] inByteArray, int iOffset, int iLen) {
		int iResult = 0;

		for (int x = 0; x < iLen; x++) {
			if ((x == 0) && (inByteArray[iOffset + (iLen - 1) - x] < 0))
				iResult = iResult
						| (0xffffffff & inByteArray[iOffset + (iLen - 1) - x]);
			else
				iResult = iResult
						| (0x000000ff & inByteArray[iOffset + (iLen - 1) - x]);
			if (x < (iLen - 1))
				iResult = iResult << 8;
		}

		return iResult;
	}

	private static byte[] adpcm_decode(byte[] raw, int len, int pre_sample,
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

			delta = (step_table[index] * code) / 4 + step_table[index] / 8;
			if (sb != 0)
				delta = -delta;
			pre_sample += delta;
			if (pre_sample > 32767)
				pre_sample = 32767;
			else if (pre_sample < -32768)
				pre_sample = -32768;
			// pcm[i] = (short)pre_sample;
			bDecoded.put(int16ToByteArray(pre_sample));
			index += index_adjust[code];
			if (index < 0)
				index = 0;
			if (index > 88)
				index = 88;
		}

		return bDecoded.array();
	}

	public AudioComponent getAudioComponent() {
		return audio;
	}

	public boolean playAudio() {
		try {
			Log.d("audio", "play audio");
			//audio.initialPlayer(8000);
			audio.play();
			if(carStateMode==1){
				//recording
				Log.d("recordaction","playAudio:"+System.currentTimeMillis());
				//this.addRecordAction(System.currentTimeMillis(), RecordAction.ACTION_AUDIO_ENABLE);
			}
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void stopAudio() {
		audio.stopPlayer();
		if(carStateMode==1){
			//recording
			Log.d("recordaction","stopAudio:"+System.currentTimeMillis());
			//this.addRecordAction(System.currentTimeMillis(), RecordAction.ACTION_AUDIO_DISABLE);
		}
		
	}
	public void stopRecordAudio() {
		audio.stopRecord();
	}

	public void startMic() {

	}

	public void stopMic() {

	}
	
	
	public String startFlim(Context context, int width, int height) throws ParseException, Exception{
		return startFlim(this.getVideoFolder(context), TimeUtility.getCurrentTimeStr()+".avi", width, height);
	}
	public String startFlim(String path, String fileName, int width, int height) throws Exception{
		flim.start(path, fileName, width, height);
		
		if(carStateMode==1){
			//recording
			Log.d("recordaction","stopAudio:"+System.currentTimeMillis());
			//this.addRecordAction(System.currentTimeMillis(), RecordAction.ACTION_VIDEO_RECORD_START);
		}
		return path+fileName;
	}
	public void stopFlim() throws Exception{
		
		flim.stop();
		
		if(carStateMode==1){
			//recording
			Log.d("recordaction","stopAudio:"+System.currentTimeMillis());
			//this.addRecordAction(System.currentTimeMillis(), RecordAction.ACTION_VIDEO_RECORD_END);
		}
	}
	public void appendVideoDataToFlim(VideoData data) throws Exception{
		this.vData = data;
		//long preTime = System.currentTimeMillis();
		
		
		//Log.e("record2",System.currentTimeMillis()+"audioData:"+data.getTimestamp()+":"+getAudioFlag());
		if(flim.state == 1  && this.getAudioFlag()==0){
			//繰戈
			
			//timeInterval氨ゎ丁
			////Log.d("recordvideo", "^^^^^^empty:"+timeInterval+"="+timeInterval*16);
			//AudioData audioData = AudioData.createEmptyData((int)timeInterval*16,0, data.getTimestamp());
			//Log.d("recordvideo", "^^^^^^empty:=640");
			long timeInterval = data.getCustomTimestamp()-flim.getLastVideoFrameCustomTimestamp();
			
			int count = (int) (timeInterval/40)+1;
			//Log.d("recordvideo", "count["+timeInterval+"]:="+count);
			for(int i=0;i<count;i++){
				AudioData audioData = AudioData.createEmptyPCMData(640,0, data.getTimestamp());
				flim.pushAudioData(audioData,1);
			}
		}
		
		//data.setDelay((int)timeInterval);
		
		flim.pushVideoData(data,0);
		
		this.setVideoBitmapBytes(data);
		//long postTime = System.currentTimeMillis();
		////Log.d("screen","redraw time 1:"+(postTime-preTime));
		//sfhGUI.setCameraBytes(data.getData());
	}
	public void appendAudioDataToFlim(AudioData data) throws Exception{
		this.aData = data;
		if(this.getAudioFlag()==1){
			
			Log.d("audio1",System.currentTimeMillis()+"audioData:"+data.getTimestamp()+":"+getMoveFlagCount());
			Log.d("delay","count:"+this.getMoveFlagCount());
			if(this.getMoveFlagCount()>0){
				long audioTimeInterval = data.getTimestamp()-flim.getLastAudioFrameTimestamp();
				if(audioTimeInterval>1000){
					audioTimeInterval = 0;
				}
				
				AudioData audioData = AudioData.createEmptyPCMData(640,0, data.getTimestamp());
				//AudioData audioData = AudioData.createEmptyData((int)audioTimeInterval*8,0, data.getTimestamp());
				
				flim.pushAudioData(audioData,this.getMoveFlagCount());
				getAudioComponent().writeAudioData(audioData);
				this.decreaseMoveFlagCount();
			}else{
			
				flim.pushAudioData(data,2);
				getAudioComponent().writeAudioData(data);
				
			}
		}
	}
}