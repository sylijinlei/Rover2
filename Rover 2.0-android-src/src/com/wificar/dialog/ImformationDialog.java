package com.wificar.dialog;

import com.CAR2.R;
import com.wificar.WificarActivity;
import com.wificar.component.WifiCar;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ImformationDialog extends Dialog {
	public EditText IP ;
	public EditText Port;
	public TextView device;
	public TextView firmware;
	public TextView software;
	private Button Okbutton;
	public ImformationDialog(Context context, int deletedialog) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,   
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); 
		setContentView(R.layout.setting_info);
		
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
				cancel();
			}
		});
		String host = WificarActivity.getInstance().getWifiCar().getHost();
		IP.setText(host);
		IP.setClickable(false);

		//Car Port
		String port = String.valueOf(WificarActivity.getInstance().getWifiCar().getPort());
		Port.setText(port);
		Port.setClickable(false);
		
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
				String version = WifiCar.getVersion(getContext());
				software.setText(version);
	}
	
}
