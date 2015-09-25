package com.wificar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import com.CAR2.R;

public class SplashActivity extends Activity {
	protected static final int MESSAGE_MAIN_PROCEDURE = 0;
	private static SplashActivity instance = null;
	private Handler handler = null;
	private String TAG = "SplashActivity";
	public boolean isExit = false;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		finish();
	}
	public static SplashActivity getInstance(){
		return instance;
	}
	public void exit(){
		Log.i(TAG, "SplashActivity is exit!");
		finish();
		System.exit(0);
		//android.os.Process.killProcess(android.os.Process.myPid());
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.exit(0);
	}
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.splash);
		instance = this;
		
		handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case MESSAGE_MAIN_PROCEDURE:
					Intent intent = new Intent(instance, WificarActivity.class);
					instance.startActivityForResult(intent, 1);
					break;
				
				default:
					break;
				}
				super.handleMessage(msg);
			}
		};
		
		
		
	}
	@Override
	protected void onStart() {
		super.onStart();
		
		if(isExit){
			isExit = false;
			finish();
		}
		Runnable init = new Runnable() {

			//@Override
			public void run() {
				
				try {
					Thread.sleep(2300);
					Message messageLoadingSuccess = new Message();
					messageLoadingSuccess.what = MESSAGE_MAIN_PROCEDURE;
					handler.sendMessage(messageLoadingSuccess);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		Thread initThread = new Thread(init);
		initThread.start();
	}
	
}
