package com.wificar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;
import com.CAR2.R;
import com.wificar.component.AudioComponent;
import com.wificar.component.CommandEncoder;
import com.wificar.component.WifiCar;
import com.wificar.dialog.Connect_Dialog;
import com.wificar.dialog.Control_share_dialog;
import com.wificar.dialog.DeleteDialog;
import com.wificar.dialog.DisGsensor;
import com.wificar.dialog.Disrecord_play_dialog;
import com.wificar.dialog.Disrecordvideo_dialog;
import com.wificar.dialog.SDcardCheck;
import com.wificar.dialog.VideoSaveDialog;
import com.wificar.dialog.wifi_not_connect;
import com.wificar.mediaplayer.MediaPlayerActivity;
import com.wificar.surface.CamerSettingSurfaceView;
import com.wificar.surface.CameraSurfaceView;
import com.wificar.surface.ControllerInterface;
import com.wificar.surface.DoubleAxisLeftControllerSurfaceView;
import com.wificar.surface.DoubleAxisRightControllerSurfaceView;
import com.wificar.util.AVIGenerator;
import com.wificar.util.BlowFish;
import com.wificar.util.BlowFishEncryptUtil;
import com.wificar.util.ByteUtility;
import com.wificar.util.ImageUtility;
import com.wificar.util.MessageUtility;
import com.wificar.util.TimeUtility;
import com.wificar.util.VideoUtility;
import com.wificar.util.WificarUtility;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
//import android.util.Log;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class WificarActivity extends Activity {
	/** Called when the activity is first created. */
	public double dimension = 0.0;
	private PopupWindow mPopupWindow;
	private Context mContext;
	
	private boolean bThreadRun = false;  //进入设置界面时的处理事情的线程标识
	private boolean bPhotoThreadRun = false;
	private String IP ;
	private String Port;
	private String version;
	private String firmwareVersion;
	private String SSID;
	
	public EditText tIP ;
	public EditText tPort;
	public TextView tdevice;
	public TextView tfirmware;
	public TextView tsoftware;
	private Button Okbutton;
	
	
	// 初始化变�??
		public static int Screen_width;
		public static int Screen_height;
		public static float dscale;// dp -- px
		public float density;
		public double screenSize;
	
	
	private Dialog dlg ;
	private static int videoWidth = 320;
	private static int videoHeight = 240;
	private static AVIGenerator movUtil  = null;
	private boolean sensorEnable = false;
	
	private AudioComponent audio = null;
	
	public static WificarActivity instance = null;
	private SensorManager mSensorManager;
	private Sensor aSensor;
	private static boolean videoRecordEnable = false;
	private Sensor mfSensor;
	private boolean gSensorControlEnable = false;

	static {
		// System.loadLibrary("avutil");
		// System.loadLibrary("avcore");
		// System.loadLibrary("avcodec");
		// System.loadLibrary("avformat");
		// System.loadLibrary("avdevice");
		// System.loadLibrary("swscale");
		// System.loadLibrary("avfilter");

		// System.loadLibrary("ffmpeg");
		// System.loadLibrary("ffmpeg-test-jni");
	}
	// public static native String stringFromJNI();
	// private static native void initial(int width, int height, int frameTime);
	// public native String videoEncodeExample(String filename);
	// private static native void openStream(String filename);
	// private static native void writeBytes(byte[] img);
	// private static native void closeStream();

	private int controllerType = 1;
	public final static int SINGLE_CONTROLLER = 0;
	public final static int DOUBLE_AXIS_CONTROLLER = 1;

	// private ControllerInterface surface = null;

	private DoubleAxisLeftControllerSurfaceView leftSurface = null;
	private DoubleAxisRightControllerSurfaceView rightSurface = null;

	private CameraSurfaceView cameraSurfaceView = null;
	private WifiCar wifiCar = null;
	private Handler handler = null;

	private boolean isLowPower = false;
	private int LowPower;
	private int level;
	private int scale;
	private String startSSID;
	private String DirectPath;
	private ProgressDialog connectionProgressDialog = null;
	private ProgressDialog settingProgressDialog;
	
	public final static int MESSAGE_GET_SETTING_INFO = 8701;
	protected static final int MESSAGE_MAIN_SETTING = 8700;
	protected static final int MESSAGE_TAKE_PHOTO = 8702;
	public final static int MESSAGE_WIFISTATE = 8900;
	public final static int MESSAGE_SOUND = 8999;
	public final static int MESSAGE_SETTING = 9000;
	public final static int MESSAGE_CONNECT_TO_CAR = 8901;
	public final static int MESSAGE_CONNECT_TO_CAR_SUCCESS = 8902;
	public final static int MESSAGE_CONNECT_TO_CAR_FAIL = 8903;
	public final static int MESSAGE_DISCONNECTED = 8904;
	protected static final int MESSAGE_START_APPLICATION = 8905;
	protected static final int MESSAGE_STOP_RECORD = 8910;
	public static final int MESSAGE_STOP_PLAY = 8911;
	protected static final int MESSAGE_START_RECORD = 8912;
	public static final int MESSAGE_START_PLAY = 8913;
	
	public static final int MESSAGE_PLAY_PICTRUE = 8914;
	public static final int MESSAGE_PLAY_VIDEO = 8917;
	public static final int MESSAGE_STOP_VIDEO = 8918;
	
	protected static final int MESSAGE_START_RECORD_AUDIO = 7000;
	protected static final int MESSAGE_STOP_RECORD_AUDIO = 7001;
	
	protected static final int MESSAGAE_BATTERY = 9502;
	public static final int MESSAGE_BATTERY_100 = 9001;
	public static final int MESSAGE_BATTERY_75 = 9002;
	public static final int MESSAGE_BATTERY_50 = 9003;
	public static final int MESSAGE_BATTERY_25 = 9004;
	public static final int MESSAGE_BATTERY_0 = 9005;
	public static final int MESSAGE_BATTERY_UNKNOWN = 9006;
	protected static final int MESSAGE_CHECK_TEST= 8915;  //检测不足时发的消息
	private float[] fAccelerometerValues = null;
	private float[] fMagneticFieldValues = null;

	private int LR = 0;
	private boolean LRshow = false;
	public boolean isGsensor = false;
	public boolean disGsensor = false;
	public boolean isNotExit = false;
	private boolean bIsPortait = true;
	private boolean orientationLock = false;
	private boolean connect_error = false;
	private boolean No_Sdcard = false;
	private boolean stopVideo = false;
	private boolean startVideo = false;
	public boolean isconnectwifi = true;
	private boolean controlEnable = false;
	public boolean succeedConnect = false;
	
	public boolean isPlayModeEnable = false;
	// private int iRotation = 0;
	private Timer stop_talk = new Timer(true);
	private Timer tGMove = null;
	private final int iStep = 100;
	private int iCarSpeedR = 0;
	private int iCarSpeedL = 0;
	private int iLastSpeedL = 0;
	private int GsensorCountF = 0;
	private int GsensorCountB = 0;
	private float fBaseDefault = 9999;
	private float fBasePitch = fBaseDefault, fBaseRoll = fBaseDefault;
	private float stickRadiu = 40;
	private float accDefaultX = 9999;
	private float accDefaultY = 9999;

	private int f = 0;
	private int b = 0;
	private int cameramove =0 ;
	private int setting = 0;
	public int audio_play = 0;
	public int audio_stop = 0;
	private int Volume;
	private int isTalk = 0;
	private int isZero = 0;
	
	//private boolean bSetting1 = false;
	public int flag = 0;
	public int sdcheck = 0;
	// Used in the back button behavior
	private long lastPressTime = 0;
	private static final int DOUBLE_PRESS_INTERVAL = 2000;
	private static final String TAG = "";

	public int with;
	public int hight;
	// button timer
	
	private Timer checkSound = new Timer(true);
	private Timer recTimer = new Timer(true);
	private Timer playTimer = new Timer(true);
	private Timer replayTimer = new Timer(true);
	private Timer checkTimer = new Timer(true);
	private Timer checkSDcard = new Timer(true);
	private Timer take_pictrue = new Timer(true);
	private Timer take_video = new Timer(true);
	private Timer stop_take_video = new Timer(true);
	private Timer reConnectTimer = new Timer(true);
	private Timer timeout2 = null;
	private long lastTime;
	private long nSDFreeSize;
	private long startRecordTimeStamp = 0;
	private long recordTimeLength ;
	
	private long recordTimeLength1;
	
	private long replay_start = 0 ;
	//private long replay_stop = 0;
	private long replay_time;
	//private long replay_time1;
	private int replay_flag = 0;
	
	private String FileName;
	private VideoSaveDialog videoSaveDialog = new VideoSaveDialog();
	
	//存放记录拍照的时间的变量名
	public int isplay_pictrue = 0 ;
		public int take_pictrue_T = 0;
		public int take_pictrue_T1;
		public int j = 0;
		public long [] record_times;
		public int take_flag = 0;
	//public int take_p_times = 0;
		public  long [] time ;
		public String take_p;
		public int pictrue_play = 0;
		public int pictrue_play1;
		public int pictrue_pressed = 0;
		//public int t = 0;
		
		//存放记录视频动作的变量名
		//开始录制的
		public int isplay_video = 0;
		public int take_video_T = 0;
		public int take_video_T1;
		public int k = 0;
		public long [] record_video_times;
		public long [] video_time;
		public String take_v;
		public int video_play = 0;
		public int video_play1;
		public int video_record_stop = 0;
		//public long press_video_start;
		private int video_pressed = 0;
		//结束录制的
		public int take_video_T_S = 0;
		public int take_video_T_S1;
		public int s = 0;
		public long [] stop_video_times;
		public long [] stop_time;
		public String stop_take_v;
		public int video_play_stop = 0;
		public int video_play_stop1;
		
		public String FILENSME = "time_record";
		public String FILENAME_V="time_video";
		public String FILENAME_S="stop_video";
		private String RECORD_TIME = "record_time";
		
		private WifiManager mWifiManager;
		private WifiInfo mWifiInfo;
		private int wifiRssi;
		private int RssiLevel;
		private String ssid;
	private Bitmap batteryBitmap = null;
	public static WificarActivity getInstance() {
		return instance;
	}

	public void sendMessage(int cmd){
		Message msg = new Message();
		msg.what = cmd;
		
		this.getHandler().sendMessage(msg);
	}
	public void openVideoStream(String fileName) throws Exception {
		//Log.e("record","start recording:" + DirectPath + " ,"+fileName );
		//Log.e("record","start recording:" + videoWidth + " " + videoHeight);
		wifiCar.startFlim(DirectPath + "/Videos" , fileName, videoWidth, videoHeight);
		videoRecordEnable = true;
	}

	public void closeVideoStream() throws Exception {
		// closeStream();
		//movUtil.finishAVI();
		Log.e("record","stop recording");
		wifiCar.stopFlim();
		videoRecordEnable = false;
		//sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,Uri.parse("file://" + Environment.getExternalStorageDirectory()))); //结束时扫描SDcard
	}
	
	public Handler getHandler() {
		return handler;
	}

	public WifiCar getWifiCar() {
		return wifiCar;
	}

	@Override
	protected void onStop() {
		//if(!isNotExit){
			//Log.e("zhang", "Home exit!");
			isNotExit = false;
			if(!No_Sdcard)
				DeleVideo();
				deleIndexVideo(); //删除*.index文件
			//wifiCar.disconnect();
			//wifiCar.setDisconnect();
			finish();
			System.exit(0);
			//exit();
			exitProgrames();
		//}
		super.onStop();
		//Log.e("activity","on stop");
	}

	@Override
	protected void onPause() {
		
		super.onPause();
		if(!No_Sdcard)
			DeleVideo();
			deleIndexVideo(); //删除*.index文件
		pause();
		
		if(isPlayModeEnable){
			try {
				wifiCar.stopPlayTrack();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		 exitProgrames();
		/* if (!isNotExit ) {
			 Log.d("wild0","disconnect");
			 //wifiCar.setDisconnect();
			 exitProgrames();
			// SplashActivity.getInstance().exit();
			 }*/
		//Log.e("activity", "on Pause");
	
	}

	private BroadcastReceiver spReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String str = intent.getAction();

			Log.d("wild0", "In myReceiver, action = " + str);
			Log.d("Settings", "Received action: " + str);

			if (str.equals("android.intent.action.BATTERY_CHANGED")) {
				//获取手机的电量
				 level = intent.getIntExtra("level", 0);
				 scale = intent.getIntExtra("scale", 100);
				 int power = level * 100 / scale;
				//Log.e("zhangzhang11" ,"电池电量：" + power + "%");
				sendMessage(MESSAGAE_BATTERY);
				if(power < 20){
					isLowPower = true;
				}
				if(power > 20){
					isLowPower = false;
				}
				Log.d("wild0", "battery changed...");
			} else if(str.equals("android.intent.action.BATTERY_LOW")){
				//Log.e("zhang", "低电量提示！");
				//int level = intent.getIntExtra("level", 0);
				 //int scale = intent.getIntExtra("scale", 100);
				isLowPower = true;
				LowPower = level * 100 / scale;
				//Log.e("zhangzhang" ,"低电量时的电池电量：" + (level * 100 / scale) + "%");
			}else if (str
					.equals("android.intent.action.ACTION_POWER_CONNECTED")) {
				//Log.d("wild0", "power connected");
			} else if (str
					.equals("android.intent.action.ACTION_POWER_DISCONNECTED")) {
				//Log.d("wild0", "power disconnected");
			} else if (str.equals("WifiManager.WIFI_STATE_CHANGED_ACTION")) {
				int wifiState = intent.getIntExtra(
						WifiManager.EXTRA_WIFI_STATE,
						WifiManager.WIFI_STATE_UNKNOWN);
				
				//sendMessage(MESSAGE_WIFISTATE);
				//Log.e("wild0", "wifi state is " + wifiState);
			} else if (str.equals("BluetoothAdapter.STATE_ON")) {
				//Log.d("wild0", "bluetooth on");
			} else if (str.equals("BluetoothAdapter.STATE_TURNING_OFF")) {
				//Log.d("wild0", "bluetooth off");
			} else if (str.equals("android.intent.action.SCREEN_OFF")) {

				// wifiCar.disconnect();
				// finish();
				// System.exit(0);
			}else if (str.equals("android.intent.action.SCREEN_ON")) {
			}
		}
	};

	public void exit() {
		if(isPlayModeEnable){
			try {
				wifiCar.stopPlayTrack();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//SplashActivity.getInstance().exit();
		finish();
		System.exit(0);
		
	}
	public void pause(){
		if(wifiCar.isConnected()==1){
			
			Log.e("activity","on exit 1");
			//wifiCar.stopAudio();
			if(isPlayModeEnable){
				try {
					wifiCar.stopPlayTrack();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				wifiCar.led_offTrack();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		try {
			wifiCar.disableIR();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}// 闽超縊
		/*try {
			boolean result = wifiCar.disableAudio();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/// 闽超羘

		disableGSensorControl();// 闽超gsensor

		if (sensorEnable) {
			mSensorManager.unregisterListener(myAccelerometerListener);
			mSensorManager.unregisterListener(myMagneticFieldListener);
		}
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d("activity","on exit");
		//wifiCar.setDisconnect();// 闽超硈絬
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
		reload();
		/*if( setting == 1 && audio_play == 1){
			//play_audio();
			setting_play();
		}*/
		
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.SCREEN_ON");
		intentFilter.addAction("android.intent.action.SCREEN_OFF");
		intentFilter.addAction("android.intent.action.BATTERY_LOW");
		intentFilter.addAction("android.intent.action.BATTERY_OKAY");
		intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
		intentFilter.addAction("android.intent.action.ACTION_POWER_CONNECTED");
		intentFilter.addAction("android.intent.action.ACTION_POWER_DISCONNECTED");
		intentFilter.addAction("WifiManager.WIFI_STATE_CHANGED_ACTION");
		intentFilter.addAction("BluetoothAdapter.STATE_TURNING_OFF");
		intentFilter.addAction("BluetoothAdapter.STATE_ON");

		registerReceiver(spReceiver, intentFilter);
		Log.e("activity", "on Resume");

		Runnable init = new Runnable() {

			// @Override
			public void run() {
				Log.e("wificar", "connecting .....");
				boolean result = wifiCar.setConnect();

				// Log.d("wild0", "connecting result:" + result);
				wifiCar.updatedChange();
				if (result) {
					//Message messageConnectSuccess = new Message();
					//messageConnectSuccess.what = MESSAGE_CONNECT_TO_CAR_SUCCESS;
					//handler.sendMessage(messageConnectSuccess);
					Log.e(TAG, "result1:"+result);
				} else {
					Log.e(TAG, "result2:"+result);
					Message messageConnectFail = new Message();
					messageConnectFail.what = MESSAGE_CONNECT_TO_CAR_FAIL;
					handler.sendMessage(messageConnectFail);
				}
			}

		};

		//if (wifiCar.isChange() || !wifiCar.bConnected) {
		if ( wifiCar.isConnected()==0) {
			//Message messageConnect = new Message();
			//messageConnect.what = MESSAGE_CONNECT_TO_CAR;
			//handler.sendMessage(messageConnect);

			Thread initThread = new Thread(init);
			initThread.start();
		}

		// if(wifiCar==null) return ;
		// wifiCar.connect();
		ToggleButton gSensorTogglebutton = (ToggleButton) findViewById(R.id.g_sensor_toggle_button);
		if (gSensorTogglebutton != null && gSensorTogglebutton.isChecked()) {
			enableGSensorControl();
		}

	}

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.wificar_menu, menu);
		return super.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.videoPlayer_menu_item:
			//isNotExit = true;
			//Intent intent = new Intent();
			//intent.setClass(instance, FileListActivity.class);
			//startActivityForResult(intent, 5);
			break;

		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}*/

	@Override
	protected void onDestroy() {
		// Log.d("wild0","on Destroy");
		super.onDestroy();
		Log.d("activity","on destory");
		if(!No_Sdcard)
			DeleVideo();
			deleIndexVideo(); //删除*.index文件
	}

	// private ViewSwitcher switcher = null;
	public void reload(){
		this.changeLandscape();
		connectionProgressDialog = new ProgressDialog(this);
		//connectionProgressDialog.setTitle("タ硈絬");
		//connectionProgressDialog.setMessage("硈絬い叫祔...");

		// changePortrait();
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		aSensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mfSensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		int o = getResources().getConfiguration().orientation;
		//this.changeLandscape();// ッㄏノlandscape
		//
		// set false when initializing
		ToggleButton irTogglebutton = (ToggleButton) findViewById(R.id.light_toggle_button);
		ToggleButton micTogglebutton = (ToggleButton) findViewById(R.id.mic_toggle_button);
		ToggleButton ledTogglebutton =(ToggleButton) findViewById(R.id.led_toggle_button);
		ToggleButton videTogglebutton =(ToggleButton)findViewById(R.id.video_toggle_button);
		Button takepictruebutton = (Button) findViewById(R.id.take_picture_button);
		
		irTogglebutton.setChecked(false);
		irTogglebutton.setBackgroundResource(R.drawable.ir);
		micTogglebutton.setChecked(false);
		micTogglebutton.setBackgroundResource(R.drawable.mic);
		ledTogglebutton.setChecked(false);
		ledTogglebutton.setBackgroundResource(R.drawable.led);
		videTogglebutton.setChecked(false);
		if(dimension > 5.8){
			videTogglebutton.setBackgroundResource(R.drawable.video);
			takepictruebutton.setBackgroundResource(R.drawable.camera);
		}else {
			videTogglebutton.setBackgroundResource(R.drawable.video1);
			takepictruebutton.setBackgroundResource(R.drawable.camera1);
		}
	}
	@Override
	protected void onStart() {
		super.onStart();
		Log.d("activity", "WificarActivity:on Start");

	}

	public void reStartConnect(){ //断电或者信号弱到没有的时候重新连接
		Log.e("wificar", "restart connecting .....");
		
		reConnectTimer = new Timer(true);
		reConnectTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				boolean result = wifiCar.setConnect();

				Log.e("zhang", "reconnecting result:" + result);
				wifiCar.updatedChange();
				if (result) {
					//Message messageConnectSuccess = new Message();
					//messageConnectSuccess.what = MESSAGE_CONNECT_TO_CAR_SUCCESS;
					//handler.sendMessage(messageConnectSuccess);
				} else {
					Message messageConnectFail = new Message();
					messageConnectFail.what = MESSAGE_CONNECT_TO_CAR_FAIL;
					handler.sendMessage(messageConnectFail);
					
					if(reConnectTimer != null){
						reConnectTimer.cancel();
						reConnectTimer = null;
					}
				}
			}
			
		}, 6000);  //wifi信号断开14s+6s = 20s后重新连接
		
		/**/
	}
	
    
