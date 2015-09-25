package com.wificar;

import com.CAR2.R;
import com.wificar.component.WifiCar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class SettingActivity extends Activity{
	private static SettingActivity instance = null;
	
	public EditText IP ;
	public EditText Port;
	public TextView device;
	public TextView firmware;
	public TextView software;
	private Button Okbutton;
	
	public int audio_play = WificarActivity.getInstance().audio_play;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		instance = this;	
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);	
		setContentView(R.layout.setting_info);
		
		/*if(WificarActivity.getInstance().with >=1200){
			setContentView(R.layout.setting_info);
		}else if(WificarActivity.getInstance().with <= 800){
			setContentView(R.layout.setting_info_normal);
		}*/
		//setContentView(R.layout.setting_info);


		if(audio_play == 1){
			WificarActivity.getInstance().setting_play();
			
		}
		IP = (EditText) findViewById(R.id.EditText_IP);
		Port = (EditText)findViewById(R.id.EditText_PORT);
		device = (TextView)findViewById(R.id.TextView_D);
		firmware = (TextView)findViewById(R.id.TextView_F);
		software = (TextView)findViewById(R.id.TextView_S);
		Okbutton = (Button) findViewById(R.id.OkButton);
		
		Okbutton.setOnClickListener(new View.OnClickListener() {
			
			//@Override
			public void onClick(View v) {
				//Okbutton.setBackgroundColor(0xA254D1);
				Okbutton.setBackgroundResource(R.drawable.ok_off);
				WificarActivity.getInstance().onResume();
				instance.finish();
			}
		});
		
		String host = WificarActivity.getInstance().getWifiCar().getHost();
		IP.setText(host);
		IP.setClickable(false);

		//Car Port
		String port = String.valueOf(WificarActivity.getInstance().getWifiCar().getPort());
		Port.setText(port);
		IP.setClickable(false);
		
		//Device Connected 
		String ssid = "";
		try{
				ssid = WificarActivity.getInstance().getWifiCar().getSSID();
				
		}
		catch(Exception e){
			e.printStackTrace();
		}
		Log.i("zhang", "ssid :" + ssid);
		Log.i("zhang", "deivce :" + device);
		
		device.setText(ssid);

		//Firmware Version
		String firmwareVersion = WificarActivity.getInstance().getWifiCar().getFilewareVersion();
		
		if(!firmwareVersion.equals("")){
			firmwareVersion = "1.0";
		}else if(firmwareVersion.equals("")){
			firmwareVersion = " ";
		}
		Log.i("zhang", "firmwareVersion11 :" +firmwareVersion);
		firmware.setText(firmwareVersion);
		
		
		//Software Version
		String version = WifiCar.getVersion(instance);
		software.setText(version);

	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		this.onBackPressed();
		if (this.isFinishing()){
	        //Insert your finishing code here
			Log.d("activity","setting on Pause:finish");
			//·µ»ØµÄ¶¯»­
			//overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right); 
	    }
	}

}
