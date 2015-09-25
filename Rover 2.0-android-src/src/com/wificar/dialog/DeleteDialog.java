package com.wificar.dialog;

import com.wificar.ImageGalleryActivity;
import com.CAR2.R;
import com.wificar.VideoGalleryActivity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class DeleteDialog extends Dialog{
	private int i;
	public DeleteDialog(Context context) {
		super(context);
		
		/* Window w=getWindow(); 
		 WindowManager.LayoutParams lp =w.getAttributes(); 
		 
		 w.setLayout(120, 100);
		 w.setGravity(Gravity.RIGHT | Gravity.TOP);
		 lp.x=10; 
		 lp.y=70;
		// lp.height = (int) (d.getHeight() * 0.1); //高度设置为屏幕的0.6 ;
		 //lp.width = (int) (d.getWidth() * 0.65); // 宽度设置为屏幕的0.95
		 lp.height = 90;
		 getWindow().setAttributes(lp);*/
		
	}
	public DeleteDialog(Context context, int theme ,int inter){
        super(context, theme);
       // this.context = context;
        this.i = inter;
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_dialog);
		
		Button positiveBtn = (Button) findViewById(R.id.positiveButton);
		Button cancelBtn = (Button) findViewById(R.id.cancelButton);
		TextView text = (TextView) findViewById(R.id.message);
		if(i == 1){
			text.setText("DELETE PHOTOS?");
		}else if(i == 2){
			text.setText("DELETE VIDEOS?");
		}
		
		positiveBtn.setOnClickListener(new View.OnClickListener() {
			
			//@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//dismiss();
				cancel();
				if(i == 1){
					ImageGalleryActivity.getInstance().Delete_photo();
				}else if(i == 2){
					VideoGalleryActivity.getInstance().Delete_video();
				}
				
			}
		});
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			
			//@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss(); 
				
			}
		});
	}
	
}