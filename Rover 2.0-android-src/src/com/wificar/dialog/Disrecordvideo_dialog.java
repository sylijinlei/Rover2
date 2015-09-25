package com.wificar.dialog;

import com.CAR2.R;

import com.wificar.WificarActivity;
import com.wificar.util.VideoUtility;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.ContextThemeWrapper;
import android.widget.EditText;
import android.widget.TextView;

public class Disrecordvideo_dialog {
	public Disrecordvideo_dialog() {
		
	}
	public static AlertDialog.Builder createdisaenablevideoDialog(Context context){
		final AlertDialog.Builder disaenablevideoDialog = new AlertDialog.Builder(new ContextThemeWrapper(context, 
				R.layout.share_dialog));
	
	//	disaenableDialog.setTitle("Connection Error");
		
		disaenablevideoDialog.setMessage("There is not enough power to record a video, please charge device.");
		disaenablevideoDialog .setCancelable(false);

		disaenablevideoDialog .setPositiveButton("OK",new DialogInterface.OnClickListener() {
					//@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						//WificarActivity.getInstance().exit();	
					}
				});
		return disaenablevideoDialog;
	}

}