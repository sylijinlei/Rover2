package com.wificar.dialog;

import com.CAR2.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class DisGsensor extends Dialog {

	private static DisGsensor dialog = null;
	public DisGsensor(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public DisGsensor(Context context, int theme ){
        super(context, theme);
       // this.context = context;
       
    }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.disagsensor); 
		//setCanceledOnTouchOutside(true);  //设置触摸对话框意外的地方取消对话框
		// setCancelable(true);
		Button button = (Button) findViewById(R.id.okbutton);
		//button.setBackgroundColor(R.color.vifrification);
	/*	TextView messageText = (TextView) findViewById(R.id.massge_dialog);
		
		messageText.setText("To share CAR 2.0 photos and videos, exit the app, " +
				"go to Settings and access a Wi-Fi network other than CAR 2.0. " +
				"Open the CAR 2.0 app and select Share.");*/
		button.setOnClickListener(new View.OnClickListener() {
			
			//@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cancel();
			}
		});
	}
	/*@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			Log.i("zhang", "11111111111111111111");
			//((DialogInterface) mdialog).dismiss();
			//cancel();
			
		}else if(event.getAction() == MotionEvent.ACTION_UP){
			//dismiss();
		}
		return super.onTouchEvent(event);
	}*/
	
	
}
