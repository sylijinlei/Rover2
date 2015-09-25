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

public class wifi_not_connect {
	public wifi_not_connect() {
		
	}
	public static AlertDialog.Builder createwificonnectDialog(Context context){
		final AlertDialog.Builder connectDialogwifi = new AlertDialog.Builder(new ContextThemeWrapper(context, 
				R.layout.share_dialog));
	
		connectDialogwifi.setTitle("Connection Error");
		
		connectDialogwifi.setMessage("Please check your Wi-Fi connection");
		connectDialogwifi .setCancelable(false);

		connectDialogwifi .setPositiveButton("Done",new DialogInterface.OnClickListener() {
					//@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						//WificarActivity.getInstance().exit();	
					}
				});
		return connectDialogwifi;
	}

}