//    public static int dip2px(float dpValue) {
//		return (int) (dpValue * dscale + 0.5f);
//	}
//	
	
	   /** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		instance = this;
		Log.d("activity", "WificarActivity:on Create");

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,   //取消屏保
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,      
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
	//	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
			//	WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//setContentView(R.layout.double_axis_landscape);
		isconnectwifi = note_Intent(instance);
		 DisplayMetrics dm = new DisplayMetrics();
	     getWindowManager().getDefaultDisplay().getMetrics(dm);
		
	 

	 	// 获取屏幕的宽度，高度和密度以及dp / px
//	 	public void getDisplayMetrics() {
//	 		Screen_width = dm.widthPixels;
//	 		Screen_height = dm.heightPixels;
//	 		Log.e(TAG, "Screen_width:"+Screen_width);
//	 		Log.e(TAG, "Screen_height:"+Screen_height);
//	 		scale = activity.getResources().getDisplayMetrics().density;
	 		
	 		Log.e(TAG, "Width = " + dm.widthPixels );
			Log.e(TAG, "Height = " + dm.heightPixels);
//	 	}
	     
		//启动就创建CAR 2.0文件夹
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			DirectPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CAR 2.0";
			
			File fileP = new File(DirectPath + "/Pictures");
			if (!fileP.exists()) {
				fileP.mkdirs();  
			}
			File fileV = new File(DirectPath + "/Videos");
			if (!fileV.exists()) {
				fileV.mkdirs();  
			}
		}
		
		//////////////03.24程序启动后10ms检测SDcard的剩余容量///////////
		checkTimer = new Timer(true);
		checkTimer.schedule(new SDCardSizeTest(), 10);
		//////////////03.24程序启动后每隔1s检测SDcard的剩余容量///////////
		
		/*
		 * 获取屏幕分辨率
		 */
