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

public class Disrecord_play_dialog {
	public Disrecord_play_dialog() {
		
	}
	public static AlertDialog.Builder createdisaenableDialog(Context context){
		final AlertDialog.Builder disaenableDialog = new AlertDialog.Builder(new ContextThemeWrapper(context, 
				R.layout.share_dialog));
	
	//	disaenableDialog.setTitle("Connection Error");
		
		disaenableDialog.setMessage("There is not enough power to play or record a path, please charge device.");
		disaenableDialog .setCancelable(false);

		disaenableDialog .setPositiveButton("OK",new DialogInterface.OnClickListener() {
					//@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						//WificarActivity.getInstance().exit();	
					}
				});
		return disaenableDialog;
	}

}