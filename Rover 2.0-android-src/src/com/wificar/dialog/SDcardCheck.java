package com.wificar.dialog;

import com.CAR2.R;
import com.wificar.WificarActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.ContextThemeWrapper;


public class SDcardCheck {
	//public SDcardCheck(){}
	
	public static AlertDialog.Builder creatSDcardCheckDialog(Context context){
		final AlertDialog.Builder sdcardcheckDialog = new AlertDialog.Builder(new ContextThemeWrapper(context,
				R.layout.share_dialog));
		sdcardcheckDialog.setTitle(R.string.sdcard_tilt);
		sdcardcheckDialog.setPositiveButton(R.string.done_button, new OnClickListener() {
			
			//@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				WificarActivity.getInstance().video();
			}
		});
		return sdcardcheckDialog;
	}
}