//		DisplayMetrics dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		with = dm.widthPixels ;
		hight = dm.heightPixels;
		density = dm.density;
 		double bb = Math.sqrt(Math.pow(with, 2)
 				+ Math.pow(hight, 2));
 		screenSize = bb / (160 * dm.density);
		Log.e("Main", "Width = " + dm.widthPixels );
		Log.e("Main", "Height = " + dm.heightPixels);
		
		//自定义禁止G-sensor的对话框
		dlg = new DisGsensor(instance,R.style.CustomDialog);
		
		
		new MessageUtility(instance);
		int[] rands = WificarUtility.getRandamNumber();

		wifiCar = new WifiCar(this, rands[0], rands[1], rands[2], rands[3]);
		
		try {
			
			//ImageView battery = null;
			handler = new Handler() {
				
				public void handleMessage(Message msg) {
					
					ToggleButton recordTogglebutton = (ToggleButton) findViewById(R.id.record_toggle_button);
					Button playTogglebutton = (Button) findViewById(R.id.play_toggle_button);
					ToggleButton micbutton = (ToggleButton) findViewById(R.id.mic_toggle_button);
					ImageView soundImg= (ImageView) findViewById(R.id.no_sound);
					final Button takePictureBtn = (Button) findViewById(R.id.take_picture_button);
					final ToggleButton videoTogglebutton = (ToggleButton) findViewById(R.id.video_toggle_button);
					final Button recordAudioTogglebutton = (Button) findViewById(R.id.talk_button);
					switch (msg.what) {
					case MESSAGE_MAIN_SETTING:
						/*if(isSpeaking()){
							micbutton.setChecked(false);
							micbutton.setBackgroundResource(R.drawable.mic);
						}
						if(checkSound != null){
							checkSound.cancel();
							checkSound = null;
						}*/
						break;
					case MESSAGE_GET_SETTING_INFO:
						  new Thread (new MyThread()).start();
						  bThreadRun = true;
						break;
					case MESSAGE_TAKE_PHOTO:
						 /*if (cameraSurfaceView != null) {
								try {
								//	takePictureBtn.setBackgroundResource(R.drawable.camera_pressed);
									Log.e("take photo", "start take photo");
									wifiCar.takePicture(instance);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}*/
						//new Thread (new MyPhotoThread()).start();
						bPhotoThreadRun = true;
						handler.postDelayed(TakePhotoTask, 10);
						break;
					case MESSAGE_SOUND:
						
						AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE); 
						Volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
						Log.e("zhang", "currentVolume11 :" + Volume);
						
						if(Volume == 0){
							//Log.e("zhang", "soudImg :" + soundImg);
							isZero = 1;
							soundImg.setVisibility(View.VISIBLE);
							micbutton.setBackgroundResource(R.drawable.mic);
							micbutton.setChecked(false);
							micbutton.setClickable(true);
						}
						else {
							soundImg.setVisibility(View.GONE);
							if(audio_play == 0 || isTalk == 1){ //isTalk=1为对讲按钮按下时 ，audio_play = 0为Mic按钮手动按下还原
								soundImg.setVisibility(View.VISIBLE);
							}
							if(audio_play == 1 && isTalk == 0){ // MIC按钮没有手动还原，对讲按钮没有按下
								micbutton.setBackgroundResource(R.drawable.mic_pressed);
								micbutton.setChecked(true);
								micbutton.setClickable(true);
								
								isZero = 0;
							}
						}
						break;
					case MESSAGE_WIFISTATE:
						  /*switch (wifiState) { 
				            case WifiManager.WIFI_STATE_DISABLED: 
				            	Toast.makeText(instance	, "wifi关闭！", 
				                        Toast.LENGTH_LONG).show(); 
				                break; 
				            case WifiManager.WIFI_STATE_DISABLING: 
				                break; 
				            case WifiManager.WIFI_STATE_ENABLED: 
				               
				                Toast.makeText(instance	, "wifi打开！", 
				                        Toast.LENGTH_LONG).show(); 
				                break; 
				            case WifiManager.WIFI_STATE_ENABLING: 
				                break; 
				            case WifiManager.WIFI_STATE_UNKNOWN: 
				                break; 
				            } */
						break;
					
					case MESSAGE_CONNECT_TO_CAR:
						//connectionProgressDialog.show();
						break;
					
					case MESSAGE_CONNECT_TO_CAR_SUCCESS:
						
						succeedConnect = true;
						//refreshUIListener();
						connectionProgressDialog.cancel();
						//Toast connectedToast = Toast.makeText(instance,
						//		"Success to connect", Toast.LENGTH_SHORT);
						///connectedToast.show();
						//InitWifiInfo();
						//启动软件后获取当前连接的SSID
						/*try {
							startSSID = wifiCar.getSSID();
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}*/
						checkSDcard = new Timer(true);
						checkSDcard.schedule(new TimerTask() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								//Log.e("zhangzhang", "检测是否存在SDcard");
								wifiCar.isConnected();
								if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
									//Log.e("zhangzhang", "检测到没有SDcard");
									No_Sdcard = true;
								}else{
									No_Sdcard = false;
									//Log.e("zhangzhang", "检测到又有SDcard存在了");
								}
								if(!No_Sdcard)
									deleIndexVideo(); //删除*.index文件
							}
						}, 100, 1000);
						
						break;
					case MESSAGE_CONNECT_TO_CAR_FAIL:
						connectionProgressDialog.cancel();
						
						Connect_Dialog.createconnectDialog(instance).show();
					
							//wifi_not_connect.createwificonnectDialog(instance).show();
						isNotExit = true;
						connect_error = true; 
						break;
					case MESSAGE_STOP_RECORD:
						Toast toast = Toast.makeText(instance, R.string.complete_record, Toast.LENGTH_SHORT);
						//toast.setGravity(Gravity.CENTER, 0, 0);   
						toast.show();   
						try {
							wifiCar.stopRecordTrack();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						recordTogglebutton.setBackgroundResource(R.drawable.record_path);
						//recordTogglebutton.setTextColor(Color.WHITE);
						playTogglebutton.setClickable(true);
						if (recTimer != null) {
							recordTimeLength = System.currentTimeMillis()
									- startRecordTimeStamp;
							// Log.d("wild0","time length:"+recordTimeLength);
							
							SharedPreferences share_time = getSharedPreferences(RECORD_TIME, 0);// 指定操作的文件名称				// 指定操作的文件名称
							SharedPreferences.Editor edit_time = share_time.edit(); 	// 编辑文件
							
							edit_time.putLong("record", recordTimeLength);	// 保存long型
							edit_time.commit() ;	
							
							recTimer.cancel();
							recTimer = null;
						}
						
						
						take_flag = 0;
						video_record_stop = 0;
						take_pictrue_T = 0;
						take_video_T = 0;
						take_video_T_S =0 ;
						/*
						 * recordTogglebutton.setBackgroundResource(R.drawable.
						 * record_path );
						 * recordTogglebutton.setTextColor(Color.WHITE);
						 * playTogglebutton.setClickable(true);
						 * 
						 * recordTimeLength = System.currentTimeMillis()-
						 * startRecordTimeStamp;
						 */
						break;
					case MESSAGE_STOP_PLAY:
						disablePlayMode();
						isPlayModeEnable = false;
						replay_flag = 0;
						isplay_pictrue =0;
						j = 0;
						k = 0;
						s = 0;
						break;

					case MESSAGE_START_RECORD:

						take_flag = 1;
						pictrue_play = 0;
						video_play = 0;
						video_play_stop = 0;
						
						/*SharedPreferences share_time_reocrd = getSharedPreferences(FILENSME, 0);// 指定操作的文件名称
						SharedPreferences share_v = getSharedPreferences(FILENAME_V, 0);// 指定操作的文件名称
						SharedPreferences.Editor edit_time_reocrd = share_time_reocrd.edit(); 	// 编辑文件
						SharedPreferences.Editor edit_v = share_v.edit(); 	// 编辑文件
						
						
						edit_time_reocrd.putInt("pictrue_play", pictrue_play);	// 保存int型
						edit_v.putInt("video_play", video_play);	// 保存int型
						edit_v.putInt("video_play_stop", video_play_stop);	// 保存int型
						
						edit_time_reocrd.commit() ;
						edit_v.commit();*/
						
						try {
							wifiCar.startRecordTrack();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						recordTogglebutton.setBackgroundResource(R.drawable.record_path_pressed);
						recordTogglebutton.setTextColor(Color.BLACK);
						playTogglebutton.setClickable(false);

						// Log.d("wild0", "start record");
						startRecordTimeStamp = System.currentTimeMillis();

						recTimer = new Timer(true);
						recTimer.schedule(new RecordTask(), 60000);

						break;
					case MESSAGE_START_PLAY:
						isPlayModeEnable = true;
						try {
							wifiCar.startPlayTrack();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						playTogglebutton.setBackgroundResource(R.drawable.replay_pressed);
						
						//recordTogglebutton.setClickable(false);
						
						/////////////03.16读取保存的时间///////////////////
						//SharedPreferences share1 = getSharedPreferences(FILENSME, MODE_PRIVATE);
						//recordTimeLength1 = share1.getLong("record", recordTimeLength);
						/////////////03.16读取保存的时间///////////////////

						//if(replay_flag ==1){
						//	replay_time = System.currentTimeMillis() - replay_start;
						//}
						
						 SharedPreferences share_time_play = getSharedPreferences(RECORD_TIME, 0);// 指定操作的文件名称
						 recordTimeLength1 = share_time_play.getLong("record", recordTimeLength);
						
						replayTimer = new Timer(true);
						replayTimer.schedule(new rePlayTask(), recordTimeLength1);
						Log.e("aaaa", "replaystar");
						break;
						
					
					case MESSAGE_BATTERY_UNKNOWN:
						//ImageView battery0 = (ImageView)instance.findViewById(R.id.battery_image_view);
						//battery0.setBackgroundResource(R.drawable.battery_0);
						break;
					/*case MESSAGE_BATTERY_0:
						ImageView battery0 = (ImageView)instance.findViewById(R.id.battery_image_view);
						battery0.setVisibility(View.VISIBLE);
						batteryBitmap = BitmapFactory.decodeResource(instance.getResources(), R.drawable.battery_0);
						battery0.setImageBitmap(batteryBitmap);
						
						break;
					case MESSAGE_BATTERY_25:
						ImageView battery25 = (ImageView)instance.findViewById(R.id.battery_image_view);
						battery25.setVisibility(View.INVISIBLE);
						//batteryBitmap = BitmapFactory.decodeResource(instance.getResources(), R.drawable.battery_0);
						//battery25.setImageBitmap(batteryBitmap);
						break;*/
						
					/*case MESSAGE_BATTERY_50:
						ImageView battery50 = (ImageView)instance.findViewById(R.id.battery_image_view);
						batteryBitmap = BitmapFactory.decodeResource(instance.getResources(), R.drawable.battery_50);
						battery50.setImageBitmap(batteryBitmap);
						break;
					case MESSAGE_BATTERY_75:
						ImageView battery75 = (ImageView)instance.findViewById(R.id.battery_image_view);
						batteryBitmap = BitmapFactory.decodeResource(instance.getResources(), R.drawable.battery_75);
						battery75.setImageBitmap(batteryBitmap);
						break;
					case MESSAGE_BATTERY_100:
						Log.d("wild1", "battery:100");
						ImageView battery100 = (ImageView)instance.findViewById(R.id.battery_image_view);
						batteryBitmap = BitmapFactory.decodeResource(instance.getResources(), R.drawable.battery_100);
						battery100.setImageBitmap(batteryBitmap);
						//battery100.setBackgroundResource(R.drawable.battery_100);
						break;*/
					case MESSAGE_START_RECORD_AUDIO:
						try {
							Log.d("wild1", "record audio");
							wifiCar.enableRecordAudio(0);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					case MESSAGE_STOP_RECORD_AUDIO:
						recordAudioTogglebutton.setBackgroundResource(R.drawable.talk);
						try {
							wifiCar.disableRecordAudio();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if(audio_play == 1){//如果当前已经打开了MIC接收声音，关闭对讲的时候再继续打开MIC
							play_audio();
							setting_play();
							wifiCar.disableMoveFlag();
							micbutton.setBackgroundResource(R.drawable.mic_pressed);
							micbutton.setChecked(true);
						}
						
						if(stop_talk != null){
							stop_talk.cancel();
							stop_talk = null;
						}
						break;
					case MESSAGE_CHECK_TEST:
						try {
							closeVideoStream();
							Toast toast1 = Toast.makeText(instance,
									R.string.wificar_activity_toast_stop_recording,Toast.LENGTH_SHORT);
							//toast1.setGravity(Gravity.CENTER, 0, 0);   
							toast1.show();   
						} catch (Exception e) {
						// TODO Auto-generated catch block
							e.printStackTrace();
											}	
						checkSDcard();
						
						break;	
					default:
						break;
					}
					super.handleMessage(msg);
				}
			};
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 public class MyThread implements Runnable{
		  // 线程的主要工作方法  获取设置界面的信息
		  public void run() {
			  while (bThreadRun) { 
				    IP = wifiCar.getHost();
					Port = String.valueOf(wifiCar.getPort());
					version = wifiCar.getVersion(instance);
					firmwareVersion = wifiCar.getFilewareVersion();
					
					if(!firmwareVersion.equals("")){
						firmwareVersion ="1.0";
					}else if(firmwareVersion.equals("")){
						firmwareVersion =" ";
					}
					//Log.e("zhang", "firmwareVersion11 :" +firmwareVersion);
					
					try {
						SSID = wifiCar.getSSID();
						Log.e("zhang", "获取到了SSID：" + SSID);
						bThreadRun = false;
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			  }
		  }
	 } 
	/* public class MyPhotoThread implements Runnable{
		  public void run() {
			  while (bPhotoThreadRun) { 
				  if (cameraSurfaceView != null) {
						try {
						//	takePictureBtn.setBackgroundResource(R.drawable.camera_pressed);
							wifiCar.takePicture(instance);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				  bPhotoThreadRun = false;
			  }
		  }

	 } */
	
		//获取屏幕的尺寸
	 public static double getDisplayMetrics(Context cx) {
        String str = "";
        DisplayMetrics dm = new DisplayMetrics();
        dm = cx.getApplicationContext().getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        //float density = dm.density;
        
        double bb = Math.sqrt(Math. pow(screenWidth, 2) + Math.pow(screenHeight, 2));
        double screeSize = bb / (160 * dm.density);
        Log.e("zhang", "ping mu 尺寸：" + screeSize);
        
        return screeSize;
}
	
	public void lockOrientation() {
		orientationLock = true;

		// 20111125: Lock Landscape
		return;
	}

	public void releaseOrientation() {
		orientationLock = false;
		// 20111125: Lock Landscape
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
	}

	public boolean enableGSensorControl() {
		isGsensor = false;
		LRshow = true;
		this.gSensorControlEnable = true;
		LR = 0;

		accDefaultX = fBaseDefault;
		accDefaultY = fBaseDefault;
		mSensorManager.registerListener(myAccelerometerListener, aSensor,
				SensorManager.SENSOR_DELAY_UI);
		mSensorManager.registerListener(myMagneticFieldListener, mfSensor,
				SensorManager.SENSOR_DELAY_UI);
		
		/*
		 * 当开始G-sensor功能时把accDefaultX 和 accDefaultY 是设置为 fBaseDefault ，是为了在重新打开G-sensor的时候判断，设置初始值
		 * */
		accDefaultX = fBaseDefault;
		accDefaultY = fBaseDefault;
		return false;

	}
	
	public boolean isgSensorModeEnable(){
		ToggleButton gSensorTogglebutton = (ToggleButton) findViewById(R.id.g_sensor_toggle_button);
		return gSensorTogglebutton.isChecked();
	}
	public void disablegSensorMode(){
		ToggleButton gSensorTogglebutton = (ToggleButton) findViewById(R.id.g_sensor_toggle_button);
		gSensorTogglebutton.setBackgroundResource(R.drawable.g);
		disableGSensorControl();
	}
	
	public boolean isSpeaking(){
		ToggleButton speakingbutton = (ToggleButton) findViewById(R.id.mic_toggle_button);
		return speakingbutton.isChecked();
		 
	}
	public boolean istaking(){
		Button takePictureBtn = (Button) findViewById(R.id.take_picture_button);
		return takePictureBtn.isClickable();
		 
	}
	public void disablegSpeaking(){
		Button speakingbutton = (Button) findViewById(R.id.mic_toggle_button);
		speakingbutton.setBackgroundResource(R.drawable.mic);
		speakingbutton.setClickable(false);
		try {
			wifiCar.disableAudio();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void disablePlayMode(){
		//if(!isNotExit){
			Toast toast = Toast.makeText(instance, R.string.complete_play, Toast.LENGTH_SHORT);
			//toast.setGravity(Gravity.CENTER, 0, 0);   
			toast.show();
		//}
		   
		Button playTogglebutton = (Button) findViewById(R.id.play_toggle_button);
		final Button takePictureBtn = (Button) findViewById(R.id.take_picture_button);
		final ToggleButton videoTogglebutton = (ToggleButton) findViewById(R.id.video_toggle_button);
		
		isPlayModeEnable =false;
		
		/*if(take_pictrue != null){
			take_pictrue.cancel();
			take_pictrue = null;
		}
		
		if(take_video != null){
			take_video.cancel();
			take_video = null;
		}
		
		if(stop_take_video != null){
			stop_take_video.cancel();
			stop_take_video = null;
		}*/
		
		if (playTimer != null) {
			
			playTimer.cancel();
			playTimer = null;
		}
		if(replayTimer != null){
			replayTimer.cancel();
			replayTimer = null;
		}
		/* SharedPreferences share_s1 = getSharedPreferences(FILENAME_S, 0);

		 video_play_stop1 = share_s1.getInt("video_play_stop", video_play_stop);
		if(video_play_stop1 ==1){
			if(dimension > 5.8){
				videoTogglebutton.setBackgroundResource(R.drawable.video);
			}else {
				videoTogglebutton.setBackgroundResource(R.drawable.video1);
			}
			//videoTogglebutton.setBackgroundResource(R.drawable.video);
			videoTogglebutton.setChecked(false);
		//videoRecordEnable = false;
		try {
		closeVideoStream();
		//Toast.makeText(instance,
				//R.string.wificar_activity_toast_stop_recording,Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
			}
		}*/
		
		//takePictureBtn.setBackgroundResource(R.drawable.camera);
	
		//takePictureBtn.setEnabled(true);
		//takePictureBtn.setPressed(true);
		
		try {
			wifiCar.stopPlayTrack();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		playTogglebutton.setBackgroundResource(R.drawable.play_path);
		
		Log.e("aaa", "zdf");
		flag = 0;
		
		j = 0;
		k = 0;
		s = 0;
		
	}
	public void checkSDcard(){
		
		SDcardCheck.creatSDcardCheckDialog(instance).show();
		if(checkTimer != null){
			
			checkTimer.cancel();
			checkTimer = null;		
		}
		
	}


	public boolean disableGSensorControl() {
		

		gSensorControlEnable = false;
		handler.removeCallbacks(gMovingTask);
		releaseOrientation();
		// Log.d("wild0","g disable");
		
		accDefaultX = fBaseDefault;
		accDefaultY = fBaseDefault;
		
		try {
			wifiCar.move(WifiCar.LEFT_WHEEL, 0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			wifiCar.move(WifiCar.RIGHT_WHEEL,0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		mSensorManager.unregisterListener(myAccelerometerListener);
		
		//releaseOrientation();
		fBasePitch = fBaseDefault;
		fBaseRoll = fBaseDefault;
		
		leftSurface.enableControl();
		rightSurface.enableControl();
		// show();
		/*
		 * 当结束G-sensor功能时把accDefaultX 和 accDefaultY 是设置为 fBaseDefault ，是为了在重新打开G-sensor的时候判断，设置初始值
		 * */
		accDefaultX = fBaseDefault;
		accDefaultY = fBaseDefault;
				
		//	}
		//}
		return true;
	}

	class GMovingTask extends TimerTask {
		public GMovingTask() {/*
							 * if (fMagneticFieldValues != null &&
							 * fAccelerometerValues != null) { float[] values =
							 * new float[3]; float[] R = new float[9]; float[]
							 * outR = new float[9];
							 * 
							 * SensorManager.getRotationMatrix(R, null,
							 * fAccelerometerValues, fMagneticFieldValues);
							 * 
							 * if (bIsPortait == false)
							 * SensorManager.remapCoordinateSystem(R,
							 * SensorManager.AXIS_Y, SensorManager.AXIS_Z,
							 * outR); else outR = R;
							 * 
							 * SensorManager.getOrientation(outR, values);
							 * 
							 * values[0] = (float) Math.toDegrees(values[0]);
							 * values[1] = (float) Math.toDegrees(values[1]);
							 * values[2] = (float) Math.toDegrees(values[2]);
							 * 
							 * fBasePitch = (float) Math.floor(values[1]);
							 * fBaseRoll = (float) Math.floor(values[2]); }
							 */
		}

		public void run() {

			// Get value

			if (fMagneticFieldValues != null && fAccelerometerValues != null) {
				float[] values = new float[3];
				float[] R = new float[9];
				float[] outR = new float[9];

				SensorManager.getRotationMatrix(R, null, fAccelerometerValues,
						fMagneticFieldValues);

				if (bIsPortait == false)
					SensorManager.remapCoordinateSystem(R,
							SensorManager.AXIS_Y, SensorManager.AXIS_Z, outR);
				else
					outR = R;

				SensorManager.getOrientation(outR, values);

				values[0] = (float) Math.toDegrees(values[0]);
				values[1] = (float) Math.toDegrees(values[1]);
				values[2] = (float) Math.toDegrees(values[2]);

				float fPitch = (float) Math.floor(values[1]);
				float fRoll = (float) Math.floor(values[2]);
				Log.d("gsensor", "pitch:" + fPitch + ",roll" + fRoll);
				Log.d("gsensor", "fBasePitch:" + fBasePitch);
				Log.d("gsensor", "fBaseDefault:" + fBaseDefault);
				// Check if base value is exist, if not, save current one as
				// default value

				// Log.d("wild0","bIsPortait:"+bIsPortait);
				// Log.d("wild0","fPitch:"+fPitch);
				// Log.d("wild0","fBasePitch:"+fBasePitch);

				if ((fBasePitch == fBaseDefault) || (fBaseRoll == fBaseDefault)) {
					fBasePitch = fPitch;
					// fBasePitch = 0;
					fBaseRoll = fRoll;
					Log.d("gsensor", "reset============================");
				} else {
					// Calculate the speed
					float fVer = fPitch - fBasePitch;
					float fHor = fRoll - fBaseRoll;

					// Set the initial angle, if the angle is larger than this,
					// it starts moving
					int iInitialAngle = 15;

					// Re-calculate the vertical angle
					if (fVer > (iInitialAngle + stickRadiu))
						fVer = (iInitialAngle + stickRadiu);
					else if (fVer < -(iInitialAngle + stickRadiu))
						fVer = -(iInitialAngle + stickRadiu);

					if (fVer < iInitialAngle && fVer > -(iInitialAngle))
						fVer = 0;
					else if (fVer >= iInitialAngle)
						fVer -= iInitialAngle;
					else if (fVer <= -(iInitialAngle))
						fVer += iInitialAngle;

					// Re-calculate the horizontal angle
					if (fHor > (iInitialAngle + stickRadiu))
						fHor = (iInitialAngle + stickRadiu);
					else if (fHor < -(iInitialAngle + stickRadiu))
						fHor = -(iInitialAngle + stickRadiu);

					if (fHor < iInitialAngle && fHor > -(iInitialAngle))
						fHor = 0;
					else if (fHor >= iInitialAngle)
						fHor -= iInitialAngle;
					else if (fHor <= -(iInitialAngle))
						fHor += iInitialAngle;

					Log.d("gsensor", "fVer:" + fVer);

					// Android 2.1: Support 2 orientation //
					setDirection(fHor, fVer);

					// Android 2.3.1: Support 4 orientation //
					/*
					 * if (iRotation == 2 || iRotation == 3) setDirection(-fHor,
					 * -fVer); else setDirection(fHor, fVer);
					 */

				}
			}

		}
	}

	public int getControllerType() {
		return this.controllerType;
	}

	public CameraSurfaceView getCameraSurfaceView() {
		return this.cameraSurfaceView;
	}

	public void setControllerType(int controllerType) {
		this.controllerType = controllerType;
	}

	public static boolean isVideoRecord() {
		return videoRecordEnable;
	}

	private void setDirection(float x, float y) {
		
		WindowManager mWindowManager =  (WindowManager) getSystemService(WINDOW_SERVICE);
	    Display mDisplay = mWindowManager.getDefaultDisplay();
	    if(mDisplay.getOrientation()==0){
	    	
			
			double radians = 0;
			if (y == 0){
				radians = x;
				Log.e("zhang", "radians y=0 :" + radians);
				}
			else if (x == 0){
				radians = y;
				Log.e("zhang", "radians x=0 :" + radians);
				}
			else{
				radians = Math.atan(Math.abs(y) / Math.abs(x));
				Log.e("zhang", "radians else :" + radians);
				}
			double angle = radians * (180 / Math.PI);
			Log.e("gsensor", "angle : " + angle);
			// Get speed first
			int iSpeed = 0;
			iSpeed = (int) Math.ceil(Math.pow(Math.pow(x, 2) + Math.pow(y, 2), 0.5)
					/ stickRadiu * 10);
			Log.e("zhang", "iSpeed :" + iSpeed);
			int iSpeedR = 0, iSpeedL = 0;

			Log.e("zhang", "x :" + x + "y : " + y );
			// Check dimension
			if (x == 0 && y != 0) {//前、后
				if (y > 0) {//前
					iSpeedL = iSpeed;
					iSpeedR = iSpeed;
				} else {//后
					iSpeedL = -iSpeed;
					iSpeedR = -iSpeed;
				}
			} else if (x != 0 && y == 0) {//左、右
				if (x > 0) {//右
					//iSpeedL = iSpeed;
					//iSpeedR = -iSpeed;
					iSpeedL = -iSpeed;
					iSpeedR = iSpeed;
				} else {//左
					//iSpeedL = -iSpeed;
					//iSpeedR = iSpeed;
					iSpeedL = iSpeed;
					iSpeedR = -iSpeed;
				}
			} else if (x > 0 && y > 0) {
				// 1st, right forward
				iSpeedL = iSpeed;

				// Calculate the speed of right wheel
				if (angle >= 67.5)
					iSpeedR = iSpeedL;
				else if (angle < 22.5)
					iSpeedR = -iSpeedL;
				else
					//iSpeedR = 0;
					iSpeedR = iSpeed;
					iSpeedL = 0;
			} 
				else if (x < 0 && y > 0) {//倾斜的时候 向左
				// 2nd, left forward
				iSpeedR = iSpeed;

				// Calculate the speed of right wheel
				if (angle >= 67.5)
					iSpeedL = iSpeedR;
				else if (angle < 22.5)
					iSpeedL = -iSpeedR;
					//iSpeedL = iSpeedR;
				else
					iSpeedL = 0;
					iSpeedR = iSpeed;
					//iSpeedR = 0;
					//iSpeedL = iSpeed;
			} else if (x < 0 && y < 0) {
				// 3rd, left backward
				iSpeedL = -iSpeed;

				// Calculate the speed of right wheel
				if (angle >= 67.5)
					iSpeedR = iSpeedL;
				else if (angle < 22.5)
					iSpeedR = -iSpeedL;
				else {
					//iSpeedL = 0;
					//iSpeedR = -iSpeed;
					
					iSpeedL = -iSpeed;
					iSpeedR = 0;
				}
			} else {
				Log.e("zhang", "else x and y :" + x + " ,"+ y);
				// 4th, right backward
				iSpeedR = -iSpeed;
				//iSpeedL = -iSpeed;//当向后超过垂直的时候左边也应该向后
				// Calculate the speed of right wheel
				if (angle >= 67.5)
					iSpeedL = iSpeedR;
				else if (angle < 22.5)
					iSpeedL = -iSpeedR;
				else {
					//iSpeedR = 0;
					//iSpeedL = -iSpeed;
					iSpeedL = 0;
					iSpeedR = -iSpeed;
					
				}
			}


			if (iSpeedL > 10)
				iSpeedL = 10;
			if (iSpeedL < -10)
				iSpeedL = -10;

			if (iSpeedR > 10)
				iSpeedR = 10;
			if (iSpeedR < -10)
				iSpeedR = -10;

			// Log.d("wificar", "R: " + iSpeedR + " , L: " + iSpeedL);

			// wifiCar.setContinueSpeed(iSpeedR, iSpeedL);

			Log.e("zhang", "the speed isSpeedL is :" + iSpeedL ); 
			Log.e("zhang", "the speed isSpeedR is :" + iSpeedR ); 


			if (iSpeedL == 0 && iCarSpeedL != 0)
				try {
					wifiCar.move(WifiCar.LEFT_WHEEL, iSpeedL);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (iSpeedR == 0 && iCarSpeedR != 0)
				try {
					wifiCar.move(WifiCar.RIGHT_WHEEL, iSpeedR);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			iCarSpeedL = iSpeedL;
			iCarSpeedR = iSpeedR;

			Log.e("zhang", "the speed iCarSpeedL is :" + iCarSpeedL ); 
			Log.e("zhang", "the speed iCarSpeedR is :" + iCarSpeedR ); 
			if (iCarSpeedL != 0)
				try {
					wifiCar.move(WifiCar.LEFT_WHEEL, iCarSpeedL);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (iCarSpeedR != 0)
				try {
					wifiCar.move(WifiCar.RIGHT_WHEEL, iCarSpeedR);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


	    	}
	    }

	// SensorEventListener myAccelerometerListener = null;
	// SensorEventListener myMagneticFieldListener = null;

	final SensorEventListener myAccelerometerListener = new SensorEventListener() {
		// @Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
		private int[] convertGravity(float dx, float dy, float gx, float gy, float range, int orientation){
			int leftVolecity = 0;
			int rightVolecity = 0;
			float my = gy-dy;
			float mx = gx-dx;
			LR ++;
			Log.e("zhang", "my :" + my + "," + "mx :" + mx);
			
			if(orientation==1){
			if(Math.abs(my)>range && my<-range ){ // 左转
				
				//leftVolecity = (int) Math.ceil(my)*(-1);;
				//rightVolecity = (int) Math.ceil(my)*(1);
				leftVolecity = (int) Math.ceil(my)*(1);;
				rightVolecity = (int) Math.ceil(my)*(-1);
				Log.e("acce", "left:"+leftVolecity+","+rightVolecity);
			}
			else if(Math.abs(my)>range && my>range ){ //右转
				
				//leftVolecity = (int) Math.ceil(my)*(-1);
				//rightVolecity = (int) Math.ceil(my)*(1);
				leftVolecity = (int) Math.ceil(my)*(1);
				rightVolecity = (int) Math.ceil(my)*(-1);
				Log.e("acce", "right:"+leftVolecity+","+rightVolecity);
			}
			else{
				
			}
			}
			else{
				if(Math.abs(my)>range && my<-range ){  //右转
					
					leftVolecity = (int) Math.ceil(my)*(-1);
					rightVolecity = (int) Math.ceil(my)*(1);
					
					Log.e("acce11", "right:"+leftVolecity+","+rightVolecity);
				}
				else if(Math.abs(my)>range && my>range ){  // 左转
					
					//leftVolecity = (int) Math.ceil(my)*(1);
					//rightVolecity = (int) Math.ceil(my)*(-1);
					leftVolecity = (int) Math.ceil(my)*(-1);
					rightVolecity = (int) Math.ceil(my)*(1);
					Log.e("acce11", "left:"+leftVolecity+","+rightVolecity);
				}
				else{
					
				}
			}
			
			if(mx<-range){ //前进
				//leftVolecity = gx;
				
				leftVolecity = leftVolecity-(int) Math.ceil(mx);
				rightVolecity = rightVolecity-(int) Math.ceil(mx);
				GsensorCountF ++;
				if(GsensorCountF < 40 && gSensorControlEnable){
					Log.e("zhang", "Gsensor :" + GsensorCountF);
					setDirectionGsensor(leftVolecity, rightVolecity);
					Log.e("zhang Forware", " leftVolecity :" + leftVolecity + "," + "rightVolecity :"+ rightVolecity);
				}
			}
			else if(mx>range ){ //后退
				leftVolecity = leftVolecity-((int) Math.ceil(mx));
				rightVolecity = rightVolecity-((int) Math.ceil(mx));
				GsensorCountB ++;
				if(GsensorCountB < 40 && gSensorControlEnable){ 
					setDirectionGsensor(leftVolecity, rightVolecity);
					Log.e("zhang Back", " leftVolecity :" + leftVolecity + "," + "rightVolecity :"+ rightVolecity);
				}
			}
			
			if(leftVolecity  >= 2){
				leftVolecity = 10;
			}
			if(leftVolecity <= -2){
				leftVolecity = -10;
			}
			if(rightVolecity >= 2){
				rightVolecity = 10;
			}
			if(rightVolecity <= -2){
				rightVolecity = -10;
			}
			
			Log.e("zhang", " leftVolecity :" + leftVolecity + "," + "rightVolecity :"+ rightVolecity);
			return new int[]{leftVolecity, rightVolecity};
		}
		// @Override
		public void onSensorChanged(SensorEvent event) {
			
			fAccelerometerValues = event.values;
			if(accDefaultX == fBaseDefault){
				accDefaultX = fAccelerometerValues[0];
			}
			if(accDefaultY == fBaseDefault){
				accDefaultY = fAccelerometerValues[1];
			}
			WindowManager mWindowManager =  (WindowManager) getSystemService(WINDOW_SERVICE);
		    Display mDisplay = mWindowManager.getDefaultDisplay();
			Log.e("acce", "getOrientation(): " + mDisplay.getOrientation());
			
			Log.e("acce", "x:"+fAccelerometerValues[0]+",y:"+fAccelerometerValues[1]+",z:"+fAccelerometerValues[2]);
			Log.e("acce", "dx:"+accDefaultX+",dy:"+accDefaultY);
			int[] iCar = new int[2];
			
			//判断在启动G-sensor的时候角度的情况  是否接近垂直 ，若是则显示图片提示
			/*
			 * 向前接近了垂直
			 * */
			if( mDisplay.getOrientation()==1){
				if((fAccelerometerValues[0]< 0.1f && fAccelerometerValues[2] < 9.8f  &&  !isGsensor) 
						|| (fAccelerometerValues[0]> 7.6f && fAccelerometerValues[2] < 5.8f &&  !isGsensor )){  //|| ( fAccelerometerValues[1] < -5.5 && fAccelerometerValues[1] < 7.9  &&  !isGsensor )
					//Log.e("zhang", "想前快垂直了");
					disGsensor = true;
					showDialog();
					gSensorControlEnable = false;
					mSensorManager.unregisterListener(myAccelerometerListener);
				}else {
				
					handler.postDelayed(gMovingTask, 100);
					isGsensor = true;
					//Log.e("zhangLLLLLLRRRRRRRR", LR + "");
					if(LR < 1){
						 if (leftSurface != null && rightSurface != null && gSensorControlEnable) {
							 leftSurface.disableControl();
							 rightSurface.disableControl();
						 }
					}
					if( mDisplay.getOrientation()==1){
						iCar = convertGravity(accDefaultX, accDefaultY, fAccelerometerValues[0], fAccelerometerValues[1], 1.5f, mDisplay.getOrientation());
						iCarSpeedL = iCar[0];
						iCarSpeedR = iCar[1];
					}
					else{
						iCar = convertGravity(accDefaultY, accDefaultX, fAccelerometerValues[1], fAccelerometerValues[0], 1.5f, mDisplay.getOrientation());
						iCarSpeedL = iCar[0];
						iCarSpeedR = iCar[1];
					}
					Log.e("acce", "cl:"+iCar[0]+",cr:"+iCar[1]);
		
					if(iCarSpeedL != iCarSpeedR){  //向左 、向右
						try {
							wifiCar.enableMoveFlag();
							wifiCar.move(WifiCar.LEFT_WHEEL, iCar[0]);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		
						try {
							wifiCar.enableMoveFlag();
							wifiCar.move(WifiCar.RIGHT_WHEEL, iCar[1]);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					if(GsensorCountB > 40 || GsensorCountF > 40){
						gSensorControlEnable = false;
						Log.e("zhang", "zhang zhang zhang:" + GsensorCountF);
						if(iCarSpeedL == iCarSpeedR){  //向前 、向后
							try {
								wifiCar.enableMoveFlag();
								wifiCar.move(WifiCar.LEFT_WHEEL, iCar[0]);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							try {
								wifiCar.enableMoveFlag();
								wifiCar.move(WifiCar.RIGHT_WHEEL, iCar[1]);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
					}
					if(iCarSpeedL == 0 && iCarSpeedR == 0 ){   //停止
						GsensorCountB = 0;
						GsensorCountF = 0;
						
						//gSensorControlEnable = true;
						try {
							wifiCar.disableMoveFlag();
							wifiCar.move(WifiCar.LEFT_WHEEL, iCarSpeedL);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							wifiCar.disableMoveFlag();
							wifiCar.move(WifiCar.RIGHT_WHEEL, iCarSpeedR);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
					}	
			
				}
			}else if( mDisplay.getOrientation()==0){
				if((fAccelerometerValues[1] < 0 && fAccelerometerValues[2] < 9.7  &&  !isGsensor) 
						|| (fAccelerometerValues[1]> 7.8f && fAccelerometerValues[2] < 5.5f &&  !isGsensor )){  //|| ( fAccelerometerValues[1] < -5.5 && fAccelerometerValues[1] < 7.9  &&  !isGsensor )
					Log.e("zhang", "想前快垂直了");
					showDialog();
					gSensorControlEnable = false;
					mSensorManager.unregisterListener(myAccelerometerListener);
				}else{
					handler.postDelayed(gMovingTask, 100);
					isGsensor = true;
					Log.e("zhangLLLLLLRRRRRRRR", LR + "");
					if(LR < 1){
						 if (leftSurface != null && rightSurface != null && gSensorControlEnable) {
							 leftSurface.disableControl();
							 rightSurface.disableControl();
						 }
					}
					if( mDisplay.getOrientation()==1){
						iCar = convertGravity(accDefaultX, accDefaultY, fAccelerometerValues[0], fAccelerometerValues[1], 1.5f, mDisplay.getOrientation());
						iCarSpeedL = iCar[0];
						iCarSpeedR = iCar[1];
					}
					else{
						iCar = convertGravity(accDefaultY, accDefaultX, fAccelerometerValues[1], fAccelerometerValues[0], 1.5f, mDisplay.getOrientation());
						iCarSpeedL = iCar[0];
						iCarSpeedR = iCar[1];
					}
					Log.e("acce", "cl:"+iCar[0]+",cr:"+iCar[1]);
		
					if(iCarSpeedL != iCarSpeedR){   //向左 、向右
						try {
							wifiCar.enableMoveFlag();
							wifiCar.move(WifiCar.LEFT_WHEEL, iCar[0]);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		
						try {
							wifiCar.enableMoveFlag();
							wifiCar.move(WifiCar.RIGHT_WHEEL, iCar[1]);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					if(GsensorCountB > 40 || GsensorCountF > 40){
						gSensorControlEnable = false;
						Log.e("zhang", "zhang zhang zhang:" + GsensorCountF);
						if(iCarSpeedL == iCarSpeedR){  //向前 、向后
							try {
								wifiCar.enableMoveFlag();
								wifiCar.move(WifiCar.LEFT_WHEEL, iCar[0]);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							try {
								wifiCar.enableMoveFlag();
								wifiCar.move(WifiCar.RIGHT_WHEEL, iCar[1]);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
					}
					if(iCarSpeedL == 0 && iCarSpeedR == 0){ //停止
						GsensorCountB = 0;
						GsensorCountF = 0;
						
						//gSensorControlEnable = true;
						try {
							wifiCar.disableMoveFlag();
							wifiCar.move(WifiCar.LEFT_WHEEL,iCarSpeedL);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		
						try {
							wifiCar.disableMoveFlag();
							wifiCar.move(WifiCar.RIGHT_WHEEL, iCarSpeedR);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
};

	final SensorEventListener myMagneticFieldListener = new SensorEventListener() {
		// @Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		// @Override
		public void onSensorChanged(SensorEvent event) {
			fMagneticFieldValues = event.values;
		}
	};

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// Log.d("wificar","onConfigurationChanged:"+orientationLock);
		try {
			if (orientationLock) {

				// return ;
			} else if (!this.gSensorControlEnable) {
				// Log.d("wificar","onConfigurationChanged:enable");
				cameraSurfaceView.destroyDrawingCache();
				// surface.destroyDrawingCache();
				// 20111126: Lock landscape
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onConfigurationChanged(newConfig);
	}

	private void changeLandscape() {
		// Log.d("wificar", "ORIENTATION_LANDSCAPE");

		controllerType = WificarUtility.getIntVariable(instance,
				WificarUtility.CONTROLLER_TYPE, 1);

		// this.controllerType = WificarActivity.SINGLE_CONTROLLER;
		Log.d("wild0", "controllerType:" + controllerType);
		    dimension = getDisplayMetrics(instance);
		    Log.e("zhangzhang", "屏幕的尺寸：" + dimension);
		    if(dimension > 5.8){
		    	setContentView(R.layout.double_axis_landscape_large);
		    }else{
		    	setContentView(R.layout.double_axis_landscape);
		    }
			
			//Log.d("wild0", "double axis");
			leftSurface = (DoubleAxisLeftControllerSurfaceView) findViewById(R.id.stick_double_axis_left_controller_surfaceview);
			rightSurface = (DoubleAxisRightControllerSurfaceView) findViewById(R.id.stick_double_axis_right_controller_surfaceview);
			leftSurface.setWifiCar(wifiCar);
			rightSurface.setWifiCar(wifiCar);
			rightSurface.setZOrderOnTop(true);
			leftSurface.setZOrderOnTop(true);
			cameraSurfaceView = (CameraSurfaceView) findViewById(R.id.car_camera_surfaceview);
			wifiCar.setSurfaceView(cameraSurfaceView);
			cameraSurfaceView.setZOrderOnTop(false);
			
		refreshUIListener();
		
		bIsPortait = false;
	}
	private void refreshUIListener() {
		Button zoomInBtn = (Button) this.findViewById(R.id.zoom_in_button);
		zoomInBtn.setOnClickListener(new View.OnClickListener() {

			// @Override
			public void onClick(View v) {
				if(succeedConnect){
					try {
						cameraSurfaceView.zoomIn();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					int value = (int) cameraSurfaceView.getTargetZoomValue();
					TextView valueText = (TextView) findViewById(R.id.screen_ratio_textview);
					valueText.setText(String.valueOf(value) + "%");
				}
				}
				
		});

		Button zoomOutBtn = (Button) this.findViewById(R.id.zoom_out_button);
		zoomOutBtn.setOnClickListener(new View.OnClickListener() {

			// @Override
			public void onClick(View v) {
				if(succeedConnect){
					try {
						cameraSurfaceView.zoomOut();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					int value = (int) cameraSurfaceView.getTargetZoomValue();
					TextView valueText = (TextView) findViewById(R.id.screen_ratio_textview);
					valueText.setText(String.valueOf(value) + "%");
				}
				}
				
		});

		// ╃化ㄏノ
		/*final Button takePictureBtn = (Button) this.findViewById(R.id.take_picture_button);
		takePictureBtn.setOnClickListener(new View.OnClickListener() {

			// @Override
			public void onClick(View v) {
				if(takePictureBtn.isClickable()){
					sendMessage(MESSAGE_TAKE_PHOTO);
					//bPhotoThreadRun = true;
					//handler.postDelayed(TakePhotoTask, 10);
				}
			}
		});*/
		final Button takePictureBtn = (Button) this.findViewById(R.id.take_picture_button);
		
		takePictureBtn.setOnTouchListener(new View.OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(succeedConnect){
					if(event.getAction() == MotionEvent.ACTION_DOWN){
						
						
						if(dimension > 5.8){
							takePictureBtn.setBackgroundResource(R.drawable.camera_pressed);
						}else {
							takePictureBtn.setBackgroundResource(R.drawable.camera_pressed1);
						}
						//bPhotoThreadRun = true;
						//handler.postDelayed(TakePhotoTask, 10);
						/*if(nSDFreeSize < 50 ){
							checkSDcard();
						}else{*/
						if(No_Sdcard){
							Toast toast = Toast.makeText(instance,
									R.string.record_fail_warning,Toast.LENGTH_SHORT);
							toast.show();
						}else{
							sendMessage(MESSAGE_TAKE_PHOTO);
						}
					
				}
				else if(event.getAction() == MotionEvent.ACTION_UP){
					if(dimension > 5.8){
						takePictureBtn.setBackgroundResource(R.drawable.camera);
					}else {
						takePictureBtn.setBackgroundResource(R.drawable.camera1);
					}
					//takePictureBtn.setBackgroundResource(R.drawable.camera);
				}
				}
				
				return false;
			}
		});
		////////////////////////////////led///////////////////////////
		final ToggleButton ledTogglebutton = (ToggleButton) findViewById(R.id.led_toggle_button);

		ledTogglebutton.setOnClickListener(new View.OnClickListener() {
		//@Override
		public void onClick(View v) {
		// Perform action on clicks
			if(succeedConnect){
				if (ledTogglebutton.isChecked()) {
					
					try {
					//wifiCar.move(WifiCar.LED_ON,0);
						wifiCar.led_onTrack();
					} catch (IOException e) {
					// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					ledTogglebutton.setBackgroundResource(R.drawable.led_on);
					
					} else {
					
						int mode = Context.MODE_PRIVATE;
						SharedPreferences carSharedPreferences = instance
								.getSharedPreferences("WIFICAR_PREFS", mode);
					
					try {
					//wifiCar.move(WifiCar.LED_OFF,0);
						wifiCar.led_offTrack();
					} catch (IOException e) {
					// TODO Auto-generated catch block
						e.printStackTrace();
					}
					ledTogglebutton.setBackgroundResource(R.drawable.led);
					
					}
			}
		
		}
		});
//////////////////////////////////led///////////////////////////////////
		final ImageView soundImg= (ImageView) findViewById(R.id.no_sound);
		final ToggleButton micToggleButton = (ToggleButton) this.findViewById(R.id.mic_toggle_button);
		{
			/*
			 * initial
			 */
			int enableMic = WificarUtility.getIntVariable(instance,
					WificarUtility.WIFICAR_MIC, 0);
			if (enableMic == 1) {
				// boolean result = wifiCar.AudioEnable();
				boolean result = wifiCar.playAudio();
				if (result) {
					micToggleButton.setBackgroundResource(R.drawable.mic_pressed);
					micToggleButton.setChecked(true);
				}
			} else {
				// boolean result = wifiCar.AudioDisable();
				boolean result = false;
				try {
					result = wifiCar.disableAudio();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (result) {
					micToggleButton.setBackgroundResource(R.drawable.mic);
					micToggleButton.setChecked(false);
				}
			}
		}
		micToggleButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on clicks

				if(succeedConnect){
					if (micToggleButton.isChecked()) {
						
						// boolean result = wifiCar.AudioEnable();
						boolean result = wifiCar.playAudio();
						if (result) {
							/*
							 * int mode = Activity.MODE_PRIVATE; SharedPreferences
							 * carSharedPreferences = instance
							 * .getSharedPreferences("WIFICAR_PREFS", mode);
							 * SharedPreferences.Editor carEditor =
							 * carSharedPreferences .edit();
							 * carEditor.putBoolean(WifiCar.PREF_MIC, true);
							 * carEditor.commit();
							 */
							checkSound = new Timer(true);
							checkSound.schedule(new SoundCheck(), 100, 300);
							
							setting = 0;
							audio_play = 1;
							
							if(isZero == 1){
								AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE); 
								mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 2 , 0); //tempVolume:音量绝对值
							}
							WificarUtility.getIntVariable(instance,WificarUtility.WIFICAR_MIC, 1);

							micToggleButton.setBackgroundResource(R.drawable.mic_pressed);
						}

					} else {
						// boolean result = wifiCar.AudioDisable();
						boolean result = false;
						try {
							result = wifiCar.disableAudio();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (result) {
							/*
							 * int mode = Activity.MODE_PRIVATE; SharedPreferences
							 * carSharedPreferences = instance
							 * .getSharedPreferences("WIFICAR_PREFS", mode);
							 * SharedPreferences.Editor carEditor =
							 * carSharedPreferences .edit();
							 * carEditor.putBoolean(WifiCar.PREF_MIC, false);
							 * carEditor.commit();
							 */
							if(checkSound != null){
								checkSound.cancel();
								checkSound = null;
							}
							
							soundImg.setVisibility(View.VISIBLE);
							
							audio_play = 0;
							WificarUtility.getIntVariable(instance,
									WificarUtility.WIFICAR_MIC, 1);

							micToggleButton.setBackgroundResource(R.drawable.mic);
							/*Toast.makeText(instance,
									MessageUtility.MESSAGE_DISABLE_GSENSOR,
									Toast.LENGTH_SHORT);*/
						}
						// Toast.makeText(HelloFormStuff.this, "Not checked",
						// Toast.LENGTH_SHORT).show();
					}
				}
				
			}
		});
		final ToggleButton lightTogglebutton = (ToggleButton) findViewById(R.id.light_toggle_button);
		{
			/*
			 * initial
			 */
			/*
			 * int mode = Activity.MODE_PRIVATE; SharedPreferences
			 * carSharedPreferences = getSharedPreferences(
			 * WifiCar.WIFICAR_PREFS, mode); boolean ir = carSharedPreferences
			 * .getBoolean(WifiCar.PREF_IR, false);
			 */
			int enableIr = WificarUtility.getIntVariable(instance,
					WificarUtility.WIFICAR_IR, 0);
			if (enableIr == 1) {
				try {
					wifiCar.enableIR();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				lightTogglebutton.setBackgroundResource(R.drawable.ir_pressed);
				lightTogglebutton.setChecked(true);
			} else {
				try {
					wifiCar.disableIR();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				lightTogglebutton.setBackgroundResource(R.drawable.ir);
				lightTogglebutton.setChecked(false);
			}
		}

		lightTogglebutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on clicks
				if(succeedConnect){
					if (lightTogglebutton.isChecked()) {
						
						WificarUtility.getIntVariable(instance,
								WificarUtility.WIFICAR_IR, 1);
						
						try {
							wifiCar.led_offTrack();
						} catch (IOException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						
						ledTogglebutton.setBackgroundResource(R.drawable.led);
						ledTogglebutton.setClickable(false);
						ledTogglebutton.setChecked(false);
						
						try {
							wifiCar.enableIR();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						lightTogglebutton.setBackgroundResource(R.drawable.ir_pressed);
						// Toast.makeText(HelloFormStuff.this, "Checked",
						// Toast.LENGTH_SHORT).show();
					} else {
						
						WificarUtility.getIntVariable(instance,
								WificarUtility.WIFICAR_IR, 0);
						
						ledTogglebutton.setBackgroundResource(R.drawable.led);
						ledTogglebutton.setClickable(true);
						ledTogglebutton.setChecked(false);
						
						try {
							wifiCar.disableIR();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						lightTogglebutton.setBackgroundResource(R.drawable.ir);
						// Toast.makeText(HelloFormStuff.this, "Not checked",
						// Toast.LENGTH_SHORT).show();
					}
				}
				
			}
		});

		final ToggleButton gSensorTogglebutton = (ToggleButton) findViewById(R.id.g_sensor_toggle_button);
		gSensorTogglebutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on clicks
				// Log.d("wild0", "gsensor on");
				if(succeedConnect){
					if (gSensorTogglebutton.isChecked()) {
						// enableGSensorControl();
						if(isPlayModeEnable){
							disablePlayMode();
						}
						//hide();
						gSensorTogglebutton.setBackgroundResource(R.drawable.g_pressed);
						enableGSensorControl();
						
					} else {
						
						gSensorTogglebutton.setBackgroundResource(R.drawable.g);
						disableGSensorControl();
					}
				}
				
			}
		});
		final Button playTogglebutton = (Button) findViewById(R.id.play_toggle_button);
		final ToggleButton recordTogglebutton = (ToggleButton) findViewById(R.id.record_toggle_button);
		//final ToggleButton replayTogglebutton = (ToggleButton) findViewById(R.id.replay_toggle_button);
		
		recordTogglebutton.setOnClickListener(new View.OnClickListener() {
			//	@Override
				public void onClick(View v) {
					if(succeedConnect){
						if (recordTogglebutton.isChecked()) {
							
							if(isPlayModeEnable){
								disablePlayMode();
							}
							if(isLowPower){ //当手机出现电量低的提示的时候，禁止录制
								Disrecord_play_dialog.createdisaenableDialog(instance).show();
								recordTogglebutton.setChecked(false);
							}else{
								Message messageStopRecord = new Message();
								messageStopRecord.what = WificarActivity.MESSAGE_START_RECORD;
								WificarActivity.getInstance().getHandler().sendMessage(messageStopRecord);
							}
						} else {

							Message messageStopRecord = new Message();
							messageStopRecord.what = WificarActivity.MESSAGE_STOP_RECORD;
							WificarActivity.getInstance().getHandler()
									.sendMessage(messageStopRecord);
						}
					}
					
				}
			});
		//final Button playTogglebutton =  (Button) findViewById(R.id.play_toggle_button);
		final GestureDetector detector = new GestureDetector(new OnGestureListener() {
					
					public boolean onSingleTapUp(MotionEvent e) {
						//Log.e("aaa", "s");
						
						return false;
					}
					
					public void onShowPress(MotionEvent e) {
					}
					
					public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
							float distanceY) {
						return false;
					}
					
					public void onLongPress(MotionEvent e) {
					}
					
					public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
							float velocityY) {
						return false;
					}
					
					public boolean onDown(MotionEvent e) {
						return false;
					}
				});
			
		        detector.setOnDoubleTapListener(new OnDoubleTapListener() {
					
					public boolean onSingleTapConfirmed(MotionEvent e) {
						if(isLowPower){ //当手机电量低的时候，禁止播放
							
							Disrecord_play_dialog.createdisaenableDialog(instance).show();
						}else{
						if(succeedConnect){
							if(flag == 0){
								//单击单次播放
								isPlayModeEnable = true;
								//wifiCar.moveCommand(WifiCar.GO_DIRECTION.TrackPlay, 1);
								try {
									wifiCar.startPlayTrack();
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								 playTogglebutton.setBackgroundResource(R.drawable.play_path_pressed);
								
								 //recordTogglebutton.setClickable(false);
								 //replayTogglebutton.setClickable(false);
								 
								 SharedPreferences share_time_play_s = getSharedPreferences(RECORD_TIME, 0);
								 recordTimeLength1 = share_time_play_s.getLong("record",  recordTimeLength);
								 

								 Log.e("aaa","recordTimeLength: "+ recordTimeLength1);
								 playTimer = new Timer(true); 
								 playTimer.schedule(new PlayTask(), recordTimeLength1);
								 if(isgSensorModeEnable()){
									 disablegSensorMode();
								 }
								Log.e("aaa", "s");
								flag =1;
							}
							
							else if(flag !=0){
								//结束播放，返回初始状态
								//wifiCar.moveCommand(WifiCar.GO_DIRECTION.TrackPlay, 0);
								try {
									wifiCar.stopPlayTrack();
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								Message messageStopPlay = new Message();
								messageStopPlay.what = WificarActivity.MESSAGE_STOP_PLAY;
								WificarActivity.getInstance().getHandler()
										.sendMessage(messageStopPlay);
								Log.e("aaa", "sf");
								flag = 0;
								//Toast.makeText(instance, R.string.complete_play, Toast.LENGTH_SHORT).show();
							}
							else if(flag == 2){
								//wifiCar.moveCommand(WifiCar.GO_DIRECTION.TrackPlay, 0);
								Message messageStopPlay = new Message();
								messageStopPlay.what = WificarActivity.MESSAGE_STOP_PLAY;
								WificarActivity.getInstance().getHandler()
										.sendMessage(messageStopPlay);
								Log.e("aaa", "df");
								flag = 0;
								//Toast.makeText(instance, R.string.complete_play, Toast.LENGTH_SHORT).show();
							}
						}
						
					}//结束电量检测
						//Log.e("aaa", "s");
						return false;
					}
					
					public boolean onDoubleTapEvent(MotionEvent e) {
						
						return false;
					}
					
					public boolean onDoubleTap(MotionEvent e) {
						if(isLowPower){ //当手机电量低的时候，禁止播放
							Disrecord_play_dialog.createdisaenableDialog(instance).show();
						}else{
						if(isgSensorModeEnable()){
							 disablegSensorMode();
						 }
						if(succeedConnect){
							if(flag != 2 && flag != 1){
								//isPlayModeEnable = true;
								Message messageStartRePlay = new Message();
								messageStartRePlay.what = WificarActivity.MESSAGE_START_PLAY;
								WificarActivity.getInstance().getHandler()
										.sendMessage(messageStartRePlay);
								Log.e("aaa", "d");
								flag = 2;
								}
						}
						
						}//结束电量检测
						return false;
					}
				});
		       // Button button =  (Button) findViewById(R.id.button);

		        playTogglebutton.setOnTouchListener(new OnTouchListener() {
				
				public boolean onTouch(View v, MotionEvent event) {

				return detector.onTouchEvent(event);
					
				}
			});	

	final Button recordAudioTogglebutton = (Button) findViewById(R.id.talk_button);
	
		recordAudioTogglebutton.setOnTouchListener(new View.OnTouchListener() {
			
			
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(succeedConnect){
					if(event.getAction() == MotionEvent.ACTION_DOWN){
						//if(succeedConnect){
							soundImg.setVisibility(0);
							recordAudioTogglebutton.setBackgroundResource(R.drawable.talk_pressed);
							instance.sendMessage(MESSAGE_START_RECORD_AUDIO);
							isTalk = 1;
							if(audio_play == 1){ // audio =1 则为声音已经打开
								disablegSpeaking();
								micToggleButton.setBackgroundResource(R.drawable.mic);
								micToggleButton.setChecked(false);
							}
						//}
						
					}
					else if(event.getAction() == MotionEvent.ACTION_UP){
						//recordAudioTogglebutton.setBackgroundResource(R.drawable.talk);
						//if(succeedConnect){
							isTalk = 0;
							stop_talk = new Timer(true);
							stop_talk.schedule(new TimerTask() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									instance.sendMessage(MESSAGE_STOP_RECORD_AUDIO);
								}
							}, 600);
							
							if(audio_play == 1){  // audio =1 则为声音已经打开
								soundImg.setVisibility(8);
							}
						}
					//}
				}
				
				
				
				return false;
			}
		});
	
	///////////////////////////
	//在控制时按分享按钮弹出的对话框
	final Button share_pressed = (Button) findViewById(R.id.share_toggle_button);
	share_pressed.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(succeedConnect){
				//Control_share_dialog.createcontrolsharedialog(instance).show();
				AlertDialog.Builder builder = new AlertDialog.Builder(WificarActivity.this);  
			    builder.setMessage("The Facebook, Twitter, Tumblr, and YouTube apps must already be installed on your device to share CAR 2.0 photos and videos.\n" +
						"Exit the app, go to Settings and access a Wi-Fi network other than CAR 2.0. Open the CAR 2.0 app and select Share.").setCancelable(false)
			    .setPositiveButton("OK",new DialogInterface.OnClickListener() {		                         
			       public void onClick(DialogInterface dialog, int which) {
			     // TODO Auto-generated method stub
			    	   dialog.cancel();
			      }
			     } ).create().show();  
			}
		}
	});
	//////////////////////////
////////////////////////////////camera up and down////////////////////////////////////
		//camerasettingSurface = (CamerSettingSurfaceView) findViewById(R.id.camera_setting_surfaceview);
		final ToggleButton buttoncameraupon = (ToggleButton) findViewById(R.id.camup_button);
		final RelativeLayout camerRelativelayout = (RelativeLayout) findViewById(R.id.linearlayoutleftcamera);
		final Button cameraStick = (Button) findViewById(R.id.camera_stick);
		
		buttoncameraupon.setOnClickListener(new View.OnClickListener() {
		
		public void onClick(View v) {
		// TODO Auto-generated method stub
			if(succeedConnect){
				if(buttoncameraupon.isChecked()){    
					buttoncameraupon.setBackgroundResource(R.drawable.up_on);
					camerRelativelayout.setVisibility(0);
					cameraStick.setVisibility(0);
					camerRelativelayout.setBackgroundResource(R.drawable.back);
					cameraStick.setBackgroundResource(R.drawable.stick_back);
			
				}
				else {
					buttoncameraupon.setBackgroundResource(R.drawable.up);
					camerRelativelayout.setVisibility(4);
					}
			}
			
			}
		
		});
		if(dimension > 5.8){
			cameraStick.setOnTouchListener(new View.OnTouchListener() {
				int lastX, lastY; 
				public boolean onTouch(View v, MotionEvent event) {
					//TODO Auto-generated method stub

					switch (event.getAction()) {  
					case MotionEvent.ACTION_DOWN:  

						controlEnable = true;
						lastX = (int) event.getRawX();  
						lastY = (int) event.getRawY();  
						
						int dx1 = (int) event.getRawX() - lastX;  
						int dy1 = (int) event.getRawY() - lastY;  

						int left1 = v.getLeft() + dx1;  
						int top1 = v.getTop() + dy1;  
						int right1 = v.getRight() + dx1;  
						int bottom1 = v.getBottom() + dy1;  
						
						v.layout(left1, top1, right1, bottom1); 
						Log.e("camera---", left1 + "," + top1 + "," + right1 + "," + bottom1);
						Log.e(TAG, "dx1:"+dx1+","+"dy1:"+dy1);
						break;  
//					case MotionEvent.ACTION_MOVE:  
//						
//						int dx = (int) event.getRawX() - lastX;  
//						int dy = (int) event.getRawY() - lastY;  
//
//						int left = v.getLeft() + dx;  
//						int top = v.getTop() + dy;  
//						int right = v.getRight() + dx;  
//						int bottom = v.getBottom() + dy;  
//
//						Log.e("camera---", left + "," + top + "," + right + "," + bottom);
//						Log.e(TAG, "dx:"+dx+","+"dy:"+dy);
//						
//						if (left < 1) {  
//							left = 1;  
//							right = left + v.getWidth();  
//						}  
//						
//
//						
//						if (top < 0) {  
//							top = 0;  
//							bottom = top + v.getHeight();  
//						}  
//
//						if(with > 850){
//							if (right > 28) {  
//								right = 28;  
//								left = 1;  
//							}  
//							if (bottom > 117) {  
//								bottom = 117;  
//								top = 89;  
//							}  
//		
//							//right forware
//							if(bottom < 65){//摄像头向上
//								b = 0;
//								f ++;
//								Log.e("zhangdddd__11111 > 850", " the f is ---: " + f);
//								if( f == 1){
//								cameramove = 1;
//								try {
//									wifiCar.enableMoveFlag();
//									wifiCar.cameraup();
//								} catch (IOException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
//								}
//								
//							}
//
//							// right back
//							if(bottom > 85 ){//向下
//								f = 0;
//								b ++;
//								Log.e("zhangdddd__11111 > 850", " the b is ---: " + b);
//								if(b == 1){
//								cameramove = 2;
//								try {
//									wifiCar.enableMoveFlag();
//									wifiCar.cameradown();
//								} catch (IOException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
//								}
//								
//							}
//						}else{
//							if (right > 22) {  
//								right = 22;  
//								left = 1;  
//							}  
//							if (bottom > 90) {  
//								bottom = 90;  
//								top = 69;  
//							}  
//		
//							//right forware
//							if(bottom < 45){
//								b = 0;
//								f ++;
//								Log.e("zhangdddd__11111", " the f is ---: " + f);
//								if( f == 1){
//								cameramove = 1;
//								try {
//									wifiCar.enableMoveFlag();
//									wifiCar.cameraup();
//								} catch (IOException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
//								}
//								
//							}
//
//							// right back
//							if(bottom > 65 ){
//								f = 0;
//								b ++;
//								Log.e("zhangdddd__11111", " the b is--- : " + b);
//								if(b == 1){
//								cameramove = 2;
//								try {
//									wifiCar.enableMoveFlag();
//									wifiCar.cameradown();
//								} catch (IOException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//									}
//								}
//								
//							}
//						}
//						 
//						v.layout(1, top, right, bottom);  
//
//						lastX = (int) event.getRawX();  
//						lastY = (int) event.getRawY();  
//
//						break;  
//					case MotionEvent.ACTION_UP: 
//						f = 0;
//						b = 0;
//						cameramove = 0;
//						try {
//							wifiCar.disableMoveFlag();
//							wifiCar.camerastop();
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						if(with > 850){
//							v.layout(1, 47, 28, 75);  
//						}else{
//							v.layout(1, 34, 22, 55);  
//						}
//						
//						break;  
////------------------------------------------------------------------------------						
					case MotionEvent.ACTION_MOVE:  
						
						int dx = (int) event.getRawX() - lastX;  
						int dy = (int) event.getRawY() - lastY;  

						int left = v.getLeft() + dx;  
						int top = v.getTop() + dy;  
						int right = v.getRight() + dx;  
						int bottom = v.getBottom() + dy;  

						Log.e("camera---", left + "," + top + "," + right + "," + bottom);

						
						if (left < dip2px(getInstance(), 1)) {  
							left = dip2px(getInstance(), 1);  
							right = left + v.getWidth();  
						}  

						if (top < 0) {  
							top = 0;  
							bottom = top + v.getHeight();  
						}  

						if(with > 850){
							if (right > dip2px(getInstance(), 30)) {  
								right = dip2px(getInstance(), 30);  
								left = dip2px(getInstance(), 1);  
							}  
							if (bottom > dip2px(getInstance(), 118)) {  
								bottom = dip2px(getInstance(), 118);  
								top = dip2px(getInstance(), 90);  
							}  
		
							//right forware
							if(bottom < dip2px(getInstance(), 55)){
								b = 0;
								f ++;
								Log.e("zhangdddd__11111 > 850", " the f is ---: " + f);
								if( f == 1){
								cameramove = 1;
								try {
									wifiCar.enableMoveFlag();
									wifiCar.cameraup();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								}
								
							}

							// right back
							if(bottom > dip2px(getInstance(), 83) ){
								f = 0;
								b ++;
								Log.e("zhangdddd__11111 > 850", " the b is ---: " + b);
								if(b == 1){
								cameramove = 2;
								try {
									wifiCar.enableMoveFlag();
									wifiCar.cameradown();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								}
								
							}
						}else{
							if (right > dip2px(getInstance(), 30)) {  
								right = dip2px(getInstance(), 30);  
								left = dip2px(getInstance(), 1);  
							}  
							if (bottom > dip2px(getInstance(), 60)) {  
								bottom = dip2px(getInstance(), 60);  
								top = dip2px(getInstance(), 70);  
							}  
		
							//right forware
							if(bottom < dip2px(getInstance(), 40)){
								b = 0;
								f ++;
								Log.e("zhangdddd__11111", " the f is ---: " + f);
								if( f == 1){
								cameramove = 1;
								try {
									wifiCar.enableMoveFlag();
									wifiCar.cameraup();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								}
								
							}

							// right back
							if(bottom > dip2px(getInstance(), 40) ){
								f = 0;
								b ++;
								Log.e("zhangdddd__11111", " the b is--- : " + b);
								if(b == 1){
								cameramove = 2;
								try {
									wifiCar.enableMoveFlag();
									wifiCar.cameradown();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									}
								}
								
							}
						}
						 
						v.layout(1, top, right, bottom);  

						lastX = (int) event.getRawX();  
						lastY = (int) event.getRawY();  

						break;  
					case MotionEvent.ACTION_UP: 
						f = 0;
						b = 0;
						cameramove = 0;
						try {
							wifiCar.disableMoveFlag();
							wifiCar.camerastop();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if(with > 850){
							v.layout(dip2px(getInstance(), 0), dip2px(getInstance(), 55), dip2px(getInstance(), 30), dip2px(getInstance(), 83));  
						}else{
							v.layout(1, 34, 22, 55);  
						}
						
						break;  
					}  
					return false;
				}
			});
		}else{
			cameraStick.setOnTouchListener(new View.OnTouchListener() {
				int lastX, lastY; 
				public boolean onTouch(View v, MotionEvent event) {
					//TODO Auto-generated method stub

					switch (event.getAction()) {  
					case MotionEvent.ACTION_DOWN:  

						//controlEnable = true;
						lastX = (int) event.getRawX();  
						lastY = (int) event.getRawY();  
						
						break;  
					case MotionEvent.ACTION_MOVE:  
						
						int dx = (int) event.getRawX() - lastX;  
						int dy = (int) event.getRawY() - lastY;  

						int left = v.getLeft() + dx;  
						int top = v.getTop() + dy;  
						int right = v.getRight() + dx;  
						int bottom = v.getBottom() + dy;  

						Log.e("camera", left + "," + top + "," + right + "," + bottom);

						if (left < 1) {  
							left = 1;  
							right = left + v.getWidth();  
						}  

						 
						if (top < 0) {  
							top = 0;  
							bottom = top + v.getHeight();  
						}  
						if(with <= 480 ){

							if (right > 29) {  
								right = 29;  
								left = 1;  
							}  
							if (bottom > 119) {  
								bottom = 119;  
								top = 91;  
							} 
							
							//right forware
							if(bottom < 67){
								b = 0;
								f ++;
								Log.e("zhangdddd__11111", " the f is : " + f);
								if( f == 1){
								try {
									wifiCar.enableMoveFlag();
									wifiCar.cameraup();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								}
								
							}

							// right back
							if(bottom > 87 ){
								f = 0;
								b ++;
								Log.e("zhangdddd__11111", " the b is : " + b);
								if(b == 1){
								try {
									wifiCar.enableMoveFlag();
									wifiCar.cameradown();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								}
								
							}
						
						}else{
							if(with > 1100){

								if (right > dip2px(getInstance(), 29)) {  
								right = dip2px(getInstance(), 29);  
								left =dip2px(getInstance(), 1);  
								}  

								if (bottom > dip2px(getInstance(), 117)) {  
									bottom = dip2px(getInstance(), 117);  
									top = dip2px(getInstance(), 90);  
								} 
								//right forware
								if(bottom < dip2px(getInstance(), 45)){
									b = 0;
									f ++;
									Log.e("zhangdddd__11111", " the f is : " + f);
									if( f == 1){
										try {
											wifiCar.enableMoveFlag();
											wifiCar.cameraup();
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
								
								}

								// right back
								if(bottom > dip2px(getInstance(), 72) ){
									f = 0;
									b ++;
									Log.e("zhangdddd__11111", " the b is : " + b);
									if(b == 1){
										try {
											wifiCar.enableMoveFlag();
											wifiCar.cameradown();
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
								
								}
							
							}else{
								if (right > 43) {  
								right = 43;  
								left = 1;   
							}  

							if (bottom > 177) {  
								bottom = 177;  
								top = 135;  
							} 
							//right forware
							if(bottom < 100){
								b = 0;
								f ++;
								Log.e("zhangdddd__11111", " the f is : " + f);
								if( f == 1){
								try {
									wifiCar.enableMoveFlag();
									wifiCar.cameraup();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								}
								
							}

							// right back
							if(bottom > 124 ){
								f = 0;
								b ++;
								Log.e("zhangdddd__11111", " the b is : " + b);
								if(b == 1){
								try {
									wifiCar.enableMoveFlag();
									wifiCar.cameradown();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								}
								
								}
							}
							
						}
						
						v.layout(1, top, right, bottom);  

						lastX = (int) event.getRawX();  
						lastY = (int) event.getRawY();  

						break;  
					case MotionEvent.ACTION_UP: 
						f = 0;
						b = 0;
						try {
							wifiCar.disableMoveFlag();
							wifiCar.camerastop();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if(with <= 480){
							v.layout(1, 49, 29, 77);  
						}else{
							if(with > 1100){
								v.layout(dip2px(getInstance(), 1), dip2px(getInstance(), 45), dip2px(getInstance(), 29), dip2px(getInstance(),72)); 
							}
								
							else
								v.layout(1, 70, 43, 112); 
						}
						
						break;  
					}  
					return false;
				}
			});
		}
/////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////Setting 04.09/////////////////////////
		final ToggleButton micbutton = (ToggleButton) findViewById(R.id.mic_toggle_button);
		final ToggleButton settingbutton = (ToggleButton) findViewById(R.id.setting_button);
		settingbutton.setOnTouchListener(new View.OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					settingbutton.setBackgroundResource(R.drawable.setting_on);
					setDialog();
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					settingbutton.setBackgroundResource(R.drawable.setting);
				}
				return false;
			}
		});
	/*	settingbutton.setOnClickListener(new View.OnClickListener() {
		
			//@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(succeedConnect){
					if(settingbutton.isChecked()){
						//sendMessage(MESSAGE_SETTING);
						settingbutton.setBackgroundResource(R.drawable.setting_on);
						setDialog();
						//进入setting时如果声音打开着就继续开着
					if(audio_play == 1){
							//setting_play();
						}
						setting = 1;
						isNotExit = true;
						sendMessage(MESSAGE_MAIN_SETTING);
						Intent settingIntent = new Intent(WificarActivity.this, SettingActivity.class);
						settingIntent.putExtra("IP", IP);
						settingIntent.putExtra("Port", Port);
						settingIntent.putExtra("version", version);
						settingIntent.putExtra("firmwareVersion", firmwareVersion);
						settingIntent.putExtra("SSID", SSID);
						//intent.setClass(WificarActivity.this, SettingActivity1.class);
						startActivityForResult(settingIntent, 1);
						////////////03.16退出时关闭录影//////////////
						if(videoRecordEnable){
							try {
								closeVideoStream();
								//Toast.makeText(instance,
								//	R.string.wificar_activity_toast_stop_recording,Toast.LENGTH_SHORT).show();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}	
						}
						////////////03.16退出时关闭录影//////////////
					}
				}
			}
		});*/
////////////////////////////////Setting 04.09////////////////////////
////////////////////////////////video////////////////////////////////////	
		final ToggleButton videoTogglebutton = (ToggleButton) findViewById(R.id.video_toggle_button);
		videoTogglebutton.setOnClickListener(new View.OnClickListener() {
		
		private Object cameraSurfaceView;
		
		//@Override
		public void onClick(View v) {
		// TODO Auto-generated method stub
			if(succeedConnect){
				if(videoTogglebutton.isChecked() ){
					
					checkTimer = new Timer(true);
					checkTimer.schedule(new SDCardSizeTest(), 50, 500);
					
					if(isLowPower){ //当手机电量低于10%的时候，禁止录制视频
						Disrecordvideo_dialog.createdisaenablevideoDialog(instance).show();
						videoTogglebutton.setChecked(false);
						
					}else{
					
					if(connect_error){
						if(dimension > 5.8){
							videoTogglebutton.setBackgroundResource(R.drawable.video_on);
						}else {
							videoTogglebutton.setBackgroundResource(R.drawable.video_on1);
						}
						//recordAudioTogglebutton.setEnabled(true);
					}else{
					
						recordAudioTogglebutton.setEnabled(false);
						recordAudioTogglebutton.setBackgroundResource(R.drawable.talk);
						
					//Date date = new Date(System.currentTimeMillis());
						SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");//锟斤拷锟斤拷锟斤拷锟节革拷式
						String filename = date.format(System.currentTimeMillis());
			
						FileName = filename +".avi";
					
					//String FileName =String.valueOf(System.currentTimeMillis() +".avi");
					if(No_Sdcard){
						NoSDcard();
						Toast toast = Toast.makeText(instance,
								R.string.record_fail_warning,Toast.LENGTH_SHORT);
						//toast.setGravity(Gravity.CENTER, 0, 0);   
						toast.show();
						recordAudioTogglebutton.setEnabled(true);
						}else{
							if(nSDFreeSize < 100 ){
								//video();
								//Log.e("aaa", "nSDFreeSize111111  = " + nSDFreeSize);
								sdcheck = 2;
								Message sdcardtest = new Message();
								sdcardtest.what = WificarActivity.MESSAGE_CHECK_TEST;
								WificarActivity.getInstance().getHandler().sendMessage(sdcardtest);
								recordAudioTogglebutton.setEnabled(true);
							}else{
								sdcheck = 1;
								startVideo = true;
								handler.postDelayed(StartVideoTask, 10);
								/*try {
									openVideoStream(FileName);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}*/
							}
							if(dimension > 5.8 ){
								videoTogglebutton.setBackgroundResource(R.drawable.video_on);
							}else {
								videoTogglebutton.setBackgroundResource(R.drawable.video_on1);
							}
					
							if(take_flag == 1){
						}
						}
					 }//结束判断是否连接
					}//结束判断电量低于10%
					
					}
					else {
					/*if(connect_error){
						if(dimension > 5.8){
							videoTogglebutton.setBackgroundResource(R.drawable.video);
						}else {
							videoTogglebutton.setBackgroundResource(R.drawable.video1);
						}
					}else{*/
						if(checkTimer != null){
							checkTimer.cancel();
							checkTimer = null;
						}
						if(dimension > 5.8 ){
							videoTogglebutton.setBackgroundResource(R.drawable.video);
						}else {
							videoTogglebutton.setBackgroundResource(R.drawable.video1);
						}
						
						recordAudioTogglebutton.setEnabled(true);
						recordAudioTogglebutton.setBackgroundResource(R.drawable.talk);
						stopVideo = true;
						handler.postDelayed(StopVideoTask, 10);
					//videoTogglebutton.setBackgroundResource(R.drawable.video);
					
					}
			}
		
		}
		//}
		});
////////////////////////////////video////////////////////////////////////		
	}

	/*
	 *
		// TODO Auto-generated method stub
			if(ConnectSucceed){
				if(videoTogglebutton.isChecked() ){
					
					checkTimer = new Timer(true);
					checkTimer.schedule(new SDCardSizeTest(), 50, 1000);
					if(isLowPower){
						Disrecordvideo_dialog.createdisaenablevideoDialog(instance).show();
						videoTogglebutton.setChecked(false);
					}else{
						if(connect_error){
							videoTogglebutton.setBackgroundResource(R.drawable.video_on);
						}else{
				
							//Date date = new Date(System.currentTimeMillis());
							SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");//锟斤拷锟斤拷锟斤拷锟节革拷式
							String filename = date.format(System.currentTimeMillis());
				
							FileName = "V" + filename +".avi";
							//String FileName =String.valueOf(System.currentTimeMillis() +".avi");
							if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
								NoSDcard();
								Toast.makeText(instance,
										R.string.take_video_fail,Toast.LENGTH_SHORT).show();
							}else{
								if(nSDFreeSize < 50 ){
									//video();
									//Log.e("aaa", "nSDFreeSize111111  = " + nSDFreeSize);
									sdcheck = 2;
									Message sdcardtest = new Message();
									sdcardtest.what = WificarActivity.MESSAGE_CHECK_TEST;
									WificarActivity.getInstance().getHandler().sendMessage(sdcardtest);
								}else{
									talkBtn.setEnabled(false);
									talkBtn.setBackgroundResource(R.drawable.call);
									sdcheck = 1;
									startVideo = true;
									handler.postDelayed(StartVideoTask, 10);
								}
								videoTogglebutton.setBackgroundResource(R.drawable.video_on);
								video_pressed = 1;
								if(take_flag == 1){
					
									video_record_stop = 1;
									video_play = 1;
									take_v = Integer.toString(take_video_T);
									// //Log.e("zhang", "take_v :" + take_v);
									take_video_T ++;
									record_video_times = new long [60];
									record_video_times[take_video_T] = System.currentTimeMillis() - startRecordTimeStamp;
					    
									////Log.e("zhang", "filename[] :" + record_times[take_video_T]);
						
									SharedPreferences share_v0 = getSharedPreferences(FILENAME_V, 0);
									SharedPreferences.Editor edit_v0 = share_v0.edit();
						
									edit_v0.putLong(take_v, record_video_times[take_video_T]);
									edit_v0.putInt("record_video", take_video_T);
									edit_v0.putInt("video_play", video_play);
					
									////Log.e("zhang", "take_v :" + take_v);
									edit_v0.commit();
								}
				
				
							}
						}
					}
					
				}
				else {
					if(connect_error){
						videoTogglebutton.setBackgroundResource(R.drawable.video);
					}else{
				
						talkBtn.setEnabled(true);
						talkBtn.setBackgroundResource(R.drawable.call);
				
						videoTogglebutton.setBackgroundResource(R.drawable.video);
			
						if(video_play == 1){
							//stop_press_video();
							
							//Log.e("zhang", "take_video_T_S :" + take_video_T_S);
							video_play_stop = 1;

							stop_take_v = Integer.toString(take_video_T_S);
							//Log.e("zhang", "stop_take_v :" + stop_take_v);
							take_video_T_S ++;
							stop_video_times = new long [60];
				    
							stop_video_times[take_video_T_S] = System.currentTimeMillis() - startRecordTimeStamp;

							//Log.e("zhang", "stop_video_times :" + stop_video_times[take_video_T_S]);
					
							SharedPreferences share_s0 = getSharedPreferences(FILENAME_S, MODE_PRIVATE);
							SharedPreferences.Editor edit_s0 = share_s0.edit();
					
							edit_s0.putLong(stop_take_v, stop_video_times[take_video_T_S]);
							edit_s0.putInt("stop_video", take_video_T_S);
							edit_s0.putInt("video_play_stop", video_play_stop);
							//Log.e("zhang", "stop_take_v :" + stop_take_v);
							edit_s0.commit();
							video_pressed = 0;
				
						}
			
						videoRecordEnable = false;
						stopVideo = true;
						handler.postDelayed(StopVideoTask, 10);
			
					}
				}
			}
			 
	 */
	
	public boolean isPortait() {
		return this.bIsPortait;
	}

	@Override
	public void onBackPressed() {
		String statement = this.getResources().getString(
				R.string.click_again_to_exit_the_program);
		long pressTime = System.currentTimeMillis();

		if (pressTime - lastPressTime <= DOUBLE_PRESS_INTERVAL) {
			//结束时扫描SDcard
			
			if(!No_Sdcard)
				DeleVideo();
				deleIndexVideo(); //删除*.index文件
//			this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file:" + DirectPath ))); 	
			//sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,Uri.parse("file://" + Environment.getExternalStorageDirectory())));
			MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/" + DirectPath}, null, null);
			pause();
			exit();
			try {
				wifiCar.stopPlayTrack();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 disableGSensorControl();
			// wifiCar.disconnect();
			// finish();
		} else {
			Toast toast =Toast.makeText(this, statement, Toast.LENGTH_SHORT);
			//toast.setGravity(Gravity.CENTER, 0, 0);   
			toast.show();   
		}
		lastPressTime = pressTime;

	}
	public void setDialog() {
		
		//新建自己风格的dialog
		final ToggleButton settingbutton = (ToggleButton) findViewById(R.id.setting_button);
		final Dialog dialog = new Dialog(instance,R.style.my_dialog);
		//绑定布局
		dialog.getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,      
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
		dialog.setContentView(R.layout.setting_info);
		Okbutton = (Button) dialog.findViewById(R.id.OkButton);
		tIP = (EditText)  dialog.findViewById(R.id.EditText_IP);
		tPort = (EditText) dialog.findViewById(R.id.EditText_PORT);
		tdevice = (TextView) dialog.findViewById(R.id.TextView_D);
		tfirmware = (TextView) dialog.findViewById(R.id.TextView_F);
		tsoftware = (TextView) dialog.findViewById(R.id.TextView_S);
		
		//设置背景、监听
		Okbutton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Okbutton.setBackgroundResource(R.drawable.ok_off);
				dialog.dismiss();
				//settingbutton.setBackgroundResource(R.drawable.setting);
				//settingbutton.setChecked(false);
			}
		});
		dialog.show();
		
		tIP.setText(IP);
		tIP.setClickable(false);
		tPort.setText(Port);
		tPort.setClickable(false);
		tdevice.setText(SSID);
		tfirmware.setText(firmwareVersion);
		tsoftware.setText(version);
	}
	/*
	 * @Override public void onAttachedToWindow() {
	 * this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
	 * super.onAttachedToWindow(); }
	 */
	
/*	//播放拍照的动作	
	class take_pictrue extends TimerTask{
		public void run(){
			if(isPlayModeEnable){
			 Message messagePlay = new Message();
				messagePlay.what = WificarActivity.MESSAGE_PLAY_PICTRUE;
				WificarActivity.getInstance().getHandler()
						.sendMessage(messagePlay);
			}
				isplay_pictrue = 1;
			 
		}
	}
	//播放录制视频的动作
	class take_video extends TimerTask{
		public void run(){
			 Message messagePlayvideo = new Message();
				messagePlayvideo.what = WificarActivity.MESSAGE_PLAY_VIDEO;
				WificarActivity.getInstance().getHandler()
						.sendMessage(messagePlayvideo);		
			 isplay_video = 1;
		}
	}


	class stop_take_video extends TimerTask{
		public void run(){
			 Message messagestopvideo = new Message();
				messagestopvideo.what = WificarActivity.MESSAGE_STOP_VIDEO;
				WificarActivity.getInstance().getHandler()
						.sendMessage(messagestopvideo);
				
			 
		}
	}*/
	
	class RecordTask extends TimerTask {
		public void run() {

			recordTimeLength = startRecordTimeStamp
					- System.currentTimeMillis();
			Message messageStopRecord = new Message();
			messageStopRecord.what = instance.MESSAGE_STOP_RECORD;
			WificarActivity.getInstance().getHandler()
					.sendMessage(messageStopRecord);
			/*
			 * dsad ToggleButton recordTogglebutton =(ToggleButton)
			 * findViewById(R.id.record_toggle_button); ToggleButton
			 * playTogglebutton =(ToggleButton)
			 * findViewById(R.id.play_toggle_button);
			 * //recordTogglebutton.setSelected(false);dad
			 * recordTogglebutton.setBackgroundResource(R.drawable.record_path);
			 * recordTogglebutton.setTextColor(Color.WHITE);
			 * playTogglebutton.setClickable(true);
			 */
		}
	}

	class PlayTask extends TimerTask {
		public void run() {
			Message messageStopPlay = new Message();
			messageStopPlay.what = instance.MESSAGE_STOP_PLAY;
			WificarActivity.getInstance().getHandler()
					.sendMessage(messageStopPlay);
		}
	}
	class rePlayTask extends TimerTask {
		@Override
		public void run() {
			replay_flag = 1;
			replay_start = System.currentTimeMillis();
			
			Message messageStartRePlay = new Message();
			messageStartRePlay.what = WificarActivity.MESSAGE_START_PLAY;
			WificarActivity.getInstance().getHandler().sendMessage(messageStartRePlay);
			j = 0;
			k = 0;
			s = 0;
			Log.e("aaa", "replay");
		}
	}
	long recordStartTime = 0;
	long recordTime = 0;
	public void setRecordStartTime(){
		recordTime = 0;
		recordStartTime = System.currentTimeMillis();
	}
	public void setRecordEndTime(){
		recordTime = System.currentTimeMillis() - recordStartTime;
		if(recordTime>20000){
			recordTime = 20000;
		}
		recordStartTime = 0;
	}
	public long getRecordTime(){
		return recordTime;
	}

	public void convertBitmapToJPG(Bitmap bitmap) {
		/*
		 * File rootsd = Environment.getExternalStorageDirectory(); File dcim =
		 * new File(rootsd.getAbsolutePath() + "/DCIM"); File file = new
		 * File(dcim.getAbsolutePath(),
		 * String.valueOf(System.currentTimeMillis())+".jpg");
		 * 
		 * OutputStream fOut = new FileOutputStream(file);
		 * 
		 * bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut); fOut.flush();
		 * fOut.close();
		 * 
		 * MediaStore.Images.Media.insertImage(getContentResolver(),file.
		 * getAbsolutePath(),file.getName(),file.getName());
		 */
		
		// Insert image to media store
		String szUrl = MediaStore.Images.Media.insertImage(
				getContentResolver(), bitmap, System.currentTimeMillis()
						+ ".jpg", System.currentTimeMillis() + ".jpg");
		Log.e("zhahg", "szUrl :" + szUrl);
		// Get real path for the inserted image
		try {
			Uri uri = Uri.parse(szUrl);
			String[] proj = { MediaStore.Images.Media.DATA };
			Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
			int actual_image_column_index = actualimagecursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			actualimagecursor.moveToFirst();
			String img_path = actualimagecursor
					.getString(actual_image_column_index);

			uri = Uri.parse("file://" + img_path);
			Log.e("zhang", "uri :" + uri);
			Log.e("zhang", "img_path :" + img_path);
			// Scan the file to update media store index
			//sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
			//结束时扫描SDcard
			sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file:" + DirectPath ))); 
			//sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,Uri.parse("file://" + Environment.getExternalStorageDirectory())));


			Toast pictureOK = Toast.makeText(instance,
					MessageUtility.MESSAGE_TAKE_PHOTO_SUCCESSFULLY,
					Toast.LENGTH_SHORT);
			 //pictureOK.setGravity(Gravity.CENTER, 0, 0);   
			 pictureOK.show();
		} catch (Exception e) {
			Toast pictureFAIL = Toast.makeText(instance,
					MessageUtility.MESSAGE_TAKE_PHOTO_FAIL, Toast.LENGTH_SHORT);
			//pictureFAIL.setGravity(Gravity.CENTER, 0, 0);   
			pictureFAIL.show();

			e.printStackTrace();
		}
	}

//////////////////////////////////////////////////////////////////////////
public void saveMyBitmap(String bitName ,Bitmap mBitmap){

//	if(No_Sdcard){
//		Toast.makeText(instance,
//				R.string.record_fail_warning,Toast.LENGTH_SHORT).show();
//	}
	//else{
		String photopath =DirectPath  + "/Pictures";
		
		File file = new File(photopath);
		if (!file.exists()) {
		file.mkdirs();  
		}
		
		File f = new File(DirectPath + "/Pictures/"  +  bitName + ".jpg");
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
		mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
		try {
		fOut.flush();
		
		} catch (IOException e) {
		e.printStackTrace();
		}
		try {
		fOut.close();
		String szUrl =DirectPath   + "/Pictures/" +  bitName + ".jpg" ;
		Log.e("zhahg", "szUrl :" + szUrl);
		// Get real path for the inserted image
		try {
			Uri uri = Uri.parse(szUrl);

			String img_path = DirectPath  + "/Pictures/" +  bitName + ".jpg";

			uri = Uri.parse("file://" + img_path);
			Log.e("zhang", "uri :" + uri);
			Log.e("zhang", "img_path :" + img_path);
			// Scan the file to update media store index
			sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
			//if(isplay_pictrue !=1){
				Toast pictureOK = Toast.makeText(instance,MessageUtility.MESSAGE_TAKE_PHOTO_SUCCESSFULLY,
						Toast.LENGTH_SHORT);
				//pictureOK.setGravity(Gravity.CENTER, 0, 0);   
				pictureOK.show();
			//}
			
		} catch (Exception e) {
			Toast pictureFAIL = Toast.makeText(instance,
					MessageUtility.MESSAGE_TAKE_PHOTO_FAIL, Toast.LENGTH_SHORT);
			//pictureFAIL.setGravity(Gravity.CENTER, 0, 0);   
			pictureFAIL.show();

			e.printStackTrace();
		}
		} catch (IOException e) {
		e.printStackTrace();
		}
		
		
	//}
}
//////////////////////
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  //如果用startActivityResult跳转进到另一个Activity,按返回键时调用的地方
		super.onActivityResult(requestCode, resultCode, data);

		/* 20111126: This part is already done in OnCreate() */
		Log.d("wild0", "onActivityResult:controllerType:" + controllerType);
		int o = getResources().getConfiguration().orientation;
		this.changeLandscape();// ッ琌ノlandscape
		/*
		 * if (o == 1) { this.changePortrait(); } else if (o == 2) {
		 * this.changeLandscape(); }
		 */

		final ToggleButton lightTogglebutton = (ToggleButton) findViewById(R.id.light_toggle_button);
		{
			/*
			 * initial
			 */
			/*
			 * int mode = Activity.MODE_PRIVATE; SharedPreferences
			 * carSharedPreferences = getSharedPreferences(
			 * WifiCar.WIFICAR_PREFS, mode); boolean ir = carSharedPreferences
			 * .getBoolean(WifiCar.PREF_IR, false);
			 */

			// Log.d("wild0","ir:"+ir);
			int enableIr = WificarUtility.getIntVariable(instance,
					WificarUtility.WIFICAR_IR, 0);
			if (enableIr == 1) {
				try {
					wifiCar.enableIR();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				lightTogglebutton.setBackgroundResource(R.drawable.ir_pressed);
			} else {
				try {
					wifiCar.disableIR();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				lightTogglebutton.setBackgroundResource(R.drawable.ir);
			}
		}
		final ToggleButton micToggleButton = (ToggleButton) this.findViewById(R.id.mic_toggle_button);
		{
			/*
			 * initial
			 */
			/*
			 * int mode = Activity.MODE_PRIVATE; SharedPreferences
			 * carSharedPreferences = getSharedPreferences(
			 * WifiCar.WIFICAR_PREFS, mode); boolean audio =
			 * carSharedPreferences.getBoolean(WifiCar.PREF_MIC, false);
			 */
			int enableMic = WificarUtility.getIntVariable(instance,
					WificarUtility.WIFICAR_MIC, 0);
			if (enableMic == 1) {
				// boolean result = wifiCar.AudioEnable();
				boolean result = wifiCar.playAudio();
				// if (result) {
				micToggleButton.setBackgroundResource(R.drawable.mic_pressed);
				// }
			} else {
				// boolean result = wifiCar.AudioDisable();
				try {
					boolean result = wifiCar.disableAudio();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// if (result) {
				micToggleButton.setBackgroundResource(R.drawable.mic);
				// }
			}
		}
		isNotExit = false;	
		//bSetting = false;
	}
	
	public class SoundCheck extends TimerTask {
		//取得当前的音量值
		public void run(){
			sendMessage(MESSAGE_SOUND);
		}

	}
	////////////////03.24 SDcard Check///////////////
	public class SDCardSizeTest extends TimerTask {
		//取得SDCard当前的状态   
		public void run(){
			String sDcString = android.os.Environment.getExternalStorageState();   
	
			if (sDcString.equals(android.os.Environment.MEDIA_MOUNTED)) {   
	
				// 取得sdcard文件路径   
				File pathFile = android.os.Environment   
						.getExternalStorageDirectory();   
	
				android.os.StatFs statfs = new android.os.StatFs(pathFile.getPath());   
	
				// 获取SDCard上BLOCK总数   
				long nTotalBlocks = statfs.getBlockCount();   
	
				// 获取SDCard上每个block的SIZE   
				long nBlocSize = statfs.getBlockSize();   
	
				// 获取可供程序使用的Block的数量   
				long nAvailaBlock = statfs.getAvailableBlocks();   
	
				// 获取剩下的所有Block的数量(包括预留的一般程序无法使用的块)   
				long nFreeBlock = statfs.getFreeBlocks();   
	
				// 计算SDCard 总容量大小MB   
				long nSDTotalSize = nTotalBlocks * nBlocSize / 1024 / 1024;   
	
				// 计算 SDCard 剩余大小MB   
				nSDFreeSize = nAvailaBlock * nBlocSize / 1024 / 1024; 
	
				//Log.e("aaaa", "nSDTotalSize = " + nSDTotalSize);
				//Log.e("aaaa", "nSDFreeSize = " + nSDFreeSize);
				//Log.e("aaaa", "sdcheck = " + sdcheck);
	
				if(nSDFreeSize < 100 && sdcheck ==1 ){
	
					sdcheck = 0;
					//Log.e("aaa", "nSDFreeSize111111  = " + nSDFreeSize);
					Message sdcardtest = new Message();
					sdcardtest.what = WificarActivity.MESSAGE_CHECK_TEST;
					WificarActivity.getInstance().getHandler().sendMessage(sdcardtest);
				}
	
			} 
		}
	
	}
////////////////03.24 SDcard Check///////////////
	public void openFile(File f) {
		Intent intent = new Intent();
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		String type = "image/jpeg";

		intent.setDataAndType(Uri.fromFile(f), type);
		startActivityForResult(intent, 5);

	}
///////////////////03.24当按下提示SDcard内存不足的确定按钮后执行下列代码/////////////////
	public void video() {
		//TODO Auto-generated method stub

		ToggleButton videoTogglebutton = (ToggleButton) findViewById(R.id.video_toggle_button);
		//videoTogglebutton.setBackgroundResource(R.drawable.video);
		if(dimension > 5.8){
			videoTogglebutton.setBackgroundResource(R.drawable.video);
		}else if(dimension < 5.8){
			videoTogglebutton.setBackgroundResource(R.drawable.video1);
		}
		videoTogglebutton.setChecked(false);

	}
	public void NoSDcard(){
		ToggleButton videoTogglebutton = (ToggleButton) findViewById(R.id.video_toggle_button);
		//videoTogglebutton.setBackgroundResource(R.drawable.video);
		if(dimension > 5.8){
			videoTogglebutton.setBackgroundResource(R.drawable.video);
		}else if(dimension < 5.8){
			videoTogglebutton.setBackgroundResource(R.drawable.video1);
		}
		videoTogglebutton.setChecked(false);
	}

public void play_audio(){

	ToggleButton micTogglebutton = (ToggleButton) findViewById(R.id.mic_toggle_button);
	
	int enableMic = WificarUtility.getIntVariable(instance,
			WificarUtility.WIFICAR_MIC, 0);
	Log.e("zhang", "enableMic :" + enableMic);
	if (enableMic == 1) {
		// boolean result = wifiCar.AudioEnable();
		
		boolean result = wifiCar.playAudio();
		if (result) {
			audio_play = 1;
			micTogglebutton.setBackgroundResource(R.drawable.mic_pressed);
			micTogglebutton.setChecked(true);
		}
	} else {
		
		boolean result1 = false;
		try {
			result1 = wifiCar.disableAudio();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (result1) {
			audio_play = 0;
			micTogglebutton.setBackgroundResource(R.drawable.mic);
			micTogglebutton.setChecked(false);
		}
	}
		

}

public void setting_play(){

	ToggleButton micTogglebutton = (ToggleButton) findViewById(R.id.mic_toggle_button);
		
		/*int enableMic = WificarUtility.getIntVariable(instance,
				WificarUtility.WIFICAR_MIC, 1);
		Log.e("zhang111", "enableMic :" + enableMic);
		if (enableMic == 1) {*/
			// boolean result = wifiCar.AudioEnable();
			boolean result = wifiCar.playAudio();
			if (result) {
				audio_play = 1;
				micTogglebutton.setBackgroundResource(R.drawable.mic_pressed);
				micTogglebutton.setChecked(true);
				//micTogglebutton.setChecked(false);
			}
		//}
		checkSound = new Timer(true);
		checkSound.schedule(new SoundCheck(), 100, 300);
			
		AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE); 
		int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		Log.e("zhang", "currentVolume :" + currentVolume);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume, 0); //tempVolume:音量绝对值
	
	
}

public void share() {
	// TODO Auto-generated method stub
	Intent intent = new Intent();
	intent.setClass(instance,ShareActivity.class);
	startActivity(intent);
	WificarActivity.this.finish();
}

public boolean note_Intent(Context context) {
	ConnectivityManager con = (ConnectivityManager) context
	.getSystemService(Context.CONNECTIVITY_SERVICE);
	NetworkInfo networkinfo = con.getActiveNetworkInfo();
	if (networkinfo == null || !networkinfo.isAvailable()) {
	// 当前网络不可用
	//Toast.makeText(context.getApplicationContext(), "请先连接Internet！",
	//Toast.LENGTH_SHORT).show();
	return false;
	}
	else{
		return true;
	}
}

//图片加水印
public  Bitmap createBitMap(Bitmap src){
	/**
	 * 水印制作方法
	 */
	Log.e("zhang", "开始了，画图");
	Bitmap wmsrc=BitmapFactory.decodeResource(getResources(), R.drawable.watermark);//水印
	if(src==null){
		return null;
	}
	int w=src.getWidth();
	int h=src.getHeight();
	int wmw=wmsrc.getWidth();
	int wmh=wmsrc.getHeight();
	//create the new bitmap
	Bitmap newb=Bitmap.createBitmap(w,h,Config.ARGB_8888);//创建一个底图
	Canvas cv=new Canvas(newb);
	//将底图画进去
	cv.drawBitmap(src, 0, 0,null);//在0,0坐标开始画入src
	//讲水印画进去
	cv.drawBitmap(wmsrc,w-wmw-5, h-wmh-5, null);  //w-wmw-5, h-wmh-7
	Log.e("zhang", "w , h : " + (w-wmw-5) + "," + (h-wmh-5));
	//保存图片
	cv.save(Canvas.FULL_COLOR_LAYER_SAVE_FLAG);
	cv.restore();
	return newb;

}

	//退出软件
	public void exitProgrames(){
		android.os.Process.killProcess(android.os.Process.myPid());
		
		pause();
		exit();
	 /* if(video_pressed == 1){
			try {
				closeVideoStream();
			} catch (Exception e1) {
			// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			video_pressed = 0 ;
		}*/
	}
	
	public void showDialog(){
			
		ToggleButton gsensor = (ToggleButton) findViewById(R.id.g_sensor_toggle_button);
		// disGsensor = false;
		// Dialog dlg = new DisGsensor(instance,R.style.CustomDialog);
		// dlg = new DisGsensor(instance,R.style.CustomDialog);
		// dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
		WindowManager m = getWindowManager(); 
		Display d = m.getDefaultDisplay(); //为获取屏幕宽、高 
		// dlg.setTitle(" " );
		Window w=dlg.getWindow(); 
		WindowManager.LayoutParams lp =w.getAttributes(); 
				 
		w.setBackgroundDrawableResource(R.color.vifrification); //设置对话框背景为透明
		// w.setGravity(Gravity.CENTER);
		// lp.x=10; 
		// lp.y=25;
		// lp.height = (int) (d.getHeight() * 0.7); //高度设置为屏幕的0.6 ;
		// lp.width = (int) (d.getWidth() * 0.9); // 宽度设置为屏幕的0.95
		//lp.alpha = 0.5f;   //设置透明度
		w.setAttributes(lp);
		dlg.show();
				 
		//disgsensor.setVisibility(View.VISIBLE);
		//ToggleButton gsensor = (ToggleButton) findViewById(R.id.g_sensor_toggle_button);
		gsensor.setBackgroundResource(R.drawable.g);
		gsensor.setChecked(false);
					
		//leftSurface.enableControl();
		//rightSurface.enableControl();
		//show();
					
		try {
			wifiCar.move(WifiCar.LEFT_WHEEL, 0);
			} catch (IOException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
		try {
			wifiCar.move(WifiCar.RIGHT_WHEEL, 0);
			} catch (IOException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/*//分享到Email
				public void sharetogmail(String path ,int i){
					
					File file = new File(path); 
					Log.e("zhang11", "use the email");
				//	if(ShareActivity.getInstance().isSystemEmail){
						ComponentName comp_gmail = new ComponentName("com.android.email", "com.android.email.activity.MessageCompose");
						//ComponentName comp_gmail = new ComponentName("com.htc.android.email", "com.htc.android.email.ComposeActivity");
						Intent  intent_gmail = new Intent();  
						//Intent intent_gmail = new Intent(android.content.Intent.ACTION_SEND);
						intent_gmail.setAction(Intent.ACTION_SEND);
						intent_gmail.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
						if(i == 1){
							intent_gmail.setType("image/jpeg"); 
						}else if(i == 2){
							intent_gmail.setType("video/*"); 
						}
						
						intent_gmail.setComponent(comp_gmail);
						startActivity(intent_gmail); //启动程序
					
					//调用系统的邮件系统
				   //  startActivity(Intent.createChooser(intent_gmail, "请选择邮件发送软件"));
				}
				//分享到facebook
				public void sharetofacebook(String path , int i){
					File file = new File(path); 
					ComponentName comp_f = new ComponentName("com.facebook.katana", "com.facebook.katana.ComposerActivity");
					Intent  intent_f = new Intent(); 
					intent_f.setAction(Intent.ACTION_SEND);
					intent_f.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file)); 
					if(i == 1){
						intent_f.setType("image/jpeg"); 
					}else if(i == 2){
						intent_f.setType("video/*"); 
					}
					intent_f.setComponent(comp_f);
					startActivity(intent_f); //启动程序

				}
				//分享到twitter
				public void sharetotwitter(String path ,int i ){
					File file = new File(path); 
					ComponentName comp = new ComponentName("com.twitter.android", "com.twitter.android.PostActivity");
					Intent  intent = new Intent();
					intent.setAction(Intent.ACTION_SEND);
					intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file)); 
					if(i == 1){
						intent.setType("image/jpeg"); 
					}else if(i == 2){
						intent.setType("video/*"); 
					}
					intent.setComponent(comp);
					startActivity(intent); //启动程序
				}
				//分享到tumblr
				public void sharetotumblr(String path ,int i){
					File file = new File(path); 
					
					Intent  intent = new Intent();
					intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file)); 
					if(i == 1){
						ComponentName comp = new ComponentName("com.tumblr", "com.tumblr.activity.PhotoPostActivity");
						intent.setAction(Intent.ACTION_MAIN);
						intent.setType("image/jpeg"); 
						intent.setComponent(comp);
					}else if(i == 2){
						ComponentName comp = new ComponentName("com.tumblr", "com.tumblr.activity.VideoPostActivity");
						//intent.setAction(Intent.ACTION_SEND_MULTIPLE);
						intent.setAction(Intent.ACTION_SEND);
						intent.setType("video/*"); 
						intent.setComponent(comp);
					}
					
					startActivity(intent); //启动程序
				}
				//分享到youtube
				public void sharetoyoutube(String path){
					File file = new File(path); 
					ComponentName comp = new ComponentName("com.google.android.apps.uploader", "com.google.android.apps.uploader.clients.youtube.YouTubeSettingsActivity");
					Intent  intent_y = new Intent();
					intent_y.setAction(Intent.ACTION_SEND);
					intent_y.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file)); 
					intent_y.setType("video/*"); 
					intent_y.setComponent(comp);
					startActivity(intent_y); //启动程序
					//startActivityForResult(intent_y, 1);
				}*/
				
				public void setDirectionGsensor(int left , int right) {
					
					if(WificarActivity.getInstance().isPlayModeEnable){
						WificarActivity.getInstance().sendMessage(WificarActivity.MESSAGE_STOP_PLAY);
					}
					Log.d("wild0", "direction left:" + left);
					iCarSpeedL = left*10;
					iCarSpeedR = right*10;
				}
				
				private Runnable gMovingTask = new Runnable() {
					public void run() {

						if (gSensorControlEnable & iCarSpeedL == iCarSpeedR) {
							//Log.e("zhang", "一直运行gMovingTask");
							// if (iLastSpeedL != 0 && iCarSpeedL == 0)
							// wifiCar.moveCommand(WifiCar.GO_DIRECTION.Left, iCarSpeedL);
							// iLastSpeedL = iCarSpeedL;
							if ( iCarSpeedL > 0 && iCarSpeedL != 0) {
								// Log.d("wild0","FFFF:"+iCarSpeedL);
								 
								try {
									wifiCar.enableMoveFlag();
									wifiCar.g_move(11, iCarSpeedL);
									//Log.e("zhang", "向前向前向前:" + iCarSpeedL);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								iLastSpeedL = iCarSpeedL;
							}
							if ( iCarSpeedL < 0 && iCarSpeedL != 0) {
								// Log.d("wild0",
								// "Run left("+controlEnable+"):"+iCarSpeedL);
								try {
									wifiCar.enableMoveFlag();
									wifiCar.g_move(12, iCarSpeedL);
									//Log.e("zhang", "向后向后向后");
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								iLastSpeedL = iCarSpeedL;
							}
							if (  iCarSpeedL  ==0 && iLastSpeedL != 0 ) {
								try {
									wifiCar.disableMoveFlag();
									wifiCar.move(WifiCar.LEFT_WHEEL, iCarSpeedL);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								try {
									wifiCar.disableMoveFlag();
									wifiCar.move(WifiCar.RIGHT_WHEEL, iCarSpeedR);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								iLastSpeedL = 0;
							}

						}

						handler.postDelayed(this, 100);
					}
				};
				private Runnable TakePhotoTask = new Runnable() {
					public void run() {

						if (bPhotoThreadRun) {
							if (cameraSurfaceView != null) {
								try {
								//	takePictureBtn.setBackgroundResource(R.drawable.camera_pressed);
									Log.e("take photo", "start take photo");
									wifiCar.takePicture(instance);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							bPhotoThreadRun = false;
						}

						handler.postDelayed(this, 100);
					}
				};
				
				private Runnable StartVideoTask = new Runnable() {
					public void run() {

						if (startVideo) {
							try {
								openVideoStream(FileName);
							} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							}
							startVideo =false;
						}

						handler.postDelayed(this, 100);
					}
				};
				private Runnable StopVideoTask = new Runnable() {
					public void run() {

						if (stopVideo) {
							stopVideo = false;
							startVideo =false;
							try {
								closeVideoStream();
								Toast toast = Toast.makeText(instance,R.string.wificar_activity_toast_stop_recording,Toast.LENGTH_SHORT);
								//toast.setGravity(Gravity.CENTER, 0, 0);   
								toast.show();   
							} catch (Exception e) {
							// TODO Auto-generated catch block
								e.printStackTrace();
							}	
						}

						handler.postDelayed(this, 100);
					}
				};
	private void DeleVideo(){
		if(startVideo){
			startVideo = false;
			String path = ReadSDPath() + "/CAR 2.0";
			File f = new File(path);
			File[] files = f.listFiles();
			/**
			 *  遍历文件，将所有文件存入ArrayList中,这个地方存的还是文件路径
			 */
			for(File file : files){
				if (file.isDirectory()) {
					
				}else {  
					String fileName = file.getName();
					if(fileName.equals(FileName)){
						File dfile = new File(file.getPath());
						dfile.delete();
					}
					}
			}
		}
	}
	private void deleIndexVideo(){
		
		String path = ReadSDPath() + "/CAR 2.0";
		File f = new File(path);
		if(f.exists()){
			f.mkdirs();
			File[] files = f.listFiles();
			/**
			 *  遍历文件，将所有文件存入ArrayList中,这个地方存的还是文件路径
			 */
			for(File file : files){
				if (file.isDirectory()) {
					
				}else {  
					String fileName = file.getName(); 
					
					if (fileName.endsWith(".index")) { 
						
						File dfile = new File(file.getPath());
						dfile.delete();
						
						String filePath = file.getPath().substring(0,file.getPath().length()-6);
						//Log.e("DeleFile", "filePath :" + filePath);
						
						File dfile1 = new File(filePath);
						dfile1.delete();
					}
				}
			}
		}
		
	}	
	//获取SD卡的跟路径
  	public String ReadSDPath(){
  		boolean SDExit=Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
  		if(SDExit){
  			return Environment.getExternalStorageDirectory().toString();
  		}else{
  			return null;
  		}
  	}